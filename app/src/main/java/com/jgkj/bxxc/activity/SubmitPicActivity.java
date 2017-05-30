package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.tools.StatusBarCompat;

/**
 * Created by fangzhou on 2016/11/19.
 * 提交个人信息照片的结果
 */
public class SubmitPicActivity extends Activity {
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submitpic);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        init();
    }
    private void init() {
        title = (TextView) findViewById(R.id.text_title);
        title.setText("提交状态");
    }
}
