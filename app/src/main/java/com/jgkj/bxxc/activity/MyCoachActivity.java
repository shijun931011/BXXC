package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.MyCoachPrivateAdapter;
import com.jgkj.bxxc.adapter.StuSubNewAdapter;
import com.jgkj.bxxc.bean.CreateDay_Time;
import com.jgkj.bxxc.bean.MyCoachAction;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.bean.entity.MyCoachForPrivateEntity.MyCoachPrivateResult;
import com.jgkj.bxxc.tools.CreateDialog;
import com.jgkj.bxxc.tools.RefreshLayout;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.jgkj.bxxc.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


/**
 * Created by fangzhou on 2016/11/5.
 * 展示我的教练页面
 */
public class MyCoachActivity extends Activity implements View.OnClickListener , SwipeRefreshLayout.OnRefreshListener{
    private Button back_forward;
    private TextView text_title;
    private ListView subListView;//没有用到
    private TextView textView1;
    public TextView coachName, mycar, myclass;
    public TextView mySub, place, num1, num2, myContext, tips;
    public ImageView coachHead;
    private String cid;
    private int uid;
    private int zhuangtai;
    private ProgressDialog dialog;
    private List<String> day = new ArrayList<>();
    private MyCoachAction.Result res;
    private String state;
    private String token;
    private String myCoachUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/myCoach";
    private ListView listView;
    private TextView noSmsData;
    private RefreshLayout swipeLayout;
    private MyCoachAction coachAction;
    private StuSubNewAdapter adapter;
    private CreateDay_Time createday;
    private List<CreateDay_Time> list;
    private TextView textView;
    private UserInfo userInfo;
    private String classType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
        String str = sp.getString("userInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str, UserInfo.class);
        classType = userInfo.getResult().getClasstype();
        //私教班
        if("2".equals(classType) || "0".equals(classType)){
            setContentView(R.layout.activity_shjiao_my_coach);
            initCoach();
        }
        //经典班
        if("1".equals(classType)){
            setContentView(R.layout.coachtime_headview);
            init();
        }
    }

    /**
     * 是否创建dialog，提示是否可以更改教练或者确定教练
     *
     * @param zhuangtai
     */
    private void isCreateDialog(int zhuangtai) {
        Log.d("shijun", "isCreateDialog");
        if (zhuangtai == 1) {
            new CreateDialog(MyCoachActivity.this, "您已进入科目二阶段，请确定是否选择该教练"
                    , "确定选择", "前去更改", uid,zhuangtai,token).createSureDialog();
        } else if (zhuangtai == 4) {
            new CreateDialog(MyCoachActivity.this, "您已进入科目三阶段，请确定是否选择该教练"
                    , "确定选择", "前去更改", uid,zhuangtai,token).createSureDialog();
        }
    }

    /**
     * 初始化布局文件
     */
    private void initCoach() {
        back_forward = (Button) findViewById(R.id.button_backward);
        back_forward.setVisibility(View.VISIBLE);
        text_title = (TextView) findViewById(R.id.text_title);
        text_title.setText("我的教练");
        back_forward.setOnClickListener(this);

        listView = (ListView)findViewById(R.id.listView);
        noSmsData = (TextView)findViewById(R.id.noSmsData);
        listView.setEmptyView(noSmsData);
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid", -1);
        cid = intent.getStringExtra("cid");
        state = intent.getStringExtra("state");
        token = intent.getStringExtra("token");
        getMyCoachPrivate(uid + "",token);
    }

    /**
     * 初始化布局文件
     */
    private void init() {
        Log.i("shijun","init()");
        dialog = ProgressDialog.show(this,null,"加载中...");
        back_forward = (Button) findViewById(R.id.button_backward);
        back_forward.setVisibility(View.VISIBLE);
        text_title = (TextView) findViewById(R.id.text_title);
        text_title.setText("我的教练");
        back_forward.setOnClickListener(this);
        textView1 = (TextView) findViewById(R.id.textView1);
        /**
         * headView布局
         */
        mySub = (TextView) findViewById(R.id.mySub);
        tips = (TextView) findViewById(R.id.tips);
        num1 = (TextView) findViewById(R.id.num1);
        coachName = (TextView) findViewById(R.id.coachName);
        place = (TextView) findViewById(R.id.place);
        mycar = (TextView) findViewById(R.id.mycar);
        myclass = (TextView) findViewById(R.id.myclass);
        num2 = (TextView) findViewById(R.id.num2);
        myContext = (TextView) findViewById(R.id.myContext);
        coachHead = (ImageView) findViewById(R.id.coachHead);
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid", -1);
        cid = intent.getStringExtra("cid");
        state = intent.getStringExtra("state");
        token = intent.getStringExtra("token");
        Log.i("shijun","ssss"+uid);
        if(!state.equals("")&&state!=null&&!state.equals("未报名")){
            if (uid != -1) {
                getMyCoach(uid + "");
            } else {
                textView1.setVisibility(View.VISIBLE);
                textView1.setText("暂无教练信息");
                dialog.dismiss();
            }
        }else{
            textView1.setVisibility(View.VISIBLE);
            textView1.setText("暂无教练信息");
            Toast.makeText(MyCoachActivity.this,"暂无教练信息", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }

        listView = (ListView) findViewById(R.id.listView);
        textView = (TextView) findViewById(R.id.textView);

        //下拉刷新
        swipeLayout = (RefreshLayout) findViewById(R.id.refresh);
        swipeLayout.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        swipeLayout.setOnRefreshListener(this);
        if(!state.equals("科目二正在进行中")&&!state.equals("科目三正在进行中")){
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            textView.setText("您的状态暂时不可预约学车");
        }
    }


    /**
     * 解析json转化为适配器需要的json数据，
     * 并且填充控件中去
     */
    private void setListView() {
        Log.i("shijun","setListView()");
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        String str = text_title.getTag().toString();
        Gson gson = new Gson();
        MyCoachAction myCoachAction = gson.fromJson(str, MyCoachAction.class);
        if (myCoachAction.getCode() == 200) {
            res = myCoachAction.getResult();
            //填充数据
            zhuangtai = res.getZhuangtai();
            isCreateDialog(res.getZhuangtai());
            if (zhuangtai == 2 ) {
                mySub.setHint("我的科目二教练");
            } else if (zhuangtai == 5) {
                mySub.setHint("我的科目三教练");
            }else{
                mySub.setHint("我的报名教练");
            }
            coachName.setText("教练："+res.getCoachname());
            num1.setText("当前学员数：" + res.getNowstudent() + "人");
            num2.setText("最高可教学员数：" + res.getMaxnum() + "人");
            myContext.setHint(res.getPrompt());
            place.setText("地址："+res.getFaddress());
            mycar.setText("车型："+res.getChexing());
            myclass.setText("班型："+res.getClass_type());
            String path = res.getFile();
            if(!path.endsWith(".jpg")&&!path.endsWith(".jpeg")&&!path.endsWith(".png")&&
                    !path.endsWith(".GIF")&&!path.endsWith(".PNG") &&!path.endsWith(".JPG")&&!path.endsWith(".gif")){
                Glide.with(MyCoachActivity.this).load("http://www.baixinxueche.com/Public/Home/img/default.png").into(coachHead);
            }else{
                Glide.with(MyCoachActivity.this).load(res.getFile()).into(coachHead);
            }

            cid = res.getCid();
            getListData();

        } else {
            Toast.makeText(MyCoachActivity.this, myCoachAction.getReason(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 获取数据并且设置值
     */
    private void getListData() {
        String str = text_title.getTag().toString();
        Gson gson = new Gson();
        coachAction = gson.fromJson(str, MyCoachAction.class);
        list = new ArrayList<>();
        List<String> strList = new ArrayList<>();
        if (coachAction.getCode() == 200) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            List<MyCoachAction.Result.Res1> res1 = coachAction.getResult().getSubject();
            for (int i = 0; i < res1.size(); i++) {
                List<String> time = null;
                List<Boolean> isApp = null;
                List<Integer> count = null;
                if (!strList.contains(res1.get(i).getTimeone())) {
                    time = new ArrayList<>();
                    isApp = new ArrayList<>();
                    count = new ArrayList<>();
                    time.add(res1.get(i).getTime_slot());
                    isApp.add(false);
                    count.add(res1.get(i).getCount());
                    createday = new CreateDay_Time(res1.get(i).getTimeone(), time,
                            isApp, count);
                    strList.add(res1.get(i).getTimeone());
                    list.add(createday);
                } else {
                    int index = strList.indexOf(res1.get(i).getTimeone());
                    list.get(index).getTime().add(res1.get(i).getTime_slot());
                    list.get(index).getIsApp().add(false);
                    list.get(index).getCount().add(res1.get(i).getCount());
                }
            }
        } else {
            Toast.makeText(MyCoachActivity.this, coachAction.getReason(), Toast.LENGTH_SHORT).show();
        }

        List<MyCoachAction.Result.Res2> res2 = coachAction.getResult().getStusubject();

        for (int i = 0; i < list.size(); i++) {
            for (int k = 0; k < res2.size(); k++) {
                if (res2.get(k).getDay().equals(list.get(i).getDay())) {
                    int size = list.get(i).getIsApp().size();
                    for (int j = 0; j < size; j++) {
                        if (res2.get(k).getTime_slot().equals(list.get(i).getTime().get(j))) {
                            list.get(i).getIsApp().set(j, true);
                        }
                    }
                }
            }
        }
        if (list.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            adapter = new StuSubNewAdapter(MyCoachActivity.this, list, cid, uid + "",token);
            listView.setAdapter(adapter);
        }
    }

    /**
     * 获取教练信息和排课时间表，并且还有我的预约时间表
     * @param uid 用户id
     *            token 用户的token值
     */
    private void getMyCoach(String uid) {
        Log.i("百信学车","我的教练参数  uid=" + uid + "   cid=" + cid + "   state=" + state + "   token=" + token + "   url=" + myCoachUrl);
        OkHttpUtils
                .post()
                .url(Urls.myCoachUrl)
                .addParams("uid", uid)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialog.dismiss();
                        Toast.makeText(MyCoachActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","我的教练结果" + s);
                        Log.d("shijun","ssss"+s);
                        text_title.setTag(s);
                        if (text_title.getTag() != null) {
                            setListView();
                        }
                        dialog.dismiss();
                    }
                });
    }

    /**
     * 获取教练信息和排课时间表，并且还有我的预约时间表
     * @param uid_
     * @param token_
     */
    private void getMyCoachPrivate(String uid_,String token_) {
        Log.i("百信学车","我的教练私教班参数  uid=" + uid  + "   token=" + token + "   url=" + myCoachUrl);
        OkHttpUtils
                .post()
                .url(Urls.myCoachUrl)
                .addParams("uid", uid_)
                .addParams("token", token_)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialog.dismiss();
                        Toast.makeText(MyCoachActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","我的教练私教班结果" + s);
                        Gson gson = new Gson();
                        MyCoachPrivateResult myCoachPrivateResult = gson.fromJson(s, MyCoachPrivateResult.class);
                        MyCoachPrivateAdapter adapter = new MyCoachPrivateAdapter(MyCoachActivity.this,myCoachPrivateResult.getResult(),uid,token);
                        listView.setAdapter(adapter);
                    }
                });
    }

    @Override
    protected void onRestart() {
        Log.i("shijun","onRestart() ");
        super.onRestart();
//        SharedPreferences sp = getSharedPreferences("getSubjectSet", Activity.MODE_PRIVATE);
//        boolean ischange = sp.getBoolean("isChange",false);
//        if(uid!=-1&&!res.getCid().equals("")&&res.getCid()!=null&&ischange){
//            Intent intent = new Intent();
//            intent.setClass(MyCoachActivity.this,AppTimeNewActivity.class);
//            intent.putExtra("uid",uid);
//            intent.putExtra("cid",res.getCid());//token
//            intent.putExtra("token",token);
//            startActivity(intent);
//        }

        //经典班
        if("1".equals(classType)){
            init();
        }
    }

    /**
     * 点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = getSharedPreferences("getSubjectSet", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isChange", false);
        editor.commit();
    }

    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                getMyCoach(uid + "");
                swipeLayout.setRefreshing(false);
            }
        }, 2000);
    }
}
