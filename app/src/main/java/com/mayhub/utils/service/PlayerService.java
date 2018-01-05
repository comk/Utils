package com.mayhub.utils.service;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.mayhub.utils.R;
import com.mayhub.utils.common.MLogUtil;
import com.mayhub.utils.common.TextUtil;

/**
 * Created by comkdai on 2017/5/25.
 */
public class PlayerService extends Service {

    private static final int NOTIFICATION_ID = 412;
    private static final int REQUEST_CODE = 100;

    public static final String ACTION_PAUSE = "com.mayhub.jlpt.action.pause";
    public static final String ACTION_PLAY = "com.mayhub.jlpt.play";
    public static final String ACTION_PREV = "com.mayhub.jlpt.prev";
    public static final String ACTION_NEXT = "com.mayhub.jlpt.next";
    public static final String ACTION_STOP_SERVICE = "com.mayhub.jlpt.stop.service";

    private NotificationManagerCompat mNotificationManager;

    private PendingIntent mPauseIntent;
    private PendingIntent mPlayIntent;
    private PendingIntent mPreviousIntent;
    private PendingIntent mNextIntent;
    private int mNotificationColor;
    private boolean mStarted;
    private NotificationCompat.Builder mNotificationBuilder;

    public static void startPlayerService(Context context){
        Intent intent = new Intent(context, PlayerService.class);
        context.startService(intent);
    }

    public static void stopPlayerService(Context context){
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(ACTION_STOP_SERVICE);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = NotificationManagerCompat.from(this);

        String pkg = getPackageName();
        mPauseIntent = PendingIntent.getBroadcast(this, REQUEST_CODE,
                new Intent(ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPlayIntent = PendingIntent.getBroadcast(this, REQUEST_CODE,
                new Intent(ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPreviousIntent = PendingIntent.getBroadcast(this, REQUEST_CODE,
                new Intent(ACTION_PREV).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mNextIntent = PendingIntent.getBroadcast(this, REQUEST_CODE,
                new Intent(ACTION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        mNotificationManager.cancelAll();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            return super.onStartCommand(intent, flags, startId);
        }
        final String action = intent.getAction();
        if(TextUtils.isEmpty(action)){
            startNotification();
            return START_STICKY;
        }
        switch (action) {
            case ACTION_PAUSE:
                MLogUtil.e("Action","ACTION_PAUSE");
                break;
            case ACTION_PLAY:
                MLogUtil.e("Action","ACTION_PLAY");
                break;
            case ACTION_NEXT:
                MLogUtil.e("Action","ACTION_NEXT");
                break;
            case ACTION_PREV:
                MLogUtil.e("Action","ACTION_PREV");
                break;
            case ACTION_STOP_SERVICE:
                MLogUtil.e("Action","ACTION_STOP_SERVICE");
                stopNotification();
                stopSelf();
                break;
            default:
        }
        return START_STICKY;
    }


    private Notification buildJBNotification() {
        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationBuilder.setOngoing(true);
        mNotificationBuilder.setAutoCancel(false);
        mNotificationBuilder.setSmallIcon(R.drawable.ic_notification);

        //Open up the player screen when the user taps on the notification.
        Intent launchNowPlayingIntent = new Intent();
        launchNowPlayingIntent.setAction(ACTION_PLAY);
        PendingIntent launchNowPlayingPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, launchNowPlayingIntent, 0);
        mNotificationBuilder.setContentIntent(launchNowPlayingPendingIntent);

        //Grab the notification layouts.
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_custom_layout);
        RemoteViews expNotificationView = new RemoteViews(getPackageName(), R.layout.notification_custom_expanded_layout);

        //Initialize the notification layout buttons.
        Intent previousTrackIntent = new Intent();
        previousTrackIntent.setAction(ACTION_PREV);
        PendingIntent previousTrackPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, previousTrackIntent, 0);

        Intent playPauseTrackIntent = new Intent();
        playPauseTrackIntent.setAction(ACTION_PAUSE);
        PendingIntent playPauseTrackPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, playPauseTrackIntent, 0);

        Intent nextTrackIntent = new Intent();
        nextTrackIntent.setAction(ACTION_NEXT);
        PendingIntent nextTrackPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, nextTrackIntent, 0);

        Intent stopServiceIntent = new Intent();
        stopServiceIntent.setAction(ACTION_STOP_SERVICE);
        PendingIntent stopServicePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, stopServiceIntent, 0);

        //Check if audio is playing and set the appropriate play/pause button.
        if (true) {
            notificationView.setImageViewResource(R.id.notification_base_play, R.drawable.btn_playback_pause_light);
            expNotificationView.setImageViewResource(R.id.notification_expanded_base_play, R.drawable.btn_playback_pause_light);
        } else {
            notificationView.setImageViewResource(R.id.notification_base_play, R.drawable.btn_playback_play_light);
            expNotificationView.setImageViewResource(R.id.notification_expanded_base_play, R.drawable.btn_playback_play_light);
        }

        //Set the notification content.
        expNotificationView.setTextViewText(R.id.notification_expanded_base_line_one, "Line 1");
        expNotificationView.setTextViewText(R.id.notification_expanded_base_line_two, "Line 2");
        expNotificationView.setTextViewText(R.id.notification_expanded_base_line_three, "Line 3");

        notificationView.setTextViewText(R.id.notification_base_line_one, "Line Title");
        notificationView.setTextViewText(R.id.notification_base_line_two, "Line Text");

        //Set the states of the next/previous buttons and their pending intents.
        if (false) {
            //This is the only song in the queue, so disable the previous/next buttons.
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_next, View.INVISIBLE);
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_previous, View.INVISIBLE);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_play, playPauseTrackPendingIntent);

            notificationView.setViewVisibility(R.id.notification_base_next, View.INVISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_previous, View.INVISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);

        } else if (false) {
            //This is the the first song in the queue, so disable the previous button.
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_previous, View.INVISIBLE);
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_next, View.VISIBLE);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_play, playPauseTrackPendingIntent);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_next, nextTrackPendingIntent);

            notificationView.setViewVisibility(R.id.notification_base_previous, View.INVISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_next, View.VISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_next, nextTrackPendingIntent);

        } else if (false) {
            //This is the last song in the cursor, so disable the next button.
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_previous, View.VISIBLE);
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_next, View.INVISIBLE);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_play, playPauseTrackPendingIntent);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_next, nextTrackPendingIntent);

            notificationView.setViewVisibility(R.id.notification_base_previous, View.VISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_next, View.INVISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_next, nextTrackPendingIntent);

        } else {
            //We're smack dab in the middle of the queue, so keep the previous and next buttons enabled.
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_previous,View.VISIBLE);
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_next, View.VISIBLE);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_play, playPauseTrackPendingIntent);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_next, nextTrackPendingIntent);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_previous, previousTrackPendingIntent);

            notificationView.setViewVisibility(R.id.notification_base_previous,View.VISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_next, View.VISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_next, nextTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_previous, previousTrackPendingIntent);

        }

        //Set the "Stop Service" pending intents.
        expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_collapse, stopServicePendingIntent);
        notificationView.setOnClickPendingIntent(R.id.notification_base_collapse, stopServicePendingIntent);

        //Set the album art.
        expNotificationView.setImageViewBitmap(R.id.notification_expanded_base_image, BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_default_art));
        notificationView.setImageViewBitmap(R.id.notification_base_image, BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_default_art));

        //Attach the shrunken layout to the notification.
        mNotificationBuilder.setContent(notificationView);

        //Build the notification object.
        Notification notification = mNotificationBuilder.build();

        //Attach the expanded layout to the notification and set its flags.
        notification.bigContentView = expNotificationView;
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE |
                Notification.FLAG_NO_CLEAR |
                Notification.FLAG_ONGOING_EVENT;

        return notification;
    }

    private Notification createNotification() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        int playPauseButtonPosition = 0;

        // If skip to previous action is enabled
        if (true) {
            notificationBuilder.addAction(R.drawable.ic_skip_previous_white_24dp,
                    "Pre", mPreviousIntent);

            // If there is a "skip to previous" button, the play/pause button will
            // be the second one. We need to keep track of it, because the MediaStyle notification
            // requires to specify the index of the buttons (actions) that should be visible
            // when in compact view.
            playPauseButtonPosition = 1;
        }

        addPlayPauseAction(notificationBuilder);

        // If skip to next action is enabled
        if (true) {
            notificationBuilder.addAction(R.drawable.ic_skip_next_white_24dp,
                    "Next", mNextIntent);
        }

        notificationBuilder
                .setColor(mNotificationColor)
                .setSmallIcon(R.drawable.ic_notification)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setUsesChronometer(true)
//                .setContentIntent(createContentIntent("description"))
                .setContentTitle("content Title")
                .setContentText("Content Text")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_default_art))
                .setStyle(new android.support.v7.app.NotificationCompat.MediaStyle());  // show only play/pause in compact view

        setNotificationPlaybackState(notificationBuilder);
        return notificationBuilder.build();
    }

    private PendingIntent createContentIntent() {
        return null;
    }

    private void addPlayPauseAction(NotificationCompat.Builder builder) {
        String label;
        int icon;
        PendingIntent intent;
        if (true) {
            label = "Pause";
            icon = R.drawable.uamp_ic_pause_white_24dp;
            intent = mPauseIntent;
        } else {
            label = "Play";
            icon = R.drawable.uamp_ic_play_arrow_white_24dp;
            intent = mPlayIntent;
        }
        builder.addAction(new NotificationCompat.Action(icon, label, intent));
    }

    private void setNotificationPlaybackState(NotificationCompat.Builder builder) {
        if (!mStarted) {
            stopForeground(true);
            return;
        }
        if (true) {
            builder
                    .setWhen(System.currentTimeMillis() - 30000)
                    .setShowWhen(true)
                    .setUsesChronometer(true);
        } else {
            builder
                    .setWhen(0)
                    .setShowWhen(false)
                    .setUsesChronometer(false);
        }

        // Make sure that the notification can be dismissed by the user when we are not playing:
        builder.setOngoing(true);
    }

    /**
     * Posts the notification and starts tracking the session to keep it
     * updated. The notification will automatically be removed if the session is
     * destroyed before {@link #stopNotification} is called.
     */
    public void startNotification() {
        if (!mStarted) {

            // The notification must be updated after setting started to true
//            Notification notification = createNotification();
            Notification notification = buildJBNotification();
            if (notification != null) {
                startForeground(NOTIFICATION_ID, notification);
                mStarted = true;
            }
        }
    }

    /**
     * Removes the notification and stops tracking the session. If the session
     * was destroyed this has no effect.
     */
    public void stopNotification() {
        if (mStarted) {
            mStarted = false;
            try {
                mNotificationManager.cancel(NOTIFICATION_ID);
            } catch (IllegalArgumentException ex) {
                // ignore if the receiver is not registered.
            }
            stopForeground(true);
        }
    }

}
