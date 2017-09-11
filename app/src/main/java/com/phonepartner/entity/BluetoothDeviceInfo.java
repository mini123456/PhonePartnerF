package com.phonepartner.entity;

import android.bluetooth.BluetoothDevice;

/**
 * Created by cwj on 2017/7/6.
 */

public class BluetoothDeviceInfo {
    private BluetoothDevice bluetoothDevice;
    private boolean isSelect;

    public BluetoothDeviceInfo(BluetoothDevice bluetoothDevice, boolean isSelect) {
        this.bluetoothDevice = bluetoothDevice;
        this.isSelect = isSelect;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
