package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.entity.ManageBankCardEntity.ManageBankCardEntity;

import java.util.List;


public class ManageBankCardAdapter extends BaseAdapter {
    private Context context;
    private List<ManageBankCardEntity> list;
    private LayoutInflater inflater;

    public ManageBankCardAdapter(Context context, List<ManageBankCardEntity> list){
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
            convertView = inflater.inflate(R.layout.item_manage_bank_card, parent, false);
            viewHolder.tv_bank_name = (TextView) convertView.findViewById(R.id.tv_bank_name);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_bank_name.setText(list.get(position).getBank_type());
        return convertView;
    }

    class ViewHolder {
        public TextView tv_bank_name;
    }

}
