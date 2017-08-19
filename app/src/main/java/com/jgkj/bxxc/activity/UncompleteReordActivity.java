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
import com.jgkj.bxxc.adapter.UnCompleteRecordAdapter;
import com.jgkj.bxxc.bean.LearnHisAction;
import com.jgkj.bxxc.tools.RefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class UncompleteReordActivity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {
    private ListView listView;
    private RefreshLayout swipehis;
    private TextView title;
    private Button back_btn;
    private int page = 1;
    private int uid;
    private String token;
    private UnCompleteRecordAdapter adapter;
    private List<LearnHisAction.Result> list = new ArrayList();

    private String notComeUrl = "http://www.baixinxueche.com/index.php/Home/Hotsearch/commentShowNotsure";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uncomplete_reord);
        InitView();
    }

    private void InitView(){
        listView = (ListView) findViewById(R.id.listView);
        title = (TextView) findViewById(R.id.text_title);
        title.setText("未完成记录(约车未到)");
        back_btn = (Button) findViewById(R.id.button_backward);
        back_btn.setVisibility(View.VISIBLE);
        back_btn.setOnClickListener(this);
        //上拉刷新
        swipehis = (RefreshLayout) findViewById(R.id.swipehis);
        swipehis.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        swipehis.setOnRefreshListener(this);
        swipehis.setTag("refresh");
        swipehis.setOnLoadListener(this);

        Intent intent = getIntent();
        uid = intent.getIntExtra("uid",uid);
        token = intent.getStringExtra("token");
    }

        /**
     * 获取学车未学记录
     *
     * @param page 分页
     */
    private void getNotHis(int uid,String page) {
        OkHttpUtils
                .post()
                .url(notComeUrl)
                .addParams("uid", uid+"")
                .addParams("page", page)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(UncompleteReordActivity.this, "发送失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("百信学车","未到学车记录结果" + s);
                        listView.setTag(s);
                        if (listView.getTag() != null) {
                            setNotAdapter();
                        }
                    }
                });
    }

    /**
     * 给控件填充参数
     * 未学课时
     */
    private void setNotAdapter() {
        String str = listView.getTag().toString();
        Gson gson = new Gson();
        LearnHisAction action = gson.fromJson(str, LearnHisAction.class);
        switch (swipehis.getTag().toString()) {
            case "refresh":
                if (action.getCode() == 200) {
                    list.clear();
                    list.addAll(action.getResult());
                    adapter = new UnCompleteRecordAdapter(UncompleteReordActivity.this, list,token,uid);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(UncompleteReordActivity.this, action.getReason(), Toast.LENGTH_SHORT).show();
                }
                break;
            case "onload":
                if (action.getCode() == 200) {
                    list.addAll(action.getResult());
                    adapter.notifyDataSetChanged();
                } else {
                    page--;
                    Toast.makeText(UncompleteReordActivity.this, action.getReason(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_backward:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        page = 1;
        list.clear();
        swipehis.setTag("refresh");
        getNotHis(uid,page+"");
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
                getNotHis(uid,page+"");
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
                getNotHis(uid,page+"");
                swipehis.setLoading(false);
            }
        }, 2000);
    }

}
