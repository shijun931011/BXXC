package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.entity.CommentEntity.CommentEntity;

import java.util.List;

public class CoachFullDetailAdapter extends BaseAdapter {
    private Context context;
    private List<CommentEntity> list;
    private LayoutInflater inflater;
    private Bitmap bitmap;
    private CommentEntity student;

    public CoachFullDetailAdapter(Context context, List<CommentEntity> list) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.student_evaluate_listview_item, parent, false);
            viewHolder.textView2 = (TextView) convertView.findViewById(R.id.evaluate_time);
            viewHolder.textView3 = (TextView) convertView.findViewById(R.id.student_decribe);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.student_head);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        student = list.get(position);
        Glide.with(context).load(student.getDefault_file()).into(viewHolder.imageView);
        viewHolder.textView2.setHint(student.getComment_time());
        viewHolder.textView3.setText(student.getComment());
        return convertView;
    }

    static class ViewHolder {
        public ImageView imageView;
        public TextView textView2, textView3;
    }

}
