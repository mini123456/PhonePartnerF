package com.phonepartner.activity;

import android.os.Bundle;
import android.text.TextUtils;
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

public class EditPwdActivity extends BaseActivity implements View.OnClickListener {

    private TextView headtitle;
    private InterceptLinearLayout line_backe_image;
    private ImageButton backImage;
    private EditText etBindPhone;
    private EditText etPwd;
    private EditText etCheckPwd;
    private Button btnCheckPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pwd);
        initView();
    }

    private void initView() {
        headtitle = (TextView) findViewById(R.id.headtitle);
        headtitle.setText("修改密码");
        line_backe_image = (InterceptLinearLayout) findViewById(R.id.line_backe_image);
        backImage = (ImageButton) findViewById(R.id.backImage);
        etBindPhone = (EditText) findViewById(R.id.etBindPhone);
        etPwd = (EditText) findViewById(R.id.etPwd);
        etCheckPwd = (EditText) findViewById(R.id.etCheckPwd);
        btnCheckPwd = (Button) findViewById(R.id.btnCheckPwd);

        line_backe_image.setOnClickListener(this);
        btnCheckPwd.setOnClickListener(this);
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
            Toast.makeText(this, "绑定的手机号", Toast.LENGTH_SHORT).show();
            return false;
        }

        String etPwdString = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(etPwdString)) {
            Toast.makeText(this, "设置您的密码", Toast.LENGTH_SHORT).show();
            return false;
        }

        String etCheckPwdString = etCheckPwd.getText().toString().trim();
        if (TextUtils.isEmpty(etCheckPwdString)) {
            Toast.makeText(this, "确认您的新密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!etCheckPwdString.equals(etCheckPwdString)){
            Toast.makeText(this, "两次输入的密码必须一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true ;

        // TODO validate success, do something


    }
}
