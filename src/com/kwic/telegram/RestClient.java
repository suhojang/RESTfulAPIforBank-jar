package com.kwic.telegram;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class RestClient {
	public static final String _LINE	= System.getProperty("line.separator");
	private static final int _TIMEOUT	= 10 * 1000;
	
	public static String sendJSON(String url, String json) throws Exception{
		return sendJSON(url, json, "UTF-8");
	}
	public static String sendJSON(String url, String json, String encoding) throws Exception{
		
        HttpClient		client	= HttpClientBuilder.create().build();
        HttpPost		post	= new HttpPost(url);
        
        RequestConfig config	= RequestConfig.custom()
        		.setSocketTimeout(_TIMEOUT)
        		.setConnectTimeout(_TIMEOUT)
        		.setConnectionRequestTimeout(_TIMEOUT)
        		.build();
        
        post.setConfig(config);
        post.addHeader("Content-Type", "application/json");
        StringEntity	se		= new StringEntity(json, encoding);        
        post.setEntity(se);
        
        HttpResponse	response	= client.execute(post);
        BufferedReader	br			= null;
        StringBuffer	sb			= new StringBuffer();
        try{
        	br	= new BufferedReader(new InputStreamReader(response.getEntity().getContent(),encoding));
            String line		= "";
            while ((line = br.readLine()) != null) {
            	sb.append(line).append(_LINE);
            }
        }catch(Exception e){
        	throw e;
        }finally{
        	try{if(br!=null)br.close();}catch(Exception e){br=null;}
        }
        return sb.toString();
	}
}
