package com.xunjia.framework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * @author 姜浩
 * 日期工具类
 */
public class DateUtils {

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static final String MONTH_PATTERN = "yyyy-MM";

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static SimpleDateFormat sdf;

    static {
        sdf = new SimpleDateFormat();
    }

    /**
     * 日期格式化
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        sdf.applyPattern(pattern);
        return sdf.format(date);
    }

    /**
     * 将字符串转换为日期对象
     *
     * @param str
     * @param pattern
     * @return
     * @throws ParseException 转换与pattern不匹配的字符串，或非法的日期时间时，抛出该异常
     */
    public static synchronized Date parse(String str, String pattern) throws ParseException {
        sdf.applyPattern(pattern);
        return sdf.parse(str);
    }

    /**
     * 将输入的毫秒数转换为时分秒
     *
     * @return
     */
    public static String getDurationBreakdown(long millis) {
        String[] units = {"时", "分", "秒"};
        Long[] values = new Long[units.length];
        if (millis <= 0) {
            throw new IllegalArgumentException("输入的毫秒数必须大于0。");
        }

        values[0] = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(values[0]);
        values[1] = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(values[1]);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        values[2] = (seconds == 0 && millis > 0) ? 1 : seconds;

        StringBuilder sb = new StringBuilder(64);
        boolean startPrinting = false;
        for (int i = 0; i < units.length; i++) {
            if (!startPrinting && values[i] != 0)
                startPrinting = true;
            if (startPrinting) {
                sb.append(values[i]);
                sb.append(units[i]);
            }
        }
        return sb.toString();
    }

    public static Date addDate(Date originalDate, int addDay) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(originalDate);
        calendar.add(Calendar.DATE, addDay);
        return calendar.getTime();
    }

    public static Date addMonth(Date originalDate, int addMonth) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(originalDate);
        calendar.add(Calendar.MONTH, addMonth);
        return calendar.getTime();
    }

    public static Date addYear(Date originalDate, int addYear) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(originalDate);
        calendar.add(Calendar.YEAR, addYear);
        return calendar.getTime();
    }

    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE);
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static String getFirstDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getYear(date), getMonth(date) - 1, 1);
        return format(calendar.getTime(), DATE_PATTERN);
    }

    public static String getLastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getYear(date), getMonth(date), 0);
        return format(calendar.getTime(), DATE_PATTERN);
    }
}
