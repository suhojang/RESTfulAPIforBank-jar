// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SBinaryEncoder.java

package com.kwic.security.base64;

// Referenced classes of package com.kwic.security.base64:
//            SEncoder, SEncoderException

public interface SBinaryEncoder extends SEncoder {

	public abstract byte[] encode(byte abyte0[]) throws SEncoderException;
}
