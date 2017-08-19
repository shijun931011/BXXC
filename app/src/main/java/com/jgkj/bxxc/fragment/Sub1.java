package com.jgkj.bxxc.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
    private LinearLayout linearLayout1,linearLayout2,linearLayout3,linearLayout4;
    private ImageView orderTest, suijiTest,moniTest,cuotiTest;
    private int index;
    private LinearLayout visual, traffic, gestures;
    private TextView visual1, traffic1, gestures1;
    private ImageView imageView;
    private MyAdapter adapter;
    private ViewPager viewpager;
    private List<SubPicture.Result> list;
    private List<String> list1;
    private Fragment mCurrentFragment,coach;
    //学习界面的轮播图，和做题的四个图片：
//    private String Sub1Url = "http://www.baixinxueche.com/index.php/Home/Apitoken/bannerbaokao";
    private String Sub1Url = "http://www.baixinxueche.com/index.php/Home/Apitoken/bannerbaokaoupdate";
    private ProgressDialog dialog;

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
        dialog = ProgressDialog.show(getActivity(), null, "数据加载中...");
        linearLayout1 = (LinearLayout) view.findViewById(R.id.linearlayout1);
        linearLayout1.setOnClickListener(this);
        linearLayout2 = (LinearLayout) view.findViewById(R.id.linearlayout2);
        linearLayout2.setOnClickListener(this);
        linearLayout3 = (LinearLayout) view.findViewById(R.id.linearlayout3);
        linearLayout3.setOnClickListener(this);
        linearLayout4 = (LinearLayout) view.findViewById(R.id.linearlayout4);
        linearLayout4.setOnClickListener(this);
        orderTest = (ImageView) view.findViewById(R.id.ordertest);
        suijiTest = (ImageView) view.findViewById(R.id.suijiTest);
        moniTest = (ImageView) view.findViewById(R.id.monitest);
        cuotiTest = (ImageView) view.findViewById(R.id.cuotiTest);

        Drawable textimg3 = getResources().getDrawable(R.drawable.textimg2);
        textimg3.setBounds(0, 0, 50, 50);
        Drawable textimg2 = getResources().getDrawable(R.drawable.textimg3);
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
        viewpager.setOnTouchListener(mOnTouchListener);

        visual1 = (TextView) view.findViewById(R.id.visual1);
        traffic1 = (TextView) view.findViewById(R.id.traffic1);
        gestures1 = (TextView) view.findViewById(R.id.gestures1);
        //设置左部图标
        visual1.setCompoundDrawables(textimg3, null, null, null);
        traffic1.setCompoundDrawables(textimg2, null, null, null);
        gestures1.setCompoundDrawables(textimg5, null, null, null);
        index = 1;

    }
    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        private int startX;
        private int startY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action){
                case MotionEvent.ACTION_DOWN:
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    int endX = (int) event.getX();
                    int endY = (int) event.getY();
                    if (Math.abs(endX - startX) < 50 && Math.abs(endY - startY) < 50){
                        Intent intent = new Intent();
                        try{
                            int itemPosition = viewpager.getCurrentItem();
                            Log.d("BXXXC", "科目"+list.get(itemPosition).getKey());
                            if (list.get(itemPosition).getKey()==2){
                                //发送广播
                                intent.setAction("tiaozhuang");
                                getActivity().sendBroadcast(intent);
                            }
                        }catch(Exception e){
                           e.printStackTrace();
                        }
                    }
                    break;
            }
            return false;
        }
    };

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
                        dialog.dismiss();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("百信学车","照片"+s);
                        dialog.dismiss();
                        Gson gson = new Gson();
                        SubPicture pic = gson.fromJson(s, SubPicture.class);
                        if (pic.getCode() == 200) {
                            list = pic.getResult();
                            list1 = pic.getMorepic();
                            if ( list != null) {
                                // 实例化listView
                                List<View> listView = new ArrayList<View>();
                                for (int k = 0; k <  list.size(); k++) {
                                    imageView = new ImageView(getActivity());
                                    Glide.with(getActivity()).load( list.get(k).getPic().toString()).into(imageView);
                                    imageView.setTag(list.get(k));
                                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                    listView.add(imageView);
                                }
                                adapter = new MyAdapter(getActivity(), listView);
                                viewpager.setAdapter(adapter);
                            }
                            Glide.with(getActivity()).load(list1.get(0)).into(orderTest);
                            Glide.with(getActivity()).load(list1.get(1)).into(suijiTest);
                            Glide.with(getActivity()).load(list1.get(2)).into(moniTest);
                            Glide.with(getActivity()).load(list1.get(3)).into(cuotiTest);

                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.linearlayout1:
                intent.setClass(getActivity(), SubTestActivity.class);
                startActivity(intent);
                break;
            case R.id.linearlayout4:
                intent.setClass(getActivity(), SubErrorTestActivity.class);
                startActivity(intent);
                break;
            case R.id.linearlayout2:
                intent.setClass(getActivity(), SubRandTestActivity.class);
                startActivity(intent);
                break;
            case R.id.linearlayout3:
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
        }

    }
}
