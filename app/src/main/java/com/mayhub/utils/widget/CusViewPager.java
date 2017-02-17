package com.mayhub.utils.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.mayhub.utils.adapter.InfiniteBasePagerAdapter;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016/6/22.
 */
public class CusViewPager extends ViewPager {

    private int autoSlideDuration = 3000;

    private int totalChildCount;

    private boolean isAttachToWindow = false;

    private boolean isAutoSlideEnable = false;

    private boolean isTouched = false;

    private Runnable autoSlideRun = new Runnable() {
        @Override
        public void run() {
            if(checkAutoSlideShouldWork()) {
                setCurrentItem(getNextItemIndex(), true);
            }
            postDelayed(this, autoSlideDuration);
        }
    };

    public CusViewPager(Context context) {
        super(context);
    }

    public CusViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCusScroller(FixedPagerScroller fixedPagerScroller){
        if(fixedPagerScroller != null) {
            // 利用反射修改ViewPager滚动时的速度
            try {
                Field mScroller;
                mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                mScroller.set(this, fixedPagerScroller);
            } catch (NoSuchFieldException e) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
    }

    public void setAutoSlideDuration(final int autoSlideDuration1) {
        autoSlideDuration = autoSlideDuration1;
    }

    public void setPagerTransitionDuration(final int transitionDuration){
        setCusScroller(new FixedPagerScroller(getContext(),new DecelerateInterpolator()) {
            @Override
            public int getCusDuration() {
                return transitionDuration;
            }
        });
    }

    public void setDefaultFixedScroller(){
        setCusScroller(new FixedPagerScroller(getContext(),new DecelerateInterpolator()) {
            @Override
            public int getCusDuration() {
                return 800;
            }
        });
    }

    private boolean checkAutoSlideShouldWork(){
        return getVisibility() == View.VISIBLE && isAttachToWindow && getAdapter() != null && !isTouched;
    }

    public void startAutoSlide(){
        if(getAdapter() != null){
            if (getTotalChildCount() > 1) {
                isAutoSlideEnable = true;
                post(autoSlideRun);
            }
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        totalChildCount = 0;
    }

    public boolean isAutoSlideEnable(){
        return isAutoSlideEnable;
    }

    public int getNextItemIndex(){
        if(getAdapter() != null) {
            int nextItemIndex = getCurrentItem() + 1;
            return nextItemIndex < getAdapter().getCount() ? nextItemIndex : 0;
        }
        return 0;
    }

    public void stopAutoSlide(){
        isAutoSlideEnable = false;
        removeCallbacks(autoSlideRun);
    }

    private int getTotalChildCount(){
        if(totalChildCount == 0 && getAdapter() != null) {
            if (getAdapter() instanceof InfiniteBasePagerAdapter) {
                InfiniteBasePagerAdapter infiniteBasePagerAdapter = (InfiniteBasePagerAdapter) getAdapter();
                totalChildCount = infiniteBasePagerAdapter.getRealCount();
            } else {
                totalChildCount = getAdapter().getCount();
            }
        }
        return totalChildCount;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachToWindow = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachToWindow = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        action = action & MotionEventCompat.ACTION_MASK;
        if(action == MotionEvent.ACTION_DOWN){
            isTouched = true;
        }else if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL){
            isTouched = false;
        }
        return super.dispatchTouchEvent(ev);
    }

    static abstract class FixedPagerScroller extends Scroller {

        public FixedPagerScroller(Context context) {
            super(context);
        }

        public FixedPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, getCusDuration());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, getCusDuration());
        }

        public abstract int getCusDuration();

    }

}
