package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
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
import com.jgkj.bxxc.activity.weixin.WXPay;
import com.jgkj.bxxc.bean.MyPayResult;
import com.jgkj.bxxc.bean.entity.BaseEntity.BaseEntity;
import com.jgkj.bxxc.bean.entity.PackageEntity.PackageEntity;
import com.jgkj.bxxc.bean.entity.WXEntity.WXEntity;
import com.jgkj.bxxc.tools.Md5;
import com.jgkj.bxxc.tools.PayResult;
import com.jgkj.bxxc.tools.RemainBaseDialog;
import com.jgkj.bxxc.tools.Urls;
import com.lmj.mypwdinputlibrary.InputPwdView;
import com.lmj.mypwdinputlibrary.MyInputPwdUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class BuyClassPackagesActivity extends Activity implements View.OnClickListener {
    private TextView title;
    private Button button_backward;
    private Button button_forward;
    public TextView tv_pakage;
    public TextView tv_time;
    public TextView tv_buy_old;
    public TextView tv_buy;
    public ImageView im_pic;
    public TextView tishi;
    private TextView payment_protocol_txt;
    public String packageId, buy_tv, class_hour, class_money, class_song, class_pic;
    private PackageEntity result;
    private EditText real_name;
    private boolean aipayflag = false, weixinFlag = false, balanceFlg = false;
    private ImageView weixin_isCheck, aipay_isCheck, balance_isCheck;
    private int uid;
    private EditText username;
    private String name;
    private Button btn_pay;
    private String token;
    private String cid;
    private String Tishi;
    private String tiaozhaun;
    private String yuyue,jingpin;
    private MyInputPwdUtil myInputPwdUtil;


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
        button_forward = (Button) findViewById(R.id.button_forward);
        button_forward.setVisibility(View.VISIBLE);
        button_forward.setText("套餐详情");
        button_forward.setOnClickListener(this);
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
        balance_isCheck = (ImageView) findViewById(R.id.balance_isCheck);
        balance_isCheck.setOnClickListener(this);
        username = (EditText) findViewById(R.id.signUpName);
        name = username.getText().toString().trim();
        btn_pay = (Button) findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(this);
        payment_protocol_txt = (TextView) findViewById(R.id.payment_protocol_txt);
        payment_protocol_txt.setOnClickListener(this);
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
        Tishi = intent.getStringExtra("ti_shi");
        uid = intent.getIntExtra("uid", uid);
        cid = intent.getStringExtra("cid");
        token = intent.getStringExtra("token");
        tiaozhaun=intent.getStringExtra("tiaozhaun");
        Log.d("BXXC","白新学车："+tiaozhaun);
        yuyue=intent.getStringExtra("yuyue");
        jingpin=intent.getStringExtra("jingpin");
        packageId = intent.getStringExtra("packageId");
        tv_pakage.setText(package_tv);
        tv_buy.setText("￥" + buy_tv);
        tv_buy_old.setText("原价：￥" + Calculation(class_hour, class_money));
        tv_time.setText("所含课时数" + class_hour + class_song);
        Glide.with(BuyClassPackagesActivity.this).load(class_pic).placeholder(R.drawable
                .package_image).error(R.drawable.package_image).into(im_pic);
        Log.d("BXXC","百信学车提示："+Tishi);
        if (Tishi.equals("")){
            tishi.setVisibility(View.GONE);
        }else{
            tishi.setVisibility(View.VISIBLE);
        }

    }

    //支付宝直接购买学时
    public void getAliBuyClass(String name, int uid_, String package_id, String url) {
        Log.d("BXXC", "百信学车支付宝学时购买：" + name + ":::" + Integer.toString(uid_) + "::::" + package_id);
        OkHttpUtils.post().url(url).addParams("name", name).addParams("uid", Integer.toString
                (uid_)).addParams("package_id", package_id).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                Toast.makeText(BuyClassPackagesActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(String s, int i) {
                Gson gson = new Gson();
                final MyPayResult myPayResult = gson.fromJson(s, MyPayResult.class);
                if (myPayResult.getCode() == 200) {
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
                                    intent.setClass(BuyClassPackagesActivity.this,
                                            PayResultActivity.class);
                                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                                    if (TextUtils.equals(resultStatus, "9000")) {
                                        Toast.makeText(BuyClassPackagesActivity.this, "支付成功",
                                                Toast.LENGTH_SHORT).show();
                                        intent.putExtra("result", 3);
                                        intent.putExtra("token", token);
                                        intent.putExtra("uid", uid);
                                        intent.putExtra("cid",cid);
                                        intent.putExtra("price", buy_tv);
                                        intent.putExtra("tiaozhaun",tiaozhaun);
                                        intent.putExtra("yuyue",yuyue);
                                        intent.putExtra("jingpin",jingpin);
                                        startActivity(intent);
                                    } else {
                                        // 判断resultStatus 为非"9000"则代表可能支付失败
                                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                                        if (TextUtils.equals(resultStatus, "8000")) {
                                            Toast.makeText(BuyClassPackagesActivity.this,
                                                    "支付结果确认中,请勿重新付款", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                            Toast.makeText(BuyClassPackagesActivity.this, "支付失败",
                                                    Toast.LENGTH_SHORT).show();
                                            //intent.putExtra("result",0);
//                                            intent.putExtra("uid", uid);
//                                            startActivity(intent);
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
                } else if (myPayResult.getCode() == 400) {
                    Toast.makeText(BuyClassPackagesActivity.this, myPayResult.getReason(), Toast
                            .LENGTH_LONG).show();
                }
            }
        });
    }

    //微信直接购买学时
    private void getWeixinBuyClass(String name, int uid_, String package_id, String url) {
        OkHttpUtils.post().url(url).addParams("name", name).addParams("uid", Integer.toString
                (uid_)).addParams("package_id", package_id).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                Toast.makeText(BuyClassPackagesActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(String s, int i) {
                Log.d("BXXC", "微信支付" + s);
                Gson gson = new Gson();
                WXEntity wxEntity = gson.fromJson(s, WXEntity.class);
                if (wxEntity.getErrorCode() == 0) {
                    WXPay wxpay = new WXPay(BuyClassPackagesActivity.this, wxEntity
                            .getResponseData().getApp_response().getAppid());
                    wxpay.doPay(s, new WXPay.WXPayResultCallBack() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(BuyClassPackagesActivity.this, "支付成功", Toast
                                    .LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            if ("1111".equals(tiaozhaun)){
                                finish();
                            }else if ("2222".equals(yuyue)){
                                finish();
                            }else if ("3333".equals(jingpin)){
                                finish();
                            }
//                            finish();
                        }

                        @Override
                        public void onError(int error_code) {
                            Toast.makeText(BuyClassPackagesActivity.this, "支付失败", Toast
                                    .LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(BuyClassPackagesActivity.this, "支付取消", Toast
                                    .LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(BuyClassPackagesActivity.this, wxEntity.getErrorMsg(), Toast
                            .LENGTH_LONG).show();
                }
            }
        });

    }

    //余额支付
    private void getClassHoData(int uid_, String classhour, String paypwd, String url) {
        Log.i("百信学车", "购买学时套餐参数" + "uid=" + uid_ + "   classhour=" + classhour + "   paypwd=" +
                paypwd + "   url=" + url);
        OkHttpUtils.post()
                .url(url)
                .addParams("paypwd", paypwd)
                .addParams("classhour", classhour)
                .addParams("uid", Integer.toString(uid_))
                .build()
                .execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int i) {
                     Toast.makeText(BuyClassPackagesActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(String s, int i) {
                Log.i("百信学车", "购买学时套餐结果" + s);
                Gson gson = new Gson();
                BaseEntity buyClassHoureEntity = gson.fromJson(s, BaseEntity.class);
                if (buyClassHoureEntity.getCode() == 200) {
                    myInputPwdUtil.hide();
                    Toast.makeText(BuyClassPackagesActivity.this, "学时购买成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    if ("1111".equals(tiaozhaun)){
                        finish();
                    }else if ("2222".equals(yuyue)){
                        finish();
                    }else if ("3333".equals(jingpin)){
                        finish();
                    }
                }
                if (buyClassHoureEntity.getCode() == 700) {
                    new RemainBaseDialog(BuyClassPackagesActivity.this, "余额不足，请充值!").call();
                    myInputPwdUtil.hide();
                }
                if (buyClassHoureEntity.getCode() == 600) {
                    Toast.makeText(BuyClassPackagesActivity.this, "支付密码错误!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }


    private boolean check() {
        name = username.getText().toString().trim();
        boolean isSuccess = false;
        if (name.equals("") || name == null) {
            isSuccess = false;
            Toast.makeText(BuyClassPackagesActivity.this, "请填写真实姓名！", Toast.LENGTH_SHORT).show();
        } else if (name != null) {
            isSuccess = true;
        }
        return isSuccess;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.button_forward:
               intent.setClass(BuyClassPackagesActivity.this,WebViewActivity.class);
               intent.putExtra("url", "http://www.baixinxueche.com/index.php/Home/info/package?package_id=1");
               intent.putExtra("title", "套餐说明");
               startActivity(intent);
               break;

            case R.id.payment_protocol_txt:
                intent.setClass(BuyClassPackagesActivity.this,WebViewActivity.class);
                intent.putExtra("url","http://www.baixinxueche.com/webshow/chongzhi/recharge.html");
                intent.putExtra("title","支付协议");
                startActivity(intent);
                break;
            case R.id.btn_pay:
                if (check() == true) {
                    if (aipayflag == false && weixinFlag == false && balanceFlg == false) {
                        Toast.makeText(BuyClassPackagesActivity.this, "请选择支付方式", Toast
                                .LENGTH_SHORT).show();
                    } else {
                        if (balanceFlg) {
                            myInputPwdUtil = new MyInputPwdUtil(BuyClassPackagesActivity.this);
                            myInputPwdUtil.getMyInputDialogBuilder().setAnimStyle(R.style
                                    .dialog_anim);
                            myInputPwdUtil.setListener(new InputPwdView.InputPwdListener() {
                                @Override
                                public void hide() {
                                    myInputPwdUtil.hide();
                                }

                                @Override
                                public void forgetPwd() {
                                    //设置支付密码
                                    Intent intent = new Intent();
                                    intent.setClass(BuyClassPackagesActivity.this,
                                            ForgetPayPasswordActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void finishPwd(String pwd) {
                                    //uid,  classhour 套餐类型  paypwd 支付密码
                                    getClassHoData(uid, packageId, Md5.md5(pwd), Urls.buyClassHour);
                                }
                            });
                            SharedPreferences sharedPreferences = getSharedPreferences("paypwd",
                                    Activity.MODE_PRIVATE);
                            // 使用getString方法获得value，注意第2个参数是value的默认值
                            String paypwd = sharedPreferences.getString("paypwd", "");
                            //判断是否设置支付密码
                            if (paypwd != null && !"".equals(paypwd)) {
                                myInputPwdUtil.show();
                            } else {
                                //设置支付密码
                                intent.setClass(BuyClassPackagesActivity.this,
                                        SetPayPasswordActivity.class);
                                startActivity(intent);
                            }

                        } else if (weixinFlag) {
                            getWeixinBuyClass(name, uid, packageId, Urls.wxbuyclass);
                        } else {
                            getAliBuyClass(name, uid, packageId, Urls.alibuyclass);

                        }
                    }
                }


                break;
            case R.id.aipay_isCheck:
                if (weixinFlag && balanceFlg) {
                    weixin_isCheck.setImageResource(R.drawable.check_background);
                    balance_isCheck.setImageResource(R.drawable.check_background);
                    weixinFlag = false;
                    balanceFlg = false;
                    aipay_isCheck.setImageResource(R.drawable.right);
                    aipayflag = true;
                } else {
                    if (!aipayflag) {
                        aipay_isCheck.setImageResource(R.drawable.right);
                        weixin_isCheck.setImageResource(R.drawable.check_background);
                        balance_isCheck.setImageResource(R.drawable.check_background);
                        weixinFlag = false;
                        balanceFlg = false;
                        aipayflag = true;
                    } else {
                        aipay_isCheck.setImageResource(R.drawable.check_background);
                        aipayflag = false;
                    }
                }
                break;
            case R.id.weixin_isCheck:
                if (aipayflag && balanceFlg) {
                    aipay_isCheck.setImageResource(R.drawable.check_background);
                    balance_isCheck.setImageResource(R.drawable.check_background);
                    balanceFlg = false;
                    aipayflag = false;
                    weixin_isCheck.setImageResource(R.drawable.right);
                    weixinFlag = true;
                } else {
                    if (!weixinFlag) {
                        weixin_isCheck.setImageResource(R.drawable.right);
                        aipay_isCheck.setImageResource(R.drawable.check_background);
                        balance_isCheck.setImageResource(R.drawable.check_background);
                        balanceFlg = false;
                        aipayflag = false;
                        weixinFlag = true;
                    } else {
                        weixin_isCheck.setImageResource(R.drawable.check_background);
                        weixinFlag = false;
                    }
                }
                break;
            case R.id.balance_isCheck:
                if (aipayflag && weixinFlag) {
                    aipay_isCheck.setImageResource(R.drawable.check_background);
                    weixin_isCheck.setImageResource(R.drawable.check_background);
                    aipayflag = false;
                    weixinFlag = false;
                    balance_isCheck.setImageResource(R.drawable.right);
                    balanceFlg = true;
                } else {
                    if (!balanceFlg) {
                        balance_isCheck.setImageResource(R.drawable.right);
                        aipay_isCheck.setImageResource(R.drawable.check_background);
                        weixin_isCheck.setImageResource(R.drawable.check_background);
                        balanceFlg = true;
                    } else {
                        balance_isCheck.setImageResource(R.drawable.check_background);
                        balanceFlg = false;
                    }
                }

        }
    }

    public String Calculation(String s1, String s2) {
        return Integer.toString(Integer.parseInt(s1) * Integer.parseInt(s2));
    }
}
