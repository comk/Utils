package com.mayhub.utils;

import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import com.mayhub.utils.activity.BaseFragmentActivity;
import com.mayhub.utils.download.DownloadListener;
import com.mayhub.utils.download.DownloadTask;
import com.mayhub.utils.download.FileDownloaderManager;
import com.mayhub.utils.download.SingleDownloadTask;

import java.io.File;

public class MainActivity extends BaseFragmentActivity {

    String url = "http://cdn.tiku.zhan.com/audio/ec8d7fb059bf11e5d0b453c034a27226/tpo15_speaking_1444113102859_45.m4a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    DownloadTask downloadTask = new SingleDownloadTask(url, new DownloadListener() {


                        @Override
                        public void onPrepare(Object tag, String realUrl, int index, int total) {

                        }

                        @Override
                        public void onStart(Object tag, String fileName, String realUrl, int fileLength, int index, int total) {

                        }

                        @Override
                        public void onProgress(Object tag, String realUrl, int progress, int index, int total) {

                        }

                        @Override
                        public void onCancel(Object tag, String realUrl, int progress, int index, int total) {

                        }

                        @Override
                        public void onFinish(Object tag, File file, String realUrl, int index, int total) {

                        }

                        @Override
                        public void onError(Object tag, int status, String error, File file, String realUrl, int index, int total) {

                        }
                    }, Environment.DIRECTORY_DOWNLOADS, i + ".mp3");
                    FileDownloaderManager.getInstance("mp3").addTaskAndStart(downloadTask);
                }
            }
        }).start();

        MediaPlayer mediaPlayer = new MediaPlayer();
        if(Build.VERSION.SDK_INT > 22) {
            PlaybackParams playbackParams = new PlaybackParams();
            mediaPlayer.setPlaybackParams(playbackParams);
        }
    }
}
