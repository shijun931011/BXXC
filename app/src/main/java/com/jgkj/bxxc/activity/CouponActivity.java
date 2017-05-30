package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.CouponAdapter;
import com.jgkj.bxxc.bean.Coupon;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.jgkj.bxxc.R.id.listView;

/**
 * 我的支付优惠劵
 */
public class CouponActivity extends Activity implements View.OnClickListener{
    private Button btn_backward;
    private TextView title;
    private ListView list_cou; //优惠劵列表
    private TextView prompt;   //提示文字
    private ImageView img_cry;//哭脸图片
    private RadioButton notused,recordused,expired;//未使用, 使用记录， 已过期
    //实现Tab滑动效果
    private ViewPager mViewPager;
    //当前页卡编号
    private int currIndex = 0;
    public Context context;
    private int uid;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        initView();
        context = this;
        //初始化ViewPager
        InitViewPager();
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid", -1);
        token = intent.getStringExtra("token");
        Log.d("zyzhang", uid + "::::" + token);
        if (uid != -1){
            getCoupon(uid+"", token,0+"");
        }else {
            img_cry.setVisibility(View.VISIBLE);
            prompt.setVisibility(View.VISIBLE);
            list_cou.setVisibility(View.GONE);
            prompt.setText("您还没有优惠卷哦...");
        }
    }

    private void initView(){
        Log.d("zyzhang","initView");
        btn_backward = (Button) findViewById(R.id.button_backward);
        title = (TextView) findViewById(R.id.text_title);
        title.setText("优惠劵");
        btn_backward.setVisibility(View.VISIBLE);
        btn_backward.setOnClickListener(this);
        notused=(RadioButton) findViewById(R.id.no_used);
        recordused=(RadioButton) findViewById(R.id.re_used);
        expired=(RadioButton) findViewById(R.id.expired);
        list_cou = (ListView) findViewById(listView);
        prompt=(TextView) findViewById(R.id.prompt);
        img_cry = (ImageView) findViewById(R.id.kulian);
        notused.setOnClickListener(this);
        recordused.setOnClickListener(this);
        expired.setOnClickListener(this);
    }

    /**
     * 初始化页卡内容区
     */
    private void InitViewPager() {
        Log.d("zyzhang","InitViewPager");
        mViewPager = (ViewPager) findViewById(R.id.vPager);
        //让ViewPager缓存2个页面
        mViewPager.setOffscreenPageLimit(2);
        //设置默认打开第一页
        mViewPager.setCurrentItem(0);
        //将顶部文字恢复默认值
        resetTextViewTextColor();
        notused.setTextColor(getResources().getColor(R.color.redTheme));
    }

    public void getCoupon(final String uid, String token, final String type) {
        Log.d("zyzhang", "getCoupon:222 " + uid + "   token:" + token + "   type" +type);
        OkHttpUtils
                .post()
                .url("http://www.baixinxueche.com/index.php/Home/Apitokenupdata/invite")
                .addParams("uid", uid)
                .addParams("token",token)
                .addParams("type",type)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(CouponActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("zyzhang", "ssss"+s);
                        //TODO
                        Gson gson = new Gson();
                        Coupon coupon = gson.fromJson(s, Coupon.class);
                        List<Coupon.Result> list = new ArrayList<Coupon.Result>();
                        if (coupon.getCode() == 200) {
                            List<Coupon.Result> results = coupon.getResult();
                            list.addAll(results);
                        }
                        if (coupon.getNocode() == 200) {
                            List<Coupon.Result> results = coupon.getNoresult();
                            Log.d("zyzhang", results + "2");
                            list.addAll(results);
                        }
                        if (list.size() == 0){
                            img_cry.setVisibility(View.VISIBLE);
                            prompt.setVisibility(View.VISIBLE);
                            list_cou.setVisibility(View.GONE);
                            prompt.setText("您还没有优惠卷哦...");
                        }else {
                            Intent intent = getIntent();
                            int uId = intent.getIntExtra("uid", -1);
                            CouponAdapter adapter = new CouponAdapter(uId + "", CouponActivity.this, list);
                            list_cou.setAdapter(adapter);
                        }
                    }
                });
    }
    /**
     * 将顶部文字恢复默认值
     */
    private void resetTextViewTextColor(){
        notused.setTextColor(getResources().getColor(R.color.right_bg));
        recordused.setTextColor(getResources().getColor(R.color.right_bg));
        expired.setTextColor(getResources().getColor(R.color.right_bg));
    }
    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.button_backward:
                finish();
                break;
            case R.id.no_used:
                prompt.setVisibility(View.GONE);
                img_cry.setVisibility(View.GONE);
                list_cou.setVisibility(View.VISIBLE);
                notused.setBackgroundResource(R.drawable.bg_selector);
                notused.setTextColor(getResources().getColor(R.color.redTheme));
                recordused.setBackgroundResource(R.color.white);
                recordused.setTextColor(getResources().getColor(R.color.right_bg));
                expired.setBackgroundResource(R.color.white);
                expired.setTextColor(getResources().getColor(R.color.right_bg));
                getCoupon(uid+"", token,0+"");
                break;
            case R.id.re_used:
                list_cou.setAdapter(null);
                prompt.setVisibility(View.GONE);
                img_cry.setVisibility(View.GONE);
                list_cou.setVisibility(View.VISIBLE);
                recordused.setBackgroundResource(R.drawable.bg_selector);
                recordused.setTextColor(getResources().getColor(R.color.redTheme));
                notused.setBackgroundResource(R.color.white);
                notused.setTextColor(getResources().getColor(R.color.right_bg));
                expired.setBackgroundResource(R.color.white);
                expired.setTextColor(getResources().getColor(R.color.right_bg));
                getCoupon(uid+"", token,1+"");
                break;
            case R.id.expired:
                list_cou.setAdapter(null);
                prompt.setVisibility(View.GONE);
                img_cry.setVisibility(View.GONE);
                list_cou.setVisibility(View.VISIBLE);
                expired.setBackgroundResource(R.drawable.bg_selector);
                expired.setTextColor(getResources().getColor(R.color.redTheme));
                recordused.setBackgroundResource(R.color.white);
                recordused.setTextColor(getResources().getColor(R.color.right_bg));
                notused.setBackgroundResource(R.color.white);
                notused.setTextColor(getResources().getColor(R.color.right_bg));
                getCoupon(uid+"", token,2+"");
                break;
        }
    }
}
