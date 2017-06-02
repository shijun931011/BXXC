package com.jgkj.bxxc.bean.entity.CommentEntity;


import java.util.List;

/**
 * Created by tongshoujun on 2017/5/31.
 */

public class CommentResult {

    private int code;

    private String reason;

    private List<CommentEntity> result ;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<CommentEntity> getResult() {
        return result;
    }

    public void setResult(List<CommentEntity> result) {
        this.result = result;
    }
}
