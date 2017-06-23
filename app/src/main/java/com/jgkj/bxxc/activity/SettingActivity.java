package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.bean.Version;
import com.jgkj.bxxc.tools.GetVersion;
import com.jgkj.bxxc.tools.GlideCacheUtil;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.jgkj.bxxc.tools.UpdateManger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class SettingActivity extends Activity implements View.OnClickListener {
    private Button back;
    private TextView title;
    private TextView softInfo;
    private TextView cleanUp;
    private Dialog  sureDialog;
    private View sureView;
    private TextView dialog_textView, dialog_sure, dialog_cancel;
    private Boolean isLogined = false;
    //退出登录
    private Button exit;
    //读取本地信息
    private SharedPreferences sp, sp1;
    private String token;
    private UserInfo userInfo;
    private UserInfo.Result result;
    private Context context;

    //账户安全
    private LinearLayout linearLayout_account_security;

    //版本更新接口
    private String versionUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/versionandroid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        context = this;
        initView();
    }

    private void initView(){
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.text_title);
        title.setText("设置");

        linearLayout_account_security = (LinearLayout)findViewById(R.id.linearLayout_account_security);
        //版本更新
        softInfo = (TextView) findViewById(R.id.softInfo);
        softInfo.setText(GetVersion.getVersion(this));
        softInfo.setOnClickListener(this);
        linearLayout_account_security.setOnClickListener(this);
        //缓存大小
        cleanUp = (TextView) findViewById(R.id.cleanUp);
        cleanUp.setOnClickListener(this);
        String str = GlideCacheUtil.getInstance().getCacheSize(getApplicationContext());
        cleanUp.setText(str);
        exit = (Button) findViewById(R.id.exit);
        exit.setOnClickListener(this);
        //验证是否登录
        sp = getApplication().getSharedPreferences("USER", Activity.MODE_PRIVATE);
        int isFirstRun = sp.getInt("isfirst",0);
        if (isFirstRun == 0) {
            exit.setVisibility(View.GONE);
        }

    }

    /**
     * 缓存清理dialog
     */
    private void createSureDialog() {
        sureDialog = new Dialog(SettingActivity.this, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        sureView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.sure_cancel_dialog, null);
        // 初始化控件
        dialog_textView = (TextView) sureView.findViewById(R.id.dialog_textView);
        dialog_textView.setText("确定清理缓存吗？");
        dialog_sure = (TextView) sureView.findViewById(R.id.dialog_sure);
        dialog_cancel = (TextView) sureView.findViewById(R.id.dialog_cancel);
        dialog_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlideCacheUtil.getInstance().clearImageAllCache(context);
                String str = GlideCacheUtil.getInstance().getCacheSize(context);
                cleanUp.setText(str);
                sureDialog.dismiss();
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sureDialog.dismiss();
            }
        });
        // 将布局设置给Dialog
        sureDialog.setContentView(sureView);
        // 获取当前Activity所在的窗体
        Window dialogWindow = sureDialog.getWindow();
        // 设置dialog宽度
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.CENTER);
        sureDialog.show();
    }

    /**
     * 确定退出dialog
     */
    private void createQuitDialog() {
        sureDialog = new Dialog(SettingActivity.this, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        sureView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.sure_cancel_dialog, null);
        // 初始化控件
        dialog_textView = (TextView) sureView.findViewById(R.id.dialog_textView);
        dialog_textView.setText("确定退出当前帐号吗？");
        dialog_sure = (TextView) sureView.findViewById(R.id.dialog_sure);
        dialog_cancel = (TextView) sureView.findViewById(R.id.dialog_cancel);
        dialog_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLogined = false;
                SharedPreferences sp = getApplication().getSharedPreferences("USER", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                sureDialog.dismiss();
                finish();
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sureDialog.dismiss();
            }
        });
        // 将布局设置给Dialog
        sureDialog.setContentView(sureView);
        // 获取当前Activity所在的窗体
        Window dialogWindow = sureDialog.getWindow();
        // 设置dialog宽度
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.CENTER);
        sureDialog.show();
    }

    /**
     * 检查更新
     */
    private void checkSoftInfo() {
        OkHttpUtils
                .get()
                .url(versionUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(SettingActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        Version version = gson.fromJson(s, Version.class);
                        if (version.getCode() == 200) {
                            if (version.getResult().get(0).getVersionCode() > GetVersion.getVersionCode(context)) {
                                UpdateManger updateManger = new UpdateManger(context, version.getResult().get(0).getPath(),version.getResult().get(0).getVersionName());
                                updateManger.checkUpdateInfo();
                            } else {
                                Toast.makeText(SettingActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.linearLayout_account_security:
                //验证是否登录
                sp = getApplication().getSharedPreferences("USER", Activity.MODE_PRIVATE);
                int isFirstRun = sp.getInt("isfirst",0);
                if (isFirstRun == 0) {
                    intent.setClass(SettingActivity.this,LoginActivity.class);
                    intent.putExtra("message","account_security");
                    startActivity(intent);
                }else{
                    intent.setClass(this,AccountSecurityActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.button_backward:
                finish();
                break;
            case R.id.softInfo:
                checkSoftInfo();
                break;
            case R.id.cleanUp:
                createSureDialog();
                break;
            case R.id.exit:
                createQuitDialog();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //验证是否登录
        sp = getApplication().getSharedPreferences("USER", Activity.MODE_PRIVATE);
        int isFirstRun = sp.getInt("isfirst",0);
        if (isFirstRun == 0) {
            exit.setVisibility(View.GONE);
        }else{
            exit.setVisibility(View.VISIBLE);
        }
    }
}
