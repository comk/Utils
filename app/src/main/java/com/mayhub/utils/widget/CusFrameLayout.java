package com.mayhub.utils.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by comkdai on 2017/3/29.
 */
public class CusFrameLayout extends FrameLayout {

    public interface DragListener{
        void onDrag(float x, float y, boolean isFromStart);
    }

    private View viewLeft;

    private View viewRight;

    private boolean isFromStart = false;

    private Rect rect = new Rect();

    private DragListener dragListener;

    public CusFrameLayout(Context context) {
        super(context);
    }

    public CusFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CusFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DragListener getDragListener() {
        return dragListener;
    }

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    public void setViewLeft(View viewLeft) {
        this.viewLeft = viewLeft;
    }

    public void setViewRight(View viewRight) {
        this.viewRight = viewRight;
    }

    private boolean isPressedDrag(float x, float y){
        if(viewLeft != null){
            viewLeft.getHitRect(rect);
            if(rect.contains((int)x, (int)y)){
                isFromStart = true;
                return true;
            }
        }
        if(viewRight != null){
            viewRight.getHitRect(rect);
            if(rect.contains((int)x, (int)y)){
                isFromStart = false;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(dragListener != null && event.getAction() != MotionEvent.ACTION_DOWN ){
            dragListener.onDrag(event.getRawX(), event.getRawY() - viewLeft.getMeasuredHeight(), isFromStart);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN && isPressedDrag(ev.getRawX(), ev.getRawY())){
            return true;
        }else {
            return super.onInterceptTouchEvent(ev);
        }
    }
}
