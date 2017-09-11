package com.phonepartner.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phonepartner.Emoji.EmojiConversionUtil;
import com.phonepartner.R;
import com.phonepartner.entity.Msg;

import java.util.List;

/**
 * @param
 * @author ldm
 * @description ListView数据适配器
 * @time 2016/6/25 11:05
 */
public class MsgAdapter extends ArrayAdapter<Msg> {

    private Context mContext;
    private List<Msg> mDatas;
    //item的最小宽度
    private int mMinWidth;
    //item的最大宽度
    private int mMaxWidth;
    private LayoutInflater mInflater;

    public MsgAdapter(Context context, List<Msg> datas) {
        super(context, -1, datas);

        mContext = context;
        mDatas = datas;

        //获取屏幕的宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        //最大宽度为屏幕宽度的百分之七十
        mMaxWidth = (int) (outMetrics.widthPixels * 0.7f);
        //最小宽度为屏幕宽度的百分之十五
        mMinWidth = (int) (outMetrics.widthPixels * 0.10f);
        mInflater = LayoutInflater.from(context);
    }


    final class ViewHolder {
        // 显示时间
        TextView R_seconds,L_seconds,R_msg,L_msg;
        //控件Item显示的长度
        View R_length,L_length;
        //显示布局
        LinearLayout recorder_leftLayout,recorder_rightLayout,message_leftLayout, message_rightLayout;
          ImageView
                recorder_left_img,recorder_right_img,msg_left_img,msg_right_img;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Msg msg = getItem(position);
        ViewHolder holder = null;
        //初始化控件
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.msg_item, null);
            holder = new ViewHolder();

            holder.recorder_leftLayout = (LinearLayout) convertView.findViewById(R.id.recorder_left_layout);
            holder.recorder_rightLayout = (LinearLayout) convertView.findViewById(R.id.recorder_right_layout);
            holder.message_leftLayout = (LinearLayout) convertView.findViewById(R.id.message_left_layout);
            holder.message_rightLayout = (LinearLayout) convertView.findViewById(R.id.message_right_layout);
            holder.recorder_left_img = (ImageView) convertView.findViewById(R.id.recorder_left_img);
            holder.recorder_right_img = (ImageView) convertView.findViewById(R.id.recorder_right_img);
            holder.msg_left_img = (ImageView) convertView.findViewById(R.id.msg_left_img);
            holder.msg_right_img = (ImageView) convertView.findViewById(R.id.msg_right_img);
            holder.R_seconds = (TextView) convertView.findViewById(R.id.right_recoder_time);
            holder.R_length = convertView.findViewById(R.id.right_recoder_lenght);
            holder.L_seconds = (TextView) convertView.findViewById(R.id.left_recoder_time);
            holder.L_length = convertView.findViewById(R.id.left_recoder_lenght);
            holder.R_msg = (TextView) convertView.findViewById(R.id.right_msg);
            holder.L_msg = (TextView) convertView.findViewById(R.id.left_msg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(msg.getType()== Msg.RECORDER_RECEIVED)//信息类型是接收的语音信息
        {
            holder.recorder_left_img.setVisibility(View.VISIBLE);
            holder.recorder_leftLayout.setVisibility(View.VISIBLE);
            holder.recorder_right_img.setVisibility(View.GONE);
            holder.recorder_rightLayout.setVisibility(View.GONE);
            holder.msg_right_img.setVisibility(View.GONE);
            holder.message_leftLayout.setVisibility(View.GONE);
            holder.msg_left_img.setVisibility(View.GONE);
            holder.message_rightLayout.setVisibility(View.GONE);
            holder.L_seconds.setText(Math.round(getItem(position).getTime()) + "\"");
            ViewGroup.LayoutParams lp = holder.L_length.getLayoutParams();
            lp.width = (int) (mMinWidth + (mMaxWidth / 60f) * getItem(position).getTime());
            holder.L_msg.setVisibility(View.GONE);
        }
        else if(msg.getType()== Msg.RECORDER_SENT)//信息类型是接发送的语音信息
        {
            holder.recorder_left_img.setVisibility(View.GONE);
            holder.recorder_leftLayout.setVisibility(View.GONE);
            holder.recorder_right_img.setVisibility(View.VISIBLE);
            holder.recorder_rightLayout.setVisibility(View.VISIBLE);
            holder.msg_left_img.setVisibility(View.GONE);
            holder.message_leftLayout.setVisibility(View.GONE);
            holder.msg_right_img.setVisibility(View.GONE);
            holder.message_rightLayout.setVisibility(View.GONE);
            holder.R_seconds.setText(Math.round(getItem(position).getTime()) + "\"");
            ViewGroup.LayoutParams lp = holder.R_length.getLayoutParams();
            lp.width = (int) (mMinWidth + (mMaxWidth / 60f) * getItem(position).getTime());
            holder.R_msg.setVisibility(View.GONE);
        }
        else if(msg.getType()== Msg.MSG_RECEIVED)//信息类型是接收的文本信息
        {
            holder.L_msg.setVisibility(View.VISIBLE);
            holder.recorder_leftLayout.setVisibility(View.GONE);
            holder.recorder_left_img.setVisibility(View.GONE);
            holder.recorder_rightLayout.setVisibility(View.GONE);
            holder.recorder_right_img.setVisibility(View.GONE);
            holder.msg_left_img.setVisibility(View.VISIBLE);
            holder.message_leftLayout.setVisibility(View.VISIBLE);
            holder.msg_right_img.setVisibility(View.GONE);
            holder.message_rightLayout.setVisibility(View.GONE);
            //            2017-4-5 处理emoji
            SpannableString spannableString = EmojiConversionUtil.getInstace().getExpressionString(mContext, msg.getContent());
            holder.L_msg.setText(spannableString);
//            holder.L_msg.setText(msg.getContent());
        }
        else if(msg.getType()== Msg.MSG_SENT)//信息类型是发送的文本信息
        {
            holder.R_msg.setVisibility(View.VISIBLE);
            holder.recorder_left_img.setVisibility(View.GONE);
            holder.recorder_leftLayout.setVisibility(View.GONE);
            holder.recorder_right_img.setVisibility(View.GONE);
            holder.recorder_rightLayout.setVisibility(View.GONE);
            holder.msg_left_img.setVisibility(View.GONE);
            holder.message_leftLayout.setVisibility(View.GONE);
            holder.msg_right_img.setVisibility(View.VISIBLE);
            holder.message_rightLayout.setVisibility(View.VISIBLE);
            //            2017-4-5 处理emoji
            SpannableString spannableString = EmojiConversionUtil.getInstace().getExpressionString(mContext, msg.getContent());
            holder.R_msg.setText(spannableString);
        }
        return convertView;
    }
}
