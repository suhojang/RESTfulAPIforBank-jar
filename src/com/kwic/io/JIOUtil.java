package com.kwic.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;

/**
 * <pre>
* io관련 util
 * </pre>
 *
 * @author Jang,Junghoon
 * @since 1.4.*
 * @see java.io.PrintStream
 * @see java.io.FileWriter
 */
public class JIOUtil {

	/**
	 * <pre>
	* object 를 복사한다.
	* 하부단의 모든 자식 object들은 implements Serializable 하여야 한다.
	 * </pre>
	 * 
	 * @param serial  target Object
	 * @return Object cloned object
	 * @throws IOException,ClassNotFoundException,Exception
	 *             </pre>
	 */
	public static Object clone(Serializable serial) throws IOException, ClassNotFoundException, Exception {
		Object copiedObject = null;

		PipedOutputStream pOut = null;
		PipedInputStream pIn = null;
		ObjectOutputStream oOut = null;
		ObjectInputStream oIn = null;

		try {
			pOut = new PipedOutputStream();
			pIn = new PipedInputStream(pOut);

			oOut = new ObjectOutputStream(pOut);
			oIn = new ObjectInputStream(pIn);

			oOut.writeObject(serial);
			copiedObject = oIn.readObject();
			
		} catch (IOException ie) {
			ie.printStackTrace();
			throw ie;
			
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			throw cnfe;
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			try {
				if(oOut != null){
					oOut.close();
				}
			} catch (Exception ex) {
			}

			try {
				if(oIn != null){
					oIn.close();
				}
			} catch (Exception ex) {
			}
		}
		return copiedObject;
	}

}
