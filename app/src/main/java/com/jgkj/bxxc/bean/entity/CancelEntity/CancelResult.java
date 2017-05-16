package com.jgkj.bxxc.bean.entity.CancelEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/5/13.
 */

public class CancelResult {

    private int code;

    private String reason;

    private List<CancelEntity> result ;

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

    public List<CancelEntity> getResult() {
        return result;
    }

    public void setResult(List<CancelEntity> result) {
        this.result = result;
    }
}
