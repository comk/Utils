package com.mayhub.utils.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Administrator on 2016/6/22.
 */
public abstract class InfiniteBasePagerAdapter<T> extends BasePagerAdapter<T>{

    public InfiniteBasePagerAdapter(List data) {
        super(data);
    }

    public InfiniteBasePagerAdapter(List data, PagerItemClickListener pagerItemClickListener) {
        super(data, pagerItemClickListener);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    public int getRealCount(){
        return getDatas().size();
    }

    private int getRealPosition(int position){
        return position % getRealCount();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, getRealPosition(position));
    }
}
