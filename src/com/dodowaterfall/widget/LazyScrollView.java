package com.dodowaterfall.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class LazyScrollView extends ScrollView {

	private View view;

	public LazyScrollView(Context context) {
		super(context);
	}

	public LazyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LazyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		myOnScrollListener.onAutoScroll(l, t, oldl, oldt);// 只要scrollview一改变就调用autoscroll，不是因为ontouch才调用
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				// scrollview的高度就是屏幕的高度，不变
				// scrollview里面的view的高度是总的高度，越来越高
				//getScrollY的值是scrollview里面的view滚出屏幕上方距离屏幕的距离
				
				if (view.getMeasuredHeight() - 20 <= getScrollY() + getHeight()) {
					if (myOnScrollListener != null) {
						myOnScrollListener.onBottom();
					}
				} else if (getScrollY() == 0) { // getScrollY当前的scrollview的最上方的坐标
					if (myOnScrollListener != null) {
						myOnScrollListener.onTop();
					}
				} else {
					if (myOnScrollListener != null) {
						myOnScrollListener.onScroll();
					}
				}
				break;
			default:
				break;
			}
		}
	};;

	OnTouchListener onTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP: // 当滑动抬起的时候才触发
				if (view != null && myOnScrollListener != null) {
					handler.sendMessageDelayed(handler.obtainMessage(1), 200);
				}
				break;
			default:
				break;
			}
			return false;
		}

	};

	/**
	 * 获得参考的View，主要是为了获得它的MeasuredHeight，然后和滚动条的ScrollY+getHeight作比较。
	 */
	public void getView() {
		this.view = getChildAt(0);// 获得scrollview里的容器view，就是LinearLayout
		if (view != null) {
			setOnTouchListener(onTouchListener);
		}
	}

	public interface MyOnScrollListener {
		void onBottom();

		void onTop();

		void onScroll();

		void onAutoScroll(int l, int t, int oldl, int oldt);
	}

	private MyOnScrollListener myOnScrollListener;

	public void setOnScrollListener(MyOnScrollListener onScrollListener) {
		this.myOnScrollListener = onScrollListener;
	}

}
