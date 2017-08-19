package com.jgkj.bxxc.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */

public class Resou {
    private int code;

    private String reason;

    private List<Result> result;

    public void setCode(int code){
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
    public void setReason(String reason){
        this.reason = reason;
    }
    public String getReason(){
        return this.reason;
    }
    public void setResult(List<Result> result){
        this.result = result;
    }
    public List<Result> getResult(){
        return this.result;
    }
    public class Result
    {
        private String hotid;

        private String name;

        private int roles;

        public void setHotid(String hotid){
            this.hotid = hotid;
        }
        public String getHotid(){
            return this.hotid;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
        public void setRoles(int roles){
            this.roles = roles;
        }
        public int getRoles(){
            return this.roles;
        }
    }
}
