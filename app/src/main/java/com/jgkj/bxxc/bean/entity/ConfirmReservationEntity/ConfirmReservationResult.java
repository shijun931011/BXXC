package com.jgkj.bxxc.bean.entity.ConfirmReservationEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/5/15.
 */

public class ConfirmReservationResult {
    private int code;
    private String reason;
    private List<ConfirmReservationEntity> result ;

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

    public List<ConfirmReservationEntity> getResult() {
        return result;
    }

    public void setResult(List<ConfirmReservationEntity> result) {
        this.result = result;
    }
}
