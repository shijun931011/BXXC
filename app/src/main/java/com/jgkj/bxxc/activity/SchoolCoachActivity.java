package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
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
import com.jgkj.bxxc.adapter.PrivateCenterAdapter;
import com.jgkj.bxxc.adapter.PrivateCoachAdapter;
import com.jgkj.bxxc.bean.CoachDetailAction;
import com.jgkj.bxxc.tools.RefreshLayout;
import com.jgkj.bxxc.tools.SelectPopupWindow;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2016/12/21.
 * 根据校区id获取校区场地的教练，并展示到listView中
 */

public class SchoolCoachActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener,
        RefreshLayout.OnLoadListener, AdapterView.OnItemClickListener,View.OnClickListener {
    private ListView listView;
    private RefreshLayout swipeLayout;
    private int page = 1;
    private String schId;
    private String schName;
    private String roles;
    private String tag;
    private SelectPopupWindow mPopupWindowSub = null;
    private SelectPopupWindow mPopupWindowCampus = null;
    private SelectPopupWindow mPopupWindowSort = null;
    private SelectPopupWindow mPopupWindowClassType = null;
//    private String[] sub = {"科目","科目二", "科目三"};
    private String[] sortStr = {"综合排序", "好评率", "累计所带学员数"};
    private String[] classType = {"私教","私教中心","私教学校"};
    private String class_type = "私教";
    private String sortString = "";
    private String class_class = "";
    //新版本排序
//    private String sortPath = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/chooseinfo";
    private String sortPath="http://www.baixinxueche.com/index.php/Home/Screen/screen";
    private List<CoachDetailAction.Result> tagList;
    private List<CoachDetailAction.Result> coachList = new ArrayList<>();
    private CoachDetailAction coachDetailAction;
    private PrivateCoachAdapter privateCoachAdapter;
    private PrivateCenterAdapter privateCenterAdapter;
    private TextView title;
    private Button sort_btn1, sort_btn2;
    private Button back_forward;
    private TextView textView;
    private int uid;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_coach);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        initView();
        coachList.clear();
        swipeLayout.setTag("REFRESH");
        page = 1;
        sort_btn1.setText("私教");
        sort_btn2.setText("综合排序");
        check();
        sort(schId,class_type,sortString,page+"" );
        Log.d("百信学车","私教类型参数值1："+schId+"class_type="+class_type+"sortString="+sortString+"page="+page);
    }

    /**
     * 设置 adapter
     */
    private void setMyAdapter(){
        String tag = listView.getTag().toString();
        Gson gson = new Gson();
        CoachDetailAction coachDetailAction = gson.fromJson(tag, CoachDetailAction.class);
        if (coachDetailAction.getCode() == 200) {
            if (sort_btn1.getText().toString().trim().equals("私教")){
                coachList.addAll(coachDetailAction.getResult());
                privateCoachAdapter = new PrivateCoachAdapter(this, coachList);
                listView.setAdapter(privateCoachAdapter);
            } else if (sort_btn1.getText().toString().trim().equals("私教中心")){
                coachList.addAll(coachDetailAction.getResult());
                privateCenterAdapter = new PrivateCenterAdapter(this, coachList);
                listView.setAdapter(privateCenterAdapter);
            }else if (sort_btn1.getText().toString().trim().equals("私教学校")){
                swipeLayout.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        } else {
            swipeLayout.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            Toast.makeText(this, coachDetailAction.getReason(), Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 设置adapter
     */
    private void setAdapter(){
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
                if(page == 1){
                    coachList.clear();
                }
                if (coachDetailAction.getCode() == 200) {
                    coachList.addAll(coachDetailAction.getResult());
                    if (sort_btn1.getText().toString().trim().equals("私教")){
                        privateCoachAdapter.notifyDataSetChanged();
                    }else if (sort_btn1.getText().toString().trim().equals("私教中心")){
                        privateCenterAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(SchoolCoachActivity.this, coachDetailAction.getReason(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void initView() {
        //排序按钮实例化并添加点击监听事件
        sort_btn1 = (Button) findViewById(R.id.coach_sort_btn1);
        sort_btn2 = (Button) findViewById(R.id.coach_sort_btn2);
        sort_btn1.setOnClickListener(this);
        sort_btn1.setOnClickListener(this);
        sort_btn2.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.textView);
        title = (TextView) findViewById(R.id.text_title);
        title.setText("中心校区");
        back_forward = (Button) findViewById(R.id.button_backward);
        back_forward.setVisibility(View.VISIBLE);
        back_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView = (ListView) findViewById(R.id.widget_layout_item);
        listView.setOnItemClickListener(this);
        listView.setFocusable(false);
        //上拉刷新
        swipeLayout = (RefreshLayout) findViewById(R.id.swipeCoach);
        swipeLayout.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);
        Intent intent = getIntent();
        schId = intent.getStringExtra("schId");
        schName = intent.getStringExtra("schName");
        uid = intent.getIntExtra("uid",uid);
        token = intent.getStringExtra("token");
        title.setText(schName);
    }

    @Override
    public void onRefresh() {
        listView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 更新数据  更新完后调用该方法结束刷新
                coachList.clear();
                swipeLayout.setTag("REFRESH");
                page = 1;
//                sort_btn1.setText("私教");
//                sort_btn2.setText("综合排序");
                check();
                sort(schId,class_type,sortString,page+"");
                Log.d("百信学车","私教类型参数值2："+schId+"class_type="+class_type+"sortString="+sortString+"page="+page);
                swipeLayout.setRefreshing(false);
            }
        }, 2000);
    }
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
                sort(schId,class_type,sortString,page+"");
                swipeLayout.setLoading(false);
            }
        }, 2000);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView coachId = (TextView) view.findViewById(R.id.coachId);
        TextView tid = (TextView) view.findViewById(R.id.coachId);
        TextView pri_center_name = (TextView) view.findViewById(R.id.coachName);
        Intent intent = new Intent();
        if (sort_btn1.getText().toString().trim().equals("私教")){
            intent.setClass(this, ReservationForPrivateActivity.class);
            intent.putExtra("coachId", coachId.getText().toString().trim());
            intent.putExtra("uid", uid);
            intent.putExtra("token", token);
        }else if(sort_btn1.getText().toString().trim().equals("私教中心")){
            intent.setClass(this, PrivateCenterDetailsActivity.class);
            intent.putExtra("tid", tid.getText().toString().trim());
            intent.putExtra("uid", uid);
            intent.putExtra("token", token);
            intent.putExtra("name",pri_center_name.getText().toString().trim());
        }
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.coach_sort_btn1:
                tag = "sort_btn1";
                coachList.clear();
                if (mPopupWindowClassType == null) {
                    mPopupWindowClassType = new SelectPopupWindow(classType, null, SchoolCoachActivity.this, selectCategory);
                }
                mPopupWindowClassType.showAsDropDown(sort_btn1, -5, 1);
                break;
            case R.id.coach_sort_btn2:
                coachList.clear();
                tag = "sort_btn2";
                if (mPopupWindowSort == null) {
                    mPopupWindowSort = new SelectPopupWindow(sortStr, null, SchoolCoachActivity.this,
                            selectCategory);
                }
                mPopupWindowSort.showAsDropDown(sort_btn2, -5, 1);
                break;

        }
    }
    private void check() {
        if (sort_btn1.getText().toString().trim().equals("私教")) {
            class_type = "1";
        } else if (sort_btn1.getText().toString().trim().equals("私教中心")){
            class_type = "2";
        } else if (sort_btn1.getText().toString().trim().equals("私教学校")){
            class_type = "3";
        }
        if (sort_btn2.getText().toString().trim().equals("综合排序")) {
            sortString = "zonghe";
        } else {
            String string = sort_btn2.getText().toString().trim();
            if (string.equals("综合排序")) {
                sortString = "zonghe";
            } else if (string.equals("好评率")) {
                sortString = "praise";
            } else if (string.equals("累计所带学员数")) {
                sortString = "leiji";
            }
        }
    }
    /**
     * 选择完成回调接口
     */
    private SelectPopupWindow.SelectCategory selectCategory = new SelectPopupWindow.SelectCategory() {
        @Override
        public void selectCategory(Integer parentSelectposition, Integer childrenSelectposition) {
             if (tag.equals("sort_btn2")) {
                sortString = sortStr[parentSelectposition];
                sort_btn2.setText(sortStr[parentSelectposition]);
            } else if (tag.equals("sort_btn1")) {
                class_class = classType[parentSelectposition];
                sort_btn1.setText(classType[parentSelectposition]);
            }
            check();
            page = 1;
            swipeLayout.setTag("REFRESH");
            sort(schId,class_type,sortString,page+"");
            Log.d("百信学车","私教类型参数值4："+schId+"class_type="+class_type+"sortString="+sortString+"page="+page);
        }
    };

    /**
     *
     * @param schId
     * @param class_type
     * @param sort
     * @param page
     */
    private void sort(String schId,String class_type,String sort,String page) {
        OkHttpUtils
                .post()
                .url(sortPath)
                .addParams("school_id", schId)
                .addParams("roles", class_type)
                .addParams("sort", sort)
                .addParams("page", page)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(SchoolCoachActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("百信学车：","场地教练查询"+s);
                        listView.setTag(s);
                        if(listView.getTag()!=null){
                            setAdapter();
                        }
                    }
                });
    }
}
