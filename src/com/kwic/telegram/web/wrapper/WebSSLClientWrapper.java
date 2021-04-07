package com.kwic.telegram.web.wrapper;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

public class WebSSLClientWrapper {
	
	/**
	 * 
	 * @param base
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public static HttpClient wrapClient(HttpClient base) throws KeyManagementException, NoSuchAlgorithmException {
		SSLContext ctx = SSLContext.getInstance("TLS");
		X509TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		ctx.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx);
		ClientConnectionManager ccm = base.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme("https", 443, ssf));

		return new DefaultHttpClient(ccm, base.getParams());
	}

	/**
	 * 
	 * @param sr
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public static SchemeRegistry wrapClient(SchemeRegistry sr) throws KeyManagementException, NoSuchAlgorithmException {
		SSLContext ctx = SSLContext.getInstance("TLS");
		X509TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		ctx.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx);
		sr.register(new Scheme("https", 443, ssf));

		return sr;
	}

}
