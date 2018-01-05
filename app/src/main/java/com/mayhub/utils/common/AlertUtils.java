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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by comkdai on 2017/3/23.
 */
public class AlertUtils implements View.OnClickListener, Animator.AnimatorListener{

    private static final int DEFAULT_DURATION = 200;

    public interface AlertListener{
        void onDismiss();
        void onBtnClick();
        void onBtnLeftClick();
        void onBtnRightClick();
    }

    public static abstract class AlertListenerAdapter implements AlertListener{

        @Override
        public void onDismiss() {

        }

        @Override
        public void onBtnClick() {

        }

        @Override
        public void onBtnLeftClick() {

        }

        @Override
        public void onBtnRightClick() {

        }
    }

    private AlertUtils(){

    }

    public static AlertUtils getInstance(){
        if(instance == null) {
            synchronized (AlertUtils.class) {
                if (instance == null){
                    instance = new AlertUtils();
                }
            }
        }
        return instance;
    }

    private static AlertUtils instance;

    private View viewRoot;

    private boolean isCancelable = true;

    private boolean isAnimating = false;

    private TextView tvMessage;

    private TextView tvLeft;

    private TextView tvRight;

    private AlertListener listener;

    public void showOptionAlert(Context context, boolean isCancelable, String message, String btnLeftTxt, String btnRightTxt){
        synchronized (AlertUtils.class) {
            if(isAnimating && isShowing()){
                return;
            }
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
                viewRoot = View.inflate(context, R.layout.layout_alert, null);
                tvMessage = (TextView) viewRoot.findViewById(R.id.tv_message);
                tvLeft = (TextView) viewRoot.findViewById(R.id.tv_option_left);
                tvRight = (TextView) viewRoot.findViewById(R.id.tv_option_right);
                this.isCancelable = isCancelable;
                tvMessage.setText(message);
                tvLeft.setText(btnLeftTxt);
                tvRight.setText(btnRightTxt);
                viewRoot.setAlpha(0f);
                viewRoot.setOnClickListener(this);
                tvLeft.setOnClickListener(this);
                tvRight.setOnClickListener(this);
                viewGroup.addView(viewRoot);
                viewRoot.animate().setDuration(DEFAULT_DURATION).setListener(this).alphaBy(1).start();
            }
        }
    }

    public void showAlert(Context context, boolean isCancelable, String message, String btnTxt){
        synchronized (AlertUtils.class) {
            if(isAnimating && isShowing()){
                return;
            }
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
                viewRoot = View.inflate(context, R.layout.layout_alert, null);
                tvMessage = (TextView) viewRoot.findViewById(R.id.tv_message);
                tvLeft = (TextView) viewRoot.findViewById(R.id.tv_option_left);
                tvRight = (TextView) viewRoot.findViewById(R.id.tv_option_right);
                this.isCancelable = isCancelable;
                tvMessage.setText(message);
                tvLeft.setText(btnTxt);
                tvRight.setVisibility(View.GONE);
                viewRoot.setAlpha(0f);
                viewRoot.setOnClickListener(this);
                tvLeft.setOnClickListener(this);
                viewGroup.addView(viewRoot);
                viewRoot.animate().setDuration(DEFAULT_DURATION).setListener(this).alphaBy(1).start();
            }
        }
    }

    private int showIndex = 0;
    private List<String> messages;
    private List<String> btnTxts;

    public void showAlert(Context context, boolean isCancelable, String[] messages, String[] btnTxts){
        showAlert(context, isCancelable, Arrays.asList(messages), Arrays.asList(btnTxts));
    }

    public void showAlert(Context context, boolean isCancelable, List<String> messages, List<String> btnTxts){
        synchronized (AlertUtils.class) {
            if(isAnimating && isShowing()){
                return;
            }
            if (context instanceof Activity) {
                showIndex = 0;
                this.messages = messages;
                this.btnTxts = btnTxts;
                Activity activity = (Activity) context;
                ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
                viewRoot = View.inflate(context, R.layout.layout_alert, null);
                tvMessage = (TextView) viewRoot.findViewById(R.id.tv_message);
                tvLeft = (TextView) viewRoot.findViewById(R.id.tv_option_left);
                tvRight = (TextView) viewRoot.findViewById(R.id.tv_option_right);
                this.isCancelable = isCancelable;
                tvMessage.setText(messages.get(showIndex));
                tvLeft.setText(btnTxts.get(showIndex));
                tvRight.setVisibility(View.GONE);
                viewRoot.setAlpha(0f);
                viewRoot.setOnClickListener(this);
                tvLeft.setOnClickListener(this);
                viewGroup.addView(viewRoot);
                viewRoot.animate().setDuration(DEFAULT_DURATION).setListener(this).alphaBy(1).start();
            }
        }
    }

    public AlertUtils listener(AlertListener alertListener){
        listener = alertListener;
        return this;
    }

    public boolean isShowing(){
        synchronized (AlertUtils.class) {
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
        synchronized (AlertUtils.class) {
            dismissLoading();
            viewRoot = null;
            tvMessage = null;
            tvRight = null;
            tvLeft = null;
            showIndex = 0;
            instance = null;
            listener = null;
        }
    }

    public void dismissLoading(){
        synchronized (AlertUtils.class) {
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
                    if(listener != null){
                        listener.onDismiss();
                    }
                }
                break;
            case R.id.tv_option_left:
                if(listener != null){
                    if(tvRight.getVisibility() == View.VISIBLE) {
                        listener.onBtnLeftClick();
                    }else {
                        if(messages == null || messages.size() == showIndex + 1) {
                            listener.onBtnClick();
                        }
                    }
                }
                if(messages == null || messages.size() == showIndex + 1) {
                    dismissLoading();
                }else{
                    showIndex ++;
                    tvMessage.setText(messages.get(showIndex));
                    tvMessage.setTranslationX(-200);
                    tvMessage.animate().setDuration(400).translationXBy(200).start();
                    tvLeft.setText(btnTxts.get(showIndex));
                }
                break;
            case R.id.tv_option_right:
                dismissLoading();
                if(listener != null){
                    listener.onBtnRightClick();
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
