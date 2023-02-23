package com.xzkj.platform.khd.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	public static final String PATTERN_yyyy_MM_dd = "yyyy-MM-dd";
	public static final String PATTERN_yyyyMMdd = "yyyyMMdd";
	public static final String PATTERN_yyyyMM = "yyyyMM";
	public static final String PATTERN_yyyy_MM = "yyyy-MM";
	public static final String PATTERN_MMdd = "MM-dd";
	public static final String PATTERN_yyMMdd = "yy/MM/dd";
	public static final String PATTERN_yyyyMMddHHmmss = "yyyyMMddHHmmss";
	public static final String PATTERN_yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";
	public static final String PATTERN_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
	public static final String PATTERN_yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
	public static final String PATTERN_HH_mm_ss = "HH:mm:ss";
	public static final String PATTERN_HHmmss = "HHmmss";
	public static final String PATTERN_yyyyMMdd_HHmmss = "yyyy/MM/dd HH:mm:ss";
	public static final String PATTERN_yyyyMMdd2 = "yyyy/MM/dd";

	public static String getFormatDate(Date date, String format){
		if(date != null){
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		}else{
			return null;
		}
	}
	
	public static String getNow(String format){
		Date currentDate = new Date();
		return formatDate(currentDate, format);
	}
	
	public static String formatDate(Date date, String format) {
		return getFormatDate(date, format);
	}
	
	public static String getTimeStamp(){
		return getNow(PATTERN_yyyyMMddHHmmss);
	}
	
	public static String convertDate(Date specDate) {
		return new SimpleDateFormat(PATTERN_yyyyMMdd).format(specDate);
	}
	public static String convertDate1(Date specDate) {
		return new SimpleDateFormat(PATTERN_yyyyMM).format(specDate);
	}
	
	public static String convertDate3(Date specDate) {
		String str = "";
		try {
			str = new SimpleDateFormat(PATTERN_yyyy_MM_dd).format(specDate);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return str;
	}
	
	public static Date converDateFromStr3(String str) throws ParseException {
		return new SimpleDateFormat(PATTERN_yyyy_MM_dd).parse(str);
	}
	
	public static Date converDateFromStr4(String str) throws ParseException {
		return new SimpleDateFormat(PATTERN_yyyy_MM).parse(str);
	}
	
	/**
	 *    判断开始前面时间是不是大于等于后面时间
	 * @param startDate yyyyMMdd
	 * @param endDate  yyyyMMdd
	 * @return
	 */
	public static boolean isCompareTime(String startDate,String endDate) {
		if(toDate(startDate,"yyyyMMdd").getTime() >= toDate(endDate, "yyyyMMdd").getTime()) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * String转Date
	 * 
	 * @param sdate
	 *            日期字符串
	 * @param fmString
	 *            指定日期格式
	 * @return
	 */
	public static Date toDate(String sdate, String fmString) {
		DateFormat df = new SimpleDateFormat(fmString);
		try {
			return df.parse(sdate);
		} catch (ParseException e) {
			throw new RuntimeException("日期格式不正确 ");
		}
	}

}
