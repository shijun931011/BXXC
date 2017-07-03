package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.Balance;
import com.jgkj.bxxc.tools.ActivityRuleDialog;
import com.jgkj.bxxc.tools.RemainBaseDialog;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.jgkj.bxxc.tools.SureRefundDialog;
import com.lmj.mypwdinputlibrary.MyInputPwdUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;


/**
 * Created by fangzhou on 2017/3/27.
 * <p>
 * 我的设置--我的钱包
 */
public class Setting_AccountActivity extends Activity implements View.OnClickListener {
    private Button btn_back;
    private TextView title;
    private Button manage_band;      //管理银行卡
    private LinearLayout myCoupod;     //优惠劵
    private LinearLayout rehour;       //剩余学时
    private LinearLayout paydetail;    //支付明细
    private TextView balance_money;    //余额
    private TextView balance_explain;   //余额说明
    private TextView balance_deal;      //余额用不完怎么办
    private TextView refund_record;      //退款记录
    private Button btn_recharge;        //充值
    private Dialog balance_diolog;
    private View balance_view;
    private TextView dialog_textView, dialog_sure, dialog_cancel,dialog_prompt;
    private int uid;
    private String token;
    private String account;

    private MyInputPwdUtil myInputPwdUtil;
    private String balanceUrl="http://www.baixinxueche.com/index.php/Home/Apitokenpt/balance";

    //广播接收更新数据
    protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            getBalance(uid+"", token);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_account);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        initView();
        getData();
        getBalance(uid+"", token);

    }
    private void getData() {
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid",-1);
        token = intent.getStringExtra("token");
        SharedPreferences sp = getSharedPreferences("useraccount",Activity.MODE_PRIVATE);
        account = sp.getString("useraccount", null);
        myInputPwdUtil = new MyInputPwdUtil(this);
    }
    private void getBalance(String uid, String token){
        OkHttpUtils.post()
                .url(balanceUrl)
                .addParams("uid", uid)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(Setting_AccountActivity.this,"请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        Balance balance = gson.fromJson(s, Balance.class);
                        if (balance.getCode() == 200){
                            Balance.Result result = balance.getResult();
                            balance_money.setText("￥" + result.getBalance());
                        }
                    }
                });
    }
    private void initView() {
        btn_back = (Button) findViewById(R.id.button_backward);
        title = (TextView) findViewById(R.id.text_title);
        manage_band = (Button) findViewById(R.id.button_forward);
        manage_band.setVisibility(View.VISIBLE);
        manage_band.setText("管理银行卡");
        manage_band.setPadding(0,0,10,0);
        manage_band.setTextSize(14);
        manage_band.setOnClickListener(this);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        title.setText("我的钱包");
        myCoupod = (LinearLayout) findViewById(R.id.mycoupod);
        rehour = (LinearLayout) findViewById(R.id.re_hour);
        paydetail = (LinearLayout) findViewById(R.id.pay_detail);
        balance_money = (TextView) findViewById(R.id.balance_money);
        balance_explain = (TextView) findViewById(R.id.balance_explain);
        balance_deal = (TextView) findViewById(R.id.balance_deal);
        refund_record = (TextView) findViewById(R.id.refund_record);
        btn_recharge = (Button) findViewById(R.id.recharge);
        myCoupod.setOnClickListener(this);
        rehour.setOnClickListener(this);
        paydetail.setOnClickListener(this);
        balance_explain.setOnClickListener(this);
        btn_recharge.setOnClickListener(this);
        balance_deal.setOnClickListener(this);
        refund_record.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.button_forward:
                intent.setClass(Setting_AccountActivity.this,ManageBankCardActivity.class);
                startActivity(intent);
                break;
            case R.id.mycoupod:
                intent.setClass(Setting_AccountActivity.this,CouponActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("token", token);
                startActivity(intent);
                break;
            case R.id.re_hour:
                intent.setClass(this, RehourActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("token", token);
                startActivity(intent);
                break;
            case R.id.pay_detail:
                intent.setClass(this, PaydetailActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("token", token);
                startActivity(intent);
                break;
            case R.id.balance_explain:
                final ActivityRuleDialog.Builder dialog = new ActivityRuleDialog.Builder(this);
                dialog.setTitle("余额说明")
                        .setMessage("1、 账户余额仅在本平台购买学车学时套餐的时候使用。" +
                                "2、 您的消费将优先使用您充值的余额，后使用活动赠送的金额" +
                                "3、 若涉及到余额退款，本平台将收回活动赠送的余额。")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.create().show();
                break;
            case R.id.recharge:
                intent.setClass(this,RechargeActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("token", token);
                startActivity(intent);
                break;
            case R.id.balance_deal:
                balance_diolog = new Dialog(Setting_AccountActivity.this, R.style.ActionSheetDialogStyle);
                //填充对话框的布局
                balance_view = LayoutInflater.from(Setting_AccountActivity.this).inflate(R.layout.sure_cancel_dialog, null);
                dialog_textView = (TextView) balance_view.findViewById(R.id.dialog_textView);
                dialog_textView.setText("用不完的余额，可以在这里申请退款，百信学车竭诚为您服务！");
                dialog_prompt = (TextView) balance_view.findViewById(R.id.diolog_prompt);
                dialog_sure = (TextView) balance_view.findViewById(R.id.dialog_sure);
                dialog_sure.setText("我要退款");
                dialog_cancel = (TextView) balance_view.findViewById(R.id.dialog_cancel);
                dialog_cancel.setText("点错了");
                dialog_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sp = getSharedPreferences("useraccount",Activity.MODE_PRIVATE);
                        account = sp.getString("useraccount", null);
//                        if (account.equals("")) {
//                            balance_diolog.dismiss();
//                            new BindCardDialog(Setting_AccountActivity.this, "系统检测到您还没有绑定任何银行卡信息，" + "暂不能退款。是否去绑定？").Bindcard();
//                        }else{
                            if (balance_money.getText().toString().equals("￥0.00")) {
                                balance_diolog.dismiss();
                                new RemainBaseDialog(Setting_AccountActivity.this, "抱歉， " +
                                        "您目前的账户余额为零，暂不支持退款").call();
                            }else{
                                balance_diolog.dismiss();
                                new SureRefundDialog(Setting_AccountActivity.this,
                                        "退款将在1-7个工作日内退还到您充值时使用的账户上。", uid, token, account, myInputPwdUtil).SureRefund();
                            }
//                        }
                        }

                });
                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        balance_diolog.dismiss();
                    }
                });
                // 将布局设置给Dialog
                balance_diolog.setContentView(balance_view);
                // 获取当前Activity所在的窗体
                Window dialogWindow = balance_diolog.getWindow();
                // 设置dialog宽度
                dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                // 设置Dialog从窗体中间弹出
                dialogWindow.setGravity(Gravity.CENTER);
                balance_diolog.show();
                break;
           case R.id.refund_record:
            intent.setClass(this,BalanceRefundActivity.class);
            intent.putExtra("uid", uid);
            intent.putExtra("token", token);
            startActivity(intent);
            break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getBalance(uid+"", token);
    }

    public void onResume() {
        super.onResume();
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("updataBalance");
        registerReceiver(this.broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.broadcastReceiver);
    }
}
