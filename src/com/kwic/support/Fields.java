package com.kwic.support;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Element;

import com.kwic.io.JOutputStream;
import com.kwic.util.StringUtil;
import com.kwic.xml.parser.JXParser;

public class Fields {
	
	private static Fields instance;

	/**
	 * 요청 해석기
	 */
	private JXParser requestJxp;
	
	/**
	 * 응답 해석기
	 */
	private JXParser responseJxp;

	/**
	 * 요청 필드 목록
	 */
	private Map<String, List<String>> requestFields;
	
	/**
	 * 요청 필수입력 필드 목록
	 */
	private Map<String, List<String>> requestRequiredFields;
	
	/**
	 * 요청 암호화 필드 목록
	 */
	private Map<String, List<String>> requestEncryptFields;
	
	/**
	 * 응답 암호화 필드 목록
	 */
	private Map<String, List<String>> responseEncryptFields;
	
	/**
	 * 요청 기본값 목록
	 */
	private Map<String, Map<String, String>> requestDefaultValues;

	/**
	 * 생성자
	 * 각 업무별 요청, 응답전문 형식을 xml 파일에서 읽어와서 필드 목록과  필드별 필수입력값 여부, 암호화 여부, 필드명, 기본값을 목록으로 생성하여 
	 * 자신의 프러퍼티로 생성한다.
	 * 기본 인코딩은 utf-8
	 * @throws Exception
	 */
	private Fields() throws Exception {
		String encoding = System.getProperty("file.encoding");
		if (encoding == null || "".equals(encoding)){
			encoding = "UTF8";
		}else{
			encoding = StringUtil.replace(encoding.toUpperCase(), "-", "");
		}
		String reqXml = "com/kwic/support/client/request.xml";
		String resXml = "com/kwic/support/client/response.xml";

		if (!"UTF8".equals(encoding)) {
			reqXml = "com/kwic/support/client/request.kr.xml";
			resXml = "com/kwic/support/client/response.kr.xml";
		}

		requestJxp 		= load(reqXml);
		responseJxp 	= load(resXml);
		
		requestFields 			= loadFields(true, "field");
		requestEncryptFields 	= loadFields(true, "field[@encrypt='true']");
		responseEncryptFields	= loadFields(false, "field[@encrypt='true']");
		requestRequiredFields 	= loadFields(true, "field[@required='true']");
		
		requestDefaultValues 	= loadRequestDefaultValues();
	}

	/**
	 * 싱글턴 객체 반환
	 * @return
	 * @throws Exception
	 */
	public static Fields getInstance() throws Exception {
		synchronized (Fields.class) {
			if (instance == null)
				instance = new Fields();
		}
		return instance;
	}

	/**
	 * 파서 호출
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private JXParser load(String path) throws Exception {
		JXParser jxp = null;
		InputStream is = null;
		JOutputStream jos = null;
		try {
			is = this.getClass().getClassLoader().getResourceAsStream(path);
			if (is == null){
				throw new Exception("레이아웃 리소스를 찾을 수 없습니다.[" + path + "]");
			}

			jos = new JOutputStream();
			jos.write(is);
			jos.flush();

			String encoding = System.getProperty("file.encoding");
			if (encoding == null || "".equals(encoding)){
				encoding = "UTF8";
			}else{
				encoding = StringUtil.replace(encoding.toUpperCase(), "-", "");
			}

			if (!"UTF8".equals(encoding)){
				jxp = new JXParser(new String(jos.getBytes(), "EUC-KR"));
			}else{
				jxp = new JXParser(new String(jos.getBytes(), "UTF-8"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (jos != null){
					jos.close();
				}
			} catch (Exception e) {
			}
			try {
				if (is != null){
					is.close();
				}
			} catch (Exception e) {
			}
		}
		return jxp;
	}

	private Map<String, List<String>> loadFields(boolean isRequest, String fildXPath) throws Exception {
		JXParser jxp = isRequest ? requestJxp : responseJxp;

		Map<String, List<String>> maps = new HashMap<String, List<String>>();
		List<String> encFields = null;
		Element[] bizArr = jxp.getElements("//business");
		Element[] fields = null;
		String MESSAGECODE = null;
		String SERVICECODE = null;
		try{
			for (int i = 0; i < bizArr.length; i++) {
				encFields = new ArrayList<String>();
				fields = jxp.getElements(bizArr[i], fildXPath);
	
				for (int j = 0; j < fields.length; j++) {
					encFields.add(jxp.getAttribute(fields[j], "name"));
				}
	
				MESSAGECODE = jxp.getAttribute(bizArr[i], "MESSAGECODE");
				SERVICECODE = jxp.getAttribute(bizArr[i], "SERVICECODE");
				if (SERVICECODE != null && !"".equals(SERVICECODE)){
					SERVICECODE = "-" + SERVICECODE;
				}else{
					SERVICECODE = "";
				}
				maps.put(MESSAGECODE + SERVICECODE, encFields);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return maps;
	}

	private Map<String, Map<String, String>> loadRequestDefaultValues() throws Exception {
		Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();

		Map<String, String> pairs = null;
		Element[] bizArr = requestJxp.getElements("//business");
		Element[] fields = null;
		String MESSAGECODE = null;
		String SERVICECODE = null;

		for (int i = 0; i < bizArr.length; i++) {
			pairs = new HashMap<String, String>();
			fields = requestJxp.getElements(bizArr[i], "field");
			for (int j = 0; j < fields.length; j++) {
				pairs.put(requestJxp.getAttribute(fields[j], "name"), requestJxp.getAttribute(fields[j], "default"));
			}

			MESSAGECODE = requestJxp.getAttribute(bizArr[i], "MESSAGECODE");
			SERVICECODE = requestJxp.getAttribute(bizArr[i], "SERVICECODE");
			if (SERVICECODE != null && !"".equals(SERVICECODE)){
				SERVICECODE = "-" + SERVICECODE;
			}else{
				SERVICECODE = "";
			}

			maps.put(MESSAGECODE + SERVICECODE, pairs);
		}

		return maps;
	}

	/**
	 * 업무별 요청전문에서 암호화 대상 필드 여부 검사
	 * @param MESSAGECODE
	 * @param SERVICECODE
	 * @param fieldName
	 * @return
	 * @throws Exception
	 */
	public boolean isRequestEncryptField(String MESSAGECODE, String SERVICECODE, String fieldName) throws Exception {
		if (MESSAGECODE == null){
			return false;
		}
		String key = makeKey(MESSAGECODE, SERVICECODE);

		List<String> list = requestEncryptFields.get(key);
		if (list == null){
			return false;
		}

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(fieldName)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 민원 자동입력방지문자 처리 전문 여부
	 * @param MESSAGECODE
	 * @param SERVICECODE
	 * @return
	 * @throws Exception
	 */
	public boolean isMWCapchaRequired(String MESSAGECODE, String SERVICECODE) throws Exception {
		Element biz = null;
		if (SERVICECODE == null || "".equals(SERVICECODE))
			biz = requestJxp.getElement("//business[@MESSAGECODE='" + MESSAGECODE + "']/field[@name='CAPTCHAOPTION' and @required='true']");
		else {
			biz = requestJxp.getElement("//business[@MESSAGECODE='" + MESSAGECODE + "' and @SERVICECODE='" + SERVICECODE + "']/field[@name='CAPTCHAOPTION' and @required='true']");
		}
		return biz == null ? false : true;
	}

	/**
	 * 업무별 응답전문에서 특정 필드가 암호화 대상인지 여부 검사
	 * @param MESSAGECODE
	 * @param SERVICECODE
	 * @param fieldName
	 * @return
	 * @throws Exception
	 */
	public boolean isResponseEncryptField(String MESSAGECODE, String SERVICECODE, String fieldName) throws Exception {
		if (MESSAGECODE == null){
			return false;
		}
		String key = makeKey(MESSAGECODE, SERVICECODE);

		List<String> list = responseEncryptFields.get(key);
		if (list == null){
			return false;
		}

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(fieldName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 업무별 요청 필드 목록 반환
	 * @param MESSAGECODE
	 * @param SERVICECODE
	 * @return
	 */
	public List<String> getRequestFields(String MESSAGECODE, String SERVICECODE) {
		String key = makeKey(MESSAGECODE, SERVICECODE);
		return requestFields.get(key) == null ? new ArrayList<String>() : requestFields.get(key);
	}

	/**
	 * 업무별 요청전문에서 암호화 대상 필드 목록 반환
	 * @param MESSAGECODE
	 * @param SERVICECODE
	 * @return
	 */
	public List<String> getRequestEncryptFields(String MESSAGECODE, String SERVICECODE) {
		String key = makeKey(MESSAGECODE, SERVICECODE);
		return requestEncryptFields.get(key) == null ? new ArrayList<String>() : requestEncryptFields.get(key);
	}

	/**
	 * 업무별 응답전문에서 암호화 대상 필드 목록 반환
	 * @param MESSAGECODE 전문코드
	 * @param SERVICECODE
	 * @return
	 */
	public List<String> getResponseEncryptFields(String MESSAGECODE, String SERVICECODE) {
		String key = makeKey(MESSAGECODE, SERVICECODE);
		return responseEncryptFields.get(key) == null ? new ArrayList<String>() : responseEncryptFields.get(key);
	}

	/**
	 *  업무별 요청전문에서 필수입력 대상 필드 목록 반환
	 * @param MESSAGECODE 전문코드
	 * @param SERVICECODE
	 * @return
	 */
	public List<String> getRequestRequiredFields(String MESSAGECODE, String SERVICECODE) {
		String key = makeKey(MESSAGECODE, SERVICECODE);
		return requestRequiredFields.get(key) == null ? new ArrayList<String>() : requestRequiredFields.get(key);
	}

	/**
	 * 업무별 요청전문에서 특정 필드의 기본값 반환
	 * @param MESSAGECODE
	 * @param SERVICECODE
	 * @param name
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public String getRequestDefaultValue(String MESSAGECODE, String SERVICECODE, String name, String value) throws Exception {
		if (value != null && !"".equals(value) && !"null".equals(value)) {
			return value;
		}

		String key = makeKey(MESSAGECODE, SERVICECODE);
		String defaultValue = requestDefaultValues.get(key).get(name);
		return defaultValue;
	}

	/**
	 * 업무별 요청전문에서 필수입력 필드명 반환
	 * @param MESSAGECODE
	 * @param SERVICECODE
	 * @param fieldName
	 * @return
	 * @throws Exception
	 */
	public String getRequestFieldTitle(String MESSAGECODE, String SERVICECODE, String fieldName) throws Exception {
		String title = null;
		if (SERVICECODE == null || "".equals(SERVICECODE)) {
			title = requestJxp.getAttribute(requestJxp.getElement("//business[@MESSAGECODE='" + MESSAGECODE + "']/field[@name='" + fieldName + "']"), "title");
		} else {
			Element biz = requestJxp.getElement("//business[@MESSAGECODE='" + MESSAGECODE + "' and @SERVICECODE='" + SERVICECODE + "']");
			title = requestJxp.getAttribute(requestJxp.getElement(biz, "field[@name='" + fieldName + "']"), "title");
		}
		return title == null || "".equals(title) ? fieldName : title;
	}
	
	public int getRequestFieldMaxBytes(String MESSAGECODE, String SERVICECODE, String fieldName) throws Exception {
		int maxBytes = 0;
		
		Element biz = requestJxp.getElement("//business[@MESSAGECODE='" + MESSAGECODE + "' and @SERVICECODE='" + SERVICECODE + "']");
		maxBytes 	= Integer.parseInt(requestJxp.getAttribute(requestJxp.getElement(biz, "field[@name='" + fieldName + "']"), "maxBytes"));
		
		return maxBytes;
	}

	/**
	 * 업무별 요청전문을 겁색하기 위한 키 생성
	 * @param MESSAGECODE
	 * @param SERVICECODE
	 * @return
	 */
	private String makeKey(String MESSAGECODE, String SERVICECODE) {
		String key = null;
		if (SERVICECODE != null && !"".equals(SERVICECODE)) {
			key = MESSAGECODE + "-" + SERVICECODE;
		} else {
			key = MESSAGECODE;
		}
		return key;
	}
}
