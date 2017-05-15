package com.jgkj.bxxc.bean;

import java.util.List;

/**
 * Created by shijun on 2017/5/15.
 */

public class PayDetail {
    private int code;
    private String reason;
    private List<PayDetail.Result> result;

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
        private  String money;  //金额
        private String orderNo;  //订单号
        private String applyTime; //支付时间
        private String paydel;    //支付详情
        private String paystate;   //支付方式

        public String getMoney() {
            return money;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public String getApplyTime() {
            return applyTime;
        }

        public String getPaydel() {
            return paydel;
        }

        public String getPaystate() {
            return paystate;
        }
    }
}
