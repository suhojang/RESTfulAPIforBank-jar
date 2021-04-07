package com.kwic.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class DebugUtil {
	
	private static boolean DEBUG = true;

	/**
	 * HttpServletRequest 객체 디버깅
	 * @param request
	 */
	public static  void printRequest(HttpServletRequest request){
		if(!DEBUG){
			return;
		}
		Enumeration<String> anames = request.getAttributeNames();
		StringBuffer buffer = new StringBuffer();
		buffer.append("Request info\n");
		while(anames.hasMoreElements()){
			String name = anames.nextElement();
			buffer.append(name).append("=").append(request.getAttribute(name));
		}
		
		Enumeration<String> pnames = request.getParameterNames();
		while(anames.hasMoreElements()){
			String name = pnames.nextElement();
			buffer.append(name).append("=").append(request.getParameter(name));
		}
	}
	
	public static void printMap(Map<String,?> map){
		if(!DEBUG){
			return;
		}
		
		Iterator<String> iterator = map.keySet().iterator();
		StringBuffer buffer = new StringBuffer();
		buffer.append("Map info\n");
		while(iterator.hasNext()){
			String name = iterator.next();
			buffer.append(name).append("=").append(map.get(name)).append("\n");
		}
	}
	
	
}
