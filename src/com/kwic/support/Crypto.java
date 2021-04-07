package com.kwic.support;

import com.kwic.security.SeedUtil;

public class Crypto {
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	private static String paddingKey(String key) {
		byte bytes[] = key.getBytes();
		int len = bytes.length / 16;
		if (bytes.length % 16 != 0){
			len++;
		}
		byte returnBytes[] = new byte[16 * len];
		System.arraycopy(bytes, 0, returnBytes, 0, bytes.length);
		for (int i = bytes.length; i < returnBytes.length; i++){
			returnBytes[i] = 49;
		}

		return new String(returnBytes);
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	private static byte[] trim(byte bytes[]) {
		int idx = bytes.length;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] != 0){
				continue;
			}
			idx = i;
			break;
		}

		byte returnBytes[] = new byte[idx];
		System.arraycopy(bytes, 0, returnBytes, 0, idx);
		return returnBytes;
	}

	/**
	 * 
	 * @param plain
	 * @param key
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptBytes(String plain, String key, String encoding) throws Exception {
		if (plain == null || plain.getBytes().length == 0) {
			return plain.getBytes();
		} else {
			key = paddingKey(key);
			SeedUtil util = new SeedUtil();
			byte encrypt[] = util.encrypt(plain, key, encoding, true);
			return encrypt;
		}
	}

	/**
	 * 
	 * @param plain
	 * @param key
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String plain, String key, String encoding) throws Exception {
		if (plain == null || plain.getBytes().length == 0) {
			return plain;
		} else {
			key = paddingKey(key);
			SeedUtil util = new SeedUtil();
			byte encrypt[] = util.encrypt(plain, key, encoding, true);
			return new String(encrypt);
		}
	}

	/**
	 * 
	 * @param encrypt
	 * @param key
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptBytes(String encrypt, String key, String encoding) throws Exception {
		if (encrypt == null || encrypt.getBytes().length == 0) {
			return encrypt.getBytes();
		} else {
			key = paddingKey(key);
			SeedUtil util = new SeedUtil();
			byte decrypt[] = util.decrypt(encrypt, key, encoding, true);
			return trim(decrypt);
		}
	}

	/**
	 * 
	 * @param encrypt
	 * @param key
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String encrypt, String key, String encoding) throws Exception {
		if (encrypt == null || encrypt.getBytes().length == 0) {
			return encrypt;
		} else {
			key = paddingKey(key);
			SeedUtil util = new SeedUtil();
			byte decrypt[] = util.decrypt(encrypt, key, encoding, true);
			return new String(trim(decrypt));
		}
	}

	public static void main(String[] args) throws Exception {
	}
}
