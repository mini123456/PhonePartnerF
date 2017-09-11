package com.phonepartner.util;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.phonepartner.entity.UserInfo;


/**
 * Created by CWJ on 2016/12/19.
 */

public class SessionUtils {
    private static SessionUtils instance;
    private SharedPreferencesUtils sharedPreferencesUtils;
    private static final String xmlFileName="phonePartner";
    public static SessionUtils getInstance(Context context) {
        if (instance == null) {
            instance = new SessionUtils();
        }
        if (instance.sharedPreferencesUtils == null) {
            instance.sharedPreferencesUtils = SharedPreferencesUtils.getInstance(context);
        }
        return instance;
    }


    public void saveLoginUserInfo(UserInfo userInfo){
        if(userInfo != null){
            Gson gson = new Gson();
           String user =  gson.toJson(userInfo);
            instance.sharedPreferencesUtils.setSP(xmlFileName, "userInfo", user);
        }
    }

    public UserInfo  getLoginUserInfo(){
        String  user = instance.sharedPreferencesUtils.getStringSP(xmlFileName, "userInfo");
        if(user != null  && !TextUtils.isEmpty(user)){
            Gson gson = new Gson();
            UserInfo userInfo = gson.fromJson(user,UserInfo.class);
            return userInfo;
        }
        return  null ;
    }
    public void saveMemberUserInfo(UserInfo userInfo){
        if(userInfo != null){
            Gson gson = new Gson();
            String user =  gson.toJson(userInfo);
            instance.sharedPreferencesUtils.setSP(xmlFileName, "memberUserInfo", user);
        }
    }

    public UserInfo  getMemberUserInfo(){
        String  user = instance.sharedPreferencesUtils.getStringSP(xmlFileName, "memberUserInfo");
        if(user != null  && !TextUtils.isEmpty(user)){
            Gson gson = new Gson();
            UserInfo userInfo = gson.fromJson(user,UserInfo.class);
            return userInfo;
        }
        return  null ;
    }

    public void saveLoginPhone(String phone){
        if(phone != null){
            instance.sharedPreferencesUtils.setSP(xmlFileName, "login_phone", phone);
        }

    }
    public String getLoginPhone(){
        String userPhone = instance.sharedPreferencesUtils.getStringSP(xmlFileName, "login_phone");

        if(userPhone !=null && !TextUtils.isEmpty(userPhone)){
            return userPhone ;
        }
        return null ;
    }
    public void saveLoginPwd(String pwd){
        if(pwd != null){
            instance.sharedPreferencesUtils.setSP(xmlFileName, "login_pwd", pwd);
        }

    }

    public String getLoginPwd(){
        String pwd = instance.sharedPreferencesUtils.getStringSP(xmlFileName, "login_pwd");

        if(pwd !=null && !TextUtils.isEmpty(pwd)){
            return pwd ;
        }
        return null ;
    }

    public void saveCheckedId(int checkedId){

        instance.sharedPreferencesUtils.setIntSP(xmlFileName,"check",checkedId);

    }
    public int getCheckedId(){
        int checkId = instance.sharedPreferencesUtils.getSP(xmlFileName,"check");
        return checkId ;
    }
    public void saveLoginState(boolean isLogin){
        instance.sharedPreferencesUtils.setBooleanSP(xmlFileName,"open",isLogin);

    }
    public boolean getLoginState(){
        boolean state = instance.sharedPreferencesUtils.getBooleanSP(xmlFileName,"open");
        return state;
    }

    public void saveAddress(String address){
        if(address != null){
            instance.sharedPreferencesUtils.setSP(xmlFileName, "address", address);
        }
    }

    public String getAddress(){
        String address = instance.sharedPreferencesUtils.getStringSP(xmlFileName, "address");
        if(address !=null && !TextUtils.isEmpty(address)){
            return address ;
        }
        return null ;
    }
    public void saveDeviceName(String DeviceName){
        if(DeviceName != null){
            instance.sharedPreferencesUtils.setSP(xmlFileName, "DeviceName", DeviceName);
        }
    }

    public String getDeviceName(){
        String DeviceName = instance.sharedPreferencesUtils.getStringSP(xmlFileName, "DeviceName");
        if(DeviceName !=null && !TextUtils.isEmpty(DeviceName)){
            return DeviceName ;
        }
        return null ;
    }
    public void saveDeviceAddress(String DeviceAddress){
        if(DeviceAddress != null){
            instance.sharedPreferencesUtils.setSP(xmlFileName, "DeviceAddress", DeviceAddress);
        }
    }
    public String getDeviceAddress(){
        String DeviceAddress = instance.sharedPreferencesUtils.getStringSP(xmlFileName, "DeviceAddress");
        if(DeviceAddress !=null && !TextUtils.isEmpty(DeviceAddress)){
            return DeviceAddress ;
        }
        return null ;
    }

    public void saveKeyValue(String key, String value){
        instance.sharedPreferencesUtils.setSP(xmlFileName, key,value);
    }

    public String getValue(String key){
        return instance.sharedPreferencesUtils.getStringSP(xmlFileName, key);
    }
}
