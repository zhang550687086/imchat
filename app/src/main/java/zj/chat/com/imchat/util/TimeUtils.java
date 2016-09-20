package zj.chat.com.imchat.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TimeUtils
 */
public class TimeUtils {

    public static final SimpleDateFormat DEFAULT_TIME_FORMAT = new SimpleDateFormat(
            "HH:mm:ss");
    public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat(
            "yyyy-MM-dd");
    public static final SimpleDateFormat FORMAT_FULL = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    public static final SimpleDateFormat FORMAT_DATE_CHS = new SimpleDateFormat(
            "yyyy年MM月dd日");

    private TimeUtils() {
        throw new AssertionError();
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_TIME_FORMAT);
    }

    public static String getDate() {
        return getTime(getCurrentTimeInLong(), FORMAT_DATE);
    }

    public static String getFull() {
        return getTime(getCurrentTimeInLong(), FORMAT_FULL);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    public static String getCurrentTimeAllString() {
        return getTime(getCurrentTimeInLong(), FORMAT_FULL);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    public static boolean DateCompare(String early, String late, String sign)
            throws ParseException {
        // 设定时间的模板
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdfShort = new SimpleDateFormat("HH:mm");
        Date e, l, s;
        // 得到指定模范的时间
        if (early.length() < 6) {
            e = sdfShort.parse(early);
        } else {
            e = sdf.parse(early);
        }
        if (late.length() < 6) {
            l = sdfShort.parse(late);
        } else {
            l = sdf.parse(late);
        }
        if (sign.length() < 6) {
            s = sdfShort.parse(sign);
        } else {
            s = sdf.parse(sign);
        }
        // 比较
        return e.getTime() < s.getTime() && s.getTime() < l.getTime();


    }

    /**
     * return time1 >= time2
     */
    public static boolean DateCompare(String intime, String sign)
            throws ParseException {
        // 设定时间的模板
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdfShort = new SimpleDateFormat("HH:mm");
        Date i, s;
        // 得到指定模范的时间
        if (intime.length() < 6) {
            i = sdfShort.parse(intime);
        } else {
            i = sdf.parse(intime);
        }
        if (sign.length() < 6) {
            s = sdfShort.parse(sign);
        } else {
            s = sdf.parse(sign);
        }
        // 比较
        return i.getTime() > s.getTime();


    }


    public static String getCurrYearLastDay_CHS() {
        return FORMAT_DATE_CHS.format(getCurrYearLast());
    }

    public static String getCurrYearLastDay() {
        return FORMAT_DATE.format(getCurrYearLast());
    }

    /**
     * 获取当年的最后一天
     *
     * @param
     * @return
     */
    public static Date getCurrYearLast() {
        Calendar currCal = Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearLast(currentYear);
    }

    /**
     * 获取某年最后一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getYearLast(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();
        return currYearLast;
    }
}
