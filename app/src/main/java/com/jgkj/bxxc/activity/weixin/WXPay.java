package com.jgkj.bxxc.activity.weixin;

import android.content.Context;
import com.google.gson.Gson;
import com.jgkj.bxxc.bean.entity.WXEntity.WXEntity;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 微信支付
 * Created by tsy on 17/5/24.
 */
public class WXPay {

    public static WXPay mWXPay;
    public static IWXAPI mWXApi;
    public static String mPayParam;
    public static WXPayResultCallBack mCallback;
    public static Context context;

    public static final int NO_OR_LOW_WX = 1;   //未安装微信或微信版本过低
    public static final int ERROR_PAY_PARAM = 2;  //支付参数错误
    public static final int ERROR_PAY = 3;  //支付失败

    public interface WXPayResultCallBack {
        void onSuccess(); //支付成功
        void onError(int error_code);   //支付失败
        void onCancel();    //支付取消
    }

    public WXPay(Context context, String wx_appid) {
        this.context = context;
        mWXApi = WXAPIFactory.createWXAPI(context, null);
        mWXApi.registerApp(wx_appid);
    }

    public static void init(Context context, String wx_appid) {
        if(mWXPay == null) {
            mWXPay = new WXPay(context, wx_appid);
        }
    }
    public static WXPay getInstance(){
        return mWXPay;
    }

    public IWXAPI getWXApi() {
        return mWXApi;
    }
    /**
     * 发起微信支付
     */
    public void doPay(String pay_param, WXPayResultCallBack callback) {
        mPayParam = pay_param;
        mCallback = callback;

        if(!check()) {
            if(mCallback != null) {
                mCallback.onError(NO_OR_LOW_WX);
            }
            return;
        }

        JSONObject param = null;
        try {
            param = new JSONObject(mPayParam);
        } catch (JSONException e) {
            e.printStackTrace();
            if(mCallback != null) {
                mCallback.onError(ERROR_PAY_PARAM);
            }
            return;
        }

        if(param == null) {
            if(mCallback != null) {
                mCallback.onError(ERROR_PAY_PARAM);
            }
            return;
        }

        Gson gson = new Gson();
        WXEntity wxEntity = gson.fromJson(mPayParam, WXEntity.class);

        PayReq req = new PayReq();
        req.appId = wxEntity.getResponseData().getApp_response().getAppid();
        req.partnerId = wxEntity.getResponseData().getApp_response().getPartnerid();
        req.prepayId = wxEntity.getResponseData().getApp_response().getPrepayid();
        req.packageValue = wxEntity.getResponseData().getApp_response().getPackagestr();
        req.nonceStr = wxEntity.getResponseData().getApp_response().getNoncestr();
        req.timeStamp = String.valueOf(wxEntity.getResponseData().getApp_response().getTimestamp());
        req.sign = wxEntity.getResponseData().getApp_response().getSign();

        mWXApi.sendReq(req);
    }

    //支付回调响应
    public static void onResp(int error_code) {
        if(mCallback == null) {
            return;
        }
        if(error_code == 0) {   //成功
            mCallback.onSuccess();
        } else if(error_code == -1) {   //错误
            mCallback.onError(ERROR_PAY);
        } else if(error_code == -2) {   //取消
            mCallback.onCancel();
        }

        mCallback = null;
    }

    //检测是否支持微信支付
    private boolean check() {
        return mWXApi.isWXAppInstalled() && mWXApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
    }
}
