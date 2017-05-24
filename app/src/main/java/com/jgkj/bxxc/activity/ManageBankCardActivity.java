package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.ManageBankCardAdapter;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.bean.entity.ManageBankCardEntity.ManageBankCardEntity;
import com.jgkj.bxxc.bean.entity.ManageBankCardEntity.ManageBankCardResult;
import com.jgkj.bxxc.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ManageBankCardActivity extends Activity{

    //标题
    private TextView title;
    private Button button_backward;
    private ImageView remind;
    private UserInfo userInfo;
    private String token;
    private ListView listView;
    private TextView noSmsData;
    private List<ManageBankCardEntity> list = new ArrayList<>();
    private ManageBankCardAdapter manageBankCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bank_card);

        SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
        String str = sp.getString("userInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str, UserInfo.class);

        SharedPreferences sp1 = getSharedPreferences("token", Activity.MODE_PRIVATE);
        token = sp1.getString("token", null);

        //标题
        title = (TextView) findViewById(R.id.text_title);
        title.setText("管理银行卡");
        button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setVisibility(View.VISIBLE);
        remind = (ImageView) findViewById(R.id.remind);
        remind.setVisibility(View.VISIBLE);
        remind.setImageDrawable(getResources().getDrawable(R.drawable.myprise));

        listView = (ListView)findViewById(R.id.listView);
        noSmsData = (TextView)findViewById(R.id.noSmsData);
        listView.setEmptyView(noSmsData);

        button_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ManageBankCardActivity.this,AddBankCardActivity.class);
                startActivity(intent);
            }
        });

        getData(userInfo.getResult().getUid(),token,Urls.getBank);

    }

    //确认支付密码
    private void getData(int uid,String token,String url) {
        Log.i("百信学车","管理银行卡参数" + "uid=" + uid + "   token=" + token  + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("token", token)
                .addParams("uid", Integer.toString(uid))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(ManageBankCardActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","管理银行卡结果" + s);
                        Gson gson = new Gson();
                        ManageBankCardResult manageBankCardResult = gson.fromJson(s, ManageBankCardResult.class);
                        if (manageBankCardResult.getCode() == 200) {
                            list = manageBankCardResult.getResult();
                            manageBankCardAdapter = new ManageBankCardAdapter(ManageBankCardActivity.this,list);
                            listView.setAdapter(manageBankCardAdapter);
                        }else{
                            Toast.makeText(ManageBankCardActivity.this, manageBankCardResult.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //确认支付密码
    private void getUpdataData(int uid,String token,String url) {
        Log.i("百信学车","管理银行卡参数" + "uid=" + uid + "   token=" + token  + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("token", token)
                .addParams("uid", Integer.toString(uid))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(ManageBankCardActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","管理银行卡结果" + s);
                        Gson gson = new Gson();
                        ManageBankCardResult manageBankCardResult = gson.fromJson(s, ManageBankCardResult.class);
                        if (manageBankCardResult.getCode() == 200) {
                            list.clear();
                            list.addAll(manageBankCardResult.getResult());
                            manageBankCardAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(ManageBankCardActivity.this, manageBankCardResult.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getUpdataData(userInfo.getResult().getUid(),token,Urls.getBank);
    }
}
