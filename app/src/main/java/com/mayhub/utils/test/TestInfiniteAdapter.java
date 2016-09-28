package com.mayhub.utils.test;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mayhub.utils.adapter.InfiniteBasePagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */
public class TestInfiniteAdapter extends InfiniteBasePagerAdapter<String> {
    public TestInfiniteAdapter(List<String> data) {
        super(data);
    }

    public TestInfiniteAdapter(List<String> data, PagerItemClickListener pagerItemClickListener) {
        super(data, pagerItemClickListener);
    }

    @Override
    public View createView(ViewGroup container, int position) {
        return new TextView(container.getContext());
    }

    @Override
    public void bindView(View bindView, int pos) {
        TextView textView = (TextView) bindView;
        textView.setText("Item " + pos);
    }
}
