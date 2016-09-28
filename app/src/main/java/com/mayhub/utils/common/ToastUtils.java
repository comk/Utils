package com.mayhub.utils.common;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtils {

	private static volatile ToastUtils instance;

	private Toast mToast;

	private ToastUtils(){

	}

	public static ToastUtils getInstance(){
		if(instance == null){
			synchronized (ToastUtils.class) {
				if(instance == null){
					instance = new ToastUtils();
				}
			}
		}
		return instance;
	}

	public void showLongToast(Context context, String text){
		if(context == null || text == null){
			return;
		}
		if(Looper.getMainLooper().getThread() == Thread.currentThread()) {
			if (mToast != null) {
				mToast.cancel();
			}
			mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
			mToast.show();
		}
	}

	public void showShortToast(Context context, String text){
		if(context == null || text == null){
			return;
		}
		if(Looper.getMainLooper().getThread() == Thread.currentThread()){
			if(mToast != null){
				mToast.cancel();
			}
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
			mToast.show();
		}
	}

	public void showLongToast(Context context, int resID){
		if(context == null){
			return;
		}
		if(Looper.getMainLooper().getThread() == Thread.currentThread()) {
			if (mToast != null) {
				mToast.cancel();
			}
			mToast = Toast.makeText(context, resID, Toast.LENGTH_LONG);
			mToast.show();
		}
	}

	public void showShortToast(Context context, int resID){
		if(context == null){
			return;
		}
		if(Looper.getMainLooper().getThread() == Thread.currentThread()) {
			if (mToast != null) {
				mToast.cancel();
			}
			mToast = Toast.makeText(context, resID, Toast.LENGTH_SHORT);
			mToast.show();
		}
	}
}
