package com.jgkj.bxxc.tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.weixin.WXPay;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpay_call_back);


        if(WXPay.getInstance() != null) {
            Toast.makeText(WXPayEntryActivity.this, "ffffff", Toast.LENGTH_SHORT).show();
            WXPay.getInstance().getWXApi().handleIntent(getIntent(), this);
        } else {
            Toast.makeText(WXPayEntryActivity.this, "finish---" + WXPay.getInstance(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if(WXPay.getInstance() != null) {
            WXPay.getInstance().getWXApi().handleIntent(intent, this);
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Toast.makeText(WXPayEntryActivity.this, "onReq", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResp(BaseResp baseResp) {
        Toast.makeText(WXPayEntryActivity.this, "onResp", Toast.LENGTH_SHORT).show();
        if(baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if(WXPay.getInstance() != null) {
                if(baseResp.errStr != null) {
                    Log.i("百信学车", "errstr=" + baseResp.errStr);
                }
                WXPay.getInstance().onResp(baseResp.errCode);
                finish();
            }
        }
    }
}
