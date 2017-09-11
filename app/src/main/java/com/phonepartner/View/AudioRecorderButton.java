package com.phonepartner.View;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.phonepartner.R;
import com.phonepartner.activity.MainActivity;
import com.phonepartner.manager.AudioManager;
import com.phonepartner.manager.DialogManager;

import java.io.IOException;


/**
 * @param
 * @author ldm
 * @description 自定义Button
 * @time 2016/6/25 9:26
 */
public class AudioRecorderButton extends Button implements AudioManager.AudioStateListener{
    //发送状态
    private static final int STATE_LOAD=4;
    // 按钮正常状态（默认状态）
    private static final int STATE_NORMAL = 1;
    //正在录音状态
    private static final int STATE_RECORDING = 2;
    //录音取消状态
    private static final int STATE_CANCEL = 3;
    //记录当前状态
    private int mCurrentState = STATE_NORMAL;
    //是否开始录音标志
    private boolean isRecording = false;
    //判断在Button上滑动距离，以判断 是否取消
    private static final int DISTANCE_Y_CANCEL = 50;
    //对话框管理工具类
    private DialogManager mDialogManager;
    //录音管理工具类
    private AudioManager mAudioManager;
    //记录录音时间，（在reset()中置空） 单位为毫秒
    private float mTime;
    // 是否触发longClick
    private boolean mReady;
    //录音准备
    private static final int MSG_AUDIO_PREPARED = 0x110;
    //音量发生改变
    private static final int MSG_VOICE_CHANGED = 0x111;
    //取消提示对话框
    private static final int MSG_DIALOG_DIMISS = 0x112;
    //录音倒计时结束
    private static final int MSG_COUNT_DOWN_DONE = 0x113;

    /**
     * @description 录音完成后的回调
     * @author ldm
     * @time  2016/6/25 11:18
     * @param
     */
    public interface AudioFinishRecorderCallBack {
        void onFinish(float seconds, String filePath) throws IOException;
    }

    private AudioFinishRecorderCallBack finishRecorderCallBack;

    public void setFinishRecorderCallBack(AudioFinishRecorderCallBack listener) {
        this.finishRecorderCallBack = listener;
    }

    //构造方法
    public AudioRecorderButton(Context context) {
        super(context, null);
        // TODO Auto-generated constructor stub
//        this(context, null);
//        mDialogManager = new DialogManager(context);
//        //录音文件存放地址
//        String dir = null;
//            dir = Environment.getExternalStorageDirectory()+ "/ldm_voice";
//
//        mAudioManager = AudioManager.getInstance(dir);
//        mAudioManager.setOnAudioStateListener(new AudioManager.AudioStateListener() {
//            public void wellPrepared() {
//                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
//            }
//        });
//
//        // 由于这个类是button所以在构造方法中添加监听事件
//        setOnLongClickListener(new OnLongClickListener() {
//
//            public boolean onLongClick(View v) {
//                mReady = true;
//                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);//必须添加这句
//                mAudioManager.prepareAudio();
//
//                return true;
//            }
//        });
    }
    //构造方法
    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(getContext());
        //录音文件存放地址
        String dir = Environment.getExternalStorageDirectory() + "/ldm_voice";
        mAudioManager = AudioManager.getInstance(dir);
//        mAudioManager.setOnAudioStateListener(new AudioManager.AudioStateListener() {
//            public void wellPrepared() {
//                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
//            }
//        });
        mAudioManager.setOnAudioStateListener(this);

        // 由于这个类是button所以在构造方法中添加监听事件
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                MainActivity.dialogManager.showLoad();
                //真正显示在audio end prepare后
//                mHandler.sendEmptyMessage(STATE_LOAD);
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    /**
     * @param
     * @description 处理Button的OnTouchEvent事件
     * @author ldm
     * @time 2016/6/25 9:35
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取TouchEvent状态
        int action = event.getAction();
        // 获得x轴坐标
        int x = (int) event.getX();
        // 获得y轴坐标
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN://手指按下
                changeState(STATE_RECORDING);
                break;

            case MotionEvent.ACTION_MOVE://手指移动
                if (isRecording) {
                    //根据x,y的坐标判断是否需要取消
                    if (wantToCancel(x, y)) {
                        changeState(STATE_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;

            case MotionEvent.ACTION_UP://手指放开
                if (!mReady) {//没有触发OnLongClick
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecording || mTime < 600) {//如果时间少于0.6s，则提示录音过短
                    MainActivity.dialogManager.dimissDialog();
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    // 延迟显示对话框
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1000);  //延时1秒后关闭过短提示对话框
                } else if (mCurrentState == STATE_RECORDING) {
                    reset();
                    //如果状态为正在录音，则结束录制
                    mDialogManager.dimissDialog();
                    mAudioManager.release();
                    MainActivity.dialogManager.dimissDialog();
                    if (finishRecorderCallBack != null) {
                        try {
                            finishRecorderCallBack.onFinish(mTime/1000, mAudioManager.getCurrentFilePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (mCurrentState == STATE_CANCEL) { // 想要取消
                    MainActivity.dialogManager.dimissDialog();
                    mDialogManager.dimissDialog();
                    mAudioManager.cancel();
                }
                reset();
                break;

        }
        return super.onTouchEvent(event);
    }

    /**
     * 恢复状态及标志位
     */
    private void reset() {
        isRecording = false;
        mReady = false;
        changeState(STATE_NORMAL);
    }

    private boolean wantToCancel(int x, int y) {
        // 判断手指的宽度是否超出范围
        if (x < 0 || x > getWidth()) {
            return true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    /**
     * @param
     * @description 根据状态改变Button显示
     * @author ldm
     * @time 2016/6/25 9:36
     */
    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:

                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;

                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    setText(R.string.str_recorder_recording);
                    if (isRecording) {
                        //更新dialog.recording()
                        mDialogManager.recording();
                    }
                    break;

                case STATE_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    setText(R.string.str_recorder_want_cancel);
                    //更新dialog.wantoCancel()
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }
    /**
     * 接收子线程数据，并用此数据配合主线程更新UI
     * Handler运行在主线程（UI线程）中，它与子线程通过Message对象传递数据。
     * Handler接受子线程传过来的(子线程用sedMessage()方法传递)Message对象，把这些消息放入主线程队列中，配合主线程进行更新UI。
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    //显示对话框
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    // 开启一个线程计算录音时间
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;

                case MSG_VOICE_CHANGED:
                    //更新声音
                    if(mTime%1000==0)
                        DialogManager.countdown.setText((int)(5-mTime/1000)+"s");
                    //更新声音
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;

                case MSG_DIALOG_DIMISS:
                    //取消对话框
                    mDialogManager.dimissDialog();
                    break;

                case MSG_COUNT_DOWN_DONE:
                    reset();
                    mAudioManager.release();
                    // callbackToAct
                    // 正常录制结束，回调录音时间和录音文件完整路径——在播放的时候需要使用
                    MainActivity.dialogManager.dimissDialog();
                    if(finishRecorderCallBack!=null){
                        try {
                            finishRecorderCallBack.onFinish(mTime/1000, mAudioManager.getCurrentFilePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mDialogManager.dimissDialog();
                    break;
                case STATE_LOAD:
                    new Thread(show).start();
                    break;

            }
            //super.handleMessage(msg);
        }
    };
    /**
     *
     * 显示对话框
     */
    private Runnable show =new Runnable() {
        @Override
        public void run() {
            MainActivity.dialogManager.showLoad();
        }
    };

    /**
     * @description 获取音量大小的线程
     * @author ldm
     * @time 2016/6/25 9:30
     * @param
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            mTime = 0;
            while (isRecording) {//判断正在录音
                try {
                    Thread.sleep(100);
                    mTime += 100;//录音时间计算
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);//每0.1秒发送消息
                    Log.d("AudioRecorderButton", "============================mtime = " + mTime );
                    Log.d("AudioRecorderButton", "============================isRecording = " + isRecording);
                    //限制录音时长
                   if (mTime == 5 * 1000){
                       Log.d("AudioRecorderButton", "============================mtime = 5S" );
                        mHandler.sendEmptyMessage(MSG_COUNT_DOWN_DONE);
                   }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


}
