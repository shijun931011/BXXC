package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.InvitedToRecordAdapter;
import com.jgkj.bxxc.bean.Invite;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2017/3/25.
 */

public class InvitedToRecordActivity extends Activity  {
    private TextView title;
    private Button back;
    private ListView listView;
    private InvitedToRecordAdapter adapter;
    private TextView invite_number;
    //获取被邀请人数据的接口：
    private String InviteSendUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenupdata/inviteSend";
    //提现接口：
    private String TiXianUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenupdata/tixian";
    private int uid;
    private String token;
    private String invitestate;
    private String inviteid;
    List<Invite.Result> list = new ArrayList<Invite.Result>();
    private UserInfo userInfo;
    private UserInfo.Result result;

    private Invite.Result Inviteresult;
    private Invite InviteInfo;

    private SharedPreferences sp1,sp2,sp3;

    public static String bankType = "";

    private TextView noSmsData;

    //广播接收更新数据
    protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            updataInviter(uid + "", token);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invited_to_record);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getInviter(uid + "", token);
    }

    private void initView() {
        title = (TextView) findViewById(R.id.text_title);
        title.setText("邀请记录");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView = (ListView) findViewById(R.id.listView);
        noSmsData = (TextView)findViewById(R.id.noSmsData);
        listView.setEmptyView(noSmsData);
        invite_number = (TextView) findViewById(R.id.invite_number);
        SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
        String str = sp.getString("userInfo", null);
        Log.d("zyzhang", "invited:" + str);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str, UserInfo.class);
        result = userInfo.getResult();
        uid = result.getUid();
        sp1 = getApplication().getSharedPreferences("token",Activity.MODE_PRIVATE);
        token = sp1.getString("token", null);

    }

    private void getInviter(String uid,String token) {
        OkHttpUtils
                .post()
                .url(InviteSendUrl)
                .addParams("uid", uid)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(InvitedToRecordActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百姓学车", "邀请记录"+s);
                        //TODO
                        Gson gson = new Gson();
                        Invite invite = gson.fromJson(s, Invite.class);
                        if (invite.getCode() == 200) {
                            List<Invite.Result> inviteResult = invite.getResult();
                            Log.d("1111", "onResponse: "+ inviteResult.size());
                            list.addAll(inviteResult);
                            //设置邀请人数
                            invite_number.setText(inviteResult.size() + "");
                            bankType = invite.getUseraccount().getBanktype();
                        }
                        adapter = new InvitedToRecordAdapter(InvitedToRecordActivity.this,list);
                        listView.setAdapter(adapter);
                    }
                });
    }

    private void updataInviter(String uid,String token) {
        OkHttpUtils
                .post()
                .url(InviteSendUrl)
                .addParams("uid", uid)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(InvitedToRecordActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百姓学车", "邀请记录"+s);
                        //TODO
                        Gson gson = new Gson();
                        Invite invite = gson.fromJson(s, Invite.class);
                        if (invite.getCode() == 200) {
                            List<Invite.Result> inviteResult = invite.getResult();
                            Log.d("1111", "onResponse: "+ inviteResult.size());
                            list.clear();
                            list.addAll(inviteResult);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("updataInvationApp");
        registerReceiver(this.broadcastReceiver, filter);
    }
}
