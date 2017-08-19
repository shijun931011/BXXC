package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.LearnHisAdapter;
import com.jgkj.bxxc.bean.LearnHisAction;
import com.jgkj.bxxc.tools.RefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2016/12/29.
 * 学车记录及评价
 */

public class LearnHisActivity extends Activity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {
    private ListView listView;
    private LearnHisAdapter adapter;
    private RefreshLayout swipehis;
    private TextView title;
    private String token;
    private Button back_btn,btn_uncomplete;  //返回    未完成记录
    private List<LearnHisAction.Result> list = new ArrayList();
//    private String learnUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenupdata/commentShowApplyTesttype";
    private String learnUrl = "http://www.baixinxueche.com/index.php/Home/Hotsearch/commentShowApplyTest";

//    private String applyTest = "http://www.baixinxueche.com/index.php/Home/Apitokenupdata/applySubjectTestAgain";
    private int page = 1;
    private String state;
    //flag代表是否是已学车还是未学车
    private boolean flag = true;
    private int uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learnhis);
        initView();
        getHis(uid+"",page+"");
//        isShowBtn();
    }

    //初始化控件
    private void initView() {
        listView = (ListView) findViewById(R.id.listView);
        swipehis = (RefreshLayout) findViewById(R.id.swipehis);
        title = (TextView) findViewById(R.id.text_title);
        title.setText("学车记录");
        back_btn = (Button) findViewById(R.id.button_backward);
        back_btn.setVisibility(View.VISIBLE);
        back_btn.setOnClickListener(this);
        btn_uncomplete = (Button) findViewById(R.id.button_forward);
        btn_uncomplete.setOnClickListener(this);
        btn_uncomplete.setVisibility(View.VISIBLE);
        btn_uncomplete.setText("未完成记录");
        //上拉刷新
        swipehis.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        swipehis.setOnRefreshListener(this);
        swipehis.setTag("refresh");
        swipehis.setOnLoadListener(this);
//        tips = (TextView) findViewById(R.id.tips);
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid",uid);
        state = intent.getStringExtra("state");
        token = intent.getStringExtra("token");
    }

    /**
     * 获取学车已学记录
     * @param page 分页
    */
    private void getHis(String uid,String page) {
        Log.d("百信学车","学车记录传值2::::"+uid+"page="+page);
        OkHttpUtils
                .post()
                .url(learnUrl)
                .addParams("uid", uid+"")
                .addParams("page", page)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(LearnHisActivity.this, "发送失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("百信学车","学车记录结果" + s);
                        listView.setTag(s);
                        if (listView.getTag() != null) {
                            setAdapter();
                        }
                    }
                });
    }

    /**
     * 给控件填充参数
     * 已学课时
     */
    private void setAdapter() {
        String str = listView.getTag().toString();
        Gson gson = new Gson();
        LearnHisAction action = gson.fromJson(str, LearnHisAction.class);
        switch (swipehis.getTag().toString()) {
            case "refresh":
                if (action.getCode() == 200) {
                    list.clear();
                    list.addAll(action.getResult());
                    adapter = new LearnHisAdapter(LearnHisActivity.this, list,token,uid);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(LearnHisActivity.this, action.getReason(), Toast.LENGTH_SHORT).show();
                }
                break;
            case "onload":
                if (action.getCode() == 200) {
                    list.addAll(action.getResult());
                    adapter.notifyDataSetChanged();
                } else {
                    page--;
                    Toast.makeText(LearnHisActivity.this, action.getReason(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
              break;
            case R.id.button_forward:
                intent.setClass(this, UncompleteReordActivity.class);
                intent.putExtra("uid",uid);
                intent.putExtra("token",token);
                startActivity(intent);

        }
    }

    @Override
    protected void onResume() {
        page = 1;
        list.clear();
        swipehis.setTag("refresh");
        getHis(uid+"",page + "");
        swipehis.setRefreshing(false);
        super.onResume();
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        swipehis.postDelayed(new Runnable() {
            @Override
            public void run() {
                page = 1;
                list.clear();
                swipehis.setTag("refresh");
                getHis(uid+"",page + "");
                swipehis.setRefreshing(false);
            }
        }, 2000);
    }

    //上拉加载
    @Override
    public void onLoad() {
        swipehis.postDelayed(new Runnable() {
            @Override
            public void run() {
                page++;
                swipehis.setTag("onload");
                getHis(uid+"",page + "");
                swipehis.setLoading(false);
            }
        }, 2000);
    }

}
