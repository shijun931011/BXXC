package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.util.Log;
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
public class MyCoachAdapter extends BaseAdapter {
    private Context context;
    private List<CoachDetailAction.Result> list;
    private LayoutInflater inflater;
    private CoachDetailAction.Result coachDetailAction;
    private LinearLayout.LayoutParams wrapParams;

    public MyCoachAdapter(Context context, List<CoachDetailAction.Result> list) {
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
            convertView = inflater.inflate(R.layout.coach_jingdian_item, parent, false);
            viewHolder.coachName = (TextView) convertView.findViewById(R.id.coachName);
            viewHolder.kemu = (ImageView) convertView.findViewById(R.id.kemu);
            viewHolder.place = (TextView) convertView.findViewById(R.id.place);
            viewHolder.coachId = (TextView) convertView.findViewById(R.id.coachId);
            viewHolder.classType = (TextView) convertView.findViewById(R.id.classType);
            viewHolder.totalPriseText2 = (TextView) convertView.findViewById(R.id.totalPriseText2);
            viewHolder.goodPrise = (TextView) convertView.findViewById(R.id.goodPrise);
            viewHolder.adopt = (TextView) convertView.findViewById(R.id.adopt);
            viewHolder.coachPic = (ImageView) convertView.findViewById(R.id.coachPic);
            viewHolder.totalPriseText1 = (LinearLayout) convertView.findViewById(R.id.totalPriseText1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        coachDetailAction = list.get(position);
        Double totalPrise = Double.parseDouble(coachDetailAction.getZonghe());
        Log.d("BXXC","综合评价："+totalPrise);
        viewHolder.totalPriseText1.removeAllViews();

//        for (int i = 0; i < totalPrise; i++) {
//            ImageView image = new ImageView(this.context);
//            image.setBackgroundResource(R.drawable.star1);
//            wrapParams = new LinearLayout.LayoutParams(30,30);
//            image.setLayoutParams(wrapParams);
//            viewHolder.totalPriseText1.addView(image);
//        }
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
            for (float i = 0; i < 2; i++){
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
            for (float i = 0; i < 3; i++){
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
            for (float i = 0; i < 4; i++){
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
            for (float i = 0; i < 5; i++){
                ImageView image = new ImageView(context);
                image.setBackgroundResource(R.drawable.star1);
                wrapParams = new LinearLayout.LayoutParams(30,30);
                image.setLayoutParams(wrapParams);
                viewHolder.totalPriseText1.addView(image);
            }
        }

        Glide.with(context).load(coachDetailAction.getFile()).placeholder(R.drawable.head1).error(R.drawable.head1).into(viewHolder.coachPic);
        viewHolder.coachPic.setTag(R.id.imageloader_uri,coachDetailAction.getFile());
        viewHolder.coachName.setText(coachDetailAction.getCoachname());
        viewHolder.place.setText(coachDetailAction.getFaddress());
        viewHolder.classType.setText("班级:"+coachDetailAction.getClass_class());
        viewHolder.totalPriseText2.setText(coachDetailAction.getZonghe()+ "分");
        viewHolder.goodPrise.setText("好评率:"+coachDetailAction.getPraise()+"%");
        viewHolder.adopt.setText("通过率:"+coachDetailAction.getPass()+"%");
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
        public TextView coachName, place,classType,totalPriseText2,goodPrise,adopt;
        private TextView coachId;
        public LinearLayout totalPriseText1;
    }

}
