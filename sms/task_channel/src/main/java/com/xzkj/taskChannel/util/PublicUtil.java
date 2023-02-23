package com.xzkj.taskChannel.util;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/** 
 * @author admin
 */
public class PublicUtil {

	/**
	 * 中国移动拥有号码段为:139,138,137,136,135,134,147,159,158,157(3G),152,151,150,188(3G),187(3G),182;16个号段(183, 184,178, 198)
	 * 中国联通拥有号码段为:130,131,132,156(3G),186(3G),185(3G);6个号段(145, 155,175, 176, 171, 166)
	 * 中国电信拥有号码段为:133,153,189(3G),180(3G);4个号码段(181, 177, 173, 199)
	 */
	
	// 移动用户号段{ 134, 135, 136, 137, 138, 139, 147, 150, 151, 152, 157, 158, 159, 182, 183, 184, 187, 188, 178, 198 };
	// 联通用户号段{ 130, 131, 132, 145, 155, 156, 185, 186, 175, 176, 171, 166 };
	// 电信用户号段{ 133, 153, 180, 181, 189, 177, 173, 199 };
	// 移动用户号段{ 1703, 1705, 1706 };
	// 联通用户号段{ 1709, 1708, 1707 };
	// 电信用户号段{ 1700, 1701, 1702 };
	// 国际区号"美国加拿大,1;埃及,20;南苏丹,211;摩洛哥,212;阿尔及利亚,213;突尼斯,216;利比亚,218;冈比亚,220;塞内加尔,221;毛里塔尼亚,222;马里,223;几内亚,224;科特迪瓦,225;布基纳法索,226;尼日尔,227;多哥,228;贝宁,229;毛里求斯,230;利比里亚,231;塞拉利,232;加纳,233;尼日利亚,234;乍得,235;中非,236;喀麦隆,237;佛得角,238;赤道几内亚,240;加蓬,241;刚果（布）,242;刚果（金）,243;安哥拉,244;几内亚比绍,245;塞舌尔,248;苏丹,249;卢旺达,250;埃塞俄比亚,251;索马里,252;吉布提,253;肯尼亚,254;坦桑尼亚,255;乌干达,256;布隆迪,257;莫桑比克,258;赞比亚,260;马达加斯加,261;留尼汪,262;津巴布韦,263;纳米尼亚,264;马拉维,265;莱索托,266;博茨瓦纳,267;斯威士兰,268;科摩罗,269;南非,27;阿森松岛,290;厄立特里亚,291;阿鲁巴,297;法罗群岛,298;格陵兰,299;希腊,30;荷兰,31;比利时,32;法国,33;西班牙,34;直布罗陀,350;葡萄牙,351;卢森堡,352;爱尔兰,353;冰岛,354;阿尔巴尼亚,355;马耳他,356;塞浦路斯,357;芬兰,358;保加利亚,359;匈牙利,36;立陶宛,370;拉脱维亚,371;爱沙尼亚,372;摩尔多瓦,373;亚美尼亚,374;白俄罗斯,375;安道尔,376;摩纳哥,377;圣马力诺,378;梵蒂冈,379;乌克兰,380;塞尔维亚,381;黑山共和国,382;克罗地亚,385;斯洛文尼亚,386;波黑,387;前南马其顿,389;意大利,39;罗马尼亚,40;瑞士,41;捷克,420;斯洛伐克,421;列支敦士登,423;奥地利,43;英国,44;丹麦,45;瑞典,46;挪威,47;波兰,48;德国,49;福克兰群岛,500;伯利兹,501;危地马拉,502;萨尔瓦多,503;洪都拉斯,504;尼加拉瓜,505;哥斯达黎加,506;巴拿马,507;海地,509;秘鲁,51;墨西哥,52;古巴,53;库拉索,531;阿根廷,54;巴西,55;智利,56;哥伦比亚,57;委内瑞拉,58;瓜德罗普,590;玻利维亚,591;圭亚那,592;厄瓜多尔,593;法属圭亚那,594;巴拉圭,595;马提尼克,596;苏里南,597;乌拉圭,598;安的列斯,599;马来西亚,60;澳大利亚,61;印度尼西亚,62;菲律宾,63;新西兰,64;新加坡,65;泰国,66;东帝汶,670;南极洲,672;文莱,673;瑙鲁,674;巴新,675;汤加,676;所罗门群岛,677;瓦努阿图,678;斐济,679;帕劳,680;库克群岛,682;纽埃,683;美属萨摩亚,684;萨摩亚,685;基里巴斯,686;新喀里多,687;图瓦卢,688;波利尼西亚,689;托克劳,690;密克罗尼,691;马绍尔群岛,692;俄罗斯,7;哈萨克斯坦,73;日本,81;韩国,82;越南,84;朝鲜,850;香港,852;澳门,853;柬埔寨,855;老挝,856;中国,86;孟加拉国,880;台湾,886;土耳其,90;印度,91;巴基斯坦,92;阿富汗,93;斯里兰卡,94;缅甸,95;马尔代夫,960;黎巴嫩,961;约旦,962;叙利亚,963;伊拉克,964;科威特,965;沙特阿拉伯,966;也门,967;阿曼,968;阿联酋,971;以色列,972;巴林,973;卡塔尔,974;不丹,975;蒙古,976;尼泊尔,977;伊朗,98;塔吉克斯坦,992;土库曼斯坦,993;阿塞拜疆,994;格鲁吉亚,995;吉尔吉斯,996;乌兹别克,998";
		
		
	private static String regPhoneStr = "^1(([3][0-9])|([4][7])|([5][01236789])|([8][0256789]))[0-9]{8}$";
	private static String regcallStr="1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}";
	/**
	 * 验证是否是手机号码(移动号码)
	 * @param phone
	 * @return
	 */
	public static boolean isPhone(String phone) {
		//判断手机号码不能为空 并且 属于移动 联通 或是 电信
		if (null != phone && !"".equals(phone) && phone.matches(regPhoneStr)) {
			return true;
		} else {
			return false;
		}

	}
	
	/**
     * 校验手机号
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
	public static boolean isMobile(String mobile) {
    	String REGEX_MOBILE = "^((17)|(14)|(13)|(15)|(18)|(16)|(19))\\d{9}$";
        return Pattern.matches(REGEX_MOBILE, mobile);
    }
	
	/**
	 * 校验座机号
	 * @param telNum
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isTelNum(String telNum) {
		String REGEX_TEL_NUM = "^(0\\d{2,3}-?)?\\d{7,8}$";
		return Pattern.matches(REGEX_TEL_NUM, telNum);
	}
	/**
	 * 校验手机号
	 * @param mobile
	 * @return 校验匹配手机号的相似度（0-9）
	 */
	public static int likeMobile(String mobile) {
		int result=0;
		String REGEX_MOBILE = "^1\\d{10}$";
		if(Pattern.matches(REGEX_MOBILE, mobile)){
			return 9;
		}
		REGEX_MOBILE = "^((17)|(14)|(13)|(15)|(18)|(16)|(19)).{9}$";
		if(Pattern.matches(REGEX_MOBILE, mobile)){
			return 8;
		}
		REGEX_MOBILE = "^(1[0-9])\\d{9}$";
		if(Pattern.matches(REGEX_MOBILE, mobile)){
			return 7;
		}
		REGEX_MOBILE = "d{11}$";
		if(Pattern.matches(REGEX_MOBILE, mobile)){
			return 6;
		}
		if(mobile.length()==11){
			return 5;
		}
		if(mobile.length()>11&&mobile.length()<=20){
			return 4;
		}
		if(mobile.length()<11){
			return 3;
		}
		if(mobile.length()>20){
			return 2;
		}
		return result;
	}
	
	/**
	 * 验证是否是固定号码
	 * @param phone
	 * @return
	 */
	public static boolean isCall(String phone) {
		//判断手机号码不能为空 并且 属于移动 联通 或是 电信
		if (null != phone && !"".equals(phone) && phone.matches(regcallStr)) {
			return true;
		} else {
			return false;
		}

	}
	
	/**
	 * 透过代理或者反向代理获得真实的客户端ip
	 * @param request
	 * @return
	 */
	public static String getClientIp(HttpServletRequest request) {
		if (request == null)
			return "";
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip != null && ip.indexOf(",") > -1) {
			ip = ip.substring(0, ip.indexOf(","));
		}
		if (ip != null && ip.length() > 20) {
			ip = ip.substring(0, 20);
		}
		if(ip.equals("127.0.0.1")){
			//根据网卡取本机配置的IP
		    InetAddress inet=null;
		    try {
		    	inet = InetAddress.getLocalHost();
		    	ip= inet.getHostAddress();
		    } catch (Exception e) {
		     //e.printStackTrace();
		    }
		}
		return ip;
	}
	
	/**
	 * 获取浏览器类型
	 * @param request
	 * @return
	 */
	public static String getUserAgent(HttpServletRequest request) {
		return request.getHeader("User-Agent");
	}
	
	public static Map<String, Long> getSandEtimeOnDay(){
		Map<String, Long> map = new HashMap<String, Long>();
		Calendar can = Calendar.getInstance();
		can.set(Calendar.MILLISECOND, 0);
		can.set(Calendar.SECOND, 0);
		can.set(Calendar.MINUTE, 0);
		can.set(Calendar.HOUR_OF_DAY, 0);
		map.put("stime", can.getTimeInMillis());
		can.set(Calendar.HOUR_OF_DAY, 24);
		map.put("etime", can.getTimeInMillis());
		return map;
	}
	
	public static Map<String, Long> getSandEtimeOnWeek(){
		Map<String, Long> map = new HashMap<String, Long>();
		Calendar can = Calendar.getInstance();
		can.set(Calendar.MILLISECOND, 0);
		can.set(Calendar.SECOND, 0);
		can.set(Calendar.MINUTE, 0);
		can.set(Calendar.HOUR_OF_DAY, 0);
		
		int nowDay = can.get(Calendar.DAY_OF_WEEK);
		if(1 == nowDay){
			can.add(Calendar.DAY_OF_WEEK, 1);
		}else{
			can.add(Calendar.WEEK_OF_YEAR, 1);
			can.set(Calendar.DAY_OF_WEEK, 2);
		}
		map.put("etime", can.getTimeInMillis());
		can.add(Calendar.DAY_OF_WEEK, -7);
		map.put("stime", can.getTimeInMillis());
		return map;
	}

	public static Map<String, Long> getSandEtimeOnMonth(){
		Map<String, Long> map = new HashMap<String, Long>();
		Calendar can = Calendar.getInstance();
		can.set(Calendar.MILLISECOND, 0);
		can.set(Calendar.SECOND, 0);
		can.set(Calendar.MINUTE, 0);
		can.set(Calendar.HOUR_OF_DAY, 0);
		can.set(Calendar.DAY_OF_MONTH, 1);
		map.put("stime", can.getTimeInMillis());
		can.add(Calendar.MONTH, 1);
		//can.add(Calendar.HOUR_OF_DAY, -1);
		map.put("etime", can.getTimeInMillis());
		return map;
	}
	
	public static Map<String, Long> getSandEtimeOnDay(Date time){
		Map<String, Long> map = new HashMap<String, Long>();
		Calendar can = Calendar.getInstance();
		if(time == null){
			return getSandEtimeOnDay();
		}
		can.setTime(time);
		
		can.set(Calendar.MILLISECOND, 0);
		can.set(Calendar.SECOND, 0);
		can.set(Calendar.MINUTE, 0);
		can.set(Calendar.HOUR_OF_DAY, 0);
		map.put("stime", can.getTimeInMillis());
		can.set(Calendar.HOUR_OF_DAY, 24);
		map.put("etime", can.getTimeInMillis());
		return map;
	}
	
	public static Map<String, Long> getSandEtimeOnWeek(Date time){
		Map<String, Long> map = new HashMap<String, Long>();
		Calendar can = Calendar.getInstance();
		if(time == null){
			return getSandEtimeOnWeek();
		}
		can.setTime(time);
		
		can.set(Calendar.MILLISECOND, 0);
		can.set(Calendar.SECOND, 0);
		can.set(Calendar.MINUTE, 0);
		can.set(Calendar.HOUR_OF_DAY, 0);
		can.add(Calendar.WEEK_OF_YEAR, 1);
		can.set(Calendar.DAY_OF_WEEK, 1);
		map.put("etime", can.getTimeInMillis());
		can.add(Calendar.DAY_OF_WEEK, -6);
		map.put("stime", can.getTimeInMillis());
		return map;
	}

	public static Map<String, Long> getSandEtimeOnMonth(Date time){
		Map<String, Long> map = new HashMap<String, Long>();
		Calendar can = Calendar.getInstance();
		if(time == null){
			return getSandEtimeOnMonth();
		}
		can.setTime(time);
		
		can.set(Calendar.MILLISECOND, 0);
		can.set(Calendar.SECOND, 0);
		can.set(Calendar.MINUTE, 0);
		can.set(Calendar.HOUR_OF_DAY, 0);
		can.set(Calendar.DAY_OF_MONTH, 1);
		map.put("stime", can.getTimeInMillis());
		can.add(Calendar.MONTH, 1);
		can.add(Calendar.HOUR_OF_DAY, -1);
		map.put("etime", can.getTimeInMillis());
		return map;
	}
	
	/**
	 * 将字符串UTF-8编码转为ISO-8859-1，ligureUI使用
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String utfToISO(String str) throws UnsupportedEncodingException {
		return new String(str.getBytes("UTF-8"),"ISO-8859-1");

	}

}

