package custom.utils;

import java.util.Map;


public final class jvuString {
	
	private static final int[] reverseTable = new int[128];
	private static final char[] convTable = { 
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 
		'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
		'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
		't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
		'8', '9', '+', '/' 
	};
	
	static {
		for (int i = 0; i < reverseTable.length; i++) reverseTable[i] = -1;
		for (int i = 0; i < 64; i++) reverseTable[convTable[i]] = i;
		reverseTable['='] = 0; // '=' is effectively end-of-line, so we'll assign it a 0 value TODO
	}
	
	private jvuString() {}
	
    /**
     * Removes all occurrences of given string. Can be a regex expression.
     * Example1: to remove all spaces, use rplsmt=" ". Example2: to remove all digits, use rplsmt="\\d".
     * Example3: to remove all lower case letters, use rplsmt="[a-z]". Example4: to remove CR and LF, use rplsmt="\r\n".
     * @param 	s
     * @param 	rplsmt
     */
	public static String rmvAll (String s, String rplsmt) {
		return (s == null) ? null : s.replaceAll(rplsmt, "");
	}
	
	/**
	 * Surround string with provided value. To use different characters for beginning and end, put both of them in srndValue. Only first 2 chars from trmValue are taken into account.
	 * Example1: to surround with single quotes, srndValue = "'". Example2: to surround with parentheses, srndValue = "()".
	 * @param 	s
	 * @param 	srndValue
	 */
	public static String surround (String s, String srndValue) {
		if (s == null || s.isEmpty() || srndValue == null || srndValue.isEmpty())
			return s;
		
		if (srndValue.length() == 1)
			return srndValue+s+srndValue;
		else
			return srndValue.toCharArray()[0]+s+srndValue.toCharArray()[1];
	}
	
	/**
	 * Trim string using provided value. To use different characters for beginning and end, put both of them in trmValue. Only first 2 chars from trmValue are taken into account.
	 * Example1: to trim single quotes, trmValue = "'". Example2: to trim parentheses, trmValue = "()".
	 * @param 	s
	 * @param 	trmValue
	 */
	public static String trim (String s, String trmValue) {
		if (s == null || s.isEmpty() || trmValue == null || trmValue.isEmpty())
			return s;
		
		s = (s.startsWith(trmValue.substring(0,1))) ? s.substring(1) : s;
		if (trmValue.length() == 1)
			return (s.endsWith(trmValue)) ? s.substring(0, s.length()-1) : s ;
		else
			return (s.endsWith(trmValue.substring(1,2))) ? s.substring(0, s.length()-1) : s ;
	}
	
	/**
	 * Perform base64 encoding on a file.
	 */
	public static String encodeBase64 (byte[] file) {
		if (file == null || file.length == 0) return null;
		
		int i = 0;
		int outctr = 0;
		int srclen = file.length;
		int outlen = (srclen * 4) / 3;
		int remainder = outlen % 4; // we need to make sure that the output is a multiple of 4
		
		if (remainder != 0)
			outlen += (4 - remainder);
		
		char[] ret = new char[outlen];
		for (i = 0; i < srclen; i += 3) {
			// Take every 3 bytes, convert to an int, use an AND mask to get the appropriate bits, and then use the convTable
			// to get the character do a "& 0xFF" to ensure that we have the positive value of the byte
			int buf = ((file[i] & 0xFF) << 16) + (((i + 1) < srclen) ? ((file[i + 1] & 0xFF) << 8) : 0) + (((i + 2) < srclen) ? (file[i + 2] & 0xFF) : 0);
			ret[outctr++] = convTable[(buf & 0x00FC0000) >> 18];
			ret[outctr++] = convTable[(buf & 0x0003F000) >> 12];
			ret[outctr++] = (((i + 1) < srclen) ? convTable[(buf & 0x00000FC0) >> 6] : '=');
			ret[outctr++] = (((i + 2) < srclen) ? convTable[(buf & 0x0000003F)] : '=');
		}

		return new String(ret);
	}
	
	/**
	 * Perform base64 encoding on multiple files.
	 */
	public static void multipleEncodeBase64 (Map<String, byte[]> filesToEncode, Map<String, byte[]> encodedFiles) {
		if (filesToEncode == null || filesToEncode.isEmpty()) return;
		
		for (String fileName : filesToEncode.keySet())
			encodedFiles.put(fileName, encodeBase64(filesToEncode.get(fileName)).getBytes());
	}
	
	/**
	 * Perform base64 encoding on multiple files. For memory optimization the input map is also used for output.
	 */
	public static void multipleEncodeBase64_memory_optimized (Map<String, byte[]> filesToEncode) {
		if (filesToEncode == null || filesToEncode.isEmpty()) return;
		
		for (String fileName : filesToEncode.keySet())
			filesToEncode.put(fileName, encodeBase64(filesToEncode.get(fileName)).getBytes());
	}
	
	/**
	 * Decode a base64 string (str) and returns the base256 byte array.
	 */
	public static byte[] decodeBase64 (String str) {
		if (str == null || str.length() == 0) return null;
		
		int buf = 0;
		int j = 0;
		str = rmvAll(str+"===","\r\n"); // add in some extra padding so that we don't need to check for the end of the line
		int len = str.indexOf("=");
		char[] encoded = str.toCharArray();
		byte[] decoded = new byte[((len * 3) / 4)];
		
		for (int i = 0; i < len; i += 4) {
			buf = (reverseTable[encoded[i]] << 18) + (reverseTable[encoded[i + 1]] << 12) + (reverseTable[encoded[i + 2]] << 6) + reverseTable[encoded[i + 3]];
			decoded[j++] = (byte) ((buf & 0x00ff0000) >> 16);
			if ((i + 2) < len) decoded[j++] = (byte) ((buf & 0x0000ff00) >> 8);
			if ((i + 3) < len) decoded[j++] = (byte) (buf & 0x000000ff);
		}

		return decoded;
	}
	
}
