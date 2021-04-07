package com.kwic.license;

import java.io.InputStream;

import org.dom4j.Element;

import com.kwic.io.JOutputStream;
import com.kwic.util.StringUtil;
import com.kwic.xml.parser.JXParser;

public class LicenseLoader {
	private static LicenseLoader instance;
	
	private JXParser requestJxp;
	private String[] macAddrs;
	
	private static final String _CODE	= "code";
	
	private LicenseLoader() throws Exception {
		String encoding = System.getProperty("file.encoding");
		if (encoding == null || "".equals(encoding)){
			encoding = "UTF8";
		}else{
			encoding = StringUtil.replace(encoding.toUpperCase(), "-", "");
		}
		String licenseXml = "com/kwic/license/license.xml";

		if (!"UTF8".equals(encoding)) {
			licenseXml = "com/kwic/license/license.kr.xml";
		}
		requestJxp	= load(licenseXml);
		macAddrs	= loadMac(_CODE);
	}
	
	public static LicenseLoader getInstance() throws Exception {
		synchronized (LicenseLoader.class) {
			if (instance == null)
				instance = new LicenseLoader();
		}
		return instance;
	}
	
	private JXParser load(String path) throws Exception {
		JXParser jxp 		= null;
		InputStream is 		= null;
		JOutputStream jos 	= null;
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
	
	private String[] loadMac(String code) throws Exception {
		String element		= requestJxp.getAttribute("//license/business", code);
		Element[] macArr	= requestJxp.getElements("//license/" + element + "/mac");
		String[] macs		= new String[macArr.length];
		
		for (int i = 0; i < macArr.length; i++) {
			macs[i]		= requestJxp.getAttribute(macArr[i], "value"); 
		}
		
		return macs;
	}
	
	public String[] getLicenseMac(){
		return macAddrs;
	}
	
}
