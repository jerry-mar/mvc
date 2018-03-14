package com.jerry_mar.mvc.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static final String format = "yyyy-MM-dd HH:mm:ss";
    public static final String dateFormat = "yyyy-MM-dd";
    public static final String timeFormat = "HH:mm:ss";
    /**
     * @since 1.0
     * @param val 日期字符串
     * @param format 格式化字符串
     * @return 日期
     */
    public static Date parse(String val, String format) {
        Date date;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
            if (val.length() < format.length()) {
                val += format.substring(val.length()).replaceAll("[YyMmDdHhSs]",
                        "0");
            }
            date = dateFormat.parse(val);
        } catch (Exception e) {
            date = null;
        }
        return date;
    }

    /**
     * @since 1.0
     * @param val 日期字符串
     * @return 日期
     */
    public static Date parse(String val) {
        return parse(val, format);
    }

    /**
     * @since 1.0
     * @param val 日期字符串
     * @return 日期
     */
    public static Date parseDate(String val) {
        return parse(val, dateFormat);
    }

    /**
     * @since 1.0
     * @param val 日期字符串
     * @return 时间
     */
    public static Date parseTime(String val) {
        return parse(val, timeFormat);
    }

    /**
     * @since 1.0
     * @param date 日期
     * @param format 格式化字符串
     * @return 日期字符串
     */
    public static String parse(Date date, String format) {
        String result;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
            result = dateFormat.format(date);
        } catch (Exception e) {
            result = StringUtils.NULL;
        }
        return result;
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 日期字符串
     */
    public static String parse(Date date) {
        return parse(date, format);
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 日期字符串
     */
    public static String parseDate(Date date) {
        return parse(date, dateFormat);
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 时间字符串
     */
    public static String parseTime(Date date) {
        return parse(date, timeFormat);
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 年份
     */
    public static int year(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 月份
     */
    public static int month(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 哪天
     */
    public static int day(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 小时
     */
    public static int hour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 分钟
     */
    public static int minute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 秒
     */
    public static int second(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 毫秒时间戳
     */
    public static long millis(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 本月共几天
     */
    public static int daysOfMonth(Date date) {
        date = date(year(date), month(date), 1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return day(calendar.getTime());
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 本周周一
     */
    public static Date monday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day = day == 1 ? -6 : 2 - day;
        calendar.add(Calendar.DAY_OF_WEEK, day);
        return calendar.getTime();
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 本周周日
     */
    public static Date sunday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day = day == 1 ? 0 : 8 - day;
        calendar.add(Calendar.DAY_OF_WEEK, day);
        return calendar.getTime();
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 天数操作
     */
    public static Date operate(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        long millis = millis(date) + ((long) day) * 24 * 3600 * 1000;
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    /**
     * @since 1.0
     * @param year 年
     * @param month 月
     * @param day 日
     * @return 日期
     */
    public static Date date(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.getTime();
    }

    /**
     * @since 1.0
     * @param date 日期
     * @return 汉字格式日期字符串
     */
    public static String format(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH);
        int nowWeek = now.get(Calendar.WEEK_OF_MONTH);
        int nowDay = now.get(Calendar.DAY_OF_WEEK);
        int nowHour = now.get(Calendar.HOUR_OF_DAY);
        int nowMinute = now.get(Calendar.MINUTE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (year != nowYear) {
            return parse(date, dateFormat);
        }

        if (month != nowMonth || week != nowWeek) {
            return parse(date, "M月dd日");
        }

        int d = nowDay - day;
        if (Math.abs(d) > 2) {
            return parse(date, "M月dd日");
        }

        switch (d) {
            case -1 : return "明天" + parse(date, timeFormat);
            case 1 : return "昨天" + parse(date, timeFormat);
            case 2 : return "前天" + parse(date, timeFormat);
        }

        int h = nowHour - hour;
        if (h == 0) {
            if (nowMinute - minute < 1) {
                return "刚刚";
            } else {
                return (nowMinute - minute) + "分钟前";
            }
        }
        if (h <= 12) {
            return h + "小时前";
        }
        return "今天" + parse(date, timeFormat);
    }

    public static String format(String date) {
        return format(new Date(Long.parseLong(date)));
    }
}
