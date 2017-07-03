package com.jgkj.bxxc.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.BXCenterActivity;
import com.jgkj.bxxc.activity.CarPickUpActivity;
import com.jgkj.bxxc.activity.ClassTypeActivity;
import com.jgkj.bxxc.activity.ClassicActivity;
import com.jgkj.bxxc.activity.DrivingCompanionActivity;
import com.jgkj.bxxc.activity.HeadlinesActivity;
import com.jgkj.bxxc.activity.InviteFriendsActivity;
import com.jgkj.bxxc.activity.LoginActivity;
import com.jgkj.bxxc.activity.PlaceChooseActivity;
import com.jgkj.bxxc.activity.PrivateActivity;
import com.jgkj.bxxc.activity.QuesAnsActivity;
import com.jgkj.bxxc.activity.UseGuideActivity;
import com.jgkj.bxxc.activity.WebViewActivity;
import com.jgkj.bxxc.adapter.MyAdapter;
import com.jgkj.bxxc.bean.HeadlinesAction;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.bean.entity.BannerEntity.BannerEntity;
import com.jgkj.bxxc.bean.entity.BannerEntity.BannerResult;
import com.jgkj.bxxc.tools.AutoTextView;
import com.jgkj.bxxc.tools.BannerPage;
import com.jgkj.bxxc.tools.CallDialog;
import com.jgkj.bxxc.tools.NetworkImageHolderView;
import com.jgkj.bxxc.tools.RemainBaseDialog;
import com.jgkj.bxxc.tools.SecondToDate;
import com.jgkj.bxxc.tools.Urls;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import okhttp3.Call;

/**
 * 首页布局
 */
public class IndexFragment extends Fragment implements OnClickListener {
    private View view, view1;
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
    //毫秒数转化为天数
    private SecondToDate std;
    private TextView lookMore;
    private ImageView imageView, KefuPhone;
    private RelativeLayout select_coach, select_place,select_class;//教练、 场地、 班型
    private LinearLayout yQfirend,classic_coach,private_coach,linear_driving_companion;
    private AutoTextView headlines;

    private List<String> imagePath = new ArrayList<>();
    private LinearLayout.LayoutParams wrapParams;
    private Timer timer = new Timer();
    private int currentItem = 0;
    private Runnable runnable;
    private Handler handler = new Handler();
    private String headlinesUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/nowLinesTitleAndroid";
    private List<HeadlinesAction.Result> headlinesList = new ArrayList<HeadlinesAction.Result>();;
    private int headlinesCount = 0;
    private ImageView bxhead;
    private HeadlinesAction action;
    private TextView quesAns,bxCenter,customerPhone;
    private SharedPreferences sp;
    private UserInfo userInfo;
    private UserInfo.Result result;
    private TextView use_guide;
    private TextView car_pickup;
    private Boolean isLogined = false;
    private String token;
    private int uid;
    private List<BannerEntity> bannerEntitylist;

    private ConvenientBanner cb_convenientBanner;
    private List<BannerPage> page = new ArrayList<>();  //数据集合

    private TextView text_title, place;
    private ImageView kefu,im_title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container.getTag()==null){
            view = inflater.inflate(R.layout.index_fragment2, container, false);
            init();
            getImage();
            getheadlines();
            container.setTag(view);
        }else{
            view = (View) container.getTag();
        }
        return view;
    }

    //初始化布局
    private void init() {

        text_title = (TextView) view.findViewById(R.id.text_title);
        //地区
        place = (TextView) view.findViewById(R.id.txt_place);
        //客服电话
        kefu = (ImageView) view.findViewById(R.id.remind);
        im_title = (ImageView) view.findViewById(R.id.im_title);
        place.setOnClickListener(this);
        kefu.setOnClickListener(this);

        text_title.setVisibility(View.GONE);
        im_title.setVisibility(View.VISIBLE);
        place.setVisibility(View.VISIBLE);
        kefu.setVisibility(View.VISIBLE);
        place.setText("合肥");
        kefu.setImageResource(R.drawable.kefu_phone);


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
        use_guide = (TextView) view.findViewById(R.id.use_guide);
        car_pickup = (TextView) view.findViewById(R.id.car_pickup);
        select_coach = (RelativeLayout) view.findViewById(R.id.select_coach);
        select_place = (RelativeLayout) view.findViewById(R.id.select_place);
        select_class = (RelativeLayout) view.findViewById(R.id.select_class);
        headlines.setText("科技改变生活，百信引领学车!");
        // 实例化控件
        coach = new CoachFragment();
        fragmentManager = getFragmentManager();
        cb_convenientBanner = (ConvenientBanner)view.findViewById(R.id.cb_convenientBanner);
        quesAns = (TextView) view.findViewById(R.id.quesAns);
        bxCenter = (TextView) view.findViewById(R.id.bxCenter);
        yQfirend = (LinearLayout) view.findViewById(R.id.yQfirend);
        classic_coach = (LinearLayout) view.findViewById(R.id.classic_coach);
        private_coach = (LinearLayout) view.findViewById(R.id.private_coach);
        linear_driving_companion = (LinearLayout) view.findViewById(R.id.linear_driving_companion);
        headlines.setOnClickListener(this);
        bxhead.setOnClickListener(this);
        select_place.setOnClickListener(this);
        select_coach.setOnClickListener(this);
        select_class.setOnClickListener(this);
        yQfirend.setOnClickListener(this);
        car_pickup.setOnClickListener(this);
        bxCenter.setOnClickListener(this);
        quesAns.setOnClickListener(this);
        use_guide.setOnClickListener(this);
        classic_coach.setOnClickListener(this);
        private_coach.setOnClickListener(this);
        linear_driving_companion.setOnClickListener(this);
        car_pickup.setCompoundDrawables(null, carpick, null, null);
        bxCenter.setCompoundDrawables(null, bxcenter, null, null);
        quesAns.setCompoundDrawables(null, question, null, null);
        use_guide.setCompoundDrawables(null, signup, null,null);
        // 验证是否登录
        sp = getActivity().getApplication().getSharedPreferences("USER",
                Activity.MODE_PRIVATE);
        int isFirstRun = sp.getInt("isfirst", 0);
        if (isFirstRun != 0) {
            String str = sp.getString("userInfo", null);
            Gson gson = new Gson();
            userInfo = gson.fromJson(str, UserInfo.class);
            result = userInfo.getResult();
        }
    }

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
                .url(Urls.bannerpicsands)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(getActivity(), "网络状态不佳,请稍后再试！", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("BXXC","轮播图"+s);
                        Gson gson = new Gson();
                        BannerResult pic = gson.fromJson(s, BannerResult.class);
                        if (pic.getCode() == 200) {
                            bannerEntitylist = pic.getResult();
                            if (bannerEntitylist != null) {
                                for (int k = 0; k < bannerEntitylist.size(); k++) {
                                    page.add(new BannerPage(bannerEntitylist.get(k).getPic().toString()));
                                }
                                initImageLoader();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        license_Text_Fragment1 = new License_Text_Fragment();
        license_Text_Fragment2 = new License_Text_Fragment();
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.use_guide:     //报名指南
                intent.setClass(getActivity(), UseGuideActivity.class);
                startActivity(intent);
                break;
            case R.id.car_pickup:            //车接车送
                intent.setClass(getActivity(), CarPickUpActivity.class);
                startActivity(intent);
                break;
            case R.id.select_coach:           //教练
                //发送广播
                intent.setAction("tiaozhuang");
                getActivity().sendBroadcast(intent);
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
            case R.id.yQfirend:            //邀请好友
                if (userInfo == null){
                    intent.setClass(getActivity(), LoginActivity.class);
                    intent.putExtra("message","ivatationFriends");
                    startActivity(intent);
                }else{
                    intent.setClass(getActivity(),InviteFriendsActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.linear_driving_companion:            //陪驾
                intent.setClass(getActivity(),DrivingCompanionActivity.class);
                startActivity(intent);
                break;
            case R.id.classic_coach:       //经典班报名
                intent.setClass(getActivity(), ClassicActivity.class);
                startActivity(intent);
                break;
            case R.id.private_coach:
                if (userInfo == null){
                    intent.setClass(getActivity(), LoginActivity.class);
                    intent.putExtra("message","privateCoach");
                    startActivity(intent);
                }else{
                    intent.setClass(getActivity(), PrivateActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.remind:
                new CallDialog(getActivity(), "0551-65555744").call();
                break;
            case R.id.txt_place:
                new RemainBaseDialog(getActivity(), "目前仅支持合肥地区").call();
                break;
        }
    }

    /**
     * 获取焦点时刷新界面
     */
    @Override
    public void onResume() {
        super.onResume();
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

    //初始化网络图片缓存库
    private void initImageLoader(){
        //网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                showImageForEmptyUri(R.mipmap.ic_launcher)
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);

        cb_convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        },bannerEntitylist)
        .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
        //设置翻页的效果，不需要翻页效果可用不设，这里有十几种翻页效果
        .startTurning(2000);
    }
}
