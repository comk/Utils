package com.mayhub.utils;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.mayhub.utils.activity.App;
import com.mayhub.utils.common.MLogUtil;
import com.mayhub.utils.volley.IRequest;
import com.mayhub.utils.volley.RequestListener;
import com.mayhub.utils.volley.RequestParams;

import org.json.JSONObject;

import java.security.Permission;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/30.
 */
public class UserUtils {

    public static void registerPhoneReceiver(){
        PhoneReceiver phoneReceiver = new PhoneReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        App.getInstance().registerReceiver(phoneReceiver, intentFilter);
    }

    public static void unregisterPhoneReceiver(BroadcastReceiver receiver){
        App.getInstance().unregisterReceiver(receiver);
    }

    public static class PhoneReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
                //去电 暂停播放

            }else{
                //来电
                TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                manager.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }

        PhoneStateListener stateListener = new PhoneStateListener(){

            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch(state){
                    case TelephonyManager.CALL_STATE_IDLE:
                        //挂断 继续播放

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK://接听
                    case TelephonyManager.CALL_STATE_RINGING://响铃
                        //暂停播放

                        break;
                    }
                }
            };
    }

    private static UserUtils instance;

    private UserUtils(){

    }

    public static UserUtils getInstance(){
        if(instance == null) {
            synchronized (UserUtils.class) {
                if (instance == null) {
                    instance = new UserUtils();
                }
            }
        }
        return instance;
    }

    private static class ReqListenerProxy implements RequestListener {

        private RequestListener reqListener;

        private String timeStamp;

        ReqListenerProxy(RequestListener listener1, String timeStamp1){
            reqListener = listener1;
            timeStamp = timeStamp1;
        }

        @Override
        public void requestSuccess(String json) {
            JSONObject jsonObject;
            String status;
            try {
                jsonObject = new JSONObject(json);
                status = jsonObject.getString("status");
                if("OK".equals(status)){
                    String encryptStr = jsonObject.optString("message");
                    if(!TextUtils.isEmpty(encryptStr) && !"null".equals(encryptStr)){
                        String decryptStr = EncryptUtile.decrypt(encryptStr, timeStamp);
                        jsonObject.put("message", new JSONObject(decryptStr));
                        if(reqListener != null){
                            reqListener.requestSuccess(jsonObject.toString());
                        }
                    }else if(reqListener != null){
                        reqListener.requestSuccess(json);
                    }
                }else if(reqListener != null){
                    reqListener.requestSuccess(json);
                }
            } catch (Exception e){
                if(reqListener != null){
                    reqListener.requestSuccess(json);
                }
                MLogUtil.p(e);
            }
        }

        @Override
        public void requestError(VolleyError e) {
            if(reqListener != null){
                reqListener.requestError(e);
            }
        }
    }


    @Nullable
    private String getEncryptString(RequestParams params, String timeStamp) {
        HashMap<String, String> info = new HashMap<>();
        info.put("timestamp",timeStamp);
        for (Map.Entry<String, String> entry:params.getUrlParams().entrySet()){
            info.put(entry.getKey(), entry.getValue());
        }
        String infoStr = JsonUtils.toJson(info);
        String encryptStr = null;
        try {
            encryptStr = EncryptUtile.encrypt(infoStr, timeStamp);
        } catch (Exception e) {
            MLogUtil.p(e);
        }
        return encryptStr;
    }

    public synchronized void registerUser(RequestParams params, RequestListener requestListener) {
        doEncrypt(params, "http://172.18.1.188/ielts/user/register", requestListener);
    }


    private void doEncrypt(RequestParams params, String requestUrl, RequestListener requestListener){
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        String encryptStr = getEncryptString(params, timeStamp);
        if(TextUtils.isEmpty(encryptStr)){
            return;
        }
        RequestParams examParams = new RequestParams();
        examParams.put("info", encryptStr);
        examParams.put("timestamp", timeStamp);
        IRequest.post(MyApplication.getInstance(), requestUrl,
                examParams, new ReqListenerProxy(requestListener, timeStamp));
    }

}
