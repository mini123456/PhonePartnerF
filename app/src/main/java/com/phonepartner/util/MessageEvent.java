package com.phonepartner.util;

import com.phonepartner.entity.UserInfo;

/**
 * Created by cwj on 2017/6/20.
 */

public class MessageEvent {
    private String mesage;
    private int role;//队友或者自己
    private UserInfo userInfo;//个人信息

    public MessageEvent() {
        this.mesage = mesage;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getMesage() {
        return mesage;
    }

    public void setMesage(String mesage) {
        this.mesage = mesage;
    }
}
