package com.jgkj.bxxc.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
    private int uid;
    private String token;
    private String account;
    private TextView dialog_textView, dialog_sure, dialog_cancel,dialog_bind;
    //余额退款   uid token   account 到账的银行卡号
    private String balanceRefundUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/refund";

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

    public  SureRefundDialog(Context context, String content,int uid, String token, String account){
        this.content = content;
        this.context = context;
        this.uid = uid;
        this.token = token;
        this.account = account;
    }

    private void getBalanceRefund(String uid, String token, String account){
        OkHttpUtils.post()
                .url(balanceRefundUrl)
                .addParams("uid", uid)
                .addParams("token", token)
                .addParams("account",account)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(context,"请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("shijun","退款："+s);
                        Gson gson = new Gson();
                        Result result = gson.fromJson(s, Result.class);
                        if (result.getCode() == 200){
                            updata();
                            Toast.makeText(context,result.getReason(),Toast.LENGTH_SHORT).show();
                        }
                        if (result.getCode() == 400){
                            Toast.makeText(context,result.getReason(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void SureRefund(){
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        inflate = LayoutInflater.from(context).inflate(R.layout.sure_cancel_dialog, null);
        // 初始化控件
        dialog_textView = (TextView) inflate.findViewById(R.id.dialog_textView);
        dialog_sure = (TextView) inflate.findViewById(R.id.dialog_sure);
        dialog_sure.setText("确定退款");
        dialog_cancel = (TextView) inflate.findViewById(R.id.dialog_cancel);
        dialog_cancel.setText("再想想");
        dialog_bind = (TextView) inflate.findViewById(R.id.dialog_card_imfo);
        dialog_bind.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog_sure:
                getBalanceRefund(uid+"", token, account);
                dialog.dismiss();
                break;
            case R.id.dialog_cancel:
                dialog.hide();
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
