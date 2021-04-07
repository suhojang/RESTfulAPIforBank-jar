// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SeedUtil.java

package com.kwic.security;

import com.kwic.security.base64.SBase64;
import com.kwic.security.seed.SKey;
import com.kwic.security.seed.SPadding;
import com.kwic.security.seed.Seed128;
import java.io.UnsupportedEncodingException;

public class SeedUtil {

	public SeedUtil() {
	}

	/**
	 * 암호화
	 * @param record
	 * @param password
	 * @param charsetName
	 * @param isBase64
	 * @param isString 암호화 여부 false 일때 암호화
	 * @return
	 * @throws Exception
	 */
	public Object encrypt(String record, String password, String charsetName, boolean isBase64, boolean isString) throws Exception {
		return isString ? new String(encrypt(record, password, charsetName, isBase64), charsetName)	: encrypt(record, password, charsetName, isBase64);
	}

	/**
	 * 복호화
	 * @param record
	 * @param password
	 * @param charsetName
	 * @param isBase64
	 * @param isString
	 * @return
	 * @throws Exception 복호화 여부 false 일때 복호화
	 */
	public Object decrypt(String record, String password, String charsetName, boolean isBase64, boolean isString) throws Exception {
		return isString ? new String(decrypt(record, password, charsetName, isBase64), charsetName) : decrypt(record, password, charsetName, isBase64);
	}

	/**
	 * 암호화
	 * @param record
	 * @param password
	 * @param charsetName
	 * @param isBase64
	 * @return
	 * @throws Exception
	 */
	public byte[] encrypt(String record, String password, String charsetName, boolean isBase64) throws Exception {
		return isBase64 ? SBase64.encodeBase64(encrypt(record, password, charsetName)) : encrypt(record, password, charsetName);
	}

	/**
	 * 복호화
	 * @param record
	 * @param password
	 * @param charsetName
	 * @param isBase64
	 * @return
	 * @throws Exception
	 */
	public byte[] decrypt(String record, String password, String charsetName, boolean isBase64) throws Exception {
		return isBase64 ? decrypt(SBase64.decodeBase64(record.getBytes(charsetName)), password, charsetName) : decrypt(record.getBytes(), password, charsetName);
	}

	/**
	 * 암호화
	 * @param record
	 * @param password
	 * @param charsetName
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public byte[] encrypt(String record, String password, String charsetName) throws UnsupportedEncodingException {
		byte src[] = null;
		if (charsetName == null){
			src = SPadding.addPadding(record.getBytes(), 16);
		} else {
			src = SPadding.addPadding(record.getBytes(charsetName), 16);
		}
		int pdwRoundKey[] = new int[32];
		Seed128.SeedRoundKey(pdwRoundKey, password.getBytes());
		int block = src.length / 16;
		for (int i = 0; i < block; i++) {
			byte pbData[] = new byte[16];
			byte outData[] = new byte[16];
			System.arraycopy(src, i * 16, pbData, 0, 16);
			Seed128.SeedEncrypt(pbData, pdwRoundKey, outData);
			System.arraycopy(outData, 0, src, i * 16, 16);
		}

		return src;
	}

	/**
	 * 복호화
	 * @param src
	 * @param password
	 * @param charsetName
	 * @return
	 * @throws Exception
	 */
	public byte[] decrypt(byte src[], String password, String charsetName) throws Exception {
		int block = src.length / 16;
		for (int i = 0; i < block; i++) {
			byte pbData[] = new byte[16];
			byte outData[] = new byte[16];
			System.arraycopy(src, i * 16, pbData, 0, 16);
			Seed128.SeedDecrypt(pbData, SKey.getNoSeedKey(password), outData);
			System.arraycopy(outData, 0, src, i * 16, 16);
		}

		byte bytes[] = SPadding.removePadding(src, 16);
		return bytes;
	}
}
