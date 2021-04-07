package com.kwic.license;

import java.net.InetAddress;
import java.net.NetworkInterface;

import org.apache.commons.codec.binary.Base64;

import com.kwic.verify.DateVerity;

public class KwicLicense {
	private static KwicLicense instance;
	
	private LicenseLoader loader;
	private static String[] macAddrs;	
	
	public static final boolean _MAC_LICENSE	= true;
	
	private static boolean auth		= false;
	
	public KwicLicense() throws Exception {
		loader		= LicenseLoader.getInstance();
		macAddrs	= loader.getLicenseMac();
	}
	
	public static KwicLicense getInstance() throws Exception {
		synchronized (KwicLicense.class) {
			if (instance == null)
				instance = new KwicLicense();
		}
		return instance;
	}
	
	private static String getMacAddress() throws Exception{
		StringBuffer sb	= new StringBuffer();
		try{
			InetAddress			ip		= InetAddress.getLocalHost();
			NetworkInterface	netif	= NetworkInterface.getByInetAddress(ip);
			byte[] 				mac		= netif.getHardwareAddress();	//핵사값 반환
			
			for (byte b : mac) {
				sb.append((sb.length()==0?"":"-")+String.format("%02X", b));
			}
		}catch(Exception e){
			throw e;
		}
		return sb.toString();
	}
	
	public boolean right(boolean isMac) throws Exception {
		if (isMac) {
			String localMac		= getMacAddress().toUpperCase();
			for (String mac : macAddrs) {
				if(new String(Base64.decodeBase64(mac)).toUpperCase().indexOf("/"+localMac+"/")>=0) {
					auth	= true;
					break;
				}
			}
		} else {
			auth	= DateVerity.verify();
		}
		return auth;
	}
}
