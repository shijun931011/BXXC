package com.jgkj.bxxc.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/11.
 * 剩余课时实体
 */

public class Rehour {
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
        private String package_id;//套餐几
        private String packname;  //套餐
        private String surplus_class;//剩余课时
        private String surplus_money;//剩余钱

        public String getPackage_id() {
            return package_id;
        }

        public String getPackname() {
            return packname;
        }

        public String getSurplus_class() {
            return surplus_class;
        }

        public String getSurplus_money() {
            return surplus_money;
        }
    }
}
