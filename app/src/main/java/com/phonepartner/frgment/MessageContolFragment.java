package com.phonepartner.frgment;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.phonepartner.BaseFragment;
import com.phonepartner.R;
import com.phonepartner.View.AudioRecorderButton;
import com.phonepartner.View.InterceptLinearLayout;
import com.phonepartner.activity.MainActivity;
import com.phonepartner.activity.TeamActivity;
import com.phonepartner.adapter.MsgAdapter;
import com.phonepartner.application.BaseApplication;
import com.phonepartner.entity.Msg;
import com.phonepartner.entity.UserInfo;
import com.phonepartner.manager.MediaPlayerManager;
import com.phonepartner.util.SessionUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.phonepartner.activity.MainActivity.context;
import static com.phonepartner.util.CharactorUtils.byte2float;
import static com.phonepartner.util.CharactorUtils.byteMerger;
import static com.phonepartner.util.CharactorUtils.float2byte;

//import com.google.gson.Gson;

//import com.google.gson.Gson;

public class MessageContolFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = "MessageContolFragment";
    private static final String TAG_GPS = "Gps";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";//设备名称
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";//设备MAC地址
    public static final int USER = 1;//自己的位置标志
    public static final int FRIEND = 2 ;//朋友的位置标志

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mDeviceName = "";
    private String mDeviceAddress = "";
    public static String friend_name = "";

    private String mParam1;
    private String mParam2;

    private InterceptLinearLayout line_add_action ;
    private InterceptLinearLayout line_backe_image;
    private ImageView add_action ;
    private ImageButton backImage;
    private TextView headtitle ;
    private BluetoothLeService mBluetoothLeService;//初始化一个BluetoothLeService类
    private LocationManager locationManager;//新定义一个LocationManager类，用于定位位置信息
    public static ListView mListView;//定义一个信息列表显示控件
    public static ArrayAdapter<Msg> mAdapter;//信息适配器
    public static List<Msg> mDatas = new ArrayList<Msg>();//初始化信息适配器
    private AudioRecorderButton mAudioRecorderButton;//定义一个新的语音按钮AudioRecorderButton
    private TextView mConnectionState;//设备连接状态
    private TextView mDataField;
    private ImageButton mBtnLocation;//定位信息发送按钮
    private String provider;

    private Button mBtnMsg;//文本信息发送按钮
    private EditText etMsg;//文本信息编辑器

    private Timer timer;
    private View L_animView, R_animView;//语音信息动画显示
    private TextView left_msg,right_msg;//文本信息（定位信息）
    public static String fileName_ReceivedRecorder;//收到的语音信息写入的文件的文件名称
    public final static byte[] s1 = {0x30};//标志文本信息
    public final static byte[] s2 = {0x31};//标志语音消息
    public final static byte[] s3 = {0x32};//标志位置信息

    // 消息通知
    public  static int notifyId = 100;
    public static NotificationManager mNotificationManager;//定义一个新的通知对象
    public static NotificationCompat.Builder mBuilder;

    private boolean mConnected = false;
    boolean connect_status_bit = false;//初始化蓝牙为未连接状态
    private Location location;
    private int latitude = 0;
    private int longitude = 0;

    //用于给录音文件命名
    public static int i=0;



    OnLocationClickListener mCallBack;//地址回调接口
    //此处定义接口
    public interface OnLocationClickListener {//点击地址跳转回调接口
        //在接口中定义函数
        void onLocationClick(int person,UserInfo user,String text);
    }


    public MessageContolFragment() {
        Log.i("信息界面","我被创建了");
    }


    @Override
    public void onAttach(Activity activity) { //与activity 共享地址信息
        super.onAttach(activity);
        try {
            mCallBack = (OnLocationClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public static MessageContolFragment newInstance(String param1, String param2) {
        MessageContolFragment fragment = new MessageContolFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }

            //mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("此处为收到信息并且执行","你好，有条信息需要处理-------------------------------------");
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //mConnected = true;
                connect_status_bit = true;
                //invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                connect_status_bit = false;
                show_view(false);
                //invalidateOptionsMenu();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.i("演示数据","你好，有条信息需要处理-------------------------------------");
                displayData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        UserInfo friend = SessionUtils.getInstance(getActivity().getApplicationContext()).getMemberUserInfo();
        UserInfo friends = SessionUtils.getInstance(getActivity()).getMemberUserInfo();

        if(friends!=null){
            friend_name = friends.getMemoName();
            if(friend_name == null){
                friend_name ="吴彦祖";
            }
        }else {
            friend_name ="张学友";
        }




    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            //final boolean result = mBluetoothLeService.connect(mDeviceAddress);
           // Log.d(TAG, "Connect request result=" + result);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        getActivity().unbindService(mServiceConnection);
        getActivity().unregisterReceiver(mGattUpdateReceiver);
        mBluetoothLeService = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_contol, container, false);
        initData();
        initView(view);
        initEvent();

        return view;
    }


    private void initData() {
        mDeviceName = SessionUtils.getInstance(getActivity().getApplicationContext()).getDeviceName();
        mDeviceAddress = SessionUtils.getInstance(getActivity().getApplicationContext()).getDeviceAddress();//获取intent传递过来的设备MAC地址数据

    }

    private void initView(View view) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//软件进入聊天室时默认不弹出键盘
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);//获取LocationManager实例
        headtitle = (TextView) view.findViewById(R.id.headtitle);
        headtitle.setText("队伍消息");
        backImage = (ImageButton) view.findViewById(R.id.backImage);
        backImage.setBackgroundResource(R.mipmap.team);
        line_backe_image = (InterceptLinearLayout) view.findViewById(R.id.line_backe_image);
        line_add_action = (InterceptLinearLayout) view.findViewById(R.id.line_add_action);
        add_action = (ImageView) view.findViewById(R.id.add_action);
        line_add_action.setVisibility(View.GONE);
        mListView = (ListView) view.findViewById(R.id.listView);
        mAdapter = new MsgAdapter(getActivity(), mDatas);
        mListView.setAdapter(mAdapter);
        mAudioRecorderButton = (AudioRecorderButton) view.findViewById(R.id.id_recorder_button);
        //mAudioRecorderButton.initAudioRecorderButton(getActivity());
       // mAudioRecorderButton = new AudioRecorderButton(getActivity());
        mConnectionState = (TextView) view.findViewById(R.id.connection_state);
        mBtnMsg = (Button) view.findViewById(R.id.btn_send);//send data 1002//文本信息发送按钮
        mBtnLocation = (ImageButton) view.findViewById(R.id.button_location);//定位信息发送按钮
        etMsg = (EditText) view.findViewById(R.id.et_sendmessage);
        etMsg.setText("");//清空编辑框

        show_view(false);//按钮不可点击

        timer = new Timer();
        timer.schedule(task, 1000, 1000);

        Intent gattServiceIntent = new Intent(getActivity(), BluetoothLeService.class);
        getActivity().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
       // updateConnectionState(R.string.connecting);

        if (BaseApplication.getInstance().isConnect_status_bit()) {//如果蓝牙连接顺畅
            show_view(true);
            updateConnectionState(R.string.connected);
            //  mBluetoothLeService.enable_JDY_ble(true);
        }
        initNotify();//初始化消息通知
    }

    private void initEvent() {
        line_backe_image.setOnClickListener(this);
        mAudioRecorderButton.setFinishRecorderCallBack(audioCallBack);
        mBtnMsg.setOnClickListener(this);//文本信息发送按钮
        mBtnLocation.setOnClickListener(this);//定位信息发送按钮
        //信息点击事件，如果是语音则有播放效果
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                // 声音播放动画
                if (R_animView != null) {
                    R_animView.setBackgroundResource(R.mipmap.adj_right);
                    R_animView = null;
                }
                if (L_animView != null) {
                    L_animView.setBackgroundResource(R.mipmap.adj_left);
                    L_animView = null;
                }
                R_animView = view.findViewById(R.id.right_recoder_anim);
                R_animView.setBackgroundResource(R.drawable.r_play_anim);
                AnimationDrawable R_animation = (AnimationDrawable) R_animView.getBackground();
                R_animation.start();
                L_animView = view.findViewById(R.id.left_recoder_anim);
                L_animView.setBackgroundResource(R.drawable.l_play_anim);
                AnimationDrawable L_animation = (AnimationDrawable) L_animView.getBackground();
                L_animation.start();
                // 播放录音
                MediaPlayerManager.playSound(mDatas.get(position).filePath, new MediaPlayer.OnCompletionListener() {

                    public void onCompletion(MediaPlayer mp) {
                        //播放完成后修改图片
                        R_animView.setBackgroundResource(R.mipmap.adj_right);
                        L_animView.setBackgroundResource(R.mipmap.adj_left);
                    }
                });

            //点击定位信息
                right_msg = (TextView) view.findViewById(R.id.right_msg);//自己发送的文本信息
                left_msg = (TextView) view.findViewById(R.id.left_msg);//接收到的文本信息
                if(right_msg != null){
                    String rightContent = right_msg.getText().toString();

                    if(rightContent.equals(mDatas.get(position).getContent())&&rightContent.contains("经度")){//自己发送的定位信息
                       // Toast.makeText(mBluetoothLeService, "右边："+right_msg.getText().toString(), Toast.LENGTH_SHORT).show();
                        UserInfo user = SessionUtils.getInstance(getActivity()).getLoginUserInfo();
                        mCallBack.onLocationClick(USER,user,right_msg.getText().toString());
                        return;
                    }
                }
                if(left_msg != null){
                    String leftContent = left_msg.getText().toString();
                    if(leftContent.equals(mDatas.get(position).getContent())&&leftContent.contains("经度")){//接收到的对方的定位信息
                       // Toast.makeText(mBluetoothLeService, "左边："+left_msg.getText().toString(), Toast.LENGTH_SHORT).show();
                       UserInfo friend = SessionUtils.getInstance(context.getApplicationContext()).getMemberUserInfo();
                        Log.i("队友的信息",friend.getName());
//                        UserInfo userInfo = new UserInfo("13860473129","123456","201702293112156778","hjf","cwj","13599510549");
//                        userInfo.setAddress("厦门");
//                        userInfo.setMail("hjf@123.com");
                        String s,s1;
                        try {
                            s1=new String(MergerMessagePacket.II_OriginatingAddress, "GB2312");
                            s = new String(MergerMessagePacket.II_OriginatingAddress, "GBK");
                        }catch(Exception e){
                            s="转换失败";
                            s1="装换失败";
                        }
                        Log.i("物理地址",""+s + "\n" + s1);

                        //尝试string转byte
                        String f="E4:65:0F:23:92:C1";
                        byte[] t=f.getBytes();
                        if(t.equals(MergerMessagePacket.II_OriginatingAddress)){
                            Log.d("转换结果","成功");
                        }else{
                            Log.d("转换结果","失败");
                        }
                        mCallBack.onLocationClick(FRIEND,friend,left_msg.getText().toString());

                    return;
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.line_backe_image:
                startActivity(TeamActivity.class);
                getActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);//淡入浅出动画
                break;
            case R.id.btn_send://uuid1002 数传通道发送数据，点击文本信息
                if(TextUtils.isEmpty(etMsg.getText().toString().trim())){
                    Toast.makeText(mBluetoothLeService, "发送内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (BaseApplication.getInstance().isConnect_status_bit()) {//如果蓝牙连接顺畅
                    String tx_string = etMsg.getText().toString().trim();//获取文本编辑信息

                        Msg msg = new Msg(0, tx_string, null, Msg.MSG_SENT);//显示发送的文本信息
                        mDatas.add(msg);//将文本信息写入适配器中
                        //更新数据
                        mAdapter.notifyDataSetChanged();
                        //设置位置
                        mListView.setSelection(mDatas.size() - 1);
                        etMsg.setText("");//清空输入框中的内容

                        bleSendData(tx_string);//文本信息发送


//                    byte[] WriteBytes = new byte[1024];
//                    try {
//                        WriteBytes = tx_string.toString().getBytes("GBK");//把输入的字符串转化为字节组
//                    } catch (UnsupportedEncodingException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    byte[] data = byteMerger(s1, WriteBytes);//组合字节组s1，WriteBytes
//                    mBluetoothLeService.senddata(data);//发送文本信息
                } else {//如果蓝牙连接不顺畅，则予以提醒
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "设备没有连接！", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.button_location://点击定位信息
                Toast.makeText(getActivity().getApplicationContext(), "正在定位中...", Toast.LENGTH_SHORT).show();
                if (BaseApplication.getInstance().isConnect_status_bit()) {//如果蓝牙连接顺畅
                    //为获取地理位置信息时设置查询条件
                    // 获取 Location Provider
                   getProvider();
                    // 如果未设置位置源，打开 GPS 设置界面
                   openGPS();
                    // 获取位置
                   // String bestProvider = locationManager.getBestProvider(getCriteria(), true);
                    //获取位置信息
                    //如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER

                   Location location = locationManager.getLastKnownLocation(provider);
                 
                 // String msg_location = updateView(location);//获取定位信息
                   String msg_location  = SessionUtils.getInstance(getActivity().getApplicationContext()).getAddress();
                    if(msg_location != null){
                        Msg msg = new Msg(0, msg_location, null, Msg.MSG_SENT);//显示定位信息
                        mDatas.add(msg);//将定位信息写入适配器中
                        //更新数据
                        mAdapter.notifyDataSetChanged();
                        //设置位置
                        mListView.setSelection(mDatas.size() - 1);
                        bleSendData(msg_location.toString());//将位置信息发送
//                        byte[] WriteBytes = new byte[1024];
//                        try {
//                            WriteBytes = msg_location.toString().getBytes("GBK");//把输入的字符串转化为字节组
//                        } catch (UnsupportedEncodingException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                        byte[] data = byteMerger(s3, WriteBytes);//组合字节组s3，WriteBytes
//                        mBluetoothLeService.senddata(data);//发送定位信息
                    }else {
                        msg_location =  "经度"+24.549031+"纬度:"+ 118.125610	;
                        Msg msg = new Msg(0, msg_location, null, Msg.MSG_SENT);//显示定位信息
                        mDatas.add(msg);//将定位信息写入适配器中
                        //更新数据
                        mAdapter.notifyDataSetChanged();
                        //设置位置
                        mListView.setSelection(mDatas.size() - 1);
                        bleSendData(msg_location.toString());//将位置信息发送
                    }

                } else {//如果蓝牙连接不顺畅，则予以提醒
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "设备没有连接！", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            default:
                break;
        }
    }


    private void updateWithNewLocation(Location location2) {
        // TODO Auto-generated method stub
        while(location2 == null){
            locationManager.requestLocationUpdates(provider, 2000, (float) 0.1, locationListener);
        }
        if (location2 != null) {
            latitude = ((int)(location2.getLatitude()*100000));
            longitude = (int)(location2.getLongitude()*100000);
//          changeFormat(latitude,longitude);
        } else {
            latitude = 24549031;
            longitude = 118125610;
        }
    }
    // Gps 消息监听器
    private final LocationListener locationListener = new LocationListener() {

        // 位置发生改变后调用
        public void onLocationChanged(Location location) {
            //updateWithNewLocation(location);
            updateView(location);
        }
        // provider 被用户关闭后调用
        public void onProviderDisabled(String provider) {
            //updateWithNewLocation(null);
            updateView(null);
        }

        // provider 被用户开启后调用
        public void onProviderEnabled(String provider) {

        }

        // provider 状态变化时调用
        public void onStatusChanged(String provider, int status,Bundle extras) {
        }
    };

    private void openGPS() {
        // TODO Auto-generated method stub

        if (locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
                || locationManager
                .isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
                ){
            Toast.makeText(getActivity().getApplicationContext(), " 位置源已设置！ ", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getActivity().getApplicationContext(), " 位置源未设置！", Toast.LENGTH_SHORT).show();
    }

    private void getProvider() {
        // TODO Auto-generated method stub
        // 构建位置查询条件
        Criteria criteria = getCriteria();
        // 查询精度：高
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 是否查询海拨：否
        criteria.setAltitudeRequired(false);
        // 是否查询方位角 : 否
        criteria.setBearingRequired(false);
        // 是否允许付费：是
        criteria.setCostAllowed(true);
        // 电量要求：低
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // 返回最合适的符合条件的 provider ，第 2 个参数为 true 说明 , 如果只有一个 provider 是有效的 , 则返回当前
        // provider
        provider = locationManager.getBestProvider(criteria, true);
    }

//    public byte[] data;
    AudioRecorderButton.AudioFinishRecorderCallBack audioCallBack = new AudioRecorderButton.AudioFinishRecorderCallBack() {
        //触发语音按钮点击事件
        @Override
        public void onFinish(float seconds, String filePath) throws IOException {
            MainActivity.dialogManager.showLoad();
            Log.i("语音信息","此处结束录音，开始发送（在 MESSAGERCONTROL）");
            Msg msg = new Msg(seconds, null, filePath, Msg.RECORDER_SENT);
            mDatas.add(msg);
            //更新数据
            mAdapter.notifyDataSetChanged();
            //设置位置
            mListView.setSelection(mDatas.size() - 1);
            File recorder_file = new File(filePath);
            //录音时间过短但是刚好被发送，文件未保存
            if(recorder_file==null) {
                MainActivity.dialogManager.dimissDialog();
                Toast.makeText(MainActivity.context, "发送失败", Toast.LENGTH_LONG).show();
                return;
            }
            InputStream in = new FileInputStream(recorder_file);
             byte b[] = new byte[(int) recorder_file.length()];
            int r = (int) recorder_file.length();
            Log.d(TAG, "voice_send: "+ r);
            in.read(b);
            byte[] timer = new byte[4];
            timer = float2byte(seconds);
            if (BaseApplication.getInstance().isConnect_status_bit()) {//如果蓝牙连接顺畅
               // byte[] data0 = byteMerger(s2, timer);//组合字节组s2, time
                final byte[] data = byteMerger(timer, b);//组合字节组data0, b
                Log.d(TAG, "voice_send: "+data.length);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        //更新数据
//                        mAdapter.notifyDataSetChanged();
//                        //设置位置
//                        mListView.setSelection(mDatas.size() - 1);
                        mBluetoothLeService.sendDataByType(BluetoothLeService.VOICE_MARK,data);
//                        MainActivity.dialogManager.dimissDialog();
                        Message test=new Message();
                        test.what=100;
                        handler.sendMessage(test);
                    }
                }).start();


            }
        }
    };

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (mBluetoothLeService != null) {
                    if (!BaseApplication.getInstance().isConnect_status_bit()) {
                        updateConnectionState(R.string.connecting);
                        show_view(false);
                       // final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                       // Log.d(TAG, "Connect request result=" + result);
                    }
                }
            }
            if(msg.what==100){
//                //更新数据
//                mAdapter.notifyDataSetChanged();
//                //设置位置
//                mListView.setSelection(mDatas.size() - 1);
//                mBluetoothLeService.sendDataByType(BluetoothLeService.VOICE_MARK,data);
                MainActivity.dialogManager.dimissDialog();
            }
            super.handleMessage(msg);
        }
    };

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    //------------蓝牙连接顺畅的情况下，屏幕中的按钮方可点击
  private  void show_view(boolean p) {
        if (p) {
            mBtnMsg.setEnabled(true);
            mBtnLocation.setEnabled(true);
            mAudioRecorderButton.setEnabled(true);
        } else {
            mBtnMsg.setEnabled(false);
            mBtnLocation.setEnabled(false);
            mAudioRecorderButton.setEnabled(false);
        }
    }
    //------------延时函数
    public void delay(int ms) {
        try {
            Thread.currentThread();
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateConnectionState(final int resourceId) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    /***
     * 蓝牙发送文本信息
     * @param str
     */
    private void bleSendData(String str){
        if(TextUtils.isEmpty(str)){
            Toast.makeText(mBluetoothLeService, "发送内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] WriteBytes = new byte[10*1024];
        try {
            WriteBytes = str.getBytes("GBK");//把输入的字符串转化为字节组
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] data = byteMerger(s1, WriteBytes);//组合字节组s1，WriteBytes
//        mBluetoothLeService.senddata(s1, WriteBytes);//发送文本信息
        mBluetoothLeService.sendDataByType(BluetoothLeService.CHA_MARK, WriteBytes);//发送文本信息
    }
    //创建文件名
    public static void getFileName() {
        //fileName = UUID.randomUUID().toString() + ".amr";//创建文件名
        fileName_ReceivedRecorder = "lora"+System.currentTimeMillis()+".amr";
        Log.i("新的文件名",fileName_ReceivedRecorder);
    }
public static String mAbsolutePath;
    //创建文件
    public static void createByteFile(byte[] bytes) {
        Log.i("錄音","此處保存他人發來的錄音");
        File file = new File(Environment.getExternalStorageDirectory(), fileName_ReceivedRecorder);
        FileOutputStream outputStream = null;//创建FileOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;//创建BufferedOutputStream对象
        try {
            //如果文件存在则删除
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            mAbsolutePath = file.getAbsolutePath();
            outputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(bytes);
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //接收数据
    private void displayData(byte data1[]) {
        Log.i("信息","别人写的display");
        String byteChar = null;
        if (data1 != null && data1.length > 0) {
            //截取收到的字节组数据的第一个字节
            byte[] n = new byte[1];
            System.arraycopy(data1, 0, n, 0, 1);
            byte m = n[0];
            String a = String.valueOf(m);
            Log.d(TAG, "voice_recive: "+data1.length);
            if((m & BluetoothLeService.VOICE_MARK) == 0){
           // if (a.equals("48")) {//如果第一个字节的数字
                //从字节组数据的第二个字节起截取，获得文本信息数据
                byte[] data_received = new byte[data1.length - 1];
                System.arraycopy(data1, 1, data_received, 0, data1.length - 1);
                try {//转换字节组格式
                    byteChar = new String(data_received, "GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(byteChar.contains("{")){
                    Log.d(TAG, "displayData: "+byteChar);
                    Log.i("接收到了消息","-------------------------------------------------------------------------------");
                    //TODO:广播发送时有乱码
                    Gson gson = new Gson();
                    UserInfo userInfo = gson.fromJson(byteChar,UserInfo.class);
                     userInfo = new UserInfo("13860473129","123456","201702293112156778","hjf","cwj","13599510549");
                    userInfo.setAddress("厦门");
                    userInfo.setMail("hjf@123.com");
                    SessionUtils.getInstance(getActivity().getApplicationContext()).saveMemberUserInfo(userInfo);
                    Log.d(TAG, "displayData: "+userInfo);
                }else {
                    Msg msg = new Msg(0, byteChar, null, Msg.MSG_RECEIVED);//显示文本信息
                    mDatas.add(msg);
                    //更新数据
                    mAdapter.notifyDataSetChanged();
                    //设置位置
                    mListView.setSelection(mDatas.size() - 1);
                    mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                    showIntentActivityNotify(friend_name,byteChar);
                }

            } else if ((m & BluetoothLeService.VOICE_MARK) > 0) {//
                //截取字节组数据的第二个字节到第四个，获得语音信息的时间数据
                byte[] time0 = new byte[4];
                System.arraycopy(data1, 1, time0, 0, 4);
                float time = byte2float(time0, 0);
                //从字节组数据的第五个字节起截取，获得语音信息数据
                byte[] data_received = new byte[data1.length - 5];
                System.arraycopy(data1, 5, data_received, 0, data1.length - 5);
                getFileName();
                createByteFile(data_received);
                Msg msg = new Msg(time, null, mAbsolutePath, Msg.RECORDER_RECEIVED);//显示语音信息
                mDatas.add(msg);
                //更新数据
                mAdapter.notifyDataSetChanged();
                //设置位置
                mListView.setSelection(mDatas.size() - 1);
                mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                showIntentActivityNotify(friend_name,"收到对方发来的语音消息！");
            }
//            else if (a.equals("50")) {//如果第一个字节的数字是'50'则为定位信息
//                //从字节组数据的第二个字节起截取，获得定位信息数据
//                byte[] data_received = new byte[data1.length - 1];
//                System.arraycopy(data1, 1, data_received, 0, data1.length - 1);
//                try {
//                    byteChar = new String(data_received, "GBK");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                Msg msg = new Msg(0, byteChar, null, Msg.MSG_RECEIVED);//显示收到的定位信息
//                mDatas.add(msg);
//                //更新数据
//                mAdapter.notifyDataSetChanged();
//                //设置位置
//                mListView.setSelection(mDatas.size() - 1);
//                mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
//                showIntentActivityNotify(friend_name,byteChar);
//            }
        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        if (gattServices.size() > 0 && mBluetoothLeService.get_connected_status(gattServices) >= 4) {
            if (connect_status_bit) {
                mConnected = true;
                show_view(true);
                mBluetoothLeService.enable_JDY_ble(true);
                try {
                    Thread.currentThread();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.enable_JDY_ble(true);
                updateConnectionState(R.string.connected);
                UserInfo userInfo = SessionUtils.getInstance(getActivity().getApplicationContext()).getLoginUserInfo();
                Gson gson = new Gson();
                if(userInfo != null){
                    String userStr = gson.toJson(userInfo);
                    Log.i("广播发送消息","我发送了消息了========================");
//                   bleSendData(userStr);//设备连接上后将队友个人信息广播
//                      bleSendData("1");

                }

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



    /**
     * 实时更新文本内容
     *
     * @param location
     */
    private String updateView(Location location) {
        String data_location = null;
//        if (location != null) {
//            data_location = "经度：" + location.getLongitude() + "°\n纬度：" + location.getLatitude()+ "°" ;
//        }
        if (location == null){
            locationManager.requestLocationUpdates(provider, 2000, (float) 0.1, locationListener);
        }
        if (location != null) {
            latitude = ((int)(location.getLatitude()*100000));
            longitude = (int)(location.getLongitude()*100000);

//          changeFormat(latitude,longitude);
        } else {
            latitude = 3995076;
            longitude = 11619675;
        }
        data_location = "经度：" + latitude + "°\n纬度：" + longitude+ "°" ;
        return data_location;
    }

    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    //------------消息通知
    private void initNotify(){
        mNotificationManager = (NotificationManager)getActivity().getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getActivity().getApplicationContext());
        mBuilder.setContentTitle("测试标题")
                .setContentText("测试内容")
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                //.setNumber(number)//显示数量
                .setTicker("测试通知来啦")//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
                //.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                //.setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.logo);
    }
    public PendingIntent getDefalutIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(getActivity(), 1, new Intent(), flags);
        return pendingIntent;
    }

    public static void showIntentActivityNotify(String who,String what){
        //Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
        //notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
        mBuilder.setAutoCancel(true)//点击后让通知将消失
                .setContentTitle(who)
                .setContentText(what)
                .setWhen(System.currentTimeMillis())
                .setTicker("您有未读消息（对，这是我写的）");
        //点击的意图ACTION是跳转到Intent
        Intent resultIntent = new Intent(MainActivity.context,MainActivity.class);
        resultIntent.putExtra("activity","message");
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.context, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(notifyId, mBuilder.build());
    }
}
