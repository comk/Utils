package com.mayhub.utils.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.mayhub.utils.common.MLogUtil;

public class PopView extends View {

	public static int HEIGHT = 200;

	public static int WIDTH = 400;

	public static final int TOP = 401;
	public static final int LEFT = 402;
	public static final int RIGHT = 403;

	public static final int ARROW_HEIGHT = 30;

	private ShapeDrawable drawable;

	private int direction = TOP;

	private Bitmap mBitmap;
	
	private Paint mPaint = new Paint();
	
	private int parentWidth;
	
	private int screenX;
	
	private int arrowHeight;
	
	private int width;
	
	private int height;

	private Rect mRectTop;
	private Rect mRectLeft;
	private Rect mRectRight;
	private int len;
	public PopView(Context context) {
		super(context);
		width = WIDTH / 2;
		height = HEIGHT / 2;
		arrowHeight = ARROW_HEIGHT;
		float[] outerR = new float[] { 12, 12, 12, 12, 12, 12, 12, 12 };
		drawable = new ShapeDrawable(new RoundRectShape(outerR, null, null));
		drawable.setBounds(0, 0, width * 2,
				height * 2);
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(arrowHeight);
		mPaint.setColor(Color.BLUE);
		mRectTop = new Rect(width-(arrowHeight/2), height*2-(arrowHeight/2), width+(arrowHeight/2), height*2+(arrowHeight/2));
		mRectLeft = new Rect(width*2-(arrowHeight/2), height-(arrowHeight/2), width*2+(arrowHeight/2), height+(arrowHeight/2));
		len = (int) Math.sqrt(arrowHeight * arrowHeight / 2);
		mRectRight = new Rect(len, height-(arrowHeight/2), arrowHeight + len, height+(arrowHeight/2));
	}

	public static void initHeightWidth(Context context){
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(displayMetrics);
		WIDTH = (int) (displayMetrics.density * 180);
		HEIGHT = WIDTH / 2;
	}

	public PopView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public PopView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void updateView(Bitmap bitmap, int screenPosX, int pWidth, int dir){
		mBitmap = bitmap;
		screenX = screenPosX;
		parentWidth = pWidth;
		direction = dir;
		mPaint.setColor(Color.BLUE);
		invalidate();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private Rect setDirection(Canvas canvas){
		switch (direction){
			case TOP:
				if(screenX < width){
					canvas.translate(Math.max(screenX - width,arrowHeight - width), 0);
				}else if(screenX > parentWidth - width){
					canvas.translate(Math.min(screenX - parentWidth + width,width - arrowHeight), 0);
				}
				canvas.rotate(45,width,height*2);
				return mRectTop;
			case LEFT:
				canvas.rotate(45,width*2,height);
				return mRectLeft;
			case RIGHT:
				canvas.translate(-len/2, 0);
				canvas.rotate(45,len,height);
				return mRectRight;
		}
		return null;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(mBitmap != null){
			BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(mBitmap,
					(mBitmap.getWidth() * 2),
					(mBitmap.getHeight() * 2), true), TileMode.CLAMP,
					TileMode.CLAMP);
			drawable.getPaint().setShader(shader);
			canvas.save();
			canvas.drawRect(setDirection(canvas), mPaint);
			canvas.restore();
			if(direction == RIGHT){
				canvas.translate(len + 1, 0);
			}
			drawable.draw(canvas);
//			canvas.clipRect(new Rect(width-(arrowHeight/2), height*2-(arrowHeight/2), width+(arrowHeight/2), height*2+(arrowHeight/2)));
		}
	}
	
}
