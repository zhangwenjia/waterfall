package com.dodowaterfall.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dodowaterfall.R;

public class MyFlowView extends RelativeLayout{

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
}
