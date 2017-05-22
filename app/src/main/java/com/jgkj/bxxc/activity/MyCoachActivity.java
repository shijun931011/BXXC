package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.jgkj.bxxc.bean.MyCoachAction;
import com.jgkj.bxxc.tools.CreateDialog;
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
public class MyCoachActivity extends Activity implements View.OnClickListener {
    private Button back_forward;
    private TextView text_title;
    private ListView subListView;//没有用到
    private TextView textView;
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
    private Button btn;
    private String token;
    private String myCoachUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/myCoach";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coachtime_headview);
        init();
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
    private void init() {
        Log.i("shijun","init()");
        dialog = ProgressDialog.show(this,null,"加载中...");
        back_forward = (Button) findViewById(R.id.button_backward);
        back_forward.setVisibility(View.VISIBLE);
        text_title = (TextView) findViewById(R.id.text_title);
        text_title.setText("我的教练");
        back_forward.setOnClickListener(this);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.textView);
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
                textView.setVisibility(View.VISIBLE);
                textView.setText("暂无教练信息");
                dialog.dismiss();
            }
        }else{
            textView.setVisibility(View.VISIBLE);
            textView.setText("暂无教练信息");
            Toast.makeText(MyCoachActivity.this,"暂无教练信息", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
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
                mySub.setText("我的科目二教练");
            } else if (zhuangtai == 5) {
                mySub.setText("我的科目三教练");
            }else{
                mySub.setText("我的报名教练");
            }
            coachName.setText("教练："+res.getCoachname());
            num1.setText("当前所带学员数：" + res.getNowstudent() + "人");
            num2.setText("最高可带学员数：" + res.getMaxnum() + "人");
            myContext.setHint("\u3000\u3000"+res.getPrompt());
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
        } else {
            Toast.makeText(MyCoachActivity.this, myCoachAction.getReason(), Toast.LENGTH_SHORT).show();
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
            case R.id.btn:
                if(uid!=-1&&!res.getCid().equals("")&&res.getCid()!=null){
                    Intent intent = new Intent();
                    intent.setClass(MyCoachActivity.this,AppTimeNewActivity.class);
                    intent.putExtra("uid",uid);
                    intent.putExtra("cid",res.getCid());//token
                    intent.putExtra("token",token);
                    intent.putExtra("state",state);
                    startActivity(intent);
                }
                break;
        }
    }


}
