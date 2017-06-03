package com.jgkj.bxxc.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.BuyClassHoursActivity;
import com.jgkj.bxxc.bean.entity.ConfirmReservationEntity.ConfirmReservationResult;
import com.jgkj.bxxc.bean.entity.MenuEntity.MenuEntitys;
import com.jgkj.bxxc.bean.entity.MenuEntity.MenuResults;
import com.jgkj.bxxc.bean.entity.ReservationDetailEntity.Stusubject;
import com.jgkj.bxxc.bean.entity.ReservationDetailEntity.Subject;
import com.jgkj.bxxc.tools.BuyClassHoDialog;
import com.jgkj.bxxc.tools.RemindDialog;
import com.jgkj.bxxc.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.jgkj.bxxc.R.id.btn_reservation;


/**
 * Created by shijun on 2017/4/23.
 */

public class ReservationDetailAdapter extends BaseAdapter {
    private Context context;
    private List<Subject> list;
    private List<Stusubject> stuList;
    private LayoutInflater inflater;
    private String price;
    private String address;
    private String day;
    private int uid;
    private String token;
    private String cid;
    private String flag_class;
    private ListView listView;

    //时间段
    private String time_slot;
    //日期
    private String timeone;
    //记住预约点击的位置
    private List<String> positionList = new ArrayList<>();
    //记住取消预约点击的位置
    private List<String> positionCancelList = new ArrayList<>();

    public ReservationDetailAdapter(Context context, List<Subject> list,List<Stusubject> stuList,String price,String address,String day,int uid,String token,String cid,String flag_class){
        this.context = context;
        this.list = list;
        this.stuList = stuList;
        this.address = address;
        this.price = price;
        this.day = day;
        this.uid = uid;
        this.cid = cid;
        this.token = token;
        this.flag_class = flag_class;
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

    public void setData(List<Subject> listData){
        list = listData;
    }

    @Override
    public View getView(final int position,View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.reservation_item, parent, false);
            viewHolder.tv_timeslot = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_time_number = (TextView) convertView.findViewById(R.id.tv_time_number);
            viewHolder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            viewHolder.tv_subjectName = (TextView) convertView.findViewById(R.id.tv_subjectName);
            viewHolder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            viewHolder.btn_reservation = (Button) convertView.findViewById(btn_reservation);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_timeslot.setText(list.get(position).getTimeslot());
        viewHolder.tv_time_number.setText(list.get(position).getClasshour() + "学时");

        DecimalFormat df = new DecimalFormat("#.00");
        viewHolder.tv_price.setText("￥ " + df.format(Float.parseFloat(price)*list.get(position).getClasshour()));
        viewHolder.tv_address.setText(address);
        viewHolder.tv_subjectName.setText(flag_class);

        if(list.get(position).getCount() == 0){
            if(!checkPosition(positionList,position)){
                viewHolder.btn_reservation.setText("可预约");
                viewHolder.btn_reservation.setTextColor(context.getResources().getColor(R.color.btn_blue));
                viewHolder.btn_reservation.setBackgroundResource(R.drawable.btn_style_blue);
            }else{
                if(!checkPosition(positionCancelList,position)){
                    viewHolder.btn_reservation.setText("取消预约");
                    viewHolder.btn_reservation.setTextColor(context.getResources().getColor(R.color.btn_orange));
                    viewHolder.btn_reservation.setBackgroundResource(R.drawable.btn_style_oragen);
                }else{
                    if(!viewHolder.btn_reservation.getText().toString().equals("取消预约")){
                        viewHolder.btn_reservation.setText("可预约");
                        viewHolder.btn_reservation.setTextColor(context.getResources().getColor(R.color.btn_blue));
                        viewHolder.btn_reservation.setBackgroundResource(R.drawable.btn_style_blue);
                    }else{
                        viewHolder.btn_reservation.setText("取消预约");
                        viewHolder.btn_reservation.setTextColor(context.getResources().getColor(R.color.btn_orange));
                        viewHolder.btn_reservation.setBackgroundResource(R.drawable.btn_style_oragen);
                    }
                }
            }
        }
        if(list.get(position).getCount() == 1){
            if(check(list.get(position).getTimeslot())){
                if(!checkPosition(positionCancelList,position)){
                    viewHolder.btn_reservation.setText("取消预约");
                    viewHolder.btn_reservation.setTextColor(context.getResources().getColor(R.color.btn_orange));
                    viewHolder.btn_reservation.setBackgroundResource(R.drawable.btn_style_oragen);
                }else{
                    if(!viewHolder.btn_reservation.getText().toString().equals("取消预约")){
                        viewHolder.btn_reservation.setText("可预约");
                        viewHolder.btn_reservation.setTextColor(context.getResources().getColor(R.color.btn_blue));
                        viewHolder.btn_reservation.setBackgroundResource(R.drawable.btn_style_blue);
                    }else{
                        viewHolder.btn_reservation.setText("取消预约");
                        viewHolder.btn_reservation.setTextColor(context.getResources().getColor(R.color.btn_orange));
                        viewHolder.btn_reservation.setBackgroundResource(R.drawable.btn_style_oragen);
                    }
                }
            }else{
                viewHolder.btn_reservation.setText("已满员");
                viewHolder.btn_reservation.setTextColor(context.getResources().getColor(R.color.btn_gray));
                viewHolder.btn_reservation.setBackgroundResource(R.drawable.btn_style_gray);
            }
        }
        viewHolder.btn_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.btn_reservation.getText().toString().equals("可预约")){
                    time_slot = list.get(position).getTimeslot();
                    timeone = list.get(position).getTimeone();
                    getData(uid,token, Urls.Hours,viewHolder.btn_reservation,position);
                }
                if(viewHolder.btn_reservation.getText().toString().equals("取消预约")){
                    positionCancelList.add(position + "");
                    //提示是否取消
                    new RemindDialog(context,"确定要取消预约？",uid,token,cid,list.get(position).getTimeone(),list.get(position).getTimeslot(),Urls.quxiaoApply,viewHolder.btn_reservation).call();
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        public TextView tv_subjectName;
        public TextView tv_timeslot;
        public TextView tv_time_number;
        public TextView tv_price;
        public TextView tv_address;
        public Button btn_reservation;
    }

    //截取字符串
    public String subString(String str){
        int length = str.length();
        return str.substring(length-3,length-1);
    }

    //检测学员信息
    public boolean check(String time){
        for (Stusubject subject: stuList) {
            if(subString(subject.getDay()).equals(day) &&time.equals(subject.getTime_slot())){
                return true;
            }
        }
        return false;
    }

    //检测点击信息
    public boolean checkPosition(List<String> tempList,int i){
        for (String ss: tempList) {
            if(ss.equals(i + "")){
                return true;
            }
        }
        return false;
    }

    public void showCustomServiceAlert(final Button btn,final int position) {
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.menu_dialog);
        Button btn_buy_menu = (Button) window.findViewById(R.id.btn_buy_menu);
        TextView choose_tv = (TextView) window.findViewById(R.id.choose_tv);
        choose_tv.setVisibility(View.GONE);
        ImageView im_canael = (ImageView) window.findViewById(R.id.im_canael);
        TextView tv_buy_class_hours = (TextView) window.findViewById(R.id.tv_buy_class_hours);
        listView = (ListView) window.findViewById(R.id.listView);
        btn_buy_menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(BuyMenuAdapter.flag){
                    getDataForReservation(uid,cid,token,time_slot,timeone,BuyMenuAdapter.package_id,Urls.stuAppointmentpackage,btn,position);
                    BuyMenuAdapter.package_id = null;
                    BuyMenuAdapter.flag = false;
                    dlg.cancel();
                }else{
                    Toast.makeText(context, "请选择一种套餐", Toast.LENGTH_LONG).show();
                }
            }
        });

        im_canael.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.cancel();
            }
        });

        tv_buy_class_hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(context, BuyClassHoursActivity.class);
                intent2.putExtra("uid",uid);
                intent2.putExtra("cid",cid);//token
                intent2.putExtra("token",token);
                context.startActivity(intent2);
                dlg.cancel();
            }
        });
    }

    /**
     *
     * @param uid_
     * @param token_
     * @param url
     */
    private void getData(int uid_, String token_, String url,final Button btn,final int position) {
        Log.i("百信学车","套餐信息参数" + "uid=" + uid_ + "   token=" + token_ + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("uid", Integer.toString(uid))
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(context, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        MenuResults menuResult = gson.fromJson(s, MenuResults.class);
                        List<MenuEntitys> result = menuResult.getResult();
                        Log.i("百信学车","套餐详细信息结果" + s);
                        if (menuResult.getCode() == 200) {
                            showCustomServiceAlert(btn,position);
                            BuyMenuAdapter adapter = new BuyMenuAdapter(context,result);
                            listView.setAdapter(adapter);
                        }
                        if(menuResult.getCode() == 400){
                            new BuyClassHoDialog(context,"你暂未购买学时，请先去购买学时",uid,cid,token).call();
                        }
                    }
                });
    }

    private void getDataForReservation(int uid, String cid,String token, String time_slot,String day,String package_id,String url,final Button btn,final int position) {
        Log.i("百信学车","确认预约教练参数" + "uid=" + uid + "   token=" + token + "   cid=" + cid + "   timme_slot=" + time_slot + "   day=" + day + "   package_id=" + package_id + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("uid", Integer.toString(uid))
                .addParams("cid", cid)
                .addParams("token", token)
                .addParams("time_slot", time_slot)
                .addParams("day", day)
                .addParams("package_id", package_id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(context, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","确认预约教练结果" + s);
                        Gson gson = new Gson();
                        ConfirmReservationResult confirmReservationResult = gson.fromJson(s, ConfirmReservationResult.class);
                        if (confirmReservationResult.getCode() == 200) {
                            Toast.makeText(context, "预约成功", Toast.LENGTH_LONG).show();
                            btn.setText("取消预约");
                            btn.setTextColor(context.getResources().getColor(R.color.btn_orange));
                            btn.setBackgroundResource(R.drawable.btn_style_oragen);
                            positionList.add(position + "");
                            updata();
                        }else if (confirmReservationResult.getCode() == 400) {
                            Toast.makeText(context,confirmReservationResult.getReason() , Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(context, "预约失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //广播更新数据
    public void updata() {
        Intent intent = new Intent();
        intent.setAction("updataApp");
        context.sendBroadcast(intent);
    }



}
