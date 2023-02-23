package com.xzkj.platform.common.utils;

import java.util.UUID;

public class UUIDUtils {
	
	private static int serialId = 0;
	
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    public synchronized static int getSerial() {
    	if(serialId == 999) {
    		serialId = 0;
    	}
    	serialId ++ ;
    	return serialId;
    }

}
