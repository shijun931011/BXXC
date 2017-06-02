package com.jgkj.bxxc.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.SubErrorTestActivity;
import com.jgkj.bxxc.activity.SubExamTestActivity;
import com.jgkj.bxxc.activity.SubFourExamTestActivity;
import com.jgkj.bxxc.activity.SubFourRandTestActivity;
import com.jgkj.bxxc.activity.SubFourTestActivity;
import com.jgkj.bxxc.activity.SubRandTestActivity;
import com.jgkj.bxxc.activity.SubTestActivity;
import com.jgkj.bxxc.activity.SubfourErrorTestActivity;

public class License_Text_Fragment extends Fragment implements View.OnClickListener{
	private View view;
//	private Button orderTest,error_Sub,randomTest,examTest;
    private LinearLayout linearLayout1,linearLayout2,linearLayout3,linearLayout4;
	private int index;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.subject1, container, false);
		init();
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
		Bundle bundle = getArguments();

		index = bundle.getInt("index");
	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent();
		switch (view.getId()){
			case R.id.linearlayout1:
				if(index==1){
					intent.setClass(getActivity(),SubTestActivity.class);
				}else if(index==4){
					intent.setClass(getActivity(),SubFourTestActivity.class);
				}
				break;
			case R.id.linearlayout4://SubfourErrorTestActivity
				if(index==1){
					intent.setClass(getActivity(),SubErrorTestActivity.class);
				}else if(index==4){
					intent.setClass(getActivity(),SubfourErrorTestActivity.class);
				}
				break;
			case R.id.linearlayout2:
				if(index==1) {
					intent.setClass(getActivity(), SubRandTestActivity.class);
				}else if(index==4){
					intent.setClass(getActivity(),SubFourRandTestActivity.class);
				}
				break;
			case R.id.linearlayout3:
				if(index==1) {
					intent.setClass(getActivity(),SubExamTestActivity.class);
				}else if(index==4){
					intent.setClass(getActivity(),SubFourExamTestActivity.class);
				}
				break;
		}
		startActivity(intent);
	}
}
