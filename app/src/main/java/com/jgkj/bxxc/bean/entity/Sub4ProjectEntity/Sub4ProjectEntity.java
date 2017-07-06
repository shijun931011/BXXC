package com.jgkj.bxxc.bean.entity.Sub4ProjectEntity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by tongshoujun on 2017/7/4.
 */
@Entity
public class Sub4ProjectEntity {
    private String id;

    private String question;

    private String answer;

    private String item1;

    private String item2;

    private String item3;

    private String item4;

    private String explains;

    private String url;

    private String state;

    @Generated(hash = 1601571874)
    public Sub4ProjectEntity(String id, String question, String answer,
            String item1, String item2, String item3, String item4,
            String explains, String url, String state) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.item4 = item4;
        this.explains = explains;
        this.url = url;
        this.state = state;
    }

    @Generated(hash = 1692680409)
    public Sub4ProjectEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getItem1() {
        return item1;
    }

    public void setItem1(String item1) {
        this.item1 = item1;
    }

    public String getItem2() {
        return item2;
    }

    public void setItem2(String item2) {
        this.item2 = item2;
    }

    public String getItem3() {
        return item3;
    }

    public void setItem3(String item3) {
        this.item3 = item3;
    }

    public String getItem4() {
        return item4;
    }

    public void setItem4(String item4) {
        this.item4 = item4;
    }

    public String getExplains() {
        return explains;
    }

    public void setExplains(String explains) {
        this.explains = explains;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
