package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.bean.entity.BaseEntity.BaseEntity;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.jgkj.bxxc.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class ModifyLoginPasswordActivity extends Activity{
    //标题
    private TextView title;
    private Button button_backward;
    private TextView tv_account;
    private EditText et_old_password;
    private EditText et_new_password;
    private Button btn_confirm_modify;
    private String phone;
    private UserInfo userInfo;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_login_password);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        Intent intent = getIntent();
        phone = intent.getStringExtra("account_phone");

        SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
        String str = sp.getString("userInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str, UserInfo.class);

        SharedPreferences sp1 = getSharedPreferences("token", Activity.MODE_PRIVATE);
        token = sp1.getString("token", null);

        //标题
        title = (TextView) findViewById(R.id.text_title);
        title.setText("修改密码");
        button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setVisibility(View.VISIBLE);

        tv_account = (TextView)findViewById(R.id.tv_account);
        tv_account.setText(phone);

        et_old_password = (EditText)findViewById(R.id.et_old_password);
        et_new_password = (EditText)findViewById(R.id.et_new_password);
        btn_confirm_modify = (Button)findViewById(R.id.btn_confirm_modify);

        button_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_confirm_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_old_password.getText().toString() == null || "".equals(et_old_password.getText().toString())){
                    Toast.makeText(ModifyLoginPasswordActivity.this,"旧密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_new_password.getText().toString() == null || "".equals(et_new_password.getText().toString())){
                    Toast.makeText(ModifyLoginPasswordActivity.this,"新密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_old_password.length() < 6){
                    Toast.makeText(ModifyLoginPasswordActivity.this,"旧密码位数小于6",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_new_password.length() < 6){
                    Toast.makeText(ModifyLoginPasswordActivity.this,"新密码位数小于6",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_old_password.length() >16){
                    Toast.makeText(ModifyLoginPasswordActivity.this,"旧密码位数大于16",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_new_password.length() >16){
                    Toast.makeText(ModifyLoginPasswordActivity.this,"新密码位数大于16",Toast.LENGTH_SHORT).show();
                    return;
                }
                getData(userInfo.getResult().getUid(),token,et_old_password.getText().toString(),et_new_password.getText().toString(),phone, Urls.repassword);
            }
        });

    }

    //确认支付密码
    private void getData(int uid,String token,String password,String repassword,String phone,String url) {
        Log.i("百信学车","修改登录密码参数" + "uid=" + uid + "   token=" + token + "   password=" + password + "   repassword=" + repassword + "   phone=" + phone + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("password", password)
                .addParams("repassword", repassword)
                .addParams("phone", phone)
                .addParams("token", token)
                .addParams("uid", Integer.toString(uid))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(ModifyLoginPasswordActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","修改登录密码结果" + s);
                        Gson gson = new Gson();
                        BaseEntity baseEntity = gson.fromJson(s, BaseEntity.class);
                        if (baseEntity.getCode() == 200) {
                            Toast.makeText(ModifyLoginPasswordActivity.this, "修改登录密码成功", Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            Toast.makeText(ModifyLoginPasswordActivity.this, baseEntity.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
