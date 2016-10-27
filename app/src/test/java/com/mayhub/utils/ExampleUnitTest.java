package com.mayhub.utils;

import com.mayhub.utils.common.TimeUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    private class A{
        public String name;
        public A(String n){
            name = n;
        }
    }

    @Test
    public void testTimeParse(){
        ConcurrentHashMap<String, String> testMap = new ConcurrentHashMap<>();

        Random random = new Random();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            testMap.put(String.valueOf(i), String.valueOf(i));
        }
        System.out.println("insert cost time : " + (System.currentTimeMillis() - startTime));


        for (int i = 0; i < 10; i++) {
            int num = random.nextInt() % 10;
            System.out.println("num = " + num);
            startTime = System.currentTimeMillis();
            String value = testMap.get(String.valueOf(num));
            if(value != null){
                System.out.println("find " + value);
            }
            System.out.println("query cost time : " + (System.currentTimeMillis() - startTime));
        }

        String reg = "90...00";
        reg = reg.replaceAll("[^0-9]+",".");
        System.out.println("reg = " + reg);
        System.out.println(TimeUtils.getInstance().formatTimeMillsToDateStr(System.currentTimeMillis()));
//        System.out.println(TimeUtils.getInstance().formatTimeMillsToDateTimeStr(System.currentTimeMillis()));
//        System.out.println(TimeUtils.getInstance().formatDateTimeStringToTimeMills("1990/02/01"));
//        System.out.println(TimeUtils.getInstance().formatDateTimeStringToTimeMills("1990-02-01 12:54:20"));
//        System.out.println(TimeUtils.getInstance().isDateStrBeforeNow("1990-02-01 12:54:20"));
//        System.out.println(TimeUtils.getInstance().isDateStrBeforeNow("2020-02-01 12:54:20"));
//        System.out.println(TimeUtils.getInstance().getTimeMillsBetweenDate("1990-02-01 12:54:20","1990-02-01 12:54:21"));
//        System.out.println(TimeUtils.getInstance().isSameDay("1990-02-04 00:00:01","1990-02-01"));
//        System.out.println(TimeUtils.getInstance().isSameDay("1990-02-01","1990-02-01"));
//        System.out.println(TimeUtils.getInstance().getDateDuration("1990-01-04 00:00:01","1990-02-01"));
        System.out.println(TimeUtils.getInstance().getDateAccurateDuration("1990-01-04","2015-02-01"));
        System.out.println("cost time : " + (System.currentTimeMillis() - startTime));
    }


}