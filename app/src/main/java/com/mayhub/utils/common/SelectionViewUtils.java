package com.mayhub.utils.common;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mayhub.utils.R;
import com.mayhub.utils.widget.CusFrameLayout;

/**
 * Created by comkdai on 2017/3/23.
 */
public class SelectionViewUtils implements View.OnClickListener, CusFrameLayout.DragListener{

    private static final String TAG = "SelectionViewUtils";

    private static final int DEFAULT_DURATION = 200;

    private static final class START implements NoCopySpan { }
    private static final class END implements NoCopySpan { }

    public static final Object SELECTION_START = new BackgroundColorSpan(Color.RED);
    public static final Object SELECTION_END = new END();

    @Override
    public void onDrag(float x, float y, boolean isStart) {
        updateTextViewSelectionArea(tvSelection, x, y, isStart);
    }

    @Override
    public void dragEnd(float y, int viewId) {
        endDrag(y, viewId == leftView.getId());
    }

    public interface SelectionListener{
        void onDismiss();
        void onSelection(String inputText);
    }

    private SelectionViewUtils(){

    }

    public static SelectionViewUtils getInstance(){
        if(instance == null) {
            synchronized (SelectionViewUtils.class) {
                if (instance == null){
                    instance = new SelectionViewUtils();
                }
            }
        }
        return instance;
    }

    private static SelectionViewUtils instance;

    private View viewRoot;

    private View leftView;

    private View rightView;

    private View optionView;

    private TextView tvSelection;

    private View translateView;
    private View selectAllView;
    private View copyView;
    private View addView;

    private int startLast;

    private int endLast;

    private int lineHeight;

    float primaryHori;

    float secondaryHori;

    private SelectionListener listener;

    public void showSelection(Context context, TextView tv, float rawX, float rawY){
        if(context instanceof Activity) {
            showSelection((ViewGroup) ((Activity) context).getWindow().getDecorView(), tv, rawX, rawY);
        }
    }

    public void showSelection(ViewGroup viewGroup, TextView tv, float rawX, float rawY){
        synchronized (SelectionViewUtils.class) {
            if(isShowing()){
                return;
            }
            viewRoot = View.inflate(viewGroup.getContext(), R.layout.layout_selection_dialog, null);
            CusFrameLayout cusFrameLayout = (CusFrameLayout) viewRoot;
            rightView = viewRoot.findViewById(R.id.imgEnd);
            optionView = viewRoot.findViewById(R.id.view_option);
            leftView = viewRoot.findViewById(R.id.imgStart);
            translateView = viewRoot.findViewById(R.id.translate);
            selectAllView = viewRoot.findViewById(R.id.select_all);
            copyView = viewRoot.findViewById(R.id.copy);
            addView = viewRoot.findViewById(R.id.add);
            cusFrameLayout.setDragListener(this);
            cusFrameLayout.setViewLeft(leftView);
            cusFrameLayout.setViewRight(rightView);
            lineHeight = tv.getLineHeight();
            tvSelection = tv;
            viewRoot.setOnClickListener(this);
            viewGroup.addView(viewRoot);
            initTextViewSelectionArea(tv, rawX, rawY);
        }
    }

    private void initTextViewSelectionArea(TextView view, float rawX, float rawY){
        view.getHitRect(rect);
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        rect.right = viewLocation[0] + (rect.right - rect.left);
        rect.bottom = viewLocation[1] + (rect.bottom - rect.top);
        rect.left = viewLocation[0];
        rect.top = viewLocation[1];
        if(rect.contains((int)rawX,(int)rawY)){
            Layout layout = view.getLayout();
            if(layout != null && !TextUtils.isEmpty(view.getText().toString().trim())) {
                int line = layout.getLineForVertical((int) (rawY - rect.top - view.getPaddingTop()));
                int offset = layout.getOffsetForHorizontal(line, rawX - rect.left - view.getPaddingLeft()) - 1;
                primaryHori = layout.getPrimaryHorizontal(offset);
                secondaryHori = layout.getPrimaryHorizontal(offset+5);
                if(offset >= 0) {
                    startLast = offset;
                    endLast = offset + 5;
                    showSelectionArea(tvSelection, offset, offset + 5, false);
                    int top = rect.top + view.getPaddingTop() + ((line + 1) * lineHeight);
                    showDragger(true, (int) primaryHori, top);
                    showDragger(false, (int) secondaryHori, top);
                    layoutOptionByCenter((int) ((primaryHori + secondaryHori) / 2), top - lineHeight);
                }
            }
        }
    }

    private boolean stateReverse = false;

    Rect rect = new Rect();
    private void updateTextViewSelectionArea(TextView view, float rawX, float rawY, boolean isStart){
        if(rect.contains((int)rawX,(int)rawY)){
            Layout layout = view.getLayout();
            if(layout != null && !TextUtils.isEmpty(view.getText().toString().trim())) {
                int line = layout.getLineForVertical((int) (rawY - rect.top - view.getPaddingTop()));
                int offset = layout.getOffsetForHorizontal(line, rawX - rect.left - view.getPaddingLeft()) - 1;
                if(offset >= 0) {
                    int top = rect.top + view.getPaddingTop() + ((line + 1) * lineHeight);
//                    if(offset < startLast){
//                        startLast = offset;
//                    }else if(offset > endLast){
//                        endLast = offset;
//                    }else{
                        if(isStart || stateReverse){
                            if(offset > endLast) {
                                startLast = endLast;
                                endLast = offset;
                                stateReverse = !stateReverse;
                            }else if(offset < endLast){
                                startLast = offset;
                            }else{
                                endLast = offset;
                            }
                        }else{
                            if(offset < startLast) {
                                endLast = startLast;
                                startLast = offset;
                                stateReverse = !stateReverse;
                            }else if(offset > startLast){
                                endLast = offset;
                            }else {
                                startLast = offset;
                            }
                        }
//                    }
                    showSelectionArea(tvSelection, startLast, endLast, true);
                    layoutOptionByCenter((int) ((primaryHori + secondaryHori) / 2), top - lineHeight);
                }
            }
        }
    }

    private void endDrag(float rawY, boolean isStart){
        Log.e(TAG, "endDrag() called with: " + "rawY = [" + rawY + "]");
        if(tvSelection != null) {
            Layout layout = tvSelection.getLayout();
            if (layout != null && !TextUtils.isEmpty(tvSelection.getText().toString().trim())) {
                int line = layout.getLineForVertical((int) (rawY - rect.top - tvSelection.getPaddingTop()));
                int top2 = rect.top + tvSelection.getPaddingTop() + ((line + 1) * lineHeight);
                if(stateReverse){
                    if(isStart){
                        secondaryHori = layout.getSecondaryHorizontal(endLast) + rect.left + tvSelection.getPaddingLeft();
                        showDragger(true, (int) secondaryHori, top2);
                    }else{
                        secondaryHori = layout.getSecondaryHorizontal(startLast) + rect.left + tvSelection.getPaddingLeft();
                        showDragger(false, (int) secondaryHori, top2);
                    }
                    exchangeViewPos();
                }else {
                    if(isStart){
                        secondaryHori = layout.getSecondaryHorizontal(startLast) + rect.left + tvSelection.getPaddingLeft();
                        showDragger(false, (int) secondaryHori, top2);
                    }else {
                        secondaryHori = layout.getSecondaryHorizontal(endLast) + rect.left + tvSelection.getPaddingLeft();
                        showDragger(false, (int) secondaryHori, top2);
                    }
                }
                showSelectionArea(tvSelection, startLast, endLast, false);
                layoutOptionByCenter((int) ((primaryHori + secondaryHori) / 2), top2 - lineHeight);
            }
        }
    }

    private void exchangeViewPos() {
        FrameLayout.LayoutParams layoutParamsL = (FrameLayout.LayoutParams) leftView.getLayoutParams();
        FrameLayout.LayoutParams layoutParamsR = (FrameLayout.LayoutParams) rightView.getLayoutParams();
        rightView.setLayoutParams(layoutParamsL);
        leftView.setLayoutParams(layoutParamsR);
    }

    private void layoutOptionByCenter(final int x, final int y){
        optionView.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) optionView.getLayoutParams();
                layoutParams.topMargin = y - optionView.getMeasuredHeight();
                final View parent = optionView.getRootView();
                int width = optionView.getMeasuredWidth();
                int marginLeft = x - (width / 2);
                if(parent.getMeasuredWidth() > marginLeft + width) {
                    layoutParams.leftMargin = Math.max(0, marginLeft);
                }else{
                    layoutParams.leftMargin = parent.getMeasuredWidth() - width;
                }
                optionView.setLayoutParams(layoutParams);
            }
        });

    }

    private void test(TextView view, float rawX, float rawY){
        Rect rect = new Rect();
        view.getHitRect(rect);
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        rect.right = viewLocation[0] + (rect.right - rect.left);
        rect.bottom = viewLocation[1] + (rect.bottom - rect.top);
        rect.left = viewLocation[0];
        rect.top = viewLocation[1];
        if(rect.contains((int)rawX,(int)rawY)){
//            return getTextByPoint((TextView) view,rawX - rect.left,rawY - rect.top);
        }
    }

    private String getTextByPoint(TextView tv, float rawX, float rawY){
//        clearAllTextViewSpan();
        String findText = null;
        Layout layout = tv.getLayout();
        if(layout != null && !TextUtils.isEmpty(tv.getText().toString().trim())) {
//            int line = layout.getLineForVertical((int) (rawY - tv.getTop() + (tv.getTextSize() / 2)));
            int line = layout.getLineForVertical((int) (rawY - tv.getPaddingTop()));
            int offset = layout.getOffsetForHorizontal(line, rawX - tv.getPaddingLeft()) - 1;


//            if (startEnd[0] < startEnd[1]) {
//                Spannable spanText;
//                if(tv.getText() instanceof SpannableString) {
//                    spanText = new Spannable.Factory().newSpannable(tv.getText());
//                }else if(tv.getText() instanceof Spannable){
//                    spanText = new Spannable.Factory().newSpannable(tv.getText());
//                }else{
//                    spanText = Spannable.Factory.getInstance().newSpannable(
//                            tv.getText().toString());
//                }
//                String textString = tv.getText().toString();
//                if (startEnd[2] == 0) {
//                    spanText.setSpan(new ForegroundColorSpan(Color.WHITE), startEnd[0], startEnd[1],
//                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    spanText.setSpan(new BackgroundColorSpan(Color.BLUE), startEnd[0],
//                            startEnd[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    findText = textString.substring(startEnd[0], startEnd[1]);
//                    tv.setText(spanText, TextView.BufferType.SPANNABLE);
////                    setTextSpan(tv, false);
//                } else {
//                    int spanEnd = offset - 30 + startEnd[1];
//                    if (spanEnd >= tv.getText().length()) {
//                        spanEnd = tv.getText().length();
//                    }
//                    spanText.setSpan(new ForegroundColorSpan(Color.WHITE), offset - 30 + startEnd[0],
//                            spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    spanText.setSpan(new BackgroundColorSpan(Color.BLUE), offset - 30
//                                    + startEnd[0], spanEnd,
//                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    findText = textString.substring(offset - 30 + startEnd[0],
//                            spanEnd);
//                    tv.setText(spanText, TextView.BufferType.SPANNABLE);
////                    setTextSpan(tv, false);
//                }
//            }
            tv.setTag(null);
        }
//        lastFocusTextView = tv;
        return findText;
    }

    private void clearSelection(){
        if(tvSelection != null){
            Spannable spanText = (Spannable) tvSelection.getText();
            spanText.removeSpan(SELECTION_START);
        }
    }

    private void showSelectionArea(TextView tv, int start, int end, boolean isExtend){
        Spannable spanText = (Spannable) tv.getText();
//        if(tv.getText() instanceof SpannableString) {
//            spanText = new Spannable.Factory().newSpannable(tv.getText());
//        }else if(tv.getText() instanceof Spannable){
//            spanText = new Spannable.Factory().newSpannable(tv.getText());
//        }else{
//            spanText = Spannable.Factory.getInstance().newSpannable(
//                    tv.getText().toString());
//        }
//        Selection.removeSelection(spanText);
//        Selection.setSelection(spanText, start, end);
//        if(isExtend){
//            Selection.setSelection(spanText, start, end);
//            int oldEnd = spanText.getSpanEnd(SELECTION_END);
//            int oldStart = spanText.getSpanStart(SELECTION_START);
//            if(oldStart > start){
//                spanText.removeSpan(SELECTION_START);
//                spanText.setSpan(SELECTION_START, start, start,
//                        Spanned.SPAN_POINT_POINT | Spanned.SPAN_INTERMEDIATE);
//            }
//            if(oldEnd < end){
//                spanText.removeSpan(SELECTION_END);
//                spanText.setSpan(SELECTION_END, end, end,
//                        Spanned.SPAN_POINT_POINT);
//            }
//        }else {
//            spanText.setSpan(SELECTION_START, start, start,
//                    Spanned.SPAN_POINT_POINT | Spanned.SPAN_INTERMEDIATE);
//            spanText.setSpan(SELECTION_END, end, end,
//                    Spanned.SPAN_POINT_POINT);
////            tv.setText(spanText);
//        }
        spanText.removeSpan(SELECTION_START);
//        spanText.removeSpan(SELECTION_END);
        spanText.setSpan(SELECTION_START, start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spanText.setSpan(SELECTION_END, start,
//                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spanText, TextView.BufferType.SPANNABLE);
    }

    private void showDragger(boolean isStart, int x, int y){
        FrameLayout.LayoutParams layoutParams;
        if(isStart){
            layoutParams = (FrameLayout.LayoutParams) leftView.getLayoutParams();
            layoutParams.topMargin = y;
            layoutParams.leftMargin = x + (leftView.getMeasuredWidth() / 2);
            leftView.setLayoutParams(layoutParams);
        }else{
            layoutParams = (FrameLayout.LayoutParams) rightView.getLayoutParams();
            layoutParams.topMargin = y;
            layoutParams.leftMargin = x - (rightView.getMeasuredWidth() / 2);
            rightView.setLayoutParams(layoutParams);
        }
    }


    public SelectionViewUtils listener(SelectionListener selectionListener){
        listener = selectionListener;
        return this;
    }

    public boolean isShowing(){
        synchronized (SelectionViewUtils.class) {
            if (viewRoot != null) {
                if (viewRoot.getParent() != null && viewRoot.getParent() instanceof ViewGroup) {
                    return true;
                }
            }
            return false;
        }
    }

    public void destroy(){
        synchronized (SelectionViewUtils.class) {
            dismissLoading();
            viewRoot = null;
            instance = null;
            listener = null;
        }
    }

    public void dismissLoading(){
        synchronized (SelectionViewUtils.class) {
            if (isShowing()) {
                if (viewRoot.getParent() != null && viewRoot.getParent() instanceof ViewGroup) {
                    clearSelection();
                    final ViewGroup viewGroup = (ViewGroup) viewRoot.getParent();
                    viewGroup.removeView(viewRoot);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.v_bg:
                dismissLoading();
                if(listener != null){
                    listener.onDismiss();
                }
                break;

        }
    }
}
