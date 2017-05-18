package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.UserInfo;

public class AccountSecurityActivity extends Activity implements View.OnClickListener{

    //设置支付密码
    private LinearLayout linearlayout_set_pay_password;
    //修改支付密码
    private LinearLayout linearlayout_modify_pay_password;
    //忘记支付密码
    private LinearLayout linearlayout_forget_pay_password;

    private UserInfo userInfo;

    private String paypwd;

    //标题
    private TextView title;
    private Button button_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);

        //标题
        title = (TextView) findViewById(R.id.text_title);
        title.setText("账号与安全");
        button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setVisibility(View.VISIBLE);
        button_backward.setOnClickListener(this);

        linearlayout_set_pay_password = (LinearLayout)findViewById(R.id.linearlayout_set_pay_password);
        linearlayout_modify_pay_password = (LinearLayout)findViewById(R.id.linearlayout_modify_pay_password);
        linearlayout_forget_pay_password = (LinearLayout)findViewById(R.id.linearlayout_forget_pay_password);

        SharedPreferences sharedPreferences= getSharedPreferences("paypwd", Activity.MODE_PRIVATE);
        // 使用getString方法获得value，注意第2个参数是value的默认值
        paypwd = sharedPreferences.getString("paypwd", "");
        //判断是否设置支付密码
        if("".equals(paypwd)){
            linearlayout_set_pay_password.setVisibility(View.VISIBLE);
            linearlayout_modify_pay_password.setVisibility(View.GONE);
            linearlayout_forget_pay_password.setVisibility(View.GONE);
        }else{
            linearlayout_set_pay_password.setVisibility(View.GONE);
            linearlayout_modify_pay_password.setVisibility(View.VISIBLE);
            linearlayout_forget_pay_password.setVisibility(View.VISIBLE);
        }
        linearlayout_set_pay_password.setOnClickListener(this);
        linearlayout_modify_pay_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.linearlayout_set_pay_password:
                intent.setClass(AccountSecurityActivity.this,SetPayPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.linearlayout_modify_pay_password:
                intent.setClass(AccountSecurityActivity.this,ModifyPayPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.button_backward:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences= getSharedPreferences("paypwd", Activity.MODE_PRIVATE);
        // 使用getString方法获得value，注意第2个参数是value的默认值
        paypwd = sharedPreferences.getString("paypwd", "");
        if("".equals(paypwd)){
            linearlayout_set_pay_password.setVisibility(View.VISIBLE);
            linearlayout_modify_pay_password.setVisibility(View.GONE);
            linearlayout_forget_pay_password.setVisibility(View.GONE);
        }else{
            linearlayout_set_pay_password.setVisibility(View.GONE);
            linearlayout_modify_pay_password.setVisibility(View.VISIBLE);
            linearlayout_forget_pay_password.setVisibility(View.VISIBLE);
        }
    }
}
