package com.mayhub.utils.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络操作工具类
 *
 * @author AigeStudio 2015-05-08
 */
public class NetUtil {

    public static final class NetType {
        public static final int INVALID = 0;
        public static final int WAP = 1;
        public static final int G2 = 2;
        public static final int G3 = 3;
        public static final int WIFI = 4;
        public static final int NO_WIFI = 5;
    }

    /**
     * 获取网络类型
     *
     * @param context ...
     * @return 网络类型ID {@link NetType}
     */
    public static int getNetWorkType(Context context) {
        int type = NetType.INVALID;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String typeName = networkInfo.getTypeName();
            if (typeName.equalsIgnoreCase("WIFI")) {
                type = NetType.WIFI;
            } else if (typeName.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                type = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ?
                        NetType.G3 : NetType.G2) :
                        NetType.WAP;
            }
        }
        return type;
    }

    /**
     * 判断是否是3G+的移动网络
     *
     * @param context ...
     * @return ...
     */
    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }
}
