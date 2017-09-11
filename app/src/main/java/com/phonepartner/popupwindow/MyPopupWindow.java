package com.phonepartner.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.phonepartner.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPopupWindow extends PopupWindow {

    private Context mContext;
    private ResultListener mListener;
    private View mView;
    private String[] mColumnNames = { "name" };
    private String[] mListTexts;
    private final int[] VIEW_IDS = { R.id.text_name };
    private List<Map<String, String>> mItems = null;

    public interface ResultListener {
        void onResultChanged(int index);
    }


    public MyPopupWindow(Context context, ResultListener listener,
                         View view, String[] listText) {
        super(context);
        this.mContext = context;
        this.mListener = listener;
        this.mView = view;
        this.mListTexts = listText;

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(mContext.getResources().getDrawable(R.color.transparent));
        setTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.AnimationFade1);

        setupView();

    }

    private void setupView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mView = inflater.inflate(R.layout.common_popupwindowlist_white, null);
        setContentView(mView);

//        mView.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//        TextView txtCancel = (TextView) mView.findViewById(R.id.txt_cancel);
//        txtCancel.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });

        ListView listView = (ListView) mView.findViewById(R.id.list_popupwindow);
        listView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
        mItems = new ArrayList<Map<String, String>>();
        mItems.clear();

        for (int i = 0; i < mListTexts.length; i++) {
            Map<String, String> item = new HashMap<String, String>();
            item.put(mColumnNames[0], mListTexts[i]);
            mItems.add(item);
        }
        SimpleAdapter mAdapter = new SimpleAdapter(mContext, mItems,
                R.layout.common_popuplistview_white_item, mColumnNames, VIEW_IDS);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                mListener.onResultChanged(arg2);
                dismiss();
            }
        });
        showAtLocation(mView, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
