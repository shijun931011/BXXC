package com.jgkj.bxxc.bean.entity.Sub4ProjectEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/7/4.
 */
public class Sub4ProjectResult {

    private List<Sub4ProjectEntity> result ;

    private int code;

    private String reason;

    public List<Sub4ProjectEntity> getResult() {
        return result;
    }

    public void setResult(List<Sub4ProjectEntity> result) {
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
