package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.LearnHisAction;

import java.util.List;

/**
 * Created by Administrator on 2017/8/7.
 */

public class UnCompleteRecordAdapter extends BaseAdapter {
    private Context context;
    private List<LearnHisAction.Result> list;
    private LearnHisAction.Result result;
    private LayoutInflater inflater;
    private String token;
    private int uid;
    public UnCompleteRecordAdapter(Context context, List<LearnHisAction.Result> list,
                           String token, int uid){
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.token = token;
        this.uid = uid;
    }

    @Override
    public int getCount() {
        return list.size();
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
            convertView = inflater.inflate(R.layout.learnhis_item, parent, false);
            viewHolder.txt_commment_result = (TextView) convertView.findViewById(R.id.commemt_result);
            viewHolder.txt_study_school = (TextView) convertView.findViewById(R.id.study_school);
            viewHolder.txt_study_type = (TextView) convertView.findViewById(R.id.study_type);
            viewHolder.txt_study_coach = (TextView) convertView.findViewById(R.id.study_coach);
            viewHolder.txt_study_time = (TextView) convertView.findViewById(R.id.study_time);
            viewHolder.learn_record_line = (LinearLayout) convertView.findViewById(R.id.learn_record_line);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        result = list.get(position);


        if (result.getState() == 0){
            viewHolder.txt_commment_result.setText("未到");
            viewHolder.txt_commment_result.setTextColor(context.getResources().getColor(R.color.orange));
        }
        if (result.getFlag().equals("1")){
            viewHolder.txt_study_type.setText(result.getClass_style());
        }else if (result.getFlag().equals("2")){
            viewHolder.txt_study_type.setText(result.getClass_style()+"(科目二)");
        }else if (result.getFlag().equals("3")){
            viewHolder.txt_study_type.setText(result.getClass_style()+"(科目三)");
        }

        viewHolder.txt_study_school.setText(result.getSchool());
        viewHolder.txt_study_coach.setText(result.getCname());
        viewHolder.txt_study_time.setText(result.getDay() + result.getTime_slot());
        return convertView;
    }

    static class ViewHolder {
        public TextView txt_study_type, txt_study_school, txt_study_coach, txt_study_time, txt_commment_result;
        public LinearLayout learn_record_line;
    }

}
