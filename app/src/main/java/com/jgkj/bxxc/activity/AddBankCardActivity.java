package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.Code;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.bean.entity.BaseEntity.BaseEntity;
import com.jgkj.bxxc.tools.Base64;
import com.jgkj.bxxc.tools.SpaceText;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.jgkj.bxxc.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import okhttp3.Call;

public class AddBankCardActivity extends Activity implements View.OnClickListener{

    //标题
    private TextView title;
    private Button button_backward;
    private UserInfo userInfo;
    private String token;
    private EditText et_phone;
    private EditText et_card_number;
    private EditText et_card_style;
    private EditText et_name;
    private EditText et_verification_code;
    private TextView tv_verification_code;
    private Button btn_confirm_add;
    private AddBankCardActivity.TimeCount time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_card);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        tv_verification_code = (TextView) findViewById(R.id.tv_verification_code);
        tv_verification_code.setOnClickListener(this);
        time = new AddBankCardActivity.TimeCount(60000, 1000);//构造CountDownTimer对象

        et_card_number = (EditText)findViewById(R.id.et_card_number);
        et_card_style = (EditText)findViewById(R.id.et_card_style);
        et_name = (EditText)findViewById(R.id.et_name);
        et_phone = (EditText)findViewById(R.id.et_phone);
        et_verification_code = (EditText)findViewById(R.id.et_verification_code);
        btn_confirm_add = (Button)findViewById(R.id.btn_confirm_add);
        btn_confirm_add.setOnClickListener(this);

        SpaceText spaceText = new SpaceText(et_card_number,et_card_style);
        et_card_number.addTextChangedListener(spaceText);

        SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
        String str = sp.getString("userInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str, UserInfo.class);

        SharedPreferences sp1 = getSharedPreferences("token", Activity.MODE_PRIVATE);
        token = sp1.getString("token", null);

        //标题
        title = (TextView) findViewById(R.id.text_title);
        title.setText("添加银行卡");
        button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setVisibility(View.VISIBLE);
    }

    //添加银行卡
    private void getData(int uid,String token,String account,String bank_type,String bank_name,String bank_phone,String msg,String url) {
        Log.i("百信学车","添加银行卡参数" + "uid=" + uid + "   token=" + token  + "   account=" + account  + "   bank_type=" + bank_type  + "   bank_name=" + bank_name  + "   bank_phone=" + bank_phone  + "   msg=" + msg  +"   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("token", token)
                .addParams("uid", Integer.toString(uid))
                .addParams("account", account)
                .addParams("bank_type", bank_type)
                .addParams("bank_name", bank_name)
                .addParams("bank_phone", bank_phone)
                .addParams("phone", bank_phone)
                .addParams("msg", msg)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(AddBankCardActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","添加银行卡结果" + s);
                        Gson gson = new Gson();
                        BaseEntity baseEntity = gson.fromJson(s, BaseEntity.class);
                        if (baseEntity.getCode() == 200) {
                            Toast.makeText(AddBankCardActivity.this, "绑定成功", Toast.LENGTH_LONG).show();
                            /**
                             *
                             * 本地储存account值
                             */
                            SharedPreferences account = getSharedPreferences("useraccount", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor_account = account.edit();
                            editor_account.clear();
                            editor_account.putString("useraccount",et_card_number.getText().toString());
                            editor_account.commit();
                            finish();
                        }else{
                            Toast.makeText(AddBankCardActivity.this, baseEntity.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_verification_code:
                String str = tv_verification_code.getText().toString();
                if(str.equals("重新验证") || str.equals("获取验证码")){
                    Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0,1,2,3,5-9])|(17[0-8])|(147)|(145))\\d{8}$");// 正则表达式
                    if (et_phone.getText().toString().equals("") || et_phone.getText().toString() == null) {
                        Toast.makeText(AddBankCardActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!p.matcher(et_phone.getText().toString()).matches()) {
                        Toast.makeText(AddBankCardActivity.this, "您输入的手机号段不存在或位数不正确", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    time.start();
                    sendMeg(et_phone.getText().toString());
                }
                break;
            case R.id.button_backward:
                finish();
                break;
            case R.id.btn_confirm_add:
                if (et_card_number.getText().toString() == null || "".equals(et_card_number.getText().toString())) {
                    Toast.makeText(AddBankCardActivity.this, "银行卡号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_card_style.getText().toString() == null || "".equals(et_card_style.getText().toString())) {
                    Toast.makeText(AddBankCardActivity.this, "银行卡类型不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_name.getText().toString() == null || "".equals(et_name.getText().toString())) {
                    Toast.makeText(AddBankCardActivity.this, "持卡人姓名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_phone.getText().toString() == null || "".equals(et_phone.getText().toString())) {
                    Toast.makeText(AddBankCardActivity.this, "持卡人手机号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_verification_code.getText().toString() == null || "".equals(et_verification_code.getText().toString())) {
                    Toast.makeText(AddBankCardActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                getData(userInfo.getResult().getUid(),token,et_card_number.getText().toString(),et_card_style.getText().toString(),et_name.getText().toString(),et_phone.getText().toString(),et_verification_code.getText().toString(),Urls.addBankAndroid);
                break;
        }

    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onFinish() {//计时完毕时触发
            tv_verification_code.setText("重新验证");
            tv_verification_code.setClickable(true);
        }
        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            tv_verification_code.setClickable(false);
            tv_verification_code.setText(millisUntilFinished / 1000 + "秒");
        }
    }

    /**
     * 发送验证码
     * @param phone 手机号
     */
    private void sendMeg(String phone) {
        Log.i("百信学车","验证码参数" + "phone=" + phone + "   url=" + Urls.remsg);
        OkHttpUtils
                .post()
                .url(Urls.remsg)
                .addParams("phone", phone)
                .addParams("token", Base64.encode((new SimpleDateFormat("yyyy-MM-dd").format(new Date())+phone).getBytes()))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(AddBankCardActivity.this, "发送失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","验证码结果" + s);
                        Gson gson = new Gson();
                        final Code code = gson.fromJson(s, Code.class);
                        if (code.getCode() == 200) {
                            time.start();
                        } else {
                            Toast.makeText(AddBankCardActivity.this, code.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
