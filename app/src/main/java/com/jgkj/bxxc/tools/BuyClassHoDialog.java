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
import com.jgkj.bxxc.activity.BuyClassHoursActivity;


/**.
 * 购买学时dialog
 */

public class BuyClassHoDialog implements View.OnClickListener{
    private Context context;
    private String content;
    private Dialog dialog, sureDialog;
    private View inflate, sureView;
    private TextView dialog_textView, dialog_sure, dialog_cancel;
    private int uid;
    private String token;
    private String cid;
    private String day;
    private String time_slot;
    private String url;
    private Button btn;

    public BuyClassHoDialog(Context context, String content, int uid, String cid,String token){
        this.content = content;
        this.context = context;
        this.uid = uid;
        this.token = token;
        this.cid = cid;

    }

    public void call(){
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        inflate = LayoutInflater.from(context).inflate(
                R.layout.sure_cancel_dialog2, null);
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
                Intent intent2 = new Intent();
                intent2.setClass(context, BuyClassHoursActivity.class);
                intent2.putExtra("uid",uid);
                intent2.putExtra("cid",cid);//token
                intent2.putExtra("token",token);
                context.startActivity(intent2);
                dialog.hide();
                break;
            case R.id.dialog_cancel:
                dialog.hide();
                break;

        }
    }

}
