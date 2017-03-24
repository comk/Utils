package com.mayhub.utils.common;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.text.TextUtils;

import com.mayhub.utils.MyApplication;

import java.io.File;
import java.io.IOException;

/**
 * Created by comkdai on 2017/3/24.
 */
public class MediaPlayerUtils implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener{

    private static final String KEY_IS_SPEED_SUPPORTED = "is_speed_supported";

    private static final String KEY_IS_ENABLE_SPEED = "key_is_enable_speed";

    private static final String KEY_IS_START_BEFORE_SPEED = "is_start_before_speed";

    public interface PlayerListener{
        void onPrepared(String playPath, int duration);
        void onStart(String playPath, int curPosition);
        void onPause(String playPath, int curPosition);
        void onSeekComplete(String playPath, int seekPosition);
        void onPlaying(String playPath, int curPosition);
        void onResume(String playPath, int curPosition);
        void onStop(String playPath);
        void onComplete(String playPath);
    }

    private static MediaPlayerUtils instance;
    private PlayerListener playerListener;
    private boolean isMediaPlayerInit = false;
    private boolean isAutoPlay = false;
    private MediaPlayer mediaPlayer;
    private String curPath;
    private float speed = 1.0f;
    private MediaPlayerUtils(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        float defaultVolume = getSystemVolume();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if(defaultVolume <= 0f){
            ToastUtils.getInstance().showShortToast(MyApplication.getInstance(), "请把音量调大");
        }else {
            mediaPlayer.setVolume(defaultVolume, defaultVolume);
        }
    }

    public static MediaPlayerUtils getInstance(){
        if(instance == null){
            synchronized (MediaPlayerUtils.class){
                if(instance == null){
                    instance = new MediaPlayerUtils();
                }
            }
        }
        return instance;
    }

    public float getSystemVolume(){
        AudioManager am = (AudioManager) MyApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
        int volumeLevel = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return (float) volumeLevel / maxVolume;
    }

    public void setAutoPlay(boolean autoPlay) {
        synchronized (MediaPlayerUtils.class) {
            isAutoPlay = autoPlay;
        }
    }

    public static void setSpeedNotSupported(){
        LocalValueUtils.getInstance().saveBoolean(KEY_IS_SPEED_SUPPORTED, false);
    }

    public static boolean isSystemSpeedSupported(){
        return Build.VERSION.SDK_INT >= 23 && LocalValueUtils.getInstance().getBoolean(KEY_IS_SPEED_SUPPORTED, true);
    }

    public static void setStartBeforeSpeed(){
        LocalValueUtils.getInstance().saveBoolean(KEY_IS_START_BEFORE_SPEED, true);
    }

    public static boolean getKeyIsStartBeforeSpeed() {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        if(!TextUtils.isEmpty(manufacturer)) {
            if (manufacturer.contains("huawei") || manufacturer.contains("meizu")){
                return true;
            }
        }
        return LocalValueUtils.getInstance().getBoolean(KEY_IS_START_BEFORE_SPEED, false);
    }

    public void setPlayPath(File file){
        if(file != null && file.exists()) {
            setPlayPath(file.getAbsolutePath());
        }
    }

    public void setPlayPath(String playPath){
        synchronized (MediaPlayerUtils.class) {
            if (curPath == null || curPath.equals(playPath)) {
                curPath = playPath;
                try {
                    mediaPlayer.reset();
                    isMediaPlayerInit = false;
                    mediaPlayer.setDataSource(playPath);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    destroy();
                    e.printStackTrace();
                }
            }
        }
    }

    public void setPlayerListener(PlayerListener playerListener) {
        synchronized (MediaPlayerUtils.class) {
            this.playerListener = playerListener;
        }
    }

    public void destroy(){
       stop();
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
        setPlaybackParamsSpeed(speed);
    }

    public void seekTo(int position){
        synchronized (MediaPlayerUtils.class) {
            if(isMediaPlayerInit) {
                mediaPlayer.seekTo(position);
            }
        }
    }

    public int getDuration(){
        synchronized (MediaPlayerUtils.class){
            if(isMediaPlayerInit){
                return mediaPlayer.getDuration();
            }
            return 0;
        }
    }

    public int getCurrentPosition(){
        synchronized (MediaPlayerUtils.class){
            if(isMediaPlayerInit){
                return mediaPlayer.getCurrentPosition();
            }
            return 0;
        }
    }

    public void start(){
        synchronized (MediaPlayerUtils.class){
            if(isMediaPlayerInit){
                if(!mediaPlayer.isPlaying()) {
                    final int lastPosition = mediaPlayer.getCurrentPosition();
                    if(!getKeyIsStartBeforeSpeed()) {
                        setPlaybackParamsSpeed(speed);
                    }
                    mediaPlayer.start();
                    if(getKeyIsStartBeforeSpeed()) {
                        setPlaybackParamsSpeed(speed);
                    }
                    if (playerListener != null) {
                        if (lastPosition > 10) {
                            playerListener.onResume(curPath, mediaPlayer.getCurrentPosition());
                        } else {
                            playerListener.onStart(curPath, mediaPlayer.getCurrentPosition());
                        }
                    }
                }
            }
        }
    }

    public void pause(){
        synchronized (MediaPlayerUtils.class){
            if(isMediaPlayerInit){
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    if(mediaPlayer.isPlaying()){
                        setStartBeforeSpeed();
                        stop();
                    }
                    if(playerListener != null){
                        playerListener.onPause(curPath, mediaPlayer.getCurrentPosition());
                    }
                }
            }
        }
    }

    private void setSpeedFeature(){
        if(isSystemSpeedSupported() && !getKeyIsStartBeforeSpeed()){
            try {
                float tempSpeed = 1.0f;
                if (Build.MANUFACTURER.toLowerCase().contains("xiaomi")){
                    tempSpeed = 1.01f;
                }
                setSpeed(mediaPlayer, tempSpeed);
                if(mediaPlayer.isPlaying()) {
                    setStartBeforeSpeed();
                }
            }catch (IllegalArgumentException e){
                setSpeedNotSupported();
                ToastUtils.getInstance().showShortToast(MyApplication.getInstance(), "请退出程序重新启动再试变速功能");
                MLogUtil.p(e);
                destroy();
            }
        }
    }

    private void setPlaybackParamsSpeed(float curSpeed) {
        try {
            setSpeed(mediaPlayer, curSpeed);
        }catch (IllegalArgumentException e){
            MLogUtil.p(e);
            setSpeedNotSupported();
            ToastUtils.getInstance().showShortToast(MyApplication.getInstance(), "请退出程序重新启动再试");
            destroy();
        }catch (IllegalStateException e){
            MLogUtil.p(e);
            ToastUtils.getInstance().showShortToast(MyApplication.getInstance(), "音频加载失败，稍候再试");
            destroy();
        }
    }

    public void setSpeed(MediaPlayer mediaPlayer, float speed){
        if(Build.VERSION.SDK_INT > 22) {
            PlaybackParams playbackParams = new PlaybackParams();
            playbackParams.setSpeed(speed);
            mediaPlayer.setPlaybackParams(playbackParams);
        }
    }

    public void stop(){
        synchronized (MediaPlayerUtils.class){
            if(isMediaPlayerInit){
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    if(playerListener != null){
                        playerListener.onStop(curPath);
                    }
                    isMediaPlayerInit = false;
                }
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        synchronized (MediaPlayerUtils.class) {
            if (playerListener != null) {
                playerListener.onComplete(curPath);
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        synchronized (MediaPlayerUtils.class) {
            isMediaPlayerInit = false;
            return true;
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {

        return false;
    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        synchronized (MediaPlayerUtils.class) {
            isMediaPlayerInit = true;
            if (playerListener != null) {
                playerListener.onPrepared(curPath, mp.getDuration());
            }
            setSpeedFeature();
            if(isAutoPlay){
                start();
            }
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        synchronized (MediaPlayerUtils.class) {
            if (playerListener != null) {
                playerListener.onSeekComplete(curPath, mp.getCurrentPosition());
            }
        }
    }

}
