package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.weixin.WXPay;
import com.jgkj.bxxc.bean.CoachInfo;
import com.jgkj.bxxc.bean.MyPayResult;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.bean.entity.WXEntity.WXEntity;
import com.jgkj.bxxc.tools.PayResult;
import com.jgkj.bxxc.tools.RemainBaseDialog;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class PrivateActivity extends Activity implements View.OnClickListener, TextWatcher {
    private Button payInfo;
    private Button back_backward;
    private Button btn_fordward;
    private TextView title;
    private TextView chooseTv;
    //服务条款
    private ImageView isCheck;
    private TextView tiaokuan;
    private boolean aipayflag = false, weixinFlag = false, aserFlg = false, chooseFlag = false;
    private ImageView weixin_isCheck, aipay_isCheck;
    private LinearLayout fuwutiaokuan;
    private LinearLayout layout4;  //推荐人布局
    private EditText tuijianren;
    private Dialog dialog;
    private View inflate;
    private TextView dialog_yes, dialog_no, dialog_cancel;
    //我的信息
    private String phone, idCard, name;
    private TextView phoneNo;
    private EditText username, userId;
    private LinearLayout weixin_layout, aipay_layout;
    private TextView messageDeatil;
    //微信支付
    private IWXAPI api;
    private UserInfo userInfo;
    private UserInfo.Result useResult;
    private PriBaokao coachInfo;
    private TextView coach_Price;
    //正式接口
//    private String PripayUrl = "http://www.baixinxueche.com/index.php/Home/Aliapppay/payInviter";
//    private String weipayUrl = "http://www.baixinxueche.com/index.php/Home/Aliapppay/wxpay";
    private String privateUrl = "http://www.baixinxueche.com/index.php/Home/Aliapppay/sijiaomoney";

    //测试接口
    private String PripayUrl = "http://www.baixinxueche.com/index" +
            ".php/Home/Aliapppay/payInviterExam";
    private String weipayUrl = "http://www.baixinxueche.com/index.php/Home/Aliapppay/wxpayExam ";
    private int uid;
    private String token;
    private int pack = 1;
    private SharedPreferences sp;
    private CoachInfo.Result result;
    private String coach;
    private String mtcar;

    class PriBaokao {
        private int money;

        public int getMoney() {
            return money;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private);
        InitView();
        getData();
        getPriData(uid + "", token);
    }

    private void InitView() {
        //标题栏
        back_backward = (Button) findViewById(R.id.button_backward);
        back_backward.setVisibility(View.VISIBLE);
        back_backward.setOnClickListener(this);
        title = (TextView) findViewById(R.id.text_title);
        title.setText("私教班报名");
        btn_fordward = (Button) findViewById(R.id.button_forward);
        btn_fordward.setVisibility(View.VISIBLE);
        btn_fordward.setText("报名须知");
        layout4 = (LinearLayout) findViewById(R.id.layout4);
        tuijianren = (EditText) findViewById(R.id.tuijianren);
        //我的信息
        username = (EditText) findViewById(R.id.signUpName);
        userId = (EditText) findViewById(R.id.idCard);
        phoneNo = (TextView) findViewById(R.id.signUpPhone);
        payInfo = (Button) findViewById(R.id.payInfo);
        aipay_layout = (LinearLayout) findViewById(R.id.aipay_layout);
        aipay_isCheck = (ImageView) findViewById(R.id.aipay_isCheck);
        aipay_layout.setOnClickListener(this);
        weixin_layout = (LinearLayout) findViewById(R.id.weixin_layout);
        weixin_isCheck = (ImageView) findViewById(R.id.weixin_isCheck);
        weixin_layout.setOnClickListener(this);
        chooseTv = (TextView) findViewById(R.id.choose);
        chooseTv.setOnClickListener(this);
        messageDeatil = (TextView) findViewById(R.id.messageDetail);
        String source = "报名信息&nbsp &nbsp &nbsp &nbsp<font color='#FF8000'>注：请确保以下填写信息真实有效</font>";
        messageDeatil.setText(Html.fromHtml(source));
        messageDeatil.setTextSize(12);
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

    private void getData() {
        Intent intent = getIntent();
        sp = getApplication().getSharedPreferences("USER", Activity.MODE_PRIVATE);
        String str = sp.getString("userInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str, UserInfo.class);
        useResult = userInfo.getResult();
        SharedPreferences sp1 = getApplication().getSharedPreferences("token", Activity
                .MODE_PRIVATE);
        token = sp1.getString("token", null);
        if (str == null || sp == null) {
            Intent login = new Intent(PrivateActivity.this, LoginActivity.class);
            login.putExtra("message", "payInfo");
            startActivity(login);
            finish();
        } else {
            phoneNo.setText(useResult.getPhone() + "");
        }
    }

    private void getPriData(String uid, String token) {
        OkHttpUtils.post().url(privateUrl).addParams("uid", uid).addParams("token", token).build
                ().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                Toast.makeText(PrivateActivity.this, "加载失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(String s, int i) {
                Log.d("shijun", "HHHH:" + s);
                Gson gson = new Gson();
                coachInfo = gson.fromJson(s, PriBaokao.class);
                coach_Price.setText("总金额：￥" + coachInfo.getMoney());
            }
        });
    }

    /**
     * 报名信息
     */
    private boolean check() {
        name = username.getText().toString().trim();
        idCard = userId.getText().toString().trim();
        boolean isSuccess = false;
        if (name.equals("") || name == null || idCard.equals("") || idCard == null || chooseFlag
                == false) { // || serFlag == false
            payInfo.setBackgroundColor(getResources().getColor(R.color.gray));
            payInfo.setEnabled(false);
            isSuccess = false;
            Toast.makeText(PrivateActivity.this, "填写信息不完整！", Toast.LENGTH_SHORT).show();
        } else if (name != null && idCard != null && idCard.length() == 18 && chooseFlag == true)
        {  //&& serFlag == true
            payInfo.setEnabled(true);
            payInfo.setBackgroundColor(getResources().getColor(R.color.themeColor));
            isSuccess = true;
            isCheck.setImageResource(R.drawable.right);
        }
        return isSuccess;
    }

    /**
     * 支付宝支付
     *
     * @param uid    用户id
     * @param name   用户姓名
     * @param phone  用户手机号
     * @param idcard 用户身份证号
     */
    private void sendaiPay(String uid, String name, String phone, String idcard, int pack, String
            mtcar) {
        Log.d("BXXC", "百信学车支付宝:" + uid + "::::" + name + "::::" + phone + "::::" + idcard +
                "::::" + "1" + chooseTv.getText().toString() + "::::" + tuijianren.getText()
                .toString());
        OkHttpUtils.post().url(PripayUrl).addParams("uid", uid).addParams("name", name).addParams
                ("phone", phone).addParams("idcard", idcard).addParams("pt", "1").addParams
                ("mtcar", chooseTv.getText().toString()).addParams("tuijianren", tuijianren
                .getText().toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                Toast.makeText(PrivateActivity.this, "加载失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(final String s, int i) {
                Log.d("shijun", "ddd" + s);
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
                                    intent.setClass(PrivateActivity.this, PayResultActivity.class);
                                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                                    if (TextUtils.equals(resultStatus, "9000")) {
                                        Toast.makeText(PrivateActivity.this, "支付成功", Toast
                                                .LENGTH_SHORT).show();
                                        intent.putExtra("result", 1);
                                        intent.putExtra("uid", useResult.getUid());
                                        intent.putExtra("price", coachInfo.getMoney());
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // 判断resultStatus 为非"9000"则代表可能支付失败
                                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                                        if (TextUtils.equals(resultStatus, "8000")) {
                                            Toast.makeText(PrivateActivity.this, "支付结果确认中," +
                                                    "请勿重新付款", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                            Toast.makeText(PrivateActivity.this, "支付失败", Toast
                                                    .LENGTH_SHORT).show();
                                            intent.putExtra("result", 0);
                                            intent.putExtra("uid", useResult.getUid());
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
                } else if (myPayResult.getCode() == 400) {
                    Toast.makeText(PrivateActivity.this, myPayResult.getReason(), Toast
                            .LENGTH_LONG).show();
                }
            }
        });
    }

    //微信支付

    /**
     * uid  name  idcard  cid  phone  invite 邀请（选填）    tuijianren
     */
    private void weixinpay(String uid, String name, String phone, String idcard, int pack, String
            mtcar) {
        Log.d("BXXC", "百信学车微信:" + uid + "::::" + name + "::::" + phone + "::::" + idcard + "::::"
                + "1" + chooseTv.getText().toString() + "::::" + tuijianren.getText().toString());
        OkHttpUtils.post().url(weipayUrl).addParams("uid", uid).addParams("name", name).addParams
                ("phone", phone).addParams("idcard", idcard).addParams("pt", "1").addParams
                ("mtcar", chooseTv.getText().toString()).addParams("tuijianren", tuijianren
                .getText().toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                Toast.makeText(PrivateActivity.this, "加载失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(String s, int i) {
                Log.d("BXXC", "微信支付" + s);
                Gson gson = new Gson();
                WXEntity wxEntity = gson.fromJson(s, WXEntity.class);
                if (wxEntity.getErrorCode() == 0) {
                    WXPay wxpay = new WXPay(PrivateActivity.this, wxEntity.getResponseData()
                            .getApp_response().getAppid());
                    wxpay.doPay(s, new WXPay.WXPayResultCallBack() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(PrivateActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                            //支付成功跳转到提交个人信息照片页面
                            Intent successIntent = new Intent();
                            successIntent.setClass(PrivateActivity.this, RegisterDetailActivity2
                                    .class);
                            //successIntent.putExtra("uid",uid);
                            startActivity(successIntent);
                        }

                        @Override
                        public void onError(int error_code) {
                            Toast.makeText(PrivateActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(PrivateActivity.this, "支付取消", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(PrivateActivity.this, wxEntity.getErrorMsg(), Toast
                            .LENGTH_LONG).show();
                }
            }
        });
    }

    public void choose() {
        dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        inflate = LayoutInflater.from(this).inflate(R.layout.sure_choose_dialog, null);
        //控件
        dialog_yes = (TextView) inflate.findViewById(R.id.dialog_yes);
        dialog_no = (TextView) inflate.findViewById(R.id.dialog_no);
        dialog_cancel = (TextView) inflate.findViewById(R.id.dialog_cancel);
        dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseTv.setText("是");
                dialog.dismiss();
            }
        });
        dialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chooseTv.setText("否");
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        // 将布局设置给Dialog
        dialog.setContentView(inflate);
        // 获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        // 设置dialog宽度
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup
                .LayoutParams.WRAP_CONTENT);
        // 设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.CENTER);
        dialog.show();// 显示对话框
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payInfo:
                if (check() == true) {
                    if (aipayflag == false && weixinFlag == false) {
                        Toast.makeText(PrivateActivity.this, "请选择支付方式", Toast.LENGTH_SHORT).show();
                    } else {
                        if (weixinFlag) {
                            weixinpay(useResult.getUid() + "", username.getText().toString().trim
                                    (), phoneNo.getText().toString().trim(), userId.getText()
                                    .toString().trim(), pack, chooseTv.getText().toString().trim());
                        } else {
                            sendaiPay(useResult.getUid() + "", username.getText().toString().trim
                                    (), phoneNo.getText().toString().trim(), userId.getText()
                                    .toString().trim(), pack, chooseTv.getText().toString().trim());
                        }
                    }
                }
                break;
            case R.id.button_backward:
                finish();
                break;
            case R.id.button_forward:
                new RemainBaseDialog(PrivateActivity.this,
                         "此报名费用包括当地车管所收取的各科目考试费用和平台为您提供服务的基本服务费、" +
                         "体检费、学时卡费，考试车辆接送费用等" +
                         "不包括科目二、科目三的私教训练费用及挂科补考费。" +
                         "如需平台提供科二、科三的训练，在底部栏“我的”— “我的钱包”—“剩余学时”"+
                        "前往购买相应的私教训练套餐即可,该条例的最终解释权归平台所有").call();
                break;
            case R.id.isCheck:
                name = username.getText().toString().trim();
                idCard = userId.getText().toString().trim();
                if (name.equals("") || name == null || idCard.equals("") || idCard == null ||
                        chooseFlag == false) { // || serFlag == false
                    Toast.makeText(PrivateActivity.this, "填写信息不完整！", Toast.LENGTH_SHORT).show();
                    isCheck.setImageResource(R.drawable.check_background);
                    aserFlg = false;
                } else {
                    isCheck.setImageResource(R.drawable.right);
                    aserFlg = true;
                }
                break;
            case R.id.tiaokuan:
                Intent intent = new Intent();
                intent.setClass(PrivateActivity.this, WebViewActivity.class);
                intent.putExtra("url", "http://www.baixinxueche" + "" +
                        ".com/webshow/chongzhi/sijiaoPayAgreement.html ");
                intent.putExtra("title", "百信学车补考支付协议");
                startActivity(intent);
                break;
            case R.id.aipay_layout:
                if (weixinFlag) {
                    weixin_isCheck.setImageResource(R.drawable.check_background);
                    weixinFlag = false;
                    aipay_isCheck.setImageResource(R.drawable.right);
                    aipayflag = true;
                } else {
                    if (!aipayflag) {
                        aipay_isCheck.setImageResource(R.drawable.right);
                        aipayflag = true;
                    } else {
                        aipay_isCheck.setImageResource(R.drawable.check_background);
                        aipayflag = false;
                    }
                }
                break;
            case R.id.weixin_layout:
                if (aipayflag) {
                    aipay_isCheck.setImageResource(R.drawable.check_background);
                    aipayflag = false;
                    weixin_isCheck.setImageResource(R.drawable.right);
                    weixinFlag = true;
                } else {
                    if (!weixinFlag) {
                        weixin_isCheck.setImageResource(R.drawable.right);
                        weixinFlag = true;
                    } else {
                        weixin_isCheck.setImageResource(R.drawable.check_background);
                        weixinFlag = false;
                    }
                }
                break;
            case R.id.choose:
                if (!chooseFlag) {
                    choose();
                    chooseFlag = true;
                } else {
                    chooseFlag = false;
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
