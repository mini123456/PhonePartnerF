package com.phonepartner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.phonepartner.BaseActivity;
import com.phonepartner.R;
import com.phonepartner.View.InterceptLinearLayout;
import com.phonepartner.dao.UserInfoDao;
import com.phonepartner.entity.UserInfo;
import com.phonepartner.util.DateUtils;

import java.util.Date;
import java.util.Random;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "RegisterActivity";
    private TextView headtitle;
    private TextView tv_fun;
    private ImageButton backImage;
    private InterceptLinearLayout line_backe_image;
    private EditText etRegisterPhone;
    private EditText etRegisterPwd;
    private EditText etCheckPwd;
    private Button btn_regist;
    private EditText etUserName;
    private EditText etEmName;
    private EditText etEmPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regisit);
        initView();
        initEvent();
    }

    private void initView() {
        headtitle = (TextView) findViewById(R.id.headtitle);
        headtitle.setText("注册");
        tv_fun = (TextView) findViewById(R.id.tv_fun);
        tv_fun.setVisibility(View.GONE);
        backImage = (ImageButton) findViewById(R.id.backImage);
        backImage.setVisibility(View.VISIBLE);
        line_backe_image = (InterceptLinearLayout) findViewById(R.id.line_backe_image);
        etRegisterPhone = (EditText) findViewById(R.id.etRegisterPhone);
        etRegisterPwd = (EditText) findViewById(R.id.etRegisterPwd);
        etCheckPwd = (EditText) findViewById(R.id.etCheckPwd);
        btn_regist = (Button) findViewById(R.id.btn_regist);

        etUserName = (EditText) findViewById(R.id.etUserName);
        etEmName = (EditText) findViewById(R.id.etEmName);
        etEmPhone = (EditText) findViewById(R.id.etEmPhone);

    }

    private void initEvent() {
        line_backe_image.setOnClickListener(this);
        btn_regist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.line_backe_image:
                startActivity(new Intent(this,GuidActivity.class));
                finish();
                break;
            case R.id.btn_regist:
                if (!isValid()) {
                    return;
                } else {
                    regisiter();
                }

                break;
        }
    }


    private boolean isValid() {

        String etRegisterPhoneString = etRegisterPhone.getText().toString().trim();
        if (TextUtils.isEmpty(etRegisterPhoneString)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etRegisterPhoneString.length() != 11) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        String etRegisterPwdString = etRegisterPwd.getText().toString().trim();
        if (TextUtils.isEmpty(etRegisterPwdString)) {
            Toast.makeText(this, "设置您的登录密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etRegisterPwdString.length() < 6) {
            Toast.makeText(this, "密码长度不得少于6位", Toast.LENGTH_SHORT).show();
            return false;
        }
        String etCheckPwdString = etCheckPwd.getText().toString().trim();
        if (TextUtils.isEmpty(etCheckPwdString)) {
            Toast.makeText(this, "确认您的登录密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!etRegisterPwdString.equals(etCheckPwdString)) {
            Toast.makeText(this, "两次输入密码要一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        String etUserNameString = etUserName.getText().toString().trim();
        if (TextUtils.isEmpty(etUserNameString)) {
            Toast.makeText(this, "请输入您的姓名", Toast.LENGTH_SHORT).show();
            return false;
        }

        String etEmNameString = etEmName.getText().toString().trim();
        if (TextUtils.isEmpty(etEmNameString)) {
            Toast.makeText(this, "请输入紧急联系人姓名", Toast.LENGTH_SHORT).show();
            return false;
        }

        String etEmPhoneString = etEmPhone.getText().toString().trim();
        if (TextUtils.isEmpty(etEmPhoneString)) {
            Toast.makeText(this, "请输入紧急联系人电话", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private void regisiter() {
        String userName = etRegisterPhone.getText().toString().trim();
        String userPwd = etRegisterPwd.getText().toString().trim();
        String momoName = etUserName.getText().toString().trim();
        String emName = etEmName.getText().toString().trim();
        String emPhone = etEmPhone.getText().toString().trim();
        String uuid = createUid(userName);
      //  UserInfo u = new UserInfo(userName, userPwd, uuid);
        UserInfo u = new UserInfo(userName,userPwd,uuid,momoName,emName,emPhone);
        UserInfoDao uid = new UserInfoDao(getApplicationContext());
        boolean result = uid.intsert(u);
        if (result) {
            showShortToast("注册成功！");
            Log.d(TAG, "regisiter: " + uuid);
            startActivity(LoginActivity.class);
            finish();
        } else {
            showShortToast("注册失败");
        }

    }

    private String createUid(String phone) {
        String uid = "";
        String dateStr = DateUtils.DateToString(new Date(), DateUtils.formatYMDHMS);
        Random random = new Random();
        int i = random.nextInt(10);
        int j = random.nextInt(10);
        if (phone.length() == 11) {
            phone = phone.substring(8, 10);
            uid = dateStr + phone + i + j;
        }

        return uid;
    }


}
