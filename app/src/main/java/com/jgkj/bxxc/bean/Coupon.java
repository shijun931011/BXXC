package com.jgkj.bxxc.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/14.
 */

public class Coupon {
    private int code;
    private String reason;
    private int nocode;
    private String noreason;
    private List<Result> result;
    private List<Result> noresult;

    public List<Result> getResult() {
        return result;
    }

    public List<Result> getNoresult() {
        return noresult;
    }

    public int getCode() {
        return code;
    }

    public String getNoreason() {
        return noreason;
    }

    public int getNocode() {
        return nocode;
    }

    public String getReason() {
        return reason;
    }

    public class Result{
        private String pic;
        private String title;         //优惠劵
        private String InvalidTime;  //有效期限
        private String invitestate;   //0 不可提现 1 可提现 2 待转账 3 转账完成
        private String invitefee;    //优惠费用

        public String getPic() {
            return pic;
        }

        public String getTitle() {
            return title;
        }

        public String getInvalidTime() {
            return InvalidTime;
        }

        public String getInvitestate() {
            return invitestate;
        }

        public String getInvitefee() {
            return invitefee;
        }
    }

}
