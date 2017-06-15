package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.AppraiseActivity;
import com.jgkj.bxxc.activity.ReservationForDrivingActivity;
import com.jgkj.bxxc.bean.LearnHisAction;

import java.util.List;

/**
 * Created by fangzhou on 2016/12/29.
 */

public class DrivingHisAdapter extends BaseAdapter implements View.OnClickListener {
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.learnhis_item,
                    parent, false);
            viewHolder.day = (TextView) convertView
                    .findViewById(R.id.day);
            viewHolder.time = (TextView) convertView
                    .findViewById(R.id.time);
            viewHolder.appraise = (Button) convertView
                    .findViewById(R.id.appraise);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(!list.isEmpty()){
            result = list.get(position);
            viewHolder.day.setText(result.getDay());
            viewHolder.time.setHint(result.getTime_slot());
            viewHolder.appraise.setTag(result);
            viewHolder.appraise.setOnClickListener(this);

            if(isCome==0){
                viewHolder.appraise.setText("未到");
                viewHolder.appraise.setEnabled(false);
            }else{
                if(result.getState()!=0){
                    viewHolder.appraise.setText("查看");
                }else{
                    viewHolder.appraise.setText("评价");
                }
            }
        }
        return convertView;
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button) view;
        result = (LearnHisAction.Result)btn.getTag();
        Intent intent = new Intent();
        if(btn.getText().toString().equals("评价")){
            intent.setClass(context, AppraiseActivity.class);
            intent.putExtra("timeid",result.getCid());
            intent.putExtra("token",token);
            intent.putExtra("uid",uid);
        }
        if(btn.getText().toString().equals("查看")){
            intent.setClass(context, ReservationForDrivingActivity.class);
            intent.putExtra("coachId",result.getCid());
            intent.putExtra("uid", uid);
            intent.putExtra("token", token);
        }
        context.startActivity(intent);
    }

    static class ViewHolder {
        public TextView day,time;
        public Button appraise;
    }
}
