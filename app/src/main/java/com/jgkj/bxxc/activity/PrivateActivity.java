package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.View;
import android.view.WindowManager;
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
import com.jgkj.bxxc.bean.MyPayResult;
import com.jgkj.bxxc.bean.PriBaokao;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.bean.entity.WXEntity.WXEntity;
import com.jgkj.bxxc.tools.PayResult;
import com.jgkj.bxxc.tools.RemainBaseDialog;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;

import static com.jgkj.bxxc.activity.union.RSAUtil.verify;


/*
私教班报名
 */
public class PrivateActivity extends Activity implements View.OnClickListener, TextWatcher {
    private Button payInfo;
    private Button back_backward;
    private Button btn_fordward;
    private TextView title;
    private TextView chooseTv;
    //服务条款
    private ImageView isCheck;
    private TextView tiaokuan;
    private boolean aipayflag = false, weixinFlag = false, unionFlag = false, aserFlg = false,
            isSuccess = false, yesFlag = true, noflag = false, test_hefei_flag = true,
            test_liuan_flag = false;
    private ImageView weixin_isCheck, aipay_isCheck, uppay_isCheck;
    private LinearLayout fuwutiaokuan;
    private LinearLayout layout4;  //推荐人布局
    private EditText tuijianren;
    //我的信息
    private String phone, idCard, name;
    private TextView phoneNo;
    private EditText username, userId;
    private LinearLayout weixin_layout, aipay_layout, uppay_layout;
    private TextView messageDeatil;
    private ImageView img_icar_true, img_icar_false, img_test_hefei, img_test_liuan;
    private TextView txt_icar_true, txt_icar_false, txt_test_hefei, txt_test_liuan;
    private List<PriBaokao.Result.Bmplace> list;
    private PriBaokao.Result.Bmplace result;
    //微信支付
    private IWXAPI api;
    private UserInfo userInfo;
    private UserInfo.Result useResult;
    private PriBaokao coachInfo;
    private TextView coach_Price;
    //银联
    /*****************************************************************
     * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
     *****************************************************************/
    private final String mMode = "00";
    //正式接口
    private String PripayUrl = "http://www.baixinxueche.com/index.php/Home/Aliapppay/aliBmPay";
    private String weipayUrl = "http://www.baixinxueche.com/index.php/Home/Aliapppay/wxBmPay";
    private String unionpay = "http://www.baixinxueche.com/index.php/Home/Unionpay/unionpay";
    //    private String privateUrl = "http://www.baixinxueche.com/index
    // .php/Home/Aliapppay/sijiaomoney";
    private String privateUrl = "http://www.baixinxueche.com/index.php/Home/Unionpay/bmplace";

    private int uid;
    private String token;
    private SharedPreferences sp;
    //    private CoachInfo.Result result;
    private String coach;
    private String mtcar;

    class Union {
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
        setContentView(R.layout.activity_private);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        uppay_layout = (LinearLayout) findViewById(R.id.uppay_layout);
        uppay_layout.setOnClickListener(this);
        uppay_isCheck = (ImageView) findViewById(R.id.uppay_isCheck);
        chooseTv = (TextView) findViewById(R.id.choose1);
//        chooseTv.setOnClickListener(this);
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
        img_icar_true = (ImageView) findViewById(R.id.img_icar_true);
        img_icar_false = (ImageView) findViewById(R.id.img_icar_false);
        img_test_hefei = (ImageView) findViewById(R.id.img_test_hefei);
        img_test_liuan = (ImageView) findViewById(R.id.img_test_liuan);
        img_icar_true.setOnClickListener(this);
        img_icar_false.setOnClickListener(this);
        img_test_hefei.setOnClickListener(this);
        img_test_liuan.setOnClickListener(this);
        txt_icar_true = (TextView) findViewById(R.id.txt_icar_true);
        txt_icar_false = (TextView) findViewById(R.id.txt_icar_false);
        txt_test_hefei = (TextView) findViewById(R.id.txt_test_hefei);
        txt_test_liuan = (TextView) findViewById(R.id.txt_test_liuan);
    }

    private void getData() {
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
                Log.d("百信学车", "私教班报名费" + s);
                Gson gson = new Gson();
                coachInfo = gson.fromJson(s, PriBaokao.class);
                if (coachInfo.getCode() == 200) {
                    list = coachInfo.getResult().getBmplace();
                    if (list.get(0).getPlace().equals("合肥")) {
                        coach_Price.setText("总金额：￥" + list.get(0).getMoney());
                    } else if (list.get(1).getPlace().equals("六安")) {
                        coach_Price.setText("总金额：￥" + list.get(1).getMoney());
                    }
                }
            }
        });
    }

    /**
     * 报名信息
     */
    private boolean check() {
        name = username.getText().toString().trim();
        idCard = userId.getText().toString().trim();
        if ((name.equals("") || name == null) && (idCard.equals("") || idCard == null) &&
                (yesFlag == false && noflag == false) && (test_hefei_flag == false &&
                test_liuan_flag == false)) {
            Toast.makeText(PrivateActivity.this, "填写信息不完整！", Toast.LENGTH_SHORT).show();
            payInfo.setEnabled(false);
            payInfo.setBackgroundColor(getResources().getColor(R.color.gray));
            isSuccess = false;
        } else {
            payInfo.setBackgroundColor(getResources().getColor(R.color.themeColor));
            payInfo.setEnabled(true);
            isCheck.setImageResource(R.drawable.right);
            isSuccess = true;
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
    private void sendaiPay(String uid, String name, String phone, String idcard, String mtcar,
                           String exam_place) {
        Log.d("BXXC", "百信学车支付宝参数:" + uid + "::::" + name + "::::" + phone + "::::" + idcard +
                "::::" + "mtcar:" + mtcar + "exam_place:" + exam_place);
        OkHttpUtils.post().url(PripayUrl).addParams("uid", uid).addParams("name", name).addParams
                ("phone", phone).addParams("idcard", idcard).addParams("class_type", "2")
                .addParams("invite", "0").addParams("mtcar", mtcar).addParams("exam_place",
                exam_place).addParams("tuijianren", tuijianren.getText().toString()).build()
                .execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                Toast.makeText(PrivateActivity.this, "加载失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(final String s, int i) {
                Log.d("百信学车", "支付宝" + s);
                Gson gson = new Gson();
                final MyPayResult myPayResult = gson.fromJson(s, MyPayResult.class);
                if (myPayResult.getCode().equals("200")) {
                    final int SDK_PAY_FLAG = 1;
                    final Handler mHandler = new Handler() {
                        @SuppressWarnings("unused")
                        public void handleMessage(Message msg) {
                            switch (msg.what) {
                                case SDK_PAY_FLAG: {
                                    PayResult payResult = new PayResult((String) msg.obj);
                                    /**
                                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay
                                     * .com/doc2/
                                     * detail
                                     * .htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
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
                                        if (test_hefei_flag == true) {
                                            intent.putExtra("price", list.get(0).getMoney());
                                            Log.d("百信学车", "合肥总金额支付宝：" + list.get(0).getMoney());
                                        } else if (test_liuan_flag == true) {
                                            intent.putExtra("price", list.get(1).getMoney());
                                            Log.d("百信学车", "六安总金额支付宝：" + list.get(1).getMoney());
                                        }
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
                } else if (myPayResult.getCode().equals("400")) {
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
    private void weixinpay(String uid, String name, String phone, String idcard, String mtcar,
                           String exam_place) {
        Log.d("百信学车", "百信学车微信参数:" + uid + "::::" + name + "::::" + phone + "::::" + idcard +
                "::::" + "mtcar:" + mtcar + "::::" + exam_place);
        OkHttpUtils.post().url(weipayUrl).addParams("uid", uid).addParams("name", name).addParams
                ("phone", phone).addParams("idcard", idcard).addParams("class_type", "2")
                .addParams("invite", "0").addParams("mtcar", mtcar).addParams("exam_place",
                exam_place).addParams("tuijianren", tuijianren.getText().toString()).build()
                .execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                Toast.makeText(PrivateActivity.this, "加载失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(String s, int i) {
                Log.d("百信学车", "微信支付" + s);
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
                            successIntent.setClass(PrivateActivity.this,
                                    RegisterDetailActivity2.class);
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

    //银联支付
    private void unionPay(String uid, String name, String phone, String idcard, String mtcar,
                          String exam_place) {
        Log.d("百信学车", "银联参数值:" + uid + "name:" + name + "phone:" + idcard + "mtcar:" + mtcar +
                "exam_place:" + exam_place);
        OkHttpUtils.post().url(unionpay).addParams("uid", uid).addParams("name", name).addParams
                ("phone", phone).addParams("idcard", idcard).addParams("mtcar", mtcar).addParams
                ("exam_place", exam_place).addParams("class_type", "2").addParams("invite", "0")
                .addParams("tuijianren", tuijianren.getText().toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                Toast.makeText(PrivateActivity.this, "网络加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String s, int i) {
                Log.d("百信学车", "银联支付" + s);
//                        //2.解析服务器返回的流水号
//                        String tn = s;
                //3.调用银联sdk,传入流水号
                /**
                 * tn:交易流水号
                 * mode："00"启动银联正式环境 ,"01"连接银联测试环境（可以使用测试账号，测试账号参阅文档）
                 */
                Gson gson = new Gson();
                Union union = gson.fromJson(s, Union.class);
                if (union.getCode().equals("200")) {
                    String tn = union.getResult();
                    UPPayAssistEx.startPayByJAR(PrivateActivity.this, PayActivity.class, null,
                            null, tn, mMode);
                } else if (union.getCode().equals("400")) {
                    Toast.makeText(PrivateActivity.this, union.getReason(), Toast.LENGTH_SHORT)
                            .show();
                }

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
            //支付成功跳转到提交个人信息照片页面
            Intent successIntent = new Intent();
            successIntent.setClass(PrivateActivity.this, RegisterDetailActivity2.class);
            //successIntent.putExtra("uid",uid);
            startActivity(successIntent);
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
//                //支付成功跳转到提交个人信息照片页面
//                Intent successIntent = new Intent();
//                successIntent.setClass(PrivateActivity.this, RegisterDetailActivity2.class);
//                //successIntent.putExtra("uid",uid);
//                startActivity(successIntent);
            }
        });
        builder.create().show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payInfo:
                if (check() == true) {
                    if (aipayflag == false && weixinFlag == false && unionFlag == false) {
                        Toast.makeText(PrivateActivity.this, "请选择支付方式", Toast.LENGTH_SHORT).show();
                    } else {
                        if (unionFlag == true) {
                            if (yesFlag == true && test_hefei_flag == true) {
                                unionPay(useResult.getUid() + "", username.getText().toString()
                                        .trim(), phoneNo.getText().toString().trim(), userId
                                        .getText().toString().trim(), txt_icar_true.getText()
                                        .toString().trim(), txt_test_hefei.getText().toString()
                                        .trim());
                            } else if (yesFlag == true && test_liuan_flag == true) {
                                unionPay(useResult.getUid() + "", username.getText().toString()
                                        .trim(), phoneNo.getText().toString().trim(), userId
                                        .getText().toString().trim(), txt_icar_true.getText()
                                        .toString().trim(), txt_test_liuan.getText().toString()
                                        .trim());
                            } else if (noflag == true && test_hefei_flag == true) {
                                unionPay(useResult.getUid() + "", username.getText().toString()
                                        .trim(), phoneNo.getText().toString().trim(), userId
                                        .getText().toString().trim(), txt_icar_false.getText()
                                        .toString().trim(), txt_test_hefei.getText().toString()
                                        .trim());
                            } else if (noflag == true && test_liuan_flag == true) {
                                unionPay(useResult.getUid() + "", username.getText().toString()
                                        .trim(), phoneNo.getText().toString().trim(), userId
                                        .getText().toString().trim(), txt_icar_false.getText()
                                        .toString().trim(), txt_test_liuan.getText().toString()
                                        .trim());
                            }
                        } else if (weixinFlag == true) {
                            if (yesFlag == true && test_hefei_flag == true) {
                                weixinpay(useResult.getUid() + "", username.getText().toString()
                                        .trim(), phoneNo.getText().toString().trim(), userId
                                        .getText().toString().trim(), txt_icar_true.getText()
                                        .toString().trim(), txt_test_hefei.getText().toString()
                                        .trim());
                            } else if (yesFlag == true && test_liuan_flag == true) {
                                weixinpay(useResult.getUid() + "", username.getText().toString()
                                        .trim(), phoneNo.getText().toString().trim(), userId
                                        .getText().toString().trim(), txt_icar_true.getText()
                                        .toString().trim(), txt_test_liuan.getText().toString()
                                        .trim());
                            } else if (noflag == true && test_hefei_flag == true) {
                                weixinpay(useResult.getUid() + "", username.getText().toString()
                                        .trim(), phoneNo.getText().toString().trim(), userId
                                        .getText().toString().trim(), txt_icar_false.getText()
                                        .toString().trim(), txt_test_hefei.getText().toString()
                                        .trim());
                            } else if (noflag == true && test_liuan_flag == true) {
                                weixinpay(useResult.getUid() + "", username.getText().toString()
                                        .trim(), phoneNo.getText().toString().trim(), userId
                                        .getText().toString().trim(), txt_icar_false.getText()
                                        .toString().trim(), txt_test_liuan.getText().toString()
                                        .trim());
                            }

                        } else if (aipayflag == true) {
                            if (yesFlag == true && test_hefei_flag == true) {
                                sendaiPay(useResult.getUid() + "", username.getText().toString()
                                        .trim(), phoneNo.getText().toString().trim(), userId
                                        .getText().toString().trim(), txt_icar_true.getText()
                                        .toString().trim(), txt_test_hefei.getText().toString()
                                        .trim());
                            } else if (yesFlag == true && test_liuan_flag == true) {
                                sendaiPay(useResult.getUid() + "", username.getText().toString()
                                        .trim(), phoneNo.getText().toString().trim(), userId
                                        .getText().toString().trim(), txt_icar_true.getText()
                                        .toString().trim(), txt_test_liuan.getText().toString()
                                        .trim());
                            } else if (noflag == true && test_hefei_flag == true) {
                                sendaiPay(useResult.getUid() + "", username.getText().toString()
                                        .trim(), phoneNo.getText().toString().trim(), userId
                                        .getText().toString().trim(), txt_icar_false.getText()
                                        .toString().trim(), txt_test_hefei.getText().toString()
                                        .trim());
                            } else if (noflag == true && test_liuan_flag == true) {
                                sendaiPay(useResult.getUid() + "", username.getText().toString()
                                        .trim(), phoneNo.getText().toString().trim(), userId
                                        .getText().toString().trim(), txt_icar_false.getText()
                                        .toString().trim(), txt_test_liuan.getText().toString()
                                        .trim());
                            }
                        }
                    }
                }
                break;
            case R.id.button_backward:
                finish();
                break;
            case R.id.button_forward:
                new RemainBaseDialog(PrivateActivity.this,
                        "此报名费用包括当地车管所收取的各科目考试费用和平台为您提供服务的基本服务费、" + "体检费、学时卡费，考试车辆接送费用等" +
                                "不包括科目二、科目三的私教训练费用及挂科补考费。" + "如需平台提供科二、科三的训练，在底部栏“我的”— " +
                                "“我的钱包”—“剩余学时”" + "前往购买相应的私教训练套餐即可,该条例的最终解释权归平台所有").call();
                break;
            case R.id.isCheck:
                if (aserFlg) {
                    isCheck.setImageResource(R.drawable.check_background);
                    payInfo.setBackgroundColor(getResources().getColor(R.color.gray));
                    aserFlg = false;
                } else {
                    check();
                    isCheck.setImageResource(R.drawable.right);
                    payInfo.setBackgroundColor(getResources().getColor(R.color.themeColor));
                    aserFlg = true;
                }
                break;
            case R.id.tiaokuan:
                Intent intent = new Intent();
                intent.setClass(PrivateActivity.this, WebViewActivity.class);
                intent.putExtra("url", "http://www.baixinxueche" + "" +
                        ".com/webshow/chongzhi/sijiaoPayAgreement.html ");
                intent.putExtra("title", "百信学车服务条款");
                startActivity(intent);
                break;
            case R.id.aipay_layout:
                if (weixinFlag && unionFlag) {
                    weixin_isCheck.setImageResource(R.drawable.check_background);
                    uppay_isCheck.setImageResource(R.drawable.check_background);
                    weixinFlag = false;
                    unionFlag = false;
                    aipay_isCheck.setImageResource(R.drawable.right);
                    aipayflag = true;
                } else {
                    if (!aipayflag) {
                        aipay_isCheck.setImageResource(R.drawable.right);
                        weixin_isCheck.setImageResource(R.drawable.check_background);
                        uppay_isCheck.setImageResource(R.drawable.check_background);
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
                if (aipayflag && unionFlag) {
                    aipay_isCheck.setImageResource(R.drawable.check_background);
                    uppay_isCheck.setImageResource(R.drawable.check_background);
                    unionFlag = false;
                    aipayflag = false;
                    weixin_isCheck.setImageResource(R.drawable.right);
                    weixinFlag = true;
                } else {
                    if (!weixinFlag) {
                        weixin_isCheck.setImageResource(R.drawable.right);
                        aipay_isCheck.setImageResource(R.drawable.check_background);
                        uppay_isCheck.setImageResource(R.drawable.check_background);
                        unionFlag = false;
                        aipayflag = false;
                        weixinFlag = true;
                    } else {
                        weixin_isCheck.setImageResource(R.drawable.check_background);
                        weixinFlag = false;
                    }
                }
                break;
            case R.id.uppay_layout:
                if (aipayflag && weixinFlag) {
                    aipay_isCheck.setImageResource(R.drawable.check_background);
                    weixin_isCheck.setImageResource(R.drawable.check_background);
                    aipayflag = false;
                    weixinFlag = false;
                    uppay_isCheck.setImageResource(R.drawable.right);
                    unionFlag = true;
                } else {
                    if (!unionFlag) {
                        uppay_isCheck.setImageResource(R.drawable.right);
                        aipay_isCheck.setImageResource(R.drawable.check_background);
                        weixin_isCheck.setImageResource(R.drawable.check_background);
                        aipayflag = false;
                        weixinFlag = false;
                        unionFlag = true;
                    } else {
                        uppay_isCheck.setImageResource(R.drawable.check_background);
                        unionFlag = false;
                    }
                }
                break;
            case R.id.img_icar_true:
                if (noflag) {
                    img_icar_false.setImageResource(R.drawable.check_background);
                    noflag = false;
                    img_icar_true.setImageResource(R.drawable.circle_selected_image);
                    yesFlag = true;
                } else {
                    if (yesFlag) {
                        img_icar_true.setImageResource(R.drawable.circle_selected_image);
                        img_icar_false.setImageResource(R.drawable.check_background);
                        noflag = false;
                        yesFlag = true;
                    } else {
                        img_icar_true.setImageResource(R.drawable.check_background);
                        yesFlag = false;
                    }
                }
                break;
            case R.id.img_icar_false:
                if (!yesFlag) {
                    img_icar_true.setImageResource(R.drawable.check_background);
                    yesFlag = false;
                    img_icar_false.setImageResource(R.drawable.circle_selected_image);
                    noflag = true;
                } else {
                    if (yesFlag) {
                        img_icar_false.setImageResource(R.drawable.circle_selected_image);
                        img_icar_true.setImageResource(R.drawable.check_background);
                        noflag = true;
                        yesFlag = false;
                    } else {
                        img_icar_false.setImageResource(R.drawable.check_background);
                        noflag = false;
                    }
                }
                break;
            case R.id.img_test_hefei:
                coach_Price.setText("总金额：￥" + list.get(0).getMoney());
                if (test_liuan_flag) {
                    img_test_liuan.setImageResource(R.drawable.check_background);
                    test_liuan_flag = false;
                    img_test_hefei.setImageResource(R.drawable.circle_selected_image);
                    test_hefei_flag = true;
                } else {
                    if (!test_hefei_flag) {
                        img_test_hefei.setImageResource(R.drawable.check_background);
                        test_hefei_flag = false;
                        img_test_liuan.setImageResource(R.drawable.circle_selected_image);
                        test_liuan_flag = true;
                    } else {
                        img_test_hefei.setImageResource(R.drawable.circle_selected_image);
                        test_hefei_flag = true;
                    }
                }
                break;
            case R.id.img_test_liuan:
                coach_Price.setText("总金额：￥" + list.get(1).getMoney());
                if (test_hefei_flag) {
                    img_test_hefei.setImageResource(R.drawable.check_background);
                    test_hefei_flag = false;
                    img_test_liuan.setImageResource(R.drawable.circle_selected_image);
                    test_liuan_flag = true;
                } else {
                    if (!test_liuan_flag) {
                        img_test_liuan.setImageResource(R.drawable.circle_selected_image);
                        test_liuan_flag = true;
                        img_test_hefei.setImageResource(R.drawable.check_background);
                        test_hefei_flag = false;
                    } else {
                        img_test_liuan.setImageResource(R.drawable.check_background);
                        test_liuan_flag = false;
                    }
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
