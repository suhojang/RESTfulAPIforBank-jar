// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SPadding.java

package com.kwic.security.seed;

public class SPadding {
	
	@SuppressWarnings("unused")
	private static final byte PADDING_VALUE = 0;

	public SPadding() {
	}

	/**
	 * 패딩 추가
	 * @param source
	 * @param blockSize
	 * @return
	 */
	public static byte[] addPadding(byte source[], int blockSize) {
		int paddingCount = blockSize - source.length % blockSize;
		if (paddingCount == 0 || paddingCount == blockSize){
			return source;
		}
		byte buffer[] = new byte[source.length + paddingCount];
		System.arraycopy(source, 0, buffer, 0, source.length);
		for (int i = 0; i < paddingCount - 1; i++){
			buffer[source.length + i] = 0;
		}

		return buffer;
	}

	/**
	 * 패딩 제거
	 * @param source
	 * @param blockSize
	 * @return
	 */
	public static byte[] removePadding(byte source[], int blockSize) {
		int paddingCount = source[source.length - 1];
		if (paddingCount >= blockSize - 1){
			return source;
		}
		int zeroPaddingCount = paddingCount - 1;
		for (int i = 2; i < zeroPaddingCount + 2; i++)
			if (source[source.length - i] != 0){
				return source;
			}

		if (source.length % blockSize == 0 && paddingCount < 0) {
			return source;
		} else {
			byte buffer[] = new byte[source.length - paddingCount];
			System.arraycopy(source, 0, buffer, 0, buffer.length);
			return buffer;
		}
	}
}
