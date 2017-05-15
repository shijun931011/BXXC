package com.jgkj.bxxc.bean.entity.PackageEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/5/13.
 */

public class PackageResult {

    private int code;

    private String reason;

    private List<PackageEntity> result ;

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

    public List<PackageEntity> getResult() {
        return result;
    }

    public void setResult(List<PackageEntity> result) {
        this.result = result;
    }
}
