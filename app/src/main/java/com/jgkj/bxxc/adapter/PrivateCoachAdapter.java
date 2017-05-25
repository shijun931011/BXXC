package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.CoachDetailAction;

import java.util.List;

/**
 * 我的教练适配器
 */
public class PrivateCoachAdapter extends BaseAdapter {
    private Context context;
    private List<CoachDetailAction.Result> list;
    private LayoutInflater inflater;
    private CoachDetailAction.Result coachDetailAction;
    private LinearLayout.LayoutParams wrapParams;

    public PrivateCoachAdapter(Context context, List<CoachDetailAction.Result> list) {
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.coach_private_item, parent, false);
            viewHolder.coachName = (TextView) convertView.findViewById(R.id.coachName);
            viewHolder.kemu = (ImageView) convertView.findViewById(R.id.kemu);
            viewHolder.place = (TextView) convertView.findViewById(R.id.place);
            viewHolder.coachId = (TextView) convertView.findViewById(R.id.coachId);
            viewHolder.classType = (TextView) convertView.findViewById(R.id.classType);
            viewHolder.goodPrise = (TextView) convertView.findViewById(R.id.goodPrise);
            viewHolder.totalPriseText2 = (TextView) convertView.findViewById(R.id.totalPriseText2);
            viewHolder.coachPic = (ImageView) convertView.findViewById(R.id.coachPic);
            viewHolder.totalPriseText1 = (LinearLayout) convertView.findViewById(R.id.totalPriseText1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        coachDetailAction = list.get(position);
        int totalPrise = Integer.parseInt(coachDetailAction.getZonghe());
        viewHolder.totalPriseText1.removeAllViews();

        for (int i = 0; i < totalPrise; i++) {
            ImageView image = new ImageView(this.context);
            image.setBackgroundResource(R.drawable.star1);
            wrapParams = new LinearLayout.LayoutParams(18,18);
            image.setLayoutParams(wrapParams);
            viewHolder.totalPriseText1.addView(image);
        }
        Glide.with(context).load(coachDetailAction.getFile()).placeholder(R.drawable.head1).error(R.drawable.head1).into(viewHolder.coachPic);
        viewHolder.coachPic.setTag(R.id.imageloader_uri,coachDetailAction.getFile());
        viewHolder.coachName.setText(coachDetailAction.getCoachname());
        viewHolder.place.setText(coachDetailAction.getFaddress());
        viewHolder.classType.setText("班级:"+coachDetailAction.getClass_class());
        viewHolder.totalPriseText2.setHint(coachDetailAction.getZonghe()+".0分");
        viewHolder.goodPrise.setHint("好评率:"+coachDetailAction.getPraise()+"%");
        viewHolder.coachId.setText(coachDetailAction.getCid()+"");

        if (coachDetailAction.getClass_type().equals("科目二教练") ){
            viewHolder.kemu.setImageResource(R.drawable.kemu);
        }else if(coachDetailAction.getClass_type().equals("科目三教练") ){
            viewHolder.kemu.setImageResource(R.drawable.kemu3);
        }
        return convertView;
    }
    //listView优化
    static class ViewHolder {
        public ImageView coachPic,kemu;
        public TextView coachName, place,classType,totalPriseText2,goodPrise;
        private TextView coachId;
        public LinearLayout totalPriseText1;
    }

}
