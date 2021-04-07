package com.kwic.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateFormat {
	public static String FORMAT_FULL_YEAR	= "yyyy";
	public static String FORMAT_SHORT_YEAR	= "yy";
	public static String FORMAT_MONTH		= "MM";
	public static String FORMAT_DAY			= "dd";
	public static String FORMAT_HOUR		= "HH";
	public static String FORMAT_HOUR24		= "KK";
	public static String FORMAT_HOUR23		= "HH";
	public static String FORMAT_MINUTE		= "mm";
	public static String FORMAT_SECOND		= "ss";
	public static String FORMAT_MILI_SECOND	= "SSS";

	private String format;
	private Map<String,Integer> indexMap;
	
	public DateFormat(String format){
		this.format	= format;
		parseFormat();
	}

	private void parseFormat(){
		indexMap	= new HashMap<String,Integer>();
		
		indexMap.put(FORMAT_FULL_YEAR	, format.lastIndexOf(FORMAT_FULL_YEAR));
		indexMap.put(FORMAT_SHORT_YEAR	, format.lastIndexOf(FORMAT_SHORT_YEAR));
		indexMap.put(FORMAT_MONTH		, format.lastIndexOf(FORMAT_MONTH));
		indexMap.put(FORMAT_DAY			, format.lastIndexOf(FORMAT_DAY));
		indexMap.put(FORMAT_HOUR		, format.lastIndexOf(FORMAT_HOUR));
		indexMap.put(FORMAT_HOUR24		, format.lastIndexOf(FORMAT_HOUR24));
		indexMap.put(FORMAT_HOUR23		, format.lastIndexOf(FORMAT_HOUR23));
		indexMap.put(FORMAT_MINUTE		, format.lastIndexOf(FORMAT_MINUTE));
		indexMap.put(FORMAT_SECOND		, format.lastIndexOf(FORMAT_SECOND));
		indexMap.put(FORMAT_MILI_SECOND		, format.lastIndexOf(FORMAT_MILI_SECOND));
	}
	
	
	public java.util.Date format(String dateString){
		Map<String,String> parseDate	= parse(dateString);
		
		Calendar cal	= Calendar.getInstance(Locale.KOREA);
		if(parseDate.get(FORMAT_FULL_YEAR)!=null){
			cal.set(Calendar.YEAR, Integer.parseInt(parseDate.get(FORMAT_FULL_YEAR)));
		}
		if(parseDate.get(FORMAT_SHORT_YEAR)!=null){
			cal.set(Calendar.YEAR, (cal.get(Calendar.YEAR)/100*100)+Integer.parseInt(parseDate.get(FORMAT_SHORT_YEAR)));
		}
		if(parseDate.get(FORMAT_MONTH)!=null){
			cal.set(Calendar.MONTH, Integer.parseInt(parseDate.get(FORMAT_MONTH))-1);
		}
		if(parseDate.get(FORMAT_DAY)!=null){
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parseDate.get(FORMAT_DAY)));
		}
		if(parseDate.get(FORMAT_HOUR)!=null){
			cal.set(Calendar.HOUR, Integer.parseInt(parseDate.get(FORMAT_HOUR)));
		}
		if(parseDate.get(FORMAT_HOUR24)!=null){
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parseDate.get(FORMAT_HOUR24)));
		}
		if(parseDate.get(FORMAT_HOUR23)!=null){
			if(Integer.parseInt(parseDate.get(FORMAT_HOUR23))==24){
				cal.set(Calendar.HOUR_OF_DAY, 0);
			}else{
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parseDate.get(FORMAT_HOUR23)));
			}
		}
		if(parseDate.get(FORMAT_MINUTE)!=null){
			cal.set(Calendar.MINUTE, Integer.parseInt(parseDate.get(FORMAT_MINUTE)));
		}
		if(parseDate.get(FORMAT_SECOND)!=null){
			cal.set(Calendar.SECOND, Integer.parseInt(parseDate.get(FORMAT_SECOND)));
		}
		if(parseDate.get(FORMAT_MILI_SECOND)!=null){
			cal.set(Calendar.MILLISECOND, Integer.parseInt(parseDate.get(FORMAT_MILI_SECOND)));
		}
		
		return cal.getTime();
	}
	
	private Map<String,String> parse(String dateString){
		Map<String,String> parseDate	= new HashMap<String,String>();

		if(indexMap.get(FORMAT_FULL_YEAR)>=0){parseDate.put(FORMAT_FULL_YEAR, dateString.substring(indexMap.get(FORMAT_FULL_YEAR),indexMap.get(FORMAT_FULL_YEAR)+FORMAT_FULL_YEAR.length()));}
		if(indexMap.get(FORMAT_SHORT_YEAR)>=0){parseDate.put(FORMAT_SHORT_YEAR, dateString.substring(indexMap.get(FORMAT_SHORT_YEAR),indexMap.get(FORMAT_SHORT_YEAR)+FORMAT_SHORT_YEAR.length()));}
		if(indexMap.get(FORMAT_MONTH)>=0){parseDate.put(FORMAT_MONTH, dateString.substring(indexMap.get(FORMAT_MONTH),indexMap.get(FORMAT_MONTH)+FORMAT_MONTH.length()));}
		if(indexMap.get(FORMAT_DAY)>=0){parseDate.put(FORMAT_DAY, dateString.substring(indexMap.get(FORMAT_DAY),indexMap.get(FORMAT_DAY)+FORMAT_DAY.length()));}
		if(indexMap.get(FORMAT_HOUR)>=0){parseDate.put(FORMAT_HOUR, dateString.substring(indexMap.get(FORMAT_HOUR),indexMap.get(FORMAT_HOUR)+FORMAT_HOUR.length()));}
		if(indexMap.get(FORMAT_HOUR24)>=0){parseDate.put(FORMAT_HOUR24, dateString.substring(indexMap.get(FORMAT_HOUR24),indexMap.get(FORMAT_HOUR24)+FORMAT_HOUR24.length()));}
		if(indexMap.get(FORMAT_HOUR23)>=0){parseDate.put(FORMAT_HOUR23, dateString.substring(indexMap.get(FORMAT_HOUR23),indexMap.get(FORMAT_HOUR23)+FORMAT_HOUR23.length()));}
		if(indexMap.get(FORMAT_MINUTE)>=0){parseDate.put(FORMAT_MINUTE, dateString.substring(indexMap.get(FORMAT_MINUTE),indexMap.get(FORMAT_MINUTE)+FORMAT_MINUTE.length()));}
		if(indexMap.get(FORMAT_SECOND)>=0){parseDate.put(FORMAT_SECOND, dateString.substring(indexMap.get(FORMAT_SECOND),indexMap.get(FORMAT_SECOND)+FORMAT_SECOND.length()));}
		if(indexMap.get(FORMAT_MILI_SECOND)>=0){parseDate.put(FORMAT_MILI_SECOND, dateString.substring(indexMap.get(FORMAT_MILI_SECOND),indexMap.get(FORMAT_MILI_SECOND)+FORMAT_MILI_SECOND.length()));}
		
		return parseDate;
	}
	
	public static final String format(String inFormat,String outFormat, String value){
		java.util.Date date	= new DateFormat(inFormat).format(value);
		java.text.SimpleDateFormat sf	= new java.text.SimpleDateFormat(outFormat);
		return sf.format(date);
	}
	
	public static void main(String[] args){
	}
}
