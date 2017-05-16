package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.Invite;

import java.util.List;

/**
 * Created by shijunon 2017/4/24.
 * 邀请学车记录
 */

public class InvitedToRecordAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private Invite.Result result;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
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
        result = list.get(i);
        Log.d("zyzhang",":::"+result.getInviter());
        viewHolder.friend_account.setText(result.getInviter());
        viewHolder.friend_status.setText(result.getInvitetime());
        viewHolder.money.setText("￥"+result.getInvitered());
        return view;
    }

    class ViewHolder{
        public TextView friend_account,friend_status,money;
        public Button Withdrawals;     //提现
    }

}
