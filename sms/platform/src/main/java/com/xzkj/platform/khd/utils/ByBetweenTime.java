package com.xzkj.platform.khd.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 时间集合工具类
 */
public class ByBetweenTime {
	
	/**
     * 获取两个日期之间的日期：2020-10-01，2020-10-31
     * @param startTime
     * @param endTime
     * @return 日期的List<String>集合
     */
    public static List<String> getBetweenDate(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
 
        // 声明保存日期集合
        List<String> list = new ArrayList<>();
        try {
            // 转化成日期类型
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);
            //用Calendar 进行日期比较判断
            Calendar calendar = Calendar.getInstance();
            while (startDate.getTime() <= endDate.getTime()) {
                // 把日期添加到集合
                list.add(sdf.format(startDate));
                // 设置日期
                calendar.setTime(startDate);
                //把日期增加一天
                calendar.add(Calendar.DATE, 1);
                // 获取增加后的日期
                startDate = calendar.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
 
        return list;
    }
	
	/**
	 * @author 年-月：2020-01，2020-10
	 * @param startMonthStr 开始时间
	 * @param endMonthStr   结束时间
	 * @return 年-月的List<String>集合
	 */
	public static List<String> getMonthByBetweenTime(String startMonthStr, String endMonthStr){
		Integer startYear = Integer.parseInt(startMonthStr.split("-")[0]);
		Integer startMonth = Integer.parseInt(startMonthStr.split("-")[1]);
		
		Integer endYear = Integer.parseInt(endMonthStr.split("-")[0]);
		Integer endMonth = Integer.parseInt(endMonthStr.split("-")[1]);
		
		Integer months = 0;
		if(endYear - startYear == 0) {
			months = endMonth - startMonth;
		}
		
		if(endYear - startYear == 1) {
			months = 12 - startMonth + endMonth;
		}
		
		if(endYear - startYear > 1) {
			months = 12 - startMonth + endMonth;
			months += (endYear - startYear - 1)*12;
		}
		
		List<String> list = new ArrayList<>();
		list.add(startMonthStr);
		for(int i=1; i<=months ; i++) {
			startMonth ++;
			if(startMonth >12) {
				startYear ++;
				startMonth = 1;
			}
			
			if(startMonth < 10 ) {
				String date = startYear +"-0"+ startMonth;
				list.add(date);
			}else {
				String date = startYear +"-"+ startMonth;
				list.add(date);
			}
			
		}
		
		return list;
	}
	
	/**
	 * 年：2019，2020
	 * @param startYearStr 开始时间
	 * @param endYearStr   结束时间
	 * @return
	 */
	public static List<String> getYearByBetweenTime(Integer startYearStr, Integer endYearStr){
		
		Integer years = endYearStr - startYearStr +1;
		
		List<String> list = new ArrayList<>();
		
		for (int i = 1; i <= years; i++) {
			Integer data = 	startYearStr ++;
			list.add(data.toString());	
		}

		return list;
	}
	
	/**
     * 获取本月第一天或最后一天
     * @param type 1-第一天  2-最后一天
     * @return
     */
    public static String getMonthMinOrMaxDate(int type){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(new Date());
    	if (type == 1) {
    		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		}else {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		}
        return sdf.format(calendar.getTime());
    }
    
    /**
     * 获取年 第一个月 本月 最后一月
     * @param type 1-一个月  2-本月 3-最后一月
     * @return
     */
    public static String getYearMinOrMaxDate(int type){
    	String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));//当前年份
		String month = "";
    	if (type == 1) {
    		month = "-01";
		}else if(type == 2) {
			month = "-"+String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1);//当前月
		}else {
			month = "-12";
		}
        return year+month;
    }
    
    /**
     * 获取前或后的年月
     * @param type 1-月前 2-当前月  3-月后
     * @param number 数
     * @return
     */
    public static String getReckonDate(int type,int number){
    	
    	SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM");
    	Date nowDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        
    	String reckonDate = "";
    	
    	if (type == 1) {
    		calendar.add(Calendar.MONTH, number);//月前
    		Date updateDate = calendar.getTime();
    		reckonDate = sdf.format(updateDate);
    		//截取获得字符串数组
            String[] strArray = reckonDate.split("-");
    		if (Integer.parseInt(strArray[0]) == 2019) {
    			reckonDate = "2020-01";
			}
		}else if(type == 2){
			Date updateDate = calendar.getTime();
			reckonDate = sdf.format(updateDate);//当前月
		}else if(type == 3){
			calendar.add(Calendar.MONTH, number);//月后
			Date updateDate = calendar.getTime();
			reckonDate = sdf.format(updateDate);
		}
    	
    	return reckonDate;
    }
    
    /**
     * 获取当前时间
     * @param format 时间格式 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getToday(String format) {
		Date date = new Date();// 取时间
		SimpleDateFormat df = new SimpleDateFormat(format);// 设置日期格式
		String time = df.format(date); 
		return time;
	}

	/**
	 * 获取指定年月的最后一天
	 * @param yearMonth
	 * @return
	 */
	public static String getLastDayOfMonth(String yearMonth) {
		int year = Integer.parseInt(yearMonth.split("-")[0]);  //年
		int month = Integer.parseInt(yearMonth.split("-")[1]); //月
		Calendar cal = Calendar.getInstance();
		// 设置年份
		cal.set(Calendar.YEAR, year);
		// 设置月份
		// cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.MONTH, month); //设置当前月的上一个月
		// 获取某月最大天数
		//int lastDay = cal.getActualMaximum(Calendar.DATE);
		int lastDay = cal.getMinimum(Calendar.DATE); //获取月份中的最小值，即第一天
		// 设置日历中月份的最大天数
		//cal.set(Calendar.DAY_OF_MONTH, lastDay);
		cal.set(Calendar.DAY_OF_MONTH, lastDay - 1); //上月的第一天减去1就是当月的最后一天
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		return sdf.format(cal.getTime());
	}
	
	
	public static void main(String[] args) {
		
		// 年
		List<String> list= getYearByBetweenTime(2014,2020);
		System.out.println(list);
		
		// 月
/*		List<String> list= getMonthByBetweenTime("2018-01","2020-05");
		System.out.println(list);*/

		System.out.println(getLastDayOfMonth("2022-02"));
    }

}
