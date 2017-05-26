package com.jgkj.bxxc.bean.entity.BannerEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/5/26.
 */

public class BannerResult {
    private int code;

    private String reason;

    private List<BannerEntity> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<BannerEntity> getResult() {
        return result;
    }

    public void setResult(List<BannerEntity> result) {
        this.result = result;
    }
}
