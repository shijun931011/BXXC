package com.jgkj.bxxc.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.CoachFullDetailAdapter;
import com.jgkj.bxxc.bean.CoachInfo;
import com.jgkj.bxxc.bean.SchoolPlaceTotal;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;


/**
 * Created by fangzhou on 2016/10/29.
 * 教练个人简介和显示部分学员评价，
 * 可以进行，对教练的收藏和分享
 */
public class ReservationForPrivateActivity extends Activity implements OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        RefreshLayout.OnLoadListener {
    private CoachFullDetailAdapter adapter;
    private ListView listView;
    private TextView text_title, call;
    private Button back;
    private TextView coach_name;
    //教练评价星星
    private LinearLayout star;
    private TextView place;
    //上拉刷新
    private RefreshLayout swipeLayout;
    private View headView;
    private TextView coach_introduce;  //教练简介
    //接受数据
    private String coachId;
    /**
     * ListView的加载中footer
     */
    private View mListViewFooter;
    UMImage image;
    String url = "http://www.baixinxueche.com/index.php/Home/Info/indexs.html?pid=";
    private int commentPage = 0;
    private TextView signup_Coach;
    private ImageView coach_head, share;
    private TextView costsThat;
    private TextView marketPrise, chexing, myclass;
    //创建费用说明dialog
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private View dialogView;
    private LinearLayout fourPromise;
    //教练信息
    private TextView price, currentStu, tongguo, totalStu,original_price,coach_address;
    private LinearLayout zhonghe, zhiliang, fuwu;
    private TextView zhiliangfen, fuwufen,zhonghefen;
    private LinearLayout.LayoutParams wrapParams;
    private String state;
    private int uid;
    private int educationType;
    private String token;
    private SharedPreferences sp;
    private UserInfo userInfo;
    private List<CommentEntity> listStu;
    private TextView connectCus,haopinglv;
    //url
//    private String coachUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/CoachinfoAgain";
    private String coachUrl = "http://www.baixinxueche.com/index.php/Home/Hotsearch/CoachinfoPersonal";
    private String comment = "http://www.baixinxueche.com/index.php/Home/Api/comment";
    private String changeUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenupdata/subjectTwoCoachConfirm";
    private String commentUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/commentMore";
    //地图
    public MapView mMapView;
    //定位
    public LocationClient mLocClient;
    public MyLocationConfiguration.LocationMode mCurrentMode;
    public BitmapDescriptor mCurrentMarker;
    private BaiduMap mBaiduMap;
    private BitmapDescriptor bitmap;
    private SchoolPlaceTotal schoolPlaceTotal;
    //Marker地图标签
    private LatLng point;
    private InfoWindow mInfoWindow;
    private BitmapDescriptor  bitmapA;
    private String[] city = new String[0];
    private Marker mMarker;
    private class Result {
        private int code;
        private String reason;
        public String getReason() {
            return reason;
        }
        public int getCode() {
            return code;
        }
    }
    private LinearLayout linear_list_noData;
    private CoachInfo.Result result;
    private boolean falg = false;
    public static List<Activity> activityList = new LinkedList<Activity>();
    private static final String[] authBaseArr = { Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION };
    private static final String[] authComArr = { Manifest.permission.READ_PHONE_STATE };
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.reservation);
        //显示ProgressDialog
        progressDialog = ProgressDialog.show(ReservationForPrivateActivity.this, "加载中...", "请等待...", true, false);
        headView = getLayoutInflater().inflate(R.layout.coach_head_private, null);
        init();
        getData(coachId, coachUrl);
        bitmapA = BitmapDescriptorFactory.fromResource(R.drawable.a2);
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
        //LatLng cenpt = new LatLng(30.663791,104.07281);
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
                intent.setClass(ReservationForPrivateActivity.this,BDMAPActivity.class);
                intent.putExtra("lantitude",Double.parseDouble(latitude));
                intent.putExtra("longitude",Double.parseDouble(longitude));
                intent.putExtra("faddress", result.getFaddress());
                intent.putExtra("address",result.getAddress());
                intent.putExtra("value","1");
                startActivity(intent);
            }
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

    }

    /**
     * 全城的marker标记
     */
    private void allMarker(){
        List<SchoolPlaceTotal.Result.Res> listSch = schoolPlaceTotal.getResult().get(0).getResult();
        for (int j = 0; j < listSch.size(); j++) {
            double latitude = Double.parseDouble(listSch.get(j).getLatitude());
            double longitude = Double.parseDouble(listSch.get(j).getLongitude());
            point = new LatLng(latitude, longitude);
            OverlayOptions option = null;
            switch (j) {
                case 0:
                    option = new MarkerOptions().position(point).zIndex(j).icon(bitmapA);
                    break;
            }
            mMarker = (Marker) mBaiduMap.addOverlay(option);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(point).zoom(17.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                    .newMapStatus(builder.build()));
            mBaiduMap.setOnMarkerClickListener(new markerClickListener());
        }
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

    /**
     * 根据cid(教练id)获取教练信息
     * @param coachId 教练信息
     * @param url     请求地址
     */
    private void getData(String coachId, String url) {
        Log.i("百信学车","教练cid=" + coachId + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("cid", coachId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(ReservationForPrivateActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","私教教练信息" + s);
                        signup_Coach.setTag(s);
                        Gson gson = new Gson();
                        CoachInfo coachInfo = gson.fromJson(s, CoachInfo.class);
                        if (coachInfo.getCode() == 200) {
                            List<CoachInfo.Result> list = coachInfo.getResult();
                            result = list.get(0);
                            coach_name.setText(result.getCoachname());
                            DecimalFormat df = new DecimalFormat("#.00");
                            price.setText("￥" + df.format(result.getPrice()));
                            String path = result.getFile();
                            if (!path.endsWith(".jpg") && !path.endsWith(".jpeg") && !path.endsWith(".png") &&
                                    !path.endsWith(".GIF") && !path.endsWith(".PNG") && !path.endsWith(".JPG") && !path.endsWith(".gif")) {
                                coach_head.setImageResource(R.drawable.coach_pic);
                            } else {
                                Glide.with(ReservationForPrivateActivity.this).load(result.getFile()).placeholder(R.drawable.coach_pic).error(R.drawable.coach_pic).into(coach_head);
                            }
                            totalStu.setText("累计学员数" + result.getCount_stu() + "人");
                            place.setText("校区：" + result.getFaddress());
                            original_price.setHint("￥" + df.format(result.getMarket_price()));
                            original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                            coach_address.setText(result.getAddress());
                            coach_head.setTag(result.getFile());
                            share.setTag(result.getCid());
                            if (result.getIntroduction().equals("")){
                                coach_introduce.setVisibility(View.VISIBLE);
                            }else{
                                coach_introduce.setText(result.getIntroduction());
                            }
                            Double zhonghenum=Double.parseDouble(result.getZonghe());
                            Double teachnum = Double.parseDouble(result.getTeach());
                            Double waitnum = Double.parseDouble(result.getWait());
                            haopinglv.setText("好评率："+result.getHaopin()+"%");
                            zhiliang.removeAllViews();
                            zhonghe.removeAllViews();
                            fuwu.removeAllViews();
                            if (zhonghenum < 1){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star0);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhonghe.addView(image);
                            }
                            if (zhonghenum == 1){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star1);
                                wrapParams = new LinearLayout.LayoutParams(30,30);
                                image.setLayoutParams(wrapParams);
                                zhonghe.addView(image);
                            }
                            if (zhonghenum > 1  && zhonghenum < 2){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star2);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhonghe.addView(image);
                            }
                            if (zhonghenum == 2){
                                for (double k = 0; k < 2; k++) {
                                    ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhonghe.addView(image);
                                }
                            }
                            if (zhonghenum > 2  && zhonghenum < 3){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star3);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhonghe.addView(image);
                            }
                            if (zhonghenum == 3){
                                for (double k = 0; k < 3; k++) {
                                    ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhonghe.addView(image);
                                }
                            }
                            if (zhonghenum> 3  && zhonghenum < 4){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star4);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhonghe.addView(image);
                            }
                            if (zhonghenum == 4){
                                for (double k = 0; k < 4; k++) {
                                    ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhonghe.addView(image);
                                }
                            }
                            if (zhonghenum > 4  && zhonghenum < 5){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star5);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhonghe.addView(image);
                            }
                            if (zhonghenum == 5){
                                for (double k = 0; k < 5; k++) {
                                    ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhonghe.addView(image);
                                }
                            }

                            if (teachnum < 1){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star0);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 1){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star1);
                                wrapParams = new LinearLayout.LayoutParams(30,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum > 1  && teachnum < 2){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star2);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 2){
                                for (double k = 0; k < 2; k++) {
                                    ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhiliang.addView(image);
                                }
                            }
                            if (teachnum > 2  && teachnum < 3){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star3);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 3){
                                for (double k = 0; k < 3; k++) {
                                    ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhiliang.addView(image);
                                }
                            }
                            if (teachnum > 3  && teachnum < 4){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star4);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 4){
                                for (double k = 0; k < 4; k++) {
                                    ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhiliang.addView(image);
                                }
                            }
                            if (teachnum > 4  && teachnum < 5){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star5);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 5){
                                for (double k = 0; k < 5; k++) {
                                    ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhiliang.addView(image);
                                }
                            }

                            if (waitnum < 1){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star0);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum == 1){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star1);
                                wrapParams = new LinearLayout.LayoutParams(30,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum > 1  && waitnum < 2){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star2);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum == 2){
                                for (double k = 0; k < 2; k++) {
                                    ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    fuwu.addView(image);
                                }
                            }
                            if (waitnum > 2  && waitnum < 3){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star3);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum == 3){
                                for (double k = 0; k < 3; k++) {
                                    ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    fuwu.addView(image);
                                }
                            }
                            if (waitnum > 3  && waitnum < 4){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star4);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum == 4){
                                for (double k = 0; k < 4; k++) {
                                    ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    fuwu.addView(image);
                                }
                            }
                            if (waitnum > 4  && waitnum < 5){
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star5);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum== 5){
                                for (double k = 0; k < 5; k++) {
                                    ImageView image = new ImageView(ReservationForPrivateActivity.this);
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
                            getCommentFirst(commentUrl);
                        } else {
                            Toast.makeText(ReservationForPrivateActivity.this, "没有更多的！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * 初始化控件
     */
    private void init() {
        listStu = new ArrayList<CommentEntity>();
        connectCus = (TextView) findViewById(R.id.connectCus);
        connectCus.setOnClickListener(this);
        signup_Coach = (TextView) findViewById(R.id.signup_Coach);
        signup_Coach.setOnClickListener(this);
        zhonghe = (LinearLayout) headView.findViewById(R.id.zhonghe);
        zhiliang = (LinearLayout) headView.findViewById(R.id.zhiliang);
        fuwu = (LinearLayout) headView.findViewById(R.id.fuwu);
        zhonghefen = (TextView) headView.findViewById(R.id.zhonghefen);
        zhiliangfen = (TextView) headView.findViewById(R.id.zhiliangfen);
        fuwufen = (TextView) headView.findViewById(R.id.fuwufen);
        coach_introduce = (TextView) headView.findViewById(R.id.coach_introduce);
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid",uid);
        educationType = intent.getIntExtra("educationType",educationType);
        coachId = intent.getStringExtra("coachId");
        token = intent.getStringExtra("token");
        Log.d("百信学车","我的私教教练详情："+uid+"coachId="+coachId+"token="+token);
        signup_Coach.setText("立即预约");
        coach_head = (ImageView) headView.findViewById(R.id.coach_head);
        place = (TextView) headView.findViewById(R.id.place);
        coach_name = (TextView) headView.findViewById(R.id.coach_name);
        price = (TextView) headView.findViewById(R.id.price);
        totalStu = (TextView) headView.findViewById(R.id.totalStu);
        original_price = (TextView) headView.findViewById(R.id.original_price);
        original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        coach_address = (TextView) headView.findViewById(R.id.coach_address);
        haopinglv = (TextView) headView.findViewById(R.id.haopinglv);
        share = (ImageView) headView.findViewById(R.id.share);
        linear_list_noData = (LinearLayout)headView.findViewById(R.id.linear_list_noData);
        share.setOnClickListener(this);
        // 实例化控件
        text_title = (TextView) findViewById(R.id.text_title);
        text_title.setText("教练详情");
        back = (Button) findViewById(R.id.button_backward);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        // 实例化listView显示学员的评价
        listView = (ListView) findViewById(R.id.student_evaluate_listView);
        listView.setFocusable(false);
        listView.addHeaderView(headView, null, false);
        //上拉刷新
        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.connectCus:
                new CallDialog(this,"17756086205").call();
                break;
            case R.id.signup_Coach:
                if (signup_Coach.getText().toString().equals("立即预约")) {
                    SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
                    String str = sp.getString("userInfo", null);
                    Gson gson = new Gson();
                    userInfo = gson.fromJson(str, UserInfo.class);
                    SharedPreferences sp1 = getSharedPreferences("token", Activity.MODE_PRIVATE);
                    token = sp1.getString("token", null);
                    if (userInfo == null) {
                        Intent intent2 = new Intent();
                        intent2.setClass(ReservationForPrivateActivity.this, LoginActivity.class);
                        intent2.putExtra("message","reservation");
                        startActivity(intent2);
                    } else {
                        Intent intent2 = new Intent();
                        intent2.setClass(ReservationForPrivateActivity.this, ReservationDetailActivity.class);
                        intent2.putExtra("uid",userInfo.getResult().getUid());
                        intent2.putExtra("token",token);
                        intent2.putExtra("coachInfo", signup_Coach.getTag().toString());
                        intent2.putExtra("cid",coachId);
                        intent2.putExtra("privateclass",3);
                        intent2.putExtra("tid","0");
                        startActivity(intent2);
                    }
                }
                break;
            case R.id.share:
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

                image = new UMImage(ReservationForPrivateActivity.this, coach_head.getTag().toString());
                UMWeb web = new UMWeb(url + share.getTag().toString());
                web.setTitle("百信学车向您分享");//标题
                web.setThumb(image);  //缩略图
                web.setDescription("科技改变生活，百信引领学车！百信学车在这里向您分享我们这里最优秀的教练" + coach_name.getText().toString());//描述
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
            Toast.makeText(ReservationForPrivateActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ReservationForPrivateActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ReservationForPrivateActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
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

    private void getComment(String comment) {
        OkHttpUtils
                .post()
                .url(comment)
                .addParams("page", commentPage + "")
                .addParams("cid", coachId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(ReservationForPrivateActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("百信学车","私教更多评论"+s);
                        listView.setTag(s);
                        if (listView.getTag() != null) {
                            setCom();
                        }
                    }
                });
    }

    private void getCommentFirst(String comment) {
        OkHttpUtils
                .post()
                .url(comment)
                .addParams("page","1")
                .addParams("cid", coachId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(ReservationForPrivateActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("百信学车","评价私教教练："+s);
                        progressDialog.dismiss();
                        Gson gson = new Gson();
                        CommentResult coachInfos = gson.fromJson(s, CommentResult.class);
                        if(coachInfos.getCode() == 200){
                            listStu = coachInfos.getResult();
                            if(listStu.size() == 0){
                                linear_list_noData.setVisibility(View.VISIBLE);
                            }
                            adapter = new CoachFullDetailAdapter(ReservationForPrivateActivity.this, listStu);
                            listView.setAdapter(adapter);
                        }else{
                            Toast.makeText(ReservationForPrivateActivity.this, coachInfos.getReason(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private void setCom() {
        String str = listView.getTag().toString();
        Gson gson = new Gson();
        CommentResult coachInfo = gson.fromJson(str, CommentResult.class);
        if (coachInfo.getCode() == 200) {
            //listStu = coachInfo.getResult();
            listStu.addAll(coachInfo.getResult());
            if(listStu.size() == 0){
                linear_list_noData.setVisibility(View.VISIBLE);
            }
            // 实例化listView显示学员的评价
            adapter.notifyDataSetChanged();
//            CoachFullDetailAdapter adapter = new CoachFullDetailAdapter(ReservationForPrivateActivity.this, listStu);
//            listView.setAdapter(adapter);
        } else {
//            Toast.makeText(ReservationForPrivateActivity.this, coachInfo.getReason(), Toast.LENGTH_SHORT).show();
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
                getComment(commentUrl);
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
                getComment(commentUrl);
                swipeLayout.setRefreshing(false);

            }
        }, 2000);
    }
}
