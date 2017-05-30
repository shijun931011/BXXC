package com.jgkj.bxxc.activity;

import com.jgkj.bxxc.bean.Code;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.bean.entity.BaseEntity.BaseEntity;
import com.jgkj.bxxc.tools.Base64;
import com.jgkj.bxxc.tools.PictureOptimization;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.jgkj.bxxc.tools.Urls;
import com.umeng.socialize.UMShareAPI;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import okhttp3.Call;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;

public class ForgetPayPasswordActivity extends Activity implements View.OnClickListener {
    private ForgetPayPasswordActivity.TimeCount time;
    private PictureOptimization po;
    private EditText phone_editText, phone_code_editText;
    private LinearLayout phone_code_linear, ll;
    private Button getCode_btn;
    private TextView countDown;
    //加载对话框
    private ProgressDialog dialog;
    private Button button_backward;
    private TextView title;

    private String phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pay_password);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        init();
    }

    private void init() {
        //忘记支付密码页面
        getCode_btn = (Button) findViewById(R.id.getCode_btn);
        //倒计时监听
        countDown = (TextView) findViewById(R.id.countDown);
        countDown.setOnClickListener(this);
        phone_code_linear = (LinearLayout) findViewById(R.id.phone_code_linear);
        //手机号码输入框
        phone_editText = (EditText) findViewById(R.id.phone_editText);
        phone_code_editText = (EditText) findViewById(R.id.phone_code_editText);

        ll = (LinearLayout) findViewById(R.id.callback_id);
        time = new ForgetPayPasswordActivity.TimeCount(60000, 1000);//构造CountDownTimer对象
        getCode_btn.setOnClickListener(this);

        po = new PictureOptimization();
        ll.setBackgroundDrawable(PictureOptimization.bitmapToDrawble(PictureOptimization.decodeSampledBitmapFromResource(getResources(),
                R.drawable.baixinxueche_login_null, 480, 760), ForgetPayPasswordActivity.this));
        //返回按钮
        button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setVisibility(View.VISIBLE);
        button_backward.setOnClickListener(this);
        //标题
        title = (TextView) findViewById(R.id.text_title);
        title.setText("忘记支付密码");
    }

    /**
     * 发送验证码
     * @param phone 手机号
     */
    private void sendMeg(String phone) {
        OkHttpUtils
                .post()
                .url(Urls.remsg)
                .addParams("phone", phone)
                .addParams("token", Base64.encode((new SimpleDateFormat("yyyy-MM-dd").format(new Date())+phone).getBytes()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialog.dismiss();
                        Toast.makeText(ForgetPayPasswordActivity.this, "发送失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","验证码结果" + s);
                        dialog.dismiss();   //关闭progressdialog
                        Gson gson = new Gson();
                        final Code code = gson.fromJson(s, Code.class);
                        if (code.getCode() == 200) {
                            phone_code_linear.setVisibility(View.VISIBLE);
                            getCode_btn.setText("完成");
                            time.start();
                        } else {
                            Toast.makeText(ForgetPayPasswordActivity.this, code.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onFinish() {//计时完毕时触发
            countDown.setText("重新验证");
            countDown.setClickable(true);
        }
        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            countDown.setClickable(false);
            countDown.setText(millisUntilFinished / 1000 + "秒");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getCode_btn:
                String str = getCode_btn.getText().toString().trim();
                final String phone = phone_editText.getText().toString().trim();
                Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0,1,2,3,5-9])|(17[0-8])|(147)|(145))\\d{8}$");// 正则表达式
                if (str.equals("获取验证码")) {
                    if (phone_editText.getText().toString().equals("") || phone_editText.getText().toString() == null) {
                        Toast.makeText(ForgetPayPasswordActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                    }else if (!p.matcher(phone).matches()) {
                        Toast.makeText(ForgetPayPasswordActivity.this, "您输入的手机号段不存在或位数不正确", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog = ProgressDialog.show(ForgetPayPasswordActivity.this, null, "玩命发送中...");
                        phoneNo = phone_editText.getText().toString().trim();
                        time.start();
                        sendMeg(phoneNo);
                    }
                }
                if(str.equals("完成")){

                    if(phone_code_editText.getText().toString() == null || "".equals(phone_code_editText.getText().toString())){
                        Toast.makeText(ForgetPayPasswordActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(phone_code_editText.getText().toString().length() != 6){
                        Toast.makeText(ForgetPayPasswordActivity.this, "验证码位数不正确", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
                    String strss = sp.getString("userInfo", null);
                    Gson gson = new Gson();
                    UserInfo userInfo = gson.fromJson(strss, UserInfo.class);

                    SharedPreferences sp1 = getSharedPreferences("token", Activity.MODE_PRIVATE);
                    String token = sp1.getString("token", null);
                    getData(userInfo.getResult().getUid(),token,phone_editText.getText().toString(),phone_code_editText.getText().toString() , Urls.judgePayPwdmsg);
                }
                break;
            case R.id.countDown:
                String str1 = countDown.getText().toString();
                if (str1.equals("重新验证")) {
                    time.start();
                    sendMeg(phoneNo);
                }
                break;
            case R.id.button_backward:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void getData(int uid,String token,String phone,String msg,String url) {
        Log.i("百信学车","验证验证码参数" + "uid=" + uid + "   token=" + token + "   msg=" + msg + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("msg", msg)
                .addParams("token", token)
                .addParams("phone", phone)
                .addParams("uid", Integer.toString(uid))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(ForgetPayPasswordActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","验证验证码结果" + s);
                        Gson gson = new Gson();
                        BaseEntity baseEntity = gson.fromJson(s, BaseEntity.class);
                        if (baseEntity.getCode() == 200) {
                            Intent intent = new Intent();
                            intent.setClass(ForgetPayPasswordActivity.this,SetPayPasswordActivity.class);
                            intent.putExtra("forgetPayFlag","ForgetPayPasswordActivity");
                            startActivity(intent);
                        }else{
                            Toast.makeText(ForgetPayPasswordActivity.this, "验证码错误", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
