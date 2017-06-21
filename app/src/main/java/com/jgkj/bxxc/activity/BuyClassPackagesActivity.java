package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.MyPayResult;
import com.jgkj.bxxc.bean.entity.PackageEntity.PackageEntity;
import com.jgkj.bxxc.tools.PayResult;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class BuyClassPackagesActivity extends Activity implements View.OnClickListener {
    private TextView title;
    private Button button_backward;
    public TextView tv_pakage;
    public TextView tv_time;
    public TextView tv_buy_old;
    public TextView tv_buy;
    public ImageView im_pic;
    public TextView tishi;
    public String packageId,buy_tv,class_hour,class_money,class_song,class_pic;
    private PackageEntity result;
    private EditText real_name;
    private boolean aipayflag = false, weixinFlag = false, balanceFlg = false;
    private ImageView weixin_isCheck, aipay_isCheck, balance_isCheck;
    private int uid;
    private EditText username;
    private String name;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_class_packages);
        initView();
        getData();
    }

    //初始化
    private void initView() {
        //标题
        title = (TextView) findViewById(R.id.text_title);
        title.setText("购买学时套餐");
        button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setVisibility(View.VISIBLE);
        button_backward.setOnClickListener(this);
        tv_pakage = (TextView) findViewById(R.id.tv_pakage);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_buy_old = (TextView) findViewById(R.id.tv_buy_old);
        tv_buy = (TextView) findViewById(R.id.tv_buy);
        im_pic = (ImageView) findViewById(R.id.im_pic);
        tishi = (TextView) findViewById(R.id.annotation);
        real_name = (EditText) findViewById(R.id.signUpName);
        aipay_isCheck = (ImageView) findViewById(R.id.aipay_isCheck);
        aipay_isCheck.setOnClickListener(this);
        weixin_isCheck = (ImageView) findViewById(R.id.weixin_isCheck);
        weixin_isCheck.setOnClickListener(this);
        balance_isCheck=(ImageView) findViewById(R.id.balance_isCheck);
        balance_isCheck.setOnClickListener(this);
        username = (EditText) findViewById(R.id.signUpName);
        name=username.getText().toString().trim();
    }


    //接收值
    private void getData() {
        Intent intent = getIntent();
        String package_tv = intent.getStringExtra("tv_pakage");
        buy_tv = intent.getStringExtra("tv_buy");
        class_hour = intent.getStringExtra("class_hour");
        class_money = intent.getStringExtra("class_money");
        class_song = intent.getStringExtra("class_song");
        class_pic = intent.getStringExtra("im_pic");
        uid = intent.getIntExtra("uid",uid);
        packageId=intent.getStringExtra("packageId");
        tv_pakage.setText(package_tv);
        tv_buy.setText("￥" + buy_tv);
        tv_buy_old.setText("原价：￥" + Calculation(class_hour, class_money));
        tv_time.setText("所含课时数" + class_hour + class_song);
        Glide.with(BuyClassPackagesActivity.this).load(class_pic).placeholder(R.drawable
                .package_image).error(R.drawable.package_image).into(im_pic);
    }

    //支付宝直接购买学时
    public void getAliBuyClass(String name, int uid_, String package_id,String url){
        OkHttpUtils.post()
                .url(url)
                .addParams("name",name)
                .addParams("uid",Integer.toString(uid_))
                .addParams("package_id",package_id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(BuyClassPackagesActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        final MyPayResult myPayResult = gson.fromJson(s, MyPayResult.class);
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
                                            intent.setClass(BuyClassPackagesActivity.this,PayResultActivity.class);
                                            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                                            if (TextUtils.equals(resultStatus, "9000")) {
                                                Toast.makeText(BuyClassPackagesActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                                                intent.putExtra("result",1);
                                                intent.putExtra("uid", uid);
                                                intent.putExtra("price",buy_tv);
                                                startActivity(intent);
                                            } else {
                                                // 判断resultStatus 为非"9000"则代表可能支付失败
                                                // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                                                if (TextUtils.equals(resultStatus, "8000")) {
                                                    Toast.makeText(BuyClassPackagesActivity.this, "支付结果确认中,请勿重新付款", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                                    Toast.makeText(BuyClassPackagesActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                                                    //intent.putExtra("result",0);
                                                    intent.putExtra("uid",uid);
                                                    startActivity(intent);
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
                                    PayTask alipay = new PayTask(BuyClassPackagesActivity.this);
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
                            Toast.makeText(BuyClassPackagesActivity.this, myPayResult.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
//            case R.id.
            case R.id.aipay_isCheck:
                if (weixinFlag&&balanceFlg) {
                    weixin_isCheck.setImageResource(R.drawable.check_background);
                    balance_isCheck.setImageResource(R.drawable.check_background);
                    weixinFlag = false;
                    balanceFlg=false;
                    aipay_isCheck.setImageResource(R.drawable.right);
                    aipayflag = true;
                }else{
                    if (!aipayflag) {
                        aipay_isCheck.setImageResource(R.drawable.right);
                        weixin_isCheck.setImageResource(R.drawable.check_background);
                        balance_isCheck.setImageResource(R.drawable.check_background);
                        weixinFlag = false;
                        balanceFlg=false;
                        aipayflag = true;
                    } else {
                        aipay_isCheck.setImageResource(R.drawable.check_background);
                        aipayflag = false;
                    }
                }
                break;
            case R.id.weixin_isCheck:
                if (aipayflag&&balanceFlg) {
                    aipay_isCheck.setImageResource(R.drawable.check_background);
                    balance_isCheck.setImageResource(R.drawable.check_background);
                    balanceFlg=false;
                    aipayflag = false;
                    weixin_isCheck.setImageResource(R.drawable.right);
                    weixinFlag = true;
                } else {
                    if (!weixinFlag) {
                        weixin_isCheck.setImageResource(R.drawable.right);
                        aipay_isCheck.setImageResource(R.drawable.check_background);
                        balance_isCheck.setImageResource(R.drawable.check_background);
                        balanceFlg=false;
                        aipayflag = false;
                        weixinFlag = true;
                    } else {
                        weixin_isCheck.setImageResource(R.drawable.check_background);
                        weixinFlag = false;
                    }
                }
                break;
            case R.id.balance_isCheck:
                if (aipayflag&&weixinFlag){
                    aipay_isCheck.setImageResource(R.drawable.check_background);
                    weixin_isCheck.setImageResource(R.drawable.check_background);
                    aipayflag=false;
                    weixinFlag=false;
                    balance_isCheck.setImageResource(R.drawable.right);
                    balanceFlg=true;
                }else{
                    if (!balanceFlg){
                        balance_isCheck.setImageResource(R.drawable.right);
                        aipay_isCheck.setImageResource(R.drawable.check_background);
                        weixin_isCheck.setImageResource(R.drawable.check_background);
                        balanceFlg=true;
                    }else{
                        balance_isCheck.setImageResource(R.drawable.check_background);
                        balanceFlg=false;
                    }
                }

        }
    }

    public String Calculation(String s1, String s2) {
        return Integer.toString(Integer.parseInt(s1) * Integer.parseInt(s2));
    }
}
