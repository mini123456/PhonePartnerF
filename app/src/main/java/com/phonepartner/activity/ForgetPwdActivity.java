package com.phonepartner.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.phonepartner.BaseActivity;
import com.phonepartner.R;
import com.phonepartner.View.InterceptLinearLayout;
import com.phonepartner.dao.UserInfoDao;
import com.phonepartner.entity.UserInfo;

public class ForgetPwdActivity extends BaseActivity implements View.OnClickListener {

    private TextView headtitle;
    private TextView tv_fun;
    private InterceptLinearLayout line_add_action;
    private InterceptLinearLayout line_backe_image;
    private EditText etBindPhone;
    private EditText etImageNum;
    private EditText etPwd;
    private EditText etCheckPwd;
    private Button btnCheckPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        initView();
        initEvent();

    }

    private void initView() {
        headtitle = (TextView) findViewById(R.id.headtitle);
        headtitle.setText("忘记密码");
        tv_fun = (TextView) findViewById(R.id.tv_fun);
        line_backe_image = (InterceptLinearLayout) findViewById(R.id.line_backe_image);
        etBindPhone = (EditText) findViewById(R.id.etBindPhone);
        etImageNum = (EditText) findViewById(R.id.etImageNum);
        etPwd = (EditText) findViewById(R.id.etPwd);
        etCheckPwd = (EditText) findViewById(R.id.etCheckPwd);
        btnCheckPwd = (Button) findViewById(R.id.btnCheckPwd);


    }
    private  void initEvent(){
        btnCheckPwd.setOnClickListener(this);
        line_backe_image.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.line_backe_image:
                finish();
                break;
            case R.id.btnCheckPwd:
                if(!isValid()){
                    return;
                }else {
                    UserInfo u = new UserInfo();
                    u.setName(etBindPhone.getText().toString().trim());
                    UserInfoDao uid = new UserInfoDao(getApplicationContext());
                   boolean success =  uid.updatePwd(u);
                    if(success){
                     showShortToast("密码修改成功");
                        finish();
                    }else {
                        showShortToast("账号输入错误");
                    }
                }
                break;

        }
    }

    private boolean isValid() {
        // validate
        String etBindPhoneString = etBindPhone.getText().toString().trim();
        if (TextUtils.isEmpty(etBindPhoneString)) {
            Toast.makeText(this, "请输入绑定的手机号", Toast.LENGTH_SHORT).show();
            return false;
        }

        String etImageNumString = etImageNum.getText().toString().trim();
        if (TextUtils.isEmpty(etImageNumString)) {
            Toast.makeText(this, "请输入图形验证码", Toast.LENGTH_SHORT).show();
            return false;
        }

        String etPwdString = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(etPwdString)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        String etCheckPwdString = etCheckPwd.getText().toString().trim();
        if (TextUtils.isEmpty(etCheckPwdString)) {
            Toast.makeText(this, "请确认密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!etImageNumString .equals("123")){
            Toast.makeText(this, "图形验证码错误", Toast.LENGTH_SHORT).show();
            return  false ;
        }
        return true ;

    }
}
