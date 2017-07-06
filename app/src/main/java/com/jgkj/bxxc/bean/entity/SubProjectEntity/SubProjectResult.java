package com.jgkj.bxxc.bean.entity.SubProjectEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/7/4.
 */
public class SubProjectResult {

    private List<SubProjectEntity> result ;

    private int code;

    private String reason;

    public List<SubProjectEntity> getResult() {
        return result;
    }

    public void setResult(List<SubProjectEntity> result) {
        this.result = result;
    }

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
}
