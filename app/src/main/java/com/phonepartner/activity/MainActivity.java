package com.phonepartner.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.phonepartner.BaseActivity;
import com.phonepartner.R;
import com.phonepartner.entity.UserInfo;
import com.phonepartner.frgment.DeviceScanConFragment;
import com.phonepartner.frgment.MessageContolFragment;
import com.phonepartner.frgment.PersonalFragment;
import com.phonepartner.manager.DialogManager;
import com.phonepartner.util.MessageEvent;

import de.greenrobot.event.EventBus;

import static com.phonepartner.R.id.rbDervieceConnect;

public class MainActivity extends BaseActivity implements MessageContolFragment.OnLocationClickListener {
    public static  Context context = null;
    public static final String TAG ="MainActivity";
    private RadioGroup radioGroup;
    public static final String fragment1Tag = "deviceConnect";
  public static final String fragment2Tag = "messageControl";
    public static final String fragment3Tag = "map";
    public static final String fragment4Tag = "personal";
    public  static DialogManager dialogManager=null;
    public static FragmentManager fragmentManager;
    Fragment fragment1 ;
   Fragment fragment2 ;
    Fragment fragment3 ;
    Fragment fragment4 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        context=getApplicationContext();
        initView();
        initEvent();
//        //app重启时运行的函数
//        if (savedInstanceState == null) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            Fragment fragment = new DeviceScanConFragment();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.container, fragment, fragment1Tag).commit();
//
//        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fragment2 = fm.findFragmentByTag(fragment2Tag);
        Log.i("信息","========================"+fragment2);
        if (fragment2 == null) {
            Log.e(TAG, "=================new MessageFragment====================");
            fragment2 = new MessageContolFragment();
            ft.add(R.id.container, fragment2, fragment2Tag);
            ft.hide(fragment2);
        }
        ft.commit();

         fm = getSupportFragmentManager();
         ft = fm.beginTransaction();
        fragment3 = fm.findFragmentByTag(fragment3Tag);
        Log.i("信息","========================"+fragment2);
        if (fragment3 == null) {
            Log.e(TAG, "=================new MessageFragment====================");
            fragment3 = new BMap();
            ft.add(R.id.container, fragment3, fragment3Tag);
            ft.hide(fragment3);
        }
        ft.commit();

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        fragment4 = fm.findFragmentByTag(fragment4Tag);
        Log.i("信息","========================"+fragment2);
        if (fragment4 == null) {
            Log.e(TAG, "=================new MessageFragment====================");
            fragment4 = new PersonalFragment();
            ft.add(R.id.container, fragment4, fragment4Tag);
            ft.hide(fragment4);
        }
        ft.commit();


        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        fragment1 = fm.findFragmentByTag(fragment1Tag);
        Log.i("信息","========================"+fragment2);
        if (fragment1 == null) {
            Log.e(TAG, "=================new MessageFragment====================");
            fragment1 = new DeviceScanConFragment();
            ft.add(R.id.container, fragment1, fragment1Tag);
            ft.show(fragment1);
        }
        ft.commit();



        dialogManager= new DialogManager(this);
        fragmentManager =getSupportFragmentManager();
        Log.i("Intent","我被执行了1");
        Intent message=getIntent();
        if(message.getStringExtra("activity")!=null){
            Log.i("Intent","我被执行了2"+message.getStringExtra("activity"));
//            FragmentManager fms = getSupportFragmentManager();
//            FragmentTransaction fts = fm.beginTransaction();
//            ft.hide(fragment1);
//            ft.hide(fragment3);
//            ft.hide(fragment4);
//            ft.show(fragment2);
//            ft.commit();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume: ");

    }


    private void initView(){
        radioGroup = (RadioGroup) findViewById(R.id.activity_group_radioGroup);
        //尝试让其同框
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
//            fragment2= new MessageContolFragment();
//            fragmentManager.beginTransaction()
//                    .add(R.id.container, fragment2, fragment2Tag);
//            fragmentTransaction.hide(fragment2);
//            fragmentTransaction.commit();


//        rbDervieceConnect = (RadioButton) findViewById(rbDervieceConnect);
//        rbMap = (RadioButton) findViewById(R.id.rbMap);
//        rbMessage = (RadioButton) findViewById(R.id.rbMessage);
        //fragment1 = new DeviceScanConFragment();
//        fragment2 = new MessageContolFragment();
        //fragment3 = new BMap();
        //fragment4 = new PersonalFragment();
    }
    private void initEvent(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                 fragmentManager= getSupportFragmentManager();
                 FragmentTransaction ft = fragmentManager.beginTransaction();
                fragment1 = fragmentManager.findFragmentByTag(fragment1Tag);
                fragment2 = fragmentManager.findFragmentByTag(fragment2Tag);
                fragment3 = fragmentManager.findFragmentByTag(fragment3Tag);
                fragment4 = fragmentManager.findFragmentByTag(fragment4Tag);
                if (fragment1 != null) {
                    ft.hide(fragment1);
                }
                if (fragment2 != null) {
                    ft.hide(fragment2);
                }
                if (fragment3 != null) {
                    ft.hide(fragment3);
                }
                if (fragment4 != null) {
                    ft.hide(fragment4);
                }
                View checkView = radioGroup.findViewById(checkedId);
                if (!checkView.isPressed()){
                    return;
                }
                switch (checkedId) {
                    case rbDervieceConnect:
                        if (fragment1 == null) {
                            fragment1 = new DeviceScanConFragment();
                            ft.add(R.id.container, fragment1, fragment1Tag);
                        } else {
                            ft.show(fragment1);
                        }
                        break;
                    case R.id.rbMessage:
                        if (fragment2 == null) {
                            fragment2 = new MessageContolFragment();
                            ft.add(R.id.container, fragment2, fragment2Tag);
                        } else {
                            ft.show(fragment2);
                        }
                        break;
                    case R.id.rbMap:
                        //此处为加载地图
                        BMap.mode=0;
                        if (fragment3 == null) {

                            fragment3 = new BMap();
                            ft.add(R.id.container, fragment3,
                                    fragment3Tag);
                        } else {
                            ft.show(fragment3);
                        }
                        break;
                    case R.id.rbSetting:
                        if (fragment4 == null) {
                            fragment4 = new PersonalFragment();
                            ft.add(R.id.container, fragment4, fragment4Tag);
                        } else {
                            ft.show(fragment4);
                        }
                        break;
                    default:
                        break;
                }
                ft.commit();
            }
        });

    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton mTab = (RadioButton) radioGroup.getChildAt(i);
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag((String) mTab.getTag());
            FragmentTransaction ft = fm.beginTransaction();
            if (fragment != null) {
                if (!mTab.isChecked()) {
                    ft.hide(fragment);
                }
            }
            ft.commit();
        }
    }

    /***
     * 方法一 再按一次退出程序
     */
    private static boolean isExit = false;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }


    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
            //  AtyContainer.finishAllActivity();
        }
    }

    @Override
    public void onLocationClick(int person, UserInfo user,String text) {
        ((RadioButton)radioGroup.getChildAt(2)).setChecked(true);
         fragmentManager= getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        fragment1 = fragmentManager.findFragmentByTag(fragment1Tag);
        fragment2 = fragmentManager.findFragmentByTag(fragment2Tag);
        fragment3 = fragmentManager.findFragmentByTag(fragment3Tag);
        fragment4 = fragmentManager.findFragmentByTag(fragment4Tag);
        if (fragment1 != null) {
            ft.hide(fragment1);
        }
        if (fragment2 != null) {
            ft.hide(fragment2);
        }
        if (fragment3 != null) {
            ft.hide(fragment3);
        }
        if (fragment4 != null) {
            ft.hide(fragment4);
        }
        if (fragment1 != null) {
            ft.hide(fragment1);
        }
        if (fragment2 != null) {
            ft.hide(fragment2);
        }
        if (fragment3 != null) {
            ft.hide(fragment3);
        }
        if (fragment4 != null) {
            ft.hide(fragment4);
        }
        if (fragment3 == null) {
            fragment3 = new BMap();

            ft.add(R.id.container, fragment3,
                    fragment3Tag);
            //此处解决初始化时信息无法传递的问题
            Bundle bundle =new Bundle();
            String device_address="未连接设备";
            String device_name="未连接设备";
            try{
                device_address=user.getDevice().getAddress();
                device_name=user.getDevice().getName();
            }catch (Exception e){

            }
            String userInfoDetail =
                    "设备编号：" + device_address+ "\n"
                            + "设备名称：" + device_name + "\n"
                             +"姓名：" + user.getMemoName() + "\n" +
                            "紧急联系人" + "\n" +
                            user.getEmergenceName1() + user.getEmergencePhone1() + "\n" +
                            text;
            bundle.putString("singal",userInfoDetail);
            fragment3.setArguments(bundle);
          //((RadioButton)radioGroup.getChildAt(2)).setChecked(false);
        } else {
            ft.show(fragment3);
          // ((RadioButton)radioGroup.getChildAt(2)).setChecked(false);
        }

       // Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        //进行消息的推送
        MessageEvent me = new MessageEvent();
        me.setMesage(text);
        me.setRole(person);
        me.setUserInfo(user);
        EventBus.getDefault().post(me);
        BMap.mode=1;
        ft.commit();
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.i("Intent","我被执行了3");
        Intent message=getIntent();
        if(message.getStringExtra("activity")!=null){
            Log.i("Intent","我被执行了4"+message.getStringExtra("activity"));
            fragmentManager.beginTransaction().show(fragment2).commit();
            fragmentManager.beginTransaction().hide(fragment1).commit();
            fragmentManager.beginTransaction().hide(fragment3).commit();
            fragmentManager.beginTransaction().hide(fragment4).commit();
        }
    }
}
