package com.mayhub.utils.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mayhub.utils.R;


/**
 * Created by Administrator on 2016/3/28.
 */
public class CusRecyclerView extends RecyclerView {

    private WordSelectedListener wordSelectedListener;

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

    public CusRecyclerView(Context context) {
        this(context, null);
    }

    public CusRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CusRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        PopView.initHeightWidth(context);
        WIDTH = PopView.WIDTH / 2;
        HEIGHT = PopView.HEIGHT / 2;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(location[0] == 0 && location[1] == 0){
            getLocationOnScreen(location);
        }

        boolean isIntercept = super.onInterceptTouchEvent(ev);
        if(!isIntercept) {
            onTouchEvent(ev);
        }
        return isIntercept;
    }


    PopView popView;

    FrameLayout frameLayoutAmplify;

    private void dismissAmplifyView(){
        if(popView != null){
            popView.setVisibility(INVISIBLE);
        }
    }

    private void showAmplifyView(Bitmap bitmap, int rawX, int rawY){
        if(popView == null) {
            View rootView = getRootView();
            if (rootView instanceof FrameLayout) {
                popView = new PopView(getContext());
                frameLayoutAmplify = (FrameLayout) rootView;
                frameLayoutAmplify.setId(R.id.amplify_view);
                frameLayoutAmplify.addView(popView);
            }
        }
        if(popView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) popView.getLayoutParams();
            int topMargin = rawY - (bitmap.getHeight() * 3);
            int leftMargin = rawX - bitmap.getWidth();
            int dir = PopView.TOP;
            if(topMargin <= 0){
                dir = rawX > frameLayoutAmplify.getWidth() / 2 ? PopView.LEFT : PopView.RIGHT;
                layoutParams.topMargin = rawY - bitmap.getHeight();
                if(dir == PopView.LEFT){
                    layoutParams.leftMargin = leftMargin - (bitmap.getWidth() * 2);
                }else{
                    layoutParams.leftMargin = leftMargin + (bitmap.getWidth() * 2);
                }
            }else {
                layoutParams.topMargin = topMargin;
                if(leftMargin < 0) {
                    layoutParams.leftMargin = 0;
                }else if(rawX + bitmap.getWidth() > frameLayoutAmplify.getWidth()){
                    layoutParams.leftMargin = frameLayoutAmplify.getRight() - bitmap.getWidth() * 2;
                }else{
                    layoutParams.leftMargin = leftMargin;
                }
            }
            popView.setLayoutParams(layoutParams);
            popView.updateView(bitmap, rawX, frameLayoutAmplify.getWidth(), dir);
            popView.setVisibility(VISIBLE);
        }
    }

    public void setWordSelectedListener(WordSelectedListener listener) {
        wordSelectedListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(getAdapter() == null){
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
                    dismissAmplifyView();
                    if(wordSelectedListener != null){
                        wordSelectedListener.onWordSelected(selectedWord);
                    }
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

        int relativePosX = (int) (rawX - location[0]);
        int relativePosY = (int) (rawY - location[1]);
        if(relativePosY > 0 && relativePosY < getHeight()){
            setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(getDrawingCache(),
                    Math.min(getWidth() - WIDTH, Math.max(0,relativePosX - (WIDTH/2))),
                    Math.min(getHeight() - HEIGHT, Math.max(0,relativePosY - (HEIGHT/2))),
                    WIDTH, HEIGHT);
            setDrawingCacheEnabled(false);
            showAmplifyView(bitmap, (int)rawX, (int)rawY);
        }
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

    private int[] findTextStartEndPosition(int offset, String textString){
        int subStringWidth = 60;
        int subStrBegin = offset - subStringWidth / 2;
        int subStrEnd = offset + subStringWidth / 2;
        int stringEnd = subStringWidth / 2 + 1;
        int stringBegin = subStringWidth / 2;
        if (subStrBegin < 0)
        {
            subStrBegin = 0;
            stringEnd = offset;
            stringBegin = 0;
        }
        if (subStrEnd >= textString.length())
        {
            subStrEnd = textString.length();
            stringEnd = textString.length();
        }

        String subString = textString.substring(subStrBegin, subStrEnd);
        int divider = offset - subStrBegin + 1;
        for (int i = divider; i < subString.length(); i++)
        {
            if (subString.charAt(i) >= 'a' && subString.charAt(i) <= 'z')
            {
                continue;
            }
            if (subString.charAt(i) >= 'A' && subString.charAt(i) <= 'Z')
            {
                continue;
            }
            stringEnd = i;
            break;
        }
        for (int i = divider - 1; i >= 0; i--)
        {
            if (subString.charAt(i) >= 'a' && subString.charAt(i) <= 'z')
            {
                continue;
            }
            if (subString.charAt(i) >= 'A' && subString.charAt(i) <= 'Z')
            {
                continue;
            }
            stringBegin = i + 1;
            break;
        }

        return new int[]{stringBegin, stringEnd, subStrBegin};
    }

    private String getTextByPoint(TextView tv, float rawX, float rawY){
        clearAllTextViewSpan();
        String findText = null;
        Layout layout = tv.getLayout();
        if(layout != null && !TextUtils.isEmpty(tv.getText().toString().trim())) {
//            int line = layout.getLineForVertical((int) (rawY - tv.getTop() + (tv.getTextSize() / 2)));
            int line = layout.getLineForVertical((int) (rawY - tv.getPaddingTop()));
            int offset = layout.getOffsetForHorizontal(line, rawX - tv.getPaddingLeft()) - 1;
            if (offset < 0) {
                offset = 0;
            }
            int[] startEnd = findTextStartEndPosition(offset, tv.getText().toString());

            if (startEnd[0] < startEnd[1]) {
                Spannable spanText;
                if(tv.getText() instanceof SpannableString) {
                    spanText = new Spannable.Factory().newSpannable(tv.getText());
                }else if(tv.getText() instanceof Spannable){
                    spanText = new Spannable.Factory().newSpannable(tv.getText());
                }else{
                    spanText = Spannable.Factory.getInstance().newSpannable(
                            tv.getText().toString());
                }
                String textString = tv.getText().toString();
                if (startEnd[2] == 0) {
                    spanText.setSpan(new ForegroundColorSpan(Color.WHITE), startEnd[0], startEnd[1],
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanText.setSpan(new BackgroundColorSpan(Color.BLUE), startEnd[0],
                            startEnd[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    findText = textString.substring(startEnd[0], startEnd[1]);
                    tv.setText(spanText, TextView.BufferType.SPANNABLE);
//                    setTextSpan(tv, false);
                } else {
                    int spanEnd = offset - 30 + startEnd[1];
                    if (spanEnd >= tv.getText().length()) {
                        spanEnd = tv.getText().length();
                    }
                    spanText.setSpan(new ForegroundColorSpan(Color.WHITE), offset - 30 + startEnd[0],
                            spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanText.setSpan(new BackgroundColorSpan(Color.BLUE), offset - 30
                                    + startEnd[0], spanEnd,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    findText = textString.substring(offset - 30 + startEnd[0],
                            spanEnd);
                    tv.setText(spanText, TextView.BufferType.SPANNABLE);
//                    setTextSpan(tv, false);
                }
            }
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

    public interface WordSelectedListener {
         void onWordSelected(String word);
    }



}
