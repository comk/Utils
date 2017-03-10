package com.mayhub.utils.common;



import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 用来隐藏输入法的工具类
 * @author daihai
 *
 */
public class InputMethodTool {

	private InputMethodTool() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 隐藏键盘的静态方法
	 * @param focusContainer 包含EditText的父级容器
	 * @return 是否隐藏了键盘
	 */
	public static boolean hideInputMethod(View focusContainer){
		if(focusContainer == null){
			return false;
		}
		
		if(focusContainer instanceof ViewGroup){
			ViewGroup vg = (ViewGroup) focusContainer;
			for (int i = 0; i < vg.getChildCount(); i++) {
				if(hideInputMethod(vg.getChildAt(i)))
					break;
			}
		}else if(focusContainer instanceof EditText){
			EditText et = (EditText) focusContainer;
			if(et.isInputMethodTarget()){
				if(et.getWindowToken() != null){
					InputMethodManager imm = (InputMethodManager) focusContainer.getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					if(imm != null){
						imm.hideSoftInputFromWindow(et.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public static void setupUI(final View view, final Activity act) {
		if(view == null || act == null)
			return;
		
		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					InputMethodManager imm = (InputMethodManager) act
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm != null && act.getCurrentFocus() != null) {
						imm.hideSoftInputFromWindow(act.getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
					return false;
				}
			});
		}

		// If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {

			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				setupUI(innerView, act);
			}
		}
	}

}
