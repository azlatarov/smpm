package com.acm.util;

public class Utilities {
	
	public static boolean isNullOrEmpty(String in) {
		return (in == null) ? true : (("".equals(in)) ? true : false);
	}
	
	public static boolean hasNullOrEmpty(String... in) {
		for (String s : in)
			if(isNullOrEmpty(s)) return true;
		
		return false;
	}
	
}
