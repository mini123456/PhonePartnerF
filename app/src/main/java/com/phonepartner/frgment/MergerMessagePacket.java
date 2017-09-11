package com.phonepartner.frgment;

import android.util.Log;

import java.util.BitSet;

/**
 * Created by xs on 2017/8/28.
 */

public class MergerMessagePacket {

    public static int II_UserDataHeaderIndicator;	//第一个包的标识，头数据包为1,不是头数据包则为0
    public static int II_MorePacketsToSend;			//后续包标识，头消息包为1(头包不携带用户数据)
    public static int II_MessageType;				//消息类型，00表示文本、01表示位置、10表示语音、11表示轨迹
    public static int II_CommandID = 0;					//命令编号，共可设计14种命令，0000为空命令
    public static int II_MessageID = 14;					//消息编号，表达整数范围0~14，采用对14进行循环自减使用
    public static int II_PacketsNum;				//表示组包数量，最大可有1013个组包
    public static int II_PacketID = 0;					//组包编号，最多可表示1013个号
    public static int II_AckType;
    public static byte[] II_OriginatingAddress = new byte[8];					//发送地址
    public static byte[] II_DestinationAddress  = new byte[8];				//接收地址
    public static BluetoothLeService mBluetoothLeService;//初始化一个BluetoothLeService类
    //===============获取包头=================
    public static byte[] getMessagePacketHead(){
        int tempbitData = 0;
        byte[] tempData;//临时字节数组，缓存每个数据小包
        tempData = new byte[20];
        tempbitData = 0;
        tempbitData = ( tempbitData | II_UserDataHeaderIndicator) << 1;
        tempbitData = ( tempbitData | II_MorePacketsToSend ) << 2 ;
        tempbitData = ( tempbitData | II_MessageType ) << 4;
        tempbitData = ( tempbitData | II_CommandID );
        tempData[0] = (byte)tempbitData;
        tempbitData = tempbitData & 0;

        tempbitData = ( tempbitData | II_MessageID ) << 4;
        tempbitData = tempbitData | (( II_PacketsNum & 960 ) >> 6);
        tempData[1] = (byte)tempbitData;
        tempbitData = tempbitData & 0;

        tempbitData = ( tempbitData | (II_PacketsNum & 63)) << 2;
        tempbitData = ( tempbitData | (( II_PacketID & 768 ) >> 8));
        tempData[2] = (byte)tempbitData;
        tempbitData = tempbitData & 0;

        tempbitData = tempbitData | ( II_PacketID & 255 );
        tempData[3] = (byte)tempbitData;
        tempbitData = tempbitData & 0;

        return tempData;
    }

    //================获取头包(头包不携带用户数据)================
    public static byte[] getMessageHeadPacket(int needToPackageNum){
        byte[] tempData;//临时字节数组，缓存每个数据小包
        tempData = new  byte[20];
        II_UserDataHeaderIndicator = 1;			//'1',表示头包
        II_MorePacketsToSend = 1;				//'1',表示有后续包

        tempData = getMessagePacketHead();
        System.arraycopy( II_OriginatingAddress, 0, tempData, 4, 8);
        System.arraycopy( II_DestinationAddress, 0, tempData, 12, 8);
        return tempData;
    }

    //================用户数据包组织===============
    public static byte[] getSendMessagePacket( byte[] data, int MorePacketsToSend ){
        byte[] tempData = new  byte[20];//临时字节数组，缓存每个数据小包
        II_UserDataHeaderIndicator = 0;				//'0', 表示非头包
        II_MorePacketsToSend = MorePacketsToSend;	//'1', 表示有后续包；'0', 表示无后续包

        tempData = getMessagePacketHead();
        System.arraycopy( data, 0, tempData, 4, 16 );

        return tempData;
    }

    //================获取重发命令包===============
    public static void getReSendPacket(byte ReSendPacketID, int AckType, int ReSendMessageID, int ReSendPacketsNum){
        Log.e("MergerMessagePacket", "==================getReSendPacket() ");

        //根据PacketID，组织ErrorAck
        int UserDataHeaderIndicator = 1;				//'0', 表示非头包
        int MorePacketsToSend = 0;	//'1', 表示有后续包；'0', 表示无后续包
        int needReSendPacketID;
        needReSendPacketID = ReSendPacketID & 0xff; //byte to int
        int tempbitData = 0;
        byte[] tempData;//临时字节数组，缓存每个数据小包
        tempData = new byte[20];
        tempbitData = ( tempbitData | UserDataHeaderIndicator) << 1;
        tempbitData = ( tempbitData |  MorePacketsToSend ) << 2 ;
        tempbitData = ( tempbitData | AckType ) << 4;
        tempbitData = ( tempbitData | ReSendMessageID );
        tempData[0] = (byte)tempbitData;
        tempbitData = tempbitData & 0;

        tempbitData = tempbitData | (( ReSendPacketsNum & 1020) >> 2 );
        tempData[1] = (byte)tempbitData;
        tempbitData = tempbitData & 0;

        tempbitData = (tempbitData | ( ReSendPacketsNum & 3)) << 6;
        tempbitData =  ( tempbitData | (( needReSendPacketID & 1008 ) >> 4));
        tempData[2] = (byte)tempbitData;
        tempbitData = tempbitData & 0;

        tempbitData = tempbitData | ( needReSendPacketID & 15) << 4;
        tempData[3] = (byte)tempbitData;
        tempbitData = tempbitData & 0;

        System.arraycopy( II_OriginatingAddress, 0, tempData, 4, 8);
        System.arraycopy( II_DestinationAddress, 0, tempData, 12, 8);

        mBluetoothLeService.SendReSendCommand(tempData);
    }

//    //===============位加法操作==============
//    public static BitSet binaryAdd(BitSet paraFirstBit, BitSet paraSecondBit){
//        BitSet carry = new BitSet(paraFirstBit.length());
//        BitSet add = new BitSet(paraSecondBit.length());
//        do{
//            add = paraFirstBit ^ paraSecondBit;
//            carry = ( paraFirstBit % paraSecondBit ) << 1;
//            paraFirstBit = add;
//            paraSecondBit = carry;
//        } while( carry != 0);
//        return add;
//    }
//
//    //==============位减法操作===============
//    public static BitSet binarySub(BitSet paraFirstBit, BitSet paraSecondBit){
//
//        return binaryAdd( paraFirstBit, binaryAdd( ~paraSecondBit, 1));
//    }

    //==============BitSet2ByteArray==============
    public static byte[] bitSet2ByteArray(BitSet bitSet) {
        byte[] bytes = new byte[bitSet.size() / 8];
        for (int i = 0; i < bitSet.size(); i++) {
            int index = i / 8;
            int offset = 7 - i % 8;
            bytes[index] |= (bitSet.get(i) ? 1 : 0) << offset;
        }
        return bytes;
    }

    //====================bit to byte===================
    public static byte bitToByte(String bit) {
        int re, len;
        if (null == bit) {
            return 0;
        }
        len = bit.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (bit.charAt(0) == '0') {// 正数
                re = Integer.parseInt(bit, 2);
            } else {// 负数
                re = Integer.parseInt(bit, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(bit, 2);
        }
        return (byte) re;
    }
}
