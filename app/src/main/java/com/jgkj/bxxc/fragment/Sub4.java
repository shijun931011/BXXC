package com.jgkj.bxxc.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.SubFourExamTestActivity;
import com.jgkj.bxxc.activity.SubFourRandTestActivity;
import com.jgkj.bxxc.activity.SubFourTestActivity;
import com.jgkj.bxxc.activity.SubfourErrorTestActivity;
import com.jgkj.bxxc.activity.WebViewActivity;
import com.jgkj.bxxc.bean.SubPicture;
import com.jgkj.bxxc.tools.AutoTextView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

public class Sub4 extends Fragment implements View.OnClickListener {
    private View view;
    private LinearLayout linearLayout1,linearLayout2,linearLayout3,linearLayout4;
    private ImageView orderTest, suijiTest,moniTest,cuotiTest;
    private List<String> list1;
    private int index;
    private ProgressDialog dialog;
    private LinearLayout inner_function, dashboard;
    private TextView inner_function1, dashboard1;
    private AutoTextView showNum;
    private ImageView boom;
    private Runnable runnable;
    private Handler handler = new Handler();
    private int[] phoneSt = new int[]{130, 131, 132, 133, 134, 135, 136, 137, 138, 139,
            145, 147, 150, 151, 152, 153, 155, 156, 157, 158, 159, 180, 181,
            182, 183, 185, 186, 187, 188, 189};
    private String Sub1Url = "http://www.baixinxueche.com/index.php/Home/Apitoken/bannerbaokaoupdate";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.subject4, container, false);
        dialog = ProgressDialog.show(getActivity(), null, "数据加载中...");
        init();
        getImage();
        return view;
    }

    /**
     * 初始化布局
     */
    private void init() {
        linearLayout1 = (LinearLayout) view.findViewById(R.id.linearlayout1);
        linearLayout1.setOnClickListener(this);
        linearLayout2 = (LinearLayout) view.findViewById(R.id.linearlayout2);
        linearLayout2.setOnClickListener(this);
        linearLayout3 = (LinearLayout) view.findViewById(R.id.linearlayout3);
        linearLayout3.setOnClickListener(this);
        linearLayout4 = (LinearLayout) view.findViewById(R.id.linearlayout4);
        linearLayout4.setOnClickListener(this);
        orderTest = (ImageView) view.findViewById(R.id.ordertest);
        suijiTest = (ImageView) view.findViewById(R.id.suijiTest);
        moniTest = (ImageView) view.findViewById(R.id.monitest);
        cuotiTest = (ImageView) view.findViewById(R.id.cuotiTest);
        Drawable textimg1 = getResources().getDrawable(R.drawable.textimg1);
        textimg1.setBounds(0, 0, 50, 50);
        Drawable textimg4 = getResources().getDrawable(R.drawable.textimg4);
        textimg4.setBounds(0, 0, 50, 50);
        inner_function1 = (TextView) view.findViewById(R.id.inner_function1);
        dashboard1 = (TextView) view.findViewById(R.id.dashboard1);
        //设置左部图标
        inner_function1.setCompoundDrawables(textimg1, null, null, null);
        dashboard1.setCompoundDrawables(textimg4, null, null, null);
        inner_function = (LinearLayout) view.findViewById(R.id.inner_function);
        dashboard = (LinearLayout) view.findViewById(R.id.dashboard);
        dashboard.setOnClickListener(this);
        inner_function.setOnClickListener(this);
        boom = (ImageView) view.findViewById(R.id.boom);
        Glide.with(getActivity()).load(R.drawable.boom).into(boom);
        showNum = (AutoTextView) view.findViewById(R.id.showNum);
        showNum.setText("恭喜 " + getNum() + " 学员 顺利拿证!");
        setShowNum();
    }
    private void setShowNum() {
        runnable = new Runnable() {
            public void run() {
                showNum.next();
                showNum.setText("恭喜 " + getNum() + " 学员 顺利拿证!");
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 5000);
    }

    private String getNum() {
        int num1 = (int) (Math.random() * 29);
        int num2 = (int) (Math.random() * 9999);
        return phoneSt[num1] + "****" + num2;
    }

    private void getImage() {
        OkHttpUtils
                .post()
                .url(Sub1Url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(getActivity(), "网络状态不佳,请稍后再试！", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("百信学车","照片"+s);
                        dialog.dismiss();
                        Gson gson = new Gson();
                        SubPicture pic = gson.fromJson(s, SubPicture.class);
                        if (pic.getCode() == 200) {
                            list1 = pic.getMorepic();
                            Glide.with(getActivity()).load(list1.get(0)).into(orderTest);
                            Glide.with(getActivity()).load(list1.get(1)).into(suijiTest);
                            Glide.with(getActivity()).load(list1.get(2)).into(moniTest);
                            Glide.with(getActivity()).load(list1.get(3)).into(cuotiTest);
                        }
                    }
                });
    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.linearlayout1:
                intent.setClass(getActivity(), SubFourTestActivity.class);
                startActivity(intent);
                break;
            case R.id.linearlayout4:
                intent.setClass(getActivity(), SubfourErrorTestActivity.class);
                startActivity(intent);
                break;
            case R.id.linearlayout2:
                intent.setClass(getActivity(), SubFourRandTestActivity.class);
                startActivity(intent);
                break;
            case R.id.linearlayout3:
                intent.setClass(getActivity(), SubFourExamTestActivity.class);
                startActivity(intent);
                break;
            case R.id.inner_function:
                intent.setClass(getActivity(), WebViewActivity.class);
                intent.putExtra("url", "http://www.baixinxueche.com/webshow/kesi/gnj.html");
                intent.putExtra("title", "车内功能键");
                startActivity(intent);
                break;
            case R.id.dashboard:
                intent.setClass(getActivity(), WebViewActivity.class);
                intent.putExtra("url", "http://www.baixinxueche.com/webshow/kesi/ybp.html");
                intent.putExtra("title", "汽车仪表盘");
                startActivity(intent);
                break;
        }
    }
}
