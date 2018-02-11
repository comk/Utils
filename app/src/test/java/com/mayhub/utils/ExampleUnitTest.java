package com.mayhub.utils;


import com.mayhub.utils.common.TimeUtils;
import com.mayhub.utils.encrypt.AESEncrypt;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        Integer i = 1000, y = 1000;
//        System.out.println( i == y );
        Integer i = 100, y = 100;
        System.out.println( i == y );
    }

    @Test
    public void rxTestCode(){
        ArrayList<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");


}

    @Test
    public void testEncrypt() throws Exception{
        String ivStr = "1234567812345678";
        String keyStr = "1234567812345678";
        String content = "Test String";
        String encryptedStr = AESEncrypt.encrypt(content, keyStr, ivStr);
        System.out.println("encrypted : " + encryptedStr);
        String decryptedStr = AESEncrypt.decrypt(encryptedStr, keyStr, ivStr);
        System.out.println("decrypted : " + decryptedStr);
    }

    @Test
    public void stringTest(){
        String currentVer = "2.0-debug-release";
        String ver1 = "2.0.0-debug-release";
        String ver2 = "2.0.1-debug-release";

        if(currentVer.compareTo(ver1) >= 0){
            System.out.println("currentVer >= ver1");
        }
        if(currentVer.compareTo(ver2) <= 0){
            System.out.println("currentVer <= ver2");
        }
    }


    @Test
    public void timeTest() throws Exception{
//        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println(sdf.format(date));
//        System.out.println(sdf.getTimeZone().getID());
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT-8:00"));
//        System.out.println(sdf.format(date));
//        System.out.println(sdf.getTimeZone().getID());
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
//        System.out.println(sdf.format(date));
//        System.out.println(sdf.getTimeZone().getID());
//
//        System.out.println(Calendar.getInstance().getTimeZone().getRawOffset());
//        System.out.println((1000 * 60 * 60));
//        System.out.println(Calendar.getInstance().getTimeZone().getRawOffset() / (1000 * 60 * 60));
//        String s = "======";
//        concatStr(s);
//        System.out.println(s);

        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        Date d = sdf1.parse("Sun, 11 Feb 2018 08:17:26 GMT");
        System.out.println(d.getTime());
        System.out.println(d.toString());
        System.out.println(sdf.format(d));
    }

    public long getTimeOffset(String serverTime) throws Exception{
        long inTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.UK);
        Date d = sdf.parse(serverTime);
        return d.getTime() + inTime + Calendar.getInstance().getTimeZone().getRawOffset();
    }

    private void concatStr(String str){
        str += "--";
    }

    @Test
    public void hashCodeTest(){
        ArrayList<Integer> codes = new ArrayList<>(100000);
        for (int i = 0; i < 100000; i++) {
            String s = String.valueOf(System.currentTimeMillis() / (Math.random() * 20)) ;
            if(codes.contains(s.hashCode())){
                System.out.println("hash code exist ..." + s);
            }else{
                codes.add(s.hashCode());
            }
        }
        System.out.println("code size = " + codes.size());
    }

    @Test
    public void testNotify() throws InterruptedException {
        final String obj = "a";
//        for (int i = 0; i < 10; i++) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (obj){
                System.out.println("testNotify wait running ... ");
                    System.out.println("testNotify being running ... start");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("testNotify being running ... end");
                    obj.notifyAll();
                }

            }
        }).start();
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + " testNotify2 waiting ... ");
                    synchronized (obj){
                        System.out.println(Thread.currentThread().getName() + " testNotify2 starting ... ");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
    //                    obj.notifyAll();
                        System.out.println(Thread.currentThread().getName() + "testNotify2 end running ... ");
                    }
                    System.out.println(Thread.currentThread().getName() + "testNotify2 ending ... ");

                }
            }).start();
        }
        Thread.sleep(17000);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("testNotify notify... wait");
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    obj.notify();
//                    System.out.println("testNotify notify... end");
//                }
//            }).start();
//        }
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