package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.tools.StatusBarCompat;

import java.util.ArrayList;

/**
 * 班型
 */
public class ClassTypeActivity extends Activity implements View.OnClickListener{
    private TextView title;
    private Button button_backward;
    private Button class_description;      //班型说明
    private LinearLayout pri_class;       //私教班
    private LinearLayout ext_class;       //至尊班
    private LinearLayout vip_class;       //vip班
    private Dialog dialog;
    private ViewPager viewPager;
    private ArrayList<View> pageview;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_type);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        initview();
    }
    private void initview(){
        title = (TextView)  findViewById(R.id.text_title);
        title.setText("班型");
        button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setVisibility(View.VISIBLE);
        button_backward.setOnClickListener(this);
        pri_class = (LinearLayout) findViewById(R.id.pri_class);
        ext_class = (LinearLayout) findViewById(R.id.ext_class);
        vip_class = (LinearLayout) findViewById(R.id.vip_class);
        class_description = (Button) findViewById(R.id.button_forward);
        class_description.setVisibility(View.VISIBLE);
        class_description.setText("班型说明");
        class_description.setOnClickListener(this);
        ext_class.setOnClickListener(this);
        vip_class.setOnClickListener(this);
        pri_class.setOnClickListener(this);
    }

    private void classtypeDialog(){
        Context context = ClassTypeActivity.this;
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(R.layout.classtype_dialog);
        viewPager = (ViewPager) dialog.findViewById(R.id.viewPager);
        LayoutInflater inflater =getLayoutInflater();
        View view1 = inflater.inflate(R.layout.item01, null);
        View view2 = inflater.inflate(R.layout.item02, null);
        //将view装入数组
        pageview =new ArrayList<View>();
        pageview.add(view1);
        pageview.add(view2);
        //绑定适配器
        viewPager.setAdapter(mPagerAdapter);
        dialog.show();
        // 获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        // 设置dialog宽度
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.CENTER);
    }
    PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        //获取当前窗体界面数
        public int getCount() {
            // TODO Auto-generated method stub
            return pageview.size();
        }

        @Override
        //断是否由对象生成界面
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0==arg1;
        }
        //是从ViewGroup中移出当前View
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(pageview.get(arg1));
        }

        //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
        public Object instantiateItem(View arg0, int arg1){
            ((ViewPager)arg0).addView(pageview.get(arg1));
            return pageview.get(arg1);
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch(v.getId()){
            case R.id.button_backward:
                finish();
                break;
            case R.id.button_forward:
                classtypeDialog();
                break;
            case R.id.pri_class:
                intent.setClass(this, PrivateClassActivity.class);
                startActivity(intent);
                break;
            case R.id.ext_class:
                intent.setClass(this,ExtClassActivity.class);
                startActivity(intent);
                break;
            case R.id.vip_class:
                intent.setClass(this,VipClassActivity.class);
                startActivity(intent);
                break;

        }

    }
}
