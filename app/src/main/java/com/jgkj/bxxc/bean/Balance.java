package com.jgkj.bxxc.bean;

import java.io.Serializable;

/**
 * Created by shijun on 2017/5/15.
 */

public class Balance implements Serializable {
    private int code;
    private String reason;
    private Result result;

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }


    public Result getResult() {
        return result;
    }

    public class Result{
        private String balance;
        public String getBalance() {
            return balance;
        }
    }
}
