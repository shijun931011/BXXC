package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.PaydetailAdapter;
import com.jgkj.bxxc.bean.PayDetail;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class PaydetailActivity extends Activity implements View.OnClickListener{
    private TextView title;
    private Button btn_back;
    private TextView txt_null;     //数据为空
    private ListView paydetail_list;
    private int uid;
    private String token;

    //支付明细 接口
    /**
      uid  token
     返回值：
     'money  金额；
     orderNo 订单号
     applyTime 支付时间
     paydel 支付详情
     paystate 支付方式
     */
    private String PaydetailUrl="http://www.baixinxueche.com/index.php/Home/Apitokenpt/payDetail";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paydetail);
        InitView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getPaydetails(uid + "", token);
    }

    private void InitView(){
        title = (TextView) findViewById(R.id.text_title);
        btn_back = (Button) findViewById(R.id.button_backward);
        txt_null = (TextView) findViewById(R.id.txt_null);
        paydetail_list = (ListView) findViewById(R.id.pay_list);
        title.setText("支付明细");
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);

        Intent intent = getIntent();
        uid = intent.getIntExtra("uid", -1);
        token = intent.getStringExtra("token");

    }

    private void getPaydetails(String uid, String token){
        OkHttpUtils
                .post()
                .url(PaydetailUrl)
                .addParams("uid", uid)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(PaydetailActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("shijun", "hhhh"+s);
                        Gson gson = new Gson();
                        PayDetail payDetail = gson.fromJson(s, PayDetail.class);
                        List<PayDetail.Result> list = new ArrayList<PayDetail.Result>();
                        if (payDetail.getCode() == 200){
                            List<PayDetail.Result> paydetailResult = payDetail.getResult();
                            list.addAll(paydetailResult);
                        }
                        PaydetailAdapter adapter = new PaydetailAdapter(PaydetailActivity.this,list);
                        paydetail_list.setAdapter(adapter);
                        if (payDetail.getCode() == 400){
                            txt_null.setVisibility(View.VISIBLE);
                        }
                    }

                });
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
