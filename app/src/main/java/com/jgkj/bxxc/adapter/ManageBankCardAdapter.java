package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.util.Log;
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
    private int length;

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
        length = list.get(position).getAccount().length();
        Log.d("BXXC","银行卡："+list.get(position).getBank_type()+"HHHHHHH:"+list.get(position).getAccount());
        viewHolder.tv_bank_name.setText(subString(list.get(position).getBank_type()) +"("+list.get(position).getAccount().substring(length-5,length).replace(" ", "")+")");

        return convertView;
    }

    class ViewHolder {
        public TextView tv_bank_name;
    }

    public String subString(String ss){
        char [] stringArr = ss.toCharArray();
        for(int i=0;i<stringArr.length;i++){
            if(stringArr[i] == '.' || stringArr[i] == '·'){
                return ss.substring(0,i);
            }
        }
        return "";
    }

}
