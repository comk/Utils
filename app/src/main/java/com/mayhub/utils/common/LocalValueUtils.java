package com.mayhub.utils.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Set;

/**
 * Created by comkdai on 2016/12/2.
 */
public class LocalValueUtils {

    private static final String TAG = LocalValueUtils.class.getSimpleName();

    private static LocalValueUtils instance;

    private SharedPreferences sharedPreferences;

    private LocalValueUtils(Context context){
        sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    public static void initInstance(Context context){
        if(instance == null) {
            synchronized (LocalValueUtils.class) {
                if(instance == null){
                    instance = new LocalValueUtils(context);
                }
            }
        }else{
            instance.sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        }
    }

    public static void destroyInstance(){
        if(instance != null) {
            instance.sharedPreferences = null;
            instance = null;
        }
    }

    public static LocalValueUtils getInstance(){
        if(instance == null) {
            throw new NullPointerException("LocalValueUtils is not initialized or has been destroyed ...");
        }
        return instance;
    }

    public void save(String tag, Object value){
        if(TextUtils.isEmpty(tag) || value == null){
            return;
        }
        if(value instanceof String){
            saveString(tag, (String) value);
        }else if(value instanceof Long){
            saveLong(tag, (Long) value);
        }else if(value instanceof Integer){
            saveInt(tag, (Integer) value);
        }else if(value instanceof Boolean){
            saveBoolean(tag, (Boolean) value);
        }else if(value instanceof Float){
            saveFloat(tag, (Float) value);
        }else if(value instanceof Set){
            saveStringSet(tag, (Set<String>) value);
        }
    }

    public void saveString(String tag, String value){
        sharedPreferences.edit().putString(tag, value).apply();
    }

    public void saveLong(String tag, long value){
        sharedPreferences.edit().putLong(tag, value).apply();
    }

    public void saveInt(String tag, int value){
        sharedPreferences.edit().putInt(tag, value).apply();
    }

    public void saveBoolean(String tag, boolean value){
        sharedPreferences.edit().putBoolean(tag, value).apply();
    }

    public void saveFloat(String tag, float value){
        sharedPreferences.edit().putFloat(tag, value).apply();
    }

    public void saveStringSet(String tag, Set<String> value){
        sharedPreferences.edit().putStringSet(tag, value).apply();
    }

    public String getString(String tag){
        return sharedPreferences.getString(tag, null);
    }

    public long getLong(String tag, long value){
        return sharedPreferences.getLong(tag, value);
    }

    public Integer getInt(String tag, int value){
        return sharedPreferences.getInt(tag, value);
    }

    public boolean getBoolean(String tag, boolean value){
        return sharedPreferences.getBoolean(tag, value);
    }

    public float getFloat(String tag, float value){
        return sharedPreferences.getFloat(tag, value);
    }

    public Set<String> getStringSet(String tag, Set<String> value){
        return sharedPreferences.getStringSet(tag, null);
    }

}
