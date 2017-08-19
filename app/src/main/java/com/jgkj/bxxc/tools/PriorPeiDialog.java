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

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.ReservationDetailActivity;

/**
 * Created by Administrator on 2017/7/31.
 */

public class PriorPeiDialog implements View.OnClickListener{

    private Dialog dialog, sureDialog;
    private View inflate, sureView;
    private TextView  dialog_sijiao, dialog_peilian, dialog_cancel,dialog_choose;
    private String Sname;
    private String Center_name;
    private String Cname;
    private String Token;
    private String Class_type;
    private String CoachId;
    private String tid;
    private int uid;
    private Context context;

    public PriorPeiDialog (Context context, String center_name, String sname, String cname,String class_type,int uid, String token,String coachId, String tid){
        this.context = context;
        this.Center_name = center_name;
        this.Sname = sname;
        this.Cname = cname;
        this.uid = uid;
        this.Token = token;
        this.Class_type = class_type;
        this.CoachId = coachId;
        this.tid = tid;
    }

    public void choose() {
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        inflate = LayoutInflater.from(context).inflate(R.layout.sure_choose_dialog, null);
        //控件
        dialog_sijiao= (TextView) inflate.findViewById(R.id.dialog_yes);
        dialog_peilian = (TextView) inflate.findViewById(R.id.dialog_no);
        dialog_cancel = (TextView) inflate.findViewById(R.id.dialog_cancel);
        dialog_choose = (TextView) inflate.findViewById(R.id.diolog_choose);
        dialog_choose.setText("请选择您需要的服务类型");
        dialog_sijiao.setOnClickListener(this);
        dialog_sijiao.setText("私教");
        dialog_peilian.setOnClickListener(this);
        dialog_peilian.setText("陪练");
        dialog_cancel.setOnClickListener(this);
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
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.dialog_cancel:
                dialog.dismiss();
                break;
            case R.id.dialog_yes:
                intent.setClass(context, ReservationDetailActivity.class);
                intent.putExtra("center_name",Center_name);
                intent.putExtra("Cname",Cname);
                intent.putExtra("uid",uid);
                intent.putExtra("tid",tid);
                intent.putExtra("token",Token);
                Log.d("百信学车","Token值："+Token);
                intent.putExtra("class_style",0);
                intent.putExtra("pri_team",1);
                intent.putExtra("cid",CoachId);
                context.startActivity(intent);
                dialog.dismiss();
                break;
            case R.id.dialog_no:
                intent.setClass(context, ReservationDetailActivity.class);
                intent.putExtra("center_name",Center_name);
                intent.putExtra("Cname",Cname);
                intent.putExtra("uid",uid);
                intent.putExtra("tid",tid);
                intent.putExtra("token",Token);
                intent.putExtra("class_style",1);
                intent.putExtra("pri_team",1);
                intent.putExtra("cid",CoachId);
                context.startActivity(intent);
                dialog.dismiss();
                break;


        }

    }
}
