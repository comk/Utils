package com.mayhub.utils.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;



public class AmplifyPopWindow extends PopupWindow {
	PopView iv;


	public AmplifyPopWindow(Context context) {
		this.setBackgroundDrawable(new BitmapDrawable());
		iv = new PopView(context);
		this.setContentView(iv);
		this.setWidth(PopView.WIDTH);
		this.setHeight(PopView.HEIGHT + PopView.ARROW_HEIGHT);
		this.setOutsideTouchable(false);
	}
	
	public void showViewAtLocation(View parent, int xoffset, int yoffset, Bitmap bitmap, int screenX, int parentWidth){
		iv.updateView(bitmap, screenX,parentWidth);
		if(isShowing()){
			update(xoffset, yoffset, -1, -1);
		}else{
			showAtLocation(parent, Gravity.TOP, xoffset, yoffset);
		}
	}
}
