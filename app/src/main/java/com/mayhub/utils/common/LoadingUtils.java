package com.mayhub.utils.common;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mayhub.utils.R;

/**
 * Created by comkdai on 2017/3/23.
 */
public class LoadingUtils implements View.OnClickListener, Animator.AnimatorListener{

    private static final int DEFAULT_DURATION = 200;

    private LoadingUtils(){

    }

    public static LoadingUtils getInstance(){
        if(instance == null) {
            synchronized (LoadingUtils.class) {
                if (instance == null){
                    instance = new LoadingUtils();
                }
            }
        }
        return instance;
    }

    private static LoadingUtils instance;

    private View viewRoot;

    private boolean isCancelable = true;

    private boolean isAnimating = false;

    private TextView tvLoading;

    private String loadingText;

    public void showLoading(Context context, boolean isCancelable){
        this.isCancelable = isCancelable;
        showLoading(context);
    }

    public void showLoading(Context context, boolean isCancelable, String loadingText){
        this.isCancelable = isCancelable;
        this.loadingText = loadingText;
        showLoading(context, isCancelable);
    }

    public void showLoading(Context context){
        synchronized (LoadingUtils.class) {
            if(isAnimating && isShowing()){
                return;
            }
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
                viewRoot = View.inflate(context, R.layout.layout_loading, null);
                viewRoot.setAlpha(0f);
                viewRoot.setOnClickListener(this);
                if (!TextUtils.isEmpty(loadingText)) {
                    tvLoading = (TextView) viewRoot.findViewById(R.id.tv_loading_msg);
                    tvLoading.setText(loadingText);
                }
                viewGroup.addView(viewRoot);
                viewRoot.animate().setDuration(DEFAULT_DURATION).setListener(this).alphaBy(1).start();
            }
        }
    }

    public void updateLoadingText(String loadingText){
        synchronized (LoadingUtils.class) {
            if(isAnimating){
                return;
            }
            this.loadingText = loadingText;
            if (isShowing()) {
                tvLoading.setText(loadingText);
            }
        }
    }

    public boolean isShowing(){
        synchronized (LoadingUtils.class) {
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

    public void destroy(){
        synchronized (LoadingUtils.class) {
            dismissLoading();
            viewRoot = null;
            instance = null;
        }
    }

    public void dismissLoading(){
        synchronized (LoadingUtils.class) {
            if(isAnimating){
                return;
            }
            if (isShowing()) {
                if (viewRoot.getParent() != null && viewRoot.getParent() instanceof ViewGroup) {
                    final ViewGroup viewGroup = (ViewGroup) viewRoot.getParent();
                    viewRoot.animate().setDuration(DEFAULT_DURATION).setListener(this).alphaBy(-1).start();
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
