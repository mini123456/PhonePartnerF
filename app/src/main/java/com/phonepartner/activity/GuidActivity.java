package com.phonepartner.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.phonepartner.BaseActivity;
import com.phonepartner.Emoji.EmojiConversionUtil;
import com.phonepartner.R;

public class GuidActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvHasCount;
    private TextView tvCreateCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guid);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
        initEvent();
    }

    private void initView() {
        tvHasCount = (TextView) findViewById(R.id.tvHasCount);
        tvCreateCount = (TextView) findViewById(R.id.tvCreateCount);
//       Handler mHandler = new Handler();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //  mPreferences = getPreferences(MODE_PRIVATE);
//                Boolean ischoice = SessionUtils.getInstance(getApplicationContext()).getLoginState();//是否为第一次使用该程序
//                if(ischoice){
//                    Intent intent = new Intent(GuidActivity.this,MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }else{
//                    Intent intent = new Intent(GuidActivity.this,LoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        },3000);

        //emoji初始化
        new Thread(new Runnable() {
            @Override
            public void run() {
                //加载表情库
                EmojiConversionUtil.getInstace().getFileText(getApplication());
            }
        }).start();
    }
    private void initEvent(){
        tvHasCount.setOnClickListener(this);
        tvCreateCount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvHasCount://已有账户，跳转到登录界面
                startActivity(LoginActivity.class);
                finish();
                break;
            case R.id.tvCreateCount://没有账户，跳转到注册界面
                startActivity(RegisterActivity.class);
                finish();
                break;
        }

    }
}
