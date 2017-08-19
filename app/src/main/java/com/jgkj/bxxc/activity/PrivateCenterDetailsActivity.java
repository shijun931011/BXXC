package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.CoachFullDetailAdapter;
import com.jgkj.bxxc.bean.PriCenterDetails;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.bean.entity.CommentEntity.CommentEntity;
import com.jgkj.bxxc.bean.entity.CommentEntity.CommentResult;
import com.jgkj.bxxc.tools.CallDialog;
import com.jgkj.bxxc.tools.RefreshLayout;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;

/**
 * 私教团队介绍
 */
public class PrivateCenterDetailsActivity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        RefreshLayout.OnLoadListener{
    private ListView listView;
    private RefreshLayout swipeLayout;
    private ImageView pri_center_img;
    private View headView;
    public static List<Activity> activityList = new LinkedList<Activity>();
    private String tid;
    private String token;
    private int uid;
    private String cid;
    private PriCenterDetails.Result result;
    private List<PriCenterDetails.Result.Member> list1;
    private  List<PriCenterDetails.Result.Comment> list2;
    private UserInfo userInfo;
    private UserInfo.Result result1;
    private TextView pri_center_name;//团队中心名称
    private String center_name;
    private Button back;
    private Button share;//分享
    private TextView text_title;//标题
    private TextView team_txt;  //团队简介
    private ImageView img_coach_head_small;  //团队小头像
    private ImageView img_coach_head;//团队大头像
    private TextView place;//位置
    private TextView price,totalStu,original_price,pri_type,coach_address;//现价、累计人数、原价、服务类型、地址
    private LinearLayout linear_list_noData;//评价列表
    private TextView signup_Coach;//立即预约
    private TextView connectCus;   //联系客服
    //综合、质量、服务、好评
    private LinearLayout zhonghe, zhiliang, fuwu;
    private TextView zhiliangfen, fuwufen,zhonghefen,haopinglv;
    private List<CommentEntity> listStu = new ArrayList<CommentEntity>();
    private CoachFullDetailAdapter adapter;
    private int commentPage = 0;
    private boolean falg = false;
    private ProgressDialog progressDialog;
    private LinearLayout.LayoutParams wrapParams;
    private String latitude;
    private String longitude;
    private String Cname;
    private String teamResult = null;
    //地图
    public MapView mMapView;
    private BaiduMap mBaiduMap;
    private BitmapDescriptor  bitmapA;
    private LinearLayout mGallery;   //团队成员
    private LayoutInflater mInflater;
    private HorizontalScrollView  horizontalScrollView;
    UMImage image;
    String url = "http://www.baixinxueche.com/index.php/Home/Info/indexz.html?from=groupmessage&tid=";
    //Marker地图标签
    private LatLng point;
    //私教中心详情页
    private  String priteamcenter = "http://www.baixinxueche.com/index.php/Home/Hotsearch/CoachinfoAgain";
    //私教中心评论接口
    private  String privateCommentUrl= "http://www.baixinxueche.com/index.php/Home/Hotsearch/androidPrivilege";
    private SharedPreferences sp1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_private_center_details);
        progressDialog = ProgressDialog.show(PrivateCenterDetailsActivity.this, "加载中...", "请等待...", true, false);
        headView = getLayoutInflater().inflate(R.layout.private_center_detail_item, null);
        mInflater = LayoutInflater.from(this);
        initView();
        getpricenterData(tid);
//        getCommentFirst(privateCommentUrl);
        bitmapA = BitmapDescriptorFactory.fromResource(R.drawable.a2);
    }

    private void initView(){
        pri_center_img = (ImageView) headView.findViewById(R.id.coach_head);
        signup_Coach = (TextView) findViewById(R.id.signup_Coach);
        signup_Coach.setOnClickListener(this);
        connectCus = (TextView) findViewById(R.id.connectCus);
        connectCus.setOnClickListener(this);
        back = (Button) findViewById(R.id.button_backward);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        share = (Button) findViewById(R.id.button_forward);
        share.setOnClickListener(this);
        share.setText("分享");
        share.setVisibility(View.VISIBLE);
        text_title = (TextView) findViewById(R.id.text_title);
        text_title.setText("团队介绍");
        pri_center_name = (TextView) headView.findViewById(R.id.pri_center_name);
        img_coach_head_small = (ImageView) headView.findViewById(R.id.coach_head_small);
        img_coach_head = (ImageView) headView.findViewById(R.id.coach_head);
        team_txt = (TextView) headView.findViewById(R.id.team_txt);
        price = (TextView) headView.findViewById(R.id.price);
        original_price = (TextView) headView.findViewById(R.id.original_price);
        original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        place = (TextView) headView.findViewById(R.id.place);
        totalStu = (TextView) headView.findViewById(R.id.totalStu);
        pri_type = (TextView) headView.findViewById(R.id.pri_type);
        zhonghe = (LinearLayout) headView.findViewById(R.id.zhonghe);
        zhiliang = (LinearLayout) headView.findViewById(R.id.zhiliang);
        fuwu = (LinearLayout) headView.findViewById(R.id.fuwu);
        zhonghefen = (TextView) headView.findViewById(R.id.zhonghefen);
        zhiliangfen = (TextView) headView.findViewById(R.id.zhiliangfen);
        fuwufen = (TextView) headView.findViewById(R.id.fuwufen);
        haopinglv = (TextView) headView.findViewById(R.id.haopinglv);
        coach_address = (TextView) headView.findViewById(R.id.coach_address);
        linear_list_noData = (LinearLayout)headView.findViewById(R.id.linear_list_noData);
        mGallery = (LinearLayout) headView.findViewById(R.id.id_gallery);
        listView = (ListView) findViewById(R.id.student_evaluate_listView);
        listView.setFocusable(false);
        listView.addHeaderView(headView, null, false);
        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);
        Intent intent = getIntent();
        tid = intent.getStringExtra("tid");
//        uid = intent.getIntExtra("uid",uid);
        token = intent.getStringExtra("token");
        center_name = intent.getStringExtra("name");
        SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
        String str = sp.getString("userInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str, UserInfo.class);
        pri_center_name.setText(center_name);
        share.setTag(tid);
        sp1 = getApplication().getSharedPreferences("token",
                Activity.MODE_PRIVATE);
        token = sp1.getString("token", null);
    }

    /**
     * 初始化地图
     */
    private void initMap(final String lantitude, final String longitude){
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.coach_map);
        mBaiduMap = mMapView.getMap();
        View v = mMapView.getChildAt(0);
        //设置是否显示比例尺控件
        mMapView.showScaleControl(false);
        //设置是否显示缩放控件
        mMapView.showZoomControls(false);
        //设置指定定位坐标
        point = new LatLng(Double.parseDouble(lantitude), Double.parseDouble(longitude));
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.yaogan);
        OverlayOptions options = new MarkerOptions().icon(icon).position(point);
        mBaiduMap.addOverlay(options);
        //设定中心点坐标
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(16)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        mBaiduMap.setOnMarkerClickListener(new markerClickListener());
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent();
                intent.setClass(PrivateCenterDetailsActivity.this,BDMAPActivity.class);
                intent.putExtra("lantitude",Double.parseDouble(latitude));
                intent.putExtra("longitude",Double.parseDouble(longitude));
                intent.putExtra("school", result.getSchool());
                intent.putExtra("address",result.getAddress());
                intent.putExtra("value","2");
                startActivity(intent);
            }
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }
    /**
     * marker点击事件处理
     */
    private class markerClickListener implements BaiduMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(final Marker marker) {
            LatLng latLng = marker.getPosition();
            return true;
        }
    }
    private void getpricenterData(String tid){
        Log.i("BXXC","教练tid=" +  tid );
        OkHttpUtils.post()
                .url(priteamcenter)
                .addParams("tid", tid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(PrivateCenterDetailsActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("BXXC","私教中心详情:" + s);
                        signup_Coach.setTag(s);
                        teamResult = s;
                        Gson gson = new Gson();
                        PriCenterDetails priCenterDetails = gson.fromJson(s,  PriCenterDetails.class);
                        if (priCenterDetails.getCode() == 200){
                            List<PriCenterDetails.Result> list = priCenterDetails.getResult();
                            list1 = priCenterDetails.getResult().get(0).getMember();
                            list2 = priCenterDetails.getResult().get(0).getComment();
                            result = list.get(0);
                            for (int k = 0; k<list1.size(); k++){
                                View view = mInflater.inflate(R.layout.pri_team_item, mGallery, false);
                                ImageView img = (ImageView) view.findViewById(R.id.img_team);
                                Glide.with(PrivateCenterDetailsActivity.this).load(list1.get(k).getCoafile()).into(img);
                                TextView txt = (TextView) view.findViewById(R.id.txt_team);
                                ImageView img_kemu = (ImageView) view.findViewById(R.id.kemu);
                                if ("科目二教练".equals(list1.get(k).getClass_type())){
                                    img_kemu.setImageResource(R.drawable.kemu);
                                }
                                if ("科目三教练".equals(list1.get(k).getClass_type())){
                                    img_kemu.setImageResource(R.drawable.kemu3);
                                }
                                if ("".equals(list1.get(k).getClass_type())){
                                    img_kemu.setImageResource(R.drawable.img_peilian_right);
                                }
                                Cname = list1.get(k).getCname();
                                txt.setText(Cname);
                                cid = list1.get(k).getPid();
                                mGallery.addView(view);
                            }
                            price.setText("￥" + result.getPrice());
                            original_price.setHint("￥" + result.getMarket_price());
                            original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                            String path = result.getFile();
                            if (!path.endsWith(".jpg") && !path.endsWith(".jpeg") && !path.endsWith(".png") &&
                                    !path.endsWith(".GIF") && !path.endsWith(".PNG") && !path.endsWith(".JPG") && !path.endsWith(".gif")) {
                                img_coach_head_small.setImageResource(R.drawable.coach_pic);
                            } else {
                                Glide.with(PrivateCenterDetailsActivity.this).load(result.getFile()).placeholder(R.drawable.coach_pic).error(R.drawable.coach_pic).into(img_coach_head_small);
                            }
                            String path2 = result.getFile();
                            if (!path2.endsWith(".jpg") && !path2.endsWith(".jpeg") && !path2.endsWith(".png") &&
                                    !path2.endsWith(".GIF") && !path2.endsWith(".PNG") && !path2.endsWith(".JPG") && !path2.endsWith(".gif")) {
                                img_coach_head.setImageResource(R.drawable.coach_pic);
                            } else {
                                Glide.with(PrivateCenterDetailsActivity.this).load(result.getFilepic()).placeholder(R.drawable.coach_pic).error(R.drawable.coach_pic).into(img_coach_head);
                            }
                            totalStu.setText("累计学员数" + result.getTotalnum() + "人");
                            place.setText(result.getSchool());
                            team_txt.setText(result.getIntroduction());
                            pri_type.setText(result.getClass_type());
                            coach_address.setText(result.getAddress());
                            img_coach_head.setTag(result.getFile());
                            haopinglv.setText("好评率："+result.getHaopin()+"%");
                            Double zhonghenum=Double.parseDouble(result.getZonghe());
                            Double teachnum = Double.parseDouble(result.getTeach());
                            Double waitnum = Double.parseDouble(result.getWait());
                            zhiliang.removeAllViews();
                            zhonghe.removeAllViews();
                            fuwu.removeAllViews();
                            if (zhonghenum < 1){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star0);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhonghe.addView(image);
                            }
                            if (zhonghenum == 1){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star1);
                                wrapParams = new LinearLayout.LayoutParams(30,30);
                                image.setLayoutParams(wrapParams);
                                zhonghe.addView(image);
                            }
                            if (zhonghenum > 1  && zhonghenum < 2){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star2);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhonghe.addView(image);
                            }
                            if (zhonghenum == 2){
                                for (double k = 0; k < 2; k++) {
                                    ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhonghe.addView(image);
                                }
                            }
                            if (zhonghenum > 2  && zhonghenum < 3){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star3);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhonghe.addView(image);
                            }
                            if (zhonghenum == 3){
                                for (double k = 0; k < 3; k++) {
                                    ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhonghe.addView(image);
                                }
                            }
                            if (zhonghenum> 3  && zhonghenum < 4){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star4);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhonghe.addView(image);
                            }
                            if (zhonghenum == 4){
                                for (double k = 0; k < 4; k++) {
                                    ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhonghe.addView(image);
                                }
                            }
                            if (zhonghenum > 4  && zhonghenum < 5){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star5);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhonghe.addView(image);
                            }
                            if (zhonghenum == 5){
                                for (double k = 0; k < 5; k++) {
                                    ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhonghe.addView(image);
                                }
                            }

                            if (teachnum < 1){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star0);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 1){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star1);
                                wrapParams = new LinearLayout.LayoutParams(30,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum > 1  && teachnum < 2){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star2);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 2){
                                for (double k = 0; k < 2; k++) {
                                    ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhiliang.addView(image);
                                }
                            }
                            if (teachnum > 2  && teachnum < 3){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star3);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 3){
                                for (double k = 0; k < 3; k++) {
                                    ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhiliang.addView(image);
                                }
                            }
                            if (teachnum > 3  && teachnum < 4){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star4);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 4){
                                for (double k = 0; k < 4; k++) {
                                    ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhiliang.addView(image);
                                }
                            }
                            if (teachnum > 4  && teachnum < 5){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star5);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 5){
                                for (double k = 0; k < 5; k++) {
                                    ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhiliang.addView(image);
                                }
                            }

                            if (waitnum < 1){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star0);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum == 1){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star1);
                                wrapParams = new LinearLayout.LayoutParams(30,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum > 1  && waitnum < 2){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star2);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum == 2){
                                for (double k = 0; k < 2; k++) {
                                    ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    fuwu.addView(image);
                                }
                            }
                            if (waitnum > 2  && waitnum < 3){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star3);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum == 3){
                                for (double k = 0; k < 3; k++) {
                                    ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    fuwu.addView(image);
                                }
                            }
                            if (waitnum > 3  && waitnum < 4){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star4);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum == 4){
                                for (double k = 0; k < 4; k++) {
                                    ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    fuwu.addView(image);
                                }
                            }
                            if (waitnum > 4  && waitnum < 5){
                                ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                image.setBackgroundResource(R.drawable.star5);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum== 5){
                                for (double k = 0; k < 5; k++) {
                                    ImageView image = new ImageView(PrivateCenterDetailsActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    fuwu.addView(image);
                                }
                            }
                            zhonghefen.setText(result.getZonghe()+"分");
                            zhiliangfen.setText(result.getTeach() + "分");
                            fuwufen.setText(result.getWait() + "分");
                            falg = true;
                            latitude = result.getLatitude();
                            longitude = result.getLongitude();
                            initMap(result.getLatitude(),result.getLongitude());
                            getCommentFirst(privateCommentUrl);
                        }else {
                            Toast.makeText(PrivateCenterDetailsActivity.this, "没有更多的！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getCommentFirst(String comment) {
        OkHttpUtils
                .post()
                .url(comment)
                .addParams("page","1")
                .addParams("tid", tid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(PrivateCenterDetailsActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        progressDialog.dismiss();
                        Gson gson = new Gson();
                        CommentResult coachInfos = gson.fromJson(s, CommentResult.class);
                        if(coachInfos.getCode() == 200){
                            listStu = coachInfos.getResult();
                            if(listStu.size() == 0){
                                linear_list_noData.setVisibility(View.VISIBLE);
                            }
                            adapter = new CoachFullDetailAdapter(PrivateCenterDetailsActivity.this, listStu);
                            listView.setAdapter(adapter);
                        }else{
                            Toast.makeText(PrivateCenterDetailsActivity.this, coachInfos.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void getComment(String comment) {
        OkHttpUtils
                .post()
                .url(comment)
                .addParams("page", commentPage + "")
                .addParams("tid", tid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(PrivateCenterDetailsActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("BXXC","评论列表2："+s);
                        listView.setTag(s);
                        if (listView.getTag() != null) {
                            setCom();
                        }
                    }
                });
    }
    private void setCom() {
        String str = listView.getTag().toString();
        Gson gson = new Gson();
        CommentResult coachInfo = gson.fromJson(str, CommentResult.class);
        if (coachInfo.getCode() == 200) {
            listStu.addAll(coachInfo.getResult());
            if(listStu.size() == 0){
                linear_list_noData.setVisibility(View.VISIBLE);
            }
            // 实例化listView显示学员的评价
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(PrivateCenterDetailsActivity.this, coachInfo.getReason(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoad() {
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(falg == true){
                    if(linear_list_noData.getVisibility() == View.VISIBLE){
                        commentPage = 1;
                    }else{
                        commentPage = 2;
                    }
                    falg = false;
                }else{
                    if(linear_list_noData.getVisibility() == View.VISIBLE){
                        commentPage = 1;
                    }else{
                        commentPage++;
                    }
                }
                getComment(privateCommentUrl);
                swipeLayout.setLoading(false);
            }
        }, 2000);
    }


    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                commentPage = 1;
                listStu.clear();
                getComment(privateCommentUrl);
                swipeLayout.setRefreshing(false);

            }
        }, 2000);
    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.button_backward:
                finish();
                break;
            case R.id.connectCus:
                new CallDialog(this,"17756086205").call();
                break;
            case R.id.signup_Coach:
                if (signup_Coach.getText().toString().equals("立即预约")){
                    SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
                    String str = sp.getString("userInfo", null);
                    Gson gson = new Gson();
                    userInfo = gson.fromJson(str, UserInfo.class);
                    SharedPreferences sp1 = getSharedPreferences("token", Activity.MODE_PRIVATE);
                    token = sp1.getString("token", null);
                    if (userInfo == null) {
                        intent.setClass(this, LoginActivity.class);
                        intent.putExtra("message","reservation");
                        startActivity(intent);
                    } else {
                        intent.setClass(this, PrivateTeamlistActivity.class);
                        intent.putExtra("name",center_name);
                        intent.putExtra("priTeam", teamResult);
                        intent.putExtra("uid",userInfo.getResult().getUid());
                        intent.putExtra("token",token);
                        intent.putExtra("tid",tid);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.button_forward:
                /**
                 * setDisplayList方法是友盟内部封装好的，我们拿来调用就好了，
                 * 当我们需要多个分享时只需，在括号里面填写即可，不要另外的写dialog
                 * withTargetUrl设置分享链接，
                 * withTitle设置分享显示的标题
                 * withMedia设置分享图片等
                 * withText设置分享文本
                 * setCallback，当分享成功会回调setCallback此方法，用于显示分享动态
                 *
                 * tips：温馨提示,此版本只能分享到签名版的，debug版本无法分享，如果需要debug分享的话
                 *       请在各大平台上注册并完善debug和正式版本的信息填写，然后更换此项目的appkey
                 *
                 */

                image = new UMImage(PrivateCenterDetailsActivity.this, img_coach_head.getTag().toString());
                UMWeb web = new UMWeb(url + share.getTag().toString());
                web.setTitle("百信学车私教分享");//标题
                web.setThumb(image);  //缩略图
                web.setDescription("科技改变生活，百信引领学车！百信学车在这里向您分享我们这里最优秀的私教团队" + pri_center_name.getText().toString());//描述
                new ShareAction(this).setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.QQ,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                        .withMedia(web)
                        .setCallback(umShareListener)
                        .open();
                break;
        }

    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);
            Toast.makeText(PrivateCenterDetailsActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(PrivateCenterDetailsActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(PrivateCenterDetailsActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    //分享必须重写这个借口
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
