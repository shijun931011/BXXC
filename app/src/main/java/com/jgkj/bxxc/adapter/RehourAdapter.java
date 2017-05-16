package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.Rehour;

import java.util.List;


/**
 * Created by Administrator on 2017/5/11.
 *
 */

public class RehourAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private Rehour.Result result;
    private List<Rehour.Result> list;



    public RehourAdapter(Context context, List<Rehour.Result> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null){
             viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.rehour_item, viewGroup, false);
            viewHolder.taocan = (TextView)view.findViewById(R.id.taocan_txt);
            viewHolder.bug = (TextView) view.findViewById(R.id.bug);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        result = list.get(i);
        viewHolder.taocan.setText(result.getPackname());
//        viewHolder.bug.setText(result.getSurplus_class());
        if (result.getSurplus_class().equals("0")){
            viewHolder.bug.setText("暂未购买");
        }else{
            viewHolder.bug.setText("剩余"+result.getSurplus_class()+"个学时");
        }
        return view;
    }

    class ViewHolder{
        private TextView taocan, bug;
    }
}
