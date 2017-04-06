package com.mayhub.utils.common;


import android.widget.SeekBar;

import java.lang.ref.WeakReference;

/**
 * Created by comkdai on 2017/4/6.
 */
public class PlayerUtils implements MediaPlayerUtils.PlayerListener{
    private static final String TAG = "PlayerUtils";

    private static PlayerUtils instance;
    private long loopStartTime;
    private long loopEndTime;
    private boolean isLooping = false;
    private boolean isSentenceLooping = false;
    private WeakReference<SeekBar> refSeekBar;
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

    public void hookWithSeekBar(SeekBar seekBar){
        refSeekBar = new WeakReference<>(seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

    private void updateSeekBar(long max, long progress){
        SeekBar seekBar = refSeekBar.get();
        if(seekBar != null && seekBar.getTag() == null){
            seekBar.setProgress((int) (progress* seekBar.getMax() / max ));
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
    }

    public void subSpeed(float speed){
        MediaPlayerUtils.getInstance().setSpeed(MediaPlayerUtils.getInstance().getSpeed() - speed);
    }

    public void startLoop(){
        isLooping = true;
        isSentenceLooping = true;
        MediaPlayerUtils.getInstance().setRefreshInterval(10);
    }

    public void startPassageLoop(){
        startLoop();
        isSentenceLooping = false;
        MediaPlayerUtils.getInstance().setRefreshInterval(200);
    }

    public void setSentenceLoopInfo(long startTime, long endTime){
        loopStartTime = startTime;
        loopEndTime = endTime;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void cancelLoop(){
        loopEndTime = -1;
        loopStartTime = -1;
        isLooping = false;
        MediaPlayerUtils.getInstance().setRefreshInterval(200);
    }

    @Override
    public void onPrepared(String playPath, int duration) {
        if(isLooping){
            if(isSentenceLooping && loopStartTime > 0) {
                seekTo((int) loopStartTime);
            }
        }
    }

    @Override
    public void onStart(String playPath, int curPosition) {

    }

    @Override
    public void onPause(String playPath, int curPosition) {

    }

    @Override
    public void onSeekComplete(String playPath, int seekPosition) {

    }

    @Override
    public void onPlaying(String playPath, int curPosition) {
        if(isSentenceLooping && curPosition > loopEndTime){
            MediaPlayerUtils.getInstance().seekTo((int) loopStartTime);
        }
        updateSeekBar(MediaPlayerUtils.getInstance().getDuration(), MediaPlayerUtils.getInstance().getCurrentPosition());
    }

    @Override
    public void onResume(String playPath, int curPosition) {

    }

    @Override
    public void onStop(String playPath) {

    }

    @Override
    public void onComplete(String playPath) {
        if(isLooping){
            play(playPath, true);
        }
    }
}
