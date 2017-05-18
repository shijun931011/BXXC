package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.PackageAdapter;
import com.jgkj.bxxc.bean.entity.BaseEntity.BaseEntity;
import com.jgkj.bxxc.bean.entity.PackageEntity.PackageEntity;
import com.jgkj.bxxc.bean.entity.PackageEntity.PackageResult;
import com.jgkj.bxxc.tools.Md5;
import com.jgkj.bxxc.tools.RemainBaseDialog;
import com.jgkj.bxxc.tools.Urls;
import com.lmj.mypwdinputlibrary.InputPwdView;
import com.lmj.mypwdinputlibrary.MyInputPwdUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * Created by tongshoujun on 2017/5/13.
 */

public class BuyClassHoursActivity extends Activity implements AdapterView.OnItemClickListener{

    private ListView listView;
    private MyInputPwdUtil myInputPwdUtil;
    //标题
    private TextView title;
    private Button button_backward;
    private ImageView remind;

    private int uid;
    private String cid;
    private String token;
    private List<PackageEntity> list;
    //套餐类型
    private String pacckageId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_class_hours);

        Intent intent = getIntent();
        uid = intent.getIntExtra("uid",uid);
        cid = intent.getStringExtra("cid");
        token = intent.getStringExtra("token");

        //标题
        title = (TextView) findViewById(R.id.text_title);
        title.setText("购买学时套餐");
        button_backward = (Button) findViewById(R.id.button_backward);
        remind = (ImageView) findViewById(R.id.remind);
        button_backward.setVisibility(View.VISIBLE);
        button_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        remind.setVisibility(View.VISIBLE);
        remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RemainBaseDialog(BuyClassHoursActivity.this,"这里展示的是百信学车为您提供的的学时套餐，优惠，方便。 学时套餐用于预约私教班教练练车时候，" +
                        "且以两个学时为起步，购买后的套餐，如有多余是可以申请退款的。").call();
            }
        });

        listView = (ListView)findViewById(R.id.listView);
        getData(Urls.packages);

        listView.setOnItemClickListener(this);

        myInputPwdUtil = new MyInputPwdUtil(BuyClassHoursActivity.this);
        myInputPwdUtil.getMyInputDialogBuilder().setAnimStyle(R.style.dialog_anim);
        myInputPwdUtil.setListener(new InputPwdView.InputPwdListener() {
            @Override
            public void hide() {
                myInputPwdUtil.hide();
            }

            @Override
            public void forgetPwd() {
                Toast.makeText(BuyClassHoursActivity.this, "忘记密码", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void finishPwd(String pwd) {
                //uid,  classhour 套餐类型  paypwd 支付密码
                //Toast.makeText(BuyClassHoursActivity.this, pwd, Toast.LENGTH_SHORT).show();
                getClassHoData(uid,pacckageId, Md5.md5(pwd),Urls.buyClassHour);
            }
        });

    }

    public void show(View view){
        myInputPwdUtil.show();
    }

    private void getData(String url) {
        Log.i("百信学车","套餐信息参数" + url);
        OkHttpUtils
                .post()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(BuyClassHoursActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        PackageResult packageResult = gson.fromJson(s, PackageResult.class);
                        list = packageResult.getResult();
                        Log.i("百信学车","套餐信息结果" + s);
                        if (packageResult.getCode() == 200) {
                            PackageAdapter adapter = new PackageAdapter(BuyClassHoursActivity.this,list);
                            listView.setAdapter(adapter);
                        } else {
                            Toast.makeText(BuyClassHoursActivity.this, "没有更多的！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getClassHoData(int uid_,String classhour,String paypwd,String url) {
        Log.i("百信学车","购买学时套餐参数" + "uid=" + uid_ + "   classhour=" + classhour + "   paypwd=" + paypwd + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("paypwd", paypwd)
                .addParams("classhour", classhour)
                .addParams("uid", Integer.toString(uid_))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(BuyClassHoursActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","购买学时套餐结果" + s);
                        Gson gson = new Gson();
                        BaseEntity buyClassHoureEntity = gson.fromJson(s, BaseEntity.class);
                        if (buyClassHoureEntity.getCode() == 200) {
                            Toast.makeText(BuyClassHoursActivity.this, "学时购买成功", Toast.LENGTH_LONG).show();
                            myInputPwdUtil.hide();
                        }
                        if (buyClassHoureEntity.getCode() == 700) {
                            new RemainBaseDialog(BuyClassHoursActivity.this,"余额不总，请充值!").call();
                            myInputPwdUtil.hide();
                        }
                        if (buyClassHoureEntity.getCode() == 600) {
                            Toast.makeText(BuyClassHoursActivity.this, "支付密码错误!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        pacckageId = list.get(position).getPackageid();
        SharedPreferences sharedPreferences= getSharedPreferences("paypwd", Activity.MODE_PRIVATE);
        // 使用getString方法获得value，注意第2个参数是value的默认值
        String paypwd = sharedPreferences.getString("paypwd", "");
        //判断是否设置支付密码
        if(paypwd != null && !"".equals(paypwd)){
            myInputPwdUtil.show();
        }else{
            //设置支付密码
            Intent intent = new Intent();
            intent.setClass(BuyClassHoursActivity.this,SetPayPasswordActivity.class);
            startActivity(intent);
        }

    }
}
