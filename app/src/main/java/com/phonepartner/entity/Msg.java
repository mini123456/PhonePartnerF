package com.phonepartner.entity;

/**
 * @param
 * @author ldm
 * @description 录音实体类
 * @time 2016/6/25 11:04
 */
 public  class Msg {
    float time;//时间长度
    String content;
   public String  filePath;//文件路径
    int type;
    //---------定义信息类型
    public static final int RECORDER_RECEIVED = 0;//接收语音信息
    public static final int RECORDER_SENT = 1;//发送语音信息
    public static final int MSG_RECEIVED = 2;//接收文字信息
    public static final int MSG_SENT = 3;//发送语音信息

    public Msg(float time, String content, String filePath, int type) {//Msg类中的元素
        super();
        this.time = time;//录音时长
        this.content = content;//文本信息
        this.filePath = filePath;//录音文件路径
        this.type = type;//信息类型
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getType() {
        return type;
    }

}