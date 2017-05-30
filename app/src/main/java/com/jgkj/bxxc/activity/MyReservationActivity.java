package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.MyReservationAdapter;
import com.jgkj.bxxc.bean.MyReservationAction;
import com.jgkj.bxxc.tools.StatusBarCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
//我的保留预定
public class MyReservationActivity extends Activity implements OnClickListener {
	private ImageView back_up;
	private ListView listView;
	private MyReservationAdapter adapter;
	private MyReservationAction myReservationAction;
	private Bundle bundle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_reservation);
		StatusBarCompat.compat(this, Color.parseColor("#37363C"));
		bundle = new Bundle();
		myReservationAction = new MyReservationAction("百信学车","越达驾校中心校区", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
		bundle.putSerializable("myReservation", myReservationAction);
		
		listView = (ListView) findViewById(R.id.myReservation_listView);
		adapter = new MyReservationAdapter(MyReservationActivity.this,bundle);
		
		listView.setAdapter(adapter);
		back_up = (ImageView) findViewById(R.id.myReservation_back_up_id);
		back_up.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.myReservation_back_up_id:
			finish();
			break;
		}
		
	}

}
