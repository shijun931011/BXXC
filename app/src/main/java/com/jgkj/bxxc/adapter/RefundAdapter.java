package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.Refund;

import java.util.List;

/**
 * Created by shijun on 2017/5/12.
 */

public class RefundAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private Refund.Result result;
    private List<Refund.Result> list;


    public RefundAdapter(Context context,List<Refund.Result> list) {
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
            view = inflater.inflate(R.layout.item_refund, viewGroup, false);
            viewHolder.refund_name = (TextView) view.findViewById(R.id.refund_name);
            viewHolder.refund_time = (TextView) view.findViewById(R.id.refund_time);
            viewHolder.refund_money = (TextView) view.findViewById(R.id.refund_money);
            viewHolder.refund_state = (TextView) view.findViewById(R.id.refund_state);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        result = list.get(i);
        viewHolder.refund_name.setText(result.getRefundName());
        viewHolder.refund_time.setText(result.getRefundTime());
        viewHolder.refund_money.setText(result.getRefundMoney());
        viewHolder.refund_state.setText(result.getRefundState());
        return view;
    }

    class ViewHolder{
        public TextView refund_name, refund_time, refund_money, refund_state;
    }
}
