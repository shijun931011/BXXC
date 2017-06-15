package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.SchoolAction;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.bean.Version;
import com.jgkj.bxxc.fragment.CoachFragment;
import com.jgkj.bxxc.fragment.IndexFragment;
import com.jgkj.bxxc.fragment.My_Setting_Fragment;
import com.jgkj.bxxc.fragment.StudyFragment;
import com.jgkj.bxxc.tools.CallDialog;
import com.jgkj.bxxc.tools.GetVersion;
import com.jgkj.bxxc.tools.InvitedCouponDialog;
import com.jgkj.bxxc.tools.JPushDataUitl;
import com.jgkj.bxxc.tools.RemainBaseDialog;
import com.jgkj.bxxc.tools.SelectPopupWindow;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.jgkj.bxxc.tools.UpdateManger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Date;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

public class HomeActivity extends FragmentActivity implements OnClickListener {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    private Fragment mCurrentFragment, per, index, my_set, coach, map, study;
    private TextView text_title, place;
    private ImageView kefu,im_title;
    private ScrollView scroll_bar;
    private RelativeLayout titlebar;
    private FrameLayout frame, car_frameLayout;
    private static String[] school = {"越达驾校(新周谷堆校区)", "越达驾校(包河区第一校区)", "越达驾校(大学城中心校区)", "越达驾校(蜀山区新华校区)", "越达驾校(庐阳区总校区)"};
    private static String[] cityInfo = {"合肥"};
    //popupWindow
    private SelectPopupWindow mPopupWindow = null;
    private String[] city = {"合肥"};
    private String[][] datialPlace = {{"新周谷堆校区", "包河区第一校区", "大学城中心校区", "蜀山区新华校区", "庐阳区总校区"}};
    private Bundle bundle;
    private SchoolAction schoolAction;
    private Dialog dialog;
    private View inflate;
    private CarSendActivity carSendMap;
    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    //判断调过来的Activity
    private String fromActivity = null;
    //jPush推送
    private EditText msgText;
    public final static int CLOSE_ACTIVITY = 1001;
    public final static int TOUCH_DOWN = 1002;
    public static boolean isForeground = false;
    private Boolean isLogined = false;
    private SharedPreferences sp;
    private UserInfo userInfo;
    private UserInfo.Result result;
    private String token;
    private int uid;
    private String versionUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/versionandroid";
    private Drawable rbImg1;
    private Drawable rbImg2;
    private Drawable rbImg3;
    private Drawable rbImg4;

    private Context context;
    private boolean flag_dialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        context = this;
        init();
        //.getApplicationContext（）取的是这个应 用程序的Context，Activity.this取的是这个Activity的Context
        JPushInterface.init(getApplicationContext());
        isClearLoginSession();
        registerMessageReceiver();
        //判断
        if(flag_dialog != true){
            isCouponDialog();
            checkSoftInfo();
        }
    }

    private void isCouponDialog(){
        SharedPreferences sp1 = getSharedPreferences("ReceivedCoupon", Activity.MODE_PRIVATE);
        int isFirst = sp1.getInt("InvitedCoupon", 0);
        if (isFirst != 1){
            new InvitedCouponDialog(this).call();
        }
    }


    /**
     * 做本地判断，如果上次运行app是三天前，将会清空登录状态
     * 如果上次运行app是三天内，那么覆盖userSessionTime
     */
    private void isClearLoginSession() {
        SharedPreferences sp2 = getSharedPreferences("UserLoginSession", Activity.MODE_PRIVATE);
        SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
        Long currentTime = new Date().getTime();
        Long loginTime = sp2.getLong("userSessionTime", currentTime);
        if ((currentTime - loginTime) > 259200000) {
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.commit();
        } else {
            SharedPreferences.Editor editor2 = sp2.edit();
            editor2.putLong("userSessionTime", currentTime);
            editor2.commit();
        }
    }

    /**
     * 检查版本更新
     */
    private void checkSoftInfo() {
        OkHttpUtils.get().url(versionUrl).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                Toast.makeText(HomeActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(String s, int i) {
                Gson gson = new Gson();
                Version version = gson.fromJson(s, Version.class);
                if (version.getCode() == 200) {
                    if (version.getResult().get(0).getVersionCode() > GetVersion.getVersionCode(context)) {
                        UpdateManger updateManger = new UpdateManger(context, version.getResult().get(0).getPath(), version.getResult().get(0).getVersionName());
                        updateManger.checkUpdateInfo();
                    }
                }
            }
        });
    }

    // 初始化控件及部分方法
    private void init() {
        rbImg1 = getResources().getDrawable(R.drawable.selector_home_bottom);
        rbImg1.setBounds(0, 0, 55, 55);
        rbImg2 = getResources().getDrawable(R.drawable.selector_coach_bottom);
        rbImg2.setBounds(0, 0, 55, 55);
        rbImg3 = getResources().getDrawable(R.drawable.selector_study_bottom);
        rbImg3.setBounds(0, 0, 55, 55);
        rbImg4 = getResources().getDrawable(R.drawable.selector_me_bottom);
        rbImg4.setBounds(0, 0, 55, 55);
        titlebar = (RelativeLayout) findViewById(R.id.title_bar);
        //地区
        place = (TextView) findViewById(R.id.txt_place);
        place.setOnClickListener(this);
        //客服电话
        kefu = (ImageView) findViewById(R.id.remind);
        im_title = (ImageView) findViewById(R.id.im_title);
        kefu.setOnClickListener(this);
        car_frameLayout = (FrameLayout) findViewById(R.id.car_send_map);
        mCurrentFragment = index;
        frame = (FrameLayout) findViewById(R.id.index_fragment_layout);
        // 实例化按钮
        radioButton1 = (RadioButton) findViewById(R.id.radio_button_01);
        radioButton2 = (RadioButton) findViewById(R.id.radio_button_02);//radio_button_03
        radioButton4 = (RadioButton) findViewById(R.id.radio_button_04);
        radioButton3 = (RadioButton) findViewById(R.id.radio_button_03);
        text_title = (TextView) findViewById(R.id.text_title);
        scroll_bar = (ScrollView) findViewById(R.id.scroll_bar);
        //smoothScrollTo类似于scrollTo，但是滚动的时候是平缓的而不是立即滚动到某处。
        // 另外，smoothScrollTo()方法可以打断滑动动画。
        scroll_bar.smoothScrollTo(0, 0);
        // 设置监听
        radioButton1.setOnClickListener(this);
        radioButton2.setOnClickListener(this);
        radioButton4.setOnClickListener(this);
        radioButton3.setOnClickListener(this);
        //设置按钮顶部图标   左、上、右、下
        radioButton1.setCompoundDrawables(null, rbImg1, null, null);
        radioButton2.setCompoundDrawables(null, rbImg2, null, null);
        radioButton3.setCompoundDrawables(null, rbImg3, null, null);
        radioButton4.setCompoundDrawables(null, rbImg4, null, null);
        // 初始化一个fragment填充首页
        IndexFragment indexFragment = new IndexFragment();
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        CoachFragment coach = new CoachFragment();
        my_set = new My_Setting_Fragment();
        study = new StudyFragment();
        Intent intent = getIntent();
        fromActivity = intent.getStringExtra("FromActivity");
        if (fromActivity == null) {
            text_title.setText("我的资料");
            text_title.setVisibility(View.VISIBLE);
            im_title.setVisibility(View.GONE);
            radioButton4.setChecked(true);
            scroll_bar.setVisibility(View.GONE);
            car_frameLayout.setVisibility(View.VISIBLE);
            transaction.add(R.id.car_send_map, my_set);
            radioButton4.setChecked(true);
        } else {
            if (fromActivity.equals("WelcomeActivity")) {
                transaction.add(R.id.index_fragment_layout, indexFragment);
                kefu.setImageResource(R.drawable.kefu_phone);
                place.setText("合肥");
                text_title.setText("百信学车");
                text_title.setVisibility(View.GONE);
                im_title.setVisibility(View.VISIBLE);
                place.setVisibility(View.VISIBLE);
                kefu.setVisibility(View.VISIBLE);
                radioButton1.setChecked(true);
            } else if (fromActivity.equals("SimpleCoachActivity") || fromActivity.equals("IndexFragment")) {
                titlebar.setVisibility(View.GONE);
                radioButton2.setChecked(true);
                car_frameLayout.setVisibility(View.VISIBLE);
                transaction.add(R.id.car_send_map, coach);
                //不弹框标识
                flag_dialog = true;
            } else if (fromActivity.equals("MySetting")) {
                text_title.setText("我的资料");
                text_title.setVisibility(View.VISIBLE);
                im_title.setVisibility(View.GONE);
                radioButton4.setChecked(true);
                scroll_bar.setVisibility(View.GONE);
                car_frameLayout.setVisibility(View.VISIBLE);
                transaction.add(R.id.car_send_map, my_set);
                //不弹框标识
                flag_dialog = true;
            }else if(fromActivity.equals("LoginActivity")){
                transaction.add(R.id.index_fragment_layout, indexFragment);
                kefu.setImageResource(R.drawable.kefu_phone);
                place.setText("合肥");
                text_title.setText("百信学车");
                text_title.setVisibility(View.GONE);
                im_title.setVisibility(View.VISIBLE);
                place.setVisibility(View.VISIBLE);
                kefu.setVisibility(View.VISIBLE);
                radioButton1.setChecked(true);
                //不弹框标识
                flag_dialog = true;
            }
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * 点击监听事件
     *
     * @param v 当前视图
     */
    @Override
    public void onClick(View v) {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            // 底部导航栏监听
            case R.id.radio_button_01:
                titlebar.setVisibility(View.VISIBLE);
                place.setVisibility(View.VISIBLE);
                kefu.setVisibility(View.VISIBLE);
                index = new IndexFragment();
                if (mCurrentFragment != index) {
                    scroll_bar.setVisibility(View.VISIBLE);
                    car_frameLayout.setVisibility(View.GONE);
                    text_title.setText("百信学车");
                    text_title.setVisibility(View.GONE);
                    im_title.setVisibility(View.VISIBLE);
                    text_title.setVisibility(View.GONE);
                    im_title.setVisibility(View.VISIBLE);
                    place.setText("合肥");
                    kefu.setImageResource(R.drawable.kefu_phone);
                    transaction.replace(R.id.index_fragment_layout, index).addToBackStack(null).commit();
                    mCurrentFragment = index;
                }
                break;
            case R.id.radio_button_02:
                titlebar.setVisibility(View.GONE);
                coach = new CoachFragment();
                if (mCurrentFragment != coach) {
                    scroll_bar.setVisibility(View.GONE);
                    car_frameLayout.setVisibility(View.VISIBLE);
                    transaction.replace(R.id.car_send_map, coach).addToBackStack(null).commit();
                    mCurrentFragment = coach;
                }
                break;
            case R.id.radio_button_03:
                text_title.setVisibility(View.VISIBLE);
                im_title.setVisibility(View.GONE);
                titlebar.setVisibility(View.VISIBLE);
                kefu.setVisibility(View.GONE);
                place.setVisibility(View.GONE);
                study = new StudyFragment();
                if (mCurrentFragment != study) {
                    scroll_bar.setVisibility(View.GONE);
                    car_frameLayout.setVisibility(View.VISIBLE);
                    text_title.setText("学习");
                    transaction.replace(R.id.car_send_map, study).addToBackStack(null).commit();
                    mCurrentFragment = study;
                }
                break;
            case R.id.radio_button_04:
                text_title.setVisibility(View.VISIBLE);
                im_title.setVisibility(View.GONE);
                titlebar.setVisibility(View.VISIBLE);
                kefu.setVisibility(View.GONE);
                place.setVisibility(View.GONE);
                my_set = new My_Setting_Fragment();
                if (mCurrentFragment != my_set) {
                    scroll_bar.setVisibility(View.GONE);
                    car_frameLayout.setVisibility(View.VISIBLE);
                    scroll_bar.setEnabled(false);
                    scroll_bar.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View arg0, MotionEvent arg1) {
                            return true;
                        }
                    });
                    text_title.setText("我的资料");
                    transaction.replace(R.id.car_send_map, my_set).addToBackStack(null).commit();
                    mCurrentFragment = my_set;
                }
                break;
            case R.id.remind:
                new CallDialog(HomeActivity.this, "0551-65555744").call();
                break;
            case R.id.txt_place:
                new RemainBaseDialog(HomeActivity.this, "目前仅支持合肥地区").call();
                break;
        }
    }

    //主线程处理视图，isExit默认为false，就是点击第一次时，弹出"再按一次退出程序"
    //点击第二次时关闭应用
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 点击两次退出程序
     */
    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            //参数用作状态码；根据惯例，非 0 的状态码表示异常终止。
            System.exit(0);
        }
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.jgkj.bxxc.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!JPushDataUitl.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                setCostomMsg(showMsg.toString());
            }
        }
    }

    private void setCostomMsg(String msg) {
        if (null != msgText) {
            msgText.setText(msg);
            msgText.setVisibility(android.view.View.VISIBLE);
        }
    }

}
