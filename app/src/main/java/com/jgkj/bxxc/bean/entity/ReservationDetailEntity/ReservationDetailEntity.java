package com.jgkj.bxxc.bean.entity.ReservationDetailEntity;

/**
 * Created by tongshoujun on 2017/5/11.
 */

public class ReservationDetailEntity {

    private int code;

    private String reason;

    private ReservationDetailResult result;

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

    public ReservationDetailResult getResult() {
        return result;
    }

    public void setResult(ReservationDetailResult result) {
        this.result = result;
    }
}
