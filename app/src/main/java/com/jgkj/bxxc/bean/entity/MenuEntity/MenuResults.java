package com.jgkj.bxxc.bean.entity.MenuEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/5/12.
 */

public class MenuResults {
    private int code;

    private String reason;

    private List<MenuEntitys> result ;

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

    public List<MenuEntitys> getResult() {
        return result;
    }

    public void setResult(List<MenuEntitys> result) {
        this.result = result;
    }
}
