package com.mayhub.utils.speed;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;


import com.mayhub.utils.activity.App;
import com.mayhub.utils.common.MLogUtil;
import com.mayhub.utils.common.ThreadUtils;
import com.mayhub.utils.common.ToastUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by comkdai on 2017/3/24.
 */
public class TrackUtils extends IMediaPlayer implements Track.OnCompletionListener,
        Track.OnErrorListener,
        Track.OnInfoListener,
        Track.OnPreparedListener,
        Track.OnSeekCompleteListener{

    private static final String TAG = "TrackUtils";

    private static final int DURATION_REFRESH_INTERVAL = 2;

    private int refreshInterval = DURATION_REFRESH_INTERVAL;

    private static TrackUtils instance;

    private boolean isMediaPlayerInit = false;
    private boolean isAutoPlay = false;
    private Track mediaPlayer;
    private AssetFileDescriptor afd;
    private ArrayList<String> playList = new ArrayList<>();
    private String curPath;
    private float speed = 1.0f;


    private Runnable audioPlaying = new Runnable() {
        @Override
        public void run() {
            if(playerListener != null && isPlaying()){
                playerListener.onPlaying(curPath, getCurrentPosition());
            }
            ThreadUtils.getInstance().getWorkHandler().removeCallbacks(this);
            ThreadUtils.getInstance().getWorkHandler().postDelayed(this, refreshInterval);
        }
    };

    private TrackUtils(){
        mediaPlayer = new Track(App.getInstance());
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        float defaultVolume = getSystemVolume();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if(defaultVolume <= 0f){
            ToastUtils.getInstance().showShortToast(App.getInstance(), "请把音量调大");
        }else {
            mediaPlayer.setVolume(defaultVolume, defaultVolume);
        }
    }

    public static TrackUtils getInstance(){
        if(instance == null){
            synchronized (TrackUtils.class){
                if(instance == null){
                    instance = new TrackUtils();
                }
            }
        }
        return instance;
    }

    public float getSystemVolume(){
        AudioManager am = (AudioManager) App.getInstance().getSystemService(Context.AUDIO_SERVICE);
        int volumeLevel = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return (float) volumeLevel / maxVolume;
    }

    public void setAutoPlay(boolean autoPlay) {
        synchronized (TrackUtils.class) {
            isAutoPlay = autoPlay;
            if(isAutoPlay && isMediaPlayerInit){
                start();
            }
        }
    }

    @Override
    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public void setPlayPath(File file){
        if(file != null && file.exists()) {
            setPlayPath(file.getAbsolutePath());
        }
    }

    public void setPlayPathList(ArrayList<String> list){
        if(list != null){
            playList.clear();
            playList.addAll(list);
            if(playList.size() > 0){
                setPlayPath(playList.get(0));
            }
        }
    }

    public boolean isPlaying(){
        synchronized (TrackUtils.class){
            return mediaPlayer.isPlaying();
        }
    }

    private boolean isAssetsFile(String playPath){
        return playPath.contains("assets://");
    }

    public void setPlayPath(String playPath){
        synchronized (TrackUtils.class) {
            if (curPath == null || !curPath.equals(playPath)) {
                curPath = playPath;
                try {
                    mediaPlayer.reset();
                    isMediaPlayerInit = false;
                    if(afd != null){
                        afd.close();
                    }
                    if(isAssetsFile(playPath)){
                        afd = App.getInstance().getAssets().openFd(playPath.split("//")[1]);
                        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    }else{
                        mediaPlayer.setDataSource(playPath);
                    }
                    mediaPlayer.prepareAsync();
                } catch (Exception e) {
                    destroy();
                    e.printStackTrace();
                }
            }
        }
    }

    public void setPlayerListener(PlayerListener playerListener) {
        synchronized (TrackUtils.class) {
            this.playerListener = playerListener;
        }
    }

    public void destroy(){
       stop();
        if(afd != null){
            try {
                afd.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            afd = null;
        }
        mediaPlayer.reset();
        mediaPlayer.release();
        playerListener = null;
        mediaPlayer = null;
        curPath = null;
        instance = null;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        if(isPlaying()) {
            mediaPlayer.setPlaybackSpeed(speed);
        }
    }

    public void seekTo(int position){
        synchronized (TrackUtils.class) {
            if(isMediaPlayerInit) {
                mediaPlayer.seekTo(position);
            }
        }
    }

    public int getDuration(){
        synchronized (TrackUtils.class){
            if(isMediaPlayerInit){
                return mediaPlayer.getDuration();
            }
            return 0;
        }
    }

    public int getCurrentPosition(){
        synchronized (TrackUtils.class){
            if(isMediaPlayerInit){
                return mediaPlayer.getCurrentPosition();
            }
            return 0;
        }
    }

    public void start(){
        synchronized (TrackUtils.class){
            if(isMediaPlayerInit){
                if(!mediaPlayer.isPlaying()) {
                    final int lastPosition = mediaPlayer.getCurrentPosition();
                    mediaPlayer.start();
                    if(mediaPlayer.getCurrentSpeed() != speed){
                        mediaPlayer.setPlaybackSpeed(speed);
                    }
                    if (playerListener != null) {
                        if (lastPosition > 10) {
                            playerListener.onResume(curPath, mediaPlayer.getCurrentPosition());
                        } else {
                            playerListener.onStart(curPath, mediaPlayer.getCurrentPosition());
                        }
                        ThreadUtils.getInstance().getWorkHandler().post(audioPlaying);
                    }
                }
            }
        }
    }

    public void pause(){
        synchronized (TrackUtils.class){
            if(isMediaPlayerInit){
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    if(playerListener != null){
                        playerListener.onPause(curPath, mediaPlayer.getCurrentPosition());
                    }
                    ThreadUtils.getInstance().getWorkHandler().removeCallbacks(audioPlaying);
                }
            }
        }
    }

    public void stop(){
        synchronized (TrackUtils.class){
            if(isMediaPlayerInit){
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    if(playerListener != null){
                        playerListener.onStop(curPath);
                    }
                    isMediaPlayerInit = false;
                    curPath = null;
                    ThreadUtils.getInstance().getWorkHandler().removeCallbacks(audioPlaying);
                }
            }
        }
    }

    @Override
    public void onCompletion(Track mp) {
        synchronized (TrackUtils.class) {
            if(playList.size() > 0) {
                int idx = playList.indexOf(curPath);
                if(idx == playList.size() - 1){
                    if (playerListener != null) {
                        playerListener.onComplete(curPath);
                    }
                    ThreadUtils.getInstance().getWorkHandler().removeCallbacks(audioPlaying);
                }else{
                    setPlayPath(playList.get(idx + 1));
                }
            }else {
                if (playerListener != null) {
                    playerListener.onComplete(curPath);
                }
                ThreadUtils.getInstance().getWorkHandler().removeCallbacks(audioPlaying);
            }
        }
    }

    @Override
    public boolean onError(Track mp, int what, int extra) {
        synchronized (TrackUtils.class) {
            isMediaPlayerInit = false;
            return true;
        }
    }

    @Override
    public boolean onInfo(Track mp, int what, int extra) {

        return false;
    }

    @Override
    public void onPrepared(final Track mp) {
        synchronized (TrackUtils.class) {
            isMediaPlayerInit = true;
            if (playerListener != null) {
                playerListener.onPrepared(curPath, mp.getDuration());
            }
            MLogUtil.e(TAG, "onPrepared " + curPath);
            if(isAutoPlay){
                start();
                MLogUtil.e(TAG, "AUTO START");
            }
        }
    }

    @Override
    public void onSeekComplete(Track mp) {
        synchronized (TrackUtils.class) {
            if (playerListener != null) {
                playerListener.onSeekComplete(curPath, mp.getCurrentPosition());
            }
        }
    }

}
