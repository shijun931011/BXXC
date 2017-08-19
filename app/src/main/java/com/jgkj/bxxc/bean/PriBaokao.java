package com.jgkj.bxxc.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/12.
 */

public class PriBaokao {
    private int code;
    private String reason;
    private Result result;
    public int getCode(){
        return this.code;
    }
    public String getReason(){
        return this.reason;
    }
    public Result getResult(){
        return this.result;
    }
    public class Result
    {
        private List<Bmplace> bmplace;
        private String alertCondition;
        public List<Bmplace> getBmplace(){
            return this.bmplace;
        }
        public String getAlertCondition(){
            return this.alertCondition;
        }
        public class Bmplace
        {
            private String place;
            private String money;
            public String getPlace(){
                return this.place;
            }
            public String getMoney(){
                return this.money;
            }
        }
    }
}
