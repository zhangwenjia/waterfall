package com.dodowaterfall.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dodowaterfall.R;
import com.dodowaterfall.widget.FlowView;
import com.dodowaterfall.widget.LazyScrollView;
import com.dodowaterfall.widget.LazyScrollView.MyOnScrollListener;
import com.dodowaterfall.widget.MyFlowView;

public class MainActivity extends Activity {

	private LazyScrollView waterfall_scroll; // 最外层的scrollview
	private LinearLayout waterfall_container; // 最外层scrollview里的容器view
	
	private ArrayList<LinearLayout> waterfall_items = new ArrayList<LinearLayout>();; // 平分布局的list
	private List<String> image_filenames; // 存放图片名称的list
	
	private int item_width; // 每列宽度
	private int current_page = 0;// 当前页数
	private int scroll_height; // scorllView 高度
	private int loaded_count = 0;// 已加载数量

	private int[] topIndex; // 所在列表中显示图片最顶部位置
	private int[] bottomIndex; // 所在列表中显示图片最底部位置
	private int[] lineIndex; //每列的linearlayout布局中，有几张图片，编号从0开始
	private int[] column_height;// 每列的高度。不断累加的

	private HashMap<Integer, Integer>[] pin_mark = null;

	private final String IMAGE_PATH = "images";
	private final int COLUMN_COUNT = 4; // 显示列数
	private final int PAGE_COUNT = 10;// 每次加载10张图片
	
	private Display display;
	private AssetManager asset_manager;
	private Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		
		display = this.getWindowManager().getDefaultDisplay();
		item_width = display.getWidth() / COLUMN_COUNT;// 根据屏幕大小计算每列宽度，宽度是定死的。每张照片的高度是可变的
		asset_manager = this.getAssets();

		column_height = new int[COLUMN_COUNT];
		context = this;
		pin_mark = new HashMap[COLUMN_COUNT];

		this.lineIndex = new int[COLUMN_COUNT];
		this.bottomIndex = new int[COLUMN_COUNT];
		this.topIndex = new int[COLUMN_COUNT];

		for (int i = 0; i < COLUMN_COUNT; i++) {
			lineIndex[i] = -1;
			bottomIndex[i] = -1;
			pin_mark[i] = new HashMap();
		}

		InitLayout();

	}

	MyOnScrollListener myOnScrollListener = new MyOnScrollListener() {
		@Override
		public void onTop() {
			// 滚动到最顶端
			Toast.makeText(context, "Scroll to top", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onScroll() {
		}

		@Override
		public void onBottom() {
			// 滚动到最低端
			AddItemToContainer( ++current_page, PAGE_COUNT);
			Toast.makeText(context, "Scroll to bottom", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onAutoScroll(int horizontal, int vertical, int oldhorizontal, int oldvertical) {
			
			scroll_height = waterfall_scroll.getMeasuredHeight();
			
			Log.d("mylog","vertical+++" + vertical);
			
			if (vertical > oldvertical) {// 向下滚动
				if (vertical > 2 * scroll_height) {// 超过两屏幕后
					for (int k = 0; k < COLUMN_COUNT; k++) {
						LinearLayout localLinearLayout = waterfall_items.get(k); // 按个取linearLayout
						// 重新加载
						if (pin_mark[k].get(Math.min(bottomIndex[k] + 1, lineIndex[k])) <= vertical + 3 * scroll_height) {// 最底部的图片位置小于当前t+3*屏幕高度
							((MyFlowView) waterfall_items.get(k).getChildAt(Math.min(1 + bottomIndex[k], lineIndex[k]))).reload();
							bottomIndex[k] = Math.min(1 + bottomIndex[k], lineIndex[k]);
						}
						// 回收图片
						if (pin_mark[k].get(topIndex[k]) < vertical - 2 * scroll_height) {// 未回收图片的最高位置<t-两倍屏幕高度
							int i1 = topIndex[k];
							topIndex[k]++;
							((MyFlowView) localLinearLayout.getChildAt(i1)).recycle();// 回收到第几张图片
						}
					}
				}
			} else {// 向上滚动
				if (vertical > 2 * scroll_height) {// 超过两屏幕后
					for (int k = 0; k < COLUMN_COUNT; k++) {
						LinearLayout localLinearLayout = waterfall_items.get(k);
						// 回收图片
						if (pin_mark[k].get(bottomIndex[k]) > vertical + 3 * scroll_height) {
							((MyFlowView) localLinearLayout.getChildAt(bottomIndex[k])).recycle();
							bottomIndex[k]--;
						}
						// 重新加载
						if (pin_mark[k].get(Math.max(topIndex[k] - 1, 0)) >= vertical - 2 * scroll_height) {
							((MyFlowView) localLinearLayout.getChildAt(Math.max(-1 + topIndex[k], 0))).reload();
							topIndex[k] = Math.max(topIndex[k] - 1, 0);
						}
					}
				}
			}
		}
	}; 
	
	private Handler handler = new Handler() {
		
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				MyFlowView photo = (MyFlowView) msg.obj;
				int h = msg.arg2; // 调整后的高
				String fileName = photo.getFileName();

				int columnIndex = GetMinValue(column_height);// 返回同一行中,哪个图片的告诉最小的index值.那么当前要加载的这张图片的列数就是几

				photo.setColumnIndex(columnIndex);

				column_height[columnIndex] = column_height[columnIndex] + h; 
				waterfall_items.get(columnIndex).addView(photo); // 把图片view加载到对应的linearlayout中

				lineIndex[columnIndex]++; //对应列的linearlayout的图片数加1
				pin_mark[columnIndex].put(lineIndex[columnIndex], column_height[columnIndex]);
				bottomIndex[columnIndex] = lineIndex[columnIndex];
				
				break;
			}
		}

		@Override
		public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
			return super.sendMessageAtTime(msg, uptimeMillis);
		}
	};
	
	private void InitLayout() {
		waterfall_container = (LinearLayout) findViewById(R.id.waterfall_container);
		waterfall_scroll = (LazyScrollView) findViewById(R.id.waterfall_scroll);
		waterfall_scroll.getView();
		waterfall_scroll.setOnScrollListener(myOnScrollListener);

		// 生成column_count个平分屏幕宽度的LinearLayout
		for (int i = 0; i < COLUMN_COUNT; i++) {
			LinearLayout itemLayout = new LinearLayout(this);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(item_width, LayoutParams.FILL_PARENT);

			itemLayout.setPadding(5, 5, 5, 5);
			itemLayout.setOrientation(LinearLayout.VERTICAL);

			itemLayout.setLayoutParams(itemParam);
			waterfall_items.add(itemLayout);
			waterfall_container.addView(itemLayout);
		}

		// 加载所有图片路径
		try {
			image_filenames = Arrays.asList(asset_manager.list(IMAGE_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 第一次加载
		AddItemToContainer(current_page, PAGE_COUNT);
	}

	private void AddItemToContainer(int pageindex, int pagecount) {
		int currentIndex = pageindex * pagecount;
		int maxImagecount = 10000;
		
		for (int i = currentIndex; i < pagecount * (pageindex + 1) && i < maxImagecount; i++) {
			loaded_count++; // 已加载数量
			Random rand = new Random();
			int r = rand.nextInt(image_filenames.size());// 生成一个随机数,从list里随机去图片名称
			AddImage(image_filenames.get(r), (int) Math.ceil(loaded_count / (double) COLUMN_COUNT), loaded_count);
		}
	}

	private void AddImage(String filename, int rowIndex, int id) {
		MyFlowView item = new MyFlowView(context);
		
		item.setRowIndex(rowIndex);
		item.setId(id);
		item.setFlowId(id);
		item.setViewHandler(handler);
		item.setAssetManager(asset_manager);
		item.setFileName(IMAGE_PATH + "/" + filename);
		item.setItemWidth(item_width);
		item.loadImage();
	}

	private int GetMinValue(int[] array) {
		int m = 0;
		int length = array.length;
		for (int i = 0; i < length; ++i) {
			if (array[i] < array[m]) {
				m = i;
			}
		}
		return m;
	}
}
