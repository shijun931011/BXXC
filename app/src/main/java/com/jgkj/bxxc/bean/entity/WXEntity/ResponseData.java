package com.jgkj.bxxc.bean.entity.WXEntity;

/**
 * Created by tongshoujun on 2017/5/23.
 */

public class ResponseData {
    private String notify_url;

    private App_response app_response;

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public App_response getApp_response() {
        return app_response;
    }

    public void setApp_response(App_response app_response) {
        this.app_response = app_response;
    }
}
