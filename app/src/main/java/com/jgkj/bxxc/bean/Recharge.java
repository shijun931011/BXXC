package com.jgkj.bxxc.bean;

import java.util.List;

/**
 * Created by shijun on 2017/5/16.
 */

public class Recharge {
    private int code;
    private String reason;
    private List<Result> result;

    public String getReason() {
        return reason;
    }

    public List<Result> getResult() {
        return result;
    }

    public int getCode() {
        return code;
    }

    public class Result{
        private int moneyId;
        private int money;

        public int getMoneyId() {
            return moneyId;
        }

        public int getMoney() {
            return money;
        }
    }
}
