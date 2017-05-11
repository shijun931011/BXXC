package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.entity.ReservationDetailEntity.Subject;

import java.util.List;

/**
 * Created by shijun on 2017/4/23.
 */

public class ReservationDetailAdapter extends BaseAdapter {
    private Context context;
    private List<Subject> list;
    private LayoutInflater inflater;
    private String price;
    private String address;

    public ReservationDetailAdapter(Context context, List<Subject> list,String price,String address){
        this.context = context;
        this.list = list;
        this.address = address;
        this.price = price;
        inflater = LayoutInflater.from(context);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.reservation_item, parent, false);
            viewHolder.tv_timeslot = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_time_number = (TextView) convertView.findViewById(R.id.tv_time_number);
            viewHolder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            viewHolder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            viewHolder.btn_reservation = (Button) convertView.findViewById(R.id.btn_reservation);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_timeslot.setText(list.get(position).getTimeslot());
        viewHolder.tv_time_number.setText(list.get(position).getClasshour() + "课时");
        viewHolder.tv_price.setText("￥ " + price);
        viewHolder.tv_address.setText(address);

        return convertView;
    }

    static class ViewHolder {
        public TextView tv_timeslot;
        public TextView tv_time_number;
        public TextView tv_price;
        public TextView tv_address;
        public Button btn_reservation;
    }
}
