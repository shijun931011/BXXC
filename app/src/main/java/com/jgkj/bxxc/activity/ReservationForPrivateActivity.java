package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.CoachFullDetailAdapter;
import com.jgkj.bxxc.bean.CoachInfo;
import com.jgkj.bxxc.bean.SchoolPlaceTotal;
import com.jgkj.bxxc.bean.StuEvaluation;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.tools.CallDialog;
import com.jgkj.bxxc.tools.MyOrientationListener;
import com.jgkj.bxxc.tools.RefreshLayout;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
    //接受数据
    private String coachId;

    /**
     * ListView的加载中footer
     */
    private View mListViewFooter;
    UMImage image;
    String url = "http://www.baixinxueche.com/index.php/Home/Coach/detail.html?pid=";
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
    private TextView price, currentStu, tongguo, totalStu;
    private LinearLayout xinyong, zhiliang, fuwu;
    private TextView zhiliangfen, fuwufen;
    private LinearLayout.LayoutParams wrapParams;
    private String state;
    private int uid;
    private int educationType;
    private String token;
    private SharedPreferences sp;
    private UserInfo userInfo;
    private UserInfo.Result result;
    private List<StuEvaluation> listStu;
    private TextView connectCus,haopinglv;
    //url
    private String coachUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/CoachinfoAgain";
    private String comment = "http://www.baixinxueche.com/index.php/Home/Api/comment";
    private String changeUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenupdata/subjectTwoCoachConfirm";
    private String commentUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/commentMore";

    //地图
    public MapView mMapView;

    //定位
    public LocationClient mLocClient;
    public MyLocationConfiguration.LocationMode mCurrentMode;
    public BitmapDescriptor mCurrentMarker;
    //方向传感器
    private MyOrientationListener myOrientationListener;
    private int mXDirection;
    private double mCurrentLantitude, mCurrentLongitude;
    private float mCurrentAccracy;

    private String address= "";
    private BaiduMap mBaiduMap;
    private BitmapDescriptor bitmap;
    private SchoolPlaceTotal schoolPlaceTotal;
    //Marker地图标签
    private LatLng point;
    private final  MyLocationListenner myListener = new MyLocationListenner();
    boolean isFirstLoc = true; // 是否首次定位
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.reservation);
        headView = getLayoutInflater().inflate(R.layout.coach_head_private, null);
        init();
        initMap();
        getData(coachId, coachUrl);
        bitmapA = BitmapDescriptorFactory.fromResource(R.drawable.a2);

    }


    /**
     * 初始化地图
     */
    private void initMap(){
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.coach_map);
        mBaiduMap = mMapView.getMap();
        //设置是否显示比例尺控件
        mMapView.showScaleControl(false);
        //设置是否显示缩放控件
        mMapView.showZoomControls(false);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(ReservationForPrivateActivity.this);
        mLocClient.registerLocationListener(myListener);
        myOrientationListener = new MyOrientationListener(getApplicationContext());
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mXDirection = (int) x;
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(mCurrentAccracy)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mXDirection)
                        .latitude(mCurrentLantitude)
                        .longitude(mCurrentLongitude).build();
                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
                // 设置自定义图标
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker));
        mLocClient.start();
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
                    option = new MarkerOptions().position(point).zIndex(j).icon(
                            bitmapA);
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
     * 设置popWindow
     * @param results
     */
    private void setPup(List<SchoolPlaceTotal.Result> results) {
        city = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            city[i] = results.get(i).getSchool_aera();
        }
        allMarker();
    }

    /**
     * marker点击事件处理
     */
    private class markerClickListener implements BaiduMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(final Marker marker) {
            LatLng latLng = marker.getPosition();
            List<SchoolPlaceTotal.Result.Res> listSch = schoolPlaceTotal.getResult().get(0).getResult();
            int index = marker.getZIndex();
            double latitude = Double.parseDouble(listSch.get(index).getLatitude());
            double longitude = Double.parseDouble(listSch.get(index).getLongitude());
            if (latLng.latitude == latitude && latLng.longitude == longitude) {
                Button button = new Button(ReservationForPrivateActivity.this
                        .getApplicationContext());
                button.setBackgroundResource(R.drawable.qipao);
                button.setTextColor(getResources().getColor(R.color.black));
                button.setTextSize(12);
                button.setPadding(20, 20, 20, 40);
                button.setText(listSch.get(index).getFaddress());
                mInfoWindow = new InfoWindow(BitmapDescriptorFactory
                        .fromView(button), marker.getPosition(), -70, null);
                mBaiduMap.showInfoWindow(mInfoWindow);
            }
            return true;
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            // 构造定位数据
            if (isFirstLoc) {
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mXDirection).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                mCurrentAccracy = location.getRadius();
                mBaiduMap.setMyLocationData(locData);
            }
            mCurrentLantitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();
        }
    }

    /**
     * 根据cid(教练id)获取教练信息
     *
     * @param coachId 教练信息
     * @param url     请求地址
     */
    private void getData(String coachId, String url) {
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
                        signup_Coach.setTag(s);
                        Gson gson = new Gson();
                        CoachInfo coachInfo = gson.fromJson(s, CoachInfo.class);
                        Log.i("百信学车","预约教练信息" + s);
                        if (coachInfo.getCode() == 200) {
                            List<CoachInfo.Result> list = coachInfo.getResult();
                            CoachInfo.Result result = list.get(0);
                            coach_name.setText("教练员："+ result.getCoachname());
                            DecimalFormat df = new DecimalFormat("#.00");
                            price.setText("￥" + df.format(result.getPrice()));
                            //currentStu.setHint("当前学员数" + result.getStunum() + "人");
                            //tongguo.setHint("通过率：" + result.getTguo() + "%");
                            String path = result.getFile();
                            if (!path.endsWith(".jpg") && !path.endsWith(".jpeg") && !path.endsWith(".png") &&
                                    !path.endsWith(".GIF") && !path.endsWith(".PNG") && !path.endsWith(".JPG") && !path.endsWith(".gif")) {
                                coach_head.setImageResource(R.drawable.coach_pic);
                            } else {
                                Glide.with(ReservationForPrivateActivity.this).load(result.getFile()).placeholder(R.drawable.coach_pic).error(R.drawable.coach_pic).into(coach_head);
                            }
                            totalStu.setText("累计学员数" + result.getCount_stu() + "人");
                            place.setText("校区：" + result.getFaddress());
                            coach_head.setTag(result.getFile());
                            share.setTag(result.getCid());
                            int credit = result.getCredit();
                            int teachnum = Integer.parseInt(result.getTeach());
                            int waitnum = Integer.parseInt(result.getWait());
                            haopinglv.setText("好评率："+result.getHaopin()+"%");
                            zhiliang.removeAllViews();
                            xinyong.removeAllViews();
                            fuwu.removeAllViews();
                            for (int k = 0; k < credit; k++) {
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.xin_1);
                                LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(30, 30);
                                image.setLayoutParams(wrapParams);
                                xinyong.addView(image);
                            }
                            for (int k = 0; k < teachnum; k++) {
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star1);
                                LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(30, 30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            for (int k = 0; k < waitnum; k++) {
                                ImageView image = new ImageView(ReservationForPrivateActivity.this);
                                image.setBackgroundResource(R.drawable.star1);
                                LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(30, 30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            zhiliangfen.setText(result.getTeach() + ".0分");
                            fuwufen.setText(result.getWait() + ".0分");
                            List<StuEvaluation> listStu = new ArrayList<StuEvaluation>();
                            StuEvaluation stu = new StuEvaluation();
                            stu.setComment(result.getComment());
                            stu.setDate(result.getDate());
                            stu.setDefault_file(result.getDefault_file());
                            listStu.add(stu);
                            // 实例化listView显示学员的评价
                            CoachFullDetailAdapter adapter = new CoachFullDetailAdapter(ReservationForPrivateActivity.this, listStu);
                            listView.setAdapter(adapter);
                        } else {
                            //Toast.makeText(ReservationActivity.this, "没有更多的！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * 初始化控件
     */
    private void init() {
        listStu = new ArrayList<StuEvaluation>();
        connectCus = (TextView) findViewById(R.id.connectCus);
        connectCus.setOnClickListener(this);
        signup_Coach = (TextView) findViewById(R.id.signup_Coach);
        signup_Coach.setOnClickListener(this);
        xinyong = (LinearLayout) headView.findViewById(R.id.xinyong);
        zhiliang = (LinearLayout) headView.findViewById(R.id.zhiliang);
        fuwu = (LinearLayout) headView.findViewById(R.id.fuwu);
        zhiliangfen = (TextView) headView.findViewById(R.id.zhiliangfen);
        fuwufen = (TextView) headView.findViewById(R.id.fuwufen);
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid",uid);
        educationType = intent.getIntExtra("educationType",educationType);
        coachId = intent.getStringExtra("coachId");
        token = intent.getStringExtra("token");
        int isChange = intent.getIntExtra("isChange", -1);
        if (isChange == 0) {
            signup_Coach.setText("更改教练");
        } else {
            signup_Coach.setText("立即预约");
        }
        coach_head = (ImageView) headView.findViewById(R.id.coach_head);
        place = (TextView) headView.findViewById(R.id.place);
        coach_name = (TextView) headView.findViewById(R.id.coach_name);
        price = (TextView) headView.findViewById(R.id.price);
        totalStu = (TextView) headView.findViewById(R.id.totalStu);
        haopinglv = (TextView) headView.findViewById(R.id.haopinglv);

        share = (ImageView) headView.findViewById(R.id.share);
        share.setOnClickListener(this);
        //费用说明
        //costsThat = (TextView) headView.findViewById(R.id.costsThat);
        //costsThat.setOnClickListener(this);
        // 实例化控件
        text_title = (TextView) findViewById(R.id.text_title);
        text_title.setText("个人简介");
        back = (Button) findViewById(R.id.button_backward);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        //fourPromise = (LinearLayout) headView.findViewById(R.id.fourPromiselinearLayout);
        //fourPromise.setOnClickListener(this);
        // 实例化listView显示学员的评价
        listView = (ListView) findViewById(R.id.student_evaluate_listView);
        listView.setFocusable(false);
        listView.addHeaderView(headView);
        //上拉刷新
        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);
    }

    /**
     * 更改教练
     * @param uid 用户id
     */
    private void changeCoach(String uid) {
        OkHttpUtils
                .post()
                .url(changeUrl)
                .addParams("cid", coachId)
                .addParams("uid", uid)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                             @Override
                             public void onError(Call call, Exception e, int i) {
                                 progressDialog.dismiss();
                                 Toast.makeText(ReservationForPrivateActivity.this, "网络状态不佳，请稍后再试。", Toast.LENGTH_LONG).show();
                             }

                             @Override
                             public void onResponse(String s, int i) {
                                 progressDialog.dismiss();
                                 Gson gson = new Gson();
                                 Result result = gson.fromJson(s, Result.class);
                                 Toast.makeText(ReservationForPrivateActivity.this, result.getReason(), Toast.LENGTH_LONG).show();
                                 if (result.getCode() == 200) {
                                     Toast.makeText(ReservationForPrivateActivity.this, result.getReason(), Toast.LENGTH_SHORT).show();
                                     finish();
                                 }
                             }
                         }
                );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.connectCus:
                new CallDialog(this,"055165555744").call();
                break;
            case R.id.signup_Coach:
                if (signup_Coach.getText().toString().equals("立即预约")) {
                    SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
                    String str = sp.getString("userInfo", null);
                    Gson gson = new Gson();
                    userInfo = gson.fromJson(str, UserInfo.class);
                    if (userInfo == null) {
                        Intent intent2 = new Intent();
                        intent2.setClass(ReservationForPrivateActivity.this, LoginActivity.class);
                        startActivity(intent2);
                        finish();
                    } else {
                        Intent intent2 = new Intent();
                        intent2.setClass(ReservationForPrivateActivity.this, ReservationDetailActivity.class);
                        intent2.putExtra("uid",uid);
                        intent2.putExtra("token",token);
                        intent2.putExtra("coachInfo", signup_Coach.getTag().toString());
                        startActivity(intent2);
                    }
                } else if (signup_Coach.getText().toString().equals("更改教练")) {
                    SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
                    String str = sp.getString("userInfo", null);
                    Gson gson = new Gson();
                    userInfo = gson.fromJson(str, UserInfo.class);
                    if (userInfo == null) {
                        Intent intent2 = new Intent();
                        intent2.setClass(ReservationForPrivateActivity.this, LoginActivity.class);
                        startActivity(intent2);
                        finish();
                    } else {
                        progressDialog = ProgressDialog.show(ReservationForPrivateActivity.this, null, "修改中...");
                        changeCoach(userInfo.getResult().getUid() + "");
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

                new ShareAction(this).setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                        .withText("科技改变生活，百信引领学车！百信学车在这里向您分享我们这里最优秀的教练"+coach_name.getText().toString())
                        .withMedia(image)
                        .withTitle("百信学车向您分享")
                        .withTargetUrl(url + share.getTag().toString())
                        .setCallback(shareListener)
                        .open();
                break;
        }
    }

    /**
     * 创建展示弹框
     *
     * @param i 表示是展示4个承诺还是服务费用
     */
    private void createDialog(int i) {
        dialog = new Dialog(ReservationForPrivateActivity.this, R.style.ActionSheetDialogStyle);
        if (i == 0) {
            dialogView = LayoutInflater.from(ReservationForPrivateActivity.this).inflate(R.layout.coststhat, null);
        } else if (i == 1) {
            dialogView = LayoutInflater.from(ReservationForPrivateActivity.this).inflate(R.layout.fourpromise, null);
        }
        Button btn = (Button) dialogView.findViewById(R.id.dialog_sure);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        // 将布局设置给Dialog
        dialog.setContentView(dialogView);
        // 获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        // 设置dialog横向充满
        dialogWindow.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.BOTTOM);
        /**
         * 将对话框的大小按屏幕大小的百分比设置
         */
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.5); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(p);
        dialog.show();// 显示对话框
    }

    //分享后回调方法
    private UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ReservationForPrivateActivity.this, platform + " 分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ReservationForPrivateActivity.this, platform + " 分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(ReservationForPrivateActivity.this, platform + " 分享取消", Toast.LENGTH_SHORT).show();
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
        CoachInfo coachInfo = gson.fromJson(str, CoachInfo.class);
        if (coachInfo.getCode() == 200) {
            List<CoachInfo.Result> list = coachInfo.getResult();
            for (int k = 0; k < list.size(); k++) {
                CoachInfo.Result result = list.get(k);
                StuEvaluation stu = new StuEvaluation();
                stu.setComment(result.getComment());
                stu.setDate(result.getDate());
                stu.setDefault_file(result.getDefault_file());
                listStu.add(stu);
            }
            listView.setFocusable(false);
            // 实例化listView显示学员的评价
            CoachFullDetailAdapter adapter = new CoachFullDetailAdapter(ReservationForPrivateActivity.this, listStu);
            listView.setAdapter(adapter);
        } else {
            Toast.makeText(ReservationForPrivateActivity.this, coachInfo.getReason(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoad() {
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                commentPage++;
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
