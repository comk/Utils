package com.mayhub.utils.common;


import android.util.SparseIntArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mayhub.utils.ListenActivity;

import java.lang.ref.WeakReference;

/**
 * Created by comkdai on 2017/4/6.
 */
public class PlayerUtils implements MediaPlayerUtils.PlayerListener{
    private static final String TAG = "PlayerUtils";
    private static final int[] LOOP_COUNTS = new int[]{2,5,10,Integer.MAX_VALUE};
    private static PlayerUtils instance;
    private SparseIntArray loopStartTimes = new SparseIntArray();
    private SparseIntArray loopEndTimes = new SparseIntArray();
    private SparseIntArray loopCounts = new SparseIntArray(2);
    private int loopStartTime;
    private int loopEndTime;
    private boolean isPassageLooping = false;
    private boolean isSentenceLooping = false;
    private int loopTimes = 1;
    private WeakReference<SeekBar> refSeekBar;
    private WeakReference<View> refSentence;
    private WeakReference<ListenActivity.AudioPassageAdapter> refAdapter;
    private StatusBean sentenceStatusBean;
    private WeakReference<View> refPlay;
    private StatusBean playStatusBean;
    private WeakReference<TextView> refSpeed;
    private WeakReference<TextView> refLoopCount;
    private WeakReference<View> refPassage;
    private StatusBean passageStatusBean;
    private WeakReference<TextView> refStartTime;
    private WeakReference<TextView> refEndTime;
    private final Object lock = new Object();

    public static class StatusBean{

        public StatusBean(String onTxt, String offTxt) {
            this.onTxt = onTxt;
            this.offTxt = offTxt;
            isTxt = true;
        }

        public StatusBean(int onResId, int offResId) {
            this.onResId = onResId;
            this.offResId = offResId;
            isTxt = false;
        }

        boolean isTxt = true;
        String onTxt;
        String offTxt;
        int onResId;
        int offResId;
    }

    private PlayerUtils(){
        MediaPlayerUtils.getInstance().setPlayerListener(this);
    }

    public static PlayerUtils getInstance() {
        if(instance == null){
            synchronized (PlayerUtils.class){
                if(instance == null){
                    instance = new PlayerUtils();
                }
            }
        }
        return instance;
    }

    public void play(String path, boolean isAutoPlay){
        MediaPlayerUtils.getInstance().setAutoPlay(isAutoPlay);
        MediaPlayerUtils.getInstance().setPlayPath(path);
    }

    public void hookWithAdapter(ListenActivity.AudioPassageAdapter adapter){
        refAdapter = new WeakReference<>(adapter);
    }

    private void updateAdapter(final int index){
        if(refAdapter != null){
            final ListenActivity.AudioPassageAdapter adapter = refAdapter.get();
            if(adapter != null){
                ThreadUtils.getInstance().startUIWork(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateShowIndex(index);
                    }
                });
            }
        }
    }

    public void hookWithSententeceLoopStatus(View view, StatusBean statusBean){
        refSentence = new WeakReference<>(view);
        sentenceStatusBean = statusBean;
    }

    public void hookWithPlayStatus(View view, StatusBean statusBean){
        refPlay = new WeakReference<>(view);
        playStatusBean = statusBean;
    }

    public void hookWithLoopCountStatus(TextView view){
        refLoopCount = new WeakReference<>(view);
    }

    public void hookWithSpeedStatus(TextView view){
        refSpeed = new WeakReference<>(view);
    }

    public void hookWithPassageLoopStatus(View view, StatusBean statusBean){
        refPassage = new WeakReference<>(view);
        passageStatusBean = statusBean;
    }

    public void hookWithStartTime(TextView textView){
        refStartTime = new WeakReference<>(textView);
    }

    public void hookWithEndTime(TextView textView){
        refEndTime = new WeakReference<>(textView);
    }

    private void updateSentenceStatus(){
        updateStatus(refSentence, sentenceStatusBean, isSentenceLooping);
    }

    private void updatePassageStatus(){
        updateStatus(refPassage, passageStatusBean, isPassageLooping);
    }

    private void updatePlayStatus(){
        updateStatus(refPlay, playStatusBean, MediaPlayerUtils.getInstance().isPlaying());
    }

    private void updateStatus(WeakReference<View> ref, final StatusBean statusBean, final boolean isTrue){
        if(ref != null) {
            final View view = ref.get();
            if (view != null && statusBean != null) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isTrue) {
                            if (statusBean.isTxt) {
                                ((TextView) view).setText(statusBean.offTxt);
                            } else {
                                ((ImageView) view).setImageResource(statusBean.offResId);
                            }
                        } else {
                            if (statusBean.isTxt) {
                                ((TextView) view).setText(statusBean.onTxt);
                            } else {
                                ((ImageView) view).setImageResource(statusBean.onResId);
                            }
                        }
                    }
                });
            }
        }
    }

    private void updateStartTime(final int currentPos){
        final TextView tv = refStartTime.get();
        if(tv != null){
            tv.post(new Runnable() {
                @Override
                public void run() {
                    tv.setText(TimeUtils.getInstance().getPlayTime(currentPos));
                }
            });
        }
    }

    private void updateEndTime(final int currentPos){
        final TextView tv = refEndTime.get();
        if(tv != null){
            tv.post(new Runnable() {
                @Override
                public void run() {
                    tv.setText(TimeUtils.getInstance().getPlayTime(currentPos));
                }
            });
        }
    }

    public void hookWithSeekBar(SeekBar seekBar){
        refSeekBar = new WeakReference<>(seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    updateStartTime(progress * MediaPlayerUtils.getInstance().getDuration() / 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.setTag(1);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setTag(null);
                if(seekBar.getTag() == null){
                    MediaPlayerUtils.getInstance().seekTo(seekBar.getProgress() * MediaPlayerUtils.getInstance().getDuration() / 100);
                }
            }
        });
    }

    private void updateSeekBar(int max, int progress){
        SeekBar seekBar = refSeekBar.get();
        if(seekBar != null && seekBar.getTag() == null){
            seekBar.setProgress(progress* seekBar.getMax() / max );
            updateStartTime(progress);
        }
    }

    public void play(String path){
        play(path, false);
    }

    public void seekTo(int position){
        MediaPlayerUtils.getInstance().seekTo(position);
    }

    public void switchPlayOrPause(){
        if(MediaPlayerUtils.getInstance().isPlaying()) {
            MediaPlayerUtils.getInstance().pause();
        }else{
            MediaPlayerUtils.getInstance().start();
        }
    }

    public void addSpeed(float speed){
        MediaPlayerUtils.getInstance().setSpeed(MediaPlayerUtils.getInstance().getSpeed() + speed);
        updateSpeedStatus();
    }

    public void subSpeed(float speed){
        MediaPlayerUtils.getInstance().setSpeed(MediaPlayerUtils.getInstance().getSpeed() - speed);
        updateSpeedStatus();
    }

    private int findCurrentStartTimeIndex(){
        final int currentPos = MediaPlayerUtils.getInstance().getCurrentPosition();
        for (int i = loopStartTimes.size() - 1; i < loopStartTimes.size(); i--) {
            if(currentPos > loopStartTimes.get(i)){
                return i;
            }
        }
        return 0;
    }

    private void startSentenceLoop(){
        isSentenceLooping = true;
        final int index = findCurrentStartTimeIndex();
        updateAdapter(index);
        loopStartTime = loopStartTimes.get(index);
        loopEndTime = loopEndTimes.get(index);
        MediaPlayerUtils.getInstance().setRefreshInterval(10);
    }

    public boolean toggleSentenceLoop(){
        if(isSentenceLooping){
            isSentenceLooping = false;
            MediaPlayerUtils.getInstance().setRefreshInterval(200);
        }else{
            startSentenceLoop();
            updateLoopStatus();
        }
        updateSentenceStatus();
        return isSentenceLooping;
    }

    public boolean togglePassageLoop(){
        if(isPassageLooping){
            isPassageLooping = false;
        }else{
            startPassageLoop();
        }
        updatePassageStatus();
        return isPassageLooping;
    }

    private void startPassageLoop(){
        isPassageLooping = true;
        isSentenceLooping = false;
        MediaPlayerUtils.getInstance().setRefreshInterval(200);
    }

    public void setSentenceLoopInfos(int[] startTimes, int[] endTimes){
        for (int i = 0; i < startTimes.length; i++) {
            loopStartTimes.put(i, startTimes[i]);
            loopEndTimes.put(i, endTimes[i]);
        }
    }

    public void setSentenceLoopInfo(int startTime, int endTime){
        loopStartTime = startTime;
        loopEndTime = endTime;
        if(isSentenceLooping) {
            final int index = loopStartTimes.indexOfValue(startTime);
            updateAdapter(index);
            final int currentPos = MediaPlayerUtils.getInstance().getCurrentPosition();
            if (currentPos > endTime || currentPos < startTime){
                seekTo(startTime);
            }
        }
    }

    public void setLoopTimes(int loopTimes) {
        this.loopTimes = loopTimes;
    }

    public void cancelLoop(){
        loopEndTime = -1;
        loopStartTime = -1;
        isPassageLooping = false;
        loopCounts.clear();
        isSentenceLooping = false;
        MediaPlayerUtils.getInstance().setRefreshInterval(200);
    }

    private void updateLoopStatus(){
        if(refLoopCount != null){
            final TextView textView = refLoopCount.get();
            if(textView != null){
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(String.format("%s/%s", loopCounts.get(loopStartTime, 0) + 1, loopTimes + 1));
                    }
                });
            }
        }
    }

    private void updateSpeedStatus(){
        if(refSpeed != null){
            final TextView textView = refSpeed.get();
            if(textView != null){
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(String.format("当前语速：%s倍", MediaPlayerUtils.getInstance().getSpeed()));
                    }
                });
            }
        }
    }

    private void prepareForLoopCount(){
        int count = loopCounts.get(loopStartTime, 0);
        if(count > 0){
            if(count == loopTimes){
                loopCounts.clear();
                int index = loopStartTimes.indexOfValue(loopStartTime);
                if(index < loopStartTimes.size() - 1){
                    loopStartTime = loopStartTimes.get(index + 1);
                    loopEndTime = loopEndTimes.get(index + 1);
                    updateAdapter(index + 1);
                }else{
                    MediaPlayerUtils.getInstance().pause();
                }
            }else {
                loopCounts.put(loopStartTime, count + 1);
            }
        }else{
            loopCounts.put(loopStartTime, 1);
        }
        updateLoopStatus();
    }

    @Override
    public void onPrepared(String playPath, int duration) {
        updateEndTime(duration);
        if(isSentenceLooping && loopStartTime > 0) {
            prepareForLoopCount();
            seekTo(loopStartTime);
        }
        updateSpeedStatus();
    }

    @Override
    public void onStart(String playPath, int curPosition) {
        updatePlayStatus();
        updateSpeedStatus();
    }

    @Override
    public void onPause(String playPath, int curPosition) {
        updatePlayStatus();
        updateSpeedStatus();
    }

    @Override
    public void onSeekComplete(String playPath, int seekPosition) {
        updateSeekBar(MediaPlayerUtils.getInstance().getDuration(), seekPosition);
    }

    @Override
    public void onPlaying(String playPath, int curPosition) {
        if(isSentenceLooping && curPosition > loopEndTime){
            prepareForLoopCount();
            seekTo(loopStartTime);
        }
        updateSeekBar(MediaPlayerUtils.getInstance().getDuration(), MediaPlayerUtils.getInstance().getCurrentPosition());
    }

    @Override
    public void onResume(String playPath, int curPosition) {
        updatePlayStatus();
        updateSpeedStatus();
    }

    @Override
    public void onStop(String playPath) {
        updatePlayStatus();
        updateSpeedStatus();
    }

    @Override
    public void onComplete(String playPath) {
        updateStartTime(0);
        if(isPassageLooping){
            play(playPath, true);
        }
        updatePlayStatus();
        updateSpeedStatus();
    }

    private void clearWeakRef(WeakReference weakReference){
        if(weakReference != null){
            weakReference.clear();
        }
    }

    public void destroy(){
        MediaPlayerUtils.getInstance().destroy();
        clearWeakRef(refEndTime);
        clearWeakRef(refSeekBar);
        clearWeakRef(refStartTime);
        clearWeakRef(refPassage);
        clearWeakRef(refSentence);
        instance = null;
    }

}
