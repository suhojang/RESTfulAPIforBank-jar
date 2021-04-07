/**
* Class  NumericUtil.java
* @program 프로그램명(또는 업무명)
* @description 숫자관련 utility class
* 
* @author 장순복
* @update :Feb 23, 2007
* @package com.kwic.util; 
* @see
* 
* @DBTable
*/

package com.kwic.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumericUtil {
	 
	/**
     * 지정된 위치에서 반올림 해주는 메서드
     *
     * @param dValue
     * @param index
     *
     * @return boolean
     */
	public static double round(double dValue, int index){
		try{
			return new java.math.BigDecimal(dValue).setScale(index, java.math.BigDecimal.ROUND_HALF_EVEN).doubleValue();
		}catch(Exception e){ return dValue; }
	}
	
	/**
     * 지정된 위치에서 올림 해주는 메서드
     *
     * @param dValue
     * @param index
     *
     * @return boolean
     */
	public static double roundUp(double dValue, int index){
		try{
			return new java.math.BigDecimal(dValue).setScale(index, java.math.BigDecimal.ROUND_UP).doubleValue();
		}catch(Exception e){ return dValue; }
	}
	
	/**
     * 지정된 위치에서 버림 해주는 메서드
     *
     * @param dValue
     * @param index
     *
     * @return boolean
     */
	public static double roundDown(double dValue, int index){
		try{
			return new java.math.BigDecimal(dValue).setScale(index, java.math.BigDecimal.ROUND_DOWN).doubleValue();
		}catch(Exception e){ return dValue; }
	}
	
	  /**
     * String을 Int형으로 변환
     * 
     * @param  str	문자열
     * @return int	리턴값
     */
    public static int stringToInt(String str) {
        if (str == null || str.equals("") || str.trim().length() < 1) {
            return (int) 0;
        } else {
            return Integer.parseInt(str.trim());
        }
    }

    /**
     * String을 long형으로 변환
     * 
     * @param 	str		문자열
     * @return 	long 	리턴값
     */
    public static long stringToLong(String str) {
        if (str == null || str.equals("") || str.trim().length() < 1) {
            return (long) 0;
        } else {
            return Long.parseLong(str.trim());
        }
    }
    
    /**
     * String을  double형으로 변환
     * 
     * @param 	str		문자열
     * @return 	double 	리턴값
     */
    public static double stringToDouble(String str) {
        if (str == null || str.equals("") || str.trim().length() < 1) {
            return (double) 0;
        } else {
            return Double.parseDouble(str.trim());
        }
    }
    
    /**
     * String을  float형으로 변환
     * 
     * @param 	str		문자열
     * @return 	float 	리턴값
     */
    public static float stringToFloat(String str) {
        if (str == null || str.equals("") || str.trim().length() < 1) {
            return (float) 0;
        } else {
            return Float.parseFloat(str.trim());
        }
    }
    
    /**
     * int 값을 숫자를 포멧에 맞게 리턴한다.
     * @param	value			int값
     * @param 	formatString	포맷 스트링
     * @return 	String			포멧에 맛게 변환된 문자열
     */
    public static String int2FormatedString(int value, String formatString) {
    	
        DecimalFormat format = new DecimalFormat(formatString);
        
        return format.format(value);

    }

    /**
     * long 값을 숫자를 포멧에 맞게 리턴한다.
     * @param	value			long 값
     * @param 	formatString	포맷 스트링
     * @return 	String			포멧에 맛게 변환된 문자열
     */
    public static String longToMoneyFormat(long value, String formatString) {
    	
        DecimalFormat moneyFormat = new DecimalFormat(formatString);
        
        return moneyFormat.format(value);
    }
    
    /**
     * double 값을 숫자를 포멧에 맞게 리턴한다.
     * @param	value			double 값
     * @param 	formatString	포맷 스트링
     * @return 	String			포멧에 맛게 변환된 문자열
     */
    public static String doubleToMoneyFormat(double value, String formatString) {
    	
        DecimalFormat moneyFormat = new DecimalFormat(formatString);
        
        return moneyFormat.format(value);
    }
    
    /**
     * String value 를 BigDecimal로 변경한다.
     *
     * @param 	value
     * @return	BigDecimal
     * @throws 	Exception
     */
    public static BigDecimal string2BigDecimal(String value) throws Exception {
        BigDecimal toValue = null;

        if (value != null && !"".equals(value)) {
            toValue = new BigDecimal(value);
        } else {
            toValue = new BigDecimal("0");
        }
        return toValue;
    }

    /**
     * int value 를 BigDecimal로 변경한다.
     *
     * @param 	value
     * @return	BigDecimal
     * @throws 	Exception
     */
    public static BigDecimal string2BigDecimal(int value) throws Exception {
        BigDecimal toValue = null;
        toValue = new BigDecimal(value);
        return toValue;
    }

    /**
     * count 만큼 루프를 돌면서 String[] =>BigDecimal[]로 변경한다.
     *
     * @param 	values String[] 값
     * @param 	count  전체 배열 size
     * @return 	BigDecimal[]
     * @throws 	Exception
     */
    public static BigDecimal[] stringArray2BigDecimalArray(String[] values, int count) throws Exception {
        BigDecimal[] decimalArray = new BigDecimal[count];

        for (int i = 0; i < count; i++) {
            if (values != null && i < values.length) {
                decimalArray[i] = string2BigDecimal(values[i]);
            } else {
                decimalArray[i] = string2BigDecimal(null);
            }
        }
        return decimalArray;
    }

    /**
     * count 만큼 루프를 돌면서 BigDecimal[] =>BigDecimal[]로 변경한다.
     *
     * @param 	values BigDecimal[] 값
     * @param 	count  전체 배열 size
     * @return	BigDecimal[]
     * @throws Exception
     */
    public static BigDecimal[] copyBigDecimalArray(BigDecimal[] values, int count) throws Exception {
        BigDecimal[] decimalArray = new BigDecimal[count];

        for (int i = 0; i < count; i++) {
            if (values != null && i < values.length) {
                decimalArray[i] = values[i];
            } else {
                decimalArray[i] = string2BigDecimal(null);
            }
        }
        return decimalArray;
    }
	
    /**
     * 짝수(true)홀수(false) 인지를 판단
     *
     * @param no
     *
     * @return boolean
     */
    public boolean isEven(int no)
    {
        
        if (((no/2)*2)==no) return true;  // 짝수
        
        else return false;  // 홀수
    }
}



