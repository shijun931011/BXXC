package com.jgkj.bxxc.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.ReservationDetailActivity;

/**
 * Created by Administrator on 2017/7/31.
 */

public class PeilianDialog implements View.OnClickListener{
    private Context context;
    private String content;
    private String center_name;
    private String Cname;
    private Dialog dialog, sureDialog;
    private View inflate, sureView;
    private TextView dialog_textView, dialog_sure, dialog_cancel;
    private String coachId;
    private int uid;
    private String tid;
    private String token;
    private Button btn;

    public PeilianDialog(Context context, String content, String center_name, String cname, int uid, String token,String coachId, String tid){
        this.content = content;
        this.context = context;
        this.center_name = center_name;
        this.Cname = cname;
        this.uid = uid;
        this.token = token;
        this.coachId = coachId;
        this.tid = tid;
    }

    public void call(){
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        inflate = LayoutInflater.from(context).inflate(R.layout.sure_cancel_dialog, null);
        // 初始化控件
        dialog_textView = (TextView) inflate.findViewById(R.id.dialog_textView);
        dialog_sure = (TextView) inflate.findViewById(R.id.dialog_sure);
        dialog_cancel = (TextView) inflate.findViewById(R.id.dialog_cancel);
        dialog_cancel.setOnClickListener(this);
        dialog_sure.setOnClickListener(this);
        dialog_textView.setText(content);
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

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.dialog_cancel:
                dialog.dismiss();
                break;
            case R.id.dialog_sure:
                intent.setClass(context, ReservationDetailActivity.class);
                intent.putExtra("center_name",center_name);
                intent.putExtra("Cname",Cname);
                intent.putExtra("uid",uid);
                intent.putExtra("tid",tid);
                intent.putExtra("token",token);
                intent.putExtra("class_style",1);
                intent.putExtra("pri_team",1);
                intent.putExtra("cid",coachId);
                context.startActivity(intent);
                dialog.dismiss();
                break;
        }
    }

}
