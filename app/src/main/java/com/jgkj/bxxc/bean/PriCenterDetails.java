package com.jgkj.bxxc.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/7/27.
 */

public class PriCenterDetails {
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
        private List<Member> member;

        private List<Comment> comment;

        private String file;

        private String filepic;

        private String school;

        private int price;

        private String market_price;

        private String totalnum;

        private String teach;

        private String wait;

        private String zonghe;

        private String haopin;

        private String address;

        private String longitude;

        private String latitude;

        private String introduction;

        private String class_type;

        public void setMember(List<Member> member){
            this.member = member;
        }
        public List<Member> getMember(){
            return this.member;
        }
        public void setComment(List<Comment> comment){
            this.comment = comment;
        }
        public List<Comment> getComment(){
            return this.comment;
        }
        public void setFile(String file){
            this.file = file;
        }
        public String getFile(){
            return this.file;
        }
        public void setFilepic(String filepic){
            this.filepic = filepic;
        }
        public String getFilepic(){
            return this.filepic;
        }
        public void setSchool(String school){
            this.school = school;
        }
        public String getSchool(){
            return this.school;
        }
        public void setPrice(int price){
            this.price = price;
        }
        public int getPrice(){
            return this.price;
        }
        public void setMarket_price(String market_price){
            this.market_price = market_price;
        }
        public String getMarket_price(){
            return this.market_price;
        }
        public void setTotalnum(String totalnum){
            this.totalnum = totalnum;
        }
        public String getTotalnum(){
            return this.totalnum;
        }
        public void setTeach(String teach){
            this.teach = teach;
        }
        public String getTeach(){
            return this.teach;
        }
        public void setWait(String wait){
            this.wait = wait;
        }
        public String getWait(){
            return this.wait;
        }
        public void setZonghe(String zonghe){
            this.zonghe = zonghe;
        }
        public String getZonghe(){
            return this.zonghe;
        }
        public void setHaopin(String haopin){
            this.haopin = haopin;
        }
        public String getHaopin(){
            return this.haopin;
        }
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
        }
        public void setLongitude(String longitude){
            this.longitude = longitude;
        }
        public String getLongitude(){
            return this.longitude;
        }
        public void setLatitude(String latitude){
            this.latitude = latitude;
        }
        public String getLatitude(){
            return this.latitude;
        }
        public void setIntroduction(String introduction){
            this.introduction = introduction;
        }
        public String getIntroduction(){
            return this.introduction;
        }
        public void setClass_type(String class_type){
            this.class_type = class_type;
        }
        public String getClass_type(){
            return this.class_type;
        }

        public class Member
        {
            private String coafile;   //
            private String cname;         //团队
            private String pid;
            private String sname;      //私教 - 陪练
            private String identity;   //成员
            private String class_type;//科目
            public String getClass_type() {
                return class_type;
            }
            public void setCoafile(String coafile){
                this.coafile = coafile;
            }
            public String getCoafile(){
                return this.coafile;
            }
            public void setCname(String cname){
                this.cname = cname;
            }
            public String getCname(){
                return this.cname;
            }
            public void setPid(String pid){
                this.pid = pid;
            }
            public String getPid(){
                return this.pid;
            }
            public void setSname(String sname){
                this.sname = sname;
            }
            public String getSname(){
                return this.sname;
            }
            public void setIdentity(String identity){
                this.identity = identity;
            }
            public String getIdentity(){
                return this.identity;
            }
        }

        public class Comment
        {
            private String comment;
            private String comment_time;
            private String default_file;
            private String name;
            public void setComment(String comment){
                this.comment = comment;
            }
            public String getComment(){
                return this.comment;
            }
            public void setComment_time(String comment_time){
                this.comment_time = comment_time;
            }
            public String getComment_time(){
                return this.comment_time;
            }
            public void setDefault_file(String default_file){
                this.default_file = default_file;
            }
            public String getDefault_file(){
                return this.default_file;
            }
            public void setName(String name){
                this.name = name;
            }
            public String getName(){
                return this.name;
            }
        }


    }
}
