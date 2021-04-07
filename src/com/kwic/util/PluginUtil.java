package com.kwic.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <pre>
 * Title		: CrsUtil
 * Description	: Crs Utility
 * Date			: 2012.04.20
 * Copyright	: Copyright	(c)	2012
 * Company		: KWIC.
 * 
 * 
 *    수정일                  수정자                      수정내용
 * ---------------------------------------
 * 
 * </pre>
 * 
 * @author 장정훈
 * @version	
 * @since 
 */
public class PluginUtil {

	public static final String _REPLACE_VARIABLE_CHAR	= "$";
	public static final String _REPLACE_PARAM_CHAR		= "#";

	public static final String _TXT_EXTENSION			= ".txt";
	public static final String _TXT_EXTENSION_2			= ".TXT";
//	public static final String _TIF_EXTENSION			= ".tif";
//	public static final String _TIF_EXTENSION_2			= ".TIF";
	public static final String _BAK_EXTENSION			= ".bak";
	public static final String _BAK_EXTENSION_2			= ".BAK";
	public static final String _SUCC_EXTENSION			= ".SUCCESS";
	public static final String _DAT_EXTENSION			= ".txt";
	public static final String _DAT_EXTENSION_2			= ".TXT";
	public static final String _COMP_EXTENSION			= ".comp";

	public static final String _BICNET_FIELD_SP			= "[F]";
	public static final String _BICNET_LINE_SP			= "[L]";
	
	private static long			_SMS_SEQ				= 1;
	private static long			_TRS_SEQ				= 1;
	public static int			_START_YEAR				= 1990;
	
	private static long			_PG_ORD_NO				= 1;
	
	/**
	 * line - separator
	 * */
	public static final String _CRLF					= System.getProperty("line.separator");
	

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
	/**<pre>
	 * 문자열 안의 특정문자열을 다른 문자열로 치환한다.
	 * </pre>
	 * @param source String	원본문자열
	 * @param subject String 치환대상문자열
	 * @param object String 치환후문자열
	 * @return String
	 * */
    public static String replace(String source, String subject, String object) {
    	if(source==null|| "".equals(source) || subject==null || "".equals(subject))
    		return source;

    	StringBuffer rtnStr = new StringBuffer();
        String preStr = "";
        String nextStr = source;
        String srcStr  = source;
        
        while (srcStr.indexOf(subject) >= 0) {
            preStr = srcStr.substring(0, srcStr.indexOf(subject));
            nextStr = srcStr.substring(srcStr.indexOf(subject) + subject.length(), srcStr.length());
            srcStr = nextStr;
            rtnStr.append(preStr).append(object);
        }
        rtnStr.append(nextStr);
        return rtnStr.toString();
    }


    /**
	 * OutputStream 으로 write.
	 * 
	 * @param is 입력 stream
	 * @param os 출력 stream
	 * @exception IOException
	 */
    public static void write( InputStream is , OutputStream os ) throws IOException{
    	if(is==null || os==null){
    		return;
    	}
    	byte[] buf	= new byte[1024];
    	int iReadSize			= 0;
    	try{
    		while( (iReadSize=is.read(buf))!=-1 ){
    			os.write(buf,0,iReadSize);
    		}
    		os.flush();
    	}catch(IOException ie){
    		throw ie;
    	}finally{
    		try{if(is!=null){is.close();}}catch(IOException ie){}
    		try{if(os!=null){os.close();}}catch(IOException ie){}
    	}
    }    
    /**
	 * OutputStream 으로 write.
	 * 
	 * @param bytes byte[]
	 * @exception IOException
	 */
    public static void write( byte[] bytes , OutputStream os ) throws IOException{
    	if(os==null)
    		return;
    	try{
    		os.write(bytes);
    		os.flush();
    	}catch(IOException ie){
    		throw ie;
    	}finally{
    		try{if(os!=null){os.close();}}catch(IOException ie){}
    	}
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
	 * null을 empty string으로 변환하여 반환한다.
	 * </pre>
	 * @param source Object
	 * @return String
	 * */
	public static final String nvl(String str){
		return str==null?"":str;
	}
	public static final String nvl2(String str,String replaceStr){
		return str==null?replaceStr:str;
	}
    public static final String replaceAll(String source, String subject, String object) {
        StringBuffer rtnStr = new StringBuffer();
        String preStr = "";
        String nextStr = source;
        String srcStr  = source;

        while (srcStr.indexOf(subject) >= 0) {
            preStr = srcStr.substring(0, srcStr.indexOf(subject));
            nextStr = srcStr.substring(srcStr.indexOf(subject) + subject.length(), srcStr.length());
            srcStr = nextStr;
            rtnStr.append(preStr).append(object);
        }
        rtnStr.append(nextStr);
        return rtnStr.toString();
    }
    
	/**
	 * 문자열이 null이면 ""을, 아니면 공백을 없앤 후 리턴한다.
	 * @param sSrc : 소스문자열
	 * @return "" or 소스문자열에서 공백을 없앤 문자열
	 */
	public static String isNull(String sOrg){
		return isNull(sOrg, "");
	}
	/**
	 * 문자열이 null이면 ""을, 아니면 공백을 없앤 후 리턴한다.
	 * @param sSrc : 소스문자열
	 * @return "" or 소스문자열에서 공백을 없앤 문자열
	 */
	public static String isNull(String sOrg, String sNrg){
		
		if(sOrg == null || sOrg.equals("null")){
			return sNrg;
		}else{
			return sOrg.trim();
		}
	}
	/**<pre>
	 * 숫자형 문자를 1000단위 구분자를 넣은 문자열로 반환한다.
	 * </pre>
	 * @param val String 
	 * @return String
	 * */
	public static String getCommaNumber(String val){
		int dec	= 0;
		try{
			if(val!=null && val.indexOf(".")>=0){
				dec	= val.length()-val.indexOf(".")-1;
			}
			val	= replace(val,",","");
			Double.parseDouble(val);
			
		}catch(Exception e){
			return val;
		}
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(dec);
        return nf.format(Double.parseDouble(val));
    }

	/**<pre>
	 * 주어진 문자열이 최대크기가 될때까지 특정문자를 첨부하여 반환한다.
	 * </pre>
	 * @param str String 
	 * @param maxSize int 
	 * @param addChar String 
	 * @param boolean appendLeft 
	 * */
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
	
	/**<pre>
	 * 문자열에서 type으로 지정되어진 변수 목록을 추출한다. 
	 * </pre>
	 * @param query1 String 
	 * @param type String
	 * @return List
	 * @throws PluginException 
	 * */
	public static List<String> getConditions(String query1,String type) throws Exception{
        List<String> mappingList	= new ArrayList<String>();
        StringTokenizer parser	= new StringTokenizer(query1, type, true);
        String token			= null;
        StringTokenizer pParser	= null;
        
        for(String lastToken = null; parser.hasMoreTokens(); lastToken = token){
            token	= parser.nextToken();
            if(type.equals(lastToken)){
                if(type.equals(token)){
                    token	= null;
                    continue;
                }
 
                pParser		= new StringTokenizer(token, "=,", false);
                if(pParser.hasMoreElements())
                	mappingList.add(pParser.nextToken());

                token = parser.nextToken();
                if(!type.equals(token))
                    throw new Exception("Unterminated inline parameter in mapped statement.");
                token = null;
                continue;
            }
        }
        return mappingList;
    }

	public synchronized static final File copyFile(File src,File dest) throws Exception{
		FileChannel			fi	= null;
		FileChannel			fo	= null;
		FileInputStream		is	= null;
		FileOutputStream	os	= null;
		try{
			if(!dest.getParentFile().exists())
				dest.getParentFile().mkdirs();
			if(dest.exists())
				dest.delete();
			
			dest.createNewFile();
			
			is	= new FileInputStream(src);
			os	= new FileOutputStream(dest);
			
			fi	= is.getChannel();
			fo	= os.getChannel();
			
			fi.transferTo(0, fi.size(), fo);

			fi.close();
			is.close();
			fo.close();
			os.close();
			
		}catch(Exception e){
			throw e;
		}finally{
			try{if(fi!=null)fi.close();}catch(Exception ex){}
			try{if(fo!=null)fo.close();}catch(Exception ex){}
			try{if(is!=null)is.close();}catch(Exception ex){}
			try{if(os!=null)os.close();}catch(Exception ex){}
		}
		return dest;
	}

	public synchronized static final File moveFile(File src,File dest) throws Exception{
		try{
			if(!dest.getParentFile().exists())
				dest.getParentFile().mkdirs();
			if(dest.exists())
				if(!dest.delete())
					throw new Exception("Can not remove file. ["+dest.getAbsolutePath()+"]");
			
			if(!src.renameTo(dest))
				throw new Exception("Can not move file.["+src.getAbsolutePath()+"]->["+dest.getAbsolutePath()+"]");
			
			src	= new File(dest.getAbsolutePath());
		}catch(Exception e){
			throw e;
		}finally{
		}
		return dest;
	}
	
	public static String getMatchFileName(String parent, String name){
		String tmp	= PluginUtil.replaceAll(name, "\"", "");
		final String fileName	= (tmp.indexOf("/")>=0)?tmp.substring(tmp.lastIndexOf("/")+1):tmp;

		File folder	= new File(parent);
		if(!folder.exists())
			return fileName;
		
		File[] arr	= folder.listFiles(new FileFilter(){
			public boolean accept(File pathname) {
				if(pathname.getName().startsWith(fileName))
					return true;
				return false;
			}
		});
		return (arr==null||arr.length==0)?fileName:arr[0].getName();
	}
	
	
	public static String getImgFileMonthFolder(String path){
		String tmp	= PluginUtil.replaceAll(path, "\"", "");
		
		if(tmp.indexOf("/")>=0){
			if(tmp.startsWith("/"))
				tmp	= tmp.substring(1);
			tmp	= tmp.substring(0,tmp.lastIndexOf("/"));
			tmp	= tmp.substring(tmp.lastIndexOf("/"));
			tmp	= PluginUtil.replaceAll(tmp, "/", "");
		}else{
			tmp	= "";
		}
		return tmp;
	}

	public static final String makeFullNumber(String str,int size){
		StringBuffer sb	= new StringBuffer();
		while(sb.length()<size-str.length()){
			sb.append("0");
		}
		sb.append(str);
		return sb.toString();
	}
	
	public static final int _SEQ_TYPE_NUMBER	= 1;
	public static final int _SEQ_TYPE_CHAR		= 2;
	public static String makeSeqString(String val,int length){
		StringBuffer sb	= new StringBuffer();
		while(sb.length()<length-val.length()){
			sb.append("0");
		}
		sb.append(val);
		return sb.toString();
	}
	public static HashMap<String,String> areaMap	= null;	//시도를 담기위한 HashMap

	public synchronized static final String getSMSKey(){
		if(_SMS_SEQ>999999999999L)
			_SMS_SEQ	= 0;
		java.text.SimpleDateFormat sf	= new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS");
		return sf.format(new java.util.Date())+String.valueOf(_SMS_SEQ++);
	}
	
	public synchronized static final String getTransKey(){
		if(_TRS_SEQ>999999L)
			_TRS_SEQ	= 0;
		java.text.SimpleDateFormat sf	= new java.text.SimpleDateFormat("yyyyMMddHHmmss");
		return sf.format(new java.util.Date())+String.valueOf(StringUtil.addChar(""+_TRS_SEQ++, 6, "0", true));
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
	public static String getPgOrdNo(){
		_PG_ORD_NO++;
		if(_PG_ORD_NO>999999)
			_PG_ORD_NO	= 1;
		String val	= String.valueOf(_PG_ORD_NO);
		
		String now	= new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
		
		StringBuffer sb	= new StringBuffer();
		while(sb.length()<6-val.length()){
			sb.append("0");
		}
		sb.append(val);
		val	= now+sb.toString();
		return val;
	}
	
	public static String cutBytes(String val,int size,String enc) throws UnsupportedEncodingException{
		int cSize	= 0;
		for(int i=0;i<val.length();i++){
			cSize	+= new String(new char[]{val.charAt(i)}).getBytes(enc).length;
			if(cSize>size){
				return val.substring(0,i);
			}
		}
		return val;
	}
	
	
	public static void main(String args[]) throws Exception{
	}

	public static final int getHeight(int rowCnt,double rowHeight,double spareHeight,int emptyRowCnt){
		return (int)((rowCnt==0?emptyRowCnt:rowCnt)*rowHeight+spareHeight);
	}
	
}




