package com.kwic.support.client;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

/**<pre>

 * @since 1.6
 * */
public class KwResult implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("responseMap=").append(responseMap).append("\n");
		
		return sb.toString();
	}
	
	/**
	 * 조회결과
	 */
	private boolean result;
	
	/**
	 * 오류코드 (정상=000, 그외=오류)
	 */
	private String errorCode;
	
	/**
	 * 오류 메시지
	 */
	private String errorMessage;
	
	private Map<String,Object> responseMap;
	
	/**
	 * 오류스택
	 */
	private String errorStack;
	
	/**
	 * 응답전문 json
	 */
	private String responseJson;
	
	
	/**<pre>
	 * 조회결과를 저장합니다.
	 * 진위여부를 판단하는 업무의 진위여부가 반영되어 있습니다.
	 * 그외 다건의 응답데이터가 있는 업무의 경우 진행과정의 오류여부를 저장합니다.
	 * </pre>
	 * @param result boolean 오류여부,진위여부
	 * */
	public void setResult(boolean result){
		this.result	= result;
	}
	
	/**<pre>
	 * 조회결과를 반환합니다.
	 * 진위여부를 판단하는 업무의 진위여부가 반영되어 있습니다.
	 * 그외 다건의 응답데이터가 있는 업무의 경우 진행과정의 오류여부를 반환합니다.
	 * </pre>
	 * @return boolean 오류여부,진위여부
	 * */
	public boolean getResult(){
		return result;
	}
	
	/**<pre>
	 * 비정상 실행 시의 오류코드를 저장합니다.
	 * 정상 실행 시 오류코드는 000입니다.
	 * 오류코드는 동일한 오류메시지에서도 발생지점을 판별할 수 있는 3자리 고유번호로 구성됩니다.
	 * </pre>
	 * @param errorCode String 오류코드
	 * */
	public void setErrorCode(String errorCode){
		this.errorCode	= errorCode;
	}
	
	/**<pre>
	 * 비정상 실행 시의 오류코드를 반환합니다.
	 * 정상 실행 시 오류코드는 000입니다.
	 * 오류코드는 동일한 오류메시지에서도 발생지점을 판별할 수 있는 3자리 고유번호로 구성됩니다.
	 * </pre>
	 * @return String 오류코드
	 * */
	public String getErrorCode(){
		return errorCode;
	}
	
	/**<pre>
	 * 비정상 실행 시의 오류메시지를 저장합니다.
	 * 정상 실행 시 오류메시지는 ""입니다.
	 * 오류 발생지점을 정확히 판단하기 위해서는 오류코드가 있어야만 합니다.
	 * </pre>
	 * @param errorMessage String 오류메시지
	 * */
	public void setErrorMessage(String errorMessage){
		this.errorMessage	= errorMessage;
	}
	
	/**<pre>
	 * 비정상 실행 시의 오류메시지를 반환합니다.
	 * 정상 실행 시 오류메시지는 ""입니다.
	 * 오류 발생지점을 정확히 판단하기 위해서는 오류코드가 있어야만 합니다.
	 * </pre>
	 * @return String 오류메시지
	 * */
	public String getErrorMessage(){
		return errorMessage;
	}
	
	/**<pre>
	 * 비정상 실행 시의 stacktrace를 저장합니다.
	 * 정상적인 예외처리의 경우 오류스택이 없을 수 있습니다.
	 * </pre>
	 * @param errorStack String 오류스택 문자열
	 * */
	public void setErrorStack(String errorStack){
		this.errorStack	= errorStack;
	}
	
	/**<pre>
	 * 비정상 실행 시의 stacktrace를 반환합니다.
	 * 정상적인 예외처리의 경우 오류스택이 없을 수 있습니다.
	 * </pre>
	 * @return String 오류스택 문자열
	 * */
	public String getErrorStack(){
		return errorStack;
	}
	
	/**<pre>
	 * 스크래핑 Relay server로 부터 응답받은 xml 원문을 Map&lt;String,Object&gt;로 변환한 객체를 저장합니다. 
	 * Object는 java.util.List, java.util.Map&lt;String,Object&gt;, String의 타입이 포함되어 있습니다.
	 * Java Application에서 응답에 대한 처리 간소화를 위해 사용합니다.
	 * </pre>
	 * @param responseMap Map&lt;String,Object&gt; 응답결과 collection object
	 * */
	public void setResponseMap(Map<String,Object> responseMap){
		this.responseMap	= responseMap;
	}
	
	/**<pre>
	 * 스크래핑 Relay server로 부터 응답받은 xml 원문을 Map&lt;String,Object&gt;로 변환한 객체입니다. 
	 * Object는 java.util.List, java.util.Map&lt;String,Object&gt;, String의 타입이 포함되어 있습니다.
	 * Java Application에서 응답에 대한 처리 간소화를 위해 사용합니다.
	 * </pre>
	 * @return Map&lt;String,Object&gt; 응답결과 collection object
	 * */
	public Map<String,Object> getResponseMap(){
		return responseMap;
	}
	
	/**<pre>
	 * 스크래핑 Relay server로 부터 응답받은 xml 원문을 JSON형태로 변환한 문자열입니다. 
	 * </pre>
	 * @return String 응답결과 json 문자열
	 * */
	public String getResponseJson(){
		//for jackson-mapper-asl-1.1.1.jar
		StringWriter sw	= new StringWriter();
		try{
			new ObjectMapper().writeValue(sw, responseMap);
			responseJson	= sw.toString();
		}catch(Exception e){
			System.out.println(String.format("JSON 변환 실패. responseMap=%s, error=%s", responseMap, e.getMessage()));
			
		}
		return responseJson;
	}
}
