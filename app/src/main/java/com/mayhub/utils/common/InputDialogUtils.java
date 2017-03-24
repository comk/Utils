package com.mayhub.utils.common;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mayhub.utils.R;

/**
 * Created by comkdai on 2017/3/23.
 */
public class InputDialogUtils implements View.OnClickListener, Animator.AnimatorListener{

    private static final int DEFAULT_DURATION = 200;

    public interface AlertListener{
        void onDismiss();
        void onBtnClick(String inputText);
        void onBtnLeftClick(String inputText);
        void onBtnRightClick(String inputText);
    }

    public static abstract class AlertListenerAdapter implements AlertListener{

        @Override
        public void onDismiss() {

        }

        @Override
        public void onBtnClick(String inputText) {

        }

        @Override
        public void onBtnLeftClick(String inputText) {

        }

        @Override
        public void onBtnRightClick(String inputText) {

        }
    }

    private InputDialogUtils(){

    }

    public static InputDialogUtils getInstance(){
        if(instance == null) {
            synchronized (InputDialogUtils.class) {
                if (instance == null){
                    instance = new InputDialogUtils();
                }
            }
        }
        return instance;
    }

    private static InputDialogUtils instance;

    private View viewRoot;

    private boolean isCancelable = true;

    private boolean isAnimating = false;

    private TextView tvDialogTitle;

    private TextView tvLeft;

    private TextView tvRight;

    private EditText etContent;

    private AlertListener listener;

    public void showOptionAlert(Context context, boolean isCancelable, String dialogTitle, String hintText, String text, String btnLeftTxt, String btnRightTxt){
        synchronized (InputDialogUtils.class) {
            if(isAnimating && isShowing()){
                return;
            }
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
                viewRoot = View.inflate(context, R.layout.layout_input_dialog, null);
                tvDialogTitle = (TextView) viewRoot.findViewById(R.id.tv_dialog_title);
                tvLeft = (TextView) viewRoot.findViewById(R.id.tv_option_left);
                tvRight = (TextView) viewRoot.findViewById(R.id.tv_option_right);
                etContent = (EditText) viewRoot.findViewById(R.id.et_content);
                etContent.setHint(hintText);
                etContent.setText(text);
                this.isCancelable = isCancelable;
                tvDialogTitle.setText(dialogTitle);
                tvLeft.setText(btnLeftTxt);
                tvRight.setText(btnRightTxt);
                viewRoot.setAlpha(0f);
                viewRoot.setOnClickListener(this);
                tvLeft.setOnClickListener(this);
                tvRight.setOnClickListener(this);
                viewGroup.addView(viewRoot);
                viewRoot.animate().setDuration(DEFAULT_DURATION).setListener(this).alphaBy(1).start();
                InputMethodTool.showInputMethod(activity, etContent);
            }
        }
    }

    public void showAlert(Context context, boolean isCancelable, String dialogTitle, String hintText, String text, String btnTxt){
        synchronized (InputDialogUtils.class) {
            if(isAnimating && isShowing()){
                return;
            }
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
                viewRoot = View.inflate(context, R.layout.layout_input_dialog, null);
                tvDialogTitle = (TextView) viewRoot.findViewById(R.id.tv_dialog_title);
                tvLeft = (TextView) viewRoot.findViewById(R.id.tv_option_left);
                tvRight = (TextView) viewRoot.findViewById(R.id.tv_option_right);
                etContent = (EditText) viewRoot.findViewById(R.id.et_content);
                etContent.setHint(hintText);
                etContent.setText(text);
                tvRight.setVisibility(View.GONE);
                this.isCancelable = isCancelable;
                tvDialogTitle.setText(dialogTitle);
                tvLeft.setText(btnTxt);
                viewRoot.setAlpha(0f);
                viewRoot.setOnClickListener(this);
                tvLeft.setOnClickListener(this);
                tvRight.setOnClickListener(this);
                viewGroup.addView(viewRoot);
                viewRoot.animate().setDuration(DEFAULT_DURATION).setListener(this).alphaBy(1).start();
                InputMethodTool.showInputMethod(activity, etContent);
            }
        }
    }

    public InputDialogUtils listener(AlertListener alertListener){
        listener = alertListener;
        return this;
    }

    public boolean isShowing(){
        synchronized (InputDialogUtils.class) {
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
        synchronized (InputDialogUtils.class) {
            dismissLoading();
            viewRoot = null;
            etContent = null;
            tvDialogTitle = null;
            tvRight = null;
            tvLeft = null;
            instance = null;
            listener = null;
        }
    }

    public void dismissLoading(){
        synchronized (InputDialogUtils.class) {
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
                dismissLoading();
                if(listener != null){
                    if(tvRight.getVisibility() == View.VISIBLE) {
                        listener.onBtnLeftClick(etContent.getText().toString());
                    }else {
                        listener.onBtnClick(etContent.getText().toString());
                    }
                }
                break;
            case R.id.tv_option_right:
                dismissLoading();
                if(listener != null){
                    listener.onBtnRightClick(etContent.getText().toString());
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
