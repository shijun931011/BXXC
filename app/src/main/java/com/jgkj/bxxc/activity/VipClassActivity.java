package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.MyCoachAdapter;
import com.jgkj.bxxc.bean.CoachDetailAction;
import com.jgkj.bxxc.bean.SchoolPlaceTotal;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.tools.RefreshLayout;
import com.jgkj.bxxc.tools.SelectPopupWindow;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class VipClassActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener,SwipeRefreshLayout.OnRefreshListener,
        RefreshLayout.OnLoadListener{

    private TextView title;
    private Button button_backward;
    private Button sort_btn1, sort_btn2, sort_btn3;      //全城   科目   综合
    private ListView listView;                           //教练排序展示
    private TextView textView;                           //暂无数据
    private RefreshLayout swipeLayout;                   //上拉刷新
    private SharedPreferences sp,sp1;                    //读取本地信息
    private UserInfo userInfo;
    private UserInfo.Result result;
    private int uid;
    private String token;
    private SelectPopupWindow mPopupWindowSub = null;
    private SelectPopupWindow mPopupWindowCampus = null;
    private SelectPopupWindow mPopupWindowSort = null;
    private String tag;
    //获取场地
    private String placePath = "http://www.baixinxueche.com/index.php/Home/Apiupdata/Apiarea";
    //新版本教练中心
    private String coachShowUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/Apiarea";
    //新版本排序
    private String sortPath = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/chooseinfo";
    private SchoolPlaceTotal schoolPlaceTotal;          //校区地址详细信息
    private String[] city;
    private String[][] datialPlace;
    private int page = 1;
    private List<CoachDetailAction.Result> coachList = new ArrayList<>();
    private MyCoachAdapter adapter;
    private String[] sub = {"科目", "科目二", "科目三"};
    private String[] sortStr = {"综合排序", "通过率", "好评率"};
    private String class_type = "";
    private String sortString = "zonghe";
    private String class_class = "VIP班";
    private int schId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_class);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        initView();
        getPlace();
        getBundle();
    }

    private void initView(){
        title = (TextView)  findViewById(R.id.text_title);
        title.setText("VIP班");
        button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setVisibility(View.VISIBLE);
        button_backward.setOnClickListener(this);
        sort_btn1 = (Button) findViewById(R.id.coach_sort_btn1);
        sort_btn2 = (Button) findViewById(R.id.coach_sort_btn2);
        sort_btn3 = (Button) findViewById(R.id.coach_sort_btn3);
        listView = (ListView) findViewById(R.id.widget_layout_item);
        textView = (TextView) findViewById(R.id.textView);
        sort_btn1.setOnClickListener(this);
        sort_btn2.setOnClickListener(this);
        sort_btn3.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setFocusable(false);
        //上拉刷新
        swipeLayout = (RefreshLayout) findViewById(R.id.swipeCoach);
        swipeLayout.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);
        swipeLayout.setTag("UNENABLE");

        //验证是否登录
        sp = getApplication().getSharedPreferences("USER",
                Activity.MODE_PRIVATE);
        int isFirstRun = sp.getInt("isfirst", 0);
        if (isFirstRun != 0){
            String str = sp.getString("userInfo", null);
            Gson gson = new Gson();
            userInfo = gson.fromJson(str, UserInfo.class);
            result = userInfo.getResult();
            uid = result.getUid();
        }
        sp1 = getSharedPreferences("token",
                Activity.MODE_PRIVATE);
        token = sp1.getString("token", null);
    }

    private void getPlace(){
        OkHttpUtils
                .get()
                .url(placePath)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(VipClassActivity.this, "网络状态不佳，请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("shijun","bxxc" +s);
                        listView.setTag(s);
                        if (listView.getTag() != null) {
                            addAdapter();
                        }
                    }
                });
    }

    private void getBundle(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String string = bundle.getString("SEARCH");
            Gson gson = new Gson();
            CoachDetailAction coachDetailAction = gson.fromJson(string, CoachDetailAction.class);
            adapter = new MyCoachAdapter(this, coachDetailAction.getResult());
            listView.setAdapter(adapter);
        } else {
            check();
            sort(class_type, schId + "", sortString, page + "", class_class);

        }
    }

    /**
     * 解析服务器返回的json数据
     */
    private void addAdapter() {
        Gson gson = new Gson();
        String str = listView.getTag().toString();
        schoolPlaceTotal = gson.fromJson(str, SchoolPlaceTotal.class);
        setPup(schoolPlaceTotal.getResult());
    }
    /**
     * 设置pupweindow，下拉框值
     */
    private void setPup(List<SchoolPlaceTotal.Result> results) {
        city = new String[results.size()];
        datialPlace = new String[results.size()][];
        for (int i = 0; i < results.size(); i++) {
            city[i] = results.get(i).getSchool_aera();
            if (results.get(i) != null) {
                List<SchoolPlaceTotal.Result.Res> listSch = results.get(i).getResult();
                datialPlace[i] = new String[listSch.size()];
                for (int j = 0; j < listSch.size(); j++) {
                    datialPlace[i][j] = listSch.get(j).getSname();
                }
            }
        }
    }

    //条件查询
    private void sort(String class_type, String school, String sort, String page, String class_class) {
        OkHttpUtils
                .post()
                .url(sortPath)
                .addParams("school_id", school)
                .addParams("class_type", class_type)
                .addParams("sort", sort)
                .addParams("page", page)
                .addParams("class_class", class_class)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(VipClassActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        listView.setTag(s);
                        if (listView.getTag() != null) {
                            setAdapter();
                        }
                    }
                });
    }
    /**
     * 设置adapter
     * 区分是刷新还是加载
     */
    private void setAdapter() {
        String swiTag="";
        String tag = "";
        try {
            swiTag = swipeLayout.getTag().toString();
            tag = listView.getTag().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        swipeLayout.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        CoachDetailAction coachDetailAction = gson.fromJson(tag, CoachDetailAction.class);
        switch (swiTag) {
            case "UNENABLE":
                coachList.clear();
                setMyAdapter();
                break;
            case "REFRESH":
                coachList.clear();
                setMyAdapter();
                break;
            case "ONLOAD":
                if (coachDetailAction.getCode() == 200) {
                    coachList.addAll(coachDetailAction.getResult());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(VipClassActivity.this, coachDetailAction.getReason(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 设置adapter
     */
    private void setMyAdapter() {
        String tag = listView.getTag().toString();
        Gson gson = new Gson();
        CoachDetailAction coachDetailAction = gson.fromJson(tag, CoachDetailAction.class);
        if (coachDetailAction.getCode() == 200) {
            coachList.addAll(coachDetailAction.getResult());
            adapter = new MyCoachAdapter(this, coachList);
            listView.setAdapter(adapter);
        } else {
            swipeLayout.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            Toast.makeText(this, coachDetailAction.getReason(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 选择完成回调接口
     */
    private SelectPopupWindow.SelectCategory selectCategory = new SelectPopupWindow.SelectCategory() {
        @Override
        public void selectCategory(Integer parentSelectposition, Integer childrenSelectposition) {
            if (tag.equals("sort_btn1")){
                if(childrenSelectposition == null){
                    sort_btn1.setText("全城");
                    schId = 0;
                }else{
                    schId = schoolPlaceTotal.getResult().get(parentSelectposition).getResult().get(childrenSelectposition).getId();
                    sort_btn1.setText(datialPlace[parentSelectposition][childrenSelectposition]);
                }
            }else if (tag.equals("sort_btn2")){
                sort_btn2.setText(sub[parentSelectposition]);
            }else if (tag.equals("sort_btn3")){
                sortString = sortStr[parentSelectposition];
                sort_btn3.setText(sortStr[parentSelectposition]);
            }
            check();
            page = 1;
            swipeLayout.setTag("REFRESH");
            sort(class_type, schId + "", sortString, page + "", class_class);
        }
    };

    //ListView的item项的监听
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView coachId = (TextView) view.findViewById(R.id.coachId);
        Intent intent = new Intent();
        intent.setClass(this, ReservationActivity.class);
        intent.putExtra("coachId", coachId.getText().toString().trim());
        intent.putExtra("uid", uid);
        intent.putExtra("token", token);
        startActivity(intent);
    }

    //上拉加载
    @Override
    public void onLoad() {
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                page++;
                swipeLayout.setTag("ONLOAD");
                check();
                sort(class_type, schId + "", sortString, page + "", class_class);
                swipeLayout.setLoading(false);
                adapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        listView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                page = 1;
                swipeLayout.setTag("ONFRESH");
                sort_btn1.setText("全城");
                sort_btn2.setText("科目");
                sort_btn3.setText("综合排序");
                check();
                sort(class_type, schId + "", sortString, page + "", class_class);
                swipeLayout.setRefreshing(false);
            }
        }, 2000);
    }

    private void check() {
        if (!sort_btn2.getText().toString().trim().equals("科目")) {
            class_type = sort_btn2.getText().toString().trim()+"教练";
        }
        if (sort_btn3.getText().toString().trim().equals("综合排序")) {
            sortString = "zonghe";
        } else {
            String string = sort_btn3.getText().toString().trim();
            if (string.equals("综合排序")) {
                sortString = "zonghe";
            }else if (string.equals("好评率")) {
                sortString = "praise";
            }else if (string.equals("通过率")) {
                sortString = "pass";
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_backward:
                finish();
                break;
            case R.id.coach_sort_btn2:           //科目
                tag = "sort_btn2";
                if (mPopupWindowSub == null) {
                    mPopupWindowSub = new SelectPopupWindow(sub, null,this,
                            selectCategory);
                }
                mPopupWindowSub.showAsDropDown(sort_btn1, -5, 1);
                break;
            case R.id.coach_sort_btn1:          //全城
                tag = "sort_btn1";
                if (datialPlace == null) {
                    Toast.makeText(this, "网络状态不佳，请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (mPopupWindowCampus == null) {
                        mPopupWindowCampus = new SelectPopupWindow(city, datialPlace, this, selectCategory);
                    }
                    mPopupWindowCampus.showAsDropDown(sort_btn2, -5, 1);
                }
                break;
            case R.id.coach_sort_btn3:            //综合
                tag = "sort_btn3";
                if (mPopupWindowSort == null) {
                    mPopupWindowSort = new SelectPopupWindow(sortStr, null, this,
                            selectCategory);
                }
                mPopupWindowSort.showAsDropDown(sort_btn3, -5, 1);
                break;
        }
    }
}
