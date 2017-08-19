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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.entity.CancelEntity.CancelResult;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;


/**.
 * 提醒dialog
 */

public class RemindDialog implements View.OnClickListener{
    private Context context;
    private String content;
    private Dialog dialog, sureDialog;
    private View inflate, sureView;
    private TextView dialog_textView, dialog_sure, dialog_cancel;
    private int uid;
    private String token;
    private String cid;
    private String tid;
    private String day;
    private String time_slot;
    private String url;
    private Button btn;

    public RemindDialog(Context context, String content,int uid, String token, String cid ,String day,String time_slot,String tid,String url,Button btn){
        this.content = content;
        this.context = context;
        this.uid = uid;
        this.token = token;
        this.cid = cid;
        this.tid = tid;
        this.day = day;
        this.time_slot = time_slot;
        this.url = url;
        this.btn = btn;
    }

    public void call(){
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        inflate = LayoutInflater.from(context).inflate(R.layout.sure_cancel_dialog, null);
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
                getDataForReservation(uid, token, cid , day, time_slot, tid, url);
                dialog.dismiss();
                break;
            case R.id.dialog_cancel:
                dialog.dismiss();
                break;

        }
    }

    /**
     * @param url    请求地址
     */
    private void getDataForReservation(int uid, String token, String cid ,String day,String time_slot,String tid,String url) {
        Log.i("百信学车","取消预约" + "uid=" + uid + "   token=" + token + "   cid=" + cid + "   day=" + day + "   time_slot=" + time_slot + "   url=" + url+"tid="+tid);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("uid", Integer.toString(uid))
                .addParams("token", token)
                .addParams("cid", cid)
                .addParams("day", day)
                .addParams("time_slot", time_slot)
                .addParams("tid",tid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(context, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        CancelResult cancelResult = gson.fromJson(s,CancelResult.class);
                        Log.i("百信学车","取消预约结果" + s);
                        if (cancelResult.getCode() == 200) {
                            Toast.makeText(context, "取消预约成功！", Toast.LENGTH_SHORT).show();
                            btn.setText("可预约");
                            btn.setTextColor(context.getResources().getColor(R.color.btn_blue));
                            btn.setBackgroundResource(R.drawable.btn_style_blue);
                            updata();
                        }
                        if (cancelResult.getCode() == 400){
                            Toast.makeText(context, cancelResult.getReason() + "！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //广播更新数据
    public void updata() {
        Intent intent = new Intent();
        intent.setAction("updataApp");
        context.sendBroadcast(intent);
    }

}
