package com.jgkj.bxxc.bean;

import java.util.List;

/**
 * Created by fangzhou on 2016/12/29.
 * 学车记录
 */

public class LearnHisAction {
    /**
     * 返回码
     */
    private int code;
    /**
     * 返回信息
     */
    private String reason;
    /**
     * 是否预约过考试
     */
    private int testState;
    /**
     * 返回结果
     */
    private List<Result> result;

    public int getTestState() {
        return testState;
    }

    public void setTestState(int testState) {
        this.testState = testState;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public List<Result> getResult() {
        return result;
    }

    public  class Result{
        /**
         * 天数
         */
        private String day;
        /**
         *时间段
         */
        private String time_slot;
        /**
         *
         */
        private String timeid;
        /**
         * 教练id
         */
        private String cid;
        /**
         * 学车进程
         */
        private int state;

        private String class_type;

        private String class_style;

        private String flag;

        private String cname;

        private String school;

        private int roles;


        public String getClass_style() {
            return class_style;
        }

        public void setClass_style(String class_style) {
            this.class_style = class_style;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getTime_slot() {
            return time_slot;
        }

        public void setTime_slot(String time_slot) {
            this.time_slot = time_slot;
        }

        public String getTimeid() {
            return timeid;
        }

        public void setTimeid(String timeid) {
            this.timeid = timeid;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getClass_type() {
            return class_type;
        }

        public void setClass_type(String class_type) {
            this.class_type = class_type;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public String getCname() {
            return cname;
        }

        public void setCname(String cname) {
            this.cname = cname;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public int getRoles() {
            return roles;
        }

        public void setRoles(int roles) {
            this.roles = roles;
        }
    }
}
