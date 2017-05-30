package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.RefundAdapter;
import com.jgkj.bxxc.bean.Refund;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class BalanceRefundActivity extends Activity implements View.OnClickListener{
    private TextView title;
    private Button back;
    private ListView list_refund;
    private TextView txt_null;     //数据为空
    private int uid;
    private String token;
    /* 余额退款 传 uid  token
    * 返回
    * refundName 退款名字
    * refundTime 退款时间
    * refundMoney 退款金额
    * refundState 退款状态  1 待转账   2 转账成功
    * */
    private String RefundUrl="http://www.baixinxueche.com/index.php/Home/Apitokenpt/refundDetail";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_refund);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        InitView();
        getBalanceRefund(uid + "", token);
    }

    private void InitView() {
        title = (TextView) findViewById(R.id.text_title);
        title.setText("余额退款进度");
        back = (Button) findViewById(R.id.button_backward);
        list_refund = (ListView) findViewById(R.id.list_refund);
        txt_null = (TextView) findViewById(R.id.txt_null);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        Intent intent = getIntent();
        uid = intent.getIntExtra("uid", -1);
        token = intent.getStringExtra("token");
        Log.d("shijun", uid + "::::" + token);
    }

    private void getBalanceRefund(String uid, String token){
        OkHttpUtils
                .post()
                .url(RefundUrl)
                .addParams("uid", uid)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(BalanceRefundActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("shijun", "hhhh"+s);
                        Gson gson = new Gson();
                        Refund refund =  gson.fromJson(s, Refund.class);
                        List<Refund.Result> list = new ArrayList<Refund.Result>();
                        if (refund.getCode() == 200) {
                            List<Refund.Result> inviteResult = refund.getResult();
                            list.addAll(inviteResult);
                        }
                        RefundAdapter adapter = new RefundAdapter(BalanceRefundActivity.this,list);
                        list_refund.setAdapter(adapter);

                        if (refund.getCode() == 400){
                            txt_null.setVisibility(View.VISIBLE);
                        }
                    }

                });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_backward:
                finish();
                break;
        }
    }
}
