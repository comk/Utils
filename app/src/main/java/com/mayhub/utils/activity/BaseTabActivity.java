package com.mayhub.utils.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mayhub.utils.R;
import com.mayhub.utils.adapter.TabPagerAdapter;

import java.util.ArrayList;

/**
 * Created by comkdai on 2017/5/4.
 */
public abstract class BaseTabActivity extends BaseActivity implements OnPageChangeListener, View.OnClickListener{

    private ViewPager viewPager;
    private TextView tvLastSelected;
    private ArrayList<TextView> tvs = new ArrayList<>();
    public static class TabItemInfo {

        public TabItemInfo(String itemName, int itemIconRes) {
            this.itemName = itemName;
            this.itemIconRes = itemIconRes;
        }

        public String itemName;
        public int itemIconRes;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_tab;
    }

    public abstract ArrayList<Class> getPageFragments();

    public abstract ArrayList<TabItemInfo> getTabItemInfo();

    public abstract int getDividerColor();

    public abstract int getTextColorListResId();

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tab_item){
            if(v.getTag() != null){
                int index = (int) v.getTag();
                viewPager.setCurrentItem(index);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tvLastSelected.setEnabled(true);
        tvLastSelected = tvs.get(position);
        tvLastSelected.setEnabled(false);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }



    private void initView(){
        viewPager = (ViewPager) findViewById(R.id.pager);
        View divider = findViewById(R.id.divider);
        divider.setBackgroundColor(getDividerColor());
        viewPager.setOnPageChangeListener(this);
        viewPager.setAdapter(new TabPagerAdapter(getFragmentManager(), getPageFragments()));
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_bottom);
        final ArrayList<TabItemInfo> info = getTabItemInfo();
        final LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        for (int i = 0; i < info.size(); i++) {
            View view = layoutInflater.inflate(R.layout.layout_tab_item, linearLayout, false);
            view.setOnClickListener(this);
            view.setTag(i);
            TextView tv = (TextView) view.findViewById(R.id.tv);
            tv.setCompoundDrawablesWithIntrinsicBounds(0, info.get(i).itemIconRes, 0, 0);
            tv.setText(info.get(i).itemName);
            tv.setTextColor(getResources().getColorStateList(getTextColorListResId()));
            tvs.add(tv);
            linearLayout.addView(view);
        }
        tvLastSelected = tvs.get(0);
        tvLastSelected.setEnabled(false);
    }

}
