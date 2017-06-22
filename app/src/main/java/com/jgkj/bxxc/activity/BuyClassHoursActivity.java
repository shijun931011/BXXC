package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.PackageAdapter;
import com.jgkj.bxxc.bean.entity.PackageEntity.PackageEntity;
import com.jgkj.bxxc.bean.entity.PackageEntity.PackageResult;
import com.jgkj.bxxc.tools.RemainBaseDialog;
import com.jgkj.bxxc.tools.Urls;
import com.lmj.mypwdinputlibrary.MyInputPwdUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * Created by tongshoujun on 2017/5/13.
 */

public class BuyClassHoursActivity extends Activity implements AdapterView.OnItemClickListener{

    private ListView listView;
    private MyInputPwdUtil myInputPwdUtil;
    //标题
    private TextView title;
    private Button button_backward;
    private Button remind;
    private int uid;
    private String cid;
    private String token;
    private List<PackageEntity> list;
    //套餐类型
    private String packageId;
    private String tiaozhuan;
    private String yuyue,jingpin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_class_hours);
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid",uid);
        cid = intent.getStringExtra("cid");
        token = intent.getStringExtra("token");
        tiaozhuan=intent.getStringExtra("tiaozhuan");
        yuyue=intent.getStringExtra("yuyue");
        jingpin=intent.getStringExtra("jingpin");
        //标题
        title = (TextView) findViewById(R.id.text_title);
        title.setText("购买学时套餐");
        button_backward = (Button) findViewById(R.id.button_backward);
        remind = (Button) findViewById(R.id.button_forward);
        button_backward.setVisibility(View.VISIBLE);
        button_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        remind.setVisibility(View.VISIBLE);
        remind.setText("购买须知");
        remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RemainBaseDialog(BuyClassHoursActivity.this,"百信学车为您提供学时套餐，优惠，方便。" +
                        "您可以使用该套餐来预约私教班教练以及陪练教练的学习时间，且以3个学时为起步" +
                        "，套餐购买后不给予退款，请您按照实际需求购买套餐").call();
            }
        });

        listView = (ListView)findViewById(R.id.listView);
        getData(Urls.packages);
        listView.setOnItemClickListener(this);
    }

    public void show(View view){
        myInputPwdUtil.show();
    }

    private void getData(String url) {
        Log.i("百信学车","套餐信息参数" + url);
        OkHttpUtils
                .post()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(BuyClassHoursActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        PackageResult packageResult = gson.fromJson(s, PackageResult.class);
                        list = packageResult.getResult();
                        Log.i("百信学车","套餐信息结果" + s);
                        if (packageResult.getCode() == 200) {
                            PackageAdapter adapter = new PackageAdapter(BuyClassHoursActivity.this,list);
                            listView.setAdapter(adapter);
                        } else {
                            Toast.makeText(BuyClassHoursActivity.this, "没有更多的！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setClass(this, BuyClassPackagesActivity.class);
        intent.putExtra("tv_pakage",list.get(position).getPackagename());
        intent.putExtra("tv_buy", list.get(position).getCountmoney());
        intent.putExtra("class_hour",list.get(position).getClasshour());
        intent.putExtra("class_song",list.get(position).getSong());
        intent.putExtra("class_money",list.get(position).getClassmoney());
        intent.putExtra("im_pic",list.get(position).getPic());
        intent.putExtra("uid",uid);
        intent.putExtra("cid",cid);
        intent.putExtra("token",token);
        intent.putExtra("tiaozhuan","1111");
        intent.putExtra("yuyue","2222");
        intent.putExtra("jingpin","3333");
        intent.putExtra("packageId",list.get(position).getPackageid());
        intent.putExtra("ti_shi",list.get(position).getTishi());
        startActivity(intent);
        finish();
    }
}
