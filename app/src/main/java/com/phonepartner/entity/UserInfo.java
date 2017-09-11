package com.phonepartner.entity;

import android.bluetooth.BluetoothDevice;

/**
 * Created by cwj on 2016/10/18 0018.
 */
public class UserInfo{
    private String name;//账号
    private String pwd;//密码
    private int photo;
    private String memoName;//备注名
    private String sex ; //性别
    private String registerAccount ;//注册号
    private String mail ; // 邮箱
    private String address ; //地址
    private String emergenceName1;//紧急联系人1
    private String emergencePhone1 ;//紧急联系人电话1
    private String emergenceName2;//紧急联系人2
    private String emergencePhone2 ;//紧急联系人电话2
    private String emergenceName3;//紧急联系人3
    private String emergencePhone3 ;//紧急联系人电话3
    private String uid;//注册码
    private BluetoothDevice device;//设备

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public UserInfo(String name, String pwd,String uid ,String memoName, String emergenceName1, String emergencePhone1) {
        this.name = name;
        this.pwd = pwd;
        this.memoName = memoName;
        this.emergenceName1 = emergenceName1;
        this.emergencePhone1 = emergencePhone1;
        this.uid = uid;
    }

    public UserInfo(String name, String pwd, String uid) {
        this.uid = uid;
        this.name = name;
        this.pwd = pwd;
    }

    public UserInfo(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }

    public UserInfo(String name, String pwd, int photo) {
        this.name = name;
        this.pwd = pwd;
        this.photo = photo;
    }

    public UserInfo() {
    }


    public String getMemoName() {
        return memoName;
    }

    public void setMemoName(String memoName) {
        this.memoName = memoName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRegisterAccount() {
        return registerAccount;
    }

    public void setRegisterAccount(String registerAccount) {
        this.registerAccount = registerAccount;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergenceName1() {
        return emergenceName1;
    }

    public void setEmergenceName1(String emergenceName1) {
        this.emergenceName1 = emergenceName1;
    }

    public String getEmergencePhone1() {
        return emergencePhone1;
    }

    public void setEmergencePhone1(String emergencePhone1) {
        this.emergencePhone1 = emergencePhone1;
    }

    public String getEmergenceName2() {
        return emergenceName2;
    }

    public void setEmergenceName2(String emergenceName2) {
        this.emergenceName2 = emergenceName2;
    }

    public String getEmergencePhone2() {
        return emergencePhone2;
    }

    public void setEmergencePhone2(String emergencePhone2) {
        this.emergencePhone2 = emergencePhone2;
    }

    public String getEmergenceName3() {
        return emergenceName3;
    }

    public void setEmergenceName3(String emergenceName3) {
        this.emergenceName3 = emergenceName3;
    }

    public String getEmergencePhone3() {
        return emergencePhone3;
    }

    public void setEmergencePhone3(String emergencePhone3) {
        this.emergencePhone3 = emergencePhone3;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }
}

