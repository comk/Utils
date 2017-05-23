package com.mayhub.utils;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.mayhub.utils.common.LocalValueUtils;
import com.mayhub.utils.feature.BehaviorDemo;
import com.mayhub.utils.feature.ScrollingActivity;
import com.mayhub.utils.speed.TrackUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Main2ActivityProcess1 extends AppCompatActivity implements View.OnClickListener{

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String TAG = Main2ActivityProcess1.class.getSimpleName();
    private EventLogger eventLogger = new EventLogger();
    final MediaPlayer mediaPlayer = new MediaPlayer();

//    private SimpleExoPlayer player;
    private Handler mainHandler = new Handler();
    private String userAgent;
    private DefaultDataSourceFactory mediaDataSourceFactory;
    private MappingTrackSelector trackSelector;
    private AdaptiveVideoTrackSelection.Factory videoTrackSelectionFactory;
    private EditText etSpeed;
    private void initExoPlayer(){
        userAgent = Util.getUserAgent(getApplicationContext(), "ExoPlayerDemo");
        mediaDataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), userAgent, BANDWIDTH_METER);
        videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
//        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector, new DefaultLoadControl(),
//                null, SimpleExoPlayer.EXTENSION_RENDERER_MODE_ON);
//
//        player.setPlayWhenReady(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        player.release();
        mediaPlayer.release();
    }

    private void copyAudioFile(){
        try {
            InputStream is = getAssets().open("tpo1_listening_direction1.mp3");
            copyFileToSDCard(is);
            is.close();
        }catch (Exception e){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.activity_main2_activity_process1);
        copyAudioFile();
//        initExoPlayer();
        findViewById(R.id.tv_play).setOnClickListener(this);
        findViewById(R.id.tv_pause).setOnClickListener(this);
        findViewById(R.id.tv_stop).setOnClickListener(this);
        findViewById(R.id.tv_destroy).setOnClickListener(this);
        findViewById(R.id.set_speed).setOnClickListener(this);
        findViewById(R.id.tv_volume_add).setOnClickListener(this);
        findViewById(R.id.tv_volume_sub).setOnClickListener(this);
        LocalValueUtils.initInstance(getApplicationContext());
        TrackUtils.getInstance().setPlayPath("assets://tpo1_listening_direction1.mp3");
//        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LocalValueUtils.initInstance(getApplicationContext());
//                TextView tv = (TextView) v;
//                tv.setText(LocalValueUtils.getInstance().getString("String"));
//                LocalValueUtils.getInstance().save("String", "Main2ActivityProcess1");
//                LocalValueUtils.destroyInstance();
//                startActivity(new Intent(getApplicationContext(), BehaviorDemo.class));
//
//                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        if(Build.VERSION.SDK_INT > 22) {
//                            PlaybackParams playbackParams = new PlaybackParams();
//                            playbackParams.setSpeed(3.0f);
//                            mediaPlayer.setPlaybackParams(playbackParams);
//                        }
//                        mediaPlayer.start();
//                    }
//                });
//                try {
//                    AssetFileDescriptor afd = getAssets().openFd("tpo1_listening_direction1.mp3");
//                    InputStream is = getAssets().open("tpo1_listening_direction1.mp3");
//                    copyFileToSDCard(is);
//                    is.close();
//                    FileInputStream fis = new FileInputStream(new File(getCacheDir(), "music.mp3"));
//                    mediaPlayer.setDataSource(fis.getFD(), 0, fis.available());
//                    fis.close();
//                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//                    mediaPlayer.prepareAsync();


//                /
//                player.prepare(buildMediaSource(Uri.fromFile(new File(getCacheDir(), "music.mp3")), null));



//            }
//        });
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = Util.inferContentType(!TextUtils.isEmpty(overrideExtension) ? "." + overrideExtension
                : uri.getLastPathSegment());
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, new DefaultDataSourceFactory(getApplicationContext(), userAgent),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, new DefaultDataSourceFactory(getApplicationContext(), userAgent),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),
                        mainHandler, eventLogger);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private void copyFileToSDCard(InputStream is) throws Exception{
        File f = new File(getCacheDir(), "music.mp3");
        if(!f.exists()) {
            FileOutputStream fos = new FileOutputStream(f);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = is.read(bytes, 0, bytes.length)) != -1) {
                fos.write(bytes, 0, len);
                fos.flush();
            }
            fos.close();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_play:
                TrackUtils.getInstance().start();
                break;
            case R.id.tv_destroy:
                TrackUtils.getInstance().destroy();
                break;
            case R.id.tv_pause:
                TrackUtils.getInstance().pause();
                break;
            case R.id.tv_stop:
                TrackUtils.getInstance().stop();
                break;
            case R.id.tv_volume_add:
                TrackUtils.getInstance().setSpeed(TrackUtils.getInstance().getSpeed() + 0.1f);
                break;
            case R.id.tv_volume_sub:
                TrackUtils.getInstance().setSpeed(TrackUtils.getInstance().getSpeed() - 0.1f);
                break;
            case R.id.set_speed:
                TrackUtils.getInstance().setSpeed(1.5f);
                break;
        }
    }
}
