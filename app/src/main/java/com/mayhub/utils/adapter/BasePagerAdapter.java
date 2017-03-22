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
public abstract class BasePagerAdapter<T> extends PagerAdapter implements View.OnClickListener{

    public interface PagerItemClickListener<T>{
        void onItemClick(int pos, T t);
    }

    private PagerItemClickListener pagerItemClickListener;

    private List<T> datas = new ArrayList<>();

    private Stack<View> cacheViews = new Stack<>();

    public BasePagerAdapter(List<T> data){
        if(data != null){
            datas.addAll(data);
        }
    }

    public BasePagerAdapter(List<T> data, PagerItemClickListener pagerItemClickListener){
        this.pagerItemClickListener = pagerItemClickListener;
        if(data != null){
            datas.addAll(data);
        }
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setPagerItemClickListener(PagerItemClickListener pagerItemClickListener){
        this.pagerItemClickListener = pagerItemClickListener;
    }

    public PagerItemClickListener getPagerItemClickListener(){
        return pagerItemClickListener;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final View rootView;
        if(cacheViews.isEmpty()) {
            rootView = createView(container, position);
            if(pagerItemClickListener != null) {
                rootView.setOnClickListener(this);
            }
        }else{
            rootView = cacheViews.pop();
        }
        if(pagerItemClickListener != null){
            rootView.setTag(rootView.getId(), position);
        }else{
            rootView.setOnClickListener(null);
        }
        container.addView(rootView);
        bindView(rootView, position);
        return rootView;
    }

    public abstract View createView(ViewGroup container, int position);

    public abstract void bindView(View bindView, int position);

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        cacheViews.push((View) object);
    }

    @Override
    public void onClick(View v) {
        if(v.getTag(v.getId()) != null && v.getTag(v.getId()) instanceof Integer){
            int pos = (int) v.getTag(v.getId());
            if(pagerItemClickListener != null){
                pagerItemClickListener.onItemClick(pos, datas.get(pos));
            }
        }
    }
}
