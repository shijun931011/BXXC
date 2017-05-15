package com.jgkj.bxxc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.PayDetail;

import java.util.List;


/**
 * Created by shijun on 2017/5/13.
 */

public class PaydetailAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private PayDetail.Result result;
    private List<PayDetail.Result> list;

    public PaydetailAdapter(Context context,List<PayDetail.Result> list) {
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
        if (view == null){
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.item_paydetail, viewGroup, false);
            viewHolder.paydate = (TextView) view.findViewById(R.id.date);
            viewHolder.payMoney = (TextView) view.findViewById(R.id.pay_money);
            viewHolder.paystate = (TextView) view.findViewById(R.id.paystate);
            viewHolder.paydel = (TextView) view.findViewById(R.id.paydel);
            viewHolder.payOrider = (TextView) view.findViewById(R.id.orider_num);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        result = list.get(i);
        viewHolder.paydate.setText(result.getApplyTime());
        viewHolder.payMoney.setText(result.getMoney());
        viewHolder.paystate.setText("支付方式：" + result.getPaystate());
        viewHolder.paydel.setText("商品详情：" + result.getPaydel());
        viewHolder.payOrider.setText("交易单号：" + result.getOrderNo());
        return view;
    }

    class ViewHolder{
        private TextView paydate, payMoney, paystate, paydel, payOrider;
    }
}
