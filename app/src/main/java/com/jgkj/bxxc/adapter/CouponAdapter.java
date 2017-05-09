package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.Coupon;

import java.util.List;

/**
 * Created by shijun on 2017/4/17.
 */

public class CouponAdapter extends BaseAdapter {
    private Context context;
    private List<Coupon.Result> list;
    private LayoutInflater inflater;
    private Coupon.Result result;
    private String uid;

    public CouponAdapter(String uid, Context context, List<Coupon.Result> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        this.uid = uid;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.coupon_list, parent, false);
            viewHolder.img_coupon = (ImageView) convertView.findViewById(R.id.img_coupon);
            viewHolder.txt_coupon = (TextView) convertView.findViewById(R.id.txt_coupon);
            viewHolder.txt_effect = (TextView) convertView.findViewById(R.id.txt_effect);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        result = list.get(position);
        Glide.with(context).load(result.getPic()).into(viewHolder.img_coupon);
        return convertView;
    }

    static class ViewHolder {
        public TextView txt_coupon, txt_effect;
        public ImageView img_coupon;

    }
}
