package com.jgkj.bxxc.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.SubErrorTestActivity;
import com.jgkj.bxxc.activity.SubExamTestActivity;
import com.jgkj.bxxc.activity.SubRandTestActivity;
import com.jgkj.bxxc.activity.SubTestActivity;
import com.jgkj.bxxc.activity.TrafficSignsActivity;
import com.jgkj.bxxc.activity.WebViewActivity;
import com.jgkj.bxxc.adapter.MyAdapter;
import com.jgkj.bxxc.bean.SubPicture;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

//科目一的一级界面
public class Sub1 extends Fragment implements View.OnClickListener {
    private View view;
    private Button orderTest, error_Sub, randomTest, examTest;
    private int index;
    private LinearLayout visual, traffic, gestures;
    private TextView visual1, traffic1, gestures1;
    private ImageView imageView;
//    private GifView baoming;
    private MyAdapter adapter;
    private ViewPager viewpager;

    //学习界面的轮播图，和做题的四个图片：
    private String Sub1Url = "http://www.baixinxueche.com/index.php/Home/Apitoken/bannerbaokao";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.subject1, container, false);
        init();
        getImage();
        return view;
    }

    /**
     * 初始化布局
     */
    private void init() {
        Drawable shunxu = getResources().getDrawable(R.drawable.license_text1);
        shunxu.setBounds(0, 0, 100, 100);
        Drawable cuoti = getResources().getDrawable(R.drawable.license_text2);
        cuoti.setBounds(0, 0, 100, 100);
        Drawable suiji = getResources().getDrawable(R.drawable.license_text4);
        suiji.setBounds(0, 0, 100, 100);
        Drawable moni = getResources().getDrawable(R.drawable.license_text3);
        moni.setBounds(0, 0, 100, 100);

        Drawable textimg3 = getResources().getDrawable(R.drawable.textimg3);
        textimg3.setBounds(0, 0, 50, 50);
        Drawable textimg2 = getResources().getDrawable(R.drawable.textimg2);
        textimg2.setBounds(0, 0, 50, 50);
        Drawable textimg5 = getResources().getDrawable(R.drawable.textimg5);
        textimg5.setBounds(0, 0, 50, 50);

        visual = (LinearLayout) view.findViewById(R.id.visual);
        traffic = (LinearLayout) view.findViewById(R.id.traffic);
        gestures = (LinearLayout) view.findViewById(R.id.gestures);
        gestures.setOnClickListener(this);
        traffic.setOnClickListener(this);
        visual.setOnClickListener(this);
        viewpager = (ViewPager) view.findViewById(R.id.viewPager);
//        baoming = (GifView) view.findViewById(R.id.baoming);
//        baoming.setGifImage(R.drawable.baoming);   //设置Gif图片源
//        baoming.setShowDimension(700, 80); //设置显示的大小，拉伸或者压缩
//        baoming.setGifImageType(GifView.GifImageType.COVER);  //设置加载方式：先加载后显示、边加载边显示、只显示第一帧再显示
//        baoming.setOnClickListener(this);

        visual1 = (TextView) view.findViewById(R.id.visual1);
        traffic1 = (TextView) view.findViewById(R.id.traffic1);
        gestures1 = (TextView) view.findViewById(R.id.gestures1);
        //设置左部图标
        visual1.setCompoundDrawables(textimg3, null, null, null);
        traffic1.setCompoundDrawables(textimg2, null, null, null);
        gestures1.setCompoundDrawables(textimg5, null, null, null);

        //顺序练题
        orderTest = (Button) view.findViewById(R.id.orderTest);
        orderTest.setOnClickListener(this);
        //错题
        error_Sub = (Button) view.findViewById(R.id.error_Sub);
        error_Sub.setOnClickListener(this);
        //随机练题
        randomTest = (Button) view.findViewById(R.id.randomTest);
        randomTest.setOnClickListener(this);
        //模拟考试
        examTest = (Button) view.findViewById(R.id.examTest);
        examTest.setOnClickListener(this);
        index = 1;

        //设置按钮顶部图标
        orderTest.setCompoundDrawables(null, shunxu, null, null);
        error_Sub.setCompoundDrawables(null, cuoti, null, null);
        examTest.setCompoundDrawables(null, suiji, null, null);
        randomTest.setCompoundDrawables(null, moni, null, null);
    }

    /**
     * 图片请求，几张图片创建相对应的viewPager+ImageView
     * 来显示图片
     */
    private void getImage() {
        OkHttpUtils
                .post()
                .url(Sub1Url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(getActivity(), "网络状态不佳,请稍后再试！", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("BXXC","图片请求"+s);
                        Gson gson = new Gson();
                        SubPicture pic = gson.fromJson(s, SubPicture.class);
                        if (pic.getCode() == 200) {
                            final List<SubPicture.Result> list = pic.getResult();
                            if (list != null) {
                                // 实例化listView
                                List<View> listView = new ArrayList<View>();
                                for (int k = 0; k < list.size(); k++) {
                                    imageView = new ImageView(getActivity());
                                    Glide.with(getActivity()).load(list.get(k).getPic()).into(imageView);
                                    imageView.setTag(list.get(k));
                                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                    listView.add(imageView);
                                }
                                adapter = new MyAdapter(getActivity(), listView);
                                viewpager.setAdapter(adapter);
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.orderTest:
                intent.setClass(getActivity(), SubTestActivity.class);
                startActivity(intent);
                break;
            case R.id.error_Sub:
                intent.setClass(getActivity(), SubErrorTestActivity.class);
                startActivity(intent);
                break;
            case R.id.randomTest:
                intent.setClass(getActivity(), SubRandTestActivity.class);
                startActivity(intent);
                break;
            case R.id.examTest:
                intent.setClass(getActivity(), SubExamTestActivity.class);
                startActivity(intent);
                break;
            case R.id.visual:
                intent.setClass(getActivity(), WebViewActivity.class);
                intent.putExtra("url", "http://www.baixinxueche.com/webshow/keyi/sjcs.html");
                intent.putExtra("title", "视觉测试");
                startActivity(intent);
                break;
            case R.id.traffic:
                intent.setClass(getActivity(), TrafficSignsActivity.class);
                startActivity(intent);
                break;
            case R.id.gestures:
                intent.setClass(getActivity(), WebViewActivity.class);
                intent.putExtra("url", "http://www.baixinxueche.com/webshow/keyi/jjss.html");
                intent.putExtra("title", "交警手势");
                startActivity(intent);
                break;
//            case R.id.baoming:
//                intent.setClass(getActivity(), HomeActivity.class);
//                intent.putExtra("FromActivity", "IndexFragment");
//                startActivity(intent);
//                getActivity().finish();
//                break;
        }

    }
}
