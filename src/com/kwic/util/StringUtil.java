package com.kwic.util;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
* Class  StringUtil.java
* @program String 관련 utility class
* @description String 관련 utility class
* 
* @author 기웅정보통신
* @update :Feb 1, 2007
* @package com.kwic.util; 
* @see
* 
* @DBTable
*/
public class StringUtil {
	 
	/**
     * 영문 문자열을 한글 문자열로 변환 한다.
     * 
     * @param  str	   영문 문자열
     * @return String  변환 한글 문자열
     */
    public static String ascToksc(String str) {
        try {
            if (str != null) {
                str = new String(str.getBytes("8859_1"), "EUC-KR");
            }

        } catch (UnsupportedEncodingException e) {
        }
        return str;
    }

    /**
     * 한글문자열을 영문 문자열로 변환한다.
     * 
     * @param 	str	  	한글 문자열
     * @return 	String  변환 영문 문자열
     */
    public static String kscToasc(String str) {
        try {
            if (str != null) {
                str = new String(str.getBytes("EUC-KR"), "8859_1");
            }
        } catch (UnsupportedEncodingException e) {
        }
        return str;
    }
    
    /**
     * StringTokenizer 을 이용하여 String[] 형으로 리턴해주는 메서드
     * 
     * @param str		: 문자열
     * @param delimiter : 구분자
     * @return String[]
     */
    public static String[] tockenString(String str, String delimiter){
        
        StringTokenizer st = new StringTokenizer(str, delimiter);
        
        String[] arrStr = new String[st.countTokens()];
        
        int cnt = 0;
        
        while (st.hasMoreTokens()) 
        {           
            arrStr[cnt++] = st.nextToken();
        }
        
        return arrStr;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static String[] tocken(String str, String delimiter){
    	if(str==null)
    		return new String[0];
    	if(delimiter==null || delimiter.length()==0 || str.indexOf(delimiter)<0)
    		return new String[]{str};

    	int len	= delimiter.length();
    	List list	= new ArrayList();
    	
    	int stIdx	= 0;
    	while(str.indexOf(delimiter,stIdx)>=0){
    		if(stIdx==0 && str.startsWith(delimiter))
    			list.add("");
    		else if(stIdx==0)
    			list.add(str.substring(stIdx,str.indexOf(delimiter,stIdx)));
    		else
    			list.add(str.substring(stIdx,str.indexOf(delimiter,stIdx)));
    		
    		stIdx	= str.indexOf(delimiter,stIdx)+len;
    	}
    	
    	if(str.endsWith(delimiter)){
    		list.add("");
    	}else{
    		list.add(str.substring(stIdx));
    	}
    	
    	String[] arr	= new String[list.size()];
    	for(int i=0;i<arr.length;i++){
    		arr[i]	= (String)list.get(i);
    	}
    	return arr;
    }
    
    
    /**
     * 문자열 내의 특정한 문자열을 모두 지정한 다른 문자열로 바꾼다.
     * 원본 String 이 null 일 경우에는 null 을 반환한다.
     * StringBuffer 를 이용하였으므로 이전의 String 을 이용한 것 보다
     * 월등히 속도가 빠르다. (약 50 ~ 60 배)
     *
     * 사용 예: <BR>
     *
     *   1. 게시판에서 HTML 태그가 안 먹히게 할려면
     *
     *      String str = "<TD>HTML Tag Free Test</TD>";
     *      str = replace(str, "&", "&amp;");
     *      str = replace(str, "<", "&lt;");
     *
     *   2. ' 가 포한된 글을 DB 에 넣을려면
     *
     *      String str2 = "I don't know.";
     *      str2 = replace(str2, "'", "''");
     *
     * @param   String src       원본 String
     * @param   String oldstr    원본 String 내의 바꾸기 전 문자열
     * @param   String newstr    바꾼 후 문자열
     * @return  String           치환이 끝난 문자열
     *
     */
    public static String replace(String src, String oldstr, String newstr) {
        if (src == null) {
            return src;
        }

        StringBuffer dest = new StringBuffer("");
        int len = oldstr.length();
        int srclen = src.length();
        int pos = 0;
        int oldpos = 0;

        while ( (pos = src.indexOf(oldstr, oldpos)) >= 0) {
            dest.append(src.substring(oldpos, pos));
            dest.append(newstr);
            oldpos = pos + len;
        }

        if (oldpos < srclen) {
            dest.append(src.substring(oldpos, srclen));

        }
        return dest.toString();
    }
    
    /**
     * paramter가 Null일 경우 문자열 ""을 리턴
     *
     * @param 	str		문자열
     * @return 	String	리턴값
     */
    public static String replaceNull(String str) {
        if (str == null) {
            return "";
        } else {
            return str.trim();
        }
    }

    /**
     * parameter 가 Null일 경우 문자열 ""을 리턴하고, &nbsp; 문자열을 &amp;nbsp;로 수정
     *
     * @param 	str		문자열
     * @return 	String	리턴값
     */
    public static String replaceNullForNaeyong(String str) {
        return (replaceNull(str)).replaceAll("&nbsp;", "&amp;nbsp;");
    }

    /**
     * count 만큼 루프를 돌면서 String[] =>String[]로 변경한다.
     *
     * @param 	values String[] 값
     * @param 	count  전체 배열 size
     * @return	String[]
     * @throws 	Exception
     */
    public static String[] stringArray2NotNullStringArray(String[] values, int count) throws Exception {
        String[] stringArray = new String[count];
        for (int i = 0; i < count; i++) {
            if (values != null && i < values.length) {
                stringArray[i] = replaceNull(values[i]);
            } else {
                stringArray[i] = new String();
            }
        }
        return stringArray;
    }
    
	/**
     * 비밀번호를 특정부분만 보여주고 그 외 부분은 지정된 문자열로 대체한다.
     *
     * @param input
     * @param displayIndex
     * @param delimeter
     * @return String
     */
    public static String displayPassword(String input,
                                         int displayIndex,
                                         String delimeter) {
        StringBuffer output = new StringBuffer();
        for (int i = 0, l = input.length(); i < l; i++) {
            if (i < displayIndex) {
                output.append(input.charAt(i));
            } else {
                output.append(delimeter);
            }
        }
        return output.toString();
    }

    

    /**
     * 파일크기에 따라 Byte, KB, MB 단위를 붙인 파일 사이즈 문자열을 리턴한다.
     *
     * @param 	fileSize	파일 크기
     * @return 	String	파일 사이즈 문자열을 리턴
     */
    public static String getFileSizeWithUnit(long fileSize) {
        String unit = null;
        if (fileSize > 1000000) {
            unit = "MB";
            fileSize = Math.round(fileSize / 1000000);
        } else if (fileSize > 1000) {
            unit = "KB";
            fileSize = Math.round(fileSize / 1000);
        } else {
            unit = "Byte";
        }
        return ("" + fileSize + unit);
    }
    
    /**
     * str의 사이즈가 size보다 작은 경우 공백으로 채워서 리턴한다.
     * 
     * @param	str
     * @param	size
     * @return 	String	
     */
    public static String getSpaceBaseString(String str, int size) {
        StringBuffer temp = new StringBuffer();
        if (str == null) {
            return "";
        }
        temp.append(str.trim());

        int strSize = str.getBytes().length;
        if (strSize < size) {
            for (int i = (size - strSize); i > 0; i --) {
                temp.append(" ");
            }
        }
        return temp.toString();
    }
    
    /**
     * str의 사이즈가 size보다 작은 경우 "0"으로 채워서 리턴한다.
     * 
     * @param	str
     * @param	size
     * @return 	String	
     */
    public static String getZeroBaseString(String str, int size) {
        StringBuffer temp = new StringBuffer();
        if (str == null) {
            return "";
        }
        temp.append(str.trim());

        int strSize = str.getBytes().length;
        if (strSize < size) {
            for (int i = (size - strSize); i > 0; i --) {
                temp.append("0");
            }
        }
        return temp.toString();
    }
    
    /**
     * 스트링의 맨 앞문자를 대문자로 바꿈
     *
     * @param str
     * @return String
     */
	public static String toStrHeadUpperCase(String str){
	    
		if(str == null)	return null;
		
		char[] strBuf = str.toCharArray();
		
		strBuf[0] = Character.toUpperCase(strBuf[0]);
		
		return new String(strBuf);
	}
	
	 /**
     * 스트링의 맨 앞문자를 소문자로 바꿈
     *
     * @param str
     * @return String
     */
	public static String toStrHeadLowerCase(String str){
		if(str == null)	return null;
		
		char[] strBuf = str.toCharArray();
		
		strBuf[0] = Character.toLowerCase(strBuf[0]);
		
		return new String(strBuf);
	}
	
	/**
     * 한글을 바이트 단위로 자르기
     * truncateNicely("문자열",2,"...")=> 리턴결과 문...
     * 
     * @param str
     * @param len
     * @param tail
     * @return String
     */
	public static String truncateNicely(String str, int len, String tail) {
		if (str == null)		return null;

		int srcLen = realLength(str);

		if (srcLen < len) return str;

		String tmpTail = tail;

		if (tail == null)tmpTail = "";

		int tailLen = realLength(tmpTail);

		if (tailLen > len)return "";

		char a;
		int i = 0;
		int realLen = 0;

		for (i = 0; i < len - tailLen && realLen < len - tailLen; i++) {
			a = str.charAt(i);
				if ((a & 0xFF00) == 0)realLen += 1;
				else	realLen += 2;
		}

		while (realLength(str.substring(0, i)) > len - tailLen) {
			i--;
		}

		return str.substring(0, i) + tmpTail;
	}
	
	/**
     * 문자열 길이
     *
     * @param str
     * @return int
     */
	public static int realLength(String str) {
		int len	= 0;
		try{len	= str.getBytes("EUC-KR").length;}catch(Exception ex){}
		return len;
	}
	
	/**
     * str를  format에 맞추어 출력
     * getFormatedStr("0164699120","???-???-????") --->> 016-469-9120로 출력
     * 
     * @param str 
     * @param format 
     * @return String
     */
    public String getFormatedStr(String str,String format)
    {
        String rtn="";
        int start=0,len=0;
        int strCount,formatCount;

        strCount = str.length();
        formatCount = format.length();

        if (str.equals("")) return rtn;
        
        if (str.equals("&nbsp;")) return "&nbsp;";
             
        //str에서 -를 제거한다.
        for (int i=0; i<strCount; ++i) 
        {
            if (!str.substring(i,i+1).equals("-"))   rtn = rtn + str.substring(i,i+1);
        }

        str = rtn;
        strCount = str.length();

        //포멧에서 ?의  count
        for (int j=0; j<formatCount; ++j) 
        {
            if (format.substring(j,j+1).equals("?")) ++len;
        }
   
        //str의 길이가 len보다 작으면 앞에 0를 붙인다.
        if (strCount<len) 
        {
            for (int i=0; i<(len-strCount); ++i) 
            {
                str = '0' + str;
            }
            strCount = len;
        }

        rtn = "";
         
        for (int i=0; i<strCount; ++i) 
        {
            
            for (int j=start; j<formatCount; ++j) 
            {
                
                if (format.substring(j,j+1).equals("?")) 
                {
                    rtn = rtn + str.substring(i,i+1);
                    start = start + 1;
                    break;
                }
                else {
                    rtn = rtn + format.substring(j,j+1);
                    start = start + 1;
                }
                
            }
            
        }
        
        return rtn+format.substring(start);
        
    }


	
	public static String substring(String str,int size){
		if(str==null)
			return "";
		if(str.length()<=size){
			return str;
		}
		return str.substring(0,size-1)+"..";
	}
	
	
	/**
	 * 1000단위 콤마 처리용.
	 * @param moneyString
	 * @return
	 */
	public static String makeMoneyType(String moneyString)
	{
	    String ls_Part1 = "";
	    String ls_Part2 = "";
		if ( moneyString == null || moneyString.length() == 0 ) return "0";
		
		// 이상한 문자가 들어온경우 원래 값 반환. 
		// ex) 95억 -_-);
		try {
			Double.parseDouble(moneyString);
		} catch(NumberFormatException nfe) {
			return moneyString;
		}
		
		if ( moneyString.indexOf(".") != -1) {
			ls_Part1 = moneyString.substring(0,moneyString.indexOf("."));
			ls_Part2 = moneyString.substring(moneyString.indexOf("."));
		} else {
			ls_Part1 = moneyString;
		}

		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();

		dfs.setGroupingSeparator(',');
		df.setGroupingSize(3);
		df.setDecimalFormatSymbols(dfs);

		return (df.format(Long.parseLong(ls_Part1))).toString() + ls_Part2;
	}    
    
	public static final String _LINE		= System.getProperty("line.separator");
	public static final int PAGE_PER_BLOCK	= 10;	//블럭당 페이지 갯수
	public static final int ROW_PER_PAGE	= 50;	//페이지당 row 수
	public static final String[] imgNames	= new String[]{
												"/kbss/img/i_firstpage.gif"
												,"/kbss/img/i_beforepage.gif"
												,"/kbss/img/i_nextpage.gif"
												,"/kbss/img/i_lastpage.gif"
											};
	/**
	 * 페이징 문자열 생성
	 * arg	PAGE_PER_BLOCK	:	블럭당 보여줄 페이지 갯수
	 * arg	ROW_PER_PAGE	:	페이지당 보여줄 row갯수
	 * arg 	tRowCnt			:	총 row수
	 * arg	CURRENT_PAGE	:	현재페이지번호
	 * */
	public static String getPageTag( int PAGE_PER_BLOCK, int ROW_PER_PAGE, int tRowCnt, int CURRENT_PAGE){
		StringBuffer sb	= new StringBuffer();
		try{
			//페이지당 20개, 블럭당 5페이지
			int t_page			= tRowCnt/ROW_PER_PAGE; //전체 페이지 숫자 (1,2,...)
			if(tRowCnt%ROW_PER_PAGE!=0)		t_page++;

			int t_block			= t_page/PAGE_PER_BLOCK; //전체 블럭 수 (1,2,...)
			if(t_page%PAGE_PER_BLOCK!=0)	t_block++;

			int n_block			= CURRENT_PAGE/PAGE_PER_BLOCK;	//현재 블럭
			if(CURRENT_PAGE%PAGE_PER_BLOCK!=0)	n_block++;

			int s_page			= (n_block-1)*PAGE_PER_BLOCK+1;	//현재블럭의 시작 페이지
			int e_page			= n_block*PAGE_PER_BLOCK;		//현재블럭의 끝 페이지

			sb.append("<table border='0' align='center' cellpadding='0' cellspacing='0'>"+_LINE);
			sb.append("<tr height='5'><td></td></tr><tr>"+_LINE);
			//이전블럭
			if(n_block>1){
				sb.append("<td width='20'><img alt='이전블럭' src='"+imgNames[0]+"' onfocus='this.blur();' style='cursor:pointer;' onclick='javascript:gotoPage("+((n_block-1)*PAGE_PER_BLOCK)+");'></td>"+_LINE);
			}else{
				sb.append("<td width='20'><img alt='이전블럭' src='"+imgNames[0]+"'></td>"+_LINE);
			}
			//이전페이지
			if(CURRENT_PAGE>1){
				sb.append("<td width='20'><img alt='이전페이지' src='"+imgNames[1]+"'  onfocus='this.blur();' style='cursor:pointer;' onclick='javascript:gotoPage("+(CURRENT_PAGE-1)+");'></td>"+_LINE);
			}else{
				sb.append("<td width='20'><img alt='이전페이지' src='"+imgNames[1]+"'></td>"+_LINE);
			}

			sb.append("<td class='number'>&nbsp;"+_LINE);
			for(int j=s_page;j<=e_page;j++){
				if(j>t_page){
					break;
				}
				if(j==CURRENT_PAGE){
					sb.append("<font color='#FB8D18'><b>"+j+"</b></font>&nbsp;"+_LINE);
				}else{
					sb.append("<span onclick='javascript:gotoPage("+j+");' style='cursor:pointer'><font color='#6695C9'><b>"+j+"</b></font></span>&nbsp;"+_LINE);
				}
			}
			sb.append("&nbsp;</td>"+_LINE);

			//다음페이지
			if(CURRENT_PAGE<t_page){
				sb.append("<td width='20'><img alt='다음페이지' src='"+imgNames[2]+"' onfocus='this.blur();' style='cursor:pointer;' onclick='javascript:gotoPage("+(CURRENT_PAGE+1)+");'></td>"+_LINE);
			}else{
				sb.append("<td width='20'><img alt='다음페이지' src='"+imgNames[2]+"'></td>"+_LINE);
			}

			//다음블럭
			if(n_block<t_block){
				sb.append("<td width='20'><img alt='다음블럭' src='"+imgNames[3]+"' onfocus='this.blur();' style='cursor:pointer;' onclick='javascript:gotoPage("+((n_block*PAGE_PER_BLOCK)+1)+");'></td>"+_LINE);
			}else{
				sb.append("<td width='20'><img alt='다음블럭' src='"+imgNames[3]+"'></td>"+_LINE);
			}
			sb.append("</tr>"+_LINE);
			sb.append("</table>"+_LINE);
		}catch(Exception ex){
			ex.printStackTrace();
			sb.setLength(0);
			sb.append(ex.getMessage());
		}
		return sb.toString();
	}

	/**
	 * 일반 문자열을 html형식으로 변환
	 * */
	public static String htmlFormat(String s){
		s	= StringUtil.replace(s,"&","&amp;");
		s	= StringUtil.replace(s,"\"","&quot;");
		s	= StringUtil.replace(s,"<","&lt;");
		s	= StringUtil.replace(s,">","&gt;");
		s	= StringUtil.replace(s,_LINE,"<br>");
		return s;
	}
	/**
	 * <p>	년,월,일 처리	(yyyy년mm월dd일) <p>
	 *  20080101 형식을 2008년01월01일 로 바꿔준다. 
	 * 
	 *@return java.lang.String
	 *@throws java.lang.Exception  
	 * */
	public static String strToDate(String str){
		
		int strCnt = str.length();
		if(strCnt != 8){
			return str;
		}else{
		
	        String yyyy   = str.substring(0,4);
	        String mm     = str.substring(4,6);
	        String dd     = str.substring(6,8);
	        String date = yyyy + " 년 " + mm + " 월 " + dd + " 일 ";
			return date;
		}
	}

	/**
	 * <p>	년,월 처리	(yyyy년mm월) <p>
	 *  20080101 형식을 2008년01월 로 바꿔준다. 
	 * 
	 *@return java.lang.String
	 *@throws java.lang.Exception  
	 * */
	public static String strToDate2(String str){
		
		int strCnt = str.length();
		if(strCnt != 8){
			return str;
		}else{
		
	        String yyyy   = str.substring(0,4);
	        String mm     = str.substring(4,6);
	        
	        String date = yyyy + " 년 " + mm + " 월 " ;
			return date;
		}
	}	
	/**
	* 특정문자를 주어진 횟수만큼 반복한 문자열 반환<p>
	*@return java.lang.String
	*@throws java.lang.Exception
	*/
	public static String append(String str, int cnt) throws Exception{
		StringBuffer sb	= new StringBuffer();
		for(int i=0;i<cnt-1;i++){
			sb.append(str);
		}
		return sb.toString();
	}

    public static String makeCalendar(int startValue, int endValue, String selectedValue){
    	selectedValue	= selectedValue==null?"":selectedValue;
        String value = "";
        StringBuffer sb	= new StringBuffer();
    	for (int i=startValue; i<=endValue; i++){
    		if(i<=9){value="0"+i;}else{value=""+i;}
    		sb.append("<option value=\"")
    		.append(value)
    		.append("\" "+(selectedValue.equals(value)?"selected":"")+">")
    		.append(value)
    		.append("</option>")
    		.append(System.getProperty("line.separator"));
    	}
    	return sb.toString();
    }
	/**
	 * 구분자에 의해 분리된 string을 분리하여 string array 로 만든다.
	 * 내부적으로  Trim 하지 않는다.
	 * @param str 구분자에 의해 분리된 string
	 * @param delim 구분자
	 * @return 분리된 string array
	 * @exception Execption error occurs
	 */
	public static String[] split(String str,String spr){
        String[] returnVal = null;
        int cnt = 1;

        int index = str.indexOf(spr);
        int index0 = 0;
        while (index >= 0) {
            cnt++;
            index = str.indexOf(spr, index + 1);
        }
        returnVal = new String[cnt];
        cnt = 0;
        index = str.indexOf(spr);
        while (index >= 0) {
            returnVal[cnt] = str.substring(index0, index);
            index0 = index + 1;
            index = str.indexOf(spr, index + 1);
            cnt++;
        }
        returnVal[cnt] = str.substring(index0);
        
        return returnVal;
    }
	/**<pre>
	 * 주어진 문자열이 최대크기가 될때까지 특정문자를 첨부하여 반환한다.
	 * </pre>
	 * @param str String 
	 * @param maxSize int 
	 * @param addChar String 
	 * @param boolean appendLeft 
	 * */
	public static final String addChar(short num, int maxSize){
		return addChar(String.valueOf(num), maxSize,"0",true);
	}
	public static final String addChar(int num, int maxSize){
		return addChar(String.valueOf(num), maxSize,"0",true);
	}
	public static final String addChar(long num, int maxSize){
		return addChar(String.valueOf(num), maxSize,"0",true);
	}
	public static final String addChar(String str, int maxSize){
		return addChar(str, maxSize," ",false);
	}
	public static final String addChar(String str,int maxSize,String addChar,boolean appendLeft){
		if(maxSize<1 || addChar==null || addChar.length()<1)
			return str;
		
		if(str==null)
			str	= "";
		
		if(str.getBytes().length>maxSize){
			byte[] bytes	= new byte[maxSize];
			System.arraycopy(str.getBytes(), 0, bytes, 0, bytes.length);
			return new String(bytes);
		}else if(str.getBytes().length==maxSize){
			return str;
		}

		StringBuffer sb	= new StringBuffer();
		
		if(!appendLeft){
			sb.append(str);
		
			while(sb.toString().getBytes().length<maxSize){
				sb.append(addChar);
			}
		}else{
			while(sb.toString().getBytes().length<maxSize-str.getBytes().length){
				sb.append(addChar);
			}
			sb.append(str);
		}

		if(sb.toString().getBytes().length>maxSize){
			byte[] bytes	= new byte[maxSize];
			System.arraycopy(sb.toString().getBytes(), 0, bytes, 0, bytes.length);
			return new String(bytes);
		}
				
		return sb.toString();
	}
	
	public static final String convertTelNo(String telNo){
		telNo	= replace(telNo,"-","");
		
		if(telNo==null)
			return "";
		
		if(telNo.length()==7){
			return telNo.substring(0,3)+"-"+telNo.substring(3);
		}else if(telNo.length()==8){
			return telNo.substring(0,4)+"-"+telNo.substring(4);
		}else if(telNo.length()<7){
			return telNo;
		}		
		
		String tel1	= null;
		String tel2	= null;
		String tel3	= null;
		
		tel3	= telNo.substring(telNo.length()-4);
		
		if(telNo.length()==9){
			tel1	= telNo.substring(0,2);
			tel2	= telNo.substring(2,5);
		}else if(telNo.length()==10 && telNo.startsWith("02")){
			tel1	= telNo.substring(0,2);
			tel2	= telNo.substring(2,6);
		}else if(telNo.length()==10){
			tel1	= telNo.substring(0,3);
			tel2	= telNo.substring(3,6);
		}else if(telNo.length()==11){
			tel1	= telNo.substring(0,3);
			tel2	= telNo.substring(3,7);
		}else if(telNo.length()==12){
			tel1	= telNo.substring(0,4);
			tel2	= telNo.substring(4,8);
		}
		return tel1+"-"+tel2+"-"+tel3;
	}
	
	/**<pre>
	 * 문자열을 구성하는 각각의 문자들이 숫자형인지 판별한다.
	 * </pre>
	 * @param num String
	 * @return boolean
	 * */
	public static boolean isNumber(String num){
		if(num==null || num.trim().length()==0)
			return false;

		num	= num.trim();
		char c;
		for(int i=0;i<num.length();i++){
			c	= num.charAt(i);
			if(c<'0' || c>'9')
				return false;
		}
		return true;
	}
	
	public static List<String> splitSmsString(String msg) throws UnsupportedEncodingException{
		return splitString(msg,80);
	}
	public static List<String> splitString(String str,int size) throws UnsupportedEncodingException{
		StringBuffer sb	= new StringBuffer();
		List<String> list	= new ArrayList<String>();
		if(str.getBytes("EUC-KR").length<=size){
			list.add(str);
			return list;
		}
		
		for(int i=0;i<str.length();i++){
			if(sb.toString().getBytes("EUC-KR").length+new String(new char[]{str.charAt(i)}).getBytes("EUC-KR").length>80){
				list.add(sb.toString());
				sb.setLength(0);
			}
			sb.append(new String(new char[]{str.charAt(i)}));
		}
		if(sb.length()!=0)
			list.add(sb.toString());

		return list;
	} 

	//bytes To Hex String
	public static String bytesToHexString(byte[] bytes) {
		final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for ( int j = 0; j < bytes.length; j++ ) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	public static String currentTimeToKey(){
		char[] arr1	= new char[]{'A','B','C','D','E','F','G','H','I','J'};
		char[] arr2	= String.valueOf(Calendar.getInstance().getTimeInMillis()).toCharArray();
		
		StringBuffer sb	= new StringBuffer();
		for(int i=3;i<arr2.length;i++)
			sb.append(arr1[Integer.parseInt(String.valueOf(arr2[i]))]);
		return sb.toString();
	}
	
	public static int CONTAIN_TYPE_NUMBER	= 1;
	public static int CONTAIN_TYPE_ENGLISH	= 2;
	public static int CONTAIN_TYPE_SIGNAL	= 3;
	
	public static boolean isContain(String val,int type){
		//숫자
		if(type==CONTAIN_TYPE_NUMBER){
			for(int i=48;i<=57;i++)
				if(val.indexOf(String.valueOf((char)i))>=0)
					return true;
			return false;
		}else if(type==CONTAIN_TYPE_ENGLISH){
			//영문대문자
			for(int i=65;i<=90;i++)
				if(val.indexOf(String.valueOf((char)i))>=0)
					return true;
			//영문소문자
			for(int i=97;i<=122;i++)
				if(val.indexOf(String.valueOf((char)i))>=0)
					return true;
			return false;
		}else if(type==CONTAIN_TYPE_SIGNAL){
			//특수문자
			for(int i=1;i<=127;i++){
				if(i==48){i=58;}
				if(i==65){i=91;}
				if(i==97){i=123;}
				if(val.indexOf(String.valueOf((char)i))>=0)
					return true;
			}
			return false;
		}
		return false;
	}
	/**
	 * 아이디 유효성 검증
	 * 영문,숫자,_,- 만사용가능
	 * */
	public static boolean isValidID(String val){
		int c	= 0;
		for(int i=0;i<val.length();i++){
			c	= (int)val.charAt(i);
			if(c>=48 && c<=57)
				continue;
			else if(c>=65 && c<=90)
				continue;
			else if(c>=97 && c<=122)
				continue;
			else if(c==95)
				continue;
			else if(c==45)
				continue;
			
			return false;
		}
		return true;
	}
	
    /**
     * <p>Checks if the String contains only unicode digits.
     * A decimal point is not a unicode digit and returns false.</p>
     *
     * <p><code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.</p>
     *
     * <pre>
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = true
     * StringUtils.isNumeric("  ")   = false
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("12 3") = false
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains digits, and is non-null
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
    
    //주민번호 체크(외국인등록번호 통합)
    public static boolean isValidateSsn( String ssn ) {
    	ssn	= replace(ssn,"-","");
    	boolean isKorean	= true;
    	int	check	= 0;
    	if( ssn == null || ssn.length() != 13 )
    		return false;
    	if( Character.getNumericValue( ssn.charAt( 6 ) ) > 4 && Character.getNumericValue( ssn.charAt( 6 ) ) < 9 )
    		isKorean = false;

    	for( int i = 0 ; i < 12 ; i++ ) {
    		if( isKorean )
    			check	+= ( ( i % 8 + 2 ) * Character.getNumericValue( ssn.charAt( i ) ) );
    		else
    			check	+= ( ( 9 - i % 8 ) * Character.getNumericValue( ssn.charAt( i ) ) );
    	}
    	if( isKorean ) {
    		check	= 11 - ( check % 11 );
    		check	%= 10;
    	} else {
    		int remainder	= check % 11;
    		if ( remainder == 0 )
    			check = 1;
    		else if ( remainder==10 )
    			check = 0;
    		else
    			check = remainder;

    		int check2	= check + 2;
    		if ( check2 > 9 )
    			check = check2 - 10;
    		else
    			check = check2;
    	}

    	if( check == Character.getNumericValue( ssn.charAt( 12 ) ) )
    		return true;
    	return false;
    }
    
    //사업자번호 체크
    public static boolean isValidBzNo(String bzno){
    	bzno	= replace(bzno,"-","");
    	if(bzno.length()!=10)
    		return false;
    	
        // 정규 표현식을 이용한 유효성 검사하기
        Pattern p	= Pattern.compile("^\\d{10}$");
        Matcher m	= p.matcher(bzno);
        if(!m.matches())
        	return false;
        
    	return true;
        /*
        // 여기까지 내려오면 숫자로만 이루어진 10자리이다.
        int sum	= 0;
        // 1. 각각의 자리에 1 3 7 1 3 7 1 3 5 를 곱한 합을 구한다. 
        String checkNo	= "137137135";
        for(int i=0;i<checkNo.length();i++)
            sum += (bzno.charAt(i)-'0') * (checkNo.charAt(i)-'0');
        // 2. 마지막에서 두번째 숫자에 5를 곱하고 10으로 나누어 나온 몫을 더한다.
        sum += ((bzno.charAt(8)-'0') * 5)/10;

        // 3. 매직키인 10로 나누어 나머지만 취한다. 
        sum %= 10;

        // 4. 매직키인 10에서 나머지를 빼면
        sum = 10 - sum;

        if(sum==(bzno.charAt(9)-'0'))
        	return true;
        return false;
        */
    }
    //사업자번호로 법인여부 확인
    public static boolean isCorpBzNo(String bzno){
    	bzno	= replace(bzno,"-","");
    	//기웅번호 임시로 가입가능하게 처리
    	if("2148159394".equals(bzno))
    		return false;
    	if(bzno.length()!=10)
    		return false;
    	int no	= Integer.parseInt(bzno.substring(3,5));
    	if(no>=81 && no<=87)
    		return true;
    	return false;
    }
    //휴대폰 체크
    public static boolean isValidMobile(String hp){
    	hp	= replace(hp,"-","");
    	return hp.matches("(01[016789])(\\d{3,4})(\\d{4})");
    }
	//이메일 검증
    public static boolean isValidEmail(String email) {
    	Pattern p	= Pattern.compile("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$");
    	Matcher m	= p.matcher(email);
    	if(m.matches())
    		return true;
    	return false;
    }
    //마스킹 처리
	public static String mask(String val){
		StringBuffer sb	= new StringBuffer();
		for(int i=0;i<val.length();i++){
			if(i%2==0)
				sb.append(val.charAt(i));
			else
				sb.append("*");
		}
		return sb.toString();
	} 
  	
	public static void main(String[] args){
	}

}



