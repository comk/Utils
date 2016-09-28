package com.mayhub.utils.test;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mayhub.utils.R;
import com.mayhub.utils.adapter.BasePagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */
public class TestBasePagerAdapter extends BasePagerAdapter<String> {
    public TestBasePagerAdapter(List<String> data) {
        super(data);
    }

    public TestBasePagerAdapter(List<String> data, BasePagerAdapter.PagerItemClickListener pagerItemClickListener) {
        super(data, pagerItemClickListener);
    }

    @Override
    public View createView(ViewGroup container, int position) {
        return new TextView(container.getContext());
    }

    @Override
    public void bindView(View bindView, int pos) {
        TextView imageView = (TextView) bindView;
        imageView.setText("Item " + pos);
    }
}
