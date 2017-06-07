package com.mayhub.utils.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mayhub.utils.common.MLogUtil;

/**
 * Created by comkdai on 2017/5/25.
 */
public class PlayerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case PlayerService.ACTION_PAUSE:
                MLogUtil.e("Action","ACTION_PAUSE");
                break;
            case PlayerService.ACTION_PLAY:
                MLogUtil.e("Action","ACTION_PLAY");
                break;
            case PlayerService.ACTION_NEXT:
                MLogUtil.e("Action","ACTION_NEXT");
                break;
            case PlayerService.ACTION_PREV:
                MLogUtil.e("Action","ACTION_PREV");
                break;
            default:
        }
    }
}
