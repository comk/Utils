package com.mayhub.utils;

import android.graphics.Color;
import android.os.Bundle;

import com.mayhub.utils.activity.BaseTabActivity;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by comkdai on 2017/5/4.
 */
public class TestTabActivity extends BaseTabActivity {

    Class[] clazz = new Class[]{TestFragment1.class, TestFragment2.class, TestFragment3.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public ArrayList<Class> getPageFragments() {
        return new ArrayList(Arrays.asList(clazz));
    }

    @Override
    public ArrayList<TabItemInfo> getTabItemInfo() {
        ArrayList<TabItemInfo> item = new ArrayList<>();
        for (int i = 0; i < clazz.length; i++) {
            item.add(new TabItemInfo("tab " + (i + 1), R.drawable.selector_tab_icon));
        }
        return item;
    }

    @Override
    public int getDividerColor() {
        return Color.GRAY;
    }

    @Override
    public int getTextColorListResId() {
        return R.color.state_color_list;
    }
}
