package com.phonepartner.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.phonepartner.BaseActivity;
import com.phonepartner.R;
import com.phonepartner.View.InterceptLinearLayout;
import com.phonepartner.entity.UserInfo;

public class TeamMemberActivity extends BaseActivity implements View.OnClickListener {

    private TextView headtitle;
    private ImageButton backImage;
    private InterceptLinearLayout line_backe_image;
    private TextView tvAccount;
    private TextView tvUserName;
    private TextView tvEmail;
    private TextView tvAddress;
    private TextView tvEmergencyName1;
    private TextView tvEmergencyPhone1;
    private TextView tvDeviceName;
    private TextView tvDeviceAddress;
    private UserInfo mUserInfo ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_member);
        initView();
        initData();
        setData();
    }

    private void initData(){
        String str = getIntent().getExtras().getString("user");
        Gson gson = new Gson();
        mUserInfo = gson.fromJson(str,UserInfo.class);
    }
    private void initView() {
        headtitle = (TextView) findViewById(R.id.headtitle);
        headtitle.setText("队员信息");
        backImage = (ImageButton) findViewById(R.id.backImage);
        line_backe_image = (InterceptLinearLayout) findViewById(R.id.line_backe_image);
        tvAccount = (TextView) findViewById(R.id.tvAccount);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvEmergencyName1 = (TextView) findViewById(R.id.tvEmergencyName1);
        tvEmergencyPhone1 = (TextView) findViewById(R.id.tvEmergencyPhone1);
        tvDeviceName = (TextView) findViewById(R.id.tvDeviceName);
        tvDeviceAddress = (TextView) findViewById(R.id.tvDeviceAddress);
        line_backe_image.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.line_backe_image:
                finish();
                break;
        }
    }
    private void setData(){
        if(mUserInfo!=null){
            tvAccount.setText(mUserInfo.getName());
            tvUserName.setText(mUserInfo.getMemoName());
            tvEmail.setText(mUserInfo.getMail());
            tvAddress.setText(mUserInfo.getAddress());
            tvEmergencyName1.setText(mUserInfo.getEmergenceName1());
            tvEmergencyPhone1.setText(mUserInfo.getEmergencePhone1());
            if(mUserInfo.getDevice() !=null){
                tvDeviceName.setText(mUserInfo.getDevice().getName());
                tvDeviceAddress.setText(mUserInfo.getDevice().getAddress());
                Log.d("设备地址：",mUserInfo.getDevice().getAddress());
            }
        }
    }
}
