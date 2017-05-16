package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.RehourAdapter;
import com.jgkj.bxxc.bean.Rehour;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 剩余学时
 */
public class RehourActivity extends Activity implements View.OnClickListener{
    private Button btn_backward, btn_tuikuan;
    private TextView title;
    private ListView list_hour;           //剩余学时
    private int uid;
    private String token;
    private TextView prompt;   //提示文字
    private ImageView img_cry;//哭脸图片
    private TextView immediate_bug;     //学时不够？立即购买
    private TextView extra_hours;//多余课时怎么办？
    private Dialog  extraDialog;
    private View extraView;
    private TextView dialog_textView, dialog_sure, dialog_cancel,dialog_prompt;


    // 剩余学时，
    /*
     * uid  token
     * 返回 400 失败  200 成功
     * package_id 套餐几
     * surplus_class 剩余课时
     * surplus_money 剩余钱
     * */
    private String RehourUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/Hours";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rehour);
        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getRehour(uid + "", token);
    }

    private void initView(){
        btn_backward = (Button) findViewById(R.id.button_backward);
        btn_tuikuan = (Button) findViewById(R.id.button_forward);
        title = (TextView) findViewById(R.id.text_title);
        btn_tuikuan = (Button) findViewById(R.id.button_forward);
        list_hour = (ListView) findViewById(R.id.listView);
        prompt=(TextView) findViewById(R.id.prompt);
        img_cry = (ImageView) findViewById(R.id.kulian);
        immediate_bug = (TextView) findViewById(R.id.immediate_bug);
        extra_hours = (TextView) findViewById(R.id.extra_hours);
        title.setText("剩余学时");
        btn_backward.setVisibility(View.VISIBLE);
        btn_tuikuan.setVisibility(View.VISIBLE);
        btn_tuikuan.setText("退款进度");
        btn_backward.setOnClickListener(this);
        btn_tuikuan.setOnClickListener(this);
        immediate_bug.setOnClickListener(this);
        extra_hours.setOnClickListener(this);
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid", -1);
        token = intent.getStringExtra("token");
        Log.d("shijun", uid + "::::" + token);
    }

    private void getRehour(String uid, String token){
        OkHttpUtils
                .post()
                .url(RehourUrl)
                .addParams("uid", uid)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(RehourActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("shijun", "hhhh"+s);
                        Gson gson = new Gson();
                        Rehour rehour = gson.fromJson(s, Rehour.class);
                        List<Rehour.Result> list = new ArrayList<Rehour.Result>();
                        if (rehour.getCode() == 200){
                            List<Rehour.Result> results = rehour.getResult();
                            list.addAll(results);
                        }
                        RehourAdapter adapter = new RehourAdapter(RehourActivity.this, list);
                        list_hour.setAdapter(adapter);
                        if (rehour.getCode() == 400){
                            img_cry.setVisibility(View.VISIBLE);
                            prompt.setVisibility(View.VISIBLE);
                            immediate_bug.setVisibility(View.GONE);
                            prompt = (TextView) findViewById(R.id.prompt);
                            prompt.setHighlightColor(getResources().getColor(android.R.color.transparent));
                            SpannableString spanableInfo = new SpannableString("您还没有任何可用学时，请速速  前往购买");
                            spanableInfo.setSpan(new Clickable(clickListener),14,20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            prompt.setText(spanableInfo);
                            prompt.setMovementMethod(LinkMovementMethod.getInstance());
                        }
                    }
                });
    }

    private View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(RehourActivity.this, "点击成功....",Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(RehourActivity.this,BugClassActivity.class);
//            startActivity(intent);
        }
    };

    class Clickable extends ClickableSpan {
        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener l) {
            mListener = l;
        }

        /**
         * 重写父类点击事件
         */
        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }

        /**
         * 重写父类updateDrawState方法  我们可以给TextView设置字体颜色,背景颜色等等...
         */
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.color_bule));
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.button_backward:
                finish();
                break;
            case R.id.button_forward:
                intent.setClass(this,RefundActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("token", token);
                startActivity(intent);
                break;
            case R.id.immediate_bug:
                intent.setClass(this, BugPageageActivity.class);
                startActivity(intent);
                break;
            case R.id.extra_hours:
                extraDialog = new Dialog(RehourActivity.this,  R.style.ActionSheetDialogStyle);
                // 填充对话框的布局
                extraView = LayoutInflater.from(RehourActivity.this).inflate(R.layout.sure_cancel_dialog, null);
                dialog_textView = (TextView) extraView.findViewById(R.id.dialog_textView);
                dialog_textView.setText("您剩下的多余学时，可以在这里申请退款，百信学车竭诚为您服务！");
                dialog_prompt = (TextView) extraView.findViewById(R.id.diolog_prompt);
                dialog_sure = (TextView) extraView.findViewById(R.id.dialog_sure);
                dialog_sure.setText("我要退款");
                dialog_cancel = (TextView) extraView.findViewById(R.id.dialog_cancel);
                dialog_cancel.setText("点错了");
                dialog_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        extraDialog.dismiss();
                    }
                });
                // 将布局设置给Dialog
                extraDialog.setContentView( extraView);
                // 获取当前Activity所在的窗体
                Window dialogWindow = extraDialog.getWindow();
                // 设置dialog宽度
                dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                // 设置Dialog从窗体中间弹出
                dialogWindow.setGravity(Gravity.CENTER);
                extraDialog.show();
        }
    }
}
