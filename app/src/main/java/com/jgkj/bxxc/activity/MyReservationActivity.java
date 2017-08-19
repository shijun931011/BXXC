package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.MyReservationAdapter;
import com.jgkj.bxxc.bean.MyReservationAction;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 我的预约
 */
public class MyReservationActivity extends Activity implements OnClickListener{
	private Button back;
	private TextView text_title;
	private ListView listView;
	private TextView txt_noSmsData;
	private MyReservationAdapter adapter;
	private MyReservationAction myReservationAction;
	private List<MyReservationAction.Result> ReservationTimeResult;
	private int uid;
	private String token;

	/**
	 * 我的预约
	 * uid，token
	 */
	private String MyReservationUrl = "http://www.baixinxueche.com/index.php/Home/Hotsearch/myAppoint";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_reservation);
		initView();
		getMyReservation(uid,token);
	}

	private void initView(){
		back = (Button) findViewById(R.id.button_backward);
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(this);
		text_title = (TextView) findViewById(R.id.text_title);
		text_title.setText("我的约车时间");
		listView = (ListView) findViewById(R.id.myReservation_listView);
		txt_noSmsData = (TextView) findViewById(R.id.noSmsData);
		Intent intent = getIntent();
		uid = intent.getIntExtra("uid", uid);
		token = intent.getStringExtra("token");

	}
	private void getMyReservation(final int uid, final String token){
		Log.d("百信学车","我的预约参数:uid :" + uid + " token = " + token);
		OkHttpUtils.post()
				.url(MyReservationUrl)
				.addParams("uid",uid+"")
				.addParams("token",token)
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int i) {
						Toast.makeText(MyReservationActivity.this, "网络连接失败",Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onResponse(String s, int i) {
						Log.d("百信学车","我的预约:"+s);
						Gson gson = new Gson();
						MyReservationAction myReservationAction = gson.fromJson(s, MyReservationAction.class);
						List<MyReservationAction.Result> list = new ArrayList<MyReservationAction.Result>();
//						List<MyReservationAction.Result.Res> list1 = new ArrayList<MyReservationAction.Result.Res>();
						if (myReservationAction.getCode() == 200){
							List<MyReservationAction.Result> ReservationTimeResult = myReservationAction.getResult();
//							List<MyReservationAction.Result.Res> ReservationDetailResult = myReservationAction.getResult().get(0).getRes();
							list.addAll(ReservationTimeResult);
//							list1.addAll(ReservationDetailResult);
						}
						MyReservationAdapter adapter = new MyReservationAdapter(MyReservationActivity.this,list,uid,token);
						listView.setAdapter(adapter);
						if (myReservationAction.getCode() == 400){
							txt_noSmsData.setVisibility(View.VISIBLE);
						}

					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_backward:
				finish();
				break;
		}

	}

}
