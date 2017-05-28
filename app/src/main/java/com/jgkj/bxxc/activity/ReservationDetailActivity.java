package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.ReservationDetailAdapter;
import com.jgkj.bxxc.bean.CoachInfo;
import com.jgkj.bxxc.bean.entity.ReservationDetailEntity.ReservationDetailEntity;
import com.jgkj.bxxc.bean.entity.ReservationDetailEntity.Stusubject;
import com.jgkj.bxxc.bean.entity.ReservationDetailEntity.Subject;
import com.jgkj.bxxc.tools.RemainBaseDialog;
import com.jgkj.bxxc.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;



/**
 * Created by tongshoujun on 2017/5/10.
 */

public class ReservationDetailActivity extends Activity implements View.OnClickListener {

    private int uid;
    private String token;
    private String cid;
    private String coachInfo = null;
    //标题
    private TextView title;
    private Button button_backward;
    private Button remind;
    //教练图片
    private ImageView im_coachPic;
    //教练姓名
    private TextView tv_coachName;
    //累计学员
    private TextView tv_total_stu;
    private LinearLayout zhiliang;
    //质量
    private int teachnum;
    private ListView listView;
    private TextView noSmsData;
    private ReservationDetailAdapter adapter;
    private ProgressDialog progressDialog;

    private TextView tv1_week;
    private TextView tv2_week;
    private TextView tv3_week;
    private TextView tv4_week;
    private TextView tv5_week;
    private TextView tv6_week;
    private TextView tv7_week;

    private TextView tv1_number;
    private TextView tv2_number;
    private TextView tv3_number;
    private TextView tv4_number;
    private TextView tv5_number;
    private TextView tv6_number;
    private TextView tv7_number;

    private TextView tv_bg_01;
    private TextView tv_bg_02;
    private TextView tv_bg_03;
    private TextView tv_bg_04;
    private TextView tv_bg_05;
    private TextView tv_bg_06;
    private TextView tv_bg_07;

    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private LinearLayout linearLayout3;
    private LinearLayout linearLayout4;
    private LinearLayout linearLayout5;
    private LinearLayout linearLayout6;
    private LinearLayout linearLayout7;

    //学生信息
    private List<Stusubject> stusubjectList;
    //教练信息
    private List<Subject> subjectList;
    //保存教练信息
    private List<Subject> subjectListResult;

    //价格
    private String price;
    //地址
    private String address;

    //广播接收更新数据
    protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            upData(cid,uid,token, Urls.ptcourse);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);

        //显示ProgressDialog
        progressDialog = ProgressDialog.show(ReservationDetailActivity.this, "加载中...", "请等待...", true, false);

        //标题
        title = (TextView) findViewById(R.id.text_title);
        title.setText(getDate());
        button_backward = (Button) findViewById(R.id.button_backward);
        remind = (Button) findViewById(R.id.button_forward);
        button_backward.setVisibility(View.VISIBLE);
        button_backward.setOnClickListener(this);
        remind.setOnClickListener(this);
        remind.setVisibility(View.VISIBLE);
        remind.setText("预约须知");

        im_coachPic = (ImageView)findViewById(R.id.im_coachPic);
        tv_coachName = (TextView)findViewById(R.id.tv_coachName);
        tv_total_stu = (TextView)findViewById(R.id.tv_total_stu);
        zhiliang = (LinearLayout)findViewById(R.id.zhiliang);
        listView = (ListView)findViewById(R.id.listView);
        noSmsData = (TextView)findViewById(R.id.noSmsData);
        listView.setEmptyView(noSmsData);

        Intent intent = getIntent();
        uid = intent.getIntExtra("uid",uid);
        coachInfo = intent.getStringExtra("coachInfo");
        token = intent.getStringExtra("token");

        Gson gson = new Gson();
        CoachInfo coachInfoResult = gson.fromJson(coachInfo, CoachInfo.class);
        List<CoachInfo.Result> list = coachInfoResult.getResult();
        CoachInfo.Result result = list.get(0);
        tv_coachName.setText(result.getCoachname());
        tv_total_stu.setText(result.getCount_stu() + "");
        teachnum = Integer.parseInt(result.getTeach());

        //教学质量
        for (int k = 0; k < teachnum; k++) {
            ImageView image = new ImageView(ReservationDetailActivity.this);
            image.setBackgroundResource(R.drawable.star1);
            LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(30, 30);
            image.setLayoutParams(wrapParams);
            zhiliang.addView(image);
        }

        //头像
        Glide.with(this).load(result.getFile()).placeholder(R.drawable.head1).error(R.drawable.head1).into(im_coachPic);

        //请求信息
        cid = Integer.toString(result.getCid());
        getData(cid,uid,token, Urls.ptcourse);

        tv1_week = (TextView)findViewById(R.id.tv1_week);
        tv2_week = (TextView)findViewById(R.id.tv2_week);
        tv3_week = (TextView)findViewById(R.id.tv3_week);
        tv4_week = (TextView)findViewById(R.id.tv4_week);
        tv5_week = (TextView)findViewById(R.id.tv5_week);
        tv6_week = (TextView)findViewById(R.id.tv6_week);
        tv7_week = (TextView)findViewById(R.id.tv7_week);

        tv1_number = (TextView)findViewById(R.id.tv1_number);
        tv2_number = (TextView)findViewById(R.id.tv2_number);
        tv3_number = (TextView)findViewById(R.id.tv3_number);
        tv4_number = (TextView)findViewById(R.id.tv4_number);
        tv5_number = (TextView)findViewById(R.id.tv5_number);
        tv6_number = (TextView)findViewById(R.id.tv6_number);
        tv7_number = (TextView)findViewById(R.id.tv7_number);

        tv_bg_01 = (TextView)findViewById(R.id.tv_bg_01);
        tv_bg_02 = (TextView)findViewById(R.id.tv_bg_02);
        tv_bg_03 = (TextView)findViewById(R.id.tv_bg_03);
        tv_bg_04 = (TextView)findViewById(R.id.tv_bg_04);
        tv_bg_05 = (TextView)findViewById(R.id.tv_bg_05);
        tv_bg_06 = (TextView)findViewById(R.id.tv_bg_06);
        tv_bg_07 = (TextView)findViewById(R.id.tv_bg_07);

        linearLayout1 = (LinearLayout)findViewById(R.id.linearLayout1);
        linearLayout2 = (LinearLayout)findViewById(R.id.linearLayout2);
        linearLayout3 = (LinearLayout)findViewById(R.id.linearLayout3);
        linearLayout4 = (LinearLayout)findViewById(R.id.linearLayout4);
        linearLayout5 = (LinearLayout)findViewById(R.id.linearLayout5);
        linearLayout6 = (LinearLayout)findViewById(R.id.linearLayout6);
        linearLayout7 = (LinearLayout)findViewById(R.id.linearLayout7);

        linearLayout1.setOnClickListener(this);
        linearLayout2.setOnClickListener(this);
        linearLayout3.setOnClickListener(this);
        linearLayout4.setOnClickListener(this);
        linearLayout5.setOnClickListener(this);
        linearLayout6.setOnClickListener(this);
        linearLayout7.setOnClickListener(this);

        //设置星期
        tv1_week.setText(getWeek(0));
        tv2_week.setText(getWeek(1));
        tv3_week.setText(getWeek(2));
        tv4_week.setText(getWeek(3));
        tv5_week.setText(getWeek(4));
        tv6_week.setText(getWeek(5));
        tv7_week.setText(getWeek(6));

        //设置日期号
        tv1_number.setText(getDay(0));
        tv2_number.setText(getDay(1));
        tv3_number.setText(getDay(2));
        tv4_number.setText(getDay(3));
        tv5_number.setText(getDay(4));
        tv6_number.setText(getDay(5));
        tv7_number.setText(getDay(6));

        tv_bg_01.setBackgroundResource(R.color.list_text_select_color);
        tv_bg_02.setBackgroundResource(R.color.white);
        tv_bg_03.setBackgroundResource(R.color.white);
        tv_bg_04.setBackgroundResource(R.color.white);
        tv_bg_05.setBackgroundResource(R.color.white);
        tv_bg_06.setBackgroundResource(R.color.white);
        tv_bg_07.setBackgroundResource(R.color.white);

    }

    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.linearLayout1:
                tv_bg_01.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.white);

                subjectListResult = subjectList(tv1_number.getText().toString());
                adapter = new ReservationDetailAdapter(ReservationDetailActivity.this,subjectListResult,stusubjectList,price,address,tv1_number.getText().toString(),uid,token,cid);
                listView.setAdapter(adapter);
                break;
            case R.id.linearLayout2:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.white);

                subjectListResult = subjectList(tv2_number.getText().toString());
                adapter = new ReservationDetailAdapter(ReservationDetailActivity.this,subjectListResult,stusubjectList,price,address,tv2_number.getText().toString(),uid,token,cid);
                listView.setAdapter(adapter);
                break;
            case R.id.linearLayout3:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.white);

                subjectListResult = subjectList(tv3_number.getText().toString());
                adapter = new ReservationDetailAdapter(ReservationDetailActivity.this,subjectListResult,stusubjectList,price,address,tv3_number.getText().toString(),uid,token,cid);
                listView.setAdapter(adapter);
                break;
            case R.id.linearLayout4:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.white);

                subjectListResult = subjectList(tv4_number.getText().toString());
                adapter = new ReservationDetailAdapter(ReservationDetailActivity.this,subjectListResult,stusubjectList,price,address,tv4_number.getText().toString(),uid,token,cid);
                listView.setAdapter(adapter);
                break;
            case R.id.linearLayout5:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.white);

                subjectListResult = subjectList(tv5_number.getText().toString());
                adapter = new ReservationDetailAdapter(ReservationDetailActivity.this,subjectListResult,stusubjectList,price,address,tv5_number.getText().toString(),uid,token,cid);
                listView.setAdapter(adapter);
                break;
            case R.id.linearLayout6:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_07.setBackgroundResource(R.color.white);

                subjectListResult = subjectList(tv6_number.getText().toString());
                adapter = new ReservationDetailAdapter(ReservationDetailActivity.this,subjectListResult,stusubjectList,price,address,tv6_number.getText().toString(),uid,token,cid);
                listView.setAdapter(adapter);
                break;
            case R.id.linearLayout7:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.list_text_select_color);

                subjectListResult = subjectList(tv7_number.getText().toString());
                adapter = new ReservationDetailAdapter(ReservationDetailActivity.this,subjectListResult,stusubjectList,price,address,tv7_number.getText().toString(),uid,token,cid);
                listView.setAdapter(adapter);
                break;
            case R.id.button_forward:
                new RemainBaseDialog(ReservationDetailActivity.this,"每次预约以两个学时起，您可以根据实际需求，选购学时套餐，预约您的心意教练，" +
                        "体验高品质的驾培服务。您需要注意：如果约车已经下单，但有突发事件不能如时赴约，请在约定时间两小时前申请取消，否则将视为您的约车行为已经实施，不能退款。").call();
                break;
            case R.id.button_backward:
                finish();
                break;
            default:
                break;
        }
    }

    //获取一个星期日期 格式dd
    public String getDay(int day){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        //System.out.println("当前日期："+sf.format(c.getTime()));
        c.add(Calendar.DAY_OF_MONTH, day);
        String dd = sf.format(c.getTime()).toString();
        return dd.substring(dd.length()-2,dd.length());
    }

    //日期转星期
    public String dateToWeek(Date dt) {
        String[] weekDays = {"周末", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    //获取星期
    public String getWeek(int day){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        //System.out.println("当前日期："+sf.format(c.getTime()));
        c.add(Calendar.DAY_OF_MONTH, day);
        String dd = sf.format(c.getTime()).toString();
        String week = "";
        try {
            Date date = sf.parse(dd);
            week = dateToWeek(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return week;
    }

    //获取当前年月
    public String getDate(){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        return year + "年" + month + "月";
    }

    /**
     * 根据cid(教练id)获取教练信息
     * @param coachId 教练信息
     * @param url     请求地址
     */
    private void getData(String coachId, int uid_, String token_, String url) {
        Log.i("百信学车","预约教练详细信息参数" + "cid=" + coachId + "   uid=" + uid_ + "   token=" + token_ + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("cid", coachId)
                .addParams("uid", Integer.toString(uid_))
                .addParams("token", token_)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(ReservationDetailActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                        //关闭ProgressDialog
                        progressDialog.dismiss();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        //关闭ProgressDialog
                        Log.i("百信学车","预约教练详细信息结果" + s);
                        progressDialog.dismiss();
                        Gson gson = new Gson();
                        ReservationDetailEntity reservationDetailEntity = gson.fromJson(s, ReservationDetailEntity.class);
                        if (reservationDetailEntity.getCode() == 200) {
                            price = reservationDetailEntity.getResult().getPrice();
                            address = reservationDetailEntity.getResult().getFaddress();
                            stusubjectList = reservationDetailEntity.getResult().getStusubject();
                            subjectList = reservationDetailEntity.getResult().getSubject();
                            subjectListResult = subjectList(tv1_number.getText().toString());
                            adapter = new ReservationDetailAdapter(ReservationDetailActivity.this,subjectListResult,stusubjectList,price,address,tv1_number.getText().toString(),uid,token,cid);
                            listView.setAdapter(adapter);
                        }
                    }
                });
    }

    /**
     * 根据cid(教练id)获取教练信息
     * @param coachId 教练信息
     * @param url     请求地址
     */
    private void upData(String coachId, int uid_, String token_, String url) {
        Log.i("百信学车","预约教练详细信息参数" + "cid=" + coachId + "   uid=" + uid_ + "   token=" + token_ + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("cid", coachId)
                .addParams("uid", Integer.toString(uid_))
                .addParams("token", token_)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(ReservationDetailActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        ReservationDetailEntity reservationDetailEntity = gson.fromJson(s, ReservationDetailEntity.class);
                        Log.i("百信学车","预约教练详细信息结果" + s);
                        if (reservationDetailEntity.getCode() == 200) {
                            price = reservationDetailEntity.getResult().getPrice();
                            address = reservationDetailEntity.getResult().getFaddress();
                            stusubjectList = reservationDetailEntity.getResult().getStusubject();
                            subjectList = reservationDetailEntity.getResult().getSubject();
                            subjectListResult = subjectList(tv1_number.getText().toString());
                            //adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    //获取当天数据
    public List<Subject> subjectList(String day){
        List<Subject> subjectTempList = new ArrayList<>();
        for (Subject subject: subjectList) {
            if(subString(subject.getTimeone()).equals(day)){
                subjectTempList.add(subject);
            }
        }
        return subjectTempList;
    }

    //截取字符串
    public String subString(String str){
        int length = str.length();
        return str.substring(length-3,length-1);
    }

    public void onResume() {
        super.onResume();
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("updataApp");
        registerReceiver(this.broadcastReceiver, filter);
    }


}
