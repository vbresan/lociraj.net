package biz.binarysolutions.lociraj.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 *
 */
public class Crypto {

	/**
	 * 
	 * @param digest
	 * @return
	 */
	private static String toHexString(byte[] digest) {
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < digest.length; i++) {
			
			int    byteAsInt    = digest[i] & 0xff;
			String byteAsString = Integer.toHexString(byteAsInt);
			
			if (byteAsString.length() == 1) {
				sb.append("0");
			}

			sb.append(byteAsString);
		}
		
	    return sb.toString();
	}

	/**
	 * 
	 * @param string
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public static String getSHA1(String string) throws NoSuchAlgorithmException {
		
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] buffer = string.getBytes();
        md.update(buffer);
        
        byte[] digest = md.digest();

		return toHexString(digest);
	}
}
