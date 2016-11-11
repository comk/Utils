package com.mayhub.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mayhub.utils.common.MLogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Java对象和JSON字符串相互转化工具类
 *
 * @author penghuaiyi
 * @date 2013-08-10
 */
public final class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    private JsonUtils() {
    }

    /**
     * 对象转换成json字符串
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * json字符串转成对象
     *
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(str, type);
    }

    /**
     * json字符串转成对象
     *
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(str, type);
    }

    /**
     * 将JSONObjec对象转换成Map-List集合
     *
     * @param json
     * @return
     */
    public static Map<String, Object> toMap(JsonObject json) {
        Map<String, Object> map = new HashMap<String, Object>();
        Set<Entry<String, JsonElement>> entrySet = json.entrySet();
        for (Iterator<Entry<String, JsonElement>> iter = entrySet.iterator(); iter
                .hasNext(); ) {
            Entry<String, JsonElement> entry = iter.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof JsonArray)
                map.put((String) key, toList((JsonArray) value));
            else if (value instanceof JsonObject)
                map.put((String) key, toMap((JsonObject) value));
            else
                map.put((String) key, value);
        }
        return map;
    }

    /**
     * 将JSONArray对象转换成List集合
     *
     * @param json
     * @return
     */
    public static List<Object> toList(JsonArray json) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < json.size(); i++) {
            Object value = json.get(i);
            if (value instanceof JsonArray) {
                list.add(toList((JsonArray) value));
            } else if (value instanceof JsonObject) {
                list.add(toMap((JsonObject) value));
            } else {
                list.add(value);
            }
        }
        return list;
    }

    public static String getListFirstItem(String json){
        if(json != null && json.contains("[\"")) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                if (jsonArray != null && jsonArray.length() > 0) {
                    return jsonArray.getString(0);
                }
            } catch (JSONException e) {
            }
        }
        return json;
    }

    public static List<String> toList(String json) {
        List<String> list = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            String item = null;
            if (jsonArray != null && jsonArray.length() > 0){
                for (int i = 0; i < jsonArray.length(); i++) {  //遍历数据
                    item = jsonArray.getJSONObject(i).toString(); //从JSONArray里面获取一个JSONObject对象
                    list.add(item);
                }
            }
        }catch (Exception ex){
            MLogUtil.e("", ex.getMessage());
        }
        return list;
    }

    public static List<String> toStringList(String json){
        List<String> list = new ArrayList<String>();
        if(json != null && json.startsWith("[")) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                String item = null;
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {  //遍历数据
                        item = jsonArray.getString(i); //从JSONArray里面获取一个JSONObject对象
                        list.add(item);
                    }
                }
            } catch (Exception ex) {
                list.add(json);
                MLogUtil.e(TAG, ex.getMessage() + json);
            }
        }else{
            list.add(json);
        }
        return list;
    }

    public static List<String> toExampleLst(String examples){
        List<String> exampleLst = new ArrayList<String>();
        try {
            JSONArray exapArr = new JSONArray(examples);
            JSONObject object = null;
            if (exapArr != null && exapArr.length() > 0){
                for (int i=0; i<exapArr.length(); i++){
                    object = exapArr.getJSONObject(i);
                    exampleLst.add((String) object.get("en"));
                    exampleLst.add((String) object.get("cn"));
                }
            }
        }catch (Exception ex){
            MLogUtil.e(TAG, ex.getMessage());
        }
        return exampleLst;
    }

    /**
     * 把json 转换为ArrayList 形式
     *
     * @return
     */

    public static List<Map<String, Object>> getList(JSONArray jsonArray)

    {
        List<Map<String, Object>> list = null;
        try {
            JSONObject jsonObject;
            list = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                list.add(getMap(jsonObject.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 将json 数组转换为Map 对象
     *
     * @param jsonString
     * @return
     */

    public static Map<String, Object> getMap(String jsonString)

    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            @SuppressWarnings("unchecked")
            Iterator<String> keyIter = jsonObject.keys();
            String key;
            Object value;
            Map<String, Object> valueMap = new HashMap<String, Object>();
            while (keyIter.hasNext()) {
                key = (String) keyIter.next();
                value = jsonObject.get(key);
                valueMap.put(key, value);
            }
            return valueMap;
        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }
    }
}
