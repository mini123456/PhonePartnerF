package com.phonepartner.frgment;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.phonepartner.BaseFragment;
import com.phonepartner.R;
import com.phonepartner.View.InterceptLinearLayout;
import com.phonepartner.adapter.DeviceListAdapter;
import com.phonepartner.application.BaseApplication;
import com.phonepartner.entity.UserInfo;
import com.phonepartner.util.SessionUtils;

import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceScanConFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceScanConFragment extends BaseFragment implements View.OnClickListener {

    public static final int REQUEST_PERMISSION_BT = 9;
   // public static final int REQUEST_LOCATION_PERMISSION = 8 ;
    private static final String TAG = "DeviceScanConFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final long SCAN_PERIOD = 30000;//扫描时间
    private String mParam1;
    private String mParam2;
    private TextView headtitle;
    private TextView tv_fun;
    private InterceptLinearLayout line_add_action;
    private ImageButton backImage;
    private InterceptLinearLayout line_backe_image;
    private TextView tvCnDvName;
    private RecyclerView rvDeviceList;
    //  private ImageView ivConnect;
    private ImageView ivStartConnect;
    private Animation operatingAnim;//动画
    private ImageView ivIsConnect;

    private boolean mScanning = false;//是否正在搜索
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private DeviceListAdapter mDevListAdapter;


    public BluetoothLeService mBluetoothLeService;//初始化一个BluetoothLeService类
    private boolean mConnected = false;
//    boolean connect_status_bit = false;//初始化蓝牙为未连接状态
    private String mDeviceAddress = "";//连接地址
    private int count = 0;
    private BluetoothDevice mDevice;

    public DeviceScanConFragment() {
        // Required empty public constructor
    }

    public static DeviceScanConFragment newInstance(String param1, String param2) {
        DeviceScanConFragment fragment = new DeviceScanConFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        bleEnable();//询问用户是否开启蓝牙
        mHandler = new Handler();
        //绑定Service
        Intent gattServiceIntent = new Intent(getActivity(), BluetoothLeService.class);
        getActivity().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_BT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO 请求权限成功
                    showCustomToast("请求权限成功", getActivity().getApplicationContext());
                } else {
                    //TODO 提示权限已经被禁用
                    showCustomToast("请求权限成功", getActivity().getApplicationContext());
                }
                break;
//            case REQUEST_LOCATION_PERMISSION:
//                if (!isLocationOpen(getActivity().getApplicationContext())) {
//                    Toast.makeText(getActivity(), "安卓6.0系统要求：如果要使用蓝牙设备，必须打开位置！", Toast.LENGTH_SHORT).show();
//                }
//                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device_connect, container, false);
        initView(view);
        setData();
        initEvent();
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(mServiceConnection);//取消绑定 Serivice
        // getActivity().unregisterReceiver(mGattUpdateReceiver);
        mBluetoothLeService = null;
        getActivity().unregisterReceiver(mGattUpdateReceiver);

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: " + count++);
        scanLeDevice(true);
        //  getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        //checkPermission();

    }

    @Override
    public void onPause() {//停止扫描
        super.onPause();
        Log.d(TAG, "onPause: ");
        //scanLeDevice(false);
    }


    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                showCustomToast("Unable to initialize Bluetooth", getActivity().getApplicationContext());
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    private void initView(View view) {
        headtitle = (TextView) view.findViewById(R.id.headtitle);
        headtitle.setText("设备");
        tv_fun = (TextView) view.findViewById(R.id.tv_fun);
        tv_fun.setText("刷新");
        line_add_action = (InterceptLinearLayout) view.findViewById(R.id.line_add_action);
        backImage = (ImageButton) view.findViewById(R.id.backImage);
        line_backe_image = (InterceptLinearLayout) view.findViewById(R.id.line_backe_image);
        line_backe_image.setVisibility(View.GONE);
        tvCnDvName = (TextView) view.findViewById(R.id.tvCnDvName);
        rvDeviceList = (RecyclerView) view.findViewById(R.id.rvDeviceList);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        rvDeviceList.setLayoutManager(manager);
        ivIsConnect = (ImageView) view.findViewById(R.id.ivIsConnect);
        ivStartConnect = (ImageView) view.findViewById(R.id.ivStartConnect);
        //初始化动画
        operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.tip);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

    }

    private void initEvent() {
        line_add_action.setOnClickListener(this);
        line_add_action.setVisibility(View.VISIBLE);
        //   ivStartConnect.setOnClickListener(this);
    }

    private void setData() {
        mDevListAdapter = new DeviceListAdapter(getActivity().getApplicationContext());
        //mAdapter = new DdeviceLvAdapter(getActivity().getApplicationContext());
        mDevListAdapter.setOnItemClickLitener(new DeviceListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(final BluetoothDevice device1) {
                if (mDevListAdapter.getCount() > 0) {

                    if (device1 == null) {
                        return;
                    }
                    String name = "";
                    if (device1.getName() != null) {
                        name = device1.getName();
                    } else {
                        name = "unknow-device";
                    }
                    showAlertDialog("连接设备", "确定连接设备" + name + "?", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("弹框","0");
                            if (mDevice != null){   //切换设备连接时，先断开上一个连接
                                Log.e(TAG, "===============having BluetoothDevice connected " + mDevice.getName());
                                mBluetoothLeService.disconnect();
                                mBluetoothLeService.close();
                            }
                            mDevice = device1;
                            mDeviceAddress = device1.getAddress();
                            UserInfo userInfo = SessionUtils.getInstance(getActivity().getApplicationContext()).getLoginUserInfo();
                                    userInfo.setDevice(device1);
                                    SessionUtils.getInstance(getActivity().getApplicationContext()).saveLoginUserInfo(userInfo);
                            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                            ivStartConnect.startAnimation(operatingAnim);//开始连接设备
                            Log.e(TAG, "Connect request result=" + result + "设备名" + device1.getName() +
                                    "设备地址" + device1.getAddress());

                            if (result && BaseApplication.getInstance().isConnect_status_bit()) {//如果连接上，则改变连接名
//                                mDevListAdapter.setIsselect(true);
//                                mDevListAdapter.notifyDataSetChanged();
//                                ivStartConnect.clearAnimation();
//                                if (device1.getName() != null) {
//                                    tvCnDvName.setText(device1.getName());
//                                    ivIsConnect.setImageResource(R.mipmap.sport);
//                                    //SessionUtils.getInstance(getActivity().getApplicationContext()).saveDeviceName(device1.getName());
//                                     userInfo = SessionUtils.getInstance(getActivity().getApplicationContext()).getLoginUserInfo();
//                                    userInfo.setDevice(device1);
//                                    SessionUtils.getInstance(getActivity().getApplicationContext()).saveLoginUserInfo(userInfo);
//                                } else {
//                                    tvCnDvName.setText("unknow-device");
//                                    ivIsConnect.setImageResource(R.mipmap.sport);
//                                    //SessionUtils.getInstance(getActivity().getApplicationContext()).saveDeviceName("unknow-device");
//                                }
//                                SessionUtils.getInstance(getActivity().getApplicationContext()).saveDeviceAddress(device1.getAddress());

                            }
                            if (mScanning) {
                                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                mScanning = false;
                            }
                            dialog.dismiss();
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("弹框","1");
                            dialog.dismiss();

                        }
                    });

                }
            }

        });
        rvDeviceList.setAdapter(mDevListAdapter); //将蓝牙配器与ListView关联
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.line_add_action://注册
                tvCnDvName.setText("暂无数据连接");
                ivIsConnect.setImageResource(R.mipmap.connotice);
                mDevListAdapter.setIsselect(false);

                if (mScanning) {
                    tv_fun.setText("停止");
                    //ivStartConnect.startAnimation(operatingAnim);
                    scanLeDevice(false);
//                    mDevListAdapter.clear();
//                    mDevListAdapter.notifyDataSetChanged();
                    //mScanning = true;
                } else {
                    tv_fun.setText("刷新");
                   // ivStartConnect.clearAnimation();
                    scanLeDevice(true);


                    // mScanning =false ;
                }

                mDevListAdapter.notifyDataSetChanged();
                break;
            default:
                break;
//            case R.id.ivStartConnect:
//                if(!mScanning){
//                 ivStartConnect.startAnimation(operatingAnim);
//                    scanLeDevice(true);
//                    mDevListAdapter.clear();
//                    mDevListAdapter.notifyDataSetChanged();
//                    //mScanning = true;
//                }else {
//                    ivStartConnect.clearAnimation();
//                    scanLeDevice(false);
//                   // mScanning =false ;
//                }
//                break;
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: " + "scanLeDevice" + enable);
                    mScanning = false;
                    if (mBluetoothAdapter != null) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);

                    }

                }
            }, SCAN_PERIOD);//after 10sec stop

            mScanning = true;
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);//开始搜索
            }

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d(TAG, "onLeScan: " + device.getAddress());
            if (device == null) {
                showCustomToast("当前没有设备", getActivity().getApplicationContext());
                return;
            }
            if (Looper.myLooper() == Looper.getMainLooper()) {
                // Android 5.0 及以上

                mDevListAdapter.addDevice(device);
                mDevListAdapter.notifyDataSetChanged();
            } else {
                // Android 5.0 以下
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDevListAdapter.addDevice(device);
                        mDevListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
//           getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mAdapter.addDevice(device);
//                    mAdapter.notifyDataSetChanged();
//                   // mDevListAdapter.notifyDataSetChanged();
//                  //  mDevListAdapter.addDevice(device);//增加设备
//
//                }
//            });
//        }
    };


    private void bleEnable() {
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            showCustomToast("您的设备不支持蓝牙4.0", getActivity().getApplicationContext());
            // finish();
        }//判断设备是否支持BLE
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE); //首先获取BluetoothManager
        mBluetoothAdapter = bluetoothManager.getAdapter();//获取BluetoothAdapter蓝牙适配器
        // Checks if Bluetooth is supported on the device.判断该设备是否支持蓝牙
        if (mBluetoothAdapter == null) {
            // Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            showCustomToast("Bluetooth not supported.", getActivity().getApplicationContext());
            //finish();
            return;
        }
        // 如果本地蓝牙没有开启，则开启
        if (!mBluetoothAdapter.isEnabled()) {
            // 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
            // 那么将会收到RESULT_OK的结果，
            // 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(mIntent, 1);
            // 用enable()方法来开启，无需询问用户(实惠无声息的开启蓝牙设备),这时就需要用到android.permission.BLUETOOTH_ADMIN权限
            // mBluetoothAdapter.enable();
            // mBluetoothAdapter.disable();//关闭蓝牙
        }
        //启动权限检测



    }



        /** *判断位置信息是否开启 */
    public static boolean isLocationOpen(final Context context){
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //gps定位
        boolean isGpsProvider = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //网络定位
        boolean isNetWorkProvider = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGpsProvider|| isNetWorkProvider;
    }

    /******************
     * 连接状态
     ***********************************************/
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("","intent.getAction()=="+intent.getAction());
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {//连接成功
                Log.i("连接结果","成功");

                //mConnected = true;
//                connect_status_bit = true;
                BaseApplication.getInstance().setConnect_status_bit(true);
                mDevListAdapter.setIsselect(true);
                mDevListAdapter.notifyDataSetChanged();
                ivIsConnect.setImageResource(R.mipmap.sport);
                ivStartConnect.clearAnimation();
                Log.e("","intent.getAction()=1111111111111111111="+intent.getAction());
                //invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                //updateConnectionState(R.string.disconnected);
                mDevListAdapter.setIsselect(false);
                mDevListAdapter.notifyDataSetChanged();
                tvCnDvName.setText("暂无设备连接");
                ivStartConnect.clearAnimation();//设备未连接
//                connect_status_bit = false;
                BaseApplication.getInstance().setConnect_status_bit(false);
                // show_view(false);
                Log.e("","intent.getActi2222222222222222222on()=="+intent.getAction());
                //invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //displayData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        if (gattServices.size() > 0 && mBluetoothLeService.get_connected_status(gattServices) >= 4) {
            if ( BaseApplication.getInstance().isConnect_status_bit()) {
                mConnected = true;
                //show_view(true);

                try {
                    Thread.currentThread();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.enable_JDY_ble(true);

                if (mDevice.getName() != null) {
                    tvCnDvName.setText(mDevice.getName());
                    ivIsConnect.setImageResource(R.mipmap.sport);
                    SessionUtils.getInstance(getActivity().getApplicationContext()).saveDeviceName(mDevice.getName());

                } else {
                    tvCnDvName.setText("unknow-device");
                    SessionUtils.getInstance(getActivity().getApplicationContext()).saveDeviceName("unknow-device");
                }
                SessionUtils.getInstance(getActivity().getApplicationContext()).saveDeviceAddress(mDevice.getAddress());

                ivStartConnect.clearAnimation();
                // updateConnectionState(R.string.connected);
            } else {
                //Toast.makeText(this, "Deleted Successfully!", Toast.LENGTH_LONG).show();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "设备没有连接！", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }



}
