package com.phonepartner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.phonepartner.BaseActivity;
import com.phonepartner.R;
import com.phonepartner.View.InterceptLinearLayout;
import com.phonepartner.View.ViewHolder;
import com.phonepartner.adapter.PowerfulAdapter;
import com.phonepartner.entity.UserInfo;
import com.phonepartner.util.SessionUtils;

import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends BaseActivity implements View.OnClickListener {

    private TextView headtitle;
    private ImageView add_action;
    private TextView tv_fun;
    private ImageButton backImage;
    private InterceptLinearLayout line_backe_image;
    private ListView listTeam;
    private List<UserInfo> teamList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        initData();
        initView();
        initEvent();
    }

    private void initView() {
        headtitle = (TextView) findViewById(R.id.headtitle);
        headtitle.setText("队伍");
        add_action = (ImageView) findViewById(R.id.add_action);
        tv_fun = (TextView) findViewById(R.id.tv_fun);
        tv_fun.setVisibility(View.GONE);
        backImage = (ImageButton) findViewById(R.id.backImage);
        line_backe_image = (InterceptLinearLayout) findViewById(R.id.line_backe_image);
        listTeam = (ListView) findViewById(R.id.listTeam);
        PowerfulAdapter<UserInfo> adapter = new PowerfulAdapter<UserInfo>(getApplicationContext(),teamList,R.layout.person_item) {
            @Override
            public void convert(ViewHolder holder, UserInfo u) {
                holder.setTextToTextView(R.id.friend_name,u.getMemoName());

            }

            @Override
            public void setListener(ViewHolder holder, int position, View.OnClickListener listener) {

            }
        };
        listTeam.setAdapter(adapter);
        listTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TeamActivity.this,TeamMemberActivity.class);
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                String userStr = gson.toJson(teamList.get(position));
                bundle.putString("user",userStr);
               intent.putExtras(bundle);
                startActivity(intent);
            //startActivity(TeamMemberActivity.class,bundle);
            }
        });
    }

    private void initData(){
        teamList = new ArrayList<>();
        UserInfo userInfo = SessionUtils.getInstance(getApplicationContext()).getLoginUserInfo();
        if(userInfo != null){
            teamList.add(userInfo);
        }
        /*******TODO:这里应该由广播获得，这里先写死*******/
        //UserInfo userInfo1 = SessionUtils.getInstance(getApplicationContext()).getMemberUserInfo();

        UserInfo userInfo1 = new UserInfo("13860473129","123456","201702293112156778","hjf","cwj","13599510549");

        userInfo1.setAddress("厦门");
        userInfo1.setMail("hjf@123.com");


        SessionUtils.getInstance(getApplicationContext()).saveMemberUserInfo(userInfo1);
        teamList.add(userInfo1);

        userInfo1 = new UserInfo("13860473129","123456","201702293112156778","ysp","cwj","13599510549");
        userInfo1.setAddress("厦门");
        userInfo1.setMail("hjf@123.com");
        SessionUtils.getInstance(getApplicationContext()).saveMemberUserInfo(userInfo1);

        teamList.add(userInfo1);
    }
    private void initEvent() {
        line_backe_image.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.line_backe_image:
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);//淡入浅出动画
                break;
        }
    }
}
