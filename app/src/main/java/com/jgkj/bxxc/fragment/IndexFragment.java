package com.jgkj.bxxc.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.BXCenterActivity;
import com.jgkj.bxxc.activity.CarPickUpActivity;
import com.jgkj.bxxc.activity.ClassTypeActivity;
import com.jgkj.bxxc.activity.ClassicActivity;
import com.jgkj.bxxc.activity.GuideActivity;
import com.jgkj.bxxc.activity.HeadlinesActivity;
import com.jgkj.bxxc.activity.HomeActivity;
import com.jgkj.bxxc.activity.InviteFriendsActivity;
import com.jgkj.bxxc.activity.LoginActivity;
import com.jgkj.bxxc.activity.PlaceChooseActivity;
import com.jgkj.bxxc.activity.PrivateActivity;
import com.jgkj.bxxc.activity.QuesAnsActivity;
import com.jgkj.bxxc.activity.WebViewActivity;
import com.jgkj.bxxc.adapter.MyAdapter;
import com.jgkj.bxxc.bean.HeadlinesAction;
import com.jgkj.bxxc.bean.Picture;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.tools.AutoTextView;
import com.jgkj.bxxc.tools.SecondToDate;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

/**
 * 首页布局
 */
public class IndexFragment extends Fragment implements OnClickListener {
    // LinearLayout
    private LinearLayout linearlayout;
    private View view, view1;
    // viewpager
    private ViewPager viewpager;
    // 集合list
    private List<View> list;
    // 适配器
    private MyAdapter adapter;
    // 实例化4个主button
    private TextView first_btn, fourth_btn, coach_center_btn, carsend,
            space_choose_btn, question;
    // 实例化Fragment
    private Fragment mCurrentFragment, license_Text_Fragment1,
            license_Text_Fragment2, coach;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private TextView text_title;
    //毫秒数转化为天数
    private SecondToDate std;
    private TextView lookMore;
    private ImageView imageView, KefuPhone;
    private RelativeLayout select_coach, select_place,select_class;//教练、 场地、 班型
    private LinearLayout yQfirend,classic_coach,private_coach;
    private AutoTextView headlines;
    //图片地址
    private String url = "http://www.baixinxueche.com/index.php/Home/Apitoken/bannerpics ";
    private List<String> imagePath = new ArrayList<>();
    private LinearLayout.LayoutParams wrapParams;
    private Timer timer = new Timer();
    private int currentItem = 0;
    private Runnable runnable;
    private Handler handler = new Handler();
    private String headlinesUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/nowLinesTitleAndroid";
    private List<HeadlinesAction.Result> headlinesList;
    private int headlinesCount = 0;
    private ImageView bxhead;
    private HeadlinesAction action;
    private TextView quesAns,bxCenter,customerPhone;
    private SharedPreferences sp;
    private UserInfo userInfo;
    private UserInfo.Result result;
    private TextView signup_guide;
    private TextView car_pickup;
    private Boolean isLogined = false;
    private String token;
    private int uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.index_fragment2, container, false);
        view.scrollBy(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        init();
        getImage();
        scrollView();
        getheadlines();
        headlinesList = new ArrayList<HeadlinesAction.Result>();
        return view;
    }

    //初始化布局
    private void init() {
        Drawable carpick = getResources().getDrawable(R.drawable.chejiechesong_image);
        carpick.setBounds(0, 0, 100, 100);
        Drawable bxcenter = getResources().getDrawable(R.drawable.baixincenter_image);
        bxcenter.setBounds(0, 0, 100, 100);
        Drawable question = getResources().getDrawable(R.drawable.dayijiehuo_image);
        question.setBounds(0, 0, 100, 100);
        Drawable signup = getResources().getDrawable(R.drawable.baomingzhinan_image);
        signup.setBounds(0, 0, 100, 100);
        //百信头条
        headlines = (AutoTextView) view.findViewById(R.id.headlines);
        headlines.setTag("nourl");
        bxhead = (ImageView) view.findViewById(R.id.bxhead);
        signup_guide = (TextView) view.findViewById(R.id.signup_guide);
        car_pickup = (TextView) view.findViewById(R.id.car_pickup);
        select_coach = (RelativeLayout) view.findViewById(R.id.select_coach);
        select_place = (RelativeLayout) view.findViewById(R.id.select_place);
        select_class = (RelativeLayout) view.findViewById(R.id.select_class);
        headlines.setText("科技改变生活，百信引领学车!");
        // 实例化控件
        linearlayout = (LinearLayout) view.findViewById(R.id.linearlayout);
        coach = new CoachFragment();
        fragmentManager = getFragmentManager();
        viewpager = (ViewPager) view.findViewById(R.id.viewPage);
        viewpager.setOnTouchListener(mOnTouchListener);
        quesAns = (TextView) view.findViewById(R.id.quesAns);
        bxCenter = (TextView) view.findViewById(R.id.bxCenter);
        yQfirend = (LinearLayout) view.findViewById(R.id.yQfirend);
        classic_coach = (LinearLayout) view.findViewById(R.id.classic_coach);
        private_coach = (LinearLayout) view.findViewById(R.id.private_coach);
        headlines.setOnClickListener(this);
        bxhead.setOnClickListener(this);
        select_place.setOnClickListener(this);
        select_coach.setOnClickListener(this);
        select_class.setOnClickListener(this);
        yQfirend.setOnClickListener(this);
        car_pickup.setOnClickListener(this);
        bxCenter.setOnClickListener(this);
        quesAns.setOnClickListener(this);
        signup_guide.setOnClickListener(this);
        classic_coach.setOnClickListener(this);
        private_coach.setOnClickListener(this);
        car_pickup.setCompoundDrawables(null, carpick, null, null);
        bxCenter.setCompoundDrawables(null, bxcenter, null, null);
        quesAns.setCompoundDrawables(null, question, null, null);
        signup_guide.setCompoundDrawables(null, signup, null,null);
        // 验证是否登录
        sp = getActivity().getApplication().getSharedPreferences("USER",
                Activity.MODE_PRIVATE);
        int isFirstRun = sp.getInt("isfirst", 0);
        if (isFirstRun != 0) {
            String str = sp.getString("userInfo", null);
            Log.d("11111", "init: " + str);
            Gson gson = new Gson();
            userInfo = gson.fromJson(str, UserInfo.class);
            result = userInfo.getResult();
        }
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
                            if (userInfo == null){
                                intent.setClass(getActivity(), LoginActivity.class);
                                startActivity(intent);
                            }else{
                                intent.setClass(getActivity(),InviteFriendsActivity.class);
                                startActivity(intent);
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
     * 百信头条轮播文字
     */
    private void getheadlines() {
        OkHttpUtils
                .post()
                .url(headlinesUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(getActivity(), "网络异常，请检查网络！", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        headlines.setTag(s);
                        if (headlines.getTag() != null) {
                            setHeadlines();
                        }
                    }
                });
    }

    private void setHeadlines() {
        String headlinesTag = headlines.getTag().toString();
        Gson gson = new Gson();
        action = gson.fromJson(headlinesTag, HeadlinesAction.class);
        if (action.getCode() == 200) {
            headlinesList.addAll(action.getResult());
            runnable = new Runnable() {
                public void run() {
                    headlines.next();
                    headlines.setText(headlinesList.get(headlinesCount).getTitle());
                    headlines.setTag(headlinesList.get(headlinesCount).getUrl());
                    if (headlinesCount < (headlinesList.size()-1)) {
                        headlinesCount++;
                    } else {
                        headlinesCount = 0;
                    }
                    handler.postDelayed(this, 2000);
                }
            };
            handler.postDelayed(runnable, 2000);
        }
    }

    /**
     * 图片请求，几张图片创建相对应的viewPager+ImageView
     * 来显示图片
     */
    private void getImage() {
        OkHttpUtils
                .post()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(getActivity(), "网络状态不佳,请稍后再试！", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        Picture pic = gson.fromJson(s, Picture.class);
                        if (pic.getCode() == 200) {
                            final List<String> list = pic.getResult();
                            if (list != null) {
                                // 实例化listView
                                List<View> listView = new ArrayList<View>();
                                for (int k = 0; k < list.size(); k++) {
                                    imageView = new ImageView(getActivity());
                                    Glide.with(getActivity()).load(list.get(k)).into(imageView);
                                    imageView.setTag(list.get(k));
                                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                    listView.add(imageView);
                                }
                                adapter = new MyAdapter(getActivity(), listView);
                                SharedPreferences sp = getActivity().getSharedPreferences("PicCount", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt("Count", list.size());
                                editor.commit();
                                viewpager.setAdapter(adapter);
                            }
                        }
                    }
                });
    }

    //自动轮播
    private void scrollView() {
        SharedPreferences sp = getActivity().getSharedPreferences("PicCount", Activity.MODE_PRIVATE);
        final int count = sp.getInt("Count", -1);
        if (count != -1) {
            final ImageView[] dots = new ImageView[count];
            for (int k = 0; k < count; k++) {
                ImageView image = new ImageView(getActivity());
                image.setImageDrawable(getResources().getDrawable(R.drawable.selector));
                image.setId(k);
                wrapParams = new LinearLayout.LayoutParams(ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT);
                wrapParams.leftMargin = 5;
                image.setLayoutParams(wrapParams);
                linearlayout.addView(image);
                dots[k] = (ImageView) linearlayout.getChildAt(k);
                dots[k].setEnabled(true);
            }
            final Handler mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (currentItem < (count - 1)) {
                        currentItem++;
                        viewpager.setCurrentItem(currentItem);
                    } else if (currentItem == (count - 1)) {
                        currentItem = 0;
                        viewpager.setCurrentItem(currentItem);
                    }
                    for (int j = 0; j < count; j++) {
                        dots[j].setEnabled(false);
                    }
                    dots[currentItem].setEnabled(true);
                }
            };
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0);
                }
            };
            timer.schedule(timerTask, 1000, 3000);
        }
    }
    // fragment切换
    public void switchFragment(Fragment from, Fragment to) {
        transaction.setCustomAnimations(R.anim.switch_fragment_anim_in,
                R.anim.switch_fragment_anim_out);
        if (!to.isAdded()) {
            transaction.hide(from).add(R.id.license_text_learnning, to)
                    .addToBackStack(null).commit();
        } else {
            transaction.hide(from).show(to).commit();
        }
    }
    @Override
    public void onClick(View v) {
        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        license_Text_Fragment1 = new License_Text_Fragment();
        license_Text_Fragment2 = new License_Text_Fragment();
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.signup_guide:     //报名指南
                intent.setClass(getActivity(), GuideActivity.class);
                startActivity(intent);
                break;
            case R.id.car_pickup:            //车接车送
                intent.setClass(getActivity(), CarPickUpActivity.class);
                startActivity(intent);
                break;
            case R.id.select_coach:           //教练
                intent.setClass(getActivity(), HomeActivity.class);
                intent.putExtra("FromActivity", "IndexFragment");
                startActivity(intent);
                getActivity().finish();
                mCurrentFragment = coach;
                break;

            case R.id.select_place:          //场地
                intent.setClass(getActivity(), PlaceChooseActivity.class);
                startActivity(intent);
                break;
            case R.id.select_class:    //班型
                intent.setClass(getActivity(), ClassTypeActivity.class);
                startActivity(intent);
                break;
            case R.id.headlines:
                if (headlines.getTag().toString().equals("nourl")) {
                    Toast.makeText(getActivity(), "加载中,请稍后再试!", Toast.LENGTH_SHORT).show();
                } else {
                    intent.setClass(getActivity(),WebViewActivity.class);
                    intent.putExtra("url",headlines.getTag().toString());
                    intent.putExtra("title","百信头条");
                    startActivity(intent);
                }
                break;
            case R.id.bxhead:
                Intent bxheadIntent = new Intent();
                bxheadIntent.setClass(getActivity(), HeadlinesActivity.class);
                startActivity(bxheadIntent);
                break;
            case R.id.quesAns:        //答疑解惑
                intent.setClass(getActivity(),QuesAnsActivity.class);
                startActivity(intent);
                break;
            case R.id.bxCenter:        //百信中心
                intent.setClass(getActivity(),BXCenterActivity.class);
                startActivity(intent);
                break;
//            case R.id.search:
//                new CallDialog(getActivity(),"055165555744").call();
//                break;
            case R.id.yQfirend:            //邀请好友
                if (userInfo == null){
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }else{
                    intent.setClass(getActivity(),InviteFriendsActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.classic_coach:       //经典班报名
                intent.setClass(getActivity(), ClassicActivity.class);
                startActivity(intent);
                break;
            case R.id.private_coach:
                intent.setClass(getActivity(), PrivateActivity.class);
                startActivity(intent);
                break;
        }
    }
}
