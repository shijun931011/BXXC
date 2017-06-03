package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
    private String tag;
    private SelectPopupWindow mPopupWindowSub = null;
    private SelectPopupWindow mPopupWindowCampus = null;
    private SelectPopupWindow mPopupWindowSort = null;
    private SelectPopupWindow mPopupWindowClassType = null;
    private String[] sub = {"科目","科目二", "科目三"};
    private String[] sortStr = {"综合排序","通过率", "好评率"};
    private String[] classType = {"班型","经理班", "VIP班"};
    private String class_type = "";
    private String sortString = "";
    private String class_class = "";
    //新版本排序
    private String sortPath = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/chooseinfo";
    private List<CoachDetailAction.Result> tagList;
    private List<CoachDetailAction.Result> coachList = new ArrayList<>();
    private CoachDetailAction coachDetailAction;
    private MyCoachAdapter adapter;
    private TextView title;
    private Button sort_btn1, sort_btn3, sort_btn4;
    private Button back_forward;
    private TextView textView;
    private String schoolPath = "http://www.baixinxueche.com/index.php/Home/Apitoken/Coaches";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_coach);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        initView();
        sendRequest(page+"",schId);
        //sort(class_type, sortString, page+"", class_class);
    }

    /**
     * 根据页数和校区id查找教练
     * @param schId
     * @param page
     */
    private void sendRequest(String page, String schId) {
        OkHttpUtils
                .post()
                .url(schoolPath)
                .addParams("page", page)
                .addParams("school_id", schId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(SchoolCoachActivity.this, "网络状态不佳,请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        listView.setTag(s);
                        if(listView.getTag()!=null){
                            swipeLayout.setTag("REFRESH");
                            setAdapter();
                        }else{
                            Toast.makeText(SchoolCoachActivity.this, "网络状态不佳,请检查网络", Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SchoolCoachActivity.this, coachDetailAction.getReason(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    /**
     * 设置 adapter
     */
    private void setMyAdapter(){
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

    private void initView() {
        //排序按钮实例化并添加点击监听事件
        sort_btn1 = (Button) findViewById(R.id.coach_sort_btn1);
        sort_btn3 = (Button) findViewById(R.id.coach_sort_btn3);
        sort_btn4 = (Button) findViewById(R.id.coach_sort_btn4);
        sort_btn1.setOnClickListener(this);
        sort_btn3.setOnClickListener(this);
        sort_btn4.setOnClickListener(this);

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
                sort_btn1.setText("科目");
                sort_btn3.setText("综合排序");
                sort_btn4.setText("班型");
                check();
                sort(class_type, sortString, page+"", class_class);
                //sendRequest(page+"",schId);
                swipeLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
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
                sort(class_type, sortString, page+"", class_class);
                swipeLayout.setLoading(false);
                adapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView coachId = (TextView) view.findViewById(R.id.coachId);
        Intent intent = new Intent();
        intent.setClass(SchoolCoachActivity.this, ReservationActivity.class);
        intent.putExtra("coachId", coachId.getText().toString().trim());
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.coach_sort_btn1:
                tag = "sort_btn1";
                coachList.clear();
                if (mPopupWindowSub == null) {
                    mPopupWindowSub = new SelectPopupWindow(sub, null, SchoolCoachActivity.this,
                            selectCategory);
                }
                mPopupWindowSub.showAsDropDown(sort_btn1, -5, 1);
                break;
            case R.id.coach_sort_btn3:
                coachList.clear();
                tag = "sort_btn3";
                if (mPopupWindowSort == null) {
                    mPopupWindowSort = new SelectPopupWindow(sortStr, null, SchoolCoachActivity.this,
                            selectCategory);
                }
                mPopupWindowSort.showAsDropDown(sort_btn3, -5, 1);
                break;
            case R.id.coach_sort_btn4:
                tag = "sort_btn4";
                coachList.clear();
                if (mPopupWindowClassType == null) {
                    mPopupWindowClassType = new SelectPopupWindow(classType, null, SchoolCoachActivity.this,
                            selectCategory);
                }
                mPopupWindowClassType.showAsDropDown(sort_btn4, -5, 1);
                break;
        }
    }
    private void check() {
        if (sort_btn4.getText().toString().trim().equals("班型")) {
            class_class = "";
        } else {
            class_class = sort_btn4.getText().toString().trim();
        }
        if (sort_btn1.getText().toString().trim().equals("科目")) {
            class_type = "";
        }else{
            class_type = sort_btn1.getText().toString().trim() + "教练";
        }
        if (sort_btn3.getText().toString().trim().equals("综合排序")) {
            sortString = "zonghe";
        } else {
            String string = sort_btn3.getText().toString().trim();
            if (string.equals("综合排序")) {
                sortString = "zonghe";
            } else if (string.equals("好评率")) {
                sortString = "praise";
            } else if (string.equals("通过率")) {
                sortString = "pass";
            }
        }
    }
    /**
     * 选择完成回调接口
     */
    private SelectPopupWindow.SelectCategory selectCategory = new SelectPopupWindow.SelectCategory() {
        @Override
        public void selectCategory(Integer parentSelectposition, Integer childrenSelectposition) {
            if (tag.equals("sort_btn1")) {
                sort_btn1.setText(sub[parentSelectposition]);
            } else if (tag.equals("sort_btn3")) {
                sortString = sortStr[parentSelectposition];
                sort_btn3.setText(sortStr[parentSelectposition]);
            } else if (tag.equals("sort_btn4")) {
                class_class = classType[parentSelectposition];
                sort_btn4.setText(classType[parentSelectposition]);
            }
            check();
            page = 1;
            swipeLayout.setTag("REFRESH");
            sort(class_type,sortString, page+"", class_class);
        }
    };

    /**
     * 条件查询
     * @param class_type 班级类型
     * @param sort  排序方法
     * @param page  页数
     * @param class_class 科目几
     */
    private void sort(String class_type, String sort, String page, String class_class) {
        OkHttpUtils
                .post()
                .url(sortPath)
                .addParams("school_id", schId)
                .addParams("class_type", class_type)
                .addParams("sort", sort)
                .addParams("page", page)
                .addParams("class_class", class_class)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(SchoolCoachActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        listView.setTag(s);
                        if(listView.getTag()!=null){
                            setAdapter();
                        }
                    }
                });
    }

}
