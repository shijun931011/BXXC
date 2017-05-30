package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.tools.StatusBarCompat;

/**
 * Created by fangzhou on 2017/2/6.
 * <p>
 * 向用户展示流程
 */

public class GuideActivity extends Activity implements View.OnClickListener {
    private ImageView image;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        initView();

    }

    private void initView() {
        image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(this);
        Glide.with(this).
                load(R.drawable.guide1).
                asBitmap().
                into(image);
    }

    @Override
    public void onClick(View view) {
        switch (count) {
            case 0:
                Glide.with(this).
                        load(R.drawable.guide2).
                        asBitmap().
                        into(image);
                break;
            case 1:
                Glide.with(this).
                        load(R.drawable.guide3).
                        asBitmap().
                        into(image);
                break;
            case 2:
                Glide.with(this).
                        load(R.drawable.guide4).
                        asBitmap().
                        into(image);
                break;
            default:
                finish();
                break;
        }
        count++;
    }
}
