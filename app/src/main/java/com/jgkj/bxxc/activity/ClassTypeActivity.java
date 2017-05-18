package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.tools.Urls;

/**
 * 班型
 */
public class ClassTypeActivity extends Activity implements View.OnClickListener{

    private TextView title;
    private Button button_backward;
    private LinearLayout pri_class;       //私教班
    private LinearLayout ext_class;       //至尊班
    private LinearLayout vip_class;       //vip班

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_type);
        initview();
    }

    private void initview(){
        title = (TextView)  findViewById(R.id.text_title);
        title.setText("班型");
        button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setVisibility(View.VISIBLE);
        button_backward.setOnClickListener(this);
        pri_class = (LinearLayout) findViewById(R.id.pri_class);
        ext_class = (LinearLayout) findViewById(R.id.ext_class);
        vip_class = (LinearLayout) findViewById(R.id.vip_class);
        ext_class.setOnClickListener(this);
        vip_class.setOnClickListener(this);
        pri_class.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch(v.getId()){
            case R.id.button_backward:
                finish();
                break;
            case R.id.pri_class:
                intent.setClass(this, PrivateClassActivity.class);
                startActivity(intent);
                break;
            case R.id.ext_class:
                intent.setClass(this,ExtClassActivity.class);
                startActivity(intent);
                break;
            case R.id.vip_class:
                intent.setClass(this,VipClassActivity.class);
                startActivity(intent);
                break;

        }

    }
}
