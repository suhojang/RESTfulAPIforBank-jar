package com.kwic.support;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;

import com.kwic.telegram.tcp.JTcpManager;

public class ScrapUtil {
	
	/**
	 * bytes To Hex String
	 * @param bytes
	 * @return
	 */
	public static String bytesToHexString(byte[] bytes) {
		final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[bytes.length * 2];
		
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * hex To Byte Array
	 * @param hex
	 * @return
	 */
	public static byte[] hexToByteArray(String hex) {
		if (hex == null || hex.length() == 0) {
			return null;
		}

		byte[] ba = new byte[hex.length() / 2];
		for (int i = 0; i < ba.length; i++) {
			ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return ba;
	}

	/**
	 * decode Base64 String
	 * @param base64
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static byte[] decodeBase64String(String base64, String encoding) throws Exception {
		return Base64.decodeBase64(base64.getBytes(encoding));
	}

	/**
	 * encode Base64 String
	 * @param bytes
	 * @param encoding
	 * @return
	 */
	public static String encodeBase64String(byte[] bytes, String encoding) {
		String str = "";
		try {
			str = new String(Base64.encodeBase64(bytes), encoding);
		} catch (Exception e) {
			str = new String(Base64.encodeBase64(bytes));
		}
		return str;
	}

	public static String send(String ip, int port, byte[] message) throws Exception {
		String result = "";
		try {
			result = new String(JTcpManager.getInstance().sendMessage(ip, port, message, true), "UTF-8");
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	public static String removeXSS(String str, boolean use_html) {
		String str_low = "";
		if (use_html) {
			// HTML tag를 모두 제거
			str = str.replaceAll("<", "&lt;");
			str = str.replaceAll(">", "&gt;");

			// 특수 문자 제거
			str = str.replaceAll("\"", "&gt;");
			str = str.replaceAll("&", "&amp;");
			str = str.replaceAll("%00", null);
			str = str.replaceAll("\"", "&#34;");
			str = str.replaceAll("\'", "&#39;");
			str = str.replaceAll("%", "&#37;");
			str = str.replaceAll("../", "");
			str = str.replaceAll("..\\\\", "");
			str = str.replaceAll("./", "");
			str = str.replaceAll("%2F", "");
			// 허용할 HTML tag만 변경
			str = str.replaceAll("&lt;p&gt;", "<p>");
			str = str.replaceAll("&lt;P&gt;", "<P>");
			str = str.replaceAll("&lt;br&gt;", "<br>");
			str = str.replaceAll("&lt;BR&gt;", "<BR>");
			// 스크립트 문자열 필터링
			str_low = str.toLowerCase();

			if (str_low.contains("javascript") || str_low.contains("script")     || str_low.contains("iframe")   || 
				str_low.contains("document")   || str_low.contains("vbscript")   || str_low.contains("applet")   || 
				str_low.contains("embed")      || str_low.contains("object")     || str_low.contains("frame")    || 
				str_low.contains("grameset")   || str_low.contains("layer")      || str_low.contains("bgsound")  || 
				str_low.contains("alert")      || str_low.contains("onblur")     || str_low.contains("onchange") || 
				str_low.contains("onclick")    || str_low.contains("ondblclick") || str_low.contains("enerror")  || 
				str_low.contains("onfocus")    || str_low.contains("onload")     || str_low.contains("onmouse")  || 
				str_low.contains("onscroll")   || str_low.contains("onsubmit")   || str_low.contains("onunload")) {
		
				str = str_low;
				str = str.replaceAll("javascript", "x-javascript");
				str = str.replaceAll("script", "x-script");
				str = str.replaceAll("iframe", "x-iframe");
				str = str.replaceAll("document", "x-document");
				str = str.replaceAll("vbscript", "x-vbscript");
				str = str.replaceAll("applet", "x-applet");
				str = str.replaceAll("embed", "x-embed");
				str = str.replaceAll("object", "x-object");
				str = str.replaceAll("frame", "x-frame");
				str = str.replaceAll("grameset", "x-grameset");
				str = str.replaceAll("layer", "x-layer");
				str = str.replaceAll("bgsound", "x-bgsound");
				str = str.replaceAll("alert", "x-alert");
				str = str.replaceAll("onblur", "x-onblur");
				str = str.replaceAll("onchange", "x-onchange");
				str = str.replaceAll("onclick", "x-onclick");
				str = str.replaceAll("ondblclick", "x-ondblclick");
				str = str.replaceAll("enerror", "x-enerror");
				str = str.replaceAll("onfocus", "x-onfocus");
				str = str.replaceAll("onload", "x-onload");
				str = str.replaceAll("onmouse", "x-onmouse");
				str = str.replaceAll("onscroll", "x-onscroll");
				str = str.replaceAll("onsubmit", "x-onsubmit");
				str = str.replaceAll("onunload", "x-onunload");
			}
		} else {
			str = str.replaceAll("\"", "&gt;");
			str = str.replaceAll("&", "&amp;");
			str = str.replaceAll("<", "&lt;");
			str = str.replaceAll(">", "&gt;");
			str = str.replaceAll("%00", null);
			str = str.replaceAll("\"", "&#34;");
			str = str.replaceAll("\'", "&#39;");
			str = str.replaceAll("%", "&#37;");
			str = str.replaceAll("../", "");
			str = str.replaceAll("..\\\\", "");
			str = str.replaceAll("./", "");
			str = str.replaceAll("%2F", "");
		}
		return str;
	}

	public static String isNull(String param) throws Exception {
		return param == null || "".equals(param) ? "" : param;
	}

	public static final String printMap(Map<?, ?> map) {
		StringBuffer sb	= new StringBuffer();
		Object fieldName = null;
		Iterator<?> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			fieldName = iter.next();
			sb.append(String.valueOf(fieldName) + "=" + String.valueOf(map.get(fieldName)));
		}
		return sb.toString();
	}

	public static final String readIn(boolean close) {
		BufferedReader br = null;
		String line = null;
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			line = br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (close && br != null){
					br.close();
				}
			} catch (Exception ex) {
			}
		}
		return line;
	}

	public static final String getErrorStack(Exception e) {
		StringBuffer errLog = new StringBuffer();
		errLog.append(e.toString() + " : " + e.getMessage() + "\n");
		StackTraceElement[] stacks = e.getStackTrace();
		for (int i = 0; i < stacks.length; i++) {
			errLog.append(stacks[i].toString() + "\n");
		}
		return errLog.toString();
	}

	public static Map<String, String> jsonToMap(String json) throws Exception {
		if (json != null){
			json = json.trim();
		}
		if (json.startsWith("(")){
			json = json.substring(1);
		}
		if (json.endsWith("(")){
			json = json.substring(0, json.length() - 1);
		}

		Map<?, ?> map = (Map<?, ?>) (new ObjectMapper().readValue(json, Map.class));

		Map<String, String> responseMap = new HashMap<String, String>();

		Object fieldName = null;
		Iterator<?> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			fieldName = iter.next();
			responseMap.put(String.valueOf(fieldName), String.valueOf(map.get(fieldName)));
		}
		return responseMap;
	}

	public static void main(String[] args) {
	}
}
