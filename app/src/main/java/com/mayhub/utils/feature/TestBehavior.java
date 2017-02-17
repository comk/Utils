package com.mayhub.utils.feature;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by comkdai on 2016/12/15.
 */
public class TestBehavior extends CoordinatorLayout.Behavior<TextView> {

    private static final String TAG = "TestBehavior";

    public TestBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams params) {
//        super.onAttachedToLayoutParams(params);
//    }
//
//    @Override
//    public void onDetachedFromLayoutParams() {
//        super.onDetachedFromLayoutParams();
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
//        Log.d(TAG, "onInterceptTouchEvent() called with: " + "parent = [" + parent + "], child = [" + child + "], ev = [" + ev + "]");
//        return super.onInterceptTouchEvent(parent, child, ev);
//    }
//
//    @Override
//    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
//        Log.d(TAG, "onTouchEvent() called with: " + "parent = [" + parent + "], child = [" + child + "], ev = [" + ev + "]");
//        return super.onTouchEvent(parent, child, ev);
//    }
//
//    @Override
//    public int getScrimColor(CoordinatorLayout parent, View child) {
//        return super.getScrimColor(parent, child);
//    }
//
//    @Override
//    public float getScrimOpacity(CoordinatorLayout parent, View child) {
//        return super.getScrimOpacity(parent, child);
//    }
//
//    @Override
//    public boolean blocksInteractionBelow(CoordinatorLayout parent, View child) {
//        Log.d(TAG, "blocksInteractionBelow() called with: " + "parent = [" + parent + "], child = [" + child + "]");
//        return super.blocksInteractionBelow(parent, child);
//    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        Log.e(TAG, "layoutDependsOn() called with: " + "parent = [" + parent + "], child = [" + child + "], dependency = [" + dependency + "]");
        return dependency instanceof NestedScrollView;
//        return false;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {
        Log.e(TAG, "onDependentViewChanged() called with: " + "getScrollY = [" + dependency.getScrollY() + "]");
//        if(child.getBottom() <= 0){
//            return false;
//        }else{
//            ViewCompat.offsetTopAndBottom(child, dependency.getScrollY());
        if(Math.abs(dependency.getScrollY()) <= child.getMeasuredHeight()) {
            ViewCompat.setTranslationY(child, -dependency.getScrollY());
            return true;
        }
        return false;
//        }
    }

//    @Override
//    public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {
//        super.onDependentViewRemoved(parent, child, dependency);
//    }
//
//    @Override
//    public boolean isDirty(CoordinatorLayout parent, View child) {
//        return super.isDirty(parent, child);
//    }
//
//    @Override
//    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
//        Log.d(TAG, "onMeasureChild() called with: " + "parent = [" + parent + "], child = [" + child + "], parentWidthMeasureSpec = [" + parentWidthMeasureSpec + "], widthUsed = [" + widthUsed + "], parentHeightMeasureSpec = [" + parentHeightMeasureSpec + "], heightUsed = [" + heightUsed + "]");
//        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
//    }
//
//    @Override
//    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
//        Log.d(TAG, "onLayoutChild() called with: " + "parent = [" + parent + "], child = [" + child + "], layoutDirection = [" + layoutDirection + "]");
//        return super.onLayoutChild(parent, child, layoutDirection);
//    }
//
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, TextView child, View directTargetChild, View target, int nestedScrollAxes) {
        Log.e(TAG, "onStartNestedScroll() called with: " + "coordinatorLayout = [" + coordinatorLayout + "], child = [" + child + "], directTargetChild = [" + directTargetChild + "], target = [" + target + "], nestedScrollAxes = [" + nestedScrollAxes + "]");
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }
//
    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, TextView child, View directTargetChild, View target, int nestedScrollAxes) {
        Log.e(TAG, "onNestedScrollAccepted() called with: " + "coordinatorLayout = [" + coordinatorLayout + "], child = [" + child + "], directTargetChild = [" + directTargetChild + "], target = [" + target + "], nestedScrollAxes = [" + nestedScrollAxes + "]");
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, TextView child, View target) {
        Log.e(TAG, "onStopNestedScroll() called with: " + "coordinatorLayout = [" + coordinatorLayout + "], child = [" + child + "], target = [" + target + "]");
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }
//
    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, TextView child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.e(TAG, "onNestedScroll() called with: " + "coordinatorLayout = [" + coordinatorLayout + "], child = [" + child + "], target = [" + target + "], dxConsumed = [" + dxConsumed + "], dyConsumed = [" + dyConsumed + "], dxUnconsumed = [" + dxUnconsumed + "], dyUnconsumed = [" + dyUnconsumed + "]");
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, TextView child, View target, int dx, int dy, int[] consumed) {
        Log.e(TAG, "onNestedPreScroll() called with: " + "coordinatorLayout = [" + coordinatorLayout + "], child = [" + child + "], target = [" + target + "], dx = [" + dx + "], dy = [" + dy + "], consumed = [" + consumed + "]");
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, TextView child, View target, float velocityX, float velocityY, boolean consumed) {
        Log.e(TAG, "onNestedFling() called with: " + "coordinatorLayout = [" + coordinatorLayout + "], child = [" + child + "], target = [" + target + "], velocityX = [" + velocityX + "], velocityY = [" + velocityY + "], consumed = [" + consumed + "]");
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }
//
//    @Override
//    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
//        Log.d(TAG, "onNestedPreFling() called with: " + "coordinatorLayout = [" + coordinatorLayout + "], child = [" + child + "], target = [" + target + "], velocityX = [" + velocityX + "], velocityY = [" + velocityY + "]");
//        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
//    }
//
//    @NonNull
//    @Override
//    public WindowInsetsCompat onApplyWindowInsets(CoordinatorLayout coordinatorLayout, View child, WindowInsetsCompat insets) {
//        return super.onApplyWindowInsets(coordinatorLayout, child, insets);
//    }
//
//    @Override
//    public boolean onRequestChildRectangleOnScreen(CoordinatorLayout coordinatorLayout, View child, Rect rectangle, boolean immediate) {
//        Log.d(TAG, "onRequestChildRectangleOnScreen() called with: " + "coordinatorLayout = [" + coordinatorLayout + "], child = [" + child + "], rectangle = [" + rectangle + "], immediate = [" + immediate + "]");
//        return super.onRequestChildRectangleOnScreen(coordinatorLayout, child, rectangle, immediate);
//    }
//
//    @Override
//    public void onRestoreInstanceState(CoordinatorLayout parent, View child, Parcelable state) {
//        super.onRestoreInstanceState(parent, child, state);
//    }
//
//    @Override
//    public Parcelable onSaveInstanceState(CoordinatorLayout parent, View child) {
//        return super.onSaveInstanceState(parent, child);
//    }
}
