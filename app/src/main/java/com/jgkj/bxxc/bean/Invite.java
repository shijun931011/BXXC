package com.jgkj.bxxc.bean;

import java.util.List;

/**
 * Created by shijun on 2017/4/24.
 */

public class Invite {
    private int code;
    private String reason;
    private List<Result> result;
    private Useraccount useraccount;

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public List<Result> getResult() {
        return result;
    }

    public Useraccount getUseraccount() {
        return useraccount;
    }

    public void setUseraccount(Useraccount useraccount) {
        this.useraccount = useraccount;
    }

    public class Useraccount{
        public String banktype;
        public String account;

        public String getBanktype() {
            return banktype;
        }

        public void setBanktype(String banktype) {
            this.banktype = banktype;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }
    }

    public class Result{
        private String invitestate;//提现的状态
        private String inviter;//被邀请人的手机号
        private String invitetime;//获取邀请人的状态
        private String invitered;//邀请人得到的钱
        private String inviteid;

        public String getInvitestate() {
            return invitestate;
        }

        public String getInviter() {
            return inviter;
        }

        public String getInvitetime() {
            return invitetime;
        }

        public String getInvitered() {
            return invitered;
        }

        public String getInviteid() {
            return inviteid;
        }
    }
}
