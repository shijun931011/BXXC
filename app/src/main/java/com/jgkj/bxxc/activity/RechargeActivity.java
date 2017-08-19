package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.weixin.WXPay;
import com.jgkj.bxxc.adapter.RechargeAdapter;
import com.jgkj.bxxc.bean.ItemModel;
import com.jgkj.bxxc.bean.MyPayResult;
import com.jgkj.bxxc.bean.Recharge;
import com.jgkj.bxxc.bean.entity.WXEntity.WXEntity;
import com.jgkj.bxxc.tools.PayResult;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.jgkj.bxxc.tools.Urls;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.jgkj.bxxc.activity.union.RSAUtil.verify;

public class RechargeActivity extends Activity implements View.OnClickListener{
    private TextView title;
    private TextView recharge_protocol;     //充值协议
    private Button back;
    private int uid;
    private int moneyid;
    private String token;
    private RecyclerView recyclerView;
    private RechargeAdapter adapter;
    private boolean aipayflag = false, weixinFlag = false,unionFlag=false;
    private ImageView weixin_isCheck, aipay_isCheck,union_isCheck;
    private LinearLayout weixin_layout, aipay_layout,union_layout;
    private Button btn_recharge;
    private Recharge.Result result;
    ArrayList<ItemModel> list1 = new ArrayList<>();
    ArrayList<String> moneyIdList = new ArrayList<>();
    // 充值钱的一些信息
    private String CZmoneyUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/CZmoney";
    // 支付宝 支付 充值支付
    /*
     * uid   moneyId是http://www.baixinxueche.com/index.php/Home/Apitokenpt/CZmoney接口中的对应的moneyId与 money的关系
     * */
    private  String AlipayRechargeUrl="http://www.baixinxueche.com/index.php/Home/wxapppay/alipay";
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
        setContentView(R.layout.activity_recharge);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        InitView();
        getCZmoney(uid+"", token);
    }

    private void InitView(){
        title = (TextView) findViewById(R.id.text_title);
        recharge_protocol = (TextView) findViewById(R.id.recharge_protocol_txt);
        recharge_protocol.setOnClickListener(this);
        back = (Button) findViewById(R.id.button_backward);
        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        weixin_isCheck=(ImageView) findViewById(R.id.weixin_isCheck);
        aipay_isCheck=(ImageView) findViewById(R.id.aipay_isCheck);
        union_isCheck = (ImageView) findViewById(R.id.union_isCheck);
        btn_recharge = (Button) findViewById(R.id.btn_recharge);
        btn_recharge.setOnClickListener(this);
        aipay_layout = (LinearLayout) findViewById(R.id.aipay_layout);
        aipay_layout.setOnClickListener(this);
        weixin_layout = (LinearLayout) findViewById(R.id.weixin_layout);
        weixin_layout.setOnClickListener(this);
        union_layout = (LinearLayout) findViewById(R.id.union_layout);
        union_layout.setOnClickListener(this);
        title.setText("充值");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid",-1);
        token = intent.getStringExtra("token");
    }
    private void getCZmoney(String uid, String token){
        OkHttpUtils.post()
                .url(CZmoneyUrl)
                .addParams("uid", uid)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(RechargeActivity.this,"请检查网络", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        Recharge recharge = gson.fromJson(s, Recharge.class);
                        if (recharge.getCode() == 200){
                            List<Recharge.Result> list = recharge.getResult();
                            for (int k = 0; k < list.size(); k++){
                                int count = list.get(k).getMoney();
                                moneyIdList.add(""+list.get(k).getMoneyId());
                                list1.add(new ItemModel(ItemModel.ONE, count));
                            }
                            recyclerView.setAdapter(adapter = new RechargeAdapter());
                            adapter.replaceAll(list1);
                        }
                    }
                });
    }
    //支付宝充值
    private void AlipayRecharge( String uid_ ,String moneyId){
        OkHttpUtils.post()
                .url(AlipayRechargeUrl)
                .addParams("uid",uid_)
                .addParams("moneyId", moneyId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(RechargeActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        final MyPayResult myPayResult = gson.fromJson(s, MyPayResult.class);
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
                                            Intent intent = new Intent();
                                            intent.setClass(RechargeActivity.this,PayResultActivity.class);
                                            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                                            if (TextUtils.equals(resultStatus, "9000")) {
                                                Toast.makeText(RechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                                                intent.putExtra("result",2);
                                                intent.putExtra("uid", uid);
                                                intent.putExtra("moneyId", RechargeAdapter.positionIndex);
                                                RechargeAdapter.positionIndex=1001;
                                                startActivity(intent);
                                            } else {
                                                // 判断resultStatus 为非"9000"则代表可能支付失败
                                                // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                                                if (TextUtils.equals(resultStatus, "8000")) {
                                                    Toast.makeText(RechargeActivity.this, "支付结果确认中,请勿重新付款", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                                    Toast.makeText(RechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
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
                                    PayTask alipay = new PayTask(RechargeActivity.this);
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
                            Toast.makeText(RechargeActivity.this, myPayResult.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    //微信充值
    private void WXRecharge(String uid_ ,String moneyId){
        OkHttpUtils.post()
                .url(Urls.wxapppay)
                .addParams("uid",uid_)
                .addParams("moneyId", moneyId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(RechargeActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        WXEntity wxEntity = gson.fromJson(s, WXEntity.class);
                        if(wxEntity.getErrorCode() == 0){
                            WXPay wxpay = new WXPay(RechargeActivity.this, wxEntity.getResponseData().getApp_response().getAppid());
                            wxpay.doPay(s, new WXPay.WXPayResultCallBack() {
                                @Override
                                public void onSuccess() {
                                    //刷新支付余额
                                    Intent intent = new Intent();
                                    intent.setAction("updataBalance");
                                    sendBroadcast(intent);
                                    Toast.makeText(RechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(int error_code) {
                                    Toast.makeText(RechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancel() {
                                    Toast.makeText(RechargeActivity.this, "支付取消", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else{
                            Toast.makeText(RechargeActivity.this, wxEntity.getErrorMsg(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void unionRecharge(String uid_ ,String moneyId){
        OkHttpUtils.post()
                .url(Urls.unionRecharge)
                .addParams("uid",uid_)
                .addParams("moneyId", moneyId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(RechargeActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("百信学车","银联充值:"+s);
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
                        UPPayAssistEx.startPayByJAR(RechargeActivity.this, PayActivity.class,null,null,tn,mMode);
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
            msg = "充值成功！";
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "充值失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("充值结果通知");
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
        switch (view.getId()){
            case R.id.button_backward:
                finish();
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
                    } else {
                        aipay_isCheck.setImageResource(R.drawable.check_background);
                        aipayflag = false;
                    }
                }
                break;
            case R.id.weixin_layout:
                if (aipayflag  && unionFlag) {
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
                    } else {
                        union_isCheck.setImageResource(R.drawable.check_background);
                        unionFlag = false;
                    }
                }
                break;
            case R.id.btn_recharge:            //充值
                if (aipayflag == false &&weixinFlag == false && unionFlag == false){
                    Toast.makeText(RechargeActivity.this, "请选择充值方式", Toast.LENGTH_SHORT).show();
                }else {
                    if (RechargeAdapter.positionIndex ==1001){
                        Toast.makeText(RechargeActivity.this, "请选择充值金额", Toast.LENGTH_SHORT).show();
                    }else {
                        if (unionFlag){
                            unionRecharge(uid+"", moneyIdList.get(RechargeAdapter.positionIndex));
                        }else if (weixinFlag){
//                            /**
//                             * 微信支付
//                             * 微信支付常见坑
//                             * 1.微信开放平台的包名和签名是否和本地的一致
//                             * 2.服务器能拿到prepare_id,还是返回-1，查看调起支付接口时的签名是否计算正确
//                             * 3.能调起支付，没有返回消息的，请查看自己项目包下是否有（wxapi.WXPayEntryActivity）
//                             * 4.本地调试时一定要使用正式签名文件进行调试，否则是调不起微信支付窗口的
//                             * 5.网络上遇到说微信缓存会影响返回-1的，目前没有遇到过
//                             */
                            WXRecharge(uid+"", moneyIdList.get(RechargeAdapter.positionIndex));
                        }else if (aipayflag){
                            AlipayRecharge(uid+"", moneyIdList.get(RechargeAdapter.positionIndex));
                        }

                    }
                }
                break;
            case R.id.recharge_protocol_txt:
                Intent intent = new Intent();
                intent.setClass(RechargeActivity.this,WebViewActivity.class);
                intent.putExtra("url","http://www.baixinxueche.com/webshow/chongzhi/recharge.html");
                intent.putExtra("title","充值协议");
                startActivity(intent);
                break;

        }
    }
}
