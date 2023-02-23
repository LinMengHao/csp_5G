package com.xzkj.taskPorxy.conf;

public class GlobalBlackFilterConfig {
    /**
     * 能静黑名单配置
     */
    public static String NJ_USERNAME="bjxz01";
    public static String NJ_PASSWORD="405088";
    //客户端url
    public static String NJ_SECRETKEY="0WoIOu2js$q89fzU";
    public static String NJ_SERVICEURL="http://150.158.105.68:8080/hbck/check";
    /**
     * 江苏
     */
   // public static List<Integer> JS_BLACK_LEVEL=new ArrayList<>();

    /**
     * 黑名单过滤策略
     */
   // public static List<String> BLACK_SORT=new ArrayList<>();

    /**
     * 东云短信
     */
    public static String DY_DX_ACCESSKEY="0673a1c2c6930de3ec1ab3400cdb3b15";
    public static String DY_DX_SECRETKEY="5b21c56c6f22384132e20e6d0c577032";
    public static String DY_DX_SERVICEURL="http://www.bsats.cn/forbid.php";


    /**
     * 东云语音
     */
    public static  String DY_VOICE_ACCESSKEY="ac2a2b551cd7dee795092f21f532dcb1";
    public static  String DY_VOICE_SECRETKEY="3b514e1b56787ff6328004dc00a0f83b";
    public static  String DY_VOICE_SERVICEURL="http://www.bsats.cn/forbid.php";

    /**
     * 修治私有黑名单库
     */
    //public static  List<String> XZ_BLACK=new ArrayList<>();

    /**
     * 修治私有白名单库
     */
    //public static  List<String> XZ_WHITE=new ArrayList<>();

   // public static List<Integer> BLACK_PERCENTAGE=new ArrayList<>();

   // static {
        //BufferedReader reader=null;
        //BufferedReader reader1=null;
       // InputStream inputStream = null;
       // try {
//            reader=new BufferedReader(new FileReader(new File("/Users/yoca-391/Desktop/works/xiuzhiMms/task_proxy/src/main/resources/xzblack.txt")));
            //reader=new BufferedReader(new FileReader(new File("/home/platform/sms_video/xzblack.txt")));
//            reader1=new BufferedReader(new FileReader("/Users/yoca-391/Desktop/works/xiuzhiMms/task_proxy/src/main/resources/xzwhite.txt"));
            //reader1=new BufferedReader(new FileReader("/home/platform/sms_video/xzwhite.txt"));
//            inputStream = new FileInputStream(new File("/Users/yoca-391/Desktop/works/xiuzhiMms/task_proxy/src/main/resources/black.properties"));
        //    inputStream = new FileInputStream(new File("/home/platform/sms_video/black.properties"));
       // } catch (FileNotFoundException e) {
       //     throw new RuntimeException(e);
       // }
//
       // Properties properties = new Properties();
      //  try {
            /*String s;
            while ((s=reader.readLine())!=null){
                XZ_BLACK.add(s);
            }*/
            /*while ((s=reader1.readLine())!=null){
                XZ_WHITE.add(s);
            }*/
          //  properties.load(inputStream);
     //   } catch (IOException e) {
     //       throw new RuntimeException(e);
      //  }
        /*String levels = properties.getProperty("JS.BLACK.LEVEL");
        if (StringUtils.isNotBlank(levels)){
            String[] split = levels.split(",");
            for (int i = 0; i < split.length; i++) {
                JS_BLACK_LEVEL.add(Integer.valueOf(split[i]));
            }
        }
        NJ_SECRETKEY = properties.getProperty("NJ.SECRETKEY");
        String property = properties.getProperty("BLACK.SORT");
        String[] split1 = property.split(",");
        for (int i = 0; i < split1.length; i++) {
            BLACK_SORT.add(split1[i]);
        }
        String property1 = properties.getProperty("BLACK.PERCENTAGE");
        String[] split2 = property1.split(",");
        for (int i = 0; i < split2.length; i++) {
            BLACK_PERCENTAGE.add(Integer.valueOf(split2[i]));
        }

        NJ_USERNAME = properties.getProperty("NJ.USERNAME");
        NJ_PASSWORD = properties.getProperty("NJ.PASSWORD");
        NJ_SERVICEURL = properties.getProperty("NJ.SERVICEURL");

        DY_DX_ACCESSKEY = properties.getProperty("DY.DX.ACCESSKEY");
        DY_DX_SECRETKEY = properties.getProperty("DY.DX.SECRETKEY");
        DY_DX_SERVICEURL = properties.getProperty("DY.DX.SERVICEURL");

        DY_VOICE_ACCESSKEY = properties.getProperty("DY.VOICE.ACCESSKEY");
        DY_VOICE_SECRETKEY = properties.getProperty("DY.VOICE.SECRETKEY");
        DY_VOICE_SERVICEURL = properties.getProperty("DY.VOICE.SERVICEURL");
        try {
            inputStream.close();
            //reader.close();
            //reader1.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
  //  }

    /*public static void main(String[] args) {
        System.out.println(NJ_PASSWORD);
        System.out.println(NJ_SECRETKEY);
        System.out.println(NJ_SERVICEURL);
        System.out.println(NJ_USERNAME);
        System.out.println(JS_BLACK_LEVEL.toString());
        System.out.println(BLACK_SORT.toString());
        System.out.println(DY_DX_ACCESSKEY);
        System.out.println(DY_DX_SECRETKEY);
        System.out.println(DY_DX_SERVICEURL);
        System.out.println(DY_VOICE_SECRETKEY);
        System.out.println(DY_VOICE_ACCESSKEY);
        System.out.println(DY_VOICE_SERVICEURL);
        //System.out.println(XZ_BLACK.toString());
       // System.out.println(XZ_WHITE.toString());
        System.out.println(BLACK_PERCENTAGE.toString());
    }*/
}
