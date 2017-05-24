package com.jgkj.bxxc.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.CoachDetailAction;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

public class CoachFragment extends Fragment implements View.OnClickListener{
    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;
    private Button btn_coach;
    private Button btn_private;
    private ImageView search;
    private Fragment coach;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private EditText mEtSearch = null;// 输入搜索内容
    private Button mBtnClearSearchText = null;// 清空搜索信息的按钮
    private LinearLayout mLayoutClearSearchText = null;
    private Dialog dialog;
    private View view;
    private String searchUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/like";

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
        search = (ImageView) view.findViewById(R.id.search);
        Log.d("shijun","搜索ID:"+search);
        search.setOnClickListener(this);
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
    public void creatDialog() {
        dialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.search_dialog,null);
        // 初始化控件
        mEtSearch = (EditText) inflate.findViewById(R.id.et_search);
        mBtnClearSearchText = (Button) inflate.findViewById(R.id.btn_clear_search_text);
        mLayoutClearSearchText = (LinearLayout) inflate.findViewById(R.id.layout_clear_search_text);
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int textLength = mEtSearch.getText().length();
                if (textLength > 0) {
                    mLayoutClearSearchText.setVisibility(View.VISIBLE);
                } else {
                    mLayoutClearSearchText.setVisibility(View.GONE);
                }
            }
        });

        mBtnClearSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtSearch.setText("");
                mLayoutClearSearchText.setVisibility(View.GONE);
            }
        });
        mEtSearch.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    search(mEtSearch.getText().toString().trim(), "1");
                }
                return false;
            }
        });
        // 将布局设置给Dialog
        dialog.setContentView(inflate);
        // 获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        // 设置dialog横向充满
        dialogWindow.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.TOP);
        dialog.show();/// 显示对话框
    }


    /**
     * 教练中心页面模糊查找
     *
     * @param str        编辑框输出的文字
     * @param searchPage 页数
     */
    private void search(String str, String searchPage) {
        OkHttpUtils
                .post()
                .url(searchUrl)
                .addParams("input", str)
                .addParams("page", searchPage)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "网络异常，请检查网络！", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        dialog.dismiss();
                        Gson gson = new Gson();
                        CoachDetailAction coachDetailAction = gson.fromJson(s, CoachDetailAction.class);
                        if (coachDetailAction.getCode() == 200) {
                            coach = new CoachFragment();
                            fragmentManager= getFragmentManager();
                            Bundle bundle = new Bundle();
                            bundle.putString("SEARCH", s);
                            coach.setArguments(bundle);
                            transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.car_send_map, coach).commit();
                        } else {
                            Toast.makeText(getActivity(), coachDetailAction.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_private:
                viewPager.setCurrentItem(0, true);
                break;
            case R.id.btn_coach:
                viewPager.setCurrentItem(1, true);
                break;
            case R.id.search:
                creatDialog();
                break;

        }
    }

}
