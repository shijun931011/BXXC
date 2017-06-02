package com.jgkj.bxxc.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * 
 * @ClassName: RollableTextView.java
 * @Description: 可以滚动的textView，支持自定义停留时间、滚动速度、是否滚动，理论支持无限条字符串。
 *               暂不支持drawable、paddingTop、paddingBottom、 wrap_content
 * @author Daniel
 * @date 2016年1月12日下午4:12:35
 * 
 */
public class RollableTextView extends TextView {
	@SuppressWarnings("unused")
	private static final String TAG = "RollableTextView";

	List<String> strings = new ArrayList<String>();

	private int currentHeight;

	private Paint mPaint = new Paint(ANTI_ALIAS_FLAG);

	private boolean showStop = false;

	private boolean stopable = true;

	private boolean rollable = true;

	private int stopTime = 2000;

	private int scrollTime = 0;

	private int currentPosition = 0;

	public RollableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint.setColor(getCurrentTextColor());
		mPaint.setTextSize(getTextSize());
		mPaint.setTextAlign(Paint.Align.LEFT);
		setText(" "); // 如果不设置不好控制高度
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		// 如果string列表为空，则装载默认值
		if (strings.size() == 0) {
			strings.add("暂无消息");
		}

		// 如果不能滚动，则显示第一个
		if (!rollable) {
			setText(strings.get(0));
			super.onDraw(canvas);
			return;
		}

		// 如果高度大于了总高度，则回到初始位置显示。
		if (currentHeight + 1 > getMeasuredHeight() * strings.size()) {
			currentHeight = 0;
		} else {
			currentHeight += 1;
		}

		// 计算位置绘制字符
		int x, y;
		x = getPaddingLeft() + getCompoundDrawablePadding();

		for (int i = 0; i < strings.size(); i++) {
			String string = strings.get(i);
			// 判断是否需要缩略
			if (string.length() > 30) {
				string = string.substring(0, 30) + "..";
			}
			// 计算高度，如果是第一个，需要在最后也要显示出来
			if (i == 0) {
				// 如果只有一条数据，需要手动多画一次
				if (strings.size() == 1) {
					y = getMeasuredHeight() - currentHeight;
					int y1 = getMeasuredHeight() * 2 - currentHeight;
					canvas.drawText(string, x, y1, mPaint);
				} else {
					y = getMeasuredHeight() - currentHeight < 0
							? getMeasuredHeight() * (strings.size() + 1) - currentHeight
							: getMeasuredHeight() - currentHeight;
				}
			} else {
				y = getMeasuredHeight() * (i + 1) - currentHeight;
			}
			canvas.drawText(string, x, y, mPaint);

			if (stopable) {
				setShouldStop(i);
			}
		}

		// 控制延时操作，需要停留就停留。
		new Handler().postDelayed(new Runnable() {
			public void run() {
				invalidate();
			}
		}, showStop ? stopTime : scrollTime);
		showStop = false;
	}

	/***
	 * 
	 * @Description 判断是否停留
	 * @author Daniel
	 * @param i
	 */
	private void setShouldStop(int i) {
		int y = getMeasuredHeight() * (i + 1) - currentHeight;
		int yOff = getMeasuredHeight() - (int) ((getMeasuredHeight() - getTextSize() / 2) / 2 - 3);
		if (y == yOff) {
			showStop = true;
			setCurrentPosition(i);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 在计算完毕时设置一个初始值
		currentHeight = (int) ((getMeasuredHeight() - getTextSize() + 3) / 2);
	}

	/***
	 * 
	 * @Description 设置滚动字串列表
	 * @param strings
	 * @author Daniel
	 */
	public void setStrings(List<String> strings) {
		this.strings = strings;
		invalidate();
	}

	/***
	 * 
	 * @Description 设置是否需要滚到内容停止2S
	 * @param stopable
	 * @author Daniel
	 */
	public void setStopable(boolean stopable) {
		this.stopable = stopable;
	}

	public boolean isRollable() {
		return rollable;
	}

	/***
	 * 
	 * @Description 设置是否需要滚动
	 * @return
	 * @author Daniel
	 */
	public void setRollable(boolean rollable) {
		this.rollable = rollable;
	}

	public int getStopTime() {
		return stopTime;
	}

	/***
	 * 
	 * @Description 设置停留时间，默认2000ms
	 * @param stopTime
	 * @author Daniel
	 */
	public void setStopTime(int stopTime) {
		this.stopTime = stopTime;
	}

	public int getScrollTime() {
		return scrollTime;
	}

	/***
	 * 
	 * @Description 设置滚动间隔，默认0ms，建议100ms以内
	 * @param scrollTime
	 * @author Daniel
	 */
	public void setScrollTime(int scrollTime) {
		this.scrollTime = scrollTime;
	}

	/***
	 * 
	 * @Description 获取当前位置
	 * @return
	 * @author Daniel
	 */
	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}

}
