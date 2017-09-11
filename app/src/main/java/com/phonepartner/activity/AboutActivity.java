package com.phonepartner.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.phonepartner.BaseActivity;
import com.phonepartner.R;
import com.phonepartner.View.InterceptLinearLayout;

public class AboutActivity extends BaseActivity {

    private TextView headtitle;
    private InterceptLinearLayout line_backe_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
    }

    private void initView() {
        headtitle = (TextView) findViewById(R.id.headtitle);
        headtitle.setText("关于");
        line_backe_image = (InterceptLinearLayout) findViewById(R.id.line_backe_image);
        line_backe_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);//淡入浅出动画
            }
        });
    }
}
