package cool.hyz.musicplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author xujiayi
 * @date 2024/7/11
 * 我只是个自由的主！
 */
public class DateUtils {
    public static String getDateStr(String time) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(time);
        } catch (Exception ignored) {}

        if (date == null) return "最近";
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(date);

        long now = new Date().getTime() / 1000;
        long dTime = now - date.getTime() / 1000;
        if (dTime < 10) return "刚刚";
        else if (dTime < 60) return dTime + "秒前";
        else if (dTime < 60 * 60) return (dTime / 60) + "分钟前";
        else if (dTime < 60 * 60 * 24) return (dTime / 60 / 60) + "小时前";
        else if (dTime < 60 * 60 * 24 * 10) return (dTime / 60 / 60 / 24) + "天前";
        else return calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" +calendar.get(Calendar.DATE)  + "日";
    }
    public static Date getDate(String time) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(time);
        } catch (Exception ignored) {}
        return date;
    }

    public static  String formatTime(double time) {
        String min = ((int) time / 1000 / 60) + "";
        min = (min.length() == 1 ? "0" : "") + min;
        String sec = ((int) time / 1000 % 60) + "";
        sec = (sec.length() == 1 ? "0" : "") + sec;
        return min + ":" + sec;
    }
}
