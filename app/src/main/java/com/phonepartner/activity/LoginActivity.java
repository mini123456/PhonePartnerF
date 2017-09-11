package com.phonepartner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.phonepartner.BaseActivity;
import com.phonepartner.R;
import com.phonepartner.View.InterceptLinearLayout;
import com.phonepartner.dao.UserInfoDao;
import com.phonepartner.entity.UserInfo;
import com.phonepartner.util.SessionUtils;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "LoginActivity";
    private TextView headtitle;
    private TextView tv_fun;
    private InterceptLinearLayout line_add_action;
    private ImageButton backImage;
    private InterceptLinearLayout line_backe_image;
    private RelativeLayout headline;
    private EditText etLoginPhone;
    private EditText etLoginPwd;
    private TextView line_forgetPwd;
    private Button btn_login;
    private  String mUserPhone = "";
    private String mUserPwd = "" ;
    private TextView tvCreateCountOnLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initData();
        initView();
        initEvent();
    }
    private void initData(){
        mUserPhone = SessionUtils.getInstance(getApplicationContext()).getLoginPhone();
        mUserPwd = SessionUtils.getInstance(getApplicationContext()).getLoginPwd();
    }
    private void initView() {
        headtitle = (TextView) findViewById(R.id.headtitle);
        headtitle.setText("登录");
        tv_fun = (TextView) findViewById(R.id.tv_fun);
        tv_fun.setVisibility(View.GONE);
        etLoginPhone = (EditText) findViewById(R.id.etLoginPhone);
        etLoginPwd = (EditText) findViewById(R.id.etLoginPwd);
        line_forgetPwd = (TextView) findViewById(R.id.line_forgetPwd);
        btn_login = (Button) findViewById(R.id.btn_login);
        line_backe_image = (InterceptLinearLayout) findViewById(R.id.line_backe_image);
        tvCreateCountOnLogin=(TextView)findViewById(R.id.tvCreateCountOnLogin);

        if(mUserPhone != null && !TextUtils.isEmpty(mUserPhone)){
            etLoginPhone.setText(mUserPhone);
        }
        if(mUserPwd != null && !TextUtils.isEmpty(mUserPwd)){
            etLoginPwd.setText(mUserPwd);
        }

    }

    private void initEvent(){
       // backImage.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        line_forgetPwd.setOnClickListener(this);
        line_backe_image.setOnClickListener(this);
        tvCreateCountOnLogin.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_login:
              //  startActivity(MainActivity.class);
                if(!isValid()){
                    return;
                }else {
                    login();
                }
                break;
            case R.id.line_forgetPwd:
                startActivity(ForgetPwdActivity.class);
                break;
            case R.id.line_backe_image:
                startActivity(new Intent(this,GuidActivity.class));
                finish();
                break;
            case R.id.tvCreateCountOnLogin:
                startActivity(RegisterActivity.class);
                finish();
                break;
            default:
                break;
        }
    }


    private boolean isValid() {
        // validate
        String etLoginPhoneString = etLoginPhone.getText().toString().trim();
        if (TextUtils.isEmpty(etLoginPhoneString)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return false;
        }

        String etLoginPwdString = etLoginPwd.getText().toString().trim();
        if (TextUtils.isEmpty(etLoginPwdString)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }

       return  true;
    }
    private void login(){
        String userName = etLoginPhone.getText().toString().trim();
        String userPwd = etLoginPwd.getText().toString().trim();
        UserInfo u = new UserInfo(userName, userPwd);
        UserInfoDao uid = new UserInfoDao(getApplicationContext());
        //int result = uid.login(u);
        UserInfo user  = uid.login(u);

//        if (result > 0) {
        if(user.getUid() != null){
            SessionUtils.getInstance(getApplicationContext()).saveLoginPhone(userName);
            SessionUtils.getInstance(getApplicationContext()).saveLoginPwd(userPwd);
            SessionUtils.getInstance(getApplicationContext()).saveLoginState(true);
            SessionUtils.getInstance(getApplicationContext()).saveLoginUserInfo(user);
//            UserInfo userInfo = SessionUtils.getInstance(getApplicationContext()).getLoginUserInfo();
//
//            if(userInfo == null){
//                SessionUtils.getInstance(getApplicationContext()).saveLoginUserInfo(user);
//            }
            showShortToast("登录成功！");
            startActivity(MainActivity.class);
            finish();
        } else {
           showShortToast("用户名或者密码错误！");
        }
    }
}
