package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.ReservationActivity;
import com.jgkj.bxxc.activity.ReservationDetailActivity;
import com.jgkj.bxxc.activity.ReservationForPrivateActivity;
import com.jgkj.bxxc.bean.entity.MyCoachForPrivateEntity.MyCoachPrivaetEntity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;


public class MyCoachPrivateAdapter extends BaseAdapter {
    private Context context;
    private List<MyCoachPrivaetEntity> list;
    private LayoutInflater inflater;
    private String coachUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/CoachinfoAgain";
    private int uid;
    private String token;

    public MyCoachPrivateAdapter(Context context, List<MyCoachPrivaetEntity> list,int uid,String token){
        this.context = context;
        this.list = list;
        this.uid = uid;
        this.token = token;
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
            convertView = inflater.inflate(R.layout.my_coach_private_item, parent, false);
            viewHolder.coachName = (TextView) convertView.findViewById(R.id.coachName);
            viewHolder.place = (TextView) convertView.findViewById(R.id.place);
            viewHolder.goodPrise = (TextView) convertView.findViewById(R.id.goodPrise);
            viewHolder.totalPriseText1 = (LinearLayout) convertView.findViewById(R.id.totalPriseText1);
            viewHolder.totalPriseText2 = (TextView) convertView.findViewById(R.id.totalPriseText2);
            viewHolder.kemu = (ImageView) convertView.findViewById(R.id.kemu);
            viewHolder.coachPic = (ImageView) convertView.findViewById(R.id.coachPic);
            viewHolder.tv_total_stu = (TextView) convertView.findViewById(R.id.tv_total_stu);
            viewHolder.linear = (LinearLayout) convertView.findViewById(R.id.linear);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.totalPriseText1.removeAllViews();
        for (int k = 0; k < Integer.parseInt(list.get(position).getZonghe()); k++) {
            ImageView image = new ImageView(context);
            image.setBackgroundResource(R.drawable.star1);
            LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(30, 30);
            image.setLayoutParams(wrapParams);
            viewHolder.totalPriseText1.addView(image);
        }

        if (list.get(position).getClass_type().equals("科目二教练") ){
            viewHolder.kemu.setImageResource(R.drawable.kemu);
        }else if(list.get(position).getClass_type().equals("科目三教练") ){
            viewHolder.kemu.setImageResource(R.drawable.kemu3);
        }

        Glide.with(context).load(list.get(position).getFile()).placeholder(R.drawable.head1).error(R.drawable.head1).into(viewHolder.coachPic);

        viewHolder.tv_total_stu.setText("累计所带学员" + list.get(position).getNowstudent() + "人");
        viewHolder.totalPriseText2.setText(list.get(position).getZonghe() + ".0分");
        viewHolder.coachName.setText(list.get(position).getCoachname());
        viewHolder.place.setText(list.get(position).getFaddress());
        viewHolder.goodPrise.setText(list.get(position).getPraise() + "%");

        viewHolder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(list.get(position).getCid(),coachUrl);
            }
        });

        return convertView;
    }

    class ViewHolder {
        public TextView coachName;
        public TextView place,tv_total_stu;
        public TextView goodPrise;
        public LinearLayout totalPriseText1,linear;
        public TextView totalPriseText2;
        public ImageView coachPic,kemu;
    }

    /**
     * 根据cid(教练id)获取教练信息
     *
     * @param coachId 教练信息
     * @param url     请求地址
     */
    private void getData(String coachId, String url) {
        OkHttpUtils
                .post()
                .url(url)
                .addParams("cid", coachId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(context, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Intent intent2 = new Intent();
                        intent2.setClass(context, ReservationDetailActivity.class);
                        intent2.putExtra("uid",uid);
                        intent2.putExtra("token",token);
                        intent2.putExtra("coachInfo", s);
                        context.startActivity(intent2);
                    }
                });
    }

}
