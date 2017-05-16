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
import com.jgkj.bxxc.bean.entity.MenuEntity.MenuEntitys;
import com.jgkj.bxxc.bean.entity.PackageEntity.PackageEntity;

import java.util.List;


/**
 * Created by shijun on 2017/4/17.
 */

public class PackageAdapter extends BaseAdapter {
    private Context context;
    private List<PackageEntity> list;
    private LayoutInflater inflater;

    public PackageAdapter(Context context, List<PackageEntity> list){
        this.context = context;
        this.list = list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.buy_class_hours_item, parent, false);

            viewHolder.im_pic = (ImageView) convertView.findViewById(R.id.im_pic);
            viewHolder.tv_pakage = (TextView) convertView.findViewById(R.id.tv_pakage);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_buy_old = (TextView) convertView.findViewById(R.id.tv_buy_old);
            viewHolder.tv_buy = (TextView) convertView.findViewById(R.id.tv_buy);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Glide.with(context).load(list.get(position).getPic()).placeholder(R.drawable.package_image).error(R.drawable.head1).into(viewHolder.im_pic);
        viewHolder.tv_pakage.setText(list.get(position).getPackagename());
        viewHolder.tv_buy.setText("￥" + list.get(position).getCountmoney());
        viewHolder.tv_buy_old.setText("原价：￥" + Calculation(list.get(position).getClasshour(),list.get(position).getClassmoney()));
        viewHolder.tv_time.setText("所含课时数" + list.get(position).getClasshour());

        return convertView;
    }

    static class ViewHolder {
        public TextView tv_pakage;
        public TextView tv_time;
        public TextView tv_buy_old;
        public TextView tv_buy;
        public ImageView im_pic;

    }

    public String Calculation(String s1,String s2){
        return Integer.toString(Integer.parseInt(s1)*Integer.parseInt(s2));
    }

}
