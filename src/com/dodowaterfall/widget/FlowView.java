package com.dodowaterfall.widget;

import java.io.BufferedInputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

// 每一张的图片view
public class FlowView extends ImageView implements View.OnClickListener, View.OnLongClickListener {

	private final String LOGTAG = "mylog";
	
	private Context context;
	public Bitmap bitmap;
	private Handler viewHandler; // 图片加载的回调
	private String fileName;
	private AssetManager assetManager;
	
	private int flowId;
	private int columnIndex;// 图片属于第几列
	private int rowIndex;// 图片属于第几行
	private int itemWidth;

	public final int what = 1;

	public FlowView(Context c, AttributeSet attrs, int defStyle) {
		super(c, attrs, defStyle);
		this.context = c;
		Init();
	}

	public FlowView(Context c, AttributeSet attrs) {
		super(c, attrs);
		this.context = c;
		Init();
	}

	public FlowView(Context c) {
		super(c);
		this.context = c;
		Init();
	}

	public int getFlowId() {
		return flowId;
	}

	public void setFlowId(int flowId) {
		this.flowId = flowId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getItemWidth() {
		return itemWidth;
	}

	public void setItemWidth(int itemWidth) {
		this.itemWidth = itemWidth;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public Handler getViewHandler() {
		return viewHandler;
	}

	public FlowView setViewHandler(Handler viewHandler) {
		this.viewHandler = viewHandler;
		return this;
	}
	
	public AssetManager getAssetManager() {
		return assetManager;
	}

	public void setAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	private void Init() {
		setOnClickListener(this);
		setOnLongClickListener(this);
		setAdjustViewBounds(true);
	}

	@Override
	public boolean onLongClick(View v) {
		Toast.makeText(context, 
				"长按 " + 
				"flowId is:" + flowId + "|" +
				"fileName is:"+ fileName + "|" +
				"itemWidth is:" + itemWidth + "|" +
				"columnIndex is:" + columnIndex + "|" +
				"rowIndex is:" + rowIndex + "|"
				, Toast.LENGTH_SHORT).show();
		
		Log.d(LOGTAG , 
				"长按 "+
				"flowId is:" + flowId + "|" +
				"fileName is:"+ fileName + "|" +
				"itemWidth is:" + itemWidth + "|" +
				"columnIndex is:" + columnIndex + "|" +
				"rowIndex is:" + rowIndex + "|");
		
		return true;
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(context, 
				"单击：" + 
				"flowId is:" + flowId + "|" +
				"fileName is:"+ fileName + "|" +
				"itemWidth is:" + itemWidth + "|" +
				"columnIndex is:" + columnIndex + "|" +
				"rowIndex is:" + rowIndex + "|", 
				Toast.LENGTH_SHORT).show();
		
		Log.d(LOGTAG , 
				"单击 "+
				"flowId is:" + flowId + "|" +
				"fileName is:"+ fileName + "|" +
				"itemWidth is:" + itemWidth + "|" +
				"columnIndex is:" + columnIndex + "|" +
				"rowIndex is:" + rowIndex + "|");
	}

	/**
	 * 加载图片
	 */
	public void loadImage() {
		new LoadImageThread().start();
	}

	/**
	 * 重新加载图片
	 */
	public void reload() {
		if (this.bitmap == null) {
			new ReloadImageThread().start();
		}
	}

	/**
	 * 回收内存
	 */
	public void recycle() {
		setImageBitmap(null);
		if ((this.bitmap == null) || (this.bitmap.isRecycled()))
			return;
		this.bitmap.recycle();
		this.bitmap = null;
	}

	class ReloadImageThread extends Thread {
		@Override
		public void run() {
			BufferedInputStream buf;
			try {
				buf = new BufferedInputStream(getAssetManager().open(fileName));
				bitmap = BitmapFactory.decodeStream(buf);
			} catch (IOException e) {
				e.printStackTrace();
			}

			((Activity) context).runOnUiThread(new Runnable() {
				public void run() {
					if (bitmap != null) {
						setImageBitmap(bitmap);
					}
				}
			});
		}
	}

	class LoadImageThread extends Thread {
		LoadImageThread() {
		}

		public void run() {
			BufferedInputStream buf;
			try {
				buf = new BufferedInputStream(getAssetManager().open(fileName));
				bitmap = BitmapFactory.decodeStream(buf);
			} catch (IOException e) {
				e.printStackTrace();
			}

			((Activity) context).runOnUiThread(new Runnable() {
				public void run() {
					if (bitmap != null) {
						int width = bitmap.getWidth();// 照片的原始宽高
						int height = bitmap.getHeight();

						LayoutParams lp = getLayoutParams();
						int layoutHeight = (height * itemWidth) / width;// 调整高度。根据给定的宽度，调整照片的高度
						if (lp == null) {
							lp = new LayoutParams(itemWidth, layoutHeight);
						}
						setLayoutParams(lp);
						setImageBitmap(bitmap);
						Handler h = getViewHandler();
						Message m = h.obtainMessage(what, width, layoutHeight, FlowView.this);
						h.sendMessage(m);
					}
				}
			});
		}
	}
}
