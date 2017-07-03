package com.jgkj.bxxc.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.ForgetPayPasswordActivity;
import com.jgkj.bxxc.activity.SetPayPasswordActivity;
import com.jgkj.bxxc.bean.UserInfo;
import com.lmj.mypwdinputlibrary.InputPwdView;
import com.lmj.mypwdinputlibrary.MyInputPwdUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by shijun on 2017/5/23.
 */

public class SureRefundDialog implements View.OnClickListener{
    private Context context;
    private String content;
    private Dialog dialog, sureDialog;
    private View inflate, sureView;
    private MyInputPwdUtil myInputPwdUtil;
    private int length;
    private int uid;
    private String token;
    private String account;
    private String paypwd;
    private TextView dialog_textView, dialog_sure, dialog_cancel,dialog_bind;
    private UserInfo userInfo;
    private UserInfo.Result result;

    //余额退款   uid token   account 到账的银行卡号
    private String balanceRefundUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/refund";
    //余额退款   uid  token  paypwd   account 到账的银行卡号   200, 400, 600
//    private String balanceRefundNewUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/refundpwd";
    //余额退款    uid  token  paypwd  200, 400, 600
    private String balanceRefundNewUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenptchoose/refundpwdwp";

    private class Result {
        private int code;
        private String reason;

        public String getReason() {
            return reason;
        }

        public int getCode() {
            return code;
        }
    }

    public  SureRefundDialog(Context context, String content,int uid, String token, String account, MyInputPwdUtil myInputPwdUtil){
        this.content = content;
        this.context = context;
        this.uid = uid;
        this.token = token;
        this.account = account;
        this.myInputPwdUtil = myInputPwdUtil;
    }

    private void getBalanceRefund(String uid, String paypwd_, String token){
        Log.d("BXXC","百信学车："+uid+""+":::::::"+paypwd_+":::::::"+"::::"+token+"::::"+account+"::::"+Md5.md5(paypwd_)+"::::");
        OkHttpUtils.post()
                .url(balanceRefundNewUrl)
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("paypwd",Md5.md5(paypwd_))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(context,"请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("BXXC","百信学车退款："+s);
                        Gson gson = new Gson();
                        Result result = gson.fromJson(s, Result.class);
                        if (result.getCode() == 200){
                            updata();
                            Toast.makeText(context,result.getReason(),Toast.LENGTH_SHORT).show();
                            myInputPwdUtil.hide();
                        }
                        if (result.getCode() == 400){
                            Toast.makeText(context,result.getReason(),Toast.LENGTH_SHORT).show();
                            myInputPwdUtil.hide();
                        }
                        if (result.getCode() == 600){
                            Toast.makeText(context, result.getReason(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void SureRefund(){
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        inflate = LayoutInflater.from(context).inflate(R.layout.sure_idcard_dialog, null);
        // 初始化控件
        dialog_textView = (TextView) inflate.findViewById(R.id.dialog_textView);
        dialog_sure = (TextView) inflate.findViewById(R.id.dialog_sure);
        dialog_sure.setText("确定退款");
        dialog_cancel = (TextView) inflate.findViewById(R.id.dialog_cancel);
        dialog_cancel.setText("再想想");
        dialog_bind = (TextView) inflate.findViewById(R.id.dialog_card_imfo);
        dialog_bind.setVisibility(View.GONE);
//        length = account.length();
//        dialog_bind.setText("银行卡信息："+account.substring(length-5,length).replace(" ", ""));
        dialog_sure.setOnClickListener(this);
        dialog_cancel.setOnClickListener(this);
        dialog_textView.setText(content);
        // 将布局设置给Dialog
        dialog.setContentView(inflate);
        // 获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        // 设置dialog宽度
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.CENTER);
        dialog.show();// 显示对话框
    }

    public void show(View view){
        myInputPwdUtil.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog_sure:
                SharedPreferences sp= context.getSharedPreferences("paypwd", Activity.MODE_PRIVATE);
                // 使用getString方法获得value，注意第2个参数是value的默认值
                paypwd = sp.getString("paypwd", "");
                myInputPwdUtil = new MyInputPwdUtil(context);
                myInputPwdUtil.getMyInputDialogBuilder().setAnimStyle(R.style.dialog_anim);
                myInputPwdUtil.setListener(new InputPwdView.InputPwdListener() {
                    @Override
                    public void hide() {
                        myInputPwdUtil.hide();
                    }
                    @Override
                    public void forgetPwd() {
                        //设置支付密码
                        Intent intent = new Intent();
                        intent.setClass(context,ForgetPayPasswordActivity.class);
                        context.startActivity(intent);
                    }

                    @Override
                    public void finishPwd(String pwd) {
//                        Toast.makeText(context, pwd, Toast.LENGTH_SHORT).show();
                        getBalanceRefund(uid+"", pwd, token);
                    }
                });
                //判断是否设置支付密码
                if(paypwd != null && !"".equals(paypwd)){
                    myInputPwdUtil.show();
                }else{
                    //设置支付密码
                    Intent intent = new Intent();
                    intent.setClass(context,SetPayPasswordActivity.class);
                    context.startActivity(intent);
                }
                dialog.dismiss();
                break;
            case R.id.dialog_cancel:
                dialog.dismiss();
                break;
        }
    }

    //广播更新数据
    public void updata() {
        Intent intent = new Intent();
        intent.setAction("updataBalance");
        context.sendBroadcast(intent);
    }
}
