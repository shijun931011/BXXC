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
 * Created by Administrator on 2017/7/26.
 */

public class PrivateCenterSearchAdapter extends BaseAdapter {
    private Context context;
    private List<CoachDetailAction.Resultarr> list;
    private LayoutInflater inflater;
    private CoachDetailAction.Resultarr coachDetailAction;
    private LinearLayout.LayoutParams wrapParams;

    public PrivateCenterSearchAdapter(Context context, List<CoachDetailAction.Resultarr> list) {
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
            convertView = inflater.inflate(R.layout.my_coach_private_item, parent, false);
            viewHolder.coachName = (TextView) convertView.findViewById(R.id.coachName);
            viewHolder.kemu = (ImageView) convertView.findViewById(R.id.kemu);
            viewHolder.place = (TextView) convertView.findViewById(R.id.place);
            viewHolder.tid = (TextView) convertView.findViewById(R.id.coachId);
            viewHolder.goodPrise = (TextView) convertView.findViewById(R.id.goodPrise);
            viewHolder.totalPriseText2 = (TextView) convertView.findViewById(R.id.totalPriseText2);
            viewHolder.tv_stunum = (TextView) convertView.findViewById(R.id.tv_total_stu);
            viewHolder.coachPic = (ImageView) convertView.findViewById(R.id.coachPic);
            viewHolder.totalPriseText1 = (LinearLayout) convertView.findViewById(R.id.totalPriseText1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        coachDetailAction = list.get(position);

        Double totalPrise = Double.parseDouble(coachDetailAction.getPtzonghe());
        viewHolder.totalPriseText1.removeAllViews();

        if (totalPrise < 1){
            ImageView image = new ImageView(context);
            image.setBackgroundResource(R.drawable.star0);
            wrapParams = new LinearLayout.LayoutParams(150,30);
            image.setLayoutParams(wrapParams);
            viewHolder.totalPriseText1.addView(image);
        }
        if (totalPrise  == 1){
            ImageView image = new ImageView(context);
            image.setBackgroundResource(R.drawable.star1);
            wrapParams = new LinearLayout.LayoutParams(30,30);
            image.setLayoutParams(wrapParams);
            viewHolder.totalPriseText1.addView(image);
        }
        if (totalPrise > 1  && totalPrise < 2){
            ImageView image = new ImageView(context);
            image.setBackgroundResource(R.drawable.star2);
            wrapParams = new LinearLayout.LayoutParams(150,30);
            image.setLayoutParams(wrapParams);
            viewHolder.totalPriseText1.addView(image);
        }
        if (totalPrise == 2){
            for (double i = 0; i < 2; i++){
                ImageView image = new ImageView(context);
                image.setBackgroundResource(R.drawable.star1);
                wrapParams = new LinearLayout.LayoutParams(30,30);
                image.setLayoutParams(wrapParams);
                viewHolder.totalPriseText1.addView(image);
            }
        }
        if (totalPrise > 2 && totalPrise < 3){
            ImageView image = new ImageView(context);
            image.setBackgroundResource(R.drawable.star3);
            wrapParams = new LinearLayout.LayoutParams(150,30);
            image.setLayoutParams(wrapParams);
            viewHolder.totalPriseText1.addView(image);
        }
        if (totalPrise == 3){
            for (double i = 0; i < 3; i++){
                ImageView image = new ImageView(context);
                image.setBackgroundResource(R.drawable.star1);
                wrapParams = new LinearLayout.LayoutParams(30,30);
                image.setLayoutParams(wrapParams);
                viewHolder.totalPriseText1.addView(image);
            }
        }
        if (totalPrise > 3 && totalPrise < 4){
            ImageView image = new ImageView(context);
            image.setBackgroundResource(R.drawable.star4);
            wrapParams = new LinearLayout.LayoutParams(150,30);
            image.setLayoutParams(wrapParams);
            viewHolder.totalPriseText1.addView(image);
        }
        if (totalPrise == 4){
            for (double i = 0; i < 4; i++){
                ImageView image = new ImageView(context);
                image.setBackgroundResource(R.drawable.star1);
                wrapParams = new LinearLayout.LayoutParams(30,30);
                image.setLayoutParams(wrapParams);
                viewHolder.totalPriseText1.addView(image);
            }
        }
        if (totalPrise > 4 && totalPrise < 5){
            ImageView image = new ImageView(context);
            image.setBackgroundResource(R.drawable.star5);
            wrapParams = new LinearLayout.LayoutParams(150,30);
            image.setLayoutParams(wrapParams);
            viewHolder.totalPriseText1.addView(image);
        }
        if (totalPrise == 5){
            for (double i = 0; i < 5; i++){
                ImageView image = new ImageView(context);
                image.setBackgroundResource(R.drawable.star1);
                wrapParams = new LinearLayout.LayoutParams(30,30);
                image.setLayoutParams(wrapParams);
                viewHolder.totalPriseText1.addView(image);
            }
        }
        Glide.with(context).load(coachDetailAction.getFile()).placeholder(R.drawable.head1).error(R.drawable.head1).into(viewHolder.coachPic);
        viewHolder.coachPic.setTag(R.id.imageloader_uri,coachDetailAction.getFile());
        viewHolder.coachName.setText(coachDetailAction.getName());
        viewHolder.place.setText(coachDetailAction.getPtschool());
        viewHolder.totalPriseText2.setText(coachDetailAction.getPtzonghe()+"分");
        viewHolder.goodPrise.setText(coachDetailAction.getPtpraise());
        viewHolder.tid.setText(coachDetailAction.getPttid());
        viewHolder.tv_stunum.setText("累计所带学员" + coachDetailAction.getTotalnum() + "人");
        viewHolder.kemu.setVisibility(View.GONE);
        return convertView;
    }
    //listView优化
    static class ViewHolder {
        public ImageView coachPic,kemu;
        public TextView coachName, place,classType,totalPriseText2,goodPrise,tv_stunum;
        private TextView tid;
        public LinearLayout totalPriseText1;
    }

}
