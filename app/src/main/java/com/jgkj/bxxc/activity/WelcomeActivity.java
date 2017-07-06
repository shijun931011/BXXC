package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.Advertising;
import com.jgkj.bxxc.bean.SubjectVersionEntity;
import com.jgkj.bxxc.bean.entity.Sub4ProjectEntity.Sub4ProjectResult;
import com.jgkj.bxxc.bean.entity.SubProjectEntity.SubProjectEntity;
import com.jgkj.bxxc.bean.entity.SubProjectEntity.SubProjectResult;
import com.jgkj.bxxc.db.DBManager;
import com.jgkj.bxxc.tools.PictureOptimization;
import com.jgkj.bxxc.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import cn.jpush.android.api.InstrumentedActivity;
import okhttp3.Call;


/**
 * Created by fangzhou on 2016/10/5.
 * <p>
 * 启动页，所有接口都没有进行加密
 */
public class WelcomeActivity extends InstrumentedActivity implements View.OnClickListener {
    private ImageView welcome_imageview;
    private PictureOptimization po;
    private TextView skip;
    private Advertising ad;
    private Runnable delayRunable;
    private Handler handler = new Handler();
    //广告页，即广告图片的url获取
    private String adUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/nowStar";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        init();

    }

    //初始化控件，并请求广告页地址
    private void init() {
        skip = (TextView) findViewById(R.id.skip);
        welcome_imageview = (ImageView) findViewById(R.id.welcome_imageview);
        skip.setVisibility(View.GONE);
        skip.setOnClickListener(this);
        welcome_imageview.setOnClickListener(this);
        //防止图片内存溢出
        po = new PictureOptimization();
        getAD();
        getVersion();
    }

    private void isOpenAD() {
        String str = skip.getTag().toString();
        Gson gson = new Gson();
        ad = gson.fromJson(str, Advertising.class);
        if (ad.getCode() == 200) {
            //延迟跳转
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Glide.with(WelcomeActivity.this).load(ad.getResult().getContent()).into(welcome_imageview);
                    handler.postDelayed(
                            delayRunable = new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                                    intent.putExtra("FromActivity", "WelcomeActivity");
                                    startActivity(intent);
                                    finish();
                                }
                            }, Long.parseLong(ad.getResult().getDuration() + "000")); //延迟3秒跳转
                }
            }, 3000); //延迟3秒跳转
        } else {
            Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
            intent.putExtra("FromActivity", "WelcomeActivity");
            startActivity(intent);
            finish();
        }
    }

    private void getAD() {
        OkHttpUtils
                .post()
                .url(adUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        //此方法相当于一个定时器
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                                intent.putExtra("FromActivity", "WelcomeActivity");
                                startActivity(intent);
                                finish();
                            }
                        }, 3000); //延迟3秒跳转

                    }

                    @Override
                    public void onResponse(String s, int i) {
                        skip.setTag(s);
                        if (skip.getTag() != null) {
                            isOpenAD();
                        }
                    }
                });
    }

    /**
     * 获取科目一题目
     */
    private void getSubProject1(final int versionName) {
        OkHttpUtils
                .post()
                .url(Urls.sub)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(WelcomeActivity.this, "获取科目一题目失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        SubProjectResult subProjectResult = gson.fromJson(s,SubProjectResult.class);

                        if(subProjectResult.getCode() == 200){
                            //删除数据库
                            DBManager.getInstance().delSubProject();
                            //保存数据库
                            DBManager.getInstance().insertSubProject(subProjectResult.getResult());

                            SharedPreferences version = getSharedPreferences("SUB_VERSION1", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor_version = version.edit();
                            editor_version.clear();
                            editor_version.putInt("versionOne",versionName);
                            editor_version.commit();
                        }else{
                            Toast.makeText(WelcomeActivity.this, subProjectResult.getReason(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * 获取科目四题目
     */
    private void getSubProject4(final int versionName) {
        OkHttpUtils
                .post()
                .url(Urls.subfour)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(WelcomeActivity.this, "获取科目四题目失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        Sub4ProjectResult sub4ProjectResult = gson.fromJson(s,Sub4ProjectResult.class);

                        if(sub4ProjectResult.getCode() == 200){
                            //删除数据库
                            DBManager.getInstance().del4SubProject();
                            //保存数据库
                            DBManager.getInstance().insertSub4Project(sub4ProjectResult.getResult());

                            SharedPreferences version = getSharedPreferences("SUB_VERSION4", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor_version = version.edit();
                            editor_version.clear();
                            editor_version.putInt("versionFour",versionName);
                            editor_version.commit();
                        }else{
                            Toast.makeText(WelcomeActivity.this, sub4ProjectResult.getReason(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * 判断题库版本
     */
    private void getVersion() {
        OkHttpUtils
                .post()
                .url(Urls.subjectVersion)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(WelcomeActivity.this, "获取题库版本失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        SubjectVersionEntity subjectVersionEntity = gson.fromJson(s,SubjectVersionEntity.class);

                        if(subjectVersionEntity.getCode() == 200){
                            SharedPreferences sp = getSharedPreferences("SUB_VERSION1", Activity.MODE_PRIVATE);
                            int versionOne = sp.getInt("versionOne", 0);
                            SharedPreferences sp4 = getSharedPreferences("SUB_VERSION4", Activity.MODE_PRIVATE);
                            int versionFour = sp4.getInt("versionFour", 0);

                            if(versionOne == 0 || versionOne != subjectVersionEntity.getVersionone()){
                                Toast.makeText(WelcomeActivity.this, "1", Toast.LENGTH_SHORT).show();
                                getSubProject1(subjectVersionEntity.getVersionone());
                            }

                            if(versionFour == 0 || versionFour != subjectVersionEntity.getVersionfour()){
                                Toast.makeText(WelcomeActivity.this, "4", Toast.LENGTH_SHORT).show();
                                getSubProject4(subjectVersionEntity.getVersionfour());
                            }
                        }else{
                            Toast.makeText(WelcomeActivity.this, subjectVersionEntity.getReason(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.skip:
                //即使是finish()，已启用的线程也会走，不会跟着Activity销毁而销毁因此我们需要利用
                //handler.removerCallbacks(Runable runable),方法取消指定已启用的线程
                handler.removeCallbacks(delayRunable);
                intent.setClass(WelcomeActivity.this, HomeActivity.class);
                intent.putExtra("FromActivity", "WelcomeActivity");
                startActivity(intent);
                finish();
                break;
            case R.id.welcome_imageview:
                if (ad.getResult().getOpenUrl().isEmpty()) {
                } else {
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(ad.getResult().getOpenUrl());
                    intent.setData(content_url);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }







}

