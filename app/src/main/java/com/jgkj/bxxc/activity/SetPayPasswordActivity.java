package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.bean.entity.BaseEntity.BaseEntity;
import com.jgkj.bxxc.tools.Md5;
import com.jgkj.bxxc.tools.PasswordInputView;
import com.jgkj.bxxc.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class SetPayPasswordActivity extends Activity{

    private PasswordInputView again_paypswd_pet;
    private TextView tv_remain;
    private boolean flag = false;
    private String password;
    private UserInfo userInfo;
    private UserInfo.Result result;

    //标题
    private TextView title;
    private Button button_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pay_password);

        //标题
        title = (TextView) findViewById(R.id.text_title);
        title.setText("设置支付密码");
        button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setVisibility(View.VISIBLE);
        button_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        again_paypswd_pet = (PasswordInputView)findViewById(R.id.again_paypswd_pet);
        tv_remain = (TextView)findViewById(R.id.tv_remain);

        //弹出键盘
        again_paypswd_pet.setFocusable(true);
        again_paypswd_pet.setFocusableInTouchMode(true);
        again_paypswd_pet.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        again_paypswd_pet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //密码6位时执行
                if(again_paypswd_pet.getText().toString().length() == 6 && flag == false){
                    password = again_paypswd_pet.getText().toString();
                    flag = true;
                    tv_remain.setVisibility(View.VISIBLE);
                    again_paypswd_pet.setText("");
                    tv_remain.setText("请再次输入支付密码");
                }else if(again_paypswd_pet.getText().toString().length() == 6 && flag == true){
                    if(!again_paypswd_pet.getText().toString().equals(password)){
                        again_paypswd_pet.setText("");
                        tv_remain.setText("两次密码不一致，请从新设置");
                    }else{
                        SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
                        String str = sp.getString("userInfo", null);
                        Gson gson = new Gson();
                        userInfo = gson.fromJson(str, UserInfo.class);

                        SharedPreferences sp1 = getSharedPreferences("token", Activity.MODE_PRIVATE);
                        String token = sp1.getString("token", null);

                        getData(userInfo.getResult().getUid(),token, Md5.md5(password), Urls.setPayPwd);
                    }
                    flag = false;
                }
            }
        });
    }

    private void getData(int uid,String token,String paypwd,String url) {
        Log.i("百信学车","设置支付密码参数" + "uid=" + uid + "   token=" + token + "   paypwd=" + paypwd + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("paypwd", paypwd)
                .addParams("token", token)
                .addParams("uid", Integer.toString(uid))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(SetPayPasswordActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","设置支付密码结果" + s);
                        Gson gson = new Gson();
                        BaseEntity baseEntity = gson.fromJson(s, BaseEntity.class);
                        if (baseEntity.getCode() == 200) {
                            Toast.makeText(SetPayPasswordActivity.this, "支付密码设置成功", Toast.LENGTH_LONG).show();
                            /**
                             * 本地存储paypw(支付密码值)值
                             */
                            SharedPreferences sp_paypwd = getSharedPreferences("paypwd", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor_paypwd = sp_paypwd.edit();
                            editor_paypwd.clear();
                            editor_paypwd.putString("paypwd",Md5.md5(password));
                            editor_paypwd.commit();
                            finish();
                        }else{
                            Toast.makeText(SetPayPasswordActivity.this, baseEntity.getReason().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
