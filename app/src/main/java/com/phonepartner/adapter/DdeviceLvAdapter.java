package com.phonepartner.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.phonepartner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwj on 2017/6/22.
 */

public class DdeviceLvAdapter  extends BaseAdapter {

    private List<BluetoothDevice> mBleArray;
    private ViewHolder viewHolder;
    private Context mContext;

    public DdeviceLvAdapter(Context mContext) {
        mBleArray = new ArrayList<BluetoothDevice>();
        this.mContext = mContext;
    }

    public void addDevice(BluetoothDevice device) {
        if (!mBleArray.contains(device)) {
            mBleArray.add(device);
        }
    }

    public void clear() {
        mBleArray.clear();
    }

    @Override
    public int getCount(){
//    {if(mBleArray != null && mBleArray.size()>0){
        return mBleArray.size();
//    }
       // return  0 ;
    }

    @Override
    public BluetoothDevice getItem(int position) {

        return mBleArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_device_list, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_devName = (TextView) convertView
                    .findViewById(R.id.tvDeviceName);
            viewHolder.tv_devAddress = (TextView) convertView
                    .findViewById(R.id.tvChangeDevice);
            convertView.setTag(viewHolder);
        } else {
            convertView.getTag();
        }
        // add-Parameters
        if(mBleArray != null && mBleArray.size()>0){
            BluetoothDevice device = mBleArray.get(position);
            String devName = device.getName();
            if (devName != null && devName.length() > 0) {
                viewHolder.tv_devName.setText(devName);
            } else {
                viewHolder.tv_devName.setText("unknow-device");
            }
            viewHolder.tv_devAddress.setText(device.getAddress());
        }else {
            viewHolder.tv_devName.setText("");
        }

        return convertView;
    }
    class ViewHolder {
        TextView tv_devName, tv_devAddress;
    }

}


