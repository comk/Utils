package com.mayhub.utils.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.mayhub.utils.common.FindTextViewUtils;
import com.mayhub.utils.common.SelectionViewUtils;


/**
 * Created by comkdai on 2017/6/2.
 */
public abstract class BaseTextSelectionActivity extends BaseTabActivity implements GestureDetector.OnGestureListener{

    private GestureDetector gestureDetector = new GestureDetector(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract View getCurrentVisibleView();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return gestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        TextView textView = FindTextViewUtils.findTextViewByPosition(e, getCurrentVisibleView());
        if(textView != null){
            SelectionViewUtils.getInstance().showSelection(BaseTextSelectionActivity.this, textView, e.getRawX(), e.getRawY());
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
