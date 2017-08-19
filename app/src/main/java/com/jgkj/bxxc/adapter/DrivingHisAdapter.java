package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jgkj.bxxc.bean.LearnHisAction;

import java.util.List;

/**
 * Created by fangzhou on 2016/12/29.
 */

public class DrivingHisAdapter extends BaseAdapter{
    private Context context;
    private List<LearnHisAction.Result> list;
    private LearnHisAction.Result result;
    private int isCome;
    private LayoutInflater inflater;
    private String token;
    private int uid;
    public DrivingHisAdapter(Context context, List<LearnHisAction.Result> list, int isCome,
                             String token, int uid){
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.isCome = isCome;
        this.token = token;
        this.uid = uid;
    }

    @Override
    public int getCount() {
        if(list.isEmpty()){
            return 0;
        }else{
            return list.size();
        }
    }
    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return convertView;
    }




}
