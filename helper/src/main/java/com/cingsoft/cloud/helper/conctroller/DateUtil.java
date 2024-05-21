package com.cingsoft.cloud.helper.conctroller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static String  getNow(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+ 1;
        int day = c.get(Calendar.DATE);

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        String str= String.format("%d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
        return str;
    }

    public static String timeStamp2Date(String seconds) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }

        /*
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }*/

        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        //linux 平台需要加000
        //return sdf.format(new Date(Long.valueOf(seconds+"000")));

        return sdf.format(new Date(Long.valueOf(seconds)));
    }

    public static String  getNextDay(){
        Calendar c = Calendar.getInstance();
        int s = c.get(Calendar.DATE);
        c.set(Calendar.DATE, s+1);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+ 1;
        int day = c.get(Calendar.DATE);

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        String str= String.format("%d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
        return str;
    }

    public static int getDistanceDays(String startTime, String endTime)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int days= 0;

        try {
            Date start = simpleDateFormat.parse(startTime);
            Date end = simpleDateFormat.parse(endTime);
            days= getDistanceDays(start, end);

        }catch (Exception e){ e.printStackTrace(); }
        return days;
    }

    public static int getDistanceDays(Date startTime, Date endTime) {
        int days = 0;
        long time1 = startTime.getTime();
        long time2 = endTime.getTime();

        long diff = time2 - time1;
        days = (int) (diff / (24 * 60 * 60 * 1000));
        return days;
    }

    public double getDistanceHour(String startTime, String endTime)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        double hour= 0;

        try {
            Date start = simpleDateFormat.parse(startTime);
            Date end = simpleDateFormat.parse(endTime);
            hour= getDistanceHour(start, end);

        }catch (Exception e){ e.printStackTrace(); }
        return hour;
    }

    public double getDistanceHour(Date startTime, Date endTime) {
        double hour = 0;
        long time1 = startTime.getTime();
        long time2 = endTime.getTime();

        long diff = time2 - time1;
        hour = (diff / (60 * 60 * 1000));
        return hour;
    }

    public static long timeStamp(String date) {
        long stamp= 0;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = format.parse( date );
            stamp= d.getTime() / 1000;
        } catch ( Exception E) { E.printStackTrace();
            return stamp;
        }

        return stamp;
    }
}

