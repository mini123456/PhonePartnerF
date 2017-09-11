package com.phonepartner.frgment;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.phonepartner.R;
import com.phonepartner.attributes.SampleGattAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothLeService extends Service {
    private final static String TAG = "BluetoothLeService";
    public static final int CHA_MARK = 0x00000000;//文字信息
    public static final int VOICE_MARK = 0x00000001;//语音信息

    public static final int NO_LAST_CHA = 0x00000000;
    public static final int LAST_CHA = 0x00000010;//最后一个包
    public static final int MIDDLE_CHA = 0x00000001 << 3;//不是最后一条信息
    public static final int INFO_MARK = 0x11110000;//信息编码

    public final static int B = 19;
    public final static int mSendMaxPackageLength = 16;//用户数据每包最大长度
    public final static int mReceMaxPackageLength = 20;


    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    public final static String EXTRA_UUID = "com.example.bluetooth.le.uuid_DATA";
    public final static String EXTRA_NAME = "com.example.bluetooth.le.name_DATA";
    public final static String EXTRA_PASSWORD = "com.example.bluetooth.le.password_DATA";
    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
//    public static String Service_uuid = "0000ffe0-0000-1000-8000-00805f9b34fb";
//    public static String Characteristic_uuid_TX = "0000ffe1-0000-1000-8000-00805f9b34fb";
//    public static String Characteristic_uuid_FUNCTION = "0000ffe1-0000-1000-8000-00805f9b34fb";

    public static String Service_uuid = "00001234-0000-1000-8000-00805f9b34fb";// 00001234-0000-1000-8000-00805f9b34fb
    public static String Characteristic_uuid_TX = "00001236-0000-1000-8000-00805f9b34fb";//1236,可读可通知
    public static String Characteristic_uuid_FUNCTION = "00001235-0000-1000-8000-00805f9b34fb";//1235可写

    public static int n;
    public final static int TIME = 100;//发送包间隔时间为15ms，即1333byte/s
    byte[] tempPackets = new byte[10 * 1024];                                    //临时字节数组，缓存总的数据包信息

    byte[] WriteBytes = new byte[10 * 1024];
    byte[] resultData = new byte[10 * 1024];//临时字节数组，缓存接收到的数据
    byte[] packetsID = new byte[1024];        //临时字节数组，缓存接收到的PacketID
    boolean isReSend = false;                //标识，是否需要重发的PacketID

    public static int needToPackageNum;
    public static int getActualPacketNum = 0;           //实际获取到的数据包数量
    public static int getPacketsNum;                    //应接收的数据包数量
    public static int getMessageID;

    public void sendDataByType(int type, byte[] data) {
        if (data == null || data.length == 0) {            //发送数据为空，返回
            return;
        }

        int userDataLength;
        userDataLength = data.length;
        needToPackageNum = (userDataLength % mSendMaxPackageLength) == 0 ? userDataLength / mSendMaxPackageLength : userDataLength / mSendMaxPackageLength + 1;
        int presentPacketNum = 0;                            //用于截取用户数据
        byte[] sendData = new byte[20];                    //临时字节数组，以每个包20字节缓存数据发送
        byte[] tempData = new byte[20];                                    //临时字节数组，缓存组织好的一个20字节头包或者用户数据包

        MergerMessagePacket.II_MessageType = type;
        MergerMessagePacket.II_PacketsNum = needToPackageNum + 1;//needToPackageNum为用户数据包数量，+1为加上头包后的数量
        tempData = MergerMessagePacket.getMessageHeadPacket(needToPackageNum);//组织包头数据
        Log.e(TAG, "==============MergerMessagePacket.II_PacketID is  " + Integer.toString(MergerMessagePacket.II_PacketID));
        System.arraycopy(tempData, 0, tempPackets, 0, 20);//把构建好的头包数据放到临时字节数组中
        BluetoothGattCharacteristic gg;
        gg = mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));

        while (needToPackageNum != 0) {                    //待组包的数量等于输入消息字节数/16的得数+1
            if (needToPackageNum != 1) {                    //不为最后一个待组包数据
                //中间数据包组织
                MergerMessagePacket.II_PacketID += 1;//自加1
                Log.e(TAG, "==============MergerMessagePacket.II_PacketID is  " + Integer.toString(MergerMessagePacket.II_PacketID));
                System.arraycopy(data, (presentPacketNum * mSendMaxPackageLength), tempData, 0, mSendMaxPackageLength);
                tempData = MergerMessagePacket.getSendMessagePacket(tempData, 1);        //'1',表示还有后续包标识
                needToPackageNum -= 1;
                presentPacketNum += 1;
                //tempPackets = byteMerger(tempPackets, tempData);//把构建好的数据包放到临时字节数组中
                System.arraycopy(tempData, 0, tempPackets, presentPacketNum *20, 20);
            }
            else if(needToPackageNum == 1){
                //尾部数据包组织
                tempData = new byte[20];
                MergerMessagePacket.II_PacketID += 1;//自加1
                Log.e(TAG, "==============MergerMessagePacket.II_PacketID is  " + Integer.toString(MergerMessagePacket.II_PacketID));
                System.arraycopy(data, (presentPacketNum * mSendMaxPackageLength), tempData, 0, userDataLength - presentPacketNum * mSendMaxPackageLength);
                tempData = MergerMessagePacket.getSendMessagePacket(tempData, 0);        //'0',表示无后续包标识
                needToPackageNum -= 1;
                presentPacketNum += 1;
                MergerMessagePacket.II_PacketID = 0;
                //tempPackets = byteMerger(tempPackets, tempData);//把构建好的数据包放到临时字节数组中
                System.arraycopy(tempData, 0, tempPackets, presentPacketNum * 20, 20);
            }

        }
        Log.e(TAG, "======================MergerMessagePacket.II_PacketsNum = " + MergerMessagePacket.II_PacketsNum);
        //数据发送，从临时字节数组中逐一提取20字节发送
        for (int i = 0; i < MergerMessagePacket.II_PacketsNum; i++) {
            Log.d(TAG, "==============SendDataBy20  of Packet[" + Integer.toString(i) + "]");
            System.arraycopy(tempPackets, i * 20, sendData, 0, 20);
            gg.setValue(sendData);
            mBluetoothGatt.writeCharacteristic(gg);
            try {
                Thread.sleep(TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //消息编号，表达整数范围0~14，采用对14进行循环自减使用
        MergerMessagePacket.II_MessageID -= 1;//自减1
        if (MergerMessagePacket.II_MessageID == 0) {        //循环自减
            MergerMessagePacket.II_MessageID = 14;
        }
    }

    //==============组织丢包后重发数据包=================
    public void SendReSendPacket(int ReSendPacketID, int isReSendAll, int AckType) {
        //根据ReSendPacketID，提取临时字节数组tempPackets中相应数据包
        Log.e(TAG, "==================get reSendPacketNumber is " + Integer.toString(ReSendPacketID));
        BluetoothGattCharacteristic gg;
        gg = mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
        byte[] sendData = new byte[20];                    //临时字节数组，以每个包20字节缓存数据发送
        int needToSendPacketsNum;
        needToSendPacketsNum = MergerMessagePacket.II_PacketsNum;
        if (isReSendAll == 0) {
            for (int i = 0; i < needToSendPacketsNum; i++) {
                System.arraycopy(tempPackets, i * 20, sendData, 0, 20);
                gg.setValue(sendData);
                mBluetoothGatt.writeCharacteristic(gg);
                try {
                    Thread.sleep(TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (isReSendAll == 1) {
            int needToReSendPacketID;
            for (int posPacketID = 2; posPacketID < needToSendPacketsNum + 1; ) {
                needToReSendPacketID = ((tempPackets[posPacketID] & 3) << 8) | tempPackets[posPacketID + 1];
                if (ReSendPacketID == needToReSendPacketID) {
                    Log.e(TAG, "====================get the needToReSendPacketID is " + Integer.toString(needToReSendPacketID));
                    System.arraycopy(tempPackets, posPacketID - 2, sendData, 0, 20);
                    if (AckType == 0) {
                        sendData[0] &= 63;     //如果数据包是需要重发的最后一包，修改HeaderIndicator,MorePacketToSend 为0;
                    }
                    gg.setValue(sendData);
                    mBluetoothGatt.writeCharacteristic(gg);
                    try {
                        Thread.sleep(TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                posPacketID += 20;
            }
        }
    }

    //=================重发命令数据包=================
    public void SendReSendCommand(byte[] ReSendPacket) {
        Log.e(TAG, " ================== SendReSendCommand()=============");
        BluetoothGattCharacteristic gg;
        gg = mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
        //发送ErrorAck
        gg.setValue(ReSendPacket);
        mBluetoothGatt.writeCharacteristic(gg);
        try {
            Thread.sleep(TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void enable_noty() {
        BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(Service_uuid));
        BluetoothGattCharacteristic ale = service.getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
        boolean set = mBluetoothGatt.setCharacteristicNotification(ale, true);
        Log.d(TAG, " setnotification = " + set);
        BluetoothGattDescriptor dsc = ale.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        byte[] bytes = {0};
        dsc.setValue(bytes);
        mBluetoothGatt.writeDescriptor(dsc);
    }

    public void enable_JDY_ble(boolean p) {

        try {
            if (p) {
                BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(Service_uuid));
                BluetoothGattCharacteristic ale;// =service.getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
                switch (0) {
                    case 0://0xFFE1 //透传
                    {
                        ale = service.getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
                    }
                    break;
                    case 1:// 0xFFE2 //iBeacon_UUID
                    {
                        ale = service.getCharacteristic(UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb"));
                    }
                    break;
                    case 2://0xFFE3 //iBeacon_Major
                    {
                        ale = service.getCharacteristic(UUID.fromString("0000ffe3-0000-1000-8000-00805f9b34fb"));
                    }
                    break;
                    case 3://0xFFE4 //iBeacon_Minor
                    {
                        ale = service.getCharacteristic(UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb"));
                    }
                    break;
                    case 4://0xFFE5 //广播间隔
                    {
                        ale = service.getCharacteristic(UUID.fromString("0000ffe5-0000-1000-8000-00805f9b34fb"));
                    }
                    break;
                    case 5://0xFFE6 //密码功能
                    {
                        ale = service.getCharacteristic(UUID.fromString("0000ffe6-0000-1000-8000-00805f9b34fb"));
                    }
                    break;
                    case 6:// 0xFFE7 //设备名功能
                    {
                        ale = service.getCharacteristic(UUID.fromString("0000ffe7-0000-1000-8000-00805f9b34fb"));
                    }
                    break;
                    case 7:// 0xFFE8 //IO输出功能功能
                    {
                        ale = service.getCharacteristic(UUID.fromString("0000ffe8-0000-1000-8000-00805f9b34fb"));
                    }
                    break;
                    case 8:// 0xFFE9 //PWM功能
                    {
                        ale = service.getCharacteristic(UUID.fromString("0000ffe9-0000-1000-8000-00805f9b34fb"));
                    }
                    break;
                    case 9:// 0xFFEA //复位模块
                    {
                        ale = service.getCharacteristic(UUID.fromString("0000ffea-0000-1000-8000-00805f9b34fb"));
                    }
                    break;
                    case 10:// 0xFFEB //发射功率
                    {
                        ale = service.getCharacteristic(UUID.fromString("0000ffeb-0000-1000-8000-00805f9b34fb"));
                    }
                    break;
                    case 11:// 0xFFEC //RTC功能
                    {
                        ale = service.getCharacteristic(UUID.fromString("0000ffec-0000-1000-8000-00805f9b34fb"));
                    }
                    break;
                    default:
                        ale = service.getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
                        break;
                }
                //17.07.05 modified by xst，手动初始特征通知函数，解决蓝牙onCharacteristicChange不回调问题
                /*boolean set = mBluetoothGatt.setCharacteristicNotification(ale, true);
                if (set){
                    setCharacteristicNotification(ale, true);
                    Log.d(TAG, "=============onTest.setCharaed==============");
                }*/
                setCharacteristicNotification(ale, true);
                enableNotifications(ale);
                // boolean set = mBluetoothGatt.setCharacteristicNotification(ale, true);
                // Log.d(TAG, " setnotification = " + set);
                BluetoothGattDescriptor dsc = ale.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                byte[] bytes = {0};
                dsc.setValue(bytes);
                boolean success = mBluetoothGatt.writeDescriptor(dsc);
                Log.d(TAG, "writing enabledescriptor if (p):" + success);
            } else {
                BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString("49535343-fe7d-4ae5-8fa9-9fafd205e455"));
                BluetoothGattCharacteristic ale = service.getCharacteristic(UUID.fromString(Service_uuid));
                boolean set = mBluetoothGatt.setCharacteristicNotification(ale, false);
                Log.d(TAG, " setnotification = " + set);
                BluetoothGattDescriptor dsc = ale.getDescriptor(UUID.fromString(Characteristic_uuid_TX));
                byte[] bytes = {0};
                dsc.setValue(bytes);
                boolean success = mBluetoothGatt.writeDescriptor(dsc);
                Log.d(TAG, "writing enabledescriptor else (p):" + success);
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
    }

    //================================17.07.03 added by xst=========================================
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    /**
     * Enables notifications on given characteristic
     *
     * @return true is the request has been sent, false if one of the arguments was <code>null</code> or the characteristic does not have the CCCD.
     */
    protected final boolean enableNotifications(final BluetoothGattCharacteristic characteristic) {
        Log.d(TAG, "===============onTest.enableNotifications============");
        final BluetoothGatt gatt = mBluetoothGatt;
        if (gatt == null || characteristic == null)
            return false;

        // Check characteristic property
        final int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0)
            return false;

        Log.d("BLE", "gatt.setCharacteristicNotification(" + characteristic.getUuid() + ", true)");
        gatt.setCharacteristicNotification(characteristic, true);
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            Log.v("BLE", "Enabling notifications for " + characteristic.getUuid());
            Log.d("BLE", "gatt.writeDescriptor(" + CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID + ", value=0x01-00)");
            return gatt.writeDescriptor(descriptor);
        }
        return false;
    }

    public void read_uuid() {
        String txt = "AAE50111";
        WriteBytes = txt.toString().getBytes();

        BluetoothGattCharacteristic gg;
        gg = mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
        //byte t[]={51,1,2};
        gg.setValue(WriteBytes);
        mBluetoothGatt.writeCharacteristic(gg);
    }

    public Boolean set_uuid(String txt) {//uuid
        if (txt.length() == 36) {
            String v1 = "", v2 = "", v3 = "", v4 = "";
            v1 = txt.substring(8, 9);
            v2 = txt.substring(13, 14);
            v3 = txt.substring(18, 19);
            v4 = txt.substring(23, 24);
            if (v1.equals("-") && v2.equals("-") && v3.equals("-") && v4.equals("-")) {
                txt = txt.replace("-", "");
                txt = "AAF1" + txt;
                WriteBytes = txt.toString().getBytes();
                BluetoothGattCharacteristic gg;
                gg = mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
                gg.setValue(WriteBytes);
                mBluetoothGatt.writeCharacteristic(gg);
                return true;
            } else {
                //Toast toast = Toast.makeText(DeviceControlActivity.this, "提示！UUID输入格式不对", Toast.LENGTH_SHORT);
                //toast.show();
                return false;
            }
        } else {
            //Toast toast = Toast.makeText(DeviceControlActivity.this, "提示！UUID输入不对", Toast.LENGTH_SHORT);
            //toast.show();
            return false;
        }

    }

    public void set_func(String mayjor0, String minor0) {//mayjor minor
        String mayjor = "", minor = "";
        String sss = mayjor0;
        int i = Integer.valueOf(sss).intValue();
        String vs = String.format("%02x", i);
        if (vs.length() == 2) vs = "00" + vs;
        else if (vs.length() == 3) vs = "0" + vs;
        mayjor = vs;
        sss = minor0;
        i = Integer.valueOf(sss).intValue();
        vs = String.format("%02x", i);
        if (vs.length() == 2) vs = "00" + vs;
        else if (vs.length() == 3) vs = "0" + vs;
        minor = vs;
        String txt = "AAF21AFF4C000215" + mayjor + minor + "CD00";
        WriteBytes = txt.toString().getBytes();
        BluetoothGattCharacteristic gg;
        gg = mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
        //byte t[]={51,1,2};
        gg.setValue(WriteBytes);
        mBluetoothGatt.writeCharacteristic(gg);
    }

    public void uuid_1001_send_data(String value) {
        WriteBytes = value.toString().getBytes();
        BluetoothGattCharacteristic gg;
        gg = mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
        gg.setValue(WriteBytes);
        mBluetoothGatt.writeCharacteristic(gg);
    }

    public void set_dev_name(String name) {
        int length = name.length();
        String len = String.valueOf(length);
        int ilen = len.length();
        String he = String.format("%02X", length);
        name = name;
        String txt = "AAE4" + he + name;
        WriteBytes = txt.toString().getBytes();
        BluetoothGattCharacteristic gg;
        gg = mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
        gg.setValue(WriteBytes);
        mBluetoothGatt.writeCharacteristic(gg);
    }

    public void out_io_set(String value) {
        WriteBytes = value.toString().getBytes();
        BluetoothGattCharacteristic gg;
        gg = mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
        gg.setValue(WriteBytes);
        mBluetoothGatt.writeCharacteristic(gg);
    }


    public void set_password(String value) {
        String st1 = value;
        st1 = "AAE2" + st1;
        WriteBytes = st1.toString().getBytes();
        BluetoothGattCharacteristic gg;
        gg = mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
        gg.setValue(WriteBytes);
        mBluetoothGatt.writeCharacteristic(gg);
    }

    public void set_adv_time(int i) {
        if (i == 0) {
            out_io_set("AA0900");
        } else if (i == 0) {
            out_io_set("AA0901");
        } else if (i == 0) {
            out_io_set("AA0902");
        } else if (i == 0) {
            out_io_set("AA0903");
        } else {
            out_io_set("AA0901");
        }
    }

    public void password_value(String value) {
        //String txt="AAE2"+he+name;
        //WriteBytes= hex2byte(value.toString().getBytes());
        String txt = "AAE2";
        value = value;
        txt = txt + value;
        WriteBytes = txt.toString().getBytes();
        BluetoothGattCharacteristic gg;
        gg = mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
        gg.setValue(WriteBytes);
        mBluetoothGatt.writeCharacteristic(gg);
    }

    public void password_enable(boolean p) {
        String g_pass = "";
        if (p) {
            g_pass = "AAE101";
        } else {
            g_pass = "AAE100";
        }
        WriteBytes = g_pass.toString().getBytes();
        BluetoothGattCharacteristic gg;
        gg = mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
        gg.setValue(WriteBytes);
        mBluetoothGatt.writeCharacteristic(gg);
    }

    public void userkey(String key) {
        String g_pass = "AA20";
        key = key;
        g_pass += key;
        WriteBytes = g_pass.toString().getBytes();
        BluetoothGattCharacteristic gg;
        gg = mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
        gg.setValue(WriteBytes);
        mBluetoothGatt.writeCharacteristic(gg);
    }

    public int get_connected_status(List<BluetoothGattService> gattServices) {
        final String LIST_NAME1 = "NAME";
        final String LIST_UUID1 = "UUID";
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
        //mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
        int count_char = 0;
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME1, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID1, uuid);
            gattServiceData.add(currentServiceData);
            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME1, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID1, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
                count_char++;
            }
            //mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
        return count_char;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                Log.d(TAG, "onCharacteristicWrite: " + characteristic.getValue().toString());
                Log.d(TAG, "onCharacteristicWrite: " + true);
                //write成功（发送值成功）
            } else if (status == BluetoothGatt.GATT_FAILURE) {
                Log.d(TAG, "onCharacteristicWrite: " + false);
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                final byte[] data = characteristic.getValue();
                Log.e(TAG, String.format("onCharacteristicRead==", data));

                for (int i = 0; i < data.length; i++) {
                    Log.e(TAG, String.format(" onCharacteristicRead data[" + i + "]=", data[i]));

                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.e(TAG, String.format("================onCharacteristicChanged==============="));
//            final byte[] data = characteristic.getValue();
//            for(int i=0;i<data.length;i++){
//                Log.d(TAG, String.format(" onCharacteristicChanged data["+i+"]="+Integer.toHexString(data[i])));
//
//            }
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {//心率配置文件
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            //以下是接收函数
            final byte[] receiveData = characteristic.getValue();    //接收到的数据
            int dataLength = receiveData.length;
            if (dataLength == 1) {
                Log.e(TAG, " ======================the dataLength = 1");
            }
            int getUserDataHeaderIndicator;
            int getMorePacketsToSend;
            int getPacketID;
            int getMessageType;
            int getAckType;
            byte[] getPacketHeader = new byte[4];               //临时字节数组，缓存数据包包头
            byte[] II_PacketID = new byte[1];
            getActualPacketNum += 1;//自加1
            System.arraycopy(receiveData, 0, getPacketHeader, 0, 4);//获取数据包头
            getUserDataHeaderIndicator = (getPacketHeader[0] & 128) >> 7;
            getMorePacketsToSend = (getPacketHeader[0] & 64) >> 6;
            Log.e(TAG, "============the getMorePacketsToSend is " + Integer.toString(getMorePacketsToSend) + " and the getUserDataHeaderIndicator is  " + Integer.toString(getUserDataHeaderIndicator));
            II_PacketID[0] = (byte) (((getPacketHeader[2] & 3) << 8) | (getPacketHeader[3] & 255));
            if (getUserDataHeaderIndicator == 1 && getMorePacketsToSend == 1) {//如果是第一个包并且有后续包
                //头包接收
                System.arraycopy(receiveData, 4, MergerMessagePacket.II_OriginatingAddress, 0, 8);
                System.arraycopy(receiveData, 12, MergerMessagePacket.II_DestinationAddress, 0, 8);
                getPacketsNum = ((getPacketHeader[1] & 15) << 6) | ((getPacketHeader[2] & 252) >> 2);
                getMessageType = (getPacketHeader[0] & 48) >> 4;
                resultData[0] = (byte) getMessageType;//添加数据类型到临时数组
                System.arraycopy(II_PacketID, 0, packetsID, getActualPacketNum - 1, 1);              //保存II_PacketID到临时字节数组PacketID
            } else if (getUserDataHeaderIndicator == 1 && getMorePacketsToSend == 0) {//如果是第一个包并且无后续包
                //命令数据包接收
                getAckType = (getPacketHeader[0] & 48) >> 4;
                if (getAckType != 3) {                        //AckType，2bit，11表示SuccessAck
                    Log.e(TAG, "=================get AckErrorPacket, the AckType is ==============" + Integer.toString(getAckType));
                    //重发消息包接收，提取相关数据内容，发送需要重发的数据包
                    int ReSendPacketID;
                    int ReSendPacketsNum;
                    ReSendPacketsNum = ((getPacketHeader[1] & 255) << 2) | ((getPacketHeader[2] & 192) >> 6);
                    if (ReSendPacketsNum == 0) {
                        //参数1：ReSendPacketID
                        //参数2：‘0’重新发送全部数据包；‘1’按照需要重发的数据包ID发送数据包
                        //参数3：‘1’有后续数据包需要重传；‘0’无后续数据包需要重传
                        SendReSendPacket(0, 0, 1);
                    } else {
                        ReSendPacketID = ((getPacketHeader[2] & 63) << 4) | (getPacketHeader[3] >> 4);
                        if (getAckType == 1) {
                            SendReSendPacket(ReSendPacketID, 1, 0);
                        } else {
                            SendReSendPacket(ReSendPacketID, 1, 1);
                        }
                    }
                } else if (getAckType == 3) {
                    resultData = new byte[10 * 1024];
                    packetsID = new byte[1024];
                    getActualPacketNum = 0;
                    Log.e(TAG, "=================get AckErrorPacket, the AckType is ==============" + Integer.toString(getAckType));
                }
            } else if (getUserDataHeaderIndicator == 0 && getMorePacketsToSend == 1) {//如果不是头包且有后续包
                //用户数据包接收
                getMessageID = (getPacketHeader[1] & 240) >> 4;
                getPacketID = ((getPacketHeader[2] & 3) << 8) | (getPacketHeader[3] & 255);
                System.arraycopy(receiveData, 4, resultData, (getPacketID - 1) * 16 + 1, dataLength - 4);//按照PacketID顺序保存数据到临时字节数组
                Log.e(TAG, "============the getActualPacketNum is " + Integer.toString(getActualPacketNum) + " and the PacketID is  " + Integer.toString(getPacketID));
                System.arraycopy(II_PacketID, 0, packetsID, getActualPacketNum - 1, 1);              //保存II_PacketID到临时字节数组PacketID
            } else {//如果不是头包且无后续包
                // 用户尾部数据包接收
                getPacketID = ((getPacketHeader[2] & 3) << 8) | (getPacketHeader[3] & 255);
                System.arraycopy(receiveData, 4, resultData, (getPacketID - 1) * 16 + 1, dataLength - 4);//按照PacketID顺序保存数据到临时字节数组
                System.arraycopy(II_PacketID, 0, packetsID, getActualPacketNum - 1, 1);              //保存II_PacketID到临时字节数组PacketID
                Log.e(TAG, "============the getActualPacketNum is " + Integer.toString(getActualPacketNum) + " and the PacketID is  " + Integer.toString(getPacketID));
                Log.e(TAG, "============the MergerMessagePacket.II_PacketID is " + Integer.toString(MergerMessagePacket.II_PacketID));
                Log.e(TAG, "============ the total getActualPacketNum =" + Integer.toString(getActualPacketNum));
                Log.e(TAG, "============ the getPacketsNum =" + Integer.toString(getPacketsNum));
                if (getActualPacketNum == getPacketsNum) {
//                    for(int i=0;i<1024;i++){
//                        Log.d(TAG, String.format(" receiveData["+i+"]="+Integer.toHexString(resultData[i])));
//
//                    }
                    intent.putExtra(EXTRA_DATA, resultData);
                    sendBroadcast(intent);

                    resultData = new byte[10 * 1024];
                    packetsID = new byte[1024];
                    getActualPacketNum = 0;
                    MergerMessagePacket.mBluetoothLeService = this;
                    //AccessAck 组织发送
                    //参数1：PacketID
                    //参数2：AckType，2bit，11表示SuccessAck
                    //参数3：ReSendMessageID
                    //参数4：ReSendPacketsNum
                    MergerMessagePacket.getReSendPacket((byte) 0, 3, 0, 1);
                }
                else {
                    //应接收内容包数与实际接收到消息内容包数不等，则筛选出丢包的PacketID，并构建ERRORACK包告诉发送方要求重发
                    int ReSendMessageID;
                    int ReSendPacketsNum = 0;
                    int EndSearchReSend = 0;//判断是否还有后续重发包
                    int AckType;
                    ReSendMessageID = getMessageID;
                    ReSendPacketsNum = getPacketsNum - getActualPacketNum;
                    resultData = new byte[10 * 1024];
                    packetsID = new byte[1024];
                    getActualPacketNum = 0;
//                    if (ReSendPacketsNum < 0) {      //出现接收异常，初始参数
//                        Log.w(TAG, "===================unexpected error, the total getActualPacketNum =" + Integer.toString(getActualPacketNum) + ", and the getPacketsNum =" + Integer.toString(getPacketsNum));
//                        resultData = new byte[10 * 1024];
//                        packetsID = new byte[1024];
//                        getActualPacketNum = 0;
//                    } else {
//                        EndSearchReSend = ReSendPacketsNum;
//                        byte[] arr = new byte[getPacketsNum];
//                        for (int i = 0; i < getPacketsNum; i++) {
//                            arr[i] = (byte) i;
//                        }
//                        for (int i = 0; i < packetsID.length; i++) {
//                            for (int j = 0; j < arr.length; j++) {
//                                if (arr[j] == packetsID[i]) {
//                                    isReSend = false;
//                                    break;
//                                }
//                                isReSend = true;
//                            }
//                            if (isReSend) {
//                                Log.e(TAG, "================the reSendPacket Number is" + packetsID[i]);
//                                EndSearchReSend -= 1;
//                                if (EndSearchReSend == 0) {
//                                    AckType = 1;    //AckType; 10表示有后续重发包，01表示没有后续重发包）
//                                } else {
//                                    AckType = 2;
//                                }
//                                MergerMessagePacket.mBluetoothLeService = this;
//                                //重传数据包组织发送
//                                //packetsID[i]：参数1：需要重传的数据包编号
//                                //AckType：参数2：‘2’表示还有后续重传包，‘1’表示无后续包重传包
//                                //ReSendMessageID：参数3：需要重传的数据包编号
//                                //ReSendPacketsNum：参数4：需要重传的数据包总数
//                                MergerMessagePacket.getReSendPacket(packetsID[i], AckType, ReSendMessageID, ReSendPacketsNum);
//                            }
//                        }

//                    }
                }
            }
        }
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
//        if (mBluetoothGatt != null){
//            Log.d(TAG, "============== the mBluetoothGatt is not null");
//            mBluetoothGatt.disconnect();
//            mBluetoothGatt.close();
//            Log.d(TAG, "============== the mBluetoothGatt is now null");
//        }
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        // Previously connected device.  Try to reconnect.
//        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
//                && mBluetoothGatt != null) {
//            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
//            if (mBluetoothGatt.connect()) {
//                mConnectionState = STATE_CONNECTING;
//                return true;
//            } else {
//                return false;
//            }
//        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        mBluetoothGatt.disconnect();
    }

    public boolean isconnect() {


        return mBluetoothGatt.connect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }


}