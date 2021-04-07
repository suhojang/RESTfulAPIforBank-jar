/**
* Class  DateUtil.java
* @program 날짜 관련 유틸
* @description 날짜 관련 유틸
* 
* @author 기웅정보통신
* @update :Feb 1, 2007
* @package com.kwic.util; 
* @see
* 
* @DBTable
*/
package com.kwic.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	 /**
     * 
     * format에 맞는 현재 날짜 및 시간을 리턴
     * 
     * @param format
     * 
     * @return String
     */
    public static String getCurrentDateTime(String format)
    {
        return new SimpleDateFormat(format).format(new Date());
    }
    
    /**
     * Date 값을 특정한 format 으로 변환 시켜준다. 
     * Date가 Null 이면 공백 String을 Return 한다.
     * @param  date
     * @param  format
     * @return String
     */
    public static String dateToString(Date date, String format) {
        if (date == null) {
            return "";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            return formatter.format(date);
        }

    }
    
    /**
     * 포맷에 맞는 문자열을 java.util.Date 형으로 변환한다.
     *
     * @param  strDate
     * @param  format
     * @return Date
     */
    public static Date stringToDate(String strDate, String format) {

        Date date = null;

        if (strDate == null) {
            return null;

        } else {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(format);
                date = formatter.parse(strDate);
            } catch (ParseException e) {
                return null;
            }
        }
        return date;
    }

    /**
     * yyyy/MM/dd 형태의 문자열을 java.util.Date 형으로 변환한다.
     *
     * @param strDate
     * @return Date
     */
    public static Date stringToDate(String strDate) {
        Date date = null;
        if (strDate != null && !"".equals(strDate)) {
            date = stringToDate(strDate, "yyyy-MM-dd");
        }
        return date;
    }

    /**
     * yyyy/MM/dd 형태의 문자열을 java.sql.Date 형으로 변환한다.
     *
     * @param strDate
     * @return java.sql.Date
     */
    public static java.sql.Date stringToSqlDate(String strDate) {
        return dateToSqlDate(stringToDate(strDate));
    }

    /**
     * java.util.Date 형의 데이터를 java.sql.Date형으로 변환한다.
     *
     * @param date
     * @return java.sql.Date
     */
    public static java.sql.Date dateToSqlDate(Date date) {
    	
        java.sql.Date sqlDate = null;
        
        if (date != null) {
        	
            sqlDate = new java.sql.Date(date.getTime());
            
        }
        
        return sqlDate;
    }
    
    /**
     * date 더하기 빼기
     *
     * @param 	date	date객체
     * @param 	amount	date객체에 반영될 값
     * @return 	Date	
     */
    public static Date addDate(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, amount);
        return calendar.getTime();
    }
    
    /**
     * 
     * format에 날짜가 유효한 날짜인지를 검사
     *
     * @param date 
     * @param format 
     *
     * @return boolean
     */
    public boolean isValidDate(String date, String format)
    {  
       SimpleDateFormat sDateFormat = new  SimpleDateFormat(format);
       
       sDateFormat.setLenient(false);
       
       return sDateFormat.parse(date,new ParsePosition(0)) == null ? false : true;
    }
    
    /**
     * 
     * format에 날짜가 유효한 시간인지를 검사
     *
     * @param time 
     * @param format 
     *
     * @return boolean
     */
    public boolean isValidTime(String time, String format)
    {  
       SimpleDateFormat sDateFormat = new  SimpleDateFormat(format);
       
       sDateFormat.setLenient(false);
       
       return sDateFormat.parse(time,new ParsePosition(0)) == null ? false : true;
    }
    
    /**
     * 
     * 요일에 대한 int를 리턴
     * 0=일요일,1=월요일,2=화요일,3=수요일,4=목요일,5=금요일,6=토요일
     * 
     * @param year 
     * @param month
     * @param day 
     *
     * @return String
     */
    public int getWeekDay(int year,int month,int day)
    {
        Calendar cal = Calendar.getInstance();
        
        cal.set(year,month-1,day);
        
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
        
    }
}



