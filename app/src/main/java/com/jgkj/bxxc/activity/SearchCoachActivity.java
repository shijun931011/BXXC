package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.PrivateCenterSearchAdapter;
import com.jgkj.bxxc.adapter.PrivateCoachAdapter;
import com.jgkj.bxxc.adapter.SearchOldDataAdapter;
import com.jgkj.bxxc.bean.CoachDetailAction;
import com.jgkj.bxxc.bean.Resou;
import com.jgkj.bxxc.tools.FlowLayout;
import com.jgkj.bxxc.tools.selfSearchGridView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import okhttp3.Call;

public class SearchCoachActivity extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener{
    private EditText mEtSearch = null;// 输入搜索内容
    private Button mBtnClearSearchText = null;// 清空搜索信息的按钮
    private LinearLayout mLayoutClearSearchText = null;
    private ListView  search_list;     //教练搜索
    public boolean flag = false;
    public static String strResult;
    private int uid;
    private String token;
//    private String searchUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/like";
    private String searchUrl="http://www.baixinxueche.com/index.php/Home/Screen/searchEngines";
    private List<CoachDetailAction.Result> listTemp = new ArrayList<>();
    private List<CoachDetailAction.Resultarr> liststu = new ArrayList<>();
    private List<CoachDetailAction.Result> result1;
    private List<CoachDetailAction.Resultarr> result2;
    private PrivateCoachAdapter adapter;
    private PrivateCenterSearchAdapter adapter1;
    private Button btn_back;
    private Button btn_cancel;
    private TextView tv_clearolddata;  //清除历史记录
    private setSearchCallBackListener sCBlistener;   //历史记录内容
    private ArrayList<String> OldDataList =new ArrayList<String>();       //历史记录集合
    private List<Resou.Result> resouList1 = new ArrayList<>();    //热搜集合
    private Resou.Result result;
    private ArrayList<String> resouList2 = new ArrayList<>();
    private SearchOldDataAdapter OldDataAdapter;
    private selfSearchGridView gridviewolddata;//  历史搜索
    private View.OnClickListener TextViewItemListener;
    private int countOldDataSize=15;//默认搜索记录的条数， 正确的是传入进来的条数
    private LinearLayout resou;
    //热门搜索
    private FlowLayout hotflowLayout;
    /**
     * 热搜
     * 1 私教个人  2表示私教中心或学校
     */
    private  String  ResouUrl = "http://www.baixinxueche.com/index.php/Home/Hotsearch/hotSearch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_coach);
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid",uid);
        token = intent.getStringExtra("token");
        Resou();
        initView();
        initData();
    }

    private void initView(){
        // 初始化控件
        mEtSearch = (EditText) findViewById(R.id.et_search);
        mBtnClearSearchText = (Button) findViewById(R.id.btn_clear_search_text);
        mLayoutClearSearchText = (LinearLayout) findViewById(R.id.layout_clear_search_text);
        search_list = (ListView) findViewById(R.id.widget_layout_item);
        btn_back=(Button) findViewById(R.id.button_backward);
        tv_clearolddata = (TextView) findViewById(R.id.tv_clearolddata);
        gridviewolddata = (selfSearchGridView) findViewById(R.id.gridviewolddata);
        hotflowLayout = (FlowLayout) findViewById(R.id.id_flowlayouthot);
        resou = (LinearLayout) findViewById(R.id.resou);
        gridviewolddata.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //历史记录点击搜索
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(sCBlistener!=null){
//                    sCBlistener.Search(OldDataList.get(position).trim());
                }
            }
        });
        gridviewolddata.setSelector(new ColorDrawable(Color.TRANSPARENT));//去除背景点击效果
        tv_clearolddata.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_cancel=(Button) findViewById(R.id.cancel);
        btn_cancel.setOnClickListener(this);
        search_list.setOnItemClickListener(this);
        search_list.setFocusable(false);
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
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
    }

    /**
     * 热搜
     */
    private void Resou(){
        OkHttpUtils
                .post()
                .url(ResouUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(SearchCoachActivity.this, "网络异常，请检查网络！", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, final int i) {
                        Log.d("BXXC","热搜："+s);
                        Gson gson = new Gson();
                        Resou resou = gson.fromJson(s, Resou.class);
                        if (resou.getCode() == 200){
                            resouList1 = resou.getResult();
                            hotflowLayout.removeAllViews();
                            LayoutInflater mInflater = LayoutInflater.from(SearchCoachActivity.this);
                            for (int k = 0; k < resouList1.size(); k++) {
                                TextView tv = (TextView) mInflater.inflate(R.layout.suosou_item, hotflowLayout, false);
                                tv.setText(resouList1.get(k).getName());
                                final String hottid=resouList1.get(k).getHotid();
                                final  String name =resouList1.get(k).getName();
                                if (resouList1.get(k).getRoles() == 1) {
                                    tv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent();
                                            intent.setClass(SearchCoachActivity.this, ReservationForPrivateActivity.class);
                                            intent.putExtra("coachId", hottid);
                                            intent.putExtra("uid", uid);
                                            intent.putExtra("token", token);
                                            startActivity(intent);
                                        }
                                    });
                                } else if (resouList1.get(k).getRoles() == 2) {
                                    tv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent();
                                            intent.setClass(SearchCoachActivity.this, PrivateCenterDetailsActivity.class);
                                            intent.putExtra("tid", hottid);
                                            intent.putExtra("name", name);
                                            intent.putExtra("uid", uid);
                                            intent.putExtra("token", token);
                                            startActivity(intent);
                                        }
                                    });
                                }
                                tv.getBackground().setLevel(MyRandom(1, 5));
                                hotflowLayout.addView(tv);
                            }
                        }
                    }
                });
    }

    protected  void initData(){
        String shareData =  "";
        List<String> skills = Arrays.asList(shareData.split(","));
        initData(skills , new setSearchCallBackListener() {
            @Override
            public void Search(String str) {
            }
            @Override
            public void ClearOldData() {
            }
            @Override
            public void SaveOldData(ArrayList<String> AlloldDataList) {

            }
        });
    }
    /**
     * 教练中心页面模糊查找
     *
     * @param str        编辑框输出的文字
     * @param searchPage 页数
     */
    private void search(String str,String searchPage) {
        Log.i("百姓学车","模糊查询参数"+ "str=" + str + "   page=" + searchPage + "url=" + searchUrl);
        OkHttpUtils
                .post()
                .url(searchUrl)
                .addParams("keyname", str)
                .addParams("page", searchPage)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(SearchCoachActivity.this, "网络异常，请检查网络！", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百姓学车","模糊查询"+s);
                        Gson gson = new Gson();
                        CoachDetailAction coachDetailAction = gson.fromJson(s, CoachDetailAction.class);
                        if (coachDetailAction.getCode() == 200) {
                            result1=coachDetailAction.getResult();
                            result2=coachDetailAction.getResultarr();
                            liststu.clear();
                            listTemp.clear();
                            listTemp.addAll(result1);
                            liststu.addAll(result2);
                            if (liststu.size()==0){
                                search_list.setVisibility(View.VISIBLE);
                                adapter = new PrivateCoachAdapter(SearchCoachActivity.this, listTemp);
                                search_list.setAdapter(adapter);
                            }else if (listTemp.size()==0){
                                search_list.setVisibility(View.VISIBLE);
                                adapter1 = new PrivateCenterSearchAdapter(SearchCoachActivity.this, liststu);
                                search_list.setAdapter(adapter1);
                            }else{
                                search_list.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(SearchCoachActivity.this, coachDetailAction.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void initData(List<String> olddatalist, setSearchCallBackListener sCb){
        SetCallBackListener(sCb);
//        OldDataList.clear();
        if(olddatalist!=null) {
            OldDataList.addAll(olddatalist);
        }
        countOldDataSize = OldDataList.size();
        OldDataAdapter = new SearchOldDataAdapter(this,OldDataList);
        gridviewolddata.setAdapter(OldDataAdapter);
    }

    private void executeSearch_and_NotifyDataSetChanged(String str) {
        if (sCBlistener != null && (!str.equals(""))) {
            if (OldDataList.size() > 0 && OldDataList.get(0).equals(str)) {
            } else {
                if (OldDataList.size() == countOldDataSize && OldDataList.size() > 0) {
//                    OldDataList.remove(OldDataList.size() - 1);
                    OldDataList.add(0, str);//把最新的添加到前面
                    OldDataAdapter.notifyDataSetChanged();
                    sCBlistener.SaveOldData(OldDataList);
                }
            }
            sCBlistener.Search(str);
        }
    }

    /**
     * 生成随机数
     * @param max  最大值
     * @param min   最小值
     * @return
     */
    public int MyRandom(int min,int max){
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        return s;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView coachId = (TextView) view.findViewById(R.id.coachId);
        TextView tid = (TextView) view.findViewById(R.id.coachId);
        TextView pri_center_name = (TextView) view.findViewById(R.id.coachName);
        Intent intent = new Intent();
        if (liststu.size()==0){
            intent.setClass(SearchCoachActivity.this, ReservationForPrivateActivity.class);
            intent.putExtra("coachId", coachId.getText().toString().trim());
            intent.putExtra("uid", uid);
            intent.putExtra("token", token);
        }else if (listTemp.size()== 0){
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
        switch (view.getId()){
            case R.id.button_backward:
                 finish();
                 break;
            case R.id.cancel:
                finish();
                break;
            case R.id.tv_clearolddata:       //清除历史记录
                if (sCBlistener != null){
                    OldDataList.clear();
                    OldDataAdapter.notifyDataSetChanged();
                    sCBlistener.ClearOldData();
                }
                break;
        }
    }

    public interface setSearchCallBackListener{
        /**
         * @param str  搜索关键字
         */
        public void Search(String str);

        /**
         * 清除历史搜索记录
         */
        public abstract void ClearOldData();

        /**
         * 保存搜索记录
         */
        public abstract void SaveOldData(ArrayList<String> AlloldDataList);
    }

    /**
     * 设置接口回调
     * @param sCb   setCallBackListener接口
     */
    private void SetCallBackListener(setSearchCallBackListener sCb){
        sCBlistener=sCb;
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
