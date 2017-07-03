package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.tools.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 班型
 */
public class ClassTypeActivity extends Activity implements View.OnClickListener{
    private TextView title;
    private Button button_backward;
    private Button class_description;      //班型说明
    private LinearLayout pri_class;       //私教班
    private LinearLayout ext_class;       //至尊班
    private LinearLayout vip_class,linear_driving_companion;       //vip班,陪驾
    private Dialog dialog;
    private ViewPager viewPager;
    private ArrayList<View> pageview;
    MyPagerAdapter myPagerAdapter;
    ViewPagerIndicator indicator1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_type);
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
        linear_driving_companion=(LinearLayout) findViewById(R.id.linear_driving_companion);
        class_description = (Button) findViewById(R.id.button_forward);
        class_description.setVisibility(View.VISIBLE);
        class_description.setText("班型说明");
        class_description.setOnClickListener(this);
        ext_class.setOnClickListener(this);
        vip_class.setOnClickListener(this);
        pri_class.setOnClickListener(this);
        linear_driving_companion.setOnClickListener(this);
    }

    private void classtypeDialog(){
        Context context = ClassTypeActivity.this;
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(R.layout.classtype_dialog);
        viewPager = (ViewPager) dialog.findViewById(R.id.viewPager);
        myPagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(myPagerAdapter);
        indicator1 = (ViewPagerIndicator) dialog.findViewById(R.id.indicator1);
        indicator1.setLength(myPagerAdapter.list.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indicator1.setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        // 获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        // 设置dialog宽度
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.CENTER);
    }
    public class MyPagerAdapter extends android.support.v4.view.PagerAdapter{
        List<Drawable> list = new ArrayList<>();

        public MyPagerAdapter(){
            list.add(getDrawable(R.drawable.private_description));
            list.add(getDrawable(R.drawable.classic_des));
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = position%list.size();
            View view = LayoutInflater.from(ClassTypeActivity.this).inflate(R.layout.item01, null);
            ImageView img= (ImageView) view.findViewById(R.id.item);
            img.setImageDrawable(list.get(position));
            container.addView(view);
            return view;
        }
    }

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
            case R.id.linear_driving_companion:           //陪驾
                intent.setClass(this,DrivingCompanionActivity.class);
                startActivity(intent);
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
