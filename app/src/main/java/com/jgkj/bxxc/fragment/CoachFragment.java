package com.jgkj.bxxc.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jgkj.bxxc.R;

import java.util.ArrayList;

public class CoachFragment extends Fragment implements View.OnClickListener{
    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;
    private Button btn_coach;
    private Button btn_private;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_private_coach, container, false);
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);

        btn_private = (Button) getActivity().findViewById(R.id.btn_private);
        btn_coach = (Button) getActivity().findViewById(R.id.btn_coach);

        btn_private.setOnClickListener(this);
        btn_coach.setOnClickListener(this);

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new PrivateFragment());
        fragmentList.add(new CoachFragment2());

        viewPager.setOffscreenPageLimit(2);

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
                    btn_coach.setTextColor(Color.WHITE);
                    btn_private.setBackgroundResource(R.drawable.baike_btn_pink_left_f_96);
                    btn_coach.setBackgroundResource(R.drawable.baike_btn_trans_right_f_96);
                }
                if(1 == arg0) {
                    btn_private.setTextColor(Color.WHITE);
                    btn_coach.setTextColor(Color.parseColor("#37363C"));
                    btn_private.setBackgroundResource(R.drawable.baike_btn_trans_left_f_96);
                    btn_coach.setBackgroundResource(R.drawable.baike_btn_pink_right_f_96);

                }
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    public void onClick(View arg0) {

        switch (arg0.getId()) {
            case R.id.btn_private:
                viewPager.setCurrentItem(0, true);
                break;
            case R.id.btn_coach:
                viewPager.setCurrentItem(1, true);
                break;
            default:
                break;
        }
    }

}
