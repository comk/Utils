package com.mayhub.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.mayhub.utils.R;


/**
 * Created by comkdai on 2017/3/9.
 */
public abstract class BaseDialog extends Dialog {

    public static final int TYPE_CENTER = 0;
    public static final int TYPE_BOTTOM = 1;
    public static final int TYPE_TOP = 2;

    private View background;

    private View content;

    private FrameLayout root;

    public BaseDialog(Context context) {
        super(context,android.R.style.Theme_Black_NoTitleBar);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        this.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
        setContentView(R.layout.layout_base_dialog);
        background = findViewById(R.id.v_bg);
        root = (FrameLayout) findViewById(R.id.root);
        setCancelable(true);
    }

    @Override
    public void show() {
        background.setAlpha(0);
        content.setTranslationY(content.getMeasuredHeight());
        super.show();
        background.animate().alphaBy(1.0f).setDuration(300).start();
        content.animate().setDuration(300).translationYBy(-content.getMeasuredHeight()).start();
    }

    public void addContent(View content){
        addContent(content, 0);
    }

    public void addContent(View content, int type){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        switch (type){
            case TYPE_CENTER:
                layoutParams.gravity = Gravity.CENTER;
                root.addView(content, layoutParams);
                break;
            case TYPE_BOTTOM:
                layoutParams.gravity = Gravity.BOTTOM;
                root.addView(content, layoutParams);
                break;
            case TYPE_TOP:
                layoutParams.gravity = Gravity.TOP;
                root.addView(content, layoutParams);
                break;
        }
    }

    @Override
    public void dismiss() {
        background.animate().alphaBy(-1.0f).setDuration(300).start();
        content.animate().setDuration(300).translationYBy(content.getMeasuredHeight()).start();
        content.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(BaseDialog.this.isShowing()) {
                    if(Build.VERSION.SDK_INT >= 19){
                        if(BaseDialog.this.getWindow().getDecorView() != null &&
                                BaseDialog.this.getWindow().getDecorView().isAttachedToWindow()) {
                            BaseDialog.super.dismiss();
                        }
                    }else{
                        BaseDialog.super.dismiss();
                    }
                }
            }
        }, 400);
    }
}
