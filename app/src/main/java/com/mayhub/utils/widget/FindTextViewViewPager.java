package com.mayhub.utils.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by comkdai on 2017/6/6.
 */
public class FindTextViewViewPager extends ViewPager {
    public FindTextViewViewPager(Context context) {
        super(context);
    }

    public FindTextViewViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        super.setOnPageChangeListener(listener);
    }

}
