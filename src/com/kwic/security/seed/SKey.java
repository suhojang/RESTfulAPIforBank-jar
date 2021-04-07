// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SKey.java

package com.kwic.security.seed;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Referenced classes of package com.kwic.security.seed:
//            Seed128

public class SKey {

	public SKey() {
	}

	public static int[] getSeedKey(String password) throws Exception {
		MessageDigest md = null;
		byte pbUserKey[] = null;
		int pdwRoundKey[] = new int[32];
		try {
			md = MessageDigest.getInstance("SHA-256");
			pbUserKey = md.digest(password.getBytes("utf-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("[[FAIL]] [SEED128:KEY] - no such algorithm!!!!");
		} catch (UnsupportedEncodingException e) {
			throw new Exception("[[FAIL]] [SEED128:KEY] - unsupported encoding!!!!");
		}
		Seed128.SeedRoundKey(pdwRoundKey, pbUserKey);
		return pdwRoundKey;
	}

	public static int[] getNoSeedKey(String password) throws Exception {
		int pdwRoundKey[] = new int[32];
		Seed128.SeedRoundKey(pdwRoundKey, password.getBytes());
		return pdwRoundKey;
	}
}
