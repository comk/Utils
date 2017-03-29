package com.mayhub.utils.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by comkdai on 2017/3/29.
 */
public class SelectionLinearLayout extends LinearLayout {

    public static final String WORD_SET = "/u4e00-/u9fa5";

    public static final int UNICODE_START = 0x4E00;
    public static final int UNICODE_END = 0x9FA5;

    public static final String FORMAT_REGEX = "/([%s])/";

    public boolean isLongPressed;

    private long lastUpdateTime;

    private int WIDTH;

    private int HEIGHT;

    private int[] location = new int[2];

    private float downEventX;

    private TextView lastFocusTextView;

    private float maxRangeX,maxRangeY;

    private float downEventY;

    private String selectedWord;

    public SelectionLinearLayout(Context context) {
        super(context);
    }

    public SelectionLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectionLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(getChildCount() == 0){
            return super.onTouchEvent(ev);
        }
        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                postDelayed(longClickRunnable, 600);
                downEventX = ev.getRawX();
                downEventY = ev.getRawY();
                maxRangeX = 0;
                maxRangeY = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if(isLongPressed){
                    downEventX = -1;
                    downEventY = -1;
                    onLongClickEvent(ev.getRawX(), ev.getRawY());
                }else{
                    maxRangeX = Math.max(maxRangeX, Math.abs(downEventX - ev.getRawX()));
                    maxRangeY = Math.max(maxRangeY, Math.abs(downEventY - ev.getRawY()));
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                downEventX = -1;
                downEventY = -1;
                removeCallbacks(longClickRunnable);
                if(isLongPressed){

                    isLongPressed = false;
                }
                break;
        }
        if(isLongPressed){
            return false;
        }else{
            return super.onTouchEvent(ev);
        }
    }

    private void onLongClickEvent(float rawX, float rawY){
        if(rawX == -1 || rawY == -1 || System.currentTimeMillis() - lastUpdateTime <= 16){
            return;
        }
        lastUpdateTime = System.currentTimeMillis();
        selectedWord = findTextViewByPos(this,rawX,rawY);

//        int relativePosX = (int) (rawX - location[0]);
//        int relativePosY = (int) (rawY - location[1]);
//        if(relativePosY > 0 && relativePosY < getHeight()){
//            setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(getDrawingCache(),
//                    Math.min(getWidth() - WIDTH, Math.max(0,relativePosX - (WIDTH/2))),
//                    Math.min(getHeight() - HEIGHT, Math.max(0,relativePosY - (HEIGHT/2))),
//                    WIDTH, HEIGHT);
//            setDrawingCacheEnabled(false);
//            showAmplifyView(bitmap, (int)rawX, (int)rawY);
//        }
    }


//    private TextView findTextViewByPoint(float rawX, float rawY, ListView listView){
//        if(rawX < 0f || rawY < 0){
//            return null;
//        }
//        for (int i = 0; i < listView.getChildCount(); i++) {
//            View child = listView.getChildAt(i);
//            if(child != null && child instanceof TextView){
//                TextView tv = (TextView) child;
//                Rect outRect = new Rect();
//                child.getHitRect(outRect);
//                if(outRect.contains((int)rawX, (int)rawY)){
//                    return tv;
//                }
//            }
//        }
//        return null;
//    }

    /**
     * 查找相应位置下的英文单词
     * @param view 需要查找的view
     * @param rawX 相对屏幕的点击X坐标
     * @param rawY 相对屏幕的点击Y坐标
     * @return 对应位置的文本
     */
    private String findTextViewByPos(View view, float rawX, float rawY){
        String textView = null;
        if(view != null) {
            if(view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {//递归查找
                    textView = findTextViewByPos(((ViewGroup) view).getChildAt(i), rawX, rawY);
                    if(textView != null){
                        return textView;
                    }
                }
            }else if(view instanceof TextView){
                Rect rect = new Rect();
                view.getHitRect(rect);
                int[] viewLocation = new int[2];
                view.getLocationOnScreen(viewLocation);
                rect.right = viewLocation[0] + (rect.right - rect.left);
                rect.bottom = viewLocation[1] + (rect.bottom - rect.top);
                rect.left = viewLocation[0];
                rect.top = viewLocation[1];
                if(rect.contains((int)rawX,(int)rawY)){
                    return getTextByPoint((TextView) view,rawX - rect.left,rawY - rect.top);
                }
            }
        }
        return textView;
    }

//    private String findTextByPos(float rawX, float rawY, View view){
//        if(view != null){
//            if(view instanceof ViewGroup){
//                ViewGroup viewGroup = (ViewGroup) view;
//                ListView listView = null;
//                TextView textView;
//                for (int i = 0; i < viewGroup.getChildCount(); i++) {
//                    View child = viewGroup.getChildAt(i);
//                    if(child instanceof ListView){
//                        listView = (ListView) child;
//                        break;
//                    }
//                }
//                if(listView != null){
//                    int[] listViewLocation = new int[2];
//                    listView.getLocationOnScreen(listViewLocation);
//                    textView = findTextViewByPoint(rawX - listViewLocation[0], rawY - view.getTop() - listView.getTop(), listView);
//                    if(textView != null) {
//                        return getTextByPoint(textView, rawX + view.getLeft() - listView.getLeft(), rawY - view.getTop() + listView.getTop() - UIUtils.getInstance(getContext()).getStateBarHeight());
//                    }
//                }
//            }
//        }
//        return null;
//    }

    public void clearAllTextViewSpan(){
        if(lastFocusTextView != null && hasRemoveForegroundSpan(lastFocusTextView)){

        }else {
            for (int i = 0; i < getChildCount(); i++) {
                if (clearTextViewSpan(getChildAt(i))) {
                    break;
                }
            }
        }
    }

    /**
     * 清空TextView的Span
     * @param view 需要清空的TextView或者包含TextView 的 ViewGroup
     */
    private boolean clearTextViewSpan(View view){
        boolean hasRemove = false;
        if(view != null){
            if(view instanceof TextView){
                hasRemove = hasRemoveForegroundSpan((TextView) view);
            }else if(view instanceof ViewGroup){
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    hasRemove = clearTextViewSpan(viewGroup.getChildAt(i));
                    if(hasRemove){
                        return true;
                    }
                }
            }
        }
        return hasRemove;
    }

    private boolean hasRemoveForegroundSpan(TextView tv){
        Spannable spannable = (Spannable) tv.getText();
        if(tv.getText() instanceof SpannableString) {
            spannable = new Spannable.Factory().newSpannable(tv.getText());
        }else if(tv.getText() instanceof Spannable){
            spannable = new Spannable.Factory().newSpannable(tv.getText());
        }
        ForegroundColorSpan findSpan = null;
        if (spannable != null) {
            ForegroundColorSpan[] spans = spannable.getSpans(0, spannable.length(), ForegroundColorSpan.class);
            if (spans != null && spans.length > 0) {
                for (int i = 0; i < spans.length; i++) {
                    if (Color.WHITE == spans[i].getForegroundColor()) {
                        findSpan = spans[i];
                        break;
                    }
                }
            }
        }
        if (findSpan != null) {
            int start = spannable.getSpanStart(findSpan);
            int end = spannable.getSpanEnd(findSpan);
            BackgroundColorSpan[] backgroundColorSpan = spannable.getSpans(start, end, BackgroundColorSpan.class);
            if (backgroundColorSpan != null && backgroundColorSpan.length > 0) {
                for (int i = 0; i < backgroundColorSpan.length; i++) {
                    if(Color.BLUE == backgroundColorSpan[i].getBackgroundColor()){
                        spannable.removeSpan(backgroundColorSpan[i]);
                        break;
                    }
                }
            }
            spannable.removeSpan(findSpan);
            tv.setText(spannable, TextView.BufferType.SPANNABLE);
            return true;
        }
        return false;
    }



    private String getTextByPoint(TextView tv, float rawX, float rawY){
        clearAllTextViewSpan();
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
        lastFocusTextView = tv;
        return findText;
    }

    private Runnable longClickRunnable = new Runnable() {

        @Override
        public void run() {
            if(maxRangeX > 5 || maxRangeY > 5){
                return;
            }
            isLongPressed = true;
            onLongClickEvent(downEventX,downEventY);
        }
    };


}
