package com.phonepartner.Emoji;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.phonepartner.R;
import com.phonepartner.View.AudioRecorderButton;

import java.util.ArrayList;
import java.util.List;

/**
 * *****************************************
 *
 * @author 廖乃波
 * @文件名称    : FaceRelativeLayout.java
 * @创建时间    : 2017-1-27 下午02:34:17
 * @文件描述    : 带表情的自定义输入框
 * *****************************************
 */

public class EmojiRelativeLayout extends RelativeLayout implements
        OnItemClickListener, OnClickListener {
    private Context context;
    /*** 表情页的监听事件*/
    private OnCorpusSelectedListener mListener;
    /*** 显示表情页的viewpager*/
    private ViewPager vp_face;
    /*** 表情页界面集合*/
    private ArrayList<View> pageViews;
    /*** 游标显示布局*/
    private LinearLayout layout_point,message_LinearLayout, recorder_LinearLayout;
    /*** 游标点集合*/
    private ArrayList<ImageView> pointViews;
    /*** 表情集合*/
    private List<List<EmojiEntity>> emojis;
    /*** 表情区域*/
    private View view;
    /*** 输入框*/
    private EditText et_sendmessage;
    /*** 表情数据填充器*/
    private List<EmojiAdapter> faceAdapters;
    /*** 当前表情页*/
    private int current = 0;
    //2017-4-1 语音监听
    private ImageButton keyboard_ImageButton, recorder_ImageButton;
    private AudioRecorderButton id_recorder_button;

    public EmojiRelativeLayout(Context context) {
        super(context);
        this.context = context;
    }

    public EmojiRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public EmojiRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void setOnCorpusSelectedListener(OnCorpusSelectedListener listener) {
        mListener = listener;
    }

    /**
     * 表情选择监听
     *
     * @author naibo-liao
     * @时间： 2013-1-15下午04:32:54
     */
    public interface OnCorpusSelectedListener {

        void onCorpusSelected(EmojiEntity emoji);
        void onCorpusDeleted();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        emojis = EmojiConversionUtil.getInstace().emojiLists;
        onCreate();
    }

    private void onCreate() {
        Init_View();
        Init_viewPager();
        Init_Point();
        Init_Data();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_face:
                // 隐藏表情选择框
                InputMethodManager imm_face = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                imm_face.hideSoftInputFromWindow(v.getWindowToken(),0);
                view.setVisibility(View.VISIBLE);
                message_LinearLayout.setVisibility(View.VISIBLE);
                recorder_LinearLayout.setVisibility(View.GONE);
                break;
            case R.id.et_sendmessage:
                // 隐藏表情选择框
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                }
                break;
            //2017-4-1
            case R.id.ib_recorder:
                message_LinearLayout.setVisibility(View.GONE);
                recorder_LinearLayout.setVisibility(View.VISIBLE);
                ((EmojiRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView();
                InputMethodManager imm_recorder = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                imm_recorder.hideSoftInputFromWindow(v.getWindowToken(),0);
                break;
            case R.id.ib_keyboard:
                message_LinearLayout.setVisibility(View.VISIBLE);
                recorder_LinearLayout.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 隐藏表情选择框
     */
    public boolean hideFaceView() {
        // 隐藏表情选择框
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    /**
     * 初始化控件
     */
    private void Init_View() {
        vp_face = (ViewPager) findViewById(R.id.vp_contains);
        et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
        layout_point = (LinearLayout) findViewById(R.id.iv_image);

        message_LinearLayout = (LinearLayout) findViewById(R.id.M_LinearLayout);
        recorder_LinearLayout = (LinearLayout) findViewById(R.id.R_LinearLayout);

        et_sendmessage.setOnClickListener(this);
        findViewById(R.id.btn_face).setOnClickListener(this);
        //emoji选择列表
        view = findViewById(R.id.ll_facechoose);
        view.setVisibility(GONE);

        //2017-4-1  增加语音功能
        keyboard_ImageButton = (ImageButton) findViewById(R.id.ib_keyboard);
        recorder_ImageButton = (ImageButton) findViewById(R.id.ib_recorder);
        id_recorder_button = (AudioRecorderButton) findViewById(R.id.id_recorder_button);
        keyboard_ImageButton.setOnClickListener(this);
        recorder_ImageButton.setOnClickListener(this);
    }

    /**
     * 初始化显示表情的viewpager
     */
    private void Init_viewPager() {
        pageViews = new ArrayList<View>();
        // 左侧添加空页
        View nullView1 = new View(context);
        // 设置透明背景
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView1);

        // 中间添加表情页

        faceAdapters = new ArrayList<EmojiAdapter>();
        for (int i = 0; i < emojis.size(); i++) {
            GridView view = new GridView(context);
            EmojiAdapter adapter = new EmojiAdapter(context, emojis.get(i));
            view.setAdapter(adapter);
            faceAdapters.add(adapter);
            view.setOnItemClickListener(this);
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            pageViews.add(view);
        }

        // 右侧添加空页面
        View nullView2 = new View(context);
        // 设置透明背景
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView2);
    }

    /**
     * 初始化游标 表情页面下方的滑动指示点
     */
    private void Init_Point() {

        pointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.d1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            //2017-3-31修改   表情页面滑动的标志点点
            layoutParams.width = 15;
            layoutParams.height = 15;
            layout_point.addView(imageView, layoutParams);
            if (i == 0 || i == pageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.d2);
            }
            pointViews.add(imageView);

        }
    }

    /**
     * 填充数据
     */
    private void Init_Data() {
        vp_face.setAdapter(new ViewPagerAdapter(pageViews));

        vp_face.setCurrentItem(1);
        current = 0;
        vp_face.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                current = arg0 - 1;
                // 描绘分页点
                draw_Point(arg0);
                // 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
                if (arg0 == pointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        vp_face.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
                        pointViews.get(1).setBackgroundResource(R.drawable.d2);
                    } else {
                        vp_face.setCurrentItem(arg0 - 1);// 倒数第二屏
                        pointViews.get(arg0 - 1).setBackgroundResource(R.drawable.d2);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    /**
     * 绘制游标背景
     */
    public void draw_Point(int index) {
        for (int i = 1; i < pointViews.size(); i++) {
            if (index == i) {
                pointViews.get(i).setBackgroundResource(R.drawable.d2);
            } else {
                pointViews.get(i).setBackgroundResource(R.drawable.d1);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        EmojiEntity emoji = (EmojiEntity) faceAdapters.get(current).getItem(arg2);
        if (emoji.getId() == R.drawable.face_del_icon) {
            int selection = et_sendmessage.getSelectionStart();
            String text = et_sendmessage.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    et_sendmessage.getText().delete(start, end);
                    return;
                }
                et_sendmessage.getText().delete(selection - 1, selection);
            }
        }
        if (!TextUtils.isEmpty(emoji.getCharacter())) {
            if (mListener != null)
                mListener.onCorpusSelected(emoji);
            SpannableString spannableString = EmojiConversionUtil.getInstace().addFace(getContext(), emoji.getId(), emoji.getCharacter());
            et_sendmessage.append(spannableString);
        }

    }
}
