package com.jgkj.bxxc.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.MyReservationAction;
import com.jgkj.bxxc.bean.entity.CancelEntity.CancelResult;
import com.jgkj.bxxc.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

public class MyReservationAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private MyReservationAction.Result ResTimeresult;
	private List<MyReservationAction.Result> list;
//	private MyReservationAction.Result.Res ResDetailresult;
//	private List<MyReservationAction.Result.Res> list1;
    private Dialog  sureDialog;
	private View sureView;
	private TextView dialog_textView, dialog_sure, dialog_cancel;
	private int uid;
	private String token;

    public MyReservationAdapter(Context context, List<MyReservationAction.Result> list,int uid,String token) {
		Log.d("百信学车", "数据1"+ list);
		this.context = context;
		this.list = list;
		this.uid = uid;
		this.token = token;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}


	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.myreservation_listview_item, parent, false);
			viewHolder.txt_reservation_time = (TextView) convertView.findViewById(R.id.txt_reservation_time);
			viewHolder.txt_reservation_coach= (TextView) convertView.findViewById(R.id.txt_reservation_coach);
			viewHolder.txt_reservation_school = (TextView) convertView.findViewById(R.id.txt_reservation_school);
			viewHolder.img_reservation_type = (ImageView) convertView.findViewById(R.id.img_reservation_type);
			viewHolder.myreservation_time = (TextView) convertView.findViewById(R.id.myreservation_time);
			viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearlayout);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ResTimeresult = list.get(position);
//		ResDetailresult = list1.get(position);

		viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				createSureDialog();
			}
		});
		viewHolder.myreservation_time.setText(ResTimeresult.getDay());
		viewHolder.txt_reservation_time.setText(ResTimeresult.getRes().get(0).getTime_slot());
		viewHolder.txt_reservation_coach.setText(ResTimeresult.getRes().get(0).getName());
		viewHolder.txt_reservation_school.setText(ResTimeresult.getRes().get(0).getSchool());
		if (ResTimeresult.getRes().get(0).getClass_style().equals("1")){
		    viewHolder.img_reservation_type.setImageResource(R.drawable.img_peilian);
		}else if (ResTimeresult.getRes().get(0).getClass_style().equals("0")){
			viewHolder.img_reservation_type.setImageResource(R.drawable.img_sijiao);
		}
		return convertView;
	}

		private void createSureDialog() {
		sureDialog = new Dialog(context, R.style.ActionSheetDialogStyle);
		// 填充对话框的布局
		sureView = LayoutInflater.from(context).inflate(R.layout.sure_cancel_dialog, null);
		// 初始化控件
		dialog_textView = (TextView) sureView.findViewById(R.id.dialog_textView);
		dialog_textView.setText("此时间段你已成功预约！（两小时内不能取消，给您带来不便尽请谅解！）");
		dialog_sure = (TextView) sureView.findViewById(R.id.dialog_sure);
		dialog_sure.setText("取消预约");
		dialog_cancel = (TextView) sureView.findViewById(R.id.dialog_cancel);
		dialog_cancel.setText("知道了");
		dialog_sure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getDataForReservation(uid, token, ResTimeresult.getRes().get(0).getCid() , ResTimeresult.getDay(), ResTimeresult.getRes().get(0).getTime_slot(), ResTimeresult.getRes().get(0).getTid());
				sureDialog.dismiss();
			}
		});
		dialog_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sureDialog.dismiss();
			}
		});
		// 将布局设置给Dialog
		sureDialog.setContentView(sureView);
		// 获取当前Activity所在的窗体
		Window dialogWindow = sureDialog.getWindow();
		// 设置dialog宽度
		dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		// 设置Dialog从窗体中间弹出
		dialogWindow.setGravity(Gravity.CENTER);
		sureDialog.show();
	}


	/**
	 *
	 * @param uid
	 * @param token
	 * @param cid
	 * @param day
	 * @param time_slot
	 * @param tid
	 */
	private void getDataForReservation(int uid, String token, String cid ,String day,String time_slot,String tid) {
		Log.i("百信学车","取消预约" + "uid=" + uid + "   token=" + token + "   cid=" + cid + "   day=" + day + "   time_slot=" + time_slot);
		OkHttpUtils
				.post()
				.url(Urls.priteamCancelcourse)
				.addParams("uid", Integer.toString(uid))
				.addParams("token", token)
				.addParams("cid", cid)
				.addParams("day", day)
				.addParams("time_slot", time_slot)
				.addParams("tid",tid)
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int i) {
						Toast.makeText(context,"加载失败", Toast.LENGTH_LONG).show();
					}
					@Override
					public void onResponse(String s, int i) {
						Gson gson = new Gson();
						CancelResult cancelResult = gson.fromJson(s,CancelResult.class);
						Log.i("百信学车","取消预约结果" + s);
						if (cancelResult.getCode() == 200) {
							Toast.makeText(context, "取消预约成功！", Toast.LENGTH_SHORT).show();
						}
						if (cancelResult.getCode() == 400){
							Toast.makeText(context, cancelResult.getReason() + "！", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}
	static class ViewHolder {
		public ImageView img_reservation_type;
		public TextView txt_reservation_time, txt_reservation_coach, txt_reservation_school,myreservation_time;
		public LinearLayout linearLayout;
	}

}
