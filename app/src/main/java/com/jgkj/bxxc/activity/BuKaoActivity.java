package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.weixin.WXPay;
import com.jgkj.bxxc.bean.MyPayResult;
import com.jgkj.bxxc.bean.ShowRePay;
import com.jgkj.bxxc.bean.entity.WXEntity.WXEntity;
import com.jgkj.bxxc.tools.PayResult;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

import static com.jgkj.bxxc.activity.union.RSAUtil.verify;

/**
 * Created by fangzhou on 2017/1/5.
 * 补考展示页面
 */
public class BuKaoActivity extends Activity implements View.OnClickListener {
    private TextView title, name, classType, car, phone, ordername, orderfee,coach_Price,tiaokuan;
    private Button back;
    private ImageView img, weixin_isCheck, aipay_isCheck, union_isCheck;
    private LinearLayout weixin_layout, aipay_layout, union_layout;
    private RelativeLayout layout;
    private ShowRePay showRePay;
    //服务条款
    private ImageView isCheck;
    private boolean aipayflag = false, weixinFlag = false, serFlag = false, unionFlag = false;
    private int uid;
    private String token;
    private Button payInfo;
    /**
     * 支付宝补考支付
     * uid  baixin_state  refee
     */
    private String payUrl = "http://www.baixinxueche.com/index.php/Home/Aliappretext/retestPay";
    /*
     * 微信支付  补考支付
     * uid  baixin_state  refee
     */
    private String weixinPayUrl="http://www.baixinxueche.com/index.php/Home/Aliappretext/wxretestPay";
    /**
     * 银联补考支付
     * uid  baixin_state  refee
     */
    private String unionPayUrl="http://www.baixinxueche.com/index.php/Home/Unionpay/unionRetestPay";
    private String NewOrderUrl="http://www.baixinxueche.com/index.php/Home/Apitokenupdata/retest";

    /*****************************************************************
     * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
     *****************************************************************/
    private final String mMode = "00";

    class Union{
        private String code;
        private String reason;
        private String result;

        public String getCode() {
            return code;
        }
        public String getReason() {
            return reason;
        }
        public String getResult() {
            return result;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bukao);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        initView();
        getintent();
    }
    private void getintent(){
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid",-1);
        token = intent.getStringExtra("token");
        getData(uid+"",token);
    }

    private void initView() {
        weixin_isCheck = (ImageView) findViewById(R.id.weixin_isCheck);
        weixin_layout = (LinearLayout) findViewById(R.id.weixin_layout);
        weixin_layout.setOnClickListener(this);
        aipay_layout = (LinearLayout) findViewById(R.id.aipay_layout);
        aipay_isCheck = (ImageView) findViewById(R.id.aipay_isCheck);
        aipay_layout.setOnClickListener(this);
        union_layout = (LinearLayout) findViewById(R.id.union_layout);
        union_layout.setOnClickListener(this);
        union_isCheck = (ImageView) findViewById(R.id.union_isCheck);
        payInfo = (Button) findViewById(R.id.payInfo);
        payInfo.setOnClickListener(this);
        coach_Price = (TextView) findViewById(R.id.coach_Price);
        isCheck = (ImageView) findViewById(R.id.isCheck);
        isCheck.setOnClickListener(this);
        title = (TextView) findViewById(R.id.text_title);
        title.setText("补考详情");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        name = (TextView) findViewById(R.id.name);
        classType = (TextView) findViewById(R.id.classType);
        car = (TextView) findViewById(R.id.car);
        phone = (TextView) findViewById(R.id.phone);
        ordername = (TextView) findViewById(R.id.orderName);
        orderfee = (TextView) findViewById(R.id.orderfee);
        img = (ImageView) findViewById(R.id.headImg);
        layout = (RelativeLayout) findViewById(R.id.layout);
        tiaokuan = (TextView) findViewById(R.id.tiaokuan);
        tiaokuan.setOnClickListener(this);
    }
    private void getData(String uid, String token) {
        OkHttpUtils
                .post()
                .url(NewOrderUrl)
                .addParams("uid", uid)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(BuKaoActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("百信学车","补考费用:"+s);
                        layout.setTag(s);
                        if(layout.getTag()!=null){
                            setData();
                        }
                    }
                });
    }

    /**
     * 给控件填充值
     */
    private void setData(){
        String str = layout.getTag().toString();
        Gson gson = new Gson();
        showRePay = gson.fromJson(str,ShowRePay.class);
        if(showRePay.getCode()==200){
            String url = showRePay.getResult().getFile();
            ShowRePay.Result res = showRePay.getResult();
            if (!url.endsWith(".jpg") && !url.endsWith(".jpeg") && !url.endsWith(".png") &&
                    !url.endsWith(".GIF") && !url.endsWith(".PNG") && !url.endsWith(".JPG") && !url.endsWith(".gif")) {
                Glide.with(this).load("http://www.baixinxueche.com/Public/Home/img/default.png").into(img);
            } else {
                Glide.with(this).load(url).into(img);
            }
            name.setText(res.getName());
            car.setText("所报车型："+res.getCar());
            classType.setText("所报班级："+res.getClass_class());
            phone.setText("手机号："+res.getPhone());
            orderfee.setText("￥"+res.getRefee());
            if(res.getBaixin_state()==1){
                ordername.setText("百信学车-科目一补考费");
            }else if(res.getBaixin_state()==2){
                ordername.setText("百信学车-科目二补考费");
            }else if(res.getBaixin_state()==3){
                ordername.setText("百信学车-科目三补考费");
            }
            coach_Price.setText("总金额:￥"+res.getRefee());
        }else{
            Toast.makeText(BuKaoActivity.this, showRePay.getReason(), Toast.LENGTH_SHORT).show();
        }
    }

    //微信补考费
    private void sendweixinPay(String uid, String baixin_state, String fee){
        OkHttpUtils
                .post()
                .url(weixinPayUrl)
                .addParams("uid", uid)
                .addParams("baixin_state", baixin_state)
                .addParams("refee",fee)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(BuKaoActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车", "微信结果"+s);
                        Gson gson = new Gson();
                        WXEntity wxEntity = gson.fromJson(s, WXEntity.class);
                        if(wxEntity.getErrorCode() == 0){
                            WXPay wxpay = new WXPay(BuKaoActivity.this, wxEntity.getResponseData().getApp_response().getAppid());
                            wxpay.doPay(s, new WXPay.WXPayResultCallBack() {
                                @Override
                                public void onSuccess() {

                                    Toast.makeText(BuKaoActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(int error_code) {
                                    Toast.makeText(BuKaoActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancel() {
                                    Toast.makeText(BuKaoActivity.this, "支付取消", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else{
                            Toast.makeText(BuKaoActivity.this, wxEntity.getErrorMsg(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * 支付宝交补考费
     * @param uid
     */
    private void sendaiPay(String uid, String baixin_state, String fee) {
        OkHttpUtils
                .post()
                .url(payUrl)
                .addParams("uid", uid)
                .addParams("baixin_state", baixin_state)
                .addParams("refee",fee)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(BuKaoActivity.this, "网络加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        final MyPayResult myPayResult = gson.fromJson(s,MyPayResult.class);
                        if(myPayResult.getCode().equals("200")){
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
                                            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                                            if (TextUtils.equals(resultStatus, "9000")) {
                                                Toast.makeText(BuKaoActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                // 判断resultStatus 为非"9000"则代表可能支付失败
                                                // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                                                if (TextUtils.equals(resultStatus, "8000")) {
                                                    Toast.makeText(BuKaoActivity.this, "支付结果确认中,请勿重新付款", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                                    Toast.makeText(BuKaoActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
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
                                    PayTask alipay = new PayTask(BuKaoActivity.this);
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
                        }else if(myPayResult.getCode().equals("400")){
                            Toast.makeText(BuKaoActivity.this, myPayResult.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendunionPay(String uid, String baixin_state, String fee){
        OkHttpUtils
                .post()
                .url(unionPayUrl)
                .addParams("uid", uid)
                .addParams("baixin_state", baixin_state)
                .addParams("refee",fee)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(BuKaoActivity.this, "网络加载失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("百信学车","银联补考:"+s);
//                        //2.解析服务器返回的流水号
//                        String tn = s;
                        //3.调用银联sdk,传入流水号
                        /**
                         * tn:交易流水号
                         * mode："00"启动银联正式环境 ,"01"连接银联测试环境（可以使用测试账号，测试账号参阅文档）
                         */
                        Gson gson = new Gson();
                        Union union = gson.fromJson(s,Union.class);
                        String tn = union.getResult();
                        Log.d("百信学车","jnjisdvhnjis"+tn);
                        UPPayAssistEx.startPayByJAR(BuKaoActivity.this, PayActivity.class,null,null,tn,mMode);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            // 如果想对结果数据验签，可使用下面这段代码，但建议不验签，直接去商户后台查询交易结果
            // result_data结构见c）result_data参数说明
            if (data.hasExtra("result_data")) {
                String result = data.getExtras().getString("result_data");
                try {
                    JSONObject resultJson = new JSONObject(result);
                    String sign = resultJson.getString("sign");
                    String dataOrg = resultJson.getString("data");
                    // 此处的verify建议送去商户后台做验签
                    // 如要放在手机端验，则代码必须支持更新证书
                    boolean ret = verify(dataOrg, sign, mMode);
                    if (ret) {
                        // 验签成功，显示支付结果
                        msg = "支付成功！";
                    } else {
                        // 验签失败
                        msg = "支付失败！";
                    }
                } catch (JSONException e) {
                }
            }
            // 结果result_data为成功时，去商户后台查询一下再展示成功
            msg = "支付成功！";
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        // builder.setCustomTitle();
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payInfo:
                if (aipayflag == false && weixinFlag == false && unionFlag == false) {
                    Toast.makeText(BuKaoActivity.this, "请选择支付方式", Toast.LENGTH_SHORT).show();
                } else {
                    if (weixinFlag == true) {
                        sendweixinPay(uid+"",showRePay.getResult().getBaixin_state()+"",showRePay.getResult().getRefee()+"");
                    } else if (aipayflag == true){
                        sendaiPay(uid+"",showRePay.getResult().getBaixin_state()+"",showRePay.getResult().getRefee()+"");
                    }else if (unionFlag ==true){
                        sendunionPay(uid+"",showRePay.getResult().getBaixin_state()+"",showRePay.getResult().getRefee()+"");
                        Log.d("百信学车","银联补考支付参数"+uid+":::"+showRePay.getResult().getBaixin_state()+"::::"+showRePay.getResult().getRefee()+"");
                    }
                }
                break;
            case R.id.button_backward:
                finish();
                break;
            case R.id.isCheck:
                if (serFlag) {
                    isCheck.setImageResource(R.drawable.check_background);
                    payInfo.setBackgroundColor(getResources().getColor(R.color.gray));
                    serFlag = false;
                } else {
                    isCheck.setImageResource(R.drawable.right);
                    payInfo.setBackgroundColor(getResources().getColor(R.color.themeColor));
                    serFlag = true;
                }
                break;
            case R.id.tiaokuan:
                Intent intent = new Intent();
                intent.setClass(BuKaoActivity.this,WebViewActivity.class);
                intent.putExtra("url","http://www.baixinxueche.com/webshow/chongzhi/bukaoPayAgreement.html ");
                intent.putExtra("title","百信学车补考支付协议");
                startActivity(intent);
                break;
            case R.id.aipay_layout:
                if (weixinFlag && unionFlag) {
                    weixin_isCheck.setImageResource(R.drawable.check_background);
                    union_isCheck.setImageResource(R.drawable.check_background);
                    weixinFlag = false;
                    unionFlag = false;
                    aipay_isCheck.setImageResource(R.drawable.right);
                    aipayflag = true;
                } else {
                        if (!aipayflag) {
                            aipay_isCheck.setImageResource(R.drawable.right);
                            weixin_isCheck.setImageResource(R.drawable.check_background);
                            union_isCheck.setImageResource(R.drawable.check_background);
                            weixinFlag = false;
                            unionFlag = false;
                            aipayflag = true;
                        }else {
                            aipay_isCheck.setImageResource(R.drawable.check_background);
                            aipayflag = false;
                        }
                }
                break;
            case R.id.weixin_layout:
                if (aipayflag && unionFlag) {
                    aipay_isCheck.setImageResource(R.drawable.check_background);
                    union_isCheck.setImageResource(R.drawable.check_background);
                    unionFlag = false;
                    aipayflag = false;
                    weixin_isCheck.setImageResource(R.drawable.right);
                    weixinFlag = true;
                } else {
                    if (!weixinFlag) {
                        weixin_isCheck.setImageResource(R.drawable.right);
                        aipay_isCheck.setImageResource(R.drawable.check_background);
                        union_isCheck.setImageResource(R.drawable.check_background);
                        unionFlag = false;
                        aipayflag = false;
                        weixinFlag = true;
                    } else {
                        weixin_isCheck.setImageResource(R.drawable.check_background);
                        weixinFlag = false;
                    }
                }
                break;
            case R.id.union_layout:
                if (aipayflag && weixinFlag) {
                    aipay_isCheck.setImageResource(R.drawable.check_background);
                    weixin_isCheck.setImageResource(R.drawable.check_background);
                    aipayflag = false;
                    weixinFlag = false;
                    union_isCheck.setImageResource(R.drawable.right);
                    unionFlag = true;
                } else {
                    if (!unionFlag) {
                        union_isCheck.setImageResource(R.drawable.right);
                        aipay_isCheck.setImageResource(R.drawable.check_background);
                        weixin_isCheck.setImageResource(R.drawable.check_background);
                        unionFlag = true;
                        aipayflag = false;
                        weixinFlag = false;
                    } else {
                        union_isCheck.setImageResource(R.drawable.check_background);
                        unionFlag = false;
                    }
                }
                break;
        }

    }
}
