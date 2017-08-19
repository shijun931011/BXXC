package com.jgkj.bxxc.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.Rehour;

import java.util.List;


/**
 * Created by Administrator on 2017/5/11.
 *
 */

public class RehourAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private Rehour.Result result;
    private List<Rehour.Result> list;
    private ListView listView;
    private int uid;
    private String token;

    private Dialog  extraDialog;
    private View extraView;
    private TextView dialog_textView, dialog_sure, dialog_cancel,dialog_prompt;
    private String RehourUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/Hours";

    public RehourAdapter(Context context, List<Rehour.Result> list){
        this.context = context;
        this.list = list;
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
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null){
             viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.rehour_item, viewGroup, false);
            viewHolder.taocan = (TextView)view.findViewById(R.id.taocan_txt);
            viewHolder.bug = (TextView) view.findViewById(R.id.bug);

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        result = list.get(i);
        viewHolder.taocan.setText(result.getPackname());
        if (result.getSurplus_class().equals("0")){
            viewHolder.bug.setText("暂未购买");
        }else{
            viewHolder.bug.setText("剩余"+result.getSurplus_class()+"个学时");
        }
        return view;
    }
    class ViewHolder{
        private TextView taocan, bug;
        private TextView immediate_bug;     //学时不够？立即购买
        private TextView extra_hours;//多余课时怎么办？
    }
}
