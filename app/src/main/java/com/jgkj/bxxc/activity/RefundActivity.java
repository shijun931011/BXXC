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
import com.jgkj.bxxc.adapter.RefundAdapter;
import com.jgkj.bxxc.bean.Refund;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 退款进度
 */
public class RefundActivity extends Activity implements View.OnClickListener{
    private TextView title;
    private Button back;
    private ListView list_refund;
    private TextView txt_null;     //数据为空

    private int uid;
    private int pack=1;
    private String token;

    /*
     * 退款明细；http://www.baixinxueche.com/index.php/Home/Apitokenpt/refundDetail
    * 套餐退款 传 uid  token   package = 1
    * 余额退款 传 uid  token
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
        setContentView(R.layout.activity_refund);
        InitView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getRefund(uid + "", token, pack);
    }

    private void InitView(){
        title = (TextView) findViewById(R.id.text_title);
        title.setText("套餐退款进度");
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

    private void getRefund(String uid, String token, int pack){
        OkHttpUtils
                .post()
                .url(RefundUrl)
                .addParams("uid", uid)
                .addParams("token", token)
                .addParams("package","1")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(RefundActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
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
                        RefundAdapter adapter = new RefundAdapter(RefundActivity.this,list);
                        list_refund.setAdapter(adapter);

                        if (refund.getCode() == 400){
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
