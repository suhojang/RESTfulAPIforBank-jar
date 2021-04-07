package com.kwic.telegram.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.kwic.telegram.web.wrapper.WebSSLClientWrapper;

public class JHttpClient {
	private HttpClient httpclient;
	public static final String RESPONSE_LINE = "\r\n";
	private static JHttpClient instance;

	private JHttpClient() {
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 600000);
		HttpConnectionParams.setSoTimeout(httpParams, 600000);
		this.httpclient = new DefaultHttpClient(httpParams);
		// this.httpclient = new DefaultHttpClient();
	}

	public static JHttpClient getInstance() {
		synchronized (JHttpClient.class) {
			if (instance == null) {
				instance = new JHttpClient();
			}
		}
		return instance;
	}

	public static JHttpClient newInstance() {
		return new JHttpClient();
	}

	public void close() {
		httpclient.getConnectionManager().shutdown();
	}

	/**
	 * multi-part
	 * @param webUrl
	 * @param textMap
	 * @param fileMap
	 * @return
	 * @throws ParseException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws HttpException
	 * @throws URISyntaxException
	 */
	public String connect(String webUrl, Map<String, String> textMap, Map<String, File> fileMap) throws ParseException, ClientProtocolException, IOException,
	                                                                                                    KeyManagementException, UnrecoverableKeyException, KeyStoreException, 
	                                                                                                    NoSuchAlgorithmException, CertificateException, HttpException, 
	                                                                                                    URISyntaxException {
		
		if (webUrl.toLowerCase().startsWith("https://")){
			return doMultipartSSL(webUrl, textMap, fileMap, null, null);
		}
		else {
			return doMultipart(webUrl, textMap, fileMap);
		}
	}

	/**
	 * 
	 * @param webUrl
	 * @param textMap
	 * @param fileMap
	 * @return
	 * @throws ParseException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws HttpException
	 * @throws URISyntaxException
	 */
	public String doMultipart(String webUrl, Map<String, String> textMap, Map<String, File> fileMap) throws ParseException, ClientProtocolException, IOException, 
	                                                                                                        KeyManagementException, UnrecoverableKeyException, KeyStoreException, 
	                                                                                                        NoSuchAlgorithmException, CertificateException, HttpException, 
	                                                                                                        URISyntaxException {
		if (fileMap == null){
			return connect(webUrl, null, textMap);
		}

		String host = webUrl.replaceAll("http://", "").replaceAll("https://", "");
		if (host.indexOf("/") >= 0) {
			host = host.substring(0, host.indexOf("/"));
		}

		int port = 80;
		if (host.indexOf(":") > 0) {
			port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
			host = host.substring(0, host.indexOf(":"));
		}
		HttpHost target = new HttpHost(host, port, webUrl.toLowerCase().startsWith("https://") ? "https" : "http");
		HttpRequestBase req = new HttpPost(webUrl);

		MultipartEntity reqEntity = new MultipartEntity();
		Iterator<?> iter = fileMap.keySet().iterator();
		String name = null;
		while (iter.hasNext()) {
			name = (String) iter.next();
			reqEntity.addPart(name, new FileBody(fileMap.get(name)));
		}

		iter = textMap.keySet().iterator();
		while (iter.hasNext()) {
			name = (String) iter.next();
			reqEntity.addPart(name, new StringBody(textMap.get(name)));
		}
		((HttpPost) req).setEntity(reqEntity);

		return response(httpclient.execute(target, req));
	}

	public String doMultipartSSL(String webUrl, Map<String, String> textMap, Map<String, File> fileMap, String keystorePath, String keyPass) throws ParseException, ClientProtocolException, IOException, 
	                                                                                                                                                KeyManagementException, UnrecoverableKeyException, KeyStoreException, 
	                                                                                                                                                NoSuchAlgorithmException, CertificateException, HttpException, 
	                                                                                                                                                URISyntaxException {
		if (fileMap == null){
			return connect(webUrl, null, textMap);
		}

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

		if (keystorePath != null && !"".equals(keystorePath) && keyPass != null && !"".equals(keyPass)) {
			FileInputStream instream = new FileInputStream(new File(keystorePath));
			try {
				trustStore.load(instream, keyPass.toCharArray());
			} finally {
				try {
					if(instream != null){
						instream.close();
					}
				} catch (Exception ignore) {
				}
			}
		}

		SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
		Scheme sch = new Scheme("https", 443, socketFactory);
		httpclient.getConnectionManager().getSchemeRegistry().register(sch);
		HttpPost httppost = new HttpPost(webUrl);

		MultipartEntity reqEntity = new MultipartEntity();
		Iterator<?> iter = fileMap.keySet().iterator();
		String name = null;
		while (iter.hasNext()) {
			name = (String) iter.next();
			reqEntity.addPart(name, new FileBody(fileMap.get(name)));
		}

		iter = textMap.keySet().iterator();
		while (iter.hasNext()) {
			name = (String) iter.next();
			reqEntity.addPart(name, new StringBody(textMap.get(name)));
		}
		httppost.setEntity(reqEntity);
		// =======================================================
		httpclient = WebSSLClientWrapper.wrapClient(httpclient);
		// =======================================================

		return response(httpclient.execute(httppost));
	}

	// plain
	public String connect(String webUrl, String sendData, Map<String, String> textMap) throws ParseException, ClientProtocolException, IOException, HttpException, KeyManagementException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, URISyntaxException {
		return connect(webUrl, sendData, textMap, "EUC-KR");
	}

	// plain
	public String connect(String webUrl, String sendData, Map<String, String> textMap, String encoding) throws ParseException, ClientProtocolException, IOException, HttpException, KeyManagementException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, URISyntaxException {
		if (sendData == null || "".equals(sendData)) {
			return doPost(webUrl, textMap, encoding);
		} else {
			return doGet(webUrl, sendData, textMap, encoding);
		}
	}

	// plain
	public String connect(String webUrl, String sendData, Map<String, String> textMap, String encoding, HttpContext context) throws ParseException, ClientProtocolException, IOException, HttpException, KeyManagementException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, URISyntaxException {
		if (sendData == null || "".equals(sendData)) {
			return doPost(webUrl, textMap, encoding, context);
		} else {
			return doGet(webUrl, sendData, textMap, encoding, context);
		}
	}

	// send plain post
	public String doPost(String webUrl, Map<String, String> textMap, String encoding) throws ParseException, ClientProtocolException, IOException, KeyManagementException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, URISyntaxException {
		if (webUrl.toLowerCase().startsWith("https://")) {
			return doSSLPost(webUrl, textMap, null, null, encoding);
		}

		String host = webUrl.replaceAll("http://", "").replaceAll("https://", "");
		if (host.indexOf("/") >= 0) {
			host = host.substring(0, host.indexOf("/"));
		}

		int port = 80;
		if (host.indexOf(":") > 0) {
			port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
			host = host.substring(0, host.indexOf(":"));
		}
		HttpHost target = new HttpHost(host, port, webUrl.toLowerCase().startsWith("https://") ? "https" : "http");
		HttpPost req = new HttpPost(webUrl);

		if (textMap != null) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			Iterator<?> iter = textMap.keySet().iterator();
			String paramName = null;
			while (iter.hasNext()) {
				paramName = (String) iter.next();
				nameValuePairs.add(new BasicNameValuePair(paramName, textMap.get(paramName)));
			}

			UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(nameValuePairs, encoding);// HTTP.UTF_8
			req.setEntity(reqEntity);
		}

		return response(httpclient.execute(target, req), encoding);
	}

	// send plain post
	public String doPost(String webUrl, Map<String, String> textMap, String encoding, HttpContext context) throws ParseException, ClientProtocolException, IOException, KeyManagementException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException,URISyntaxException {
		if (webUrl.toLowerCase().startsWith("https://")) {
			return doSSLPost(webUrl, textMap, null, null, encoding, context);
		}

		String host = webUrl.replaceAll("http://", "").replaceAll("https://", "");
		if (host.indexOf("/") >= 0) {
			host = host.substring(0, host.indexOf("/"));
		}

		int port = 80;
		if (host.indexOf(":") > 0) {
			port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
			host = host.substring(0, host.indexOf(":"));
		}
		HttpHost target = new HttpHost(host, port, webUrl.toLowerCase().startsWith("https://") ? "https" : "http");
		HttpPost req = new HttpPost(webUrl);

		if (textMap != null) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			Iterator<?> iter = textMap.keySet().iterator();
			String paramName = null;
			while (iter.hasNext()) {
				paramName = (String) iter.next();
				nameValuePairs.add(new BasicNameValuePair(paramName, textMap.get(paramName)));
			}

			UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(nameValuePairs, encoding);// HTTP.UTF_8
			req.setEntity(reqEntity);
		}

		return response(httpclient.execute(target, req, context), encoding);
	}

	// send plain get
	public String doGet(String webUrl, String sendData, Map<String, String> textMap, String encoding) throws IOException, HttpException, KeyManagementException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, ParseException, CertificateException {
		if (webUrl.toLowerCase().startsWith("https://")) {
			return doSSLGet(webUrl, sendData, textMap, null, null);
		}

		String paramString = encodeString(textMap);
		if (paramString == null || "".equals(paramString.trim())) {
			paramString = "";
		}
		else {
			paramString = "?" + paramString;
		}

		webUrl += paramString;

		String host = webUrl.replaceAll("http://", "").replaceAll("https://", "");
		if (host.indexOf("/") >= 0) {
			host = host.substring(0, host.indexOf("/"));
		}

		int port = 80;
		if (host.indexOf(":") > 0) {
			port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
			host = host.substring(0, host.indexOf(":"));
		}
		HttpHost target = new HttpHost(host, port, webUrl.toLowerCase().startsWith("https://") ? "https" : "http");

		SchemeRegistry supportedSchemes = new SchemeRegistry();
		supportedSchemes.register(new Scheme(webUrl.toLowerCase().startsWith("https://") ? "https" : "http", port, PlainSocketFactory.getSocketFactory()));
		HttpParams params = new SyncBasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUseExpectContinue(params, false);

		ClientConnectionOperator scop = new DefaultClientConnectionOperator(supportedSchemes);

		HttpRequest req = new BasicHttpRequest("GET", webUrl, HttpVersion.HTTP_1_1);
		req.addHeader("Host", target.getHostName());

		HttpContext ctx = new BasicHttpContext();

		OperatedClientConnection conn = scop.createConnection();

		PrintWriter pw = null;
		HttpResponse rsp = null;
		try {
			scop.openConnection(conn, target, null, ctx, params);
			conn.sendRequestHeader(req);

			conn.flush();

			pw = new PrintWriter(conn.getSocket().getOutputStream());
			// pw.print(RESPONSE_LINE);
			// pw.flush();
			pw.print(sendData);
			pw.flush();
			// pw.print(RESPONSE_LINE);
			pw.flush();

			conn.flush();

			rsp = conn.receiveResponseHeader();
			conn.receiveResponseEntity(rsp);

		} finally {
			try {
				if(pw != null){
					pw.close();
				}
			} catch (Exception ex) {
			}
			try {
				if(conn != null){
					conn.close();
				}
			} catch (Exception ex) {
			}
		}
		return response(rsp, encoding);
	}

	// send plain get
	public String doGet(String webUrl, String sendData, Map<String, String> textMap, String encoding, HttpContext context) throws IOException, HttpException, KeyManagementException, UnrecoverableKeyException,	KeyStoreException, NoSuchAlgorithmException, ParseException, CertificateException {
		if (webUrl.toLowerCase().startsWith("https://")) {
			return doSSLGet(webUrl, sendData, textMap, null, null);
		}

		String paramString = encodeString(textMap);
		if (paramString == null || "".equals(paramString.trim())) {
			paramString = "";
		}
		else {
			paramString = "?" + paramString;
		}

		webUrl += paramString;

		String host = webUrl.replaceAll("http://", "").replaceAll("https://", "");
		if (host.indexOf("/") >= 0) {
			host = host.substring(0, host.indexOf("/"));
		}

		int port = 80;
		if (host.indexOf(":") > 0) {
			port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
			host = host.substring(0, host.indexOf(":"));
		}
		HttpHost target = new HttpHost(host, port, webUrl.toLowerCase().startsWith("https://") ? "https" : "http");

		SchemeRegistry supportedSchemes = new SchemeRegistry();
		supportedSchemes.register(new Scheme(webUrl.toLowerCase().startsWith("https://") ? "https" : "http", port, 	PlainSocketFactory.getSocketFactory()));
		HttpParams params = new SyncBasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUseExpectContinue(params, false);

		ClientConnectionOperator scop = new DefaultClientConnectionOperator(supportedSchemes);

		HttpRequest req = new BasicHttpRequest("GET", webUrl, HttpVersion.HTTP_1_1);
		req.addHeader("Host", target.getHostName());

		OperatedClientConnection conn = scop.createConnection();

		PrintWriter pw = null;
		HttpResponse rsp = null;
		try {
			scop.openConnection(conn, target, null, context, params);
			conn.sendRequestHeader(req);

			conn.flush();

			pw = new PrintWriter(conn.getSocket().getOutputStream());
			// pw.print(RESPONSE_LINE);
			// pw.flush();
			pw.print(sendData);
			pw.flush();
			// pw.print(RESPONSE_LINE);
			pw.flush();

			conn.flush();

			rsp = conn.receiveResponseHeader();
			conn.receiveResponseEntity(rsp);

		} finally {
			try {
				if(pw != null) {
					pw.close();
				}
			} catch (Exception ex) {
			}
			try {
				if(conn != null){
					conn.close();
				}
			} catch (Exception ex) {
			}
		}
		return response(rsp, encoding);
	}

	// send ssl
	public String doSSLGet(String webUrl, String sendData, Map<String, String> textMap, String keystorePath, String keyPass) throws KeyStoreException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, ParseException, ClientProtocolException, IOException, CertificateException, HttpException {
		String paramString = encodeString(textMap);
		if (paramString == null || "".equals(paramString.trim())) {
			paramString = "";
		}
		else {
			paramString = "?" + paramString;
		}

		webUrl += paramString;

		String host = webUrl.replaceAll("http://", "").replaceAll("https://", "");
		if (host.indexOf("/") >= 0) {
			host = host.substring(0, host.indexOf("/"));
		}

		int port = 80;
		if (host.indexOf(":") > 0) {
			port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
			host = host.substring(0, host.indexOf(":"));
		}
		HttpHost target = new HttpHost(host, port, webUrl.toLowerCase().startsWith("https://") ? "https" : "http");

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		if (keystorePath != null && !"".equals(keystorePath) && keyPass != null && !"".equals(keyPass)) {
			FileInputStream instream = new FileInputStream(new File(keystorePath));
			try {
				trustStore.load(instream, keyPass.toCharArray());
			} finally {
				try {
					if(instream != null){
						instream.close();
					}
				} catch (Exception ignore) {
				}
			}
		}
		SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
		Scheme sch = new Scheme("https", 443, socketFactory);

		SchemeRegistry supportedSchemes = new SchemeRegistry();
		supportedSchemes.register(sch);
		HttpParams params = new SyncBasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUseExpectContinue(params, false);

		// =======================================================
		supportedSchemes = WebSSLClientWrapper.wrapClient(supportedSchemes);
		// =======================================================

		ClientConnectionOperator scop = new DefaultClientConnectionOperator(supportedSchemes);

		HttpRequest req = new BasicHttpRequest("GET", webUrl, HttpVersion.HTTP_1_1);
		req.addHeader("Host", target.getHostName());

		HttpContext ctx = new BasicHttpContext();

		OperatedClientConnection conn = scop.createConnection();

		PrintWriter pw = null;
		HttpResponse rsp = null;
		try {
			scop.openConnection(conn, target, null, ctx, params);
			conn.sendRequestHeader(req);
			conn.flush();
			pw = new PrintWriter(conn.getSocket().getOutputStream());
			// pw.print(RESPONSE_LINE);
			// pw.flush();
			pw.print(sendData);
			pw.flush();

			conn.flush();

			rsp = conn.receiveResponseHeader();
			conn.receiveResponseEntity(rsp);

		} finally {
			try {
				if(pw != null){
					pw.close();
				}
			} catch (Exception ex) {
			}
			try {
				if(conn != null){
					conn.close();
				}
			} catch (Exception ex) {
			}
		}
		return response(rsp);

	}

	// send ssl
	public String doSSLPost(String webUrl, Map<String, String> textMap, String keystorePath, String keyPass, String encoding) throws KeyStoreException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, ParseException, ClientProtocolException, IOException, CertificateException, URISyntaxException {
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

		if (keystorePath != null && !"".equals(keystorePath) && keyPass != null && !"".equals(keyPass)) {
			FileInputStream instream = new FileInputStream(new File(keystorePath));
			try {
				trustStore.load(instream, keyPass.toCharArray());
			} finally {
				try {
					if(instream != null){
						instream.close();
					}
				} catch (Exception ignore) {
				}
			}
		}

		SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
		Scheme sch = new Scheme("https", 443, socketFactory);
		httpclient.getConnectionManager().getSchemeRegistry().register(sch);

		// HttpPost httppost1 = new HttpPost(webUrl);//webUrl
		// String Path = httppost1.getURI().getPath();
		// String Authority = httppost1.getURI().getAuthority();
		// String Fragment = httppost1.getURI().getFragment();
		// String Host = httppost1.getURI().getHost();
		// String Query = httppost1.getURI().getQuery();
		// String Scheme = httppost1.getURI().getScheme();
		//
		// System.out.println("Path : "+Path);
		// System.out.println("Authority : "+Authority);
		// System.out.println("Fragment : "+Fragment);
		// System.out.println("Host : "+Host);
		// System.out.println("Query : "+Query);
		// System.out.println("Scheme : "+Scheme);
		//
		// URI uri = new URI(
		// httppost1.getURI().getScheme()
		// ,httppost1.getURI().getUserInfo()
		// ,"bobank.kbstar.com"
		// ,httppost1.getURI().getPort()
		// ,httppost1.getURI().getPath()
		// //"/quics?chgCompId=b028770&baseCompId=b028702&page=C025255&cc=b028702:b028770"
		// ,httppost1.getURI().getQuery()
		// ,httppost1.getURI().getFragment()
		// );
		URI uri = new URI(webUrl);

		HttpPost httppost = new HttpPost(uri);
		// Path = httppost.getURI().getPath();
		// Authority = httppost.getURI().getAuthority();
		// Fragment = httppost.getURI().getFragment();
		// Host = httppost.getURI().getHost();
		// Query = httppost.getURI().getQuery();
		// Scheme = httppost.getURI().getScheme();

		// System.out.println("Path : "+Path);
		// System.out.println("Authority : "+Authority);
		// System.out.println("Fragment : "+Fragment);
		// System.out.println("Host : "+Host);
		// System.out.println("Query : "+Query);
		// System.out.println("Scheme : "+Scheme);

		if (textMap != null) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			Iterator<?> iter = textMap.keySet().iterator();
			String paramName = null;
			while (iter.hasNext()) {
				paramName = (String) iter.next();
				nameValuePairs.add(new BasicNameValuePair(paramName, textMap.get(paramName)));
			}

			UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(nameValuePairs, encoding);// HTTP.UTF_8
			httppost.setEntity(reqEntity);
		}
		// PostMethod method = new PostMethod();
		// method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

		BasicHttpContext context = new BasicHttpContext();
		CookieStore store = new BasicCookieStore();
		context.setAttribute(ClientContext.COOKIE_STORE, store);

		httpclient = WebSSLClientWrapper.wrapClient(httpclient);

		return response(httpclient.execute(httppost, context), encoding);
	}

	// send ssl
	public String doSSLPost(String webUrl, Map<String, String> textMap, String keystorePath, String keyPass, String encoding, HttpContext context) throws KeyStoreException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, ParseException, ClientProtocolException, IOException, CertificateException, URISyntaxException {
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

		if (keystorePath != null && !"".equals(keystorePath) && keyPass != null && !"".equals(keyPass)) {
			FileInputStream instream = new FileInputStream(new File(keystorePath));
			try {
				trustStore.load(instream, keyPass.toCharArray());
			} finally {
				try {
					if(instream != null){
						instream.close();
					}
				} catch (Exception ignore) {
				}
			}
		}

		SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
		Scheme sch = new Scheme("https", 443, socketFactory);
		httpclient.getConnectionManager().getSchemeRegistry().register(sch);

		URI uri = new URI(webUrl);

		HttpPost httppost = new HttpPost(uri);

		if (textMap != null) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			Iterator<?> iter = textMap.keySet().iterator();
			String paramName = null;
			while (iter.hasNext()) {
				paramName = (String) iter.next();
				nameValuePairs.add(new BasicNameValuePair(paramName, textMap.get(paramName)));
			}

			UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(nameValuePairs, encoding);// HTTP.UTF_8
			httppost.setEntity(reqEntity);
		}
		// PostMethod method = new PostMethod();
		// method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

		httpclient = WebSSLClientWrapper.wrapClient(httpclient);

		return response(httpclient.execute(httppost, context), encoding);
	}

	public String response(HttpResponse rsp) throws ParseException, IOException {
		return response(rsp, "UTF-8", false);
	}

	public String response(HttpResponse rsp, String encoding) throws ParseException, IOException {
		return response(rsp, encoding, false);
	}

	public String response(HttpResponse rsp, String encoding, boolean containHeader) throws ParseException, IOException {
		StringBuffer sb = new StringBuffer();
		Header[] headers = rsp.getAllHeaders();
		BufferedReader br = null;
		String line = null;
		try {
			if (containHeader){
				sb.append(rsp.getStatusLine()).append(RESPONSE_LINE);
			}

			if (containHeader) {
				for (int i = 0; i < headers.length; i++) {
					sb.append(headers[i].toString()).append(RESPONSE_LINE);
				}
			}
			
			if (containHeader) {
				sb.append(RESPONSE_LINE);
			}

			if (rsp.getEntity() != null) {
				br = new BufferedReader(new InputStreamReader(rsp.getEntity().getContent(), encoding));

				while ((line = br.readLine()) != null) {
					sb.append(line).append(System.getProperty("line.separator"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception ex) {
			}
		}
		return sb.toString();
	}

	private static String encodeString(Map<String, String> paramMap) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		String[] obj = paramMap.keySet().toArray(new String[paramMap.size()]);

		for (int i = 0; i < obj.length; i++) {
			if (obj[i] == null || "".equals(obj[i].trim()) || paramMap.get(obj[i]) == null) {
				continue;
			}
			sb.append(URLEncoder.encode((String) obj[i], System.getProperty("file.encoding")));
			sb.append('=');
			sb.append(URLEncoder.encode(paramMap.get(obj[i]), System.getProperty("file.encoding")));
			if (i < obj.length - 1) {
				sb.append('&');
			}
		}
		return sb.toString();
	}

}
