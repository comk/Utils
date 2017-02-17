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

    private boolean isInfiniteEnable = true;

    public InfiniteBasePagerAdapter(List data) {
        super(data);
        if(data != null && data.size() == 1){
            isInfiniteEnable = false;
        }
    }

    public InfiniteBasePagerAdapter(List data, PagerItemClickListener pagerItemClickListener) {
        super(data, pagerItemClickListener);
        if(data != null && data.size() == 1){
            isInfiniteEnable = false;
        }
    }

    @Override
    public int getCount() {
        return isInfiniteEnable ? Integer.MAX_VALUE : 1;
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
