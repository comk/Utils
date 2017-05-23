package com.mayhub.utils.speed;

/**
 * Created by comkdai on 2017/5/22.
 */
public abstract class IMediaPlayer{
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

    PlayerListener playerListener;


    public abstract void setAutoPlay(boolean autoPlay);
    public abstract void setPlayPath(String playPath);
    public abstract void seekTo(int position);
    public abstract boolean isPlaying();
    public abstract int getDuration();
    public abstract int getCurrentPosition();
    public abstract void start();
    public abstract void pause();
    public abstract void setSpeed(float speed);
    public abstract float getSpeed();
    public abstract void destroy();
    public abstract void setRefreshInterval(int interval);

    public void setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }
}
