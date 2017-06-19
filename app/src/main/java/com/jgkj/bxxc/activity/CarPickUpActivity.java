package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.MyExpandableListAdapter;
import com.jgkj.bxxc.bean.SchoolPlaceTotal;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2017/3/14.
 *
 * 车接车送
 */

public class CarPickUpActivity extends Activity implements View.OnClickListener,
        ExpandableListView.OnChildClickListener{
    private Button back;
    private TextView title;
    private ExpandableListView subListView;
    private SchoolPlaceTotal schoolPlaceTotal;
    private String[] city;
    private String[][] datialPlace;
    private String[] citys;
    private String[][] datialPlaces;
    private Integer[][] datialPlaceId;
    private MyExpandableListAdapter adapter;
    private ProgressDialog dialog ;
    private String placePath = "http://www.baixinxueche.com/index.php/Home/Apitoken/Apiarea";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_pickup);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        initView();
        dialog = ProgressDialog.show(CarPickUpActivity.this, null, "玩命加载中...");
        getPlace();
    }

    private void initView() {
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.text_title);
        title.setText("车接车送");
        subListView = (ExpandableListView) findViewById(R.id.subListView);
        subListView.setGroupIndicator(null);
        subListView.setOnChildClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_backward:
                finish();
                break;
        }
    }
    private void addAdapter() {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        Gson gson = new Gson();
        String str = subListView.getTag().toString();
        schoolPlaceTotal = gson.fromJson(str, SchoolPlaceTotal.class);
        if (schoolPlaceTotal.getCode() == 200) {
            setPup(schoolPlaceTotal.getResult());
            doDatialPlace(city,datialPlace);
            adapter = new MyExpandableListAdapter(CarPickUpActivity.this,citys,datialPlaces);
            subListView.setAdapter(adapter);
        } else {
            Toast.makeText(CarPickUpActivity.this, schoolPlaceTotal.getReason(), Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 设置ExpandableListView
     */
    private void setPup(List<SchoolPlaceTotal.Result> results) {
        city = new String[results.size()];
        datialPlace = new String[results.size()][];
        datialPlaceId = new Integer[results.size()][];
        for (int i = 0; i < results.size(); i++) {
            city[i] = results.get(i).getSchool_aera();
            if (results.get(i) != null) {
                List<SchoolPlaceTotal.Result.Res> listSch = results.get(i).getResult();
                datialPlace[i] = new String[listSch.size()];
                datialPlaceId[i] = new Integer[listSch.size()];
                for (int j = 0; j < listSch.size(); j++) {
                    datialPlace[i][j] = listSch.get(j).getSname();
                    if(i != 0){
                        datialPlaceId[i-1][j] = listSch.get(j).getId();
                    }
                    //datialPlaceId[i][j] = listSch.get(j).getId();
                }
            }
        }
    }

    private void getPlace() {
        OkHttpUtils
                .get()
                .url(placePath)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        Toast.makeText(CarPickUpActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        subListView.setTag(s);
                        if (subListView.getTag() != null) {
                            addAdapter();
                        } else {
                            Toast.makeText(CarPickUpActivity.this, "网络状态不佳,请检查网络", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
        TextView textView = (TextView) view.findViewById(R.id.textView);
        Intent intent = new Intent();
        intent.setClass(CarPickUpActivity.this,CarSendActivity.class);
        intent.putExtra("schoolId",datialPlaceId[i][i1]);
        intent.putExtra("name",textView.getText().toString());
        startActivity(intent);
        return false;
    }

    public void doDatialPlace(String[] cityTemp,String[][] datialPlaceTemp){
        citys = new String[cityTemp.length-1];
        for(int i = 1;i<cityTemp.length;i++){
            citys[i-1] = cityTemp[i];
        }
        datialPlaces = new String[citys.length][];
        for(int i = 0;i<datialPlaceTemp.length;i++){
            for(int j = 0;j<datialPlaceTemp[i].length;j++){
                if(i!=0){
                    datialPlaces[i-1] = new String[datialPlaceTemp[i].length];
                    datialPlaces[i-1][j] = datialPlaceTemp[i][j];
                }
            }
        }
    }
}
