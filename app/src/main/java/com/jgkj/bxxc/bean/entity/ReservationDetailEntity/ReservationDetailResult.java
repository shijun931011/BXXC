package com.jgkj.bxxc.bean.entity.ReservationDetailEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/5/11.
 */

public class ReservationDetailResult {

    private String cid;

    private String faddress;

    private String coachname;

    private String file;

    private String price;

    private int nowstudent;

    private List<Subject> subject;

    private List<Stusubject> stusubject;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getFaddress() {
        return faddress;
    }

    public void setFaddress(String faddress) {
        this.faddress = faddress;
    }

    public String getCoachname() {
        return coachname;
    }

    public void setCoachname(String coachname) {
        this.coachname = coachname;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getNowstudent() {
        return nowstudent;
    }

    public void setNowstudent(int nowstudent) {
        this.nowstudent = nowstudent;
    }

    public List<Subject> getSubject() {
        return subject;
    }

    public void setSubject(List<Subject> subject) {
        this.subject = subject;
    }

    public List<Stusubject> getStusubject() {
        return stusubject;
    }

    public void setStusubject(List<Stusubject> stusubject) {
        this.stusubject = stusubject;
    }
}
