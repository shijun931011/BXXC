package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.PriTeamlistAdapter;
import com.jgkj.bxxc.bean.PriCenterDetails;
import com.jgkj.bxxc.tools.PeilianDialog;
import com.jgkj.bxxc.tools.PriorPeiDialog;

import java.util.ArrayList;
import java.util.List;

public class PrivateTeamlistActivity extends Activity implements View.OnClickListener{

    private ListView pri_team_list;  //私教成员列表;
    private TextView text_title;    //标题
    private Button back;
    private String center_name;
    private String tid;
    private String token;
    private String teamResult;
    private String Cname;
    private String Sname;
    private String class_type;
    private String identity;
    private String coachId;
    private List<PriCenterDetails.Result.Member> list1;
    private int uid;
    private  List<PriCenterDetails.Result.Member> list =  new ArrayList<PriCenterDetails.Result.Member>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_teamlist);
        initView();
    }

    private void initView(){
        back = (Button) findViewById(R.id.button_backward);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        text_title = (TextView) findViewById(R.id.text_title);
        Intent intent = getIntent();
        center_name=intent.getStringExtra("name");
        uid = intent.getIntExtra("uid",uid);
        token = intent.getStringExtra("token");
        tid = intent.getStringExtra("tid");
        text_title.setText(center_name);
        pri_team_list = (ListView) findViewById(R.id.private_team_list);
        pri_team_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("BXXC", "列表："+ pri_team_list);
                Log.d("BXXC", "列表1："+ list1.get(i).getSname());
                if (list1.get(i).getSname().equals("私教-陪练")){
                    new PriorPeiDialog(PrivateTeamlistActivity.this, center_name,list1.get(i).getSname(),list1.get(i).getCname(),list1.get(i).getClass_type(),uid,token,list1.get(i).getPid(),tid).choose();
                }
                if (list1.get(i).getSname().equals("私教")){
                    Intent intent = new Intent();
                    intent.setClass(PrivateTeamlistActivity.this, ReservationDetailActivity.class);
                    intent.putExtra("center_name",center_name);
                    intent.putExtra("Cname",list1.get(i).getCname());
                    intent.putExtra("uid",uid);
                    intent.putExtra("token",token);
                    intent.putExtra("class_style",0);
                    intent.putExtra("pri_team",1);
                    intent.putExtra("cid",list1.get(i).getPid());
                    intent.putExtra("tid", tid);
                    startActivity(intent);
                }
                if (list1.get(i).getSname().equals("陪练")){
                    new PeilianDialog(PrivateTeamlistActivity.this, "因政策要求，预约陪练上车前需提供本人身份证、驾驶证，如有缺失，请勿预约，一旦预约，概不退还预约学时。", center_name,list1.get(i).getCname(),uid,token ,list1.get(i).getPid(),tid).call();
                }
            }
        });
        teamResult = intent.getStringExtra("priTeam");
        Gson gson = new Gson();
        PriCenterDetails priCenterDetails = gson.fromJson(teamResult,  PriCenterDetails.class);
        list1 = priCenterDetails.getResult().get(0).getMember();
        PriTeamlistAdapter priTeamlistAdapter = new PriTeamlistAdapter(PrivateTeamlistActivity.this, list1);
        pri_team_list.setAdapter(priTeamlistAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_backward:
                finish();
                break;
        }
    }
}
