package com.mayhub.utils.dialog;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mayhub.utils.R;
import com.mayhub.utils.adapter.BasePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by comkdai on 2017/3/22.
 */
public class ImageViewerDialog extends Dialog implements Animator.AnimatorListener{

    private static final int ANIM_DURATION = 400;

    public static ImageViewStatus create(String local, String remote, ImageView imageView){
        ImageViewStatus imageViewStatus = new ImageViewStatus();
        imageViewStatus.isRemoteLoaded = false;
        imageViewStatus.localPath = local;
        imageViewStatus.remotePath = remote;
        imageView.getLocationInWindow(imageViewStatus.centerScreenLoc);
        imageViewStatus.localSize[0] = imageView.getMeasuredWidth();
        imageViewStatus.localSize[1] = imageView.getMeasuredHeight();
        imageViewStatus.centerScreenLoc[0] += imageViewStatus.localSize[0] / 2;
        imageViewStatus.centerScreenLoc[1] += imageViewStatus.localSize[1] / 2;
        return imageViewStatus;
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

    public static class ImageViewStatus{
        private String localPath;
        private String remotePath;
        private int[] localSize = new int[2];
        private int[] centerScreenLoc = new int[2];
        private boolean isRemoteLoaded = false;
    }
    private boolean isAnimating = false;
    private ViewPager viewPager;
    private View bg;
    private ImagePagerAdapter imagePagerAdapter;
    private int[] screenCenterLoc = new int[2];
    private int width;

    public ImageViewerDialog(Context context) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        try {
            if (Build.VERSION.SDK_INT >= 11)
                getWindow().setFlags(16777216, 16777216);
        } catch (Exception e) {
        }
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_image_viewer);
        initView();
    }

    private void initView() {
        bg = findViewById(R.id.v_bg);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(imagePagerAdapter = new ImagePagerAdapter(null, new BasePagerAdapter.PagerItemClickListener() {
            @Override
            public void onItemClick(int pos, Object o) {
                dismiss();
            }
        }));
    }

    public void showDialog(int showIndex, ArrayList<ImageViewStatus> views){
        if(isAnimating){
            return;
        }
        bg.setAlpha(0f);
        viewPager.setTranslationX(0);
        viewPager.setTranslationY(0);
        viewPager.setScaleX(1);
        viewPager.setScaleX(1);
        imagePagerAdapter.resetData(views);
        viewPager.setCurrentItem(showIndex);
        show();
        final ImageViewStatus imageViewStatus = views.get(showIndex);
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                prepareAnimStatus(imageViewStatus, true);
            }
        });
    }

    @Override
    public void dismiss() {
        if(isAnimating){
            return;
        }
        prepareAnimStatus(imagePagerAdapter.getDatas().get(viewPager.getCurrentItem()), false);
        bg.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(ImageViewerDialog.this.isShowing()) {
                    if(ImageViewerDialog.this.getWindow().getDecorView() != null &&
                            Build.VERSION.SDK_INT >= 19 &&
                            ImageViewerDialog.this.getWindow().getDecorView().isAttachedToWindow()){
                        ImageViewerDialog.super.dismiss();
                    }else{
                        ImageViewerDialog.super.dismiss();
                    }
                }
            }
        }, ANIM_DURATION);
    }

    private void prepareAnimStatus(ImageViewStatus imageViewStatus, boolean isInAnimation){
        if(imageViewStatus.isRemoteLoaded){//scale
            doAnimation(isInAnimation, true, imageViewStatus);
        }else{//translate
            doAnimation(isInAnimation, false, imageViewStatus);
        }
    }

    private void doAnimation(boolean isInAnimation, boolean isScale, ImageViewStatus imageViewStatus){
        if(width == 0){
            width = bg.getMeasuredWidth();
            screenCenterLoc[0] = width / 2;
            screenCenterLoc[1] = bg.getMeasuredHeight() / 2;
        }
        if(isInAnimation){
            int dx = screenCenterLoc[0] - imageViewStatus.centerScreenLoc[0];
            int dy = screenCenterLoc[1] - imageViewStatus.centerScreenLoc[1];
            float translateX = viewPager.getTranslationX();
            float translateY = viewPager.getTranslationY();
            float scaleSize = (float)width / imageViewStatus.localSize[0];
            viewPager.setTranslationX(translateX - dx);
            viewPager.setTranslationY(translateY - dy);
            ViewPropertyAnimator viewPropertyAnimator = viewPager.animate();
            viewPropertyAnimator.translationXBy(dx).translationYBy(dy);
            if(isScale) {
                viewPropertyAnimator.scaleX(1f/scaleSize).scaleY(1f/scaleSize);
                viewPropertyAnimator.scaleXBy(scaleSize).scaleYBy(scaleSize);
            }
            viewPropertyAnimator.setDuration(ANIM_DURATION);
            viewPropertyAnimator.setListener(this);
            viewPropertyAnimator.start();
            bg.animate().setDuration(ANIM_DURATION).alphaBy(1).start();
        }else{
            int dx = screenCenterLoc[0] - imageViewStatus.centerScreenLoc[0];
            int dy = screenCenterLoc[1] - imageViewStatus.centerScreenLoc[1];
            float scaleSize = (float)width / imageViewStatus.localSize[0];
            ViewPropertyAnimator viewPropertyAnimator = viewPager.animate();
            viewPropertyAnimator.translationXBy(-dx).translationYBy(-dy);
            if(isScale) {
                viewPropertyAnimator.scaleXBy(-scaleSize).scaleYBy(-scaleSize);
            }
            viewPropertyAnimator.setDuration(ANIM_DURATION);
            viewPropertyAnimator.setListener(this);
            viewPropertyAnimator.start();
            bg.animate().setDuration(ANIM_DURATION).alphaBy(-1).start();
        }
    }

    private static class ImagePagerAdapter extends BasePagerAdapter<ImageViewStatus>{

        private int mChildCount;

        public void resetData(ArrayList<ImageViewStatus> list){
            if(list != null) {
                getDatas().clear();
                getDatas().addAll(list);
            }
            notifyDataSetChanged();
        }

        public ImagePagerAdapter(List<ImageViewStatus> data) {
            super(data);
        }

        public ImagePagerAdapter(List<ImageViewStatus> data, PagerItemClickListener pagerItemClickListener) {
            super(data, pagerItemClickListener);
        }

        @Override
        public View createView(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_image_viewer_item, container, false);
            ImageItemHolder imageItemHolder = new ImageItemHolder();
            imageItemHolder.frameLayout = (FrameLayout) view;
            imageItemHolder.imageView = (ImageView) view.findViewById(R.id.image);
            imageItemHolder.pb = (ProgressBar) view.findViewById(R.id.pb);
            view.setTag(imageItemHolder);
            return view;
        }

        @Override
        public void bindView(View bindView, int position) {
            if(bindView.getTag() instanceof ImageItemHolder){
                final ImageItemHolder imageItemHolder = (ImageItemHolder) bindView.getTag();
                final ImageViewStatus imageViewStatus = getDatas().get(position);
                if(imageViewStatus.isRemoteLoaded){
                    imageItemHolder.pb.setVisibility(View.GONE);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageItemHolder.imageView.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    imageItemHolder.imageView.setLayoutParams(layoutParams);
                    imageItemHolder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    //TODO LOAD IMAGE
                    imageItemHolder.imageView.setImageResource(R.drawable.exo_controls_fastforward);
                }else {
                    imageItemHolder.pb.setVisibility(View.VISIBLE);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageItemHolder.imageView.getLayoutParams();
                    layoutParams.width = imageViewStatus.localSize[0];
                    layoutParams.height = imageViewStatus.localSize[1];
                    imageItemHolder.imageView.setLayoutParams(layoutParams);
                    imageItemHolder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    //TODO LOAD IMAGE
                    imageItemHolder.imageView.setImageResource(R.drawable.exo_controls_fastforward);
                }
            }
        }

        private class ImageItemHolder{
            private FrameLayout frameLayout;
            private ImageView imageView;
            private ProgressBar pb;
        }

        @Override
        public int getItemPosition (Object object)
        {
            if (mChildCount > 0) {
                mChildCount --;
                return PagerAdapter.POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        @Override
        public void notifyDataSetChanged ()
        {
            mChildCount = getCount();
            super.notifyDataSetChanged ();
        }
    }

}
