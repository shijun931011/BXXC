package com.jgkj.bxxc.bean;

import java.util.List;

/**
 * Created by shijun on 2017/5/12.
 */

public class Refund {
    private int code;
    private String reason;
    private List<Result> result;

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public List<Result> getResult() {
        return result;
    }

    public class Result{

        private String refundName;
        private String refundTime;
        private String refundMoney;
        private String refundState;

        public String getRefundName() {
            return refundName;
        }

        public String getRefundTime() {
            return refundTime;
        }

        public String getRefundMoney() {
            return refundMoney;
        }

        public String getRefundState() {
            return refundState;
        }
    }
}
