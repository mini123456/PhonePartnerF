package com.phonepartner.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.phonepartner.BaseActivity;
import com.phonepartner.R;
import com.phonepartner.View.InterceptLinearLayout;
import com.phonepartner.dao.UserInfoDao;
import com.phonepartner.entity.UserInfo;
import com.phonepartner.util.SessionUtils;

public class PersponalActivity extends BaseActivity implements View.OnClickListener {

    private TextView headtitle;
    private TextView tv_fun;
    private InterceptLinearLayout line_add_action;
    private InterceptLinearLayout line_backe_image;
    private EditText etAccount;
    private LinearLayout lineAccount;
    private LinearLayout line_pwd;
    private EditText etPwd;
    private ImageView image_show_hide_pwd;
    private EditText etUserName;
    private RadioGroup rgSexSelect;
    private LinearLayout line_sex;
    private EditText etRegisterNum;
    private EditText etEmail;
    private EditText etAddress;
    private EditText etEmergencyName1;
    private EditText etEmergencyPhone1;
    private EditText etEmergencyName2;
    private EditText etEmergencyPhone2;
    private EditText etEmergencyName3;
    private EditText etEmergencyPhone3;
    private boolean isEdit = false;
    private TextView tvEmergencyDivider1;
    private TextView tvEmergencyName1;
    private TextView tvEmergencyDivider2;
    private TextView tvEmergencyName2;
    private TextView tvEmergencyDivider3;
    private TextView tvEmergencyName3;
    private LinearLayout lineEmergencyName1;
    private LinearLayout lineEmergencyName2;
    private LinearLayout lineEmergencyName3;
    private LinearLayout line_register;
    private TextView tvPwdDevider;
    private TextView tvSexDevider;
    private TextView tv_registNum_divide ;


    private UserInfo mUserInfo ;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveUserInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persponal);
        initData();
        initView();
        setData();
        initEvent();
    }

    private void initData(){
        mUserInfo = SessionUtils.getInstance(getApplicationContext()).getLoginUserInfo();
    }
    private void initView() {
        headtitle = (TextView) findViewById(R.id.headtitle);
        headtitle.setText("个人信息");
        tv_fun = (TextView) findViewById(R.id.tv_fun);
        line_add_action = (InterceptLinearLayout) findViewById(R.id.line_add_action);
        tv_fun.setVisibility(View.VISIBLE);

        if (!isEdit) {
            tv_fun.setText("编辑");
        } else {
            tv_fun.setText("完成");
        }

        line_backe_image = (InterceptLinearLayout) findViewById(R.id.line_backe_image);
        etAccount = (EditText) findViewById(R.id.etAccount);
        lineAccount = (LinearLayout) findViewById(R.id.lineAccount);
        line_pwd = (LinearLayout) findViewById(R.id.line_pwd);
        etPwd = (EditText) findViewById(R.id.etPwd);
        image_show_hide_pwd = (ImageView) findViewById(R.id.image_show_hide_pwd);
        etUserName = (EditText) findViewById(R.id.etUserName);
        rgSexSelect = (RadioGroup) findViewById(R.id.rgSexSelect);
        line_register = (LinearLayout) findViewById(R.id.line_register);
        line_sex = (LinearLayout) findViewById(R.id.line_sex);
        etRegisterNum = (EditText) findViewById(R.id.etRegisterNum);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etEmergencyName1 = (EditText) findViewById(R.id.etEmergencyName1);
        etEmergencyPhone1 = (EditText) findViewById(R.id.etEmergencyPhone1);
        etEmergencyName2 = (EditText) findViewById(R.id.etEmergencyName2);
        etEmergencyPhone2 = (EditText) findViewById(R.id.etEmergencyPhone2);
        etEmergencyName3 = (EditText) findViewById(R.id.etEmergencyName3);
        etEmergencyPhone3 = (EditText) findViewById(R.id.etEmergencyPhone3);

        tvEmergencyDivider1 = (TextView) findViewById(R.id.tvEmergencyDivider1);
        tvEmergencyName1 = (TextView) findViewById(R.id.tvEmergencyName1);
        tvEmergencyDivider2 = (TextView) findViewById(R.id.tvEmergencyDivider2);
        tvEmergencyName2 = (TextView) findViewById(R.id.tvEmergencyName2);
        tvEmergencyDivider3 = (TextView) findViewById(R.id.tvEmergencyDivider3);
        tvEmergencyName3 = (TextView) findViewById(R.id.tvEmergencyName3);
        lineEmergencyName1 = (LinearLayout) findViewById(R.id.lineEmergencyName1);
        lineEmergencyName2 = (LinearLayout) findViewById(R.id.lineEmergencyName2);
        lineEmergencyName3 = (LinearLayout) findViewById(R.id.lineEmergencyName3);
        tvPwdDevider = (TextView) findViewById(R.id.tvPwdDevider);
        tvSexDevider = (TextView) findViewById(R.id.tvSexDevider);
        tv_registNum_divide = (TextView) findViewById(R.id.tv_registNum_divide);

        lineEmergencyName1.setVisibility(View.GONE);
        tvEmergencyDivider1.setVisibility(View.GONE);
        lineEmergencyName2.setVisibility(View.GONE);
        tvEmergencyDivider2.setVisibility(View.GONE);
        lineEmergencyName3.setVisibility(View.GONE);
        tvEmergencyDivider3.setVisibility(View.GONE);
        tvEmergencyName1.setVisibility(View.VISIBLE);
        tvEmergencyName2.setVisibility(View.VISIBLE);
        tvEmergencyName3.setVisibility(View.VISIBLE);
        etAccount.setEnabled(false);
        etAccount.setHint("");
        etUserName.setEnabled(false);
        etUserName.setHint("");
        etPwd.setEnabled(false);
        etPwd.setHint("");
        etRegisterNum.setEnabled(false);
        etRegisterNum.setHint("");
        etEmail.setEnabled(false);
        etEmail.setHint("");
        etAddress.setEnabled(false);
        etAddress.setHint("");
        etEmergencyPhone1.setEnabled(false);
        etEmergencyPhone1.setHint("");
        etEmergencyPhone2.setEnabled(false);
        etEmergencyPhone2.setHint("");
        etEmergencyPhone3.setEnabled(false);
        etEmergencyPhone3.setHint("");

    }

    private void setData(){
        if(mUserInfo !=null){
            if(mUserInfo.getName() != null && mUserInfo.getPwd() != null){
                etAccount.setText(mUserInfo.getName());
                etPwd.setText(mUserInfo.getPwd());
            }
            if(mUserInfo.getAddress() != null){
                etAddress.setText(mUserInfo.getAddress());
            }
            if(mUserInfo.getMemoName() != null){
                etUserName.setText(mUserInfo.getMemoName());
            }
            if(mUserInfo.getUid() != null ){
                etRegisterNum.setText(mUserInfo.getUid());
            }
            if(mUserInfo.getMail() != null){
                etEmail.setText(mUserInfo.getMail());
            }
            if(mUserInfo.getEmergenceName1()!=null){
                tvEmergencyName1.setText(mUserInfo.getEmergenceName1());
                etEmergencyName1.setText(mUserInfo.getEmergenceName1());
            }else {
                tvEmergencyName1.setVisibility(View.GONE);
            }
            if(mUserInfo.getEmergenceName2() != null){
                tvEmergencyName2.setText(mUserInfo.getEmergenceName2());
                etEmergencyName2.setText(mUserInfo.getEmergenceName2());
            }else {
                tvEmergencyName2.setVisibility(View.GONE);
            }
            if(mUserInfo.getEmergenceName3() != null){
                tvEmergencyName3.setText(mUserInfo.getEmergenceName3());
                etEmergencyName3.setText(mUserInfo.getEmergenceName3());
            }else {
                tvEmergencyName3.setVisibility(View.GONE);
            }
            if(mUserInfo.getEmergencePhone1() != null){
                etEmergencyPhone1.setText(mUserInfo.getEmergencePhone1());
            }
            if(mUserInfo.getEmergencePhone2() != null){
                etEmergencyPhone2.setText(mUserInfo.getEmergencePhone2());
            }
            if(mUserInfo.getEmergencePhone3() != null){
                etEmergencyPhone3.setText(mUserInfo.getEmergencePhone3());
            }
            if(mUserInfo.getSex() != null){
             rgSexSelect.check(mUserInfo.getSex().equals("男") ? R.id.rbMale : R.id.rbFemale);
            }
        }
    }
    private void initEvent() {
        line_backe_image.setOnClickListener(this);
        line_add_action.setOnClickListener(this);
        image_show_hide_pwd.setOnClickListener(this);
        rgSexSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton)findViewById(rgSexSelect.getCheckedRadioButtonId());
                mUserInfo.setSex(radioButton.getText().toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.line_backe_image:
                saveUserInfo();

                break;
            case R.id.image_show_hide_pwd:
                v.setSelected(!v.isSelected());
                if (!v.isSelected()) {
                    etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());//密码显示
                } else {
                    etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//密码隐藏
                }
                break;
            case R.id.line_add_action:
                // showShortToast("点击");
                if(tv_fun.getText().equals("编辑")){
                    tv_fun.setText("完成");
                    isEdit =true;
                }else {
                    tv_fun.setText("编辑");
                    isEdit = false ;
                }
                if (isEdit) {//编辑状态
                   // tv_fun.setText("完成");
                    String str = tv_fun.getText().toString();
                   // isEdit = false;
                    lineAccount.setVisibility(View.GONE);
                    line_pwd.setVisibility(View.GONE);
                    line_sex.setVisibility(View.GONE);
                    line_register.setVisibility(View.GONE);
                    tvPwdDevider.setVisibility(View.GONE);
                    tvSexDevider.setVisibility(View.GONE);
                    tv_registNum_divide.setVisibility(View.GONE);

                    lineEmergencyName1.setVisibility(View.VISIBLE);
                    tvEmergencyDivider1.setVisibility(View.VISIBLE);
                    lineEmergencyName2.setVisibility(View.VISIBLE);
                    tvEmergencyDivider2.setVisibility(View.VISIBLE);
                    lineEmergencyName3.setVisibility(View.VISIBLE);
                    tvEmergencyDivider3.setVisibility(View.VISIBLE);
                    tvEmergencyName1.setVisibility(View.GONE);
                    tvEmergencyName2.setVisibility(View.GONE);
                    tvEmergencyName3.setVisibility(View.GONE);

                    etUserName.setEnabled(true);
                    etUserName.setHint("请输入姓名/备注名");

                    etEmail.setEnabled(true);
                    etEmail.setHint("请输入邮箱");
                    etAddress.setEnabled(true);
                    etAddress.setHint("请输入地址");
                    etEmergencyPhone1.setEnabled(true);
                    etEmergencyPhone1.setHint("请输入电话");
                    etEmergencyPhone2.setEnabled(true);
                    etEmergencyPhone2.setHint("请输入电话");
                    etEmergencyPhone3.setEnabled(true);
                    etEmergencyPhone3.setHint("请输入电话");

                } else {//查看状态
                   // tv_fun.setText("编辑");
                    isEdit = true;
                    line_pwd.setVisibility(View.VISIBLE);
                    line_sex.setVisibility(View.VISIBLE);
                    tvPwdDevider.setVisibility(View.VISIBLE);
                    tvSexDevider.setVisibility(View.VISIBLE);
                    lineAccount.setVisibility(View.VISIBLE);
                    tv_registNum_divide.setVisibility(View.VISIBLE);
                    line_register.setVisibility(View.VISIBLE);

                    lineEmergencyName1.setVisibility(View.GONE);
                    tvEmergencyDivider1.setVisibility(View.GONE);
                    lineEmergencyName2.setVisibility(View.GONE);
                    tvEmergencyDivider2.setVisibility(View.GONE);
                    lineEmergencyName3.setVisibility(View.GONE);
                    tvEmergencyDivider3.setVisibility(View.GONE);
                    tvEmergencyName1.setVisibility(View.VISIBLE);
                    tvEmergencyName2.setVisibility(View.VISIBLE);
                    tvEmergencyName3.setVisibility(View.VISIBLE);

                    etAccount.setEnabled(false);

                    etAccount.setHint("");
                    etUserName.setEnabled(false);
                    etUserName.setHint("");
                    etPwd.setEnabled(false);
                    etPwd.setHint("");
                    etRegisterNum.setEnabled(false);
                    etRegisterNum.setHint("");
                    etEmail.setEnabled(false);
                    etEmail.setHint("");
                    etAddress.setEnabled(false);
                    etAddress.setHint("");
                    etEmergencyPhone1.setEnabled(false);
                    etEmergencyPhone1.setHint("");
                    etEmergencyPhone2.setEnabled(false);
                    etEmergencyPhone2.setHint("");
                    etEmergencyPhone3.setEnabled(false);
                    etEmergencyPhone3.setHint("");
                }
              //  String r  = tv_fun.getText().toString().trim();
                if(tv_fun.getText().toString().trim().equals("编辑")){
//                    if(!isValid()){
//                        return;
//                    }else {
//
//                    }
                }
                break;
        }
    }


    private boolean isValid()
    {

        String etUserNameString = etUserName.getText().toString().trim();
        if (TextUtils.isEmpty(etUserNameString)) {
            Toast.makeText(this, "请输入姓名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }


//        String etEmailString = etEmail.getText().toString().trim();
//        if (TextUtils.isEmpty(etEmailString)) {
//            Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        String etAddressString = etAddress.getText().toString().trim();
//        if (TextUtils.isEmpty(etAddressString)) {
//            Toast.makeText(this, "请输入地址", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        String etEmergencyName1String = etEmergencyName1.getText().toString().trim();
        if (TextUtils.isEmpty(etEmergencyName1String)) {
            Toast.makeText(this, "请输入紧急联系人姓名1不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        String etEmergencyPhone1String = etEmergencyPhone1.getText().toString().trim();
        if (TextUtils.isEmpty(etEmergencyPhone1String)) {
            Toast.makeText(this, "请输入紧急联系人电话1不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

//        String etEmergencyName2String = etEmergencyName2.getText().toString().trim();
//        if (TextUtils.isEmpty(etEmergencyName2String)) {
//            Toast.makeText(this, "请输入紧急联系人姓名2", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        String etEmergencyPhone2String = etEmergencyPhone2.getText().toString().trim();
//        if (TextUtils.isEmpty(etEmergencyPhone2String)) {
//            Toast.makeText(this, "请输入紧急联系人电话2", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        String etEmergencyName3String = etEmergencyName3.getText().toString().trim();
//        if (TextUtils.isEmpty(etEmergencyName3String)) {
//            Toast.makeText(this, "请输入紧急联系人姓名3", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        String etEmergencyPhone3String = etEmergencyPhone3.getText().toString().trim();
//        if (TextUtils.isEmpty(etEmergencyPhone3String)) {
//            Toast.makeText(this, "请输入紧急联系人电话3", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        return true;
    }

    private void saveUserInfo(){
        if (isEdit) {//编辑状态
            showAlertDialog("提示", "是否保存个人信息？", "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!isValid()){
                        return;
                    }else {

                        mUserInfo.setMemoName(etUserName.getText().toString().trim());
                        mUserInfo.setAddress(etAddress.getText().toString().trim());
                        mUserInfo.setMail(etEmail.getText().toString().trim());
                        mUserInfo.setEmergenceName1(etEmergencyName1.getText().toString().trim());
                        mUserInfo.setEmergencePhone1(etEmergencyPhone1.getText().toString().trim());
                        mUserInfo.setEmergenceName2(etEmergencyName2.getText().toString().trim());
                        mUserInfo.setEmergencePhone2(etEmergencyPhone2.getText().toString().trim());
                        mUserInfo.setEmergenceName3(etEmergencyName3.getText().toString().trim());
                        mUserInfo.setEmergencePhone3(etEmergencyPhone3.getText().toString().trim());

                        SessionUtils.getInstance(getApplicationContext()).saveLoginUserInfo(mUserInfo);
                        //TODO:保存到数据库
                        UserInfoDao userInfoDao  = new UserInfoDao(getApplicationContext());
                       boolean updateSuccess = userInfoDao.updatePersonal(mUserInfo);
                        if(updateSuccess){
                            Toast.makeText(PersponalActivity.this, "数据保存成功!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);//淡入浅出动画
                        }

                    }
                }
            }, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);//淡入浅出动画
                }
            });
        }else {
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);//淡入浅出动画
        }
    }
}
