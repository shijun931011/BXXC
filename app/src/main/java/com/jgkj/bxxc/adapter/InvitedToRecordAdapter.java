package com.jgkj.bxxc.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.InvitedToRecordActivity;
import com.jgkj.bxxc.bean.Invite;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.tools.BangDingBankCardDialog;
import com.jgkj.bxxc.tools.TiXianDialog;

import java.util.List;

/**
 * Created by shijunon 2017/4/24.
 * 邀请学车记录
 */

public class InvitedToRecordAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Invite.Result> list;

    public InvitedToRecordAdapter(Context context,List<Invite.Result> list){
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.invited_to_record_listview, viewGroup, false);
            viewHolder.friend_account=(TextView) view.findViewById(R.id.friend_account);
            viewHolder.friend_status=(TextView) view.findViewById(R.id.friend_status);
            viewHolder.money=(TextView) view.findViewById(R.id.money);
            viewHolder.Withdrawals=(Button) view.findViewById(R.id.tixian);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        list.get(i);
        viewHolder.friend_account.setText(subString(list.get(i).getInviter()));
        viewHolder.friend_status.setText(list.get(i).getInvitetime());
        viewHolder.money.setText("￥"+list.get(i).getInvitered());
        /**
         1.未报名
         2.报名成功
         3.完善信息审核成功
         4.完善信息审核失败
         5.面签正在审核中
         6.体检正在审核中
         7.面签通过
         8.体检通过
         9.科目一审核中
         10.科目一审核通过
         11.科目一未通过
         */
        if(list.get(i).getInvitetime().equals("未报名") || list.get(i).getInvitetime().equals("报名成功") || list.get(i).getInvitetime().equals("完善信息审核成功")
                || list.get(i).getInvitetime().equals("完善信息审核失败") || list.get(i).getInvitetime().equals("面签正在审核中")
                || list.get(i).getInvitetime().equals("体检正在审核中") || list.get(i).getInvitetime().equals("面签通过")
                || list.get(i).getInvitetime().equals("体检通过") || list.get(i).getInvitetime().equals("科目一审核中")
                || list.get(i).getInvitetime().equals("科目一审核通过") || list.get(i).getInvitetime().equals("科目一未通过"))
        {
            viewHolder.money.setTextColor(context.getResources().getColor(R.color.btn_gray));
            viewHolder.Withdrawals.setTextColor(context.getResources().getColor(R.color.btn_gray));
            viewHolder.Withdrawals.setText("提现");
            viewHolder.Withdrawals.setBackgroundResource(R.drawable.btn_style_gray);
        }else{
            if(list.get(i).getInvitestate().equals("1")){
                viewHolder.money.setTextColor(context.getResources().getColor(R.color.green));
                viewHolder.Withdrawals.setTextColor(context.getResources().getColor(R.color.green));
                viewHolder.Withdrawals.setText("提现");
                viewHolder.Withdrawals.setBackgroundResource(R.drawable.btn_style_green);
            }
            if(list.get(i).getInvitestate().equals("2")){
                viewHolder.money.setTextColor(context.getResources().getColor(R.color.btn_orange));
                viewHolder.Withdrawals.setTextColor(context.getResources().getColor(R.color.btn_orange));
                viewHolder.Withdrawals.setText("入账中");
                viewHolder.Withdrawals.setBackgroundResource(R.drawable.btn_style_oragen);
            }
            if(list.get(i).getInvitestate().equals("3")){
                viewHolder.money.setTextColor(context.getResources().getColor(R.color.btn_gray));
                viewHolder.Withdrawals.setTextColor(context.getResources().getColor(R.color.btn_gray));
                viewHolder.Withdrawals.setText("已提现");
                viewHolder.Withdrawals.setBackgroundResource(R.drawable.btn_style_blue);
            }

        }

        viewHolder.Withdrawals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(list.get(i).getInvitetime().equals("未报名") || list.get(i).getInvitetime().equals("报名成功") || list.get(i).getInvitetime().equals("完善信息审核成功")
                        || list.get(i).getInvitetime().equals("完善信息审核失败") || list.get(i).getInvitetime().equals("面签正在审核中")
                        || list.get(i).getInvitetime().equals("体检正在审核中") || list.get(i).getInvitetime().equals("面签通过")
                        || list.get(i).getInvitetime().equals("体检通过") || list.get(i).getInvitetime().equals("科目一审核中")
                        || list.get(i).getInvitetime().equals("科目一审核通过") || list.get(i).getInvitetime().equals("科目一未通过")) && list.get(i).getInvitestate().equals("1")){
                    SharedPreferences spp = context.getSharedPreferences("useraccount", Activity.MODE_PRIVATE);
                    String account = spp.getString("useraccount", null);
                    if(account == null){
                        new BangDingBankCardDialog(context,"你还没有绑定银行卡，请先去绑定银行卡").call();
                    }else{
                        SharedPreferences sp = context.getSharedPreferences("USER", Activity.MODE_PRIVATE);
                        String str = sp.getString("userInfo", null);
                        Gson gson = new Gson();
                        UserInfo userInfo = gson.fromJson(str, UserInfo.class);

                        SharedPreferences sp1 = context.getSharedPreferences("token", Activity.MODE_PRIVATE);
                        String token = sp1.getString("token", null);
                        new TiXianDialog(context,"你确定提现到" + InvitedToRecordActivity.bankType + "上面吗？",userInfo.getResult().getUid(),token,list.get(i).getInviteid(),list.get(i).getInvitestate()).call();
                    }
                }
            }
        });

        viewHolder.money.setText("￥"+list.get(i).getInvitered());
        return view;
    }

    class ViewHolder{
        public TextView friend_account,friend_status,money;
        public Button Withdrawals;     //提现
    }

    public String subString(String ss){
        int length = ss.length();
        return ss.substring(0,3) + "****" + ss.substring(length-4,length);
    }

}
