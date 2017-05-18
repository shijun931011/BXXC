package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.Balance;
import com.jgkj.bxxc.tools.ActivityRuleDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;


/**
 * Created by fangzhou on 2017/3/27.
 * <p>
 * 我的设置--我的钱包
 */
public class Setting_AccountActivity extends Activity implements View.OnClickListener {
    private Button back_forward;
    private TextView title;
//    private LinearLayout bindingAlipay;
    private LinearLayout myOrder;      //订单
    private LinearLayout myCoupod;     //优惠劵
    private LinearLayout rehour;       //剩余学时
    private LinearLayout paydetail;    //支付明细
    private TextView balance_money;    //余额
    private TextView balance_explain;   //余额说明
    private Button btn_recharge;        //充值
    private int uid;
    private String token;
    private String balanceUrl="http://www.baixinxueche.com/index.php/Home/Apitokenpt/balance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_account);
        initView();
        getData();
        getBalance(uid+"", token);

    }

    private void getData() {
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid",-1);
        token = intent.getStringExtra("token");
    }

    private void getBalance(String uid, String token){
        OkHttpUtils.post()
                .url(balanceUrl)
                .addParams("uid", uid)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(Setting_AccountActivity.this,"请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("shijun","hhhh"+s);
                        Gson gson = new Gson();
                        Balance balance = gson.fromJson(s, Balance.class);
                        if (balance.getCode() == 200){
                            Balance.Result result = balance.getResult();
                            balance_money.setText("￥" + result.getBalance());
                        }
                    }
                });

    }


    private void initView() {
        back_forward = (Button) findViewById(R.id.button_backward);
        title = (TextView) findViewById(R.id.text_title);
        back_forward.setVisibility(View.VISIBLE);
        back_forward.setOnClickListener(this);
        title.setText("我的钱包");
//        bindingAlipay = (LinearLayout) findViewById(bindingAlipay);
        myOrder = (LinearLayout) findViewById(R.id.myOrder);
        myCoupod = (LinearLayout) findViewById(R.id.mycoupod);
        rehour = (LinearLayout) findViewById(R.id.re_hour);
        paydetail = (LinearLayout) findViewById(R.id.pay_detail);
        balance_money = (TextView) findViewById(R.id.balance_money);
        balance_explain = (TextView) findViewById(R.id.balance_explain);
        btn_recharge = (Button) findViewById(R.id.recharge);
//        bindingAlipay.setOnClickListener(this);
        myOrder.setOnClickListener(this);
        myCoupod.setOnClickListener(this);
        rehour.setOnClickListener(this);
        paydetail.setOnClickListener(this);
        balance_explain.setOnClickListener(this);
        btn_recharge.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.myOrder:
                intent.setClass(Setting_AccountActivity.this, MyOrderActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("token", token);
                startActivity(intent);
                break;
            case R.id.mycoupod:
                intent.setClass(Setting_AccountActivity.this,CouponActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("token", token);
                startActivity(intent);
                break;
            case R.id.re_hour:
                intent.setClass(this, RehourActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("token", token);
                startActivity(intent);
                break;
            case R.id.pay_detail:
                intent.setClass(this, PaydetailActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("token", token);
                startActivity(intent);
                break;
            case R.id.balance_explain:
                ActivityRuleDialog.Builder dialog = new ActivityRuleDialog.Builder(this);
                dialog.setTitle("余额说明")
                        .setMessage("1、 账户余额仅在本平台购买学车学时套餐的时候使用。" +
                                "2、 您的消费将优先使用您充值的余额，后使用活动赠送的金额" +
                                "3、 若涉及到余额退款，本平台将收回活动赠送的余额。")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.create().show();

        }
    }
}
