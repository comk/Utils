package com.mayhub.utils.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by comkdai on 2017/3/29.
 */
public class CusFrameLayout extends FrameLayout {

    private static final String TAG = "CusFrameLayout";

    private static final int AREA_SIZE = 100;

    public interface DragListener{
        void onDrag(float x, float y, boolean isStart);
        void dragEnd(float y, int viewId);
    }

    private View viewLeft;

    private View viewRight;

    private View dragView;

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
            rect.set(rect.left - AREA_SIZE, rect.top - AREA_SIZE, rect.right, rect.bottom + AREA_SIZE);
            if(rect.contains((int)x, (int)y)){
                dragView = viewLeft;
                return true;
            }
        }
        if(viewRight != null){
            viewRight.getHitRect(rect);
            rect.set(rect.left, rect.top - AREA_SIZE, rect.right + AREA_SIZE, rect.bottom + AREA_SIZE);
            if(rect.contains((int)x, (int)y)){
                dragView = viewRight;
                return true;
            }
        }
        dragView = null;
        return false;
    }

    private void updateViewPos(float x, float y){
        if(dragView != null){
            FrameLayout.LayoutParams layoutParams;
                layoutParams = (FrameLayout.LayoutParams) dragView.getLayoutParams();
                layoutParams.topMargin = (int) (y - dragView.getMeasuredHeight());
                layoutParams.leftMargin = (int) (x - (dragView.getMeasuredWidth() / 2));
            dragView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (dragView != null && dragListener != null && event.getAction() != MotionEvent.ACTION_DOWN) {
            if((event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP)){
                if(dragListener != null){
                    dragListener.dragEnd(event.getRawY() - viewLeft.getMeasuredHeight(), dragView.getId());
                }
                requestDisallowInterceptTouchEvent(true);
            } else {
                dragListener.onDrag(event.getRawX(), event.getRawY() - viewLeft.getMeasuredHeight(), dragView == viewLeft);
                updateViewPos(event.getRawX(), event.getRawY());
            }
        }else  {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return ev.getAction() == MotionEvent.ACTION_DOWN && isPressedDrag(ev.getRawX(), ev.getRawY()) || super.onInterceptTouchEvent(ev);
    }
}
