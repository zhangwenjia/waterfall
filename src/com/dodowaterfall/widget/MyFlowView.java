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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dodowaterfall.R;
import com.dodowaterfall.widget.FlowView.LoadImageThread;
import com.dodowaterfall.widget.FlowView.ReloadImageThread;

public class MyFlowView extends RelativeLayout implements View.OnClickListener, View.OnLongClickListener{

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

	private ImageView photo;
	private TextView distance_tv;
	private ImageView online_img;
	private RelativeLayout myFlowView;
	
	public final int what = 1;

	
	private LayoutInflater inflater;
	
	
	private void init(){
		inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		myFlowView = (RelativeLayout) inflater.inflate(R.layout.myflowview, this);
		photo = (ImageView)myFlowView.findViewById(R.id.photo);
		distance_tv = (TextView)myFlowView.findViewById(R.id.distance_tv);
		online_img = (ImageView)myFlowView.findViewById(R.id.online_img);
		
		setOnClickListener(this);
		setOnLongClickListener(this);
		photo.setAdjustViewBounds(true);
	}
	
	public MyFlowView(Context c) {
		super(c);
		this.context = c;
		
		init();
		
//		Init();
	}
	
	public MyFlowView(Context c, AttributeSet attrs) {
		super(c, attrs);
		this.context = c;
		init();
		
//		Init();
	}
	
	public MyFlowView(Context c, AttributeSet attrs, int defStyle) {
		super(c, attrs, defStyle);
		this.context = c;
		init();
//		Init();
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

	public MyFlowView setViewHandler(Handler viewHandler) {
		this.viewHandler = viewHandler;
		return this;
	}
	
	public AssetManager getAssetManager() {
		return assetManager;
	}

	public void setAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		
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
		return false;
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
		photo.setImageBitmap(null);
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
						photo.setImageBitmap(bitmap);
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

						ViewGroup.LayoutParams lp = myFlowView.getLayoutParams();
						int layoutHeight = (height * itemWidth) / width;// 调整高度。根据给定的宽度，调整照片的高度
						if (lp == null) {
							lp = new LayoutParams(itemWidth, layoutHeight);
						}
						setLayoutParams(lp);
						photo.setImageBitmap(bitmap);
						Handler h = getViewHandler();
						Message m = h.obtainMessage(what, width, layoutHeight, MyFlowView.this);
						h.sendMessage(m);
					}
				}
			});
		}
	}
}
