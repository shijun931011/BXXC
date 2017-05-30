package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.QuesAnsAdapter;
import com.jgkj.bxxc.tools.StatusBarCompat;

public class UseGuideActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView listView;
    private TextView title;
    private Button btn_back;
    private String[] listItem = {"经典班报名", "经典班使用说明", "私教班报名", "私教班使用说明"};
    private QuesAnsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_guide);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        InitView();
        initData();
    }

    private void InitView(){
        listView = (ListView) findViewById(R.id.listView);
        title = (TextView) findViewById(R.id.text_title);
        title.setText("使用指南");
        btn_back = (Button) findViewById(R.id.button_backward);
        btn_back.setVisibility(View.VISIBLE);

    }

    //初始化数据
    private void initData() {
        adapter = new QuesAnsAdapter(UseGuideActivity.this, listItem);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_backward:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent();
        switch (i) {
            case 0:
                intent.setClass(UseGuideActivity.this,WebViewActivity.class);
                intent.putExtra("url","http://www.baixinxueche.com/webshow/baoming/jingdan.html");
                intent.putExtra("title","经典班报名");
                startActivity(intent);
                break;
            case 1:
                intent.setClass(UseGuideActivity.this,WebViewActivity.class);
                intent.putExtra("url","http://www.baixinxueche.com/webshow/baoming/jingdanshuming.html  ");
                intent.putExtra("title","经典班使用说明");
                startActivity(intent);
                break;
            case 2:
                intent.setClass(UseGuideActivity.this,WebViewActivity.class);
                intent.putExtra("url","http://www.baixinxueche.com/webshow/baoming/sijiao.html");
                intent.putExtra("title","私教班报名");
                startActivity(intent);
                break;
            case 3:
                intent.setClass(UseGuideActivity.this,WebViewActivity.class);
                intent.putExtra("url","http://www.baixinxueche.com/webshow/baoming/sijiaoshuming.html");
                intent.putExtra("title","私教班使用说明");
                startActivity(intent);
                break;
        }
    }
}
