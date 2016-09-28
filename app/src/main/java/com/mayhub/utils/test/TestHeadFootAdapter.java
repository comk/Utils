package com.mayhub.utils.test;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mayhub.utils.R;
import com.mayhub.utils.adapter.LoadMoreRecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/16.
 */
public class TestHeadFootAdapter extends LoadMoreRecyclerAdapter {


    public ArrayList<String> datas = new ArrayList<>();
    String text = "Groundwater is the word used to describe water that saturates the ground, filling all the available spaces.";
    String text2 = "By far the most abundant type of groundwater is meteoric water; this is the groundwater that circulates as part of the water cycle.";
    String text3 = "Ordinary meteoric water is water that has soaked into the ground from the surface, from precipitation (rain and snow) and from lakes and streams.";
    String text4 = "There it remains, sometimes for long periods, before emerging at the surface again.";
    String text5 = "At first thought it seems incredible that there can be enough space in the “solid” ground underfoot to hold all this water.";
    public TestHeadFootAdapter(){
        datas.add(text + text2 + text3 + text4 + text5);
        datas.addAll(datas);
        datas.addAll(datas);
        datas.addAll(datas);
        datas.addAll(datas);
        datas.addAll(datas);
        datas.addAll(datas);
        datas.addAll(datas);
        datas.addAll(datas);
    }

    @Override
    public int getChildCount() {
        return datas.size();
    }

    @Override
    public int getChildItemViewType(int pos) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return new ChildViewHolder(View.inflate(parent.getContext(), R.layout.layout_list_item_child, null));
    }

    @Override
    public void onBindChildViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ChildViewHolder){
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append(datas.get(position));
            int startPos = 0;
            int endPos = text.length();
            spannableStringBuilder.setSpan(new CusClickSpan(startPos, endPos, position), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.BLACK), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            startPos += text.length();
            endPos += text2.length();
            spannableStringBuilder.setSpan(new CusClickSpan(startPos, endPos, position), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.BLACK), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            startPos += text2.length();
            endPos += text3.length();
            spannableStringBuilder.setSpan(new CusClickSpan(startPos, endPos, position), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.BLACK), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            startPos += text3.length();
            endPos += text4.length();
            spannableStringBuilder.setSpan(new CusClickSpan(startPos, endPos, position), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.BLACK), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            startPos += text4.length();
            endPos += text5.length();
            spannableStringBuilder.setSpan(new CusClickSpan(startPos, endPos, position), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.BLACK), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((ChildViewHolder) holder).tvChild.setText(spannableStringBuilder);
        }
    }

    public static class CusClickSpan extends ClickableSpan{

        private static final String TAG = CusClickSpan.class.getSimpleName();

        private int startPos;

        private int endPos;

        private int index;

        public CusClickSpan(int startPos, int endPos, int index) {
            this.startPos = startPos;
            this.endPos = endPos;
            this.index = index;
        }

        @Override
        public void onClick(View view) {
            Log.e(TAG, "onClick: " + "startPos = [" + startPos + "], endPos = [" + endPos + "], index = [" + index + "]");
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder{

        public TextView tvChild;

        public ChildViewHolder(View itemView) {
            super(itemView);
            tvChild = (TextView) itemView.findViewById(R.id.tv_child);
            tvChild.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

}
