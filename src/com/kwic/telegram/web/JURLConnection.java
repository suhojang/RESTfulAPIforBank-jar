package com.kwic.telegram.web;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;

import com.kwic.io.JOutputStream;

/**
 * <pre>
* Cilent 에서 서버로의 URLConnection을 담당
 * </pre>
 *
 * @author Jang,Junghoon
 * @since 1.3.*
 * @see com.kwic.conf.TelegramConfig
 */
public class JURLConnection {
	
	/**
	 * upload flag
	 */
	public static final int _UP = 1;
	
	/**
	 * download flag
	 */
	public static final int _DOWN = 2;

	/**
	 * 시스템 line separator
	 */
	public final static String _LINE_SP = System.getProperty("line.separator");

	public final static String CRLF = System.getProperty("line.separator");

	// public static String connect( String webUrl,String sendData) throws
	// Exception {
	//
	// URL url = null;
	// URLConnection conn = null;
	// PrintWriter pw = null;
	// BufferedReader br = null;
	// StringBuffer sb = new StringBuffer();
	// String line = null;
	// try{
	// url = new URL( webUrl );
	// conn = url.openConnection();
	//
	// //통신 방식 setting
	// conn.setDoOutput(true); //post방식:true
	// conn.setDoInput(true); //데이타 첨부되는 경우
	//
	// //header정보 setting
	// conn.setRequestProperty("Accept","*/*");
	// conn.setRequestProperty("Accept-Charset","euc-kr");
	// conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0;
	// Windows NT 5.0; i-NavFourF; .NET CLR 1.1.4322)");
	//
	//
	// pw = new PrintWriter( conn.getOutputStream() , true );
	// pw.print( sendData );
	// pw.flush();
	// br = new BufferedReader( new InputStreamReader( conn.getInputStream()) );
	//
	// while( (line=br.readLine())!=null ){
	// sb.append(line).append(_LINE_SP);
	// }
	//
	// }catch(Exception e){
	// e.printStackTrace();
	// throw e;
	// }finally{
	// try{if(pw!=null){pw.close();pw=null;}}catch(Exception ex){}
	// try{if(br!=null){br.close();br=null;}}catch(Exception ex){}
	// }
	// return sb.toString().trim();
	// }

	/**
	 * <pre>
	* URL Connection 으로 데이터 송신 및 수신을 실시한다.
	 * </pre>
	 * 
	 * @param conn
	 *            DB Connection <-- getConnection()
	 * @param flag
	 *            UpLoad or DownLoad (see -
	 *            JURLConnection._UP,JURLConnection._DOWN )
	 * @param data
	 *            전송할 stream 문자열
	 * @return String
	 * @throws Exception
	 */
	public static String connect(String webUrl, String sendData, Map<String, String> paramMap) throws Exception {

		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String line = null;
		try {
			br = new BufferedReader(new InputStreamReader((sendData == null || "".equals(sendData.trim())) ? sendPost(webUrl, paramMap) : sendGet(webUrl, sendData, paramMap)));

			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				sb.append(line).append(_LINE_SP);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (br != null) {
					br.close();
					br = null;
				}
			} catch (Exception ex) {
			}
		}
		return sb.toString().trim();
	}

	/**
	 * <pre>
	* URL Connection 으로 데이터 송신 및 수신을 실시한다.
	 * </pre>
	 * 
	 * @param conn
	 *            DB Connection <-- getConnection()
	 * @param flag
	 *            UpLoad or DownLoad (see -
	 *            JURLConnection._UP,JURLConnection._DOWN )
	 * @param data
	 *            전송할 stream 문자열
	 * @return String
	 * @throws Exception
	 */
	public static byte[] connect(String webUrl, String sendData, Map<String, String> paramMap, boolean b) throws Exception {

		InputStream is = null;
		JOutputStream jos = null;
		byte[] rtnBytes = null;
		try {
			is = (sendData == null || "".equals(sendData.trim())) ? sendPost(webUrl, paramMap) : sendGet(webUrl, sendData, paramMap);
			jos = new JOutputStream();

			byte[] bytes = new byte[1024];
			int size = -1;

			while ((size = is.read(bytes)) >= 0) {
				jos.write(bytes, 0, size);
			}
			jos.flush();

			rtnBytes = jos.getBytes();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception ex) {
			}
			try {
				if (jos != null) {
					jos.close();
				}
			} catch (Exception ex) {
			}
		}
		return rtnBytes;
	}

	/**
	 * <pre>
	* URL Connection 으로 데이터 송신 및 수신을 실시한다.
	 * </pre>
	 * 
	 * @param conn
	 *            DB Connection <-- getConnection()
	 * @param flag
	 *            UpLoad or DownLoad (see -
	 *            JURLConnection._UP,JURLConnection._DOWN )
	 * @param data
	 *            전송할 stream 문자열
	 * @return String
	 * @throws Exception
	 */
	public static String connect(String webUrl, String sendData, Map<String, String> textMap, Map<String, File> fileMap) throws Exception {

		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String line = null;
		try {
			br = new BufferedReader(new InputStreamReader(sendMultipartPost(webUrl, textMap, fileMap)));

			while ((line = br.readLine()) != null) {
				sb.append(line).append(_LINE_SP);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (br != null) {
					br.close();
					br = null;
				}
			} catch (Exception ex) {
			}
		}
		return sb.toString().trim();
	}

	/**
	 * application/x-www-form-urlencoded 인코딩을 사용하는 GET/POST 방식은 파라미터 값을 인코딩해서 보내야 한다.
	 * @param paramMap
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String encodeString(Map<String, String> paramMap) throws UnsupportedEncodingException {
		if (paramMap == null) {
			return "";
		}
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

	/**
	 * GET 방식으로 대상 URL에 파라미터를 전송한 후 응답을 InputStream으로 리턴한다.
	 * 
	 * @return InputStream
	 * @throws Exception
	 */
	private static InputStream sendGet(String urlStr, String sendStream, Map<String, String> paramMap) throws Exception {
		String paramString = encodeString(paramMap);
		if (paramString == null || "".equals(paramString.trim())) {
			paramString = "";
		}
		else {
			paramString = "?" + paramString;
		}

		URL url = new URL(new URL(urlStr).toExternalForm() + paramString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);

		PrintWriter pw = null;
		InputStream is = null;
		try {
			if (sendStream != null && !"".equals(sendStream)) {
				pw = new PrintWriter(conn.getOutputStream());
				pw.print(sendStream);
				pw.flush();
			}
			is = conn.getInputStream();
		} catch (java.net.SocketException se) {
			throw new Exception("[Too huge GET data] Sending data too huge. Connection reset by peer.");
		} finally {
			try {
				if (pw != null) {
					pw.close();
				}
			} catch (Exception e) {
			}
		}
		return is;
	}

	/**
	 * POST 방식으로 대상 URL에 파라미터를 전송한 후 응답을 InputStream으로 리턴한다.
	 * 
	 * @return InputStream
	 */
	private static InputStream sendPost(String urlStr, Map<String, String> paramMap) throws IOException {
		String paramString = encodeString(paramMap);
		if (paramString == null || "".equals(paramString.trim())) {
			paramString = "";
		}

		HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(conn.getOutputStream());
			pw.print(paramString);
			pw.flush();
		} finally {
			try {
				if (pw != null) {
					pw.close();
				}
			} catch (Exception e) {
			}
		}

		return conn.getInputStream();
	}

	private static String makeDelimeter() {
		return "RteB2n3s-pjsjToGFxe1W2V3SK-dPIvzktRO62I";
	}

	private static InputStream sendMultipartPost(String urlStr, Map<String, String> textMap, Map<String, File> fileMap) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();

		// Delimeter 생성
		String delimeter = makeDelimeter();

		byte[] newLineBytes = CRLF.getBytes();
		byte[] delimeterBytes = delimeter.getBytes();
		byte[] dispositionBytes = "Content-Disposition: form-data; name=".getBytes();
		byte[] quotationBytes = "\"".getBytes();
		byte[] contentTypeBytes = "Content-Type: application/octet-stream".getBytes();
		byte[] fileNameBytes = "; filename=".getBytes();
		byte[] twoDashBytes = "--".getBytes();

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + delimeter);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);

		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(conn.getOutputStream());
			// text parameter
			String[] nameArr = textMap.keySet().toArray(new String[textMap.size()]);
			for (int i = 0; i < nameArr.length; i++) {
				// Delimeter 전송
				out.write(twoDashBytes);
				out.write(delimeterBytes);
				out.write(newLineBytes);
				// 파라미터 이름 출력
				out.write(dispositionBytes);
				out.write(quotationBytes);
				out.write(nameArr[i].getBytes());
				out.write(quotationBytes);

				// String 이라면
				out.write(newLineBytes);
				out.write(newLineBytes);
				// 값 출력
				out.write(textMap.get(nameArr[i]).getBytes());
				out.write(newLineBytes);

				if (i == nameArr.length - 1 && fileMap.size() == 0) {
					// 마지막 Delimeter 전송
					out.write(twoDashBytes);
					out.write(delimeterBytes);
					out.write(twoDashBytes);
					out.write(newLineBytes);
				}
				out.flush();
			}
			// file parameter
			nameArr = fileMap.keySet().toArray(new String[fileMap.size()]);
			File file = null;
			BufferedInputStream bi = null;
			byte[] fileBuffer = null;
			for (int i = 0; i < nameArr.length; i++) {
				if (fileMap.get(nameArr[i]) == null || !fileMap.get(nameArr[i]).exists()) {
					continue;
				}

				// Delimeter 전송
				out.write(twoDashBytes);
				out.write(delimeterBytes);
				out.write(newLineBytes);
				// 파라미터 이름 출력
				out.write(dispositionBytes);
				out.write(quotationBytes);
				out.write(nameArr[i].getBytes());
				out.write(quotationBytes);

				file = fileMap.get(nameArr[i]);
				out.write(fileNameBytes);
				out.write(quotationBytes);
				out.write(file.getAbsolutePath().getBytes());
				out.write(quotationBytes);

				out.write(newLineBytes);
				out.write(contentTypeBytes);
				out.write(newLineBytes);
				out.write(newLineBytes);

				try {
					bi = new BufferedInputStream(new FileInputStream(file));
					fileBuffer = new byte[1024 * 8]; // 8k
					int len = -1;
					while ((len = bi.read(fileBuffer)) != -1) {
						out.write(fileBuffer, 0, len);
					}
				} finally {
					try {
						if (bi != null) {
							bi.close();
						}
					} catch (Exception e) {
					}
				}
				out.write(newLineBytes);

				if (i == nameArr.length - 1) {
					// 마지막 Delimeter 전송
					out.write(twoDashBytes);
					out.write(delimeterBytes);
					out.write(twoDashBytes);
					out.write(newLineBytes);
				}
				out.flush();
			}
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
			}
		}
		return conn.getInputStream();
	}

	public static InputStream sendStream(String endPoint, byte[] bytes, Map<String, String> paramMap) throws Exception {
		String paramString = encodeString(paramMap);
		if (paramString == null || "".equals(paramString.trim())) {
			paramString = "";
		}
		else {
			paramString = "?" + paramString;
		}

		URL url = new URL(new URL(endPoint).toExternalForm() + paramString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/octet-stream");

		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);

		InputStream is = null;
		try {
			conn.getOutputStream().write(bytes);
			conn.getOutputStream().flush();

			is = conn.getInputStream();
		} catch (java.net.SocketException se) {
			throw new Exception("[Too huge GET data] Sending data too huge. Connection reset by peer.");
		}
		return is;
	}

	public static void main(String[] args) throws Exception {
	}
}
