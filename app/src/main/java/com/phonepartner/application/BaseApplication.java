package com.phonepartner.application;

import android.app.Application;

/**
 * Created by xs on 2017/7/13.
 */

public class BaseApplication extends Application {

    public static BaseApplication mApplication=null;
    private boolean connect_status_bit=false;

    public boolean isConnect_status_bit() {
        return connect_status_bit;
    }

    public void setConnect_status_bit(boolean connect_status_bit) {
        this.connect_status_bit = connect_status_bit;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication=this;
    }


    public static BaseApplication getInstance(){

        if(null==mApplication){
            mApplication=new BaseApplication();
        }

        return mApplication;

    }

}
