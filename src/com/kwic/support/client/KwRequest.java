package com.kwic.support.client;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;

import com.kwic.license.KwicLicense;
import com.kwic.license.exception.LicenseException;
import com.kwic.security.aes.AESCipher;
import com.kwic.support.Crypto;
import com.kwic.support.Fields;
import com.kwic.support.ScrapUtil;
import com.kwic.telegram.RestClient;
import com.kwic.xml.parser.JXParser;

public class KwRequest {
	/**
	 * 요청,응답데이터의 암복호화 없음
	 * */
	public static final int CRYPT_TYPE_NONE	= 0;
	
	/**
	 * 요청,응답데이터의 암복호화에 Kwic SEED 128 bit 알고리즘 사용
	 * */
	public static final int CRYPT_TYPE_KWICSEED128 = 1;
	
	/**
	 * 요청,응답데이터의 암복호화에 Kwic AES 256 bit 알고리즘 사용
	 * */
	public static final int CRYPT_TYPE_KWICAES256 = 2;
	
	/**
	 * 요청 데이터 JSON Type 
	 */
	public static final int REQUEST_DATA_TYPE_JSON = 1;
	
	/**
	 * 요청 데이터 XML Type 
	 */
	public static final int REQUEST_DATA_TYPE_XML = 2;
	
	public static final String VALUE_ATTRIBUTE_NAME	= "VALUE";
	
	/**
	 * 문자열 변환 Characterset
	 */
	public static final String CHARSET	= "UTF-8";
	
	/**
	 * 펌뱅킹 통신 서버 요청/응답
	 * 
	 * @param url		: 전송 URL
	 * @param reqMap	: 전송 요청 데이터
	 * @return
	 */
	public static final KwResult request(String url, Map<String,Object> reqMap) {
		return request(url, reqMap, false);
	}
	
	/**
	 * 펌뱅킹 통신 서버 요청/응답
	 *  - dummy 송신 처리
	 * 
	 * @param url		: 전송 URL
	 * @param reqMap	: 전송 요청 데이터
	 * @param isDummy	: dummy 여부
	 * @return
	 */
	public static final KwResult request(String url, Map<String,Object> reqMap, boolean isDummy) {
		if (!isDummy) {
			return request(url, reqMap, AESCipher.DEFAULT_KEY, CRYPT_TYPE_KWICAES256);
		} else {
			return requestDummy(url, reqMap, AESCipher.DEFAULT_KEY, CRYPT_TYPE_KWICAES256, REQUEST_DATA_TYPE_JSON);
		}
	}
	
	/**
	 * 펌뱅킹 통신 서버 요청/응답
	 * 
	 * @param url		: 전송 URL
	 * @param reqMap	: 전송 요청 데이터
	 * @param cryptKey	: 암호화키
	 * @param cryptType	: 암호화 방식
	 * @return
	 */
	public static final KwResult request(String url, Map<String,Object> reqMap, String cryptKey, int cryptType) {
		return request(url, reqMap, cryptKey, cryptType, REQUEST_DATA_TYPE_JSON);
	}
	
	/**
	 * 펌뱅킹 통신 서버 요청/응답
	 * 1. validation
	 * 2. 암호화
	 * 3. removeXSS
	 * 4. Map to JSON
	 * 5. 요청
	 * 6. JSON to Map
	 * 
	 * @param url		: 전송 URL
	 * @param reqMap	: 전송 요청 데이터
	 * @param cryptKey	: 암호화키
	 * @param cryptType	: 암호화 방식
	 * @param requestDataType	: 요청 데이터 형식(JSON)
	 * @return
	 */
	public static final KwResult request(String url, Map<String,Object> reqMap, String cryptKey, int cryptType, int requestDataType) {
		StringBuffer errLog	= new StringBuffer();
		
		Map<String,Object> responseMap	= null;
		
		String MESSAGECODE	= null;
		String SERVICECODE	= null;
		
		boolean result		= true;
		String errorCode	= "0000";
		String errorMessage	= "";
		KwResult scrapResult	= null;
		
		try{
			//0. 라이선스 체크
			try {
				if(!KwicLicense.getInstance().right(true))
					throw new LicenseException("Invalid license. Please contact your business representative.");
			} catch (Exception e) {
				errorCode		= "ERR_LIB_0000";
				errorMessage	= "인증 되지 않은 라이선스 입니다.(서버의 MAC ADDRESS를 확인하시고 고객센터로 문의 하시기 바랍니다.)";
				throw e;
			}
			
			//1. validation 체크
			try{
				validateRequest(reqMap);
			}catch(Exception e){
				errorCode		= "ERR_LIB_0001";
				errorMessage	= e.getMessage();
				throw e;
			}
			
			//2. 요청 데이터 암호화
			try{
				if(cryptKey != null && !"".equals(cryptKey)) {
					reqMap	= encrypt(reqMap, cryptType, cryptKey);
				}
			}catch(Exception e){
				errorCode		= "ERR_LIB_0002";
				errorMessage	= "요청데이터를 암호화 중 오류가 발생하였습니다.";
				throw e;
			}
			
			MESSAGECODE		= String.valueOf(reqMap.get("MESSAGECODE"));
			SERVICECODE		= String.valueOf(reqMap.get("SERVICECODE"));
			
			//3. 암호화 필드 확인 후 평문 일 경우 removeXSS 처리
			Iterator<String> iter	= null;
			String fieldName		= null;
			String fieldValue		= null;
			try{
				iter	= reqMap.keySet().iterator();
				while(iter.hasNext()){
					fieldName	= iter.next();
					fieldValue	= String.valueOf(reqMap.get(fieldName));
					if(!Fields.getInstance().isRequestEncryptField(MESSAGECODE, SERVICECODE, fieldName)) {
						reqMap.put(fieldName, ScrapUtil.removeXSS(ScrapUtil.isNull(String.valueOf(reqMap.get(fieldName))), false));
					}
				}
			}catch(Exception e){
				errorCode    = "ERR_LIB_0003";
				errorMessage = "요청데이터 정규화 중 오류가 발생하였습니다. [" + fieldName + "=" + fieldValue + "]";
				throw e;
			}
			
			//4. 데이터 변환
			String requestData	= null;
			try{
				if (requestDataType==REQUEST_DATA_TYPE_JSON) {
					requestData	= new ObjectMapper().writeValueAsString(reqMap);
				} else if (requestDataType==REQUEST_DATA_TYPE_XML) {
					//XML은 현재 지원하지 않음.
					requestData	= mapToXml(reqMap);
				}
				
			}catch(Exception e){
				errorCode	 = "ERR_LIB_0004";
				errorMessage = "요청데이터를  JSON으로 전환 중 오류가 발생하였습니다.";
				throw e;
			}
			
			/*
			 * 요청 및 응답
			 */
			String responseContent	= null;
			try{
				responseContent	= RestClient.sendJSON(url, requestData);
			}catch(Exception e){
				if(e.getCause() instanceof SocketTimeoutException){
					errorCode    = "ERR_LIB_1000";
					errorMessage = "펌뱅킹 통신 API 서버 연결 시간이 초과되었습니다.[" + url + "]";
				}else{
					errorCode	 = "ERR_LIB_1001";
					errorMessage = "펌뱅킹 통신 API 서버에 연결할 수 없습니다.[" + url + "]";
				}
				throw e;
			}

			//응답 데이터 parsing
			scrapResult = parseResponse(cryptType, cryptKey, responseContent);			
			
		}catch(Exception e){
			result	= false;
			errLog.append(ScrapUtil.getErrorStack(e));
			if(responseMap == null) {
				responseMap	= new HashMap<String,Object>();
			}
		}
		
		if(scrapResult == null){
			scrapResult	= new KwResult();
			scrapResult.setResult(result);
			scrapResult.setErrorCode(errorCode);
			scrapResult.setErrorMessage(errorMessage);
			scrapResult.setErrorStack(errLog.toString());
			
			responseMap.put("TRXRESPCODE",	errorCode);		//거래응답코드
			responseMap.put("TRXRESPDESC",	errorMessage);	//거래응답내용
			responseMap.put("BANKRESPCODE",	"");			//은행응답코드
			responseMap.put("BANKRESPDESC",	"");			//은행응답내용
			
			scrapResult.setResponseMap(responseMap);
		}
		return scrapResult;
	}
	
	/**
	 * 테스트 통신 - Dummy
	 * 
	 * @param url		: 전송 URL
	 * @param reqMap	: 전송 요청 데이터
	 * @param cryptKey	: 암호화키
	 * @param cryptType	: 암호화 방식
	 * @param requestDataType	: 요청 데이터 형식(JSON)
	 * @return
	 */
	public static final KwResult requestDummy(String url, Map<String,Object> reqMap, String cryptKey, int cryptType, int requestDataType) {
		StringBuffer errLog	= new StringBuffer();
		
		Map<String,Object> responseMap	= null;
		
		String MESSAGECODE	= null;
		String SERVICECODE	= null;
		
		boolean result		= true;
		String errorCode	= "0000";
		String errorMessage	= "";
		KwResult scrapResult	= null;
		
		try{
			//1. validation 체크
			try{
				validateRequest(reqMap);
			}catch(Exception e){
				errorCode		= "ERR_LIB_0001";
				errorMessage	= e.getMessage();
				throw e;
			}
			
			//2. 요청 데이터 암호화
			try{
				if(cryptKey != null && !"".equals(cryptKey)) {
					reqMap	= encrypt(reqMap, cryptType, cryptKey);
				}
			}catch(Exception e){
				errorCode		= "ERR_LIB_0002";
				errorMessage	= "요청데이터를 암호화 중 오류가 발생하였습니다.";
				throw e;
			}
			
			MESSAGECODE		= String.valueOf(reqMap.get("MESSAGECODE"));
			SERVICECODE		= String.valueOf(reqMap.get("SERVICECODE"));
			
			//3. 암호화 필드 확인 후 평문 일 경우 removeXSS 처리
			Iterator<String> iter	= null;
			String fieldName		= null;
			String fieldValue		= null;
			try{
				iter	= reqMap.keySet().iterator();
				while(iter.hasNext()){
					fieldName	= iter.next();
					fieldValue	= String.valueOf(reqMap.get(fieldName));
					if(!Fields.getInstance().isRequestEncryptField(MESSAGECODE, SERVICECODE, fieldName)) {
						reqMap.put(fieldName, ScrapUtil.removeXSS(ScrapUtil.isNull(String.valueOf(reqMap.get(fieldName))), false));
					}
				}
			}catch(Exception e){
				errorCode    = "ERR_LIB_0003";
				errorMessage = "요청데이터 정규화 중 오류가 발생하였습니다. [" + fieldName + "=" + fieldValue + "]";
				throw e;
			}
			
			//4. 데이터 변환
			/*String requestData	= null;
			try{
				if (requestDataType==REQUEST_DATA_TYPE_JSON) {
					requestData	= new ObjectMapper().writeValueAsString(reqMap);
				} else if (requestDataType==REQUEST_DATA_TYPE_XML) {
					//XML은 현재 지원하지 않음.
					requestData	= mapToXml(reqMap);
				}
				
			}catch(Exception e){
				errorCode	 = "ERR_LIB_0004";
				errorMessage = "요청데이터를  JSON으로 전환 중 오류가 발생하였습니다.";
				throw e;
			}*/
			
			/*
			 * 요청 및 응답
			 */
			String responseContent	= null;
			/******************************** DUMMY DATA *************************************/
			List<Map<String,Object>> rec	= null;
			Map<String,Object> dummyMap		= new HashMap<String,Object>();
			dummyMap.putAll(reqMap);
			
			if ("9999".equals(dummyMap.get("MESSAGECODE")) && "032".equals(SERVICECODE)) {
				//상사정보조회
				dummyMap.put("NEXTYN", "N");
				
				//상사정보 LIST목록
				rec	= new ArrayList<Map<String,Object>>();
				Map<String,Object> recMap		= new HashMap<String,Object>();
				recMap.put("MEMBIZNO", 	"2148159394");
				recMap.put("MEMNM", 	"(주)기웅정보통신");
				
				List<Map<String,Object>> bank	= new ArrayList<Map<String,Object>>();
				Map<String,Object> bankMap		= new HashMap<String,Object>();
				bankMap.put("CHANNEL", 	"KSNET");
				bankMap.put("BANKCD", 	"004");
				bankMap.put("FBSCODE", 	"KSTEST01");
				bankMap.put("RECCODEYN", "N");
				
				bank.add(bankMap);
				
				recMap.put("BANK", bank);
				
				rec.add(recMap);
			} else if ("0600".equals(MESSAGECODE) && "300".equals(SERVICECODE)) {
				//잔액조회
				dummyMap.put("BALANCE", "1000");
				dummyMap.put("BALANCEAVAILABLE", "1000");
			} else if ("0600".equals(MESSAGECODE) && "400".equals(SERVICECODE)) {
				//예금주조회
				dummyMap.put("DEPOSITOR", "기웅정보통신");
			} else if ("0200".equals(MESSAGECODE) && "300".equals(SERVICECODE)) {
				//거래내역 조회
				dummyMap.put("NEXTYN", "N");
				
				//거래내역 LIST목록
				rec	= new ArrayList<Map<String,Object>>();
				Map<String,Object> recMap		= new HashMap<String,Object>();
				recMap.put("TRXNO", 	"000003");			//거래일련번호
				recMap.put("BANKCD", 	"004");				//거래은행코드
				recMap.put("ACCTNO", 	"1234567891011");	//거래계좌번호
				recMap.put("CURRCD", 	"KRW");				//거래계좌통화코드
				recMap.put("TRXDATE", 	"20200904");		//거래일자
				recMap.put("TRXTIME", 	"090000");			//거래시간
				recMap.put("TRXTYPE", 	"20");				//거래구분 20:입금
				recMap.put("INAMT", 	"30000");			//입금금액
				recMap.put("OUTAMT", 	"0");				//출금금액
				recMap.put("BALANCE", 	"1527000");			//거래후잔액
				recMap.put("DESC", 		"홍길동 입금");			//적요
				recMap.put("BRANCHCD", 	"02");				//거래지점코드 02:인터넷뱅킹
				
				rec.add(recMap);
			} else if ("0100".equals(MESSAGECODE) && "100".equals(SERVICECODE)) {
				//지급이체요청
				dummyMap.put("BALANCE", "1526700");	//출금 후 잔액
				dummyMap.put("FEE", 	"300");		//수수료
			} else if ("0600".equals(MESSAGECODE) && "101".equals(SERVICECODE)) {
				//지급이체결과조회
				//dummyMap.put("FAILYN", 		"N");				//이체 불능 여부
				//정상
				dummyMap.put("OUTBANKCD", 	"004");				//출금은행코드
				dummyMap.put("OUTACCTNO", 	"1234567891011");	//출금계좌번호
				dummyMap.put("OUTAMT", 		"30000");			//출금금액
				dummyMap.put("OUTDESC", 	"홍길동에게 입금");		//출금계좌적요
				dummyMap.put("INBANKCD", 	"004");				//입금은행코드
				dummyMap.put("INACCTNO", 	"1234567891012");	//입금계좌번호
				dummyMap.put("INDESC", 		"기웅정보통신");		//입금계좌적요
				dummyMap.put("BALANCE", 	"1497700");			//출금후잔액
				dummyMap.put("FEE", 		"300");				//수수료
			}
			
			if (rec != null) {
				dummyMap.put("REC", rec);
			}
			
			dummyMap.put("TRXRESPCODE", "0000");
			dummyMap.put("TRXRESPDESC", "");
			if (!"9999".equals(dummyMap.get("MESSAGECODE"))) {
				dummyMap.put("BANKRESPCODE", "0000");
				dummyMap.put("BANKRESPDESC", "");
			}
			
			responseContent	= new ObjectMapper().writeValueAsString(dummyMap);
			
			/********************************&&DUMMY DATA&& *************************************/

			//응답 데이터 parsing
			scrapResult = parseResponse(cryptType, cryptKey, responseContent);			
			
		}catch(Exception e){
			result	= false;
			errLog.append(ScrapUtil.getErrorStack(e));
			if(responseMap == null) {
				responseMap	= new HashMap<String,Object>();
			}
		}
		
		if(scrapResult == null){
			scrapResult	= new KwResult();
			scrapResult.setResult(result);
			scrapResult.setErrorCode(errorCode);
			scrapResult.setErrorMessage(errorMessage);
			scrapResult.setErrorStack(errLog.toString());
			
			responseMap.put("TRXRESPCODE",	errorCode);		//거래응답코드
			responseMap.put("TRXRESPDESC",	errorMessage);	//거래응답내용
			responseMap.put("BANKRESPCODE",	"");			//은행응답코드
			responseMap.put("BANKRESPDESC",	"");			//은행응답내용
			
			scrapResult.setResponseMap(responseMap);
		}
		return scrapResult;
	}
	
	/**
	 * validation check
	 * 
	 * @param reqMap
	 * @throws Exception
	 */
	private static final void validateRequest(Map<String,Object> reqMap) throws Exception{
		if(reqMap.get("MESSAGECODE") == null || "".equals(reqMap.get("MESSAGECODE"))) {
			throw new Exception("필수값 [메시지코드]가 입력되지 않았습니다.");
		}

		//validate required values
		List<String> fields	= Fields.getInstance().getRequestRequiredFields(String.valueOf(reqMap.get("MESSAGECODE")), String.valueOf(reqMap.get("SERVICECODE")));

		for(int i=0; i < fields.size(); i++){
			//required check
			if(reqMap.get(fields.get(i)) == null || "".equals(reqMap.get(fields.get(i)))) {
				throw new Exception("필수값 [" + Fields.getInstance().getRequestFieldTitle(String.valueOf(reqMap.get("MESSAGECODE")), String.valueOf(reqMap.get("SERVICECODE")), fields.get(i)) + "]이/가 입력되지 않았습니다.");
			}
			
			//lenth check
			int maxBytes	= Fields.getInstance().getRequestFieldMaxBytes(String.valueOf(reqMap.get("MESSAGECODE")), String.valueOf(reqMap.get("SERVICECODE")), fields.get(i));
			if(maxBytes>0 && String.valueOf(reqMap.get(fields.get(i))).getBytes(CHARSET).length > maxBytes) {
				throw new Exception("["+fields.get(i)+"] 항목의 최대크기("+CHARSET+" 기준)가 ["+maxBytes+"]Bytes를 초과하였습니다.");
			}
		}
		
		//get field list
		fields	= Fields.getInstance().getRequestFields(String.valueOf(reqMap.get("MESSAGECODE")), String.valueOf(reqMap.get("SERVICECODE")));
		if(fields.size() == 0) {
			throw new Exception("정의되지 않은 전문코드입니다.[MESSAGECODE=" + reqMap.get("MESSAGECODE") + ",SERVICECODE=" + reqMap.get("SERVICECODE") + "]");
		}
		
		//set default values
		for(int i=0; i < fields.size(); i++){
			if(reqMap.get(fields.get(i)) == null || "".equals(reqMap.get(fields.get(i)))) {
				reqMap.put(fields.get(i), Fields.getInstance().getRequestDefaultValue(String.valueOf(reqMap.get("MESSAGECODE")), String.valueOf(reqMap.get("SERVICECODE")), fields.get(i), String.valueOf(reqMap.get(fields.get(i)))));
			}
		}
	}
	
	/**
	 * encrypt field
	 * 
	 * @param reqMap
	 * @param cryptType
	 * @param cryptKey
	 * @return
	 * @throws Exception
	 */
	private static final Map<String,Object> encrypt(Map<String,Object> reqMap, int cryptType, String cryptKey) throws Exception{
		if(cryptType == CRYPT_TYPE_NONE){
			return reqMap;
		}
		
		if(reqMap.get("MESSAGECODE") == null) {
			return reqMap;
		}
		
		List<String> reqEncFields	= Fields.getInstance().getRequestEncryptFields(String.valueOf(reqMap.get("MESSAGECODE")), String.valueOf(reqMap.get("SERVICECODE")));
		
		String fieldName	= null;
		String fieldValue	= null;
		for(int i=0; i < reqEncFields.size(); i++){
			fieldName	= reqEncFields.get(i);
			fieldValue	= String.valueOf(reqMap.get(fieldName)) == null ? "" : String.valueOf(reqMap.get(fieldName));
			
			//암호화하기 전 평문을 base64 UTF-8 문자열로로 변환 (모든 문자는 base64로 인코딩 한 후  암호화한다. 복호화는 그 역순이다.)
			try{
				fieldValue	= new String(Base64.encodeBase64(fieldValue.getBytes("UTF-8")), "UTF-8");
			}catch(Exception e){
				fieldValue	= new String(Base64.encodeBase64(fieldValue.getBytes("UTF-8")));
			}

			if(reqMap.get(fieldName) != null){
				if(cryptType == CRYPT_TYPE_KWICSEED128){
					try{
						reqMap.put(fieldName, Crypto.encrypt(fieldValue, cryptKey, "UTF-8"));
					}catch(Exception e){
						throw new Exception("Kwic seed128 encryption error");
					}
				}else if(cryptType == CRYPT_TYPE_KWICAES256){
					try{
						reqMap.put(fieldName, AESCipher.encode(fieldValue, cryptKey, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING));
					}catch(Exception e){
						throw new Exception("Kwic aes256 encryption error");
					}
				}
			}
		}
		
		return reqMap;
	}
	
	/**
	 * map을 xml로 변환
	 * @param reqMap
	 * @return
	 * @throws Exception
	 */
	private static String mapToXml(Map<String,Object> reqMap) throws Exception{
		JXParser reqParser	= new JXParser("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><INPUT></INPUT>");
		String fieldName = null;
		Iterator<String> iter = reqMap.keySet().iterator();
		while(iter.hasNext()){
			fieldName = iter.next();
			reqParser.addElement(reqParser.getRootElement(), fieldName).addAttribute(VALUE_ATTRIBUTE_NAME, String.valueOf(reqMap.get(fieldName)));
		}
		return reqParser.toString(null) + "  ";
	}
	
	/**
	 * 응답 데이터 parsing
	 * @param cryptType
	 * @param cryptKey
	 * @param responseContent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static KwResult parseResponse(int cryptType, String cryptKey, String responseContent){
		StringBuffer errLog	= new StringBuffer();
		
		boolean result 		= true;
		String errorCode 	= "0000";
		String errorMessage	= "";
		
		Map<String,Object> responseMap = null;
		
		try {
			try {
				responseMap		= new ObjectMapper().readValue(responseContent, HashMap.class);
			} catch (Exception e) {
				errorCode	 = "ERR_LIB_2000";
				errorMessage = "응답 데이터 Parsing 중 오류가 발생 하였습니다. 응답 데이터를 확인 해 주시기 바랍니다.";
				throw e;
			}
			
			try {
				responseMap	= decryptTraverse(String.valueOf(responseMap.get("MESSAGECODE")), String.valueOf(responseMap.get("SERVICECODE")), responseMap, cryptType, cryptKey);
			} catch (Exception e) {
				errorCode	= "ERR_LIB_2001";
				errorMessage	= "응답데이터 복호화 중 오류가 발생하였습니다.";
				throw e;
			}
		} catch (Exception e) {
			result	= false;
			errLog.append(ScrapUtil.getErrorStack(e));
			if(responseMap == null) {
				responseMap	= new HashMap<String,Object>();
			}
			if("000".equals(errorCode)){
				errorCode		= "ERR_LIB_2002";
				errorMessage	= "응답데이터 처리 중 오류가 발생하였습니다.";
			}
		}
		
		KwResult scrapResult	= new KwResult();
		scrapResult.setResult(result);
		scrapResult.setErrorCode(errorCode);
		scrapResult.setErrorMessage(errorMessage);
		scrapResult.setErrorStack(errLog.toString());
		scrapResult.setResponseMap(responseMap);
		
		return scrapResult;
	}
	
	private static final Map<String,Object> decryptTraverse(String MESSAGECODE, String SERVICECODE, Map<String,Object> responseMap, int cryptType, String cryptKey) throws Exception{
		if(cryptType == CRYPT_TYPE_NONE) {
			return responseMap;
		}

		if(MESSAGECODE == null || "".equals(SERVICECODE)) {
			return responseMap;
		}
		
		List<String> resEncFields	= Fields.getInstance().getResponseEncryptFields(MESSAGECODE, SERVICECODE);

		decryptTraverseMap("", responseMap, resEncFields, cryptType, cryptKey);

		return responseMap;
	}
	
	@SuppressWarnings("unchecked")
	private static void decryptTraverseMap(String parent, Map<String, Object> resMap, List<String> decFields, int cryptType, String cryptValue) throws Exception {
		String postfix = "".equals(parent) ? "" : String.format("%s.", parent);
		for(Entry<String, Object> entry : resMap.entrySet()) {
			String curName = String.format("%s%s", postfix, entry.getKey());
			if(entry.getValue() instanceof List) {
				decryptTraverseList(curName, (List<Map<String, Object>>) entry.getValue(), decFields, cryptType, cryptValue);
			}else if(entry.getValue() instanceof Map) {
				decryptTraverseMap(curName, (Map<String, Object>) entry.getValue(), decFields, cryptType, cryptValue);
			}else if(entry.getValue() instanceof String) {
				if(decFields.contains(entry.getKey())) {
					String decValue = KwRequest.decryptValue(String.valueOf(entry.getValue()), cryptType, cryptValue);
					resMap.put(entry.getKey(), decValue);
				}
			}
		}
	}

	private static void decryptTraverseList(String postfix, List<Map<String, Object>> value, List<String> decFields, int cryptType, String cryptValue) throws Exception {
		int i = 0;
		for(Object obj : value) {
			if(obj instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> item = (Map<String, Object>) obj;
				decryptTraverseMap(String.format("%s.%d", postfix, i++), item, decFields, cryptType, cryptValue);
			}
		}
	}
	
	public static final String decryptValue(String value, int cryptType, String cryptKey) throws Exception{
		if(cryptType == CRYPT_TYPE_NONE) {
			return value;
		}
		
		String decryptValue	= null;
		if(cryptType == CRYPT_TYPE_KWICSEED128){
			try{decryptValue	= new String(Crypto.decryptBytes(value, cryptKey, "UTF-8"), "UTF-8");}catch(Exception e){}
		}else if(cryptType == CRYPT_TYPE_KWICAES256){
			try{decryptValue	= AESCipher.decode(value, cryptKey, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);}catch(Exception e){}
		}
		
		//복호화한 후 base64 문자열을 UTF-8 평문으로 변환 (모든 문자는 base64로 인코딩 한 후  암호화한다. 복호화는 그 역순이다.)
		try{
			decryptValue	= new String(Base64.decodeBase64(decryptValue.getBytes("UTF-8")), "UTF-8");
		}catch(Exception e){
			decryptValue	= new String(Base64.decodeBase64(decryptValue.getBytes("UTF-8")));
		}
		return decryptValue;
	}
}
