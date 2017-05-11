package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jgkj.bxxc.R;

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
    private int uid;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_account);
        initView();
        getData();
    }

    private void getData() {
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid",-1);
        Log.d("zyzhang","setting:"+uid);
        token = intent.getStringExtra("token");
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
//        bindingAlipay.setOnClickListener(this);
        myOrder.setOnClickListener(this);
        myCoupod.setOnClickListener(this);
        rehour.setOnClickListener(this);
        paydetail.setOnClickListener(this);
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
                startActivity(intent);
                break;
//            case R.id.pay_detail:
//                intent.setClass(this, PaydetailActivity.class);
//                startActivity(intent);
//                break;

        }
    }
}
