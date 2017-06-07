package com.mayhub.utils.common;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by comkdai on 2017/6/2.
 */
public class FindTextViewUtils {

    public static TextView findTextViewByPosition(MotionEvent ev, View view){
        TextView findTextView = null;
        if(view != null) {
            if(view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {//递归查找
                    findTextView = findTextViewByPosition(ev, ((ViewGroup) view).getChildAt(i));
                    if(findTextView != null){
                        return findTextView;
                    }
                }
            }else if(view instanceof TextView){
                Rect rect = new Rect();
                view.getGlobalVisibleRect(rect);
                if(rect.contains((int)ev.getRawX(),(int)ev.getRawY())){
                    return (TextView) view;
                }
            }
        }
        return null;
    }
}
