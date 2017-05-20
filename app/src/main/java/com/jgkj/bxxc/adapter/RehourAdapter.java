package com.jgkj.bxxc.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
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
import com.jgkj.bxxc.activity.RehourActivity;
import com.jgkj.bxxc.bean.Rehour;
import com.jgkj.bxxc.bean.entity.MenuEntity.MenuEntitys;
import com.jgkj.bxxc.bean.entity.MenuEntity.MenuResults;
import com.jgkj.bxxc.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;


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
