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
import com.jgkj.bxxc.activity.InvitedToRecordActivity;
import com.jgkj.bxxc.adapter.InvitedToRecordAdapter;
import com.jgkj.bxxc.bean.Invite;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;


/**.
 * 提醒绑定银行卡dialog
 */

public class TiXianDialog implements View.OnClickListener{
    private Context context;
    private String content;
    private Dialog dialog, sureDialog;
    private View inflate, sureView;
    private TextView dialog_textView, dialog_sure, dialog_cancel;
    private int uid;
    private String token;
    private String invitestate;
    private String inviteid;

    public TiXianDialog(Context context, String content,int uid,String token,String inviteid, String invitestate){
        this.content = content;
        this.context = context;
        this.uid = uid;
        this.token = token;
        this.inviteid = inviteid;
        this.invitestate = invitestate;

    }

    public void call(){
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        inflate = LayoutInflater.from(context).inflate(
                R.layout.sure_cancel_dialog, null);
        // 初始化控件
        dialog_textView = (TextView) inflate.findViewById(R.id.dialog_textView);
        dialog_sure = (TextView) inflate.findViewById(R.id.dialog_sure);
        dialog_cancel = (TextView) inflate.findViewById(R.id.dialog_cancel);
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
                getData(uid,token,inviteid,invitestate);
                dialog.hide();
                break;
            case R.id.dialog_cancel:
                dialog.hide();
                break;

        }
    }

    private void getData(int uid,String token,String inviteid,String invitestate) {
        Log.i("百姓学车", "提现参数"+"uid=" + uid + "   token=" + token + "   inviteid=" + inviteid + "   invitestate=" + invitestate);
        OkHttpUtils
                .post()
                .url(Urls.tixian)
                .addParams("uid", String.valueOf(uid))
                .addParams("token", token)
                .addParams("inviteid", inviteid)
                .addParams("invitestate", invitestate)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(context, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百姓学车", "提现结果"+s);
                        Gson gson = new Gson();
                        Invite invite = gson.fromJson(s, Invite.class);
                        if(invite.getCode() == 200){
                            Toast.makeText(context, "提现成功", Toast.LENGTH_LONG).show();
                            updata();
                        }else{
                            Toast.makeText(context, invite.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //广播更新数据
    public void updata() {
        Intent intent = new Intent();
        intent.setAction("updataInvationApp");
        context.sendBroadcast(intent);
    }

}
