package com.mayhub.utils.common;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mayhub.utils.R;
import com.mayhub.utils.widget.CusFrameLayout;

import java.lang.ref.WeakReference;

/**
 * Created by comkdai on 2017/3/23.
 */
public class SelectionViewUtils implements View.OnClickListener, CusFrameLayout.DragListener{

    private static final String TAG = "SelectionViewUtils";
    private static final Object SELECTION_START = new BackgroundColorSpan(Color.RED);

    @Override
    public void onDrag(float x, float y, boolean isStart) {
        TextView tvSelection = tvSelectionRef.get();
        if(tvSelection != null) {
            updateTextViewSelectionArea(tvSelection, x, y, isStart);
        }
    }

    @Override
    public void dragEnd(float y, int viewId) {
        endDrag(y, viewId == leftView.getId());
    }

    public interface SelectionListener{
        void onDismiss();
        void click(String inputText);
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

    private WeakReference<TextView> tvSelectionRef;

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
    private Rect rect = new Rect();

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
            translateView.setOnClickListener(this);
            copyView.setOnClickListener(this);
            addView.setOnClickListener(this);
            selectAllView.setOnClickListener(this);
            cusFrameLayout.setDragListener(this);
            cusFrameLayout.setViewLeft(leftView);
            cusFrameLayout.setViewRight(rightView);
            lineHeight = tv.getLineHeight();
            tvSelectionRef = new WeakReference<>(tv);
            viewRoot.setOnClickListener(this);
            viewGroup.addView(viewRoot);
            initTextViewSelectionArea(tv, rawX, rawY);
        }
    }

    private void initTextViewSelectionArea(TextView view, float rawX, float rawY){
        view.getGlobalVisibleRect(rect);
        if(rect.contains((int)rawX,(int)rawY)){
            Layout layout = view.getLayout();
            if(layout != null && !TextUtils.isEmpty(view.getText().toString().trim())) {
                int line = layout.getLineForVertical((int) (rawY - rect.top - view.getPaddingTop()));
                int offset = layout.getOffsetForHorizontal(line, rawX - rect.left - view.getPaddingLeft());
                showSelectionArea(view, offset, offset + 5);
                primaryHori = layout.getPrimaryHorizontal(startLast) + rect.left + view.getPaddingLeft();
                secondaryHori = layout.getPrimaryHorizontal(endLast) + rect.left + view.getPaddingLeft();
                if(offset >= 0) {
                    int top = rect.top + view.getPaddingTop() + ((line + 1) * lineHeight);
                    showDragger(true, (int) primaryHori, top);
                    showDragger(false, (int) secondaryHori, top);
                    layoutOptionByCenter((int) ((primaryHori + secondaryHori) / 2), top - lineHeight);
                }
            }
        }
    }

    private boolean stateReverse = false;

    private void updateTextViewSelectionArea(TextView view, float rawX, float rawY, boolean isStart){
        if(rect.contains((int)rawX,(int)rawY)){
            Layout layout = view.getLayout();
            if(layout != null && !TextUtils.isEmpty(view.getText().toString().trim())) {
                int line = layout.getLineForVertical((int) (rawY - rect.top - view.getPaddingTop()));
                int offset = layout.getOffsetForHorizontal(line, rawX - rect.left - view.getPaddingLeft());
                if(offset >= 0) {
                    int top = rect.top + view.getPaddingTop() + ((line + 1) * lineHeight);
                    if(isStart){
                        if(stateReverse){
                            if(offset < startLast) {
                                endLast = startLast;
                                startLast = offset;
                                stateReverse = !stateReverse;
                            }else if(offset > startLast){
                                endLast = offset;
                            }else {
                                startLast = offset;
                            }
                        }else {
                            if (offset > endLast) {
                                startLast = endLast;
                                endLast = offset;
                                stateReverse = !stateReverse;
                            } else if (offset < endLast) {
                                startLast = offset;
                            } else {
                                endLast = offset;
                            }
                        }
                    }else{
                        if(stateReverse){
                            if (offset > endLast) {
                                startLast = endLast;
                                endLast = offset;
                                stateReverse = !stateReverse;
                            } else if (offset < endLast) {
                                startLast = offset;
                            } else {
                                endLast = offset;
                            }
                        }else {
                            if (offset < startLast) {
                                endLast = startLast;
                                startLast = offset;
                                stateReverse = !stateReverse;
                            } else if (offset > startLast) {
                                endLast = offset;
                            } else {
                                startLast = offset;
                            }
                        }
                    }
                    showSelectionArea(view, startLast, endLast);
                    layoutOptionByCenter((int) ((primaryHori + secondaryHori) / 2), top - lineHeight);
                }
            }
        }
    }

    private void endDrag(float rawY, boolean isStart){
        TextView tvSelection = tvSelectionRef.get();
        if(tvSelection != null) {
            Layout layout = tvSelection.getLayout();
            if (layout != null && !TextUtils.isEmpty(tvSelection.getText().toString().trim())) {
                int line = layout.getLineForVertical((int) (rawY - rect.top - tvSelection.getPaddingTop()));
                int top2 = rect.top + tvSelection.getPaddingTop() + ((line + 1) * lineHeight);
                if(stateReverse){
                    if(isStart){
                        secondaryHori = layout.getPrimaryHorizontal(endLast) + rect.left + tvSelection.getPaddingLeft();
                        showDragger(true, (int) secondaryHori, top2);
                    }else{
                        secondaryHori = layout.getPrimaryHorizontal(startLast) + rect.left + tvSelection.getPaddingLeft();
                        showDragger(false, (int) secondaryHori, top2);
                    }
                    exchangeViewPos();
                    stateReverse = false;
                }else {
                    if(isStart){
                        secondaryHori = layout.getPrimaryHorizontal(startLast) + rect.left + tvSelection.getPaddingLeft();
                        showDragger(true, (int) secondaryHori, top2);
                    }else {
                        secondaryHori = layout.getPrimaryHorizontal(endLast) + rect.left + tvSelection.getPaddingLeft();
                        showDragger(false, (int) secondaryHori, top2);
                    }
                }
                showSelectionArea(tvSelection, startLast, endLast);
                layoutOptionByCenter((int) ((primaryHori + secondaryHori) / 2), top2 - lineHeight);
            }
        }
    }

    private void exchangeViewPos() {
        FrameLayout.LayoutParams layoutParamsL = (FrameLayout.LayoutParams) leftView.getLayoutParams();
        int yL = layoutParamsL.topMargin;
        int xL = layoutParamsL.leftMargin;
        FrameLayout.LayoutParams layoutParamsR = (FrameLayout.LayoutParams) rightView.getLayoutParams();
        int yR = layoutParamsR.topMargin;
        int xR = layoutParamsR.leftMargin;

        layoutParamsL.topMargin = yR;
        layoutParamsL.leftMargin = xR;
        layoutParamsR.topMargin = yL;
        layoutParamsR.leftMargin = xL;
        rightView.setLayoutParams(layoutParamsR);
        leftView.setLayoutParams(layoutParamsL);
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

    private void clearSelection(){
        TextView tvSelection = tvSelectionRef.get();
        if(tvSelection != null){
            Spannable spanText = getSpanText(tvSelection);
            spanText.removeSpan(SELECTION_START);
        }
    }

    private Spannable getSpanText(TextView tv){
        if(tv.getText() instanceof SpannableString) {
            return new Spannable.Factory().newSpannable(tv.getText());
        }else if(tv.getText() instanceof Spannable){
            return new Spannable.Factory().newSpannable(tv.getText());
        }else{
            return Spannable.Factory.getInstance().newSpannable(
                    tv.getText().toString());
        }
    }

    private void showSelectionArea(TextView tv, int start, int end){
        startLast = Math.max(start, 0);
        endLast = Math.min(end, tv.getText().length());
        Spannable spanText = getSpanText(tv);
        spanText.removeSpan(SELECTION_START);
        spanText.setSpan(SELECTION_START, start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spanText, TextView.BufferType.SPANNABLE);
    }

    private void showDragger(final boolean isStart, final int x, final int y){
        if(leftView.getMeasuredWidth() == 0){
            leftView.post(new Runnable() {
                @Override
                public void run() {
                    setDrager(isStart, y, x);
                }
            });
        }else{
            setDrager(isStart, y, x);
        }
    }

    private void setDrager(boolean isStart, int y, int x) {
        FrameLayout.LayoutParams layoutParams;
        if(isStart){
            layoutParams = (FrameLayout.LayoutParams) leftView.getLayoutParams();
            layoutParams.topMargin = y;
            layoutParams.leftMargin = x - (leftView.getMeasuredWidth() / 2);
            leftView.setLayoutParams(layoutParams);
            if(!rect.contains(rect.centerX(), layoutParams.topMargin)){
                leftView.setVisibility(View.INVISIBLE);
            }else {
                leftView.setVisibility(View.VISIBLE);
            }
        }else{
            layoutParams = (FrameLayout.LayoutParams) rightView.getLayoutParams();
            layoutParams.topMargin = y - 1;
            layoutParams.leftMargin = x - (rightView.getMeasuredWidth() / 2);
            rightView.setLayoutParams(layoutParams);
            if(!rect.contains(rect.centerX(), layoutParams.topMargin)){
                rightView.setVisibility(View.INVISIBLE);
            }else{
                rightView.setVisibility(View.VISIBLE);
            }
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
            case R.id.select_all:
                TextView tvSelection = tvSelectionRef.get();
                if(tvSelection != null) {
                    showSelectionArea(tvSelection, 0, tvSelection.getText().length());
                    Layout layout = tvSelection.getLayout();
                    int line = layout.getLineForVertical(0 - tvSelection.getPaddingTop());
                    int top2 = rect.top + tvSelection.getPaddingTop() + ((line + 1) * lineHeight);
                    secondaryHori = layout.getPrimaryHorizontal(startLast) + rect.left + tvSelection.getPaddingLeft();
                    showDragger(true, (int) secondaryHori, top2);
                    secondaryHori = layout.getPrimaryHorizontal(endLast) + rect.left + tvSelection.getPaddingLeft();
                    showDragger(false, (int) secondaryHori, rect.bottom);
                }
                break;
            case R.id.copy:
                dismissLoading();
                TextView tvSelection2 = tvSelectionRef.get();
                if(tvSelection2 != null) {
                    ClipboardManager clipboardManager = (ClipboardManager) tvSelection2.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("text", tvSelection2.getText().toString().substring(startLast, endLast));
                    clipboardManager.setPrimaryClip(clipData);
                    ToastUtils.getInstance().showShortToast(tvSelection2.getContext(), "已复制到剪切板");
                }
                break;
            case R.id.translate:

                break;
            case R.id.add:

                break;
        }
    }
}
