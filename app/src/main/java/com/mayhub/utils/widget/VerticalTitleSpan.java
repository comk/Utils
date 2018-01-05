package com.mayhub.utils.widget;

import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.text.style.UpdateAppearance;
import android.text.style.UpdateLayout;

/**
 * Created by comkdai on 2017/7/17.
 */
public class VerticalTitleSpan extends CharacterStyle implements UpdateAppearance, ParcelableSpan {


    @Override
    public void updateDrawState(TextPaint tp) {

    }

    @Override
    public int getSpanTypeId() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
