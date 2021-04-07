package com.kwic.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;

public class FileIO {
	
	/**
	 * 파일을 이진 배열로 읽기
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static final byte[] getBinaryFile(File file) throws Exception {
		JOutputStream jos = null;
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			jos = new JOutputStream();
			jos.write(is);
			jos.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (is != null){
					is.close();
				}
			} catch (Exception ex) {
			}
			try {
				if (jos != null){
					jos.close();
				}
			} catch (Exception ex) {
			}
		}
		return jos.getBytes();
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

}
