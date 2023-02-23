package com.xzkj.platform.common.utils;

public class NumberUtils {
	
	public static int getInteger(String str,int parInt){
		
		int i = 0;
		try {
			i = Integer.parseInt(str);
		} catch (NumberFormatException e) {
//			e.printStackTrace();
			i = parInt;
		}
		return i;
	}
}
