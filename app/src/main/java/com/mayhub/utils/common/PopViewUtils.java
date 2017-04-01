package com.mayhub.utils.common;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mayhub.utils.R;

import java.lang.ref.WeakReference;

/**
 * Created by comkdai on 2017/3/23.
 */
public class PopViewUtils implements View.OnClickListener, Animator.AnimatorListener{

    private static final int DEFAULT_DURATION = 200;

    public static final int DIR_FROM_LEFT = 0x201;
    public static final int DIR_FROM_TOP = 0x202;
    public static final int DIR_FROM_RIGHT = 0x203;
    public static final int DIR_FROM_BOTTOM = 0x204;

    public interface PopItemListener{
        void onDismiss();
        void onBtnClick(int index);
    }

    private PopViewUtils(){

    }

    public static PopViewUtils getInstance(){
        if(instance == null) {
            synchronized (PopViewUtils.class) {
                if (instance == null){
                    instance = new PopViewUtils();
                }
            }
        }
        return instance;
    }

    private static PopViewUtils instance;

    private ViewGroup viewRoot;

    private ViewGroup content;

    private ViewGroup contentContainer;

    private WeakReference<ViewGroup> parent;

    private int gravity = Gravity.LEFT;

    private int align = Gravity.LEFT;

    private int animationDir = DIR_FROM_TOP;

    private int width;

    private int height;

    private int linearDir = LinearLayout.VERTICAL;

    private PopItemListener listener;

    private boolean isCancelable = true;

    private boolean isAnimating = false;

    public PopViewUtils initPopContent(Context context, boolean isCancelable, String[] items, int layoutDirection){
        synchronized (PopViewUtils.class) {
            if(isAnimating || isShowing() || items == null || items.length == 0){
                return this;
            }
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
                parent = new WeakReference<>(viewGroup);
                viewRoot = (ViewGroup) View.inflate(context, R.layout.layout_pop, null);
                content = (ViewGroup) viewRoot.findViewById(R.id.content);
                LinearLayout linearLayout = (LinearLayout) content;
                contentContainer = (ViewGroup) viewRoot.findViewById(R.id.content_container);
                linearDir = layoutDirection;
                linearLayout.setOrientation(linearDir);
                width = 0;
                height = 0;
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                for (int i = 0; i < items.length; i++) {
                    TextView tv = (TextView) layoutInflater.inflate(R.layout.layout_pop_item, viewRoot, false);
                    tv.setOnClickListener(this);
                    tv.setTag(i);
                    tv.setText(items[i]);
                    content.addView(tv);
                    tv.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    if(linearDir == LinearLayout.VERTICAL){
                        width = Math.max(tv.getMeasuredWidth(), width);
                        height += tv.getMeasuredHeight();
                    }else {
                        width += tv.getMeasuredWidth();
                        height = Math.max(tv.getMeasuredHeight(), height);
                    }
                }
                FrameLayout.LayoutParams l = (FrameLayout.LayoutParams) content.getLayoutParams();
                l.width = width;
                content.setMinimumHeight(height);
                content.setMinimumWidth(width);
                content.setLayoutParams(l);
                this.isCancelable = isCancelable;
                viewRoot.setAlpha(0f);
                viewRoot.setOnClickListener(this);
            }
            return this;
        }
    }

    public PopViewUtils initPopContent(Context context, boolean isCancelable, String[] items){
        return initPopContent(context,isCancelable, items, LinearLayout.VERTICAL);
    }

    private void animateIn(){
        switch (animationDir){
            case DIR_FROM_BOTTOM:
                content.setTranslationY(height);
                content.animate().
                        setListener(this).
                        translationYBy(-height).
                        setDuration(DEFAULT_DURATION).start();
                break;
            case DIR_FROM_LEFT:
                content.setTranslationX(-width);
                content.animate().
                        setListener(this).
                        translationXBy(width).
                        setDuration(DEFAULT_DURATION).start();
                break;
            case DIR_FROM_RIGHT:
                content.setTranslationX(width);
                content.animate().setListener(this).translationXBy(-width).setDuration(DEFAULT_DURATION).start();
                break;
            case DIR_FROM_TOP:
                content.setTranslationY(-height);
                content.animate().setListener(this).translationYBy(height).setDuration(DEFAULT_DURATION).start();
                break;
        }
    }

    private void animateOut(){
        switch (animationDir){
            case DIR_FROM_BOTTOM:
                content.animate().translationY(-height).setListener(this).translationYBy(height).setDuration(DEFAULT_DURATION).start();
                break;
            case DIR_FROM_LEFT:
                content.animate().translationX(width).setListener(this).translationXBy(-width).setDuration(DEFAULT_DURATION).start();
                break;
            case DIR_FROM_RIGHT:
                content.animate().translationX(-width).setListener(this).translationXBy(width).setDuration(DEFAULT_DURATION).start();
                break;
            case DIR_FROM_TOP:
                content.animate().translationY(height).setListener(this).translationYBy(-height).setDuration(DEFAULT_DURATION).start();
                break;
        }
    }

    public void at(View anchorView, int align, int gravity){
        ViewGroup viewGroup = parent.get();
        if(viewGroup != null) {
            this.align = align;
            this.gravity = gravity;
            setPosition(anchorView);
            viewGroup.addView(viewRoot);
            viewRoot.animate().setDuration(DEFAULT_DURATION).setListener(this).alphaBy(1).start();
            animateIn();
        }
    }

    public void at(View anchorVIew, int gravity){
        at(anchorVIew, gravity, Gravity.LEFT);
    }

    private void setGravity(int[] anchorPos, int anchorWidth, int anchorHeight, boolean isHorizontal, FrameLayout.LayoutParams layoutParams){
        switch (gravity){
            case Gravity.LEFT:
                if(isHorizontal) {
                    layoutParams.leftMargin = anchorPos[0];
                }
                break;
            case Gravity.RIGHT:
                if(isHorizontal) {
                    layoutParams.leftMargin = anchorPos[0] + anchorWidth - width;
                }
                break;
            case Gravity.BOTTOM:
                if(!isHorizontal) {
                    layoutParams.topMargin = anchorPos[1] + anchorHeight - height;
                }
                break;
            case Gravity.TOP:
                if(!isHorizontal) {
                    layoutParams.topMargin = anchorPos[1];
                }
                break;
            case Gravity.CENTER:
                if(isHorizontal){
                    int anchorXCenter = anchorPos[0] + (anchorWidth / 2);
                    layoutParams.leftMargin = anchorXCenter - width/2;
                }else{
                    int anchorYCenter = anchorPos[1] + (anchorHeight / 2);
                    layoutParams.topMargin = anchorYCenter - height/2;
                }
                break;
        }
    }

    private void setPosition(View anchorView){
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) contentContainer.getLayoutParams();
        int[] anchorPos = new int[2];
        int anchorWidth = anchorView.getMeasuredWidth();
        int anchorHeight = anchorView.getMeasuredHeight();
        anchorView.getLocationOnScreen(anchorPos);
        switch (align){
            case Gravity.LEFT:
                layoutParams.leftMargin = anchorPos[0] - width;
                layoutParams.topMargin = anchorPos[1];
                setGravity(anchorPos, anchorWidth, anchorHeight, false, layoutParams);
                break;
            case Gravity.RIGHT:
                layoutParams.leftMargin = anchorPos[0] + anchorWidth;
                layoutParams.topMargin = anchorPos[1];
                setGravity(anchorPos, anchorWidth, anchorHeight, false, layoutParams);
                break;
            case Gravity.BOTTOM:
                layoutParams.leftMargin = anchorPos[0];
                layoutParams.topMargin = anchorPos[1] + anchorView.getMeasuredHeight();
                setGravity(anchorPos, anchorWidth, anchorHeight, true, layoutParams);
                break;
            case Gravity.TOP:
                layoutParams.leftMargin = anchorPos[0];
                layoutParams.topMargin = anchorPos[1] - height;
                setGravity(anchorPos, anchorWidth, anchorHeight, true, layoutParams);
                break;
        }
    }

    public boolean canReshow(){
        return parent != null && parent.get() != null;
    }

    public void reshow(){
        ViewGroup viewGroup = parent.get();
        if(viewGroup != null) {
            viewGroup.addView(viewRoot);
            viewRoot.animate().setDuration(DEFAULT_DURATION).setListener(this).alphaBy(1).start();
            animateIn();
        }
    }

    public boolean isShowing(){
        synchronized (PopViewUtils.class) {
            if (viewRoot != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (viewRoot.isAttachedToWindow() && viewRoot.getParent() != null && viewRoot.getParent() instanceof ViewGroup) {
                        return true;
                    }
                } else {
                    if (viewRoot.getParent() != null && viewRoot.getParent() instanceof ViewGroup) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public PopViewUtils setAnimationDir(int animationDir) {
        this.animationDir = animationDir;
        return this;
    }

    public void destroy(){
        synchronized (PopViewUtils.class) {
            dismissLoading();
            viewRoot = null;
            instance = null;
        }
    }

    public PopViewUtils setListener(PopItemListener listener) {
        this.listener = listener;
        return this;
    }

    public void dismissLoading(){
        synchronized (PopViewUtils.class) {
            if(isAnimating){
                return;
            }
            if (isShowing()) {
                if (viewRoot.getParent() != null && viewRoot.getParent() instanceof ViewGroup) {
                    final ViewGroup viewGroup = (ViewGroup) viewRoot.getParent();
                    animateOut();
                    viewGroup.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewGroup.removeView(viewRoot);
                        }
                    }, DEFAULT_DURATION);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.v_bg:
                if(isCancelable){
                    dismissLoading();
                    if(listener != null){
                        listener.onDismiss();
                    }
                }
                break;
            case R.id.pop_item:
                if(v.getTag() != null && v.getTag() instanceof Integer){
                    if(listener != null){
                        listener.onBtnClick((Integer) v.getTag());
                    }
                }
                break;
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
        isAnimating = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        isAnimating = false;
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
