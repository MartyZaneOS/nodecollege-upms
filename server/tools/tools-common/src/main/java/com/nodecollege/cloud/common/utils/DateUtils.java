package com.nodecollege.cloud.common.utils;

import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.exception.NCException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期操作辅助类
 *
 * @author ShenHuaJie
 * @version $Id: DateUtil.java, v 0.1 2014年3月28日 上午8:58:11 ShenHuaJie Exp $
 */
public class DateUtils {

    public final static long ONE_DAY_SECONDS = 86400;
    public final static long ONE_DAY_MILL_SECONDS = 86400000;
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    /**
     * 格式化日期
     *
     * @param date
     * @return
     */
    public static String format(Object date) {
        return format(date, DATE_PATTERN.YYYY_MM_DD);
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Object date, String pattern) {
        if (date == null) {
            return null;
        }
        if (pattern == null) {
            return format(date);
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 获取日期
     *
     * @return
     */
    public static String getDate() {
        return format(new Date());
    }

    /**
     * 获取日期时间，精确到秒
     *
     * @return
     */
    public static String getDateTimeStr() {
        return format(new Date(), DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取日期
     *
     * @param pattern
     * @return
     */
    public static String getDateTime(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * 日期计算
     *
     * @param date
     * @param field
     * @param amount
     * @return
     */
    public static Date addDate(Date date, int field, int amount) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    /**
     * 字符串转换为日期
     *
     * @param date    String
     * @param pattern String
     * @return Date
     */
    public static Date stringToDate(String date, String pattern) {
        if (date == null) {
            return null;
        }

        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 此类型的 20180910113900Z 转换为标准日期格式字符串
     *
     * @param dateString
     * @return
     */

    public static String stringToDateString(String dateString) {

        if (dateString == null) {
            return null;
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
        TimeZone tz1 = TimeZone.getTimeZone("GMT");
        df.setTimeZone(tz1);
        Date time;
        try {
            time = df.parse(dateString);
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));

            SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
            String format = formatter.format(time);
            return format;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 判断时间是否在某个时间段内
     *
     * @param nowTime   当前
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return boolean
     * @author fanqie
     * @date 2018/11/21
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        //设置当前时间
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        //设置开始时间
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        //设置结束时间
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        //处于开始时间之后，和结束时间之前的判断
        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 字符串转换为日期:不支持yyM[M]d[d]格式
     *
     * @param date
     * @return
     */
    public static Date stringToDate(String date) {
        if (date == null) {
            return null;
        }
        String separator = String.valueOf(date.charAt(4));
        String pattern = "yyyyMMdd";
        if (!separator.matches("\\d*")) {
            pattern = "yyyy" + separator + "MM" + separator + "dd";
            if (date.length() < 10) {
                pattern = "yyyy" + separator + "M" + separator + "d";
            }
        } else if (date.length() < 8) {
            pattern = "yyyyMd";
        }
        pattern += " HH:mm:ss.SSS";
        pattern = pattern.substring(0, Math.min(pattern.length(), date.length()));
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 间隔天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Integer getDayBetween(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);

        long n = end.getTimeInMillis() - start.getTimeInMillis();
        return (int) (n / (60 * 60 * 24 * 1000L));
    }

    /**
     * 获取指定时间所属年份的第一天的起始时间
     *
     * @param date 日期
     * @return Date 年度第一天
     */
    public static Date getFirstDayTimeOfYear(Date date) {
        if (NCUtils.isNullOrEmpty(date)) {
            throw new IllegalArgumentException("date empty");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * 获取指定月份上个月
     *
     * @param date 时间
     * @return Date 上个月
     * @author zhaojy
     * @date 2019-09-02
     */
    public static Date getPriorMonth(Date date) {
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        int month = start.get(Calendar.MONTH);
        start.set(Calendar.MONTH, --month);
        return start.getTime();
    }

    /**
     * 获取指定月份下个月
     *
     * @param date 时间
     * @return Date 下个月
     * @author zhaojy
     * @date 2019-09-02
     */
    public static Date getNextMonth(Date date) {
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        int month = start.get(Calendar.MONTH);
        int newMonth = ++month;
        if (month == Calendar.DECEMBER) {
            newMonth = Calendar.JANUARY;
        }
        start.set(Calendar.MONTH, newMonth);
        return start.getTime();
    }

    /**
     * 间隔秒数
     *
     * @param startDate Date 开始时间
     * @param endDate   Date  结束时间
     * @return Integer 秒数
     */
    public static Integer getSecondsBetween(Date startDate, Date endDate) {
        return (int) ((endDate.getTime() - startDate.getTime()) / (1000L));
    }

    /**
     * 间隔小时
     *
     * @param startDate Date 开始时间
     * @param endDate   Date  结束时间
     * @return Integer 秒数
     */
    public static Integer getHoursBetween(Date startDate, Date endDate) {
        return (int) ((endDate.getTime() - startDate.getTime()) / (1000L * 60 * 60));
    }

    /**
     * 间隔月
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Integer getMonthBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return null;
        }
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        int year1 = start.get(Calendar.YEAR);
        int year2 = end.get(Calendar.YEAR);
        int month1 = start.get(Calendar.MONTH);
        int month2 = end.get(Calendar.MONTH);
        int n = (year2 - year1) * 12;
        n = n + month2 - month1;
        return n;
    }

    /**
     * 间隔月，多一天就多算一个月
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Integer getMonthBetweenWithDay(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return null;
        }
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        int year1 = start.get(Calendar.YEAR);
        int year2 = end.get(Calendar.YEAR);
        int month1 = start.get(Calendar.MONTH);
        int month2 = end.get(Calendar.MONTH);
        int n = (year2 - year1) * 12;
        n = n + month2 - month1;
        int day1 = start.get(Calendar.DAY_OF_MONTH);
        int day2 = end.get(Calendar.DAY_OF_MONTH);
        if (day1 <= day2) {
            n++;
        }
        return n;
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @param format
     * @return
     */
    public static String timeStamp2DateString(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || NCConstants.NULL_STRING.equalsIgnoreCase(seconds)) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = DATE_PATTERN.YYYY_MM_DD_HH_MM_SS;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.parseLong(seconds + "000")));
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param dateStr 字符串日期
     * @param format  如：yyyy-MM-dd HH:mm:ss
     * @return
     */

    public static String dateStrToStr(String dateStr, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(dateStr).getTime() / 1000);
        } catch (Exception e) {
            logger.error("dateStrToStr error:", e);
        }
        return "";
    }

    /**
     * 获取系统当前时间
     *
     * @return
     */
    public static Date getCurrentTS() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 将字符串转换为date类型，指定字符串格式
     *
     * @param sDate  String  时间字符串
     * @param format String 格式
     * @return Date
     */
    public static Date parseDate(String sDate, String format) {
        try {
            if (StringUtils.isBlank(format)) {
                throw new ParseException("Null format. ", 0);
            }

            DateFormat dateFormat = new SimpleDateFormat(format);

            if (sDate == null) {
                throw new ParseException("sDate is null", 0);
            }

            return dateFormat.parse(sDate);
        } catch (Exception e) {
            logger.error("method parseDate error:", e);
        }
        return null;
    }

    public static Date getStartDate(Date startDate, String s) {
        String sDate = s + " 00:00:00";
        if (StringUtils.isNotEmpty(sDate)) {
            startDate = DateUtils.parseDate(sDate, DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
        }
        return startDate;
    }

    public static Date getEndDate(Date endDate, String s) {
        String eDate = s + " 23:59:59";
        if (StringUtils.isNotEmpty(eDate)) {
            endDate = DateUtils.parseDate(eDate, DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
        }
        return endDate;
    }

    /**
     * 取得当前日期
     *
     * @return Date
     */
    public static Date getCurrentDateTime() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
        try {
            date = format.parse(format.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 用默认的日期格式，格式化一个Date对象
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return date == null ? "" : sdf.format(date);
    }

    /**
     * 根据传入的格式，将日期对象格式化为日期字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format) {
        String s = "";
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            s = sdf.format(date);
        }
        return s;
    }

    /**
     * 用默认的日期时间格式，格式化一个Date对象
     *
     * @param date
     * @return
     */
    public static String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
        return date == null ? "" : sdf.format(date);
    }

    /**
     * 根据传入的格式，将日期对象格式化为时间字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatTime(Date date, String format) {
        String s = "";
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            s = sdf.format(date);
        }
        return s;
    }

    /**
     * 日期后推
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * 利用默认的格式（yyyy-MM-dd）将一个表示日期的字符串解析为日期对象
     *
     * @param dateStr 时间字符串
     * @return Date
     */
    public static Date parseDate(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            logger.info("字符串转日期失败", e);
            throw new NCException("-1", "字符串转日期失败");
        }
        return date;
    }

    /**
     * 利用默认的格式（yyyy-MM-dd HH:mm:ss）将一个表示时间的字符串解析为日期对象
     *
     * @param s
     * @return
     */
    public static Date parseTime(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
        Date date = null;
        try {
            date = sdf.parse(s);
        } catch (ParseException e) {
            logger.info("字符串转时间失败", e);
            throw new NCException("-1", "字符串转时间失败");
        }
        return date;
    }

    /**
     * 得到当前年份
     *
     * @return
     */
    public static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    /**
     * 获取年份
     *
     * @param date Date
     * @return int
     */
    public static int getCurrentYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * 得到当前月份（1至12）
     *
     * @return
     */
    public static int getCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取yyyy-MM-dd格式的当前系统日期
     *
     * @return String
     */
    public static String getCurrentDateAsString() {
        return new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD).format(new Date());
    }

    /**
     * 获取yyyy-MM-dd格式的当前系统日期
     *
     * @return String
     */
    public static Date getCurrentDay() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD);
        try {
            return simpleDateFormat.parse(simpleDateFormat.format(new Date()));
        } catch (ParseException e) {
            logger.error("method getCurrentDay error:", e);
        }
        return null;
    }

    /**
     * 获取HH:mm:ss格式的当前系统时间
     *
     * @return
     */
    public static String getCurrentTime() {
        return new SimpleDateFormat(DATE_PATTERN.HH_MM_SS).format(new Date());
    }

    /**
     * 获取指定格式的当前系统时间
     *
     * @param format
     * @return
     */
    public static String getCurrentTime(String format) {
        SimpleDateFormat t = new SimpleDateFormat(format);
        return t.format(new Date());
    }

    /**
     * 获取格式为yyyy-MM-dd HH:mm:ss的当前系统日期时间
     *
     * @return
     */
    public static String getCurrentDateTimeAsString() {
        return getCurrentDateTime(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
    }

    public static int getDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * ASN1的时间格式
     */
    public static String getGeneralizedTime() {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN.YYYYMMDDHHMMSS);
        return format.format(new Date(System.currentTimeMillis())) + "Z";
    }

    public static int getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取星期X中文名称
     *
     * @return
     */
    public static String getChineseDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        return getChineseDayOfWeek(cal.getTime());
    }

    /**
     * 获取星期X中文名称
     *
     * @param date
     * @return
     */
    public static String getChineseDayOfWeek(String date) {
        return getChineseDayOfWeek(parseDate(date));
    }

    /**
     * 获取星期X中文名称
     *
     * @param date
     * @return
     */
    public static String getChineseDayOfWeek(Date date) {
        int dateOfWeek = getDayOfWeek(date);
        if (dateOfWeek == Calendar.MONDAY) {
            return "星期一";
        } else if (dateOfWeek == Calendar.TUESDAY) {
            return "星期二";
        } else if (dateOfWeek == Calendar.WEDNESDAY) {
            return "星期三";
        } else if (dateOfWeek == Calendar.THURSDAY) {
            return "星期四";
        } else if (dateOfWeek == Calendar.FRIDAY) {
            return "星期五";
        } else if (dateOfWeek == Calendar.SATURDAY) {
            return "星期六";
        } else if (dateOfWeek == Calendar.SUNDAY) {
            return "星期日";
        }
        return null;
    }

    /**
     * 比较两个日期是否相同
     *
     * @param d1
     * @param d2
     * @return boolean
     */
    public static boolean sameDate(Date d1, Date d2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        //fmt.setTimeZone(new TimeZone()); // 如果需要设置时间区域，可以在这里设置
        return fmt.format(d1).equals(fmt.format(d2));
    }

    public static int getDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMaxDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取指定月份的第一天
     *
     * @param dateStr String yyyy-MM
     * @return Date 月度第一天
     */
    public static Date getFirstDayOfMonth(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) {
            throw new IllegalArgumentException("date empty");
        }
        dateStr += "-01";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD);
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            return cal.getTime();
        } catch (ParseException e) {
            logger.error("getLastDayOfMonth error:", e);
        }
        return null;
    }

    /**
     * 获取指定月份的第一天
     *
     * @param date 日期
     * @return Date 月度第一天
     */
    public static Date getFirstDayOfMonth(Date date) {
        if (NCUtils.isNullOrEmpty(date)) {
            throw new IllegalArgumentException("date empty");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * 判断是否是第一个月
     *
     * @param date 日期
     * @return Boolean true: 是第一个月，false：不是
     */
    public static boolean isFirstMonthOfYear(Date date) {
        if (NCUtils.isNullOrEmpty(date)) {
            throw new IllegalArgumentException("date empty");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) == Calendar.JANUARY;
    }

    /**
     * 判断是否是最后个月
     *
     * @param date 日期
     * @return Boolean true: 是第一个月，false：不是
     */
    public static boolean isLastMonthOfYear(Date date) {
        if (NCUtils.isNullOrEmpty(date)) {
            throw new IllegalArgumentException("date empty");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) == Calendar.DECEMBER;
    }

    /**
     * 获取指定月份的第一天
     *
     * @param date 日期
     * @return Date 月度第一天
     */
    public static Date getLastDayOfMonth(Date date) {
        if (NCUtils.isNullOrEmpty(date)) {
            throw new IllegalArgumentException("date empty");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        return cal.getTime();
    }

    /**
     * 获取指定月份的最后一天
     *
     * @param yyyyMM String yyyy-MM
     * @return Date 月度第最后天
     */
    public static Date getLastDayOfMonth(String yyyyMM) {
        if (StringUtils.isEmpty(yyyyMM)) {
            throw new IllegalArgumentException("date empty");
        }
        yyyyMM += "-01";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD);
        Date date = null;
        try {
            date = simpleDateFormat.parse(yyyyMM);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            return cal.getTime();
        } catch (ParseException e) {
            logger.error("getLastDayOfMonth error:", e);
        }
        return null;
    }

    /**
     * 获取指定月份属于哪个季度
     *
     * @param yyyyMM String
     * @return Integer
     */
    public static Integer getYearMonthQuarter(String yyyyMM) {
        if (StringUtils.isEmpty(yyyyMM)) {
            return 1;
        }
        yyyyMM += "-01";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD);
        Date date = null;
        try {
            date = simpleDateFormat.parse(yyyyMM);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int currentMonth = cal.get(Calendar.MONTH) + 1;

            if (currentMonth >= 1 && currentMonth <= 3) {
                return 1;
            } else if (currentMonth >= 4 && currentMonth <= 6) {
                return 2;
            } else if (currentMonth >= 7 && currentMonth <= 9) {
                return 3;
            } else if (currentMonth >= 10 && currentMonth <= 12) {
                return 4;
            }
        } catch (ParseException e) {
            logger.error("getYearMonthQuarter error:", e);
        }
        return 1;
    }

    /**
     * 获取指定月份属于哪个季度
     *
     * @param date Date
     * @return Integer
     */
    public static Integer getYearMonthQuarter(Date date) {
        if (NCUtils.isNullOrEmpty(date)) {
            return 1;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int currentMonth = cal.get(Calendar.MONTH) + 1;

        if (currentMonth >= 1 && currentMonth <= 3) {
            return 1;
        } else if (currentMonth >= 4 && currentMonth <= 6) {
            return 2;
        } else if (currentMonth >= 7 && currentMonth <= 9) {
            return 3;
        } else if (currentMonth >= 10 && currentMonth <= 12) {
            return 4;
        }
        return 1;
    }

    /**
     * 获取指定年月中的年份
     * <p>
     * todo 缺校验
     *
     * @param yyyyMM String
     * @return Integer
     */
    public static String getMonthOfYearMonth(String yyyyMM) {
        if (StringUtils.isEmpty(yyyyMM)) {
            throw new IllegalArgumentException("yyyyMM empty");
        }

        return yyyyMM.substring(0, yyyyMM.indexOf("-"));
    }

    public static int getDayOfYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    public static int getDayOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    public static int getDayOfWeek(String date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parseDate(date));
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static int getDayOfMonth(String date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parseDate(date));
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfYear(String date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parseDate(date));
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取指定格式的当前系统日期时间
     *
     * @param format
     * @return
     */
    public static String getCurrentDateTime(String format) {
        SimpleDateFormat t = new SimpleDateFormat(format);
        return t.format(new Date());
    }

    public static String toDateTimeString(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS).format(date);
    }

    public static String toString(Date date, String format) {
        SimpleDateFormat t = new SimpleDateFormat(format);
        return t.format(date);
    }

    public static String toTimeString(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(DATE_PATTERN.HH_MM_SS).format(date);
    }

    /**
     * 时间戳转换
     *
     * @param time
     * @return
     */
    public static String longTimeToDateTimeString(Long time) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
        String d = format.format(time);
        return d;
    }

    /**
     * 时间戳转换
     *
     * @param time
     * @return
     */
    public static Date longTimeToDateTime(Long time) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
        String d = format.format(time);
        return parseTime(d);
    }

    /**
     * 获得当天的起始时间。
     *
     * @return
     */
    public static Date getTodayStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获得当天的结束时间。
     *
     * @return
     */
    public static Date getTodayEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取某一天的结束时间
     *
     * @param date Date
     * @return Date
     */
    public static Date getDayStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    /**
     * 获取某一天的结束时间
     *
     * @param date Date
     * @return Date
     */
    public static Date getDayEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 获得本周周一的起始时间。
     *
     * @return
     */
    public static Date getCurrentWeekStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONDAY), calendar.get(Calendar.DAY_OF_MONTH), 0,
                0, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime();
    }

    /**
     * 获得本周周日的结束时间。
     *
     * @return
     */
    public static Date getCurrentWeekEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getCurrentWeekStartTime());
        calendar.add(Calendar.DAY_OF_WEEK, 7);
        return calendar.getTime();
    }

    /**
     * 获得本月第一天的起始时间。
     *
     * @return Date
     */
    public static Date getCurrentMonthStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONDAY), calendar.get(Calendar.DAY_OF_MONTH), 0,
                0, 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static void main(String[] args) {
        System.out.println(getDayStartTime(new Date()));

    }

    /**
     * 获得本月最后一天的结束时间。
     *
     * @return
     */
    public static Date getCurrentMonthEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONDAY), calendar.get(Calendar.DAY_OF_MONTH), 0,
                0, 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        return calendar.getTime();
    }

    /**
     * 获取两个时间的毫秒差，可以用来验证时间是否有效。
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return long
     * @author zhaojy
     * @date 2018-08-17
     */
    public static long getMillSeconds(Date startDate, Date endDate) {
        if (null == startDate || null == endDate) {
            return -1;
        }
        return endDate.getTime() - startDate.getTime();
    }

    /**
     * 当前季度的开始时间。
     *
     * @return
     */
    public static Date getCurrentQuarterStartTime() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3) {
                calendar.set(Calendar.MONTH, 0);
            } else if (currentMonth >= 4 && currentMonth <= 6) {
                calendar.set(Calendar.MONTH, 3);
            } else if (currentMonth >= 7 && currentMonth <= 9) {
                calendar.set(Calendar.MONTH, 6);
            } else if (currentMonth >= 10 && currentMonth <= 12) {
                calendar.set(Calendar.MONTH, 9);
            }
            calendar.set(Calendar.DATE, 1);
            SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat longSdf = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
            now = longSdf.parse(shortSdf.format(calendar.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前季度的结束时间。
     *
     * @return
     */
    public static Date getCurrentQuarterEndTime() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3) {
                calendar.set(Calendar.MONTH, 2);
                calendar.set(Calendar.DATE, 31);
            } else if (currentMonth >= 4 && currentMonth <= 6) {
                calendar.set(Calendar.MONTH, 5);
                calendar.set(Calendar.DATE, 30);
            } else if (currentMonth >= 7 && currentMonth <= 9) {
                calendar.set(Calendar.MONTH, 8);
                calendar.set(Calendar.DATE, 30);
            } else if (currentMonth >= 10 && currentMonth <= 12) {
                calendar.set(Calendar.MONTH, 11);
                calendar.set(Calendar.DATE, 31);
            }
            SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat longSdf = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
            now = longSdf.parse(shortSdf.format(calendar.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 日期格式
     **/
    public interface DATE_PATTERN {
        String HHMMSS = "HHmmss";
        String HH_MM_SS = "HH:mm:ss";
        String HH = "HH";
        String YYYYMMDD = "yyyyMMdd";
        String YYYY_MM_DD = "yyyy-MM-dd";
        String YYYY_M_D_SLASH = "yyyy/M/d";
        String YYYY_MM = "yyyy-MM";
        String YYYY = "yyyy";
        String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
        String MMDDHHMM = "MMddHHmm";
        String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
        String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
        String NO_SECOND_FORMAT = "yyyy-MM-dd HH:mm";
        String GREGORIAN_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String NO_MSGREGORIAN_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        String YEAR_HOUR_FORMAT = "yyyyMMddHH";
        String MONTH_FORMAT = "yyyyMM";

        /**
         * 中文：yyyy年MM月dd日
         */
        String CHINESE_YEAR_MONTH_DAY = "yyyy年MM月dd日";
        /**
         * 中文：yyyy年mm月
         */
        String CHINESE_YEAR_MONTH = "yyyy年MM月";
    }
}
