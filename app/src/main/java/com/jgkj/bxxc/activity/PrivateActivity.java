package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.CoachInfo;
import com.jgkj.bxxc.bean.Coupon;
import com.jgkj.bxxc.bean.MyPayResult;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.tools.PayResult;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

public class PrivateActivity extends Activity implements View.OnClickListener,TextWatcher {
    private Button payInfo;
    private Button back_backward;
    private TextView title;

    //服务条款
    private ImageView isCheck;
    private TextView tiaokuan;
    private boolean aipayflag = false, weixinFlag = false, aserFlg = false;
    private ImageView weixin_isCheck, aipay_isCheck;
    private LinearLayout fuwutiaokuan;
    //支付宝
    private Button imme_rightNow;
    //我的信息
    private String phone, idCard, name;
    private TextView phoneNo;
    private EditText username, userId;
    private LinearLayout weixin_layout, aipay_layout;
    private TextView messageDeatil;
    private EditText tuijianren;
    //微信支付
    private IWXAPI api;
    private UserInfo userInfo;
    private UserInfo.Result useResult;
    private TextView coach_Price;
    private String payUrl="http://www.baixinxueche.com/index.php/Home/Aliapppay/payInviter";

    private int uid;
    private String token;
    private SharedPreferences sp;
    private CoachInfo.Result result;
    private String coach;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private);
        InitView();
        getIntentData();
        Intent intent = getIntent();
        sp = getApplication().getSharedPreferences("USER",
                Activity.MODE_PRIVATE);
        String str = sp.getString("userInfo", null);
        Log.d("11111", "init: " + str);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str, UserInfo.class);
        useResult = userInfo.getResult();
        uid = useResult.getUid();
        token = intent.getStringExtra("token");
    }

    private void InitView(){
        //标题栏
        back_backward = (Button) findViewById(R.id.button_backward);
        back_backward.setVisibility(View.VISIBLE);
        back_backward.setOnClickListener(this);
        title = (TextView) findViewById(R.id.text_title);
        title.setText("订单信息");
        //我的信息
        username = (EditText) findViewById(R.id.signUpName);
        userId = (EditText) findViewById(R.id.idCard);
        phoneNo = (TextView) findViewById(R.id.signUpPhone);
        payInfo = (Button) findViewById(R.id.payInfo);
        aipay_layout = (LinearLayout) findViewById(R.id.aipay_layout);
        aipay_isCheck = (ImageView) findViewById(R.id.aipay_isCheck);
        aipay_layout.setOnClickListener(this);
        messageDeatil = (TextView) findViewById(R.id.messageDetail);
        messageDeatil.setText("报名信息     "+ Html.fromHtml("<font color=\"#ff5000\">*注：请确保以下填写信息真实有效</font>"));
        username.addTextChangedListener(this);
        userId.addTextChangedListener(this);
        coach_Price = (TextView) findViewById(R.id.coach_Price);

        //服务条款
        fuwutiaokuan = (LinearLayout) findViewById(R.id.fuwutiaokuan);
        isCheck = (ImageView) findViewById(R.id.isCheck);
        isCheck.setOnClickListener(this);
        tiaokuan = (TextView) findViewById(R.id.tiaokuan);
        tiaokuan.setOnClickListener(this);
        payInfo.setOnClickListener(this);

    }


    private void getIntentData() {
        Intent intent = getIntent();
        coach = intent.getStringExtra("coachInfo");
        Gson gson = new Gson();
        CoachInfo coachInfo = gson.fromJson(coach, CoachInfo.class);
        List<CoachInfo.Result> list = coachInfo.getResult();
        result = list.get(0);
        SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
        String str = sp.getString("userInfo", null);
        if(str==null||sp==null){
            Intent login = new Intent(PrivateActivity.this, LoginActivity.class);
            login.putExtra("message","payInfo");
            startActivity(login);
            finish();
        }else{
            Gson gson1 = new Gson();
            userInfo = gson1.fromJson(str, UserInfo.class);
            useResult = userInfo.getResult();
            coach_Price.setText("总金额：￥"+result.getPrice());
            phoneNo.setText(useResult.getPhone()+"");
        }
    }

    /**报名信息*/
    private boolean check() {
        name = username.getText().toString().trim();
        idCard = userId.getText().toString().trim();
        boolean isSuccess = false;
        if (name.equals("") || name == null || idCard.equals("") || idCard == null) { // || serFlag == false
            payInfo.setBackgroundColor(getResources().getColor(R.color.right_bg));
            payInfo.setClickable(false);
            isSuccess = false;
            Toast.makeText(PrivateActivity.this, "填写信息不完整！",Toast.LENGTH_SHORT).show();
        } else if (name != null && idCard != null && idCard.length() == 18 ) {  //&& serFlag == true
            payInfo.setClickable(true);
            payInfo.setBackgroundColor(getResources().getColor(R.color.themeColor));
            isSuccess = true;
            isCheck.setImageResource(R.drawable.right);
        }
        return isSuccess;
    }

    /**
     * 支付宝支付
     * @param uid 用户id
     * @param cid 教练id
     * @param name 用户姓名
     * @param phone 用户手机号
     * @param idcard 用户身份证号
     */
    private void sendaiPay(String uid, String cid, String name, String phone, String idcard) {
        OkHttpUtils
                .post()
                .url(payUrl)
                .addParams("uid", uid)
                .addParams("cid", cid)
                .addParams("name", name)
                .addParams("phone", phone)
                .addParams("idcard", idcard)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(PrivateActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(final String s, int i) {
                        Log.d("shijun","ddd"+s);
                        Gson gson = new Gson();
                        final MyPayResult myPayResult = gson.fromJson(s,MyPayResult.class);
                        if(myPayResult.getCode()==200){
                            final int SDK_PAY_FLAG = 1;
                            final Handler mHandler = new Handler() {
                                @SuppressWarnings("unused")
                                public void handleMessage(Message msg) {
                                    switch (msg.what) {
                                        case SDK_PAY_FLAG: {
                                            PayResult payResult = new PayResult((String) msg.obj);
                                            /**
                                             * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                                             * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                                             * docType=1) 建议商户依赖异步通知
                                             */
                                            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                                            String resultStatus = payResult.getResultStatus();
                                            Intent intent = new Intent();
                                            intent.setClass(PrivateActivity.this,PayResultActivity.class);
                                            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                                            if (TextUtils.equals(resultStatus, "9000")) {
                                                Toast.makeText(PrivateActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                                                intent.putExtra("result",1);
                                                intent.putExtra("uid",useResult.getUid());
                                                intent.putExtra("price",result.getPrice());
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // 判断resultStatus 为非"9000"则代表可能支付失败
                                                // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                                                if (TextUtils.equals(resultStatus, "8000")) {
                                                    Toast.makeText(PrivateActivity.this, "支付结果确认中,请勿重新付款", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                                    Toast.makeText(PrivateActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                                                    intent.putExtra("result",0);
                                                    intent.putExtra("uid",useResult.getUid());
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            };
                            Runnable payRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    // 构造PayTask 对象
                                    PayTask alipay = new PayTask(PrivateActivity.this);
                                    // 调用支付接口，获取支付结果
                                    String result = alipay.pay(myPayResult.getResult(), true);
                                    Message msg = new Message();
                                    msg.what = SDK_PAY_FLAG;
                                    msg.obj = result;
                                    mHandler.sendMessage(msg);
                                }
                            };
                            // 必须异步调用
                            Thread payThread = new Thread(payRunnable);
                            payThread.start();
                        }else if(myPayResult.getCode() ==400){
                            Toast.makeText(PrivateActivity.this, myPayResult.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payInfo:
                if(check()==true){
                    Log.d("1111", "onClick: ");
                    if(aipayflag == false){
                        Toast.makeText(PrivateActivity.this, "请选择支付方式",Toast.LENGTH_SHORT).show();
                    }else{
                        sendaiPay(useResult.getUid()+"",
                                result.getCid()+"",
                                username.getText().toString().trim(),
                                phoneNo.getText().toString().trim(),
                                userId.getText().toString().trim());
                    }

                }
                break;
            case R.id.button_backward:
                finish();
                break;
            case R.id.isCheck:
                check();
                break;
            case R.id.tiaokuan:
                Intent intent = new Intent();
                intent.setClass(PrivateActivity.this,WebViewActivity.class);
                intent.putExtra("url","http://www.baixinxueche.com/clause.html");
                intent.putExtra("title","服务条款");
                startActivity(intent);
                break;
            case R.id.aipay_layout:
                if (!aipayflag) {
                    aipay_isCheck.setImageResource(R.drawable.right);
                    aipayflag = true;
                } else {
                    aipay_isCheck.setImageResource(R.drawable.check_background);
                    aipayflag = false;
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        check();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


}
