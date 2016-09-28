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
import android.view.View;



public class PopView extends View {

	public static final int HEIGHT = 200;

	public static final int WIDTH = 400;

	public static final int ARROW_HEIGHT = 30;

	private ShapeDrawable drawable;

	private Bitmap mBitmap;
	
	private Paint mPaint = new Paint();
	
	private int parentWidth;
	
	private int screenX;
	
	private int arrowHeight;
	
	private int width;
	
	private int height;

	private Rect mRect;

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
		mRect = new Rect(width-(arrowHeight/2), height*2-(arrowHeight/2), width+(arrowHeight/2), height*2+(arrowHeight/2));
	}

	public PopView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public PopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void updateView(Bitmap bitmap, int screenPosX, int pWidth){
		mBitmap = bitmap;
		screenX = screenPosX;
		this.parentWidth = pWidth;
		mPaint.setColor(Color.BLUE);
		invalidate();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
			if(screenX < width){
				canvas.translate(Math.max(screenX - width,arrowHeight - width), 0);
			}else if(screenX > parentWidth - width){
				canvas.translate(Math.min(screenX - parentWidth + width,width - arrowHeight), 0);
			}
			canvas.rotate(45,width,height*2);
			canvas.drawRect(mRect, mPaint);
			canvas.restore();
			drawable.draw(canvas);
//			canvas.clipRect(new Rect(width-(arrowHeight/2), height*2-(arrowHeight/2), width+(arrowHeight/2), height*2+(arrowHeight/2)));
		}
	}
	
}
