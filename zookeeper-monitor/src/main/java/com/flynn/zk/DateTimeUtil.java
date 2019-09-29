package com.flynn.zk;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * 时间转化
 * @author chester
 *
 */
public class DateTimeUtil {
	//一天的毫秒数
	public static final int OneDayMillis = 24*60*60*1000;
	//一天的秒数
	public static final int OneDaySeconds = 24*60*60;
	
	private static final String DataFormat = "yyyy-MM-dd HH:mm:ss";
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>(); 
 
    public static DateFormat getDateFormat()   
    {  
       return getDateFormat(DataFormat); 
    }  
    
    public static DateFormat getDateFormat(String dateFormat)   
    {  
        DateFormat df = threadLocal.get();  
        if(df==null){  
            df = new SimpleDateFormat(dateFormat);  
            threadLocal.set(df);  
        }  
        return df;  
    }  
    
    public static String format(Date date) {
        return getDateFormat().format(date);
    }
    /**
     * 建字符串解析成时间,格式： yyyy-MM-dd HH:mm:ss
     * @param strDate yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date parse(String strDate){
        try {
			return getDateFormat().parse(strDate);
		} catch (ParseException e) {
			throw new RuntimeException("时间格式转换错误： "+strDate);
		}
    }
    /**
     * 字符串转毫秒时间撮
     * @param strDate yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long timeMillis(String strDate){
    	Date date = parse(strDate);
    	return date.getTime();
    }
    /**
     * 
     * @param strDate yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static int timeSeconds(String strDate){
    	return (int)(timeMillis(strDate)/1000);
    }
    
    public static String format(long millis){
    	return format(new Date(millis));
    }
    public static String format(int seconds){
    	return format(seconds*1000L);
    }
    /**
     * 转换成0点的时间撮 ,以毫秒为单位
     * @param millis 毫秒
     * @return
     */
    public static long zeroTime(){
    	Calendar calendar = Calendar.getInstance();
//        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");  
//        Calendar calendar = Calendar.getInstance(curTimeZone); 
        
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND,0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	return calendar.getTimeInMillis();
    }
    
    /**
     * 转换成0点的时间撮 ,以毫秒为单位
     * @param millis 毫秒
     * @return
     */
    public static long zeroTime(long millis){
    	Calendar calendar = Calendar.getInstance();
//        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");  
//        Calendar calendar = Calendar.getInstance(curTimeZone); 
        
    	calendar.setTimeInMillis(millis);
    	
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND,0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	return calendar.getTimeInMillis();
    }
    /**
     * 0点的时间撮， 以秒为单位
     * @param time
     * @return
     */
    public static int zeroTime(int seconds){
    	return (int)(zeroTime(seconds*1000L)/1000);
    }
    /**
     * 是否在同一天,毫秒为单位
     * @param millis1
     * @param millis2
     * @return
     */
    public static boolean sameDay(long millis1,long millis2){
    	return (zeroTime(millis1) == zeroTime(millis2));
    }
    /**
     * 是否在同一天，秒为单位
     * @param seconds1
     * @param seconds2
     * @return
     */
    public static boolean sameDay(int seconds1,int seconds2){
    	return (zeroTime(seconds1) == zeroTime(seconds2));
    }
    /**
     * 昨天0点时间撮， 毫秒为单位
     * @return
     */
    public static long yesterdayZeroTime(){
    	return zeroTime(System.currentTimeMillis())-OneDayMillis;
    }
    /**
     * 明天0点时间撮，毫秒为单位
     * @return
     */
    public static long tomorrowZeroTime(){
    	return zeroTime(System.currentTimeMillis()) + OneDayMillis;
    }
    /**
     * 当前时间的毫秒数
     * @return
     */
    public static long millisTime() {
    	return System.currentTimeMillis();
    }
    /**
     * 当前时间秒数
     * @return
     */
    public static int time() {
		return (int) (millisTime() / 1000);
	}
    
    public static int getTimesWeekmorning(){ 
    	Calendar cal = Calendar.getInstance(); 
    	cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0,0); 
    	cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); 
    	return (int) (cal.getTimeInMillis()/1000); 
    } 
    
  //获得本周日24点时间 
    public static int getTimesWeeknight(){ 
	    Calendar cal = Calendar.getInstance(); 
	    cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0,0); 
	    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); 
	    return (int) ((cal.getTime().getTime()+ (7 * 24 * 60 * 60 * 1000))/1000); 
    } 
    //获得本月第一天0点时间 
    public static int getTimesMonthmorning(){ 
	    Calendar cal = Calendar.getInstance(); 
	    cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0,0); 
	    cal.set(Calendar.DAY_OF_MONTH,cal.getActualMinimum(Calendar.DAY_OF_MONTH)); 
	    return (int) (cal.getTimeInMillis()/1000); 
    } 
    //获得本月最后一天24点时间 
    public static int getTimesMonthnight(){ 
	    Calendar cal = Calendar.getInstance(); 
	    cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0,0); 
	    cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH)); 
	    cal.set(Calendar.HOUR_OF_DAY, 24); 
	    return (int) (cal.getTimeInMillis()/1000); 
    }
}
