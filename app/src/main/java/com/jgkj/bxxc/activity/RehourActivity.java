package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
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
import com.jgkj.bxxc.adapter.RehourAdapter;
import com.jgkj.bxxc.bean.Rehour;
import com.jgkj.bxxc.bean.UserInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 剩余学时
 */
public class RehourActivity extends Activity implements View.OnClickListener{
    private Button btn_backward, btn_tuikuan;
    private TextView title;
    private ListView list_hour;           //剩余学时
    private int uid;
    private String token;
    private UserInfo userInfo;
    private UserInfo.Result result;
    private TextView prompt;   //提示文字
    private ImageView img_cry;//哭脸图片
    private TextView immediate_bug;     //学时不够？立即购买

    // 剩余学时，
    /*
     * uid  token
     * 返回 400 失败  200 成功
     * package_id 套餐几
     * surplus_class 剩余课时
     * surplus_money 剩余钱
     * */
    private String RehourUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/Hours";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rehour);
        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getRehour(uid + "", token);
    }

    private void initView(){
        btn_backward = (Button) findViewById(R.id.button_backward);
        btn_tuikuan = (Button) findViewById(R.id.button_forward);
        title = (TextView) findViewById(R.id.text_title);
        btn_tuikuan = (Button) findViewById(R.id.button_forward);
        list_hour = (ListView) findViewById(R.id.listView);
        prompt=(TextView) findViewById(R.id.prompt);
        img_cry = (ImageView) findViewById(R.id.kulian);
        immediate_bug = (TextView) findViewById(R.id.immediate_bug);
        title.setText("剩余学时");
        btn_backward.setVisibility(View.VISIBLE);
        btn_tuikuan.setVisibility(View.VISIBLE);
        btn_tuikuan.setText("退款进度");
        btn_backward.setOnClickListener(this);
        btn_tuikuan.setOnClickListener(this);
        immediate_bug.setOnClickListener(this);
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid", -1);
        token = intent.getStringExtra("token");
        Log.d("shijun", uid + "::::" + token);
    }

    private void getRehour(String uid, String token){
        OkHttpUtils
                .post()
                .url(RehourUrl)
                .addParams("uid", uid)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(RehourActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("shijun", "hhhh"+s);
                        Gson gson = new Gson();
                        Rehour rehour = gson.fromJson(s, Rehour.class);
                        List<Rehour.Result> list = new ArrayList<Rehour.Result>();
                        if (rehour.getCode() == 200){
                            List<Rehour.Result> results = rehour.getResult();
                            list.addAll(results);
                        }
                        RehourAdapter adapter = new RehourAdapter(RehourActivity.this, list);
                        list_hour.setAdapter(adapter);
                        if (rehour.getCode() == 400){
                            img_cry.setVisibility(View.VISIBLE);
                            prompt.setVisibility(View.VISIBLE);
                            prompt.setText("您还没有任何学时，请速速 前往购买 ");
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.button_backward:
                finish();
                break;
            case R.id.button_forward:
                intent.setClass(this,RefundActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("token", token);
                startActivity(intent);
                break;
            case R.id.immediate_bug:
                intent.setClass(this, BugPageageActivity.class);
                startActivity(intent);
                break;

        }
    }
}
