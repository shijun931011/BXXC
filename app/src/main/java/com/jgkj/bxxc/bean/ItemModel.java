package com.jgkj.bxxc.bean;

/**
 * Created by shijun on 2016/5/17.
 */
public class ItemModel {

    public static final int ONE = 1001;

    public int type;
    public Object data;

    public ItemModel(int type, Object data) {
        this.type = type;
        this.data = data;
    }
}
