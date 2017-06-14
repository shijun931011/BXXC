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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.LoginActivity;
import com.jgkj.bxxc.bean.UserInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/6/12.
 */

public class InvitedCouponDialog implements View.OnClickListener{
    private Context context;
    private String content;
    private Dialog dialog;
    private View inflate;
    private ImageView quxiao,coupon, fangkunag;
    private TextView recerve_tv;
    private int uid;
    private String phone;
    private boolean flag;
    private SharedPreferences sp;
    private UserInfo userInfo;
    private UserInfo.Result result;

    private String receiveInviUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenpj/receiveInvi";

    class Result{
        private int code;

        private String reason;

        public int getCode() {
            return code;
        }

        public String getReason() {
            return reason;
        }
    }

    public InvitedCouponDialog(Context context){
        this.context = context;
    }

    private void receiveInvi(int uid, String phone){
        OkHttpUtils.post()
                .url(receiveInviUrl)
                .addParams("uid",uid+"")
                .addParams("phone",phone)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(context, "网络连接失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        //// TODO: 2017/6/13
                        Log.d("BXXC","领取优惠劵："+s);
                        Gson gson = new Gson();
                        Result invitecoupon = gson.fromJson(s, Result.class);
                        isReceiceDialog();
                        if (invitecoupon.getCode() == 200){
                             dialog.dismiss();
                             new ReceivedInvitedDialog(context,"恭喜！你成功领取了百信学车报名优惠券，现在前去报名经典班，使用优惠券立减199元，百信学车恭候你的大驾！").call();

                        }else if (invitecoupon.getCode() == 400){
                            dialog.dismiss();
                            new ReceivedInvitedDialog(context,"抱歉！你已经领取过百信学车报名优惠券了，不能重复领取。现在前去报名经典班，使用优惠券立减199元，百信学车恭候你的大驾！").call();
                        }else {
                            dialog.dismiss();
                            new ReceivedInvitedDialog(context,"抱歉！你已经在平台上报过名了，暂不能领取该优惠券。").call();
                        }

                    }
                });
    }

    public void call(){
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        inflate = LayoutInflater.from(context).inflate(
                R.layout.invipic_dialog, null);
        // 初始化控件
        quxiao = (ImageView) inflate.findViewById(R.id.quxiao);
        quxiao.setOnClickListener(this);
        coupon = (ImageView) inflate.findViewById(R.id.imageView1);
        Glide.with(context).load("http://www.baixinxueche.com/Public/Home/invite/hongbaopic.png").placeholder(R.drawable.invipic).into(coupon);
//        coupon.setOnClickListener(this);
        fangkunag = (ImageView) inflate.findViewById(R.id.fangkuang);
        fangkunag.setOnClickListener(this);
        recerve_tv = (TextView) inflate.findViewById(R.id.recerve_tv);
        recerve_tv.setOnClickListener(this);

        // 将布局设置给Dialog
        dialog.setContentView(inflate);
        // 获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        // 设置dialog宽度
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.CENTER);
        dialog.show();// 显示对话框
    }

    private void isReceiceDialog(){
        SharedPreferences sp =  context.getSharedPreferences("ReceivedCoupon", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putInt("InvitedCoupon", 1);
        editor.commit();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.quxiao:
                dialog.dismiss();
                if (flag == true){
                    isReceiceDialog();
                }
                break;
            case R.id.recerve_tv:
                // 验证是否登录
                Intent intent = new Intent();
                sp =  context.getSharedPreferences("USER",Activity.MODE_PRIVATE);
                String str = sp.getString("userInfo", null);
                Gson gson = new Gson();
                userInfo = gson.fromJson(str, UserInfo.class);
                if (userInfo == null){
                    intent.setClass(context, LoginActivity.class);
                    intent.putExtra("message","InviteFriendsActivity");
                    context.startActivity(intent);
                }else{
                    result = userInfo.getResult();
                    receiveInvi(result.getUid(),result.getPhone());
                }
                break;
            case R.id.fangkuang:
                if (!flag) {
                   fangkunag.setImageResource(R.drawable.right1);
                    flag = true;
                } else {
                    fangkunag.setImageResource(R.drawable.fang);
                    flag = false;
                }
                break;
        }
    }

}
