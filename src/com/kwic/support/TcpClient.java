package com.kwic.support;

import com.kwic.telegram.tcp.JTcpManager;

public class TcpClient {
	
	public static final String DEFAULT_ENCODING = "UTF-8";

	public static String connect(String ip, int port, String message) throws Exception {
		return new String(JTcpManager.getInstance().sendMessage(ip, port, message.getBytes(), true), DEFAULT_ENCODING);
	}

	public static String connect(String ip, int port, String message, String encoding) throws Exception {
		return new String(JTcpManager.getInstance().sendMessage(ip, port, message.getBytes(), true), encoding);
	}

	public static String connect(String ip, int port, String message, String outEncoding, String inEncoding) throws Exception {
		return new String(JTcpManager.getInstance().sendMessage(ip, port, message.getBytes(outEncoding), true), inEncoding);
	}

	public static void main(String[] args) throws Exception {
	}
}
