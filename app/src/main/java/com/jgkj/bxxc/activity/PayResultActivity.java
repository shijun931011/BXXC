package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.tools.StatusBarCompat;

/**
 * Created by fangzhou on 2016/11/18.
 * 用户支付返回结果，展示支付成功还是支付失败
 */
public class PayResultActivity extends Activity implements View.OnClickListener{
    private Button button_forward;
    private ImageView success,failure;
    private Intent intent;
    private TextView price;
    private String token,tiaozhaun,yuyue,jingpin;
    private LinearLayout shibai,chenggong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payresult);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        init();
        data();
    }
    private void data() {
        intent = getIntent();
        int payResult = intent.getIntExtra("result",-1);
        token = intent.getStringExtra("token");
        tiaozhaun = intent.getStringExtra("tiaozhaun");
        yuyue = intent.getStringExtra("yuyue");
        jingpin=intent.getStringExtra("jingpin");
        if(payResult == -1){
            failure.setVisibility(View.VISIBLE);
            success.setVisibility(View.GONE);
            chenggong.setVisibility(View.GONE);
            shibai.setVisibility(View.VISIBLE);
            button_forward.setTag(0);
        }else{
            if(payResult==1){
                String count = intent.getStringExtra("price");
                price.setText(count+"");
                success.setVisibility(View.VISIBLE);
                failure.setVisibility(View.GONE);
                button_forward.setTag(1);
            }else if(payResult==0){
                failure.setVisibility(View.VISIBLE);
                success.setVisibility(View.GONE);
                chenggong.setVisibility(View.GONE);
                shibai.setVisibility(View.VISIBLE);
                button_forward.setTag(0);
            }else if(payResult==2){
                int count = intent.getIntExtra("moneyId",3080);
                price.setText(count+"");
                success.setVisibility(View.VISIBLE);
                failure.setVisibility(View.GONE);
                button_forward.setTag(0);
            }else if (payResult == 3){
                String count = intent.getStringExtra("price");
                price.setText(count+"");
                success.setVisibility(View.VISIBLE);
                failure.setVisibility(View.GONE);
                button_forward.setTag(3);
            }
        }
    }
    private void init() {
        button_forward = (Button) findViewById(R.id.button_forward);
        button_forward.setText("完成");
        button_forward.setVisibility(View.VISIBLE);
        button_forward.setOnClickListener(this);
        success = (ImageView) findViewById(R.id.success);
        failure = (ImageView) findViewById(R.id.failure);
        price = (TextView) findViewById(R.id.price);
        chenggong = (LinearLayout) findViewById(R.id.chenggong);
        shibai = (LinearLayout) findViewById(R.id.shibai);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_forward:
                int tag = Integer.parseInt(button_forward.getTag().toString());
                int uid = intent.getIntExtra("uid",-1);
                switch (tag){
                    case 0:
                        finish();
                        break;
                    case 1:
                        Intent successIntent = new Intent();
                        successIntent.setClass(PayResultActivity.this,RegisterDetailActivity2.class);
                        successIntent.putExtra("uid",uid);
                        startActivity(successIntent);
                        finish();
                        break;
                    case 3:
                        Intent intent = new Intent();
                        if ("1111".equals(tiaozhaun)){
                            finish();
                        }else if ("2222".equals(yuyue)){
                            finish();
                        }else if ("3333".equals(jingpin)){
                            finish();
                        }
                        break;
                }
                break;
        }
    }
}
