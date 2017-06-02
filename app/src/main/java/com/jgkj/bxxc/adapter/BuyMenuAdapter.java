package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.entity.MenuEntity.MenuEntitys;

import java.util.List;


public class BuyMenuAdapter extends BaseAdapter {
    private Context context;
    private List<MenuEntitys> list;
    private LayoutInflater inflater;
    //记录是否选择了套餐
    public static boolean flag = false;
    //套餐类型
    public static String package_id;
    private int locationPosition = -1;

    public BuyMenuAdapter(Context context, List<MenuEntitys> list){
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.select_menu_item, parent, false);
            viewHolder.tv_mune = (TextView) convertView.findViewById(R.id.tv_mune);
            viewHolder.tv_buy = (TextView) convertView.findViewById(R.id.tv_buy);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_mune.setText(list.get(position).getPackname());
        if(!list.get(position).getSurplus_money().equals("0")){
            viewHolder.tv_buy.setText("剩余" + list.get(position).getSurplus_class() + "个课时");
        }else{
            viewHolder.tv_buy.setText("暂未购买");
        }

        if(locationPosition == position){
            viewHolder.checkBox.setChecked(true);
        }else{
            viewHolder.checkBox.setChecked(false);
        }

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(viewHolder.checkBox.isChecked()){
                        //flag = true;
                        //package_id = list.get(position).getPackage_id();
                        if(!list.get(position).getSurplus_money().equals("0")){
                            locationPosition = position;
                            package_id = list.get(locationPosition).getPackage_id();
                            flag = true;
                        }else{
                            if(locationPosition != -1){
                                flag = true;
                            }else{
                                flag = false;
                            }
                        }
                    }else{
                        locationPosition = -1;
                        flag = false;
                    }
                    notifyDataSetChanged();
            }
        });
        return convertView;
    }

    class ViewHolder {
        public TextView tv_mune;
        public TextView tv_buy;
        public CheckBox checkBox;
    }

}
