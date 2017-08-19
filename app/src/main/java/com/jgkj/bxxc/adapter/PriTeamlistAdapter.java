package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.PriCenterDetails;

import java.util.List;

/**
 * Created by shijun on 2017/7/29.
 */

public class PriTeamlistAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private PriCenterDetails.Result result;
    private String Cname;
    private String Sname;
    private String class_type;
    private String identity;

    private List<PriCenterDetails.Result.Member> list;

    public PriTeamlistAdapter(Context context, List<PriCenterDetails.Result.Member> list){
        this.context = context;
        this.list=list;
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
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null){
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.pri_team_list, viewGroup, false);
            viewHolder.txt_team_name = (TextView)view.findViewById(R.id.txt_team_name);
            viewHolder.txt_team_sijiao = (TextView) view.findViewById(R.id.txt_team_sijiao);
            viewHolder.txt_identity = (TextView) view.findViewById(R.id.identity);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.txt_team_name.setText(list.get(i).getCname());
        viewHolder.txt_team_sijiao.setText(list.get(i).getSname());

        return view;
    }
    class ViewHolder{
        private TextView txt_team_name, txt_team_sijiao,txt_identity;
    }


}
