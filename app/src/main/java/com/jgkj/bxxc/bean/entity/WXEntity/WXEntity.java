package com.jgkj.bxxc.bean.entity.WXEntity;

/**
 * Created by tongshoujun on 2017/5/23.
 */

public class WXEntity {
    private int errorCode;

    private String errorMsg;

    private ResponseData responseData;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public ResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }
}
