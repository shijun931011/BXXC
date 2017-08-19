package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.Version;
import com.jgkj.bxxc.tools.CallDialog;
import com.jgkj.bxxc.tools.GetVersion;
import com.jgkj.bxxc.tools.UpdateManger;
import com.tencent.mm.opensdk.modelbiz.JumpToBizProfile;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;



public class AboutMeActivity extends Activity implements View.OnClickListener{
    private Button back;
    private TextView title;
    private Context context;
    private TextView softInfo;  // 版本信息
    private TextView new_softInfo;  //新版信息
    private TextView kefu_num;
    private TextView recharge_protocol_txt,baixin_sijiao;
    private LinearLayout goodprise_line3;
    private Dialog dialog;
    private TextView wechat_num;
    //版本更新接口
    private String versionUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/versionandroid";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        context = this;
        InitView();
    }
    private void InitView(){
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.text_title);
        title.setText("关于我们");
        softInfo = (TextView) findViewById(R.id.softInfo);
        softInfo.setText("百信学车 v"+ GetVersion.getVersion(this));
        new_softInfo = (TextView) findViewById(R.id.new_softInfo);
        new_softInfo.setOnClickListener(this);
        recharge_protocol_txt = (TextView) findViewById(R.id.recharge_protocol_txt);
        recharge_protocol_txt.setOnClickListener(this);
        baixin_sijiao = (TextView) findViewById(R.id.baixin_sijiao);
        baixin_sijiao.setOnClickListener(this);
        goodprise_line3 = (LinearLayout) findViewById(R.id.line3);
        goodprise_line3.setOnClickListener(this);
        kefu_num = (TextView) findViewById(R.id.kefu_num);
        kefu_num.setOnClickListener(this);
        wechat_num = (TextView) findViewById(R.id.wechat_num);
        wechat_num.setOnClickListener(this);
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
                        Toast.makeText(AboutMeActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        Version version = gson.fromJson(s, Version.class);
                        if (version.getCode() == 200) {
                            if (version.getResult().get(0).getVersionCode() > GetVersion.getVersionCode(context)) {
                                UpdateManger updateManger = new UpdateManger(context, version.getResult().get(0).getPath(),version.getResult().get(0).getVersionName(),version.getResult().get(0).getStyle());
                                updateManger.checkUpdateInfo();
                            } else {
                                Toast.makeText(AboutMeActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.button_backward:
                finish();
                break;
            case R.id.new_softInfo:
                checkSoftInfo();
                break;
            case R.id.recharge_protocol_txt:
                intent.setClass(this,WebViewActivity.class);
                intent.putExtra("url","http://www.baixinxueche.com/webshow/chongzhi/recharge.html");
                intent.putExtra("title","充值协议");
                startActivity(intent);
                break;
            case R.id.baixin_sijiao:
                intent.setClass(this, WebViewActivity.class);
                intent.putExtra("url", "http://www.baixinxueche" +
                        ".com/webshow/chongzhi/sijiaoPayAgreement.html ");
                intent.putExtra("title", "百信学车私教服务条款");
                startActivity(intent);
                break;
            case R.id.line3:
                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "Couldn't launch the market !", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.kefu_num:
                new CallDialog(this, "17756086205").call();
                break;
            case R.id.dialog_sure:
                Intent call_intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "0551-65555744"));
                startActivity(call_intent);
                dialog.hide();
                break;
            case R.id.dialog_cancel:
                dialog.hide();
                break;
            case R.id.wechat_num:
                String appId = "wx75b78ead0e64a547";//开发者平台ID
                IWXAPI api = WXAPIFactory.createWXAPI(this, appId, false);

                if (api.isWXAppInstalled()) {
                    JumpToBizProfile.Req req = new JumpToBizProfile.Req();
                    req.toUserName = "bxxueche"; // 公众号原始ID
                    req.extMsg = "";
                    req.profileType = JumpToBizProfile.JUMP_TO_NORMAL_BIZ_PROFILE; // 普通公众号
                    api.sendReq(req);
                }else{
                    Toast.makeText(this, "微信未安装", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
}
