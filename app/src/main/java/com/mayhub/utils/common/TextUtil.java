package com.mayhub.utils.common;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by comkdai on 2017/5/15.
 */
public class TextUtil {

    private static final String TAG = "TextUtil";

    static final String regex = "[\\(（]\\d[\\)）]";

    public static String getReplacedStr(ArrayList<String> originalList, ArrayList<String> showList, String str){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()){
            String indexStr = matcher.group();
            int index = Integer.parseInt(indexStr.substring(1, 2));
            String option = originalList.get(index - 1);
            int showIndex = showList.indexOf(option);
            str = str.replace(indexStr, String.format("(%s)", showIndex + 1));
        }
        Log.e(TAG, "Str = " + str);
        return str;
    }

    public static void test(){
        ArrayList<String> list1 = new ArrayList<>();
        list1.add("ABC0");
        list1.add("ABC1");
        list1.add("ABC2");
        list1.add("ABC3");
        ArrayList<String> list2 = new ArrayList<>(list1);
        for (int i = 0; i < 10; i++) {
            Collections.shuffle(list2);
            Log.e(TAG, list2.toString());
            getReplacedStr(list1, list2, "this is a example of a sentence option (1) is wrong .（2） is right");
        }

    }

}
