package com.kwic.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;

public class JFile extends File {

	private static final long serialVersionUID = 1L;

	public JFile(String fileFullPath) {
		super(fileFullPath);
	}

	public JFile(File file, String s) {
		super(file, s);
	}

	public JFile(String s1, String s2) {
		super(s1, s2);
	}

	public JFile(URI uri) {
		super(uri);
	}

	public byte[] getBytes() throws Exception {
		JOutputStream jos = null;
		InputStream is = null;

		try {
			jos = new JOutputStream();
			is = new FileInputStream(this);
			jos.write(is);
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
		return jos.getBytes();
	}

}
