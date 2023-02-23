package com.xzkj.apiController.util;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileUtil {
    // 目的号码正则表达式
    private static Pattern destNumberPat = Pattern.compile("(\\+?[0-9]{10,21})", Pattern.CASE_INSENSITIVE);
    // 扩展号码验证正则表达式
    private static Pattern extNumberPat = Pattern.compile("\\d{1,21}", Pattern.CASE_INSENSITIVE);
    // 全角
    public static String FULL_WIDTH = "full-width";
    // 半角
    public static String HALF_WIDTH = "half-width";
    // 头部签名
    public static String HEAD_SIGN = "head-sign";
    // 尾部签名
    public static String TAIL_SIGN = "tail-sign";
    // 移动用户号段
    private static Integer[] CMCC_P3 = new Integer[] { 134, 135, 136, 137, 138, 139, 147,148, 150, 151, 152, 157, 158, 159,165,172, 178,182, 183, 184, 187, 188, 198,195,197};
    // 联通用户号段
    private static Integer[] UNICOM_P3 = new Integer[] { 130, 131, 132, 145,146, 155, 156,166, 167, 171, 175, 176, 185, 186};
    // 电信用户号段
    private static Integer[] TELECOM_P3 = new Integer[] { 133, 141,149,153,162, 168, 173,174, 177, 179, 180, 181, 189, 191, 199 };
    // 移动用户号段
    private static Integer[] CMCC_P4 = new Integer[] { 1440,1703, 1705, 1706 };
    // 联通用户号段
    private static Integer[] UNICOM_P4 = new Integer[] { 1400,1704, 1707, 1708, 1709 };
    // 电信用户号段
    private static Integer[] TELECOM_P4 = new Integer[] { 1410,1700, 1701, 1702 };
    // 国际短信区号
    private static String internationalCode = "美国加拿大,1;埃及,20;南苏丹,211;摩洛哥,212;阿尔及利亚,213;突尼斯,216;利比亚,218;冈比亚,220;塞内加尔,221;毛里塔尼亚,222;马里,223;几内亚,224;科特迪瓦,225;布基纳法索,226;尼日尔,227;多哥,228;贝宁,229;毛里求斯,230;利比里亚,231;塞拉利,232;加纳,233;尼日利亚,234;乍得,235;中非,236;喀麦隆,237;佛得角,238;赤道几内亚,240;加蓬,241;刚果（布）,242;刚果（金）,243;安哥拉,244;几内亚比绍,245;塞舌尔,248;苏丹,249;卢旺达,250;埃塞俄比亚,251;索马里,252;吉布提,253;肯尼亚,254;坦桑尼亚,255;乌干达,256;布隆迪,257;莫桑比克,258;赞比亚,260;马达加斯加,261;留尼汪,262;津巴布韦,263;纳米尼亚,264;马拉维,265;莱索托,266;博茨瓦纳,267;斯威士兰,268;科摩罗,269;南非,27;阿森松岛,290;厄立特里亚,291;阿鲁巴,297;法罗群岛,298;格陵兰,299;希腊,30;荷兰,31;比利时,32;法国,33;西班牙,34;直布罗陀,350;葡萄牙,351;卢森堡,352;爱尔兰,353;冰岛,354;阿尔巴尼亚,355;马耳他,356;塞浦路斯,357;芬兰,358;保加利亚,359;匈牙利,36;立陶宛,370;拉脱维亚,371;爱沙尼亚,372;摩尔多瓦,373;亚美尼亚,374;白俄罗斯,375;安道尔,376;摩纳哥,377;圣马力诺,378;梵蒂冈,379;乌克兰,380;塞尔维亚,381;黑山共和国,382;克罗地亚,385;斯洛文尼亚,386;波黑,387;前南马其顿,389;意大利,39;罗马尼亚,40;瑞士,41;捷克,420;斯洛伐克,421;列支敦士登,423;奥地利,43;英国,44;丹麦,45;瑞典,46;挪威,47;波兰,48;德国,49;福克兰群岛,500;伯利兹,501;危地马拉,502;萨尔瓦多,503;洪都拉斯,504;尼加拉瓜,505;哥斯达黎加,506;巴拿马,507;海地,509;秘鲁,51;墨西哥,52;古巴,53;库拉索,531;阿根廷,54;巴西,55;智利,56;哥伦比亚,57;委内瑞拉,58;瓜德罗普,590;玻利维亚,591;圭亚那,592;厄瓜多尔,593;法属圭亚那,594;巴拉圭,595;马提尼克,596;苏里南,597;乌拉圭,598;安的列斯,599;马来西亚,60;澳大利亚,61;印度尼西亚,62;菲律宾,63;新西兰,64;新加坡,65;泰国,66;东帝汶,670;南极洲,672;文莱,673;瑙鲁,674;巴新,675;汤加,676;所罗门群岛,677;瓦努阿图,678;斐济,679;帕劳,680;库克群岛,682;纽埃,683;美属萨摩亚,684;萨摩亚,685;基里巴斯,686;新喀里多,687;图瓦卢,688;波利尼西亚,689;托克劳,690;密克罗尼,691;马绍尔群岛,692;俄罗斯,7;哈萨克斯坦,73;日本,81;韩国,82;越南,84;朝鲜,850;香港,852;澳门,853;柬埔寨,855;老挝,856;中国,86;孟加拉国,880;台湾,886;土耳其,90;印度,91;巴基斯坦,92;阿富汗,93;斯里兰卡,94;缅甸,95;马尔代夫,960;黎巴嫩,961;约旦,962;叙利亚,963;伊拉克,964;科威特,965;沙特阿拉伯,966;也门,967;阿曼,968;阿联酋,971;以色列,972;巴林,973;卡塔尔,974;不丹,975;蒙古,976;尼泊尔,977;伊朗,98;塔吉克斯坦,992;土库曼斯坦,993;阿塞拜疆,994;格鲁吉亚,995;吉尔吉斯,996;乌兹别克,998";
    // 国际短信区号
    private static HashMap<String, String> internationalMap = new HashMap<String, String>();
    static {
        String[] lines = internationalCode.split(";");
        for (String line : lines) {
            if (!"".equals(line)) {
                String[] segs = line.split(",");
                if (segs.length == 2) {
                    internationalMap.put(segs[1], segs[0]);
                }
            }
        }
    }

    /**
     * 验证扩展号
     *
     * @param ext
     * @return
     */
    public static boolean checkExtNumber(String ext) {
        return extNumberPat.matcher(ext).matches();
    }

    /**
     * 根据手机区号取国家名
     *
     * @param pn
     *            手机号码
     * @return 国家名
     */
    public static String getCountry(String pn) {
        int subfix = 1;
        if (pn.startsWith("00")) {
            subfix = 2;
        }
        //
        String country = internationalMap.get(pn.substring(subfix, subfix + 1));
        if (country != null) {
            return country;
        }
        //
        country = internationalMap.get(pn.substring(subfix, subfix + 2));
        if (country != null) {
            return country;
        }
        //
        country = internationalMap.get(pn.substring(subfix, subfix + 3));
        if (country != null) {
            return country;
        }
        return null;
    }


    /**
     * 仅判断是否有有效的号码
     *
     * @param destStr
     *            号码字符串
     * @return 是否有有效的手机号码
     */
    public static boolean checkValidDestNumber(String destStr) {
        try {
            Matcher destMat = destNumberPat.matcher(destStr);
            while (destMat.find()) {
                return true;
            }
        } catch (Exception ex) {
        }
        return false;
    }

    public static void main(String[] args) {
        String instr = "15259126724";
        System.out.println(MobileUtil.checkExtNumber(instr));
        //
		/*HashSet<String> res = getValidDestNumber(instr);
		for (String pn : res) {
			// System.out.println(pn);
		}
		//
		HashMap<String, Provider> res2 = sorting(instr);
		for (String pn : res2.keySet()) {
			System.out.println(pn);
			System.out.println(res2.get(pn));
			System.out.println(MobileUtil.getCountry(pn));
		}*/
    }


    /**
     * 判断用户号码的归属运营商
     *
     * @param pn
     *            用户号码
     * @return 运营商
     */
    public static String getProvider(String pn) {
        pn = pn.trim();
        if (pn.equals("") || pn.length() > 21) {
            return "0";
        }
        try {
            // 国际
            if (pn.startsWith("00") || pn.startsWith("+")) {
                return "4";
            }
            // 国内
            if (pn.length() == 11) {
                Integer p3 = Integer.valueOf(pn.substring(0, 3));
                for (Object s : CMCC_P3) {
                    if (s != null && p3.equals(s)) {
                        return "3";
                    }
                }
                for (Object s : UNICOM_P3) {
                    if (s != null && p3.equals(s)) {
                        return "2";
                    }
                }
                for (Object s : TELECOM_P3) {
                    if (s != null && p3.equals(s)) {
                        return "1";
                    }
                }

                Integer p4 = Integer.valueOf(pn.substring(0, 4));
                for (Object s : CMCC_P4) {
                    if (s != null && p4.equals(s)) {
                        return "3";
                    }
                }
                for (Object s : UNICOM_P4) {
                    if (s != null && p4.equals(s)) {
                        return "2";
                    }
                }
                for (Object s : TELECOM_P4) {
                    if (s != null && p4.equals(s)) {
                        return "1";
                    }
                }
            }
            // 未知
            return "0";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "0";
    }

    /**
     * 长短信分条
     *
     * @param content
     *            内容
     * @param sizeFirst
     *            首条计费
     * @param sizeCharge
     *            后续计费
     * @return 分割后的内容数组
     */
    public static String[] split(String content, int sizeFirst, int sizeCharge) {
        int len = content.length();
        if (len <= sizeFirst) {
            return new String[] { content };
        }

        // 长短信
        sizeFirst -= 3;
        String contentFirst = content.substring(0, sizeFirst);
        String contentOther = content.substring(sizeFirst);
        String[] contentOtherArray = characterExplode(contentOther, sizeCharge);

        //
        List<String> retList = new Vector<String>();
        retList.add(contentFirst);
        for (int i = 0; i < contentOtherArray.length; i++) {
            retList.add(contentOtherArray[i]);
        }

        return retList.toArray(new String[] {});
    }

    /**
     * 将字符串按一定长度分成数组--按字符
     *
     * @param in
     * @param length
     * @return
     */
    public static String[] characterExplode(String in, int length) {
        int size = in.length() / length;
        if (in.length() % length != 0) {
            size++;
        }
        String[] ret = new String[size];
        for (int i = 0; i < size; i++) {
            String t = in.substring(i * length, (i + 1) * length > in.length() ? in.length() : (i + 1) * length);
            ret[i] = t;
        }
        return ret;
    }

    /**
     * 计算计费条数
     *
     * @param contentLength
     *            内容长度
     * @param sizeFirst
     *            首条计费
     * @param sizeCharge
     *            后续计费
     * @return 计费条数
     */
    public static int getChargeNum(int contentLength, int sizeFirst, int sizeCharge) {
        if (contentLength <= sizeFirst) {
            return 1;
        }
        // 长短信
        sizeFirst -= 3;
        contentLength -= sizeFirst;
        int ret = 1;
        while (contentLength > 0) {
            contentLength -= sizeCharge;
            ret++;
        }
        return ret;
    }

    /**
     * 获取签名
     *
     * @param content
     *            短信内容
     * @return 数组，0=签名，1=签名位置，2=全角半角
     */
    public static String[] getSign(String content) {
        if (content.startsWith("【")) {
            int lpos = content.indexOf("】");
            if (lpos != -1) {
                String sign = content.substring(1, lpos);
                return new String[] { sign, HEAD_SIGN, FULL_WIDTH };
            }
        } else if (content.startsWith("[")) {
            int lpos = content.indexOf("]");
            if (lpos != -1) {
                String sign = content.substring(1, lpos);
                return new String[] { sign, HEAD_SIGN, HALF_WIDTH };
            }
        } else if (content.endsWith("】")) {
            int rpos = content.lastIndexOf("【");
            if (rpos != -1) {
                String sign = content.substring(rpos + 1, content.length() - 1);
                return new String[] { sign, TAIL_SIGN, FULL_WIDTH };
            }
        } else if (content.endsWith("]")) {
            int rpos = content.lastIndexOf("[");
            if (rpos != -1) {
                String sign = content.substring(rpos + 1, content.length() - 1);
                return new String[] { sign, TAIL_SIGN, HALF_WIDTH };
            }
        }
        return null;
    }

    /**
     * 签名前置
     *
     * @param content
     *            内容
     * @param trans2fullWidth
     *            是否半角转全角
     * @return 前置后的内容
     */
    public static String sign2header(String content, boolean trans2fullWidth) {
        String[] sign = MobileUtil.getSign(content);
        if (sign != null) {
            if (sign[1].equals(MobileUtil.HEAD_SIGN)) {
                // 签名本来在头部，只需替换全半角即可
                if (sign[2].equals(MobileUtil.HALF_WIDTH) && trans2fullWidth) {
                    content = "【" + sign[0] + "】" + content.substring(sign[0].length() + 2);
                }
            } else {
                // 签名在尾部
                content = content.substring(0, content.length() - sign[0].length() - 2);
                //
                if (sign[2].equals(MobileUtil.FULL_WIDTH) || trans2fullWidth) {
                    // 如果是全角或要转为全角
                    content = "【" + sign[0] + "】" + content;
                } else {
                    content = "[" + sign[0] + "]" + content;
                }
            }
        }
        return content;
    }
}
