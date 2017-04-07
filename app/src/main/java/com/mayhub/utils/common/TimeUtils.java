package com.mayhub.utils.common;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/6/15.
 */
public class TimeUtils {

    private static volatile TimeUtils instance;

    public static final long TIME_MILLS_SECOND = 1000;
    public static final long TIME_MILLS_MINUTES = 60 * TIME_MILLS_SECOND;
    public static final long TIME_MILLS_HOUR = 60 * TIME_MILLS_MINUTES;
    public static final long TIME_MILLS_DAY = 24 * TIME_MILLS_HOUR;
    public static final long TIME_MILLS_MONTH = 30 * TIME_MILLS_DAY;
    public static final long TIME_MILLS_YEAR = 12 * TIME_MILLS_MONTH;
    private static final String FORMAT_TIME = "%s:%s:%s";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.US);

    private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private DecimalFormat decimalFormat = new DecimalFormat("00");

    private Calendar calendar = Calendar.getInstance(Locale.US);

    private Calendar calendar2 = Calendar.getInstance(Locale.US);

    private TimeUtils(){

    }

    public static TimeUtils getInstance(){
        if(instance == null){
            synchronized (TimeUtils.class){
                if(instance == null){
                    instance = new TimeUtils();
                }
            }
        }
        return instance;
    }

    private String correctDateStr(String dateStr){
        return dateStr.replaceAll("[^0-9|:| ]","-");
    }

    /**
     * this method is trying to make dateStr like '1990-08-06 15:30:25' to time mills
     * @note dateStr like '1990-08-06' will success but the dateStr will be appended time to '1990-08-06 00:00:00'
     * @param dateStr the dateStr you want to format
     * @return time mills
     */
    public long formatDateTimeStringToTimeMills(String dateStr){
        return formatDateToTimeMills(parseDateStrToDate(dateStr));
    }

    /**
     * this method is trying to format Date to time mills
     * @param date you want to format Date Object
     * @return time mills
     */
    public long formatDateToTimeMills(Date date){
        if(date == null){
            return 0;
        }else{
            calendar.setTime(date);
            return calendar.getTimeInMillis();
        }
    }

    /**
     * parse Date Object from dateStr and dateStr should like '1990-08-06 15:30:25'
     * @param dateStr
     * @return
     */
    public Date parseDateStrToDate(String dateStr){
        Date date = null;
        try {
            date = simpleDateTimeFormat.parse(dateStr);
        }catch (ParseException e) {
            try {
                date = simpleDateFormat.parse(correctDateStr(dateStr));
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        return date;
    }

    /**
     * format time mills to Date like '1990-08-06'
     * @param timeMills
     * @return
     */
    public String formatTimeMillsToDateStr(long timeMills){
        return simpleDateFormat.format(new Date(timeMills));
    }

    /**
     * format time mills to DateTime like '1990-08-06 15:30:25'
     * @param timeMills
     * @return
     */
    public String formatTimeMillsToDateTimeStr(long timeMills){
        return simpleDateTimeFormat.format(new Date(timeMills));
    }

    /**
     * calculate the time mills between two date with dateStr
     * and the oldDateStr is the smaller one ,and newDateStr2 is the bigger one
     * @param oldDateStr the dateStr you think is smaller
     * @param newDateStr2 the dateStr you think is bigger
     * @return
     */
    public long getTimeMillsBetweenDate(String oldDateStr, String newDateStr2){
        return getTimeMillsBetweenDate(parseDateStrToDate(oldDateStr),parseDateStrToDate(newDateStr2));
    }

    /**
     * calculate the time mills between two date
     * and the oldDate is the smaller one ,and newDate is the bigger one
     * @param oldDate the date you think is smaller
     * @param newDate the date you think is bigger
     * @return
     */
    public long getTimeMillsBetweenDate(Date oldDate, Date newDate){
        calendar.setTime(oldDate);
        calendar2.setTime(newDate);
        return calendar2.getTimeInMillis() - calendar.getTimeInMillis();
    }

    /**
     * this check the dateStr is smaller than now or is older than now , or if it is past
     * @param dateStr
     * @return
     */
    public boolean isDateStrBeforeNow(String dateStr){
        return isDateBeforeNow(parseDateStrToDate(dateStr));
    }

    /**
     * this check the date is smaller than now or is older than now , or if it is past
     * @param date
     * @return
     */
    public boolean isDateBeforeNow(Date date){
        calendar.setTime(date);
        return calendar.getTimeInMillis() < System.currentTimeMillis();
    }

    /**
     * this two date is the same day
     * @param dateStr
     * @param dateStr2
     * @return
     */
    public boolean isSameDay(String dateStr, String dateStr2){
        return isSameDay(parseDateStrToDate(dateStr),parseDateStrToDate(dateStr2));
    }

    /**
     * this two date is the same day
     * @param date
     * @param date2
     * @return
     */
    public boolean isSameDay(Date date, Date date2){
        calendar.setTime(date);
        calendar2.setTime(date2);
        return calendar2.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) &&
                calendar.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR);
    }

    /**
     * calculate the datetime between two date
     * and the anchorDate should be smaller than varyDate
     * @param anchorDate
     * @param varyDate
     * @return
     */
    public String getDateDuration(Date anchorDate, Date varyDate){
        if(anchorDate.getTime() > varyDate.getTime()){
            throw new IllegalArgumentException("the anchorDate should be smaller than varyDate");
        }

//        calendar.setTime(anchorDate);
//        calendar2.setTime(varyDate);
//
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int min = calendar.get(Calendar.MINUTE);
//        int sec = calendar.get(Calendar.SECOND);
//        int mills = calendar.get(Calendar.MILLISECOND);
//
//        int year2 = calendar2.get(Calendar.YEAR);
//        int month2 = calendar2.get(Calendar.MONTH) + 1;
//        int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
//        int hour2 = calendar2.get(Calendar.HOUR_OF_DAY);
//        int min2 = calendar2.get(Calendar.MINUTE);
//        int sec2 = calendar2.get(Calendar.SECOND);
//        int mills2 = calendar2.get(Calendar.MILLISECOND);


        long duration = varyDate.getTime() - anchorDate.getTime();
        if(duration > TIME_MILLS_MONTH){
            return formatTimeMillsToDateTimeStr(anchorDate.getTime());
        }else if(duration > TIME_MILLS_DAY){
            return String.format("%s天前",duration / TIME_MILLS_DAY);
        }else if(duration > TIME_MILLS_HOUR){
            return String.format("%s小时前",duration / TIME_MILLS_HOUR);
        }else if(duration > TIME_MILLS_MINUTES){
            return String.format("%s分钟前",duration / TIME_MILLS_MINUTES);
        }else{
            return String.format("%s秒前",duration / TIME_MILLS_SECOND);
        }
    }

    /**
     * calculate the datetime between two date
     * and the anchorDate should be smaller than varyDate
     * @param anchorDate
     * @param varyDate
     * @return
     */
    public String getDateDuration(String anchorDate, String varyDate){
        return getDateDuration(parseDateStrToDate(anchorDate),parseDateStrToDate(varyDate));
    }



    /**
     * calculate the datetime between two date
     * and the anchorDate should be smaller than varyDate
     * @param anchorDate
     * @param varyDate
     * @return
     */
    public String getDateAccurateDuration(Date anchorDate, Date varyDate){
        if(anchorDate.getTime() > varyDate.getTime()){
            throw new IllegalArgumentException("the anchorDate should be smaller than varyDate");
        }

        long duration = varyDate.getTime() - anchorDate.getTime();
        if(duration < TIME_MILLS_MINUTES){
            return String.format("%s秒前",duration / TIME_MILLS_SECOND);
        }else if(duration < TIME_MILLS_HOUR){
            return String.format("%s分钟%s",duration / TIME_MILLS_MINUTES,
                    String.format("%s秒前",(duration - (TIME_MILLS_MINUTES * (duration / TIME_MILLS_MINUTES))) / TIME_MILLS_SECOND));
        }else if(duration < TIME_MILLS_DAY){
            return String.format("%s小时%s",duration / TIME_MILLS_HOUR,
                    String.format("%s分钟%s",(duration - (TIME_MILLS_HOUR * (duration / TIME_MILLS_HOUR))) / TIME_MILLS_MINUTES,
                            String.format("%s秒前",(duration - (TIME_MILLS_MINUTES * (duration / TIME_MILLS_MINUTES))) / TIME_MILLS_SECOND)));
        }else if(duration < TIME_MILLS_MONTH){
            return String.format("%s天%s",duration / TIME_MILLS_DAY,
                    String.format("%s小时%s",(duration - (TIME_MILLS_DAY * (duration / TIME_MILLS_DAY))) / TIME_MILLS_HOUR,
                            String.format("%s分钟%s",(duration - (TIME_MILLS_HOUR * (duration / TIME_MILLS_HOUR))) / TIME_MILLS_MINUTES,
                                    String.format("%s秒前",(duration - (TIME_MILLS_MINUTES * (duration / TIME_MILLS_MINUTES))) / TIME_MILLS_SECOND))));
        }else if(duration < TIME_MILLS_YEAR){
            return String.format("%s月%s",duration / TIME_MILLS_MONTH,
                    String.format("%s天%s",(duration - (TIME_MILLS_MONTH * (duration / TIME_MILLS_MONTH))) / TIME_MILLS_DAY,
                            String.format("%s小时%s",(duration - (TIME_MILLS_DAY * (duration / TIME_MILLS_DAY))) / TIME_MILLS_HOUR,
                                    String.format("%s分钟%s",(duration - (TIME_MILLS_HOUR * (duration / TIME_MILLS_HOUR))) / TIME_MILLS_MINUTES,
                                            String.format("%s秒前",(duration - (TIME_MILLS_MINUTES * (duration / TIME_MILLS_MINUTES))) / TIME_MILLS_SECOND)))));
        }else{
            return String.format("%s年%s", duration / TIME_MILLS_YEAR,
                    String.format("%s月%s",(duration - (TIME_MILLS_YEAR * (duration / TIME_MILLS_YEAR))) / TIME_MILLS_MONTH,
                            String.format("%s天%s",(duration - (TIME_MILLS_MONTH * (duration / TIME_MILLS_MONTH))) / TIME_MILLS_DAY,
                                    String.format("%s小时%s",(duration - (TIME_MILLS_DAY * (duration / TIME_MILLS_DAY))) / TIME_MILLS_HOUR,
                                            String.format("%s分钟%s",(duration - (TIME_MILLS_HOUR * (duration / TIME_MILLS_HOUR))) / TIME_MILLS_MINUTES,
                                                    String.format("%s秒前",(duration - (TIME_MILLS_MINUTES * (duration / TIME_MILLS_MINUTES))) / TIME_MILLS_SECOND))))));
        }
    }

    /**
     * calculate the datetime between two date
     * and the anchorDate should be smaller than varyDate
     * @param anchorDate
     * @param varyDate
     * @return
     */
    public String getDateAccurateDuration(String anchorDate, String varyDate){
        return getDateAccurateDuration(parseDateStrToDate(anchorDate),parseDateStrToDate(varyDate));
    }

    private String formatDateAndTimeArgs(int args){
        if(args > 9){
            return String.valueOf(args);
        }else{
            return String.format("0%s",args);
        }
    }

    public String getPlayTime(long currTime){
        int seconds = (int) (currTime/1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        return String.format(FORMAT_TIME, decimalFormat.format(hours), decimalFormat.format(minutes), decimalFormat.format(seconds));
    }

    public String formatTimeDataInMills(long timeMills){
        if(timeMills > 0){
            Calendar pubTime = Calendar.getInstance();
            pubTime.setTimeInMillis(timeMills);

            Calendar nowTime = Calendar.getInstance();

            int pubYear = pubTime.get(Calendar.YEAR);
            int pubMonth = pubTime.get(Calendar.MONTH) + 1;
            int pubDay = pubTime.get(Calendar.DAY_OF_MONTH);
            if(nowTime.after(pubTime)){
                int nowYear = nowTime.get(Calendar.YEAR);
                if(nowYear == pubYear){
                    long duration = nowTime.getTimeInMillis() - pubTime.getTimeInMillis();
                    if(duration > TIME_MILLS_MONTH){
                        return String.format("%s-%s-%s",pubYear,formatDateAndTimeArgs(pubMonth),formatDateAndTimeArgs(pubDay));
                    }else if(duration > TIME_MILLS_DAY){
                        return (duration / TIME_MILLS_DAY) + "天前";
                    }else if(duration > TIME_MILLS_HOUR){
                        return (duration / TIME_MILLS_HOUR) + "小时前";
                    }else if(duration > TIME_MILLS_MINUTES){
                        return (duration / TIME_MILLS_MINUTES) + "分钟前";
                    }else{
                        return "刚刚";
                    }
                }else{
                    return String.format("%s-%s-%s",pubYear,formatDateAndTimeArgs(pubMonth),formatDateAndTimeArgs(pubDay));
                }
            }else{
                return String.format("%s-%s-%s",pubYear,formatDateAndTimeArgs(pubMonth),formatDateAndTimeArgs(pubDay));
            }
        }
        return "";
    }
}
