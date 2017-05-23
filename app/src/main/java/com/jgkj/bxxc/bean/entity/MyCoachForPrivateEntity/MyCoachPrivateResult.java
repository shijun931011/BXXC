package com.jgkj.bxxc.bean.entity.MyCoachForPrivateEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/5/22.
 */

public class MyCoachPrivateResult {
    private int code;

    private String reason;

    private List<MyCoachPrivaetEntity> result;

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

    public List<MyCoachPrivaetEntity> getResult() {
        return result;
    }

    public void setResult(List<MyCoachPrivaetEntity> result) {
        this.result = result;
    }
}
