package com.mayhub.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.mayhub.utils.common.MLogUtil;
import com.mayhub.utils.volley.IRequest;
import com.mayhub.utils.volley.RequestListener;
import com.mayhub.utils.volley.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/30.
 */
public class UserUtils {

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
