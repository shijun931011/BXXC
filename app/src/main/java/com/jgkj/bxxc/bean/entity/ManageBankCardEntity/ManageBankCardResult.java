package com.jgkj.bxxc.bean.entity.ManageBankCardEntity;

import com.jgkj.bxxc.bean.entity.CancelEntity.CancelEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/5/21.
 */

public class ManageBankCardResult {

    private int code;

    private String reason;

    private List<ManageBankCardEntity> result ;

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

    public List<ManageBankCardEntity> getResult() {
        return result;
    }

    public void setResult(List<ManageBankCardEntity> result) {
        this.result = result;
    }
}
