package com.jgkj.bxxc.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.SearchCoachActivity;
import com.jgkj.bxxc.bean.UserInfo;

import java.util.ArrayList;

public class CoachFragment extends Fragment implements View.OnClickListener{
    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;
    private Button btn_coach;
    private Button btn_private;
    private Button btn_private_center;
    private ImageView search;
    private Fragment coach;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private SharedPreferences sp,sp1;                    //读取本地信息
    private UserInfo userInfo;
    private UserInfo.Result result;
    private int uid;
    private String token;
    private Dialog dialog;
    private View view;

    //判断点击了哪个按钮（私教/私教中心/私教学校）
    public boolean flag = false;
    public static String strResult;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.activity_private_coach, container, false);
        return view;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        btn_private = (Button) view.findViewById(R.id.btn_private);
        btn_coach = (Button) view.findViewById(R.id.btn_coach);
        btn_private_center = (Button) view.findViewById(R.id.btn_private_center);
        search = (ImageView) view.findViewById(R.id.search);
        search.setOnClickListener(this);
        btn_private.setOnClickListener(this);
        btn_private_center.setOnClickListener(this);
        btn_coach.setOnClickListener(this);
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new PrivateFragment());
        fragmentList.add(new PrivateCenterFragment());
        fragmentList.add(new PrivateSchoolFragment());
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            public int getCount() {
                return fragmentList.size();
            }
            public Fragment getItem(int arg0) {
                return fragmentList.get(arg0);
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageSelected(int arg0) {
                if(0 == arg0){
                    btn_private.setTextColor(Color.parseColor("#37363C"));
                    btn_private_center.setTextColor(Color.WHITE);
                    btn_coach.setTextColor(Color.WHITE);
                    btn_private.setBackgroundResource(R.drawable.baike_btn_pink_left_f_96);
                    btn_private_center.setBackgroundResource(R.drawable.sijiao_black_center_96);
                    btn_coach.setBackgroundResource(R.drawable.baike_btn_trans_right_f_96);
                }
                if (1 == arg0){
                    btn_private.setTextColor(Color.WHITE);
                    btn_private_center.setTextColor(Color.parseColor("#37363C"));
                    btn_coach.setTextColor(Color.WHITE);
                    btn_private.setBackgroundResource(R.drawable.baike_btn_trans_left_f_96);
                    btn_private_center.setBackgroundResource(R.drawable.sijiao_white_center_96);
                    btn_coach.setBackgroundResource(R.drawable.baike_btn_trans_right_f_96);
                }
                if(2 == arg0) {
                    btn_private.setTextColor(Color.WHITE);
                    btn_private_center.setTextColor(Color.WHITE);
                    btn_coach.setTextColor(Color.parseColor("#37363C"));
                    btn_private.setBackgroundResource(R.drawable.baike_btn_trans_left_f_96);
                    btn_private_center.setBackgroundResource(R.drawable.sijiao_black_center_96);
                    btn_coach.setBackgroundResource(R.drawable.baike_btn_pink_right_f_96);

                }
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            public void onPageScrollStateChanged(int arg0) {

            }
        });

        //验证是否登录
        sp = getActivity().getApplication().getSharedPreferences("USER",
                Activity.MODE_PRIVATE);
        int isFirstRun = sp.getInt("isfirst", 0);
        if (isFirstRun != 0){
            String str = sp.getString("userInfo", null);
            Gson gson = new Gson();
            userInfo = gson.fromJson(str, UserInfo.class);
            result = userInfo.getResult();
            uid = result.getUid();
        }
        sp1 = getActivity().getSharedPreferences("token",
                Activity.MODE_PRIVATE);
        token = sp1.getString("token", null);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_private:
                viewPager.setCurrentItem(0, true);
                flag = false;
                break;
            case R.id.btn_private_center:
                viewPager.setCurrentItem(1,true);
                flag = true;
                break;
            case R.id.btn_coach:
                viewPager.setCurrentItem(2, true);
                flag = true;
                break;
            case R.id.search:
                Intent intent = new Intent();
                intent.setClass(getActivity(), SearchCoachActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("token", token);
                startActivity(intent);
                break;

        }
    }

}
