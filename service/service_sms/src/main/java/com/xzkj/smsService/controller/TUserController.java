package com.xzkj.smsService.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xzkj.smsService.entity.TUser;
import com.xzkj.smsService.entity.User;
import com.xzkj.smsService.service.TUserService;
import com.xzkj.utils.Base64Util;
import com.xzkj.utils.Base64Utils;
import com.xzkj.utils.MD5Utils;
import com.xzkj.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author LinMengHao
 * @since 2022-10-13
 */

@Slf4j
@RefreshScope
@RestController
@RequestMapping("/baseservice/t-user")
public class TUserController {
    @Autowired
    TUserService service;
    @Value("${lmh.name}")
    private String name;
    @Value("${lmh.age}")
    private Integer age;

    @Value("${lmh.phone}")
    private List<Integer> phone=new ArrayList<>();

    @PostMapping("/test")
    public R test(@RequestBody String s){
        System.out.println("s: "+s);
        JSONObject jsonObject = JSONObject.parseObject(s);
        System.out.println("test: "+jsonObject.toJSONString());
        return R.ok();
    }
    @PostMapping(value = "findAll",consumes = {MediaType.APPLICATION_JSON_VALUE},
    produces = {MediaType.APPLICATION_JSON_VALUE})
    public R test(){
        List<TUser> list= service.findAll();
        log.info(list.toString());
        log.info("name: "+name);
        log.info("age: "+age);
        log.info("phone"+phone.toString());
        return R.ok();
    }
//    @Autowired
//    RestTemplate restTemplate;
//    @Test
//    public void test1() throws UnsupportedEncodingException {
//        long time = new Date().getTime();
//        System.out.println("ts:"+time);
//        String s = MD5Utils.MD5Encode("acc=xiuzhi&ts="+time+"&sign=1"+"|||pwd=xiuzhi66");
//        System.out.println("token:"+s);
//        JSONObject json=new JSONObject();
//        json.put("acc","xiuzhi");
//        json.put("ts",time);
//        json.put("sign","1");
//        String title= URLEncoder.encode("模版提交接口测试","UTF-8");
//        json.put("title",title);
//        json.put("var","1");
//        json.put("token",s);
//        JSONArray content=new JSONArray();
//        JSONObject data1=new JSONObject();
//        data1.put("type",1);
//        data1.put("ext","txt");
//        String body1=URLEncoder.encode("测试时间：$v1$ \r\n 测试人：$v2$","UTF-8");
//        data1.put("body",body1);
//        JSONObject data2=new JSONObject();
//        data1.put("type",2);
//        data1.put("ext","jpg");
//        String body2 = fileBase();
//        data1.put("body",body2);
//        content.add(data1);
//        content.add(data2);
//        json.put("content",content.toJSONString());
//        HttpHeaders headers=new HttpHeaders();
//        headers.set("Content-Type","application/json");
//        HttpEntity entity=new HttpEntity(json,headers);
//        ResponseEntity<String> response = restTemplate.postForEntity("http://103.29.16.3:9130/mms/model/submit", entity, String.class);
//        System.out.println("response: "+response.getBody());
//        JSONObject jsonObject2 = JSONObject.parseObject(response.getBody());
//        System.out.println("response: "+jsonObject2.toJSONString());
//    }
    @Test
    public void tes(){
        long time = new Date().getTime();
        System.out.println("ts:"+time);
        String s = MD5Utils.MD5Encode("acc=xiuzhi&ts="+time+"|||pwd=xiuzhi66");
        System.out.println("token:"+s);
    }
    @Test
    public void ts(){
        long time = new Date().getTime();
        System.out.println("ts:"+time);
        String s = MD5Utils.MD5Encode("acc=SzAdyV&ts="+time+"|||pwd=Ady@2022");
        System.out.println("token:"+s);
    }
    @Test
    public void ts1(){
        String apiKey="XIUZHIBJ01";
        String apiSecrect="x1&@9%&6";
        long reqTime=System.currentTimeMillis();
        System.out.println(reqTime);

        String s="mmsId="+"M02171223"+"&reqTime="+reqTime;
        String sign1= Base64Util.encodeBASE64(Base64Util.getSHA256(s));
        System.out.println(sign1);

        String signStr="apiKey="+apiKey+"&apiSecrect="+apiSecrect+"&reqTime="+reqTime;
        String sign= Base64Util.encodeBASE64(Base64Util.getSHA256(signStr));
        System.out.println(sign);
    }
    @Test
    public void ts2(){
        long reqTime=System.currentTimeMillis();
        System.out.println(reqTime);

    }
    @Test
    public void tes45(){
        long time = new Date().getTime();
        System.out.println("ts:"+time);
        String s = MD5Utils.MD5Encode("acc=SzAdyV&ts="+time+"|||pwd=Ady@2022");
        System.out.println("token:"+s);
    }
    @Test
    public void urlDecoder() throws UnsupportedEncodingException {
        String decode = URLDecoder.decode("%E6%B5%8B%E8%AF%95%E6%97%B6%E9%97%B4%EF%BC%9A%24v1%24+%0D%0A+%E6%B5%8B%E8%AF%95%E4%BA%BA%EF%BC%9A%24v2%24", "UTF-8");
        String decode1 = URLDecoder.decode("测试时间：$v1$ \n" +
                " 测试人：$v2$", "UTF-8");
        System.out.println(decode);
        System.out.println(decode1);
    }
    public String fileBase(){
        File file=new File("/Users/yoca-391/Desktop/works/xiuzhiMms20221216/platform/filesmodelFile/20230104/g1_20230104124720A001.gif");
        byte[] bytes=null;
        FileInputStream in = null;
        try{
            in=new FileInputStream(file);
            bytes=new byte[in.available()];
            in.read(bytes);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        byte[] encode = Base64.getEncoder().encode(bytes);
        System.out.println(new String(encode));
        return new String(encode);
    }
    @Test
    public void fileBaseTest() throws IOException {
        File file=new File("/Users/yoca-391/Desktop/works/xiuzhiMms20221216/platform/filesmodelFile/20230104/g1_20230104124720A001.gif");
        byte[] bytes=null;
        FileInputStream in = null;
        try{
            in=new FileInputStream(file);
            bytes=new byte[in.available()];
            in.read(bytes);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        byte[] encode = Base64.getEncoder().encode(bytes);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("fff","xxx");
        jsonObject.put("file",new String(encode));
        String decode = URLDecoder.decode(jsonObject.toJSONString(), "UTF-8");
        JSONObject jsonObject1 = JSONObject.parseObject(decode);
        String file2 = jsonObject1.getString("file");
        byte[] buffer = org.apache.commons.codec.binary.Base64.decodeBase64(file2.getBytes());
        int size = buffer.length/1024;
        File file1 = new File("/Users/yoca-391/Desktop/works/xiuzhiMms20221216/platform/filesmodelFile/20230104/g1_20230104124720A002.gif");
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
            file1.createNewFile();
        }

        FileOutputStream out = new FileOutputStream("/Users/yoca-391/Desktop/works/xiuzhiMms20221216/platform/filesmodelFile/20230104/g1_20230104124720A002.gif");//覆盖已存在文件，FileOutputStream(targetPath,true)在已存在文件后面追加内容
        out.write(buffer);
    }

    @Test
    public void shanghai(){
        JSONObject jsonObject=new JSONObject();
        String siId="SH20230104";
        String key="HZt7pD1wRo";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        String authenticator = MD5Utils.MD5Encode(siId+date+key).toUpperCase();
        jsonObject.put("SiID",siId);
        jsonObject.put("Authenticator",authenticator);
        jsonObject.put("Date",date);
        jsonObject.put("Method","whitelist");
        jsonObject.put("Type",1);
        String[] phones={"18908002166"};
        jsonObject.put("Phones",phones);
        System.out.println(jsonObject.toString());
    }


    @Test
    public void test2(){
        JSONObject jsonObject=new JSONObject();
        JSONObject params=new JSONObject();
        String[] split = "hhh,lll,xxx".split(",");
        for (int i = 1; i <= split.length; i++) {
            String key="v"+i;
            params.put(key,split[i-1]);
        }
        jsonObject.put("vars",params.toString());
        System.out.println(params.toJSONString());
        System.out.println(jsonObject.toJSONString());
    }
    @Test
    public void test3(){
        String spid="1195";
        String login="admin";
        String pwd= MD5Utils.MD5Encode(login+"!yJIE7TU6").toUpperCase();
        JSONObject submitJson = new JSONObject();
        submitJson.put("phones","18756232770");
        submitJson.put("sn","20221116111300001");
        submitJson.put("spid",spid);
        submitJson.put("login",login);
        submitJson.put("pwd",pwd);
        submitJson.put("tid","221116102159200002");
        System.out.println(submitJson.toJSONString());
    }
    @Test
    public void test4() throws UnsupportedEncodingException {
        String decode = URLDecoder.decode("");
        System.out.println(decode);
    }
    @Test
    public void test_4() throws UnsupportedEncodingException {
        String decode = URLDecoder.decode("body=reports=221126151240129669%2C17398515826%2C0%2CDELIVRD%2C20221126_1500033734337%2C20221126151313%3B221126151246129875%2C18056415197%2C0%2CDELIVRD%2C20221126_1500054904543%2C20221126151313%3B221126151240129667%2C17398339618%2C0%2CDELIVRD%2C20221126_1500033564335%2C20221126151313%3B221126151243129786%2C17777630539%2C0%2CDELIVRD%2C20221126_1500042484454%2C20221126151313%3B221126151242129755%2C17774890794%2C0%2CDELIVRD%2C20221126_1500040974422%2C20221126151313%3B221126151245129847%2C18032271273%2C0%2CDELIVRD%2C20221126_1500051134515%2C20221126151313%3B221126151242129734%2C17758616859%2C0%2CDELIVRD%2C20221126_1500039294402%2C20221126151313%3B221126151245129849%2C18039076700%2C0%2CDELIVRD%2C20221126_1500051324517%2C20221126151313%3B221126151242129763%2C17776460829%2C0%2CDELIVRD%2C20221126_1500041364431%2C20221126151313%3B221126151239129648%2C17377562275%2C0%2CDELIVRD%2C20221126_1500031034315%2C20221126151313%3B221126151242129757%2C17775290716%2C0%2CDELIVRD%2C20221126_1500041174426%2C20221126151313%3B221126151245129840%2C18030023050%2C0%2CDELIVRD%2C20221126_1500050454508%2C20221126151313%3B221126151239129638%2C17376225119%2C0%2CDELIVRD%2C20221126_1500030184306%2C20221126151313%3B221126151241129715%2C17738009361%2C0%2CDELIVRD%2C20221126_1500037774383%2C20221126151313%3B221126151242129768%2C17776570322%2C0%2CDELIVRD%2C20221126_1500041634436%2C20221126151313%3B221126151244129813%2C18004872670%2C0%2CDELIVRD%2C20221126_1500043844481%2C20221126151313%3B221126151241129726%2C17755894181%2C0%2CDELIVRD%2C20221126_1500038804394%2C20221126151313%3B221126151244129809%2C17799269515%2C0%2CDELIVRD%2C20221126_1500043474476%2C20221126151313%3B221126151243129796%2C17785195832%2C0%2CDELIVRD%2C20221126_1500042944465%2C20221126151313%3B221126151243129783%2C17777555797%2C0%2CDELIVRD%2C20221126_1500042314451%2C20221126151313%3B221126151243129777%2C17777334210%2C0%2CDELIVRD%2C20221126_1500042024445%2C20221126151313%3B221126151243129775%2C17776578868%2C0%2CDELIVRD%2C20221126_1500041934443%2C20221126151313%3B221126151242129751%2C17772033086%2C0%2CDELIVRD%2C20221126_1500040784419%2C20221126151313%3B221126151243129788%2C17777995869%2C0%2CDELIVRD%2C20221126_1500042574456%2C20221126151313%3B221126151243129769%2C17776447565%2C0%2CDELIVRD%2C20221126_1500041644437%2C20221126151313%3B221126151243129771%2C17776667350%2C0%2CDELIVRD%2C20221126_1500041744439%2C20221126151313%3B221126151245129843%2C18032725309%2C0%2CDELIVRD%2C20221126_1500050744511%2C20221126151313%3B221126151245129869%2C18052843602%2C0%2CDELIVRD%2C20221126_1500054344537%2C20221126151313%3B221126151242129758%2C17774851306%2C0%2CDELIVRD%2C20221126_1500041084425%2C20221126151313%3B221126151241129731%2C17758596083%2C0%2CDELIVRD%2C20221126_1500039114399%2C20221126151313%3B221126151245129863%2C18046741896%2C0%2CDELIVRD%2C20221126_1500052594531%2C20221126151313%3B221126151243129773%2C17776714883%2C0%2CDELIVRD%2C20221126_1500041834441%2C20221126151313%3B221126151243129770%2C17776575332%2C0%2CDELIVRD%2C20221126_1500041734438%2C20221126151313%3B221126151247129930%2C18078511157%2C0%2CDELIVRD%2C20221126_1500060444598%2C20221126151313%3B221126151248129955%2C18093811052%2C0%2CDELIVRD%2C20221126_1500063504623%2C20221126151313%3B221126151248129965%2C18105632093%2C0%2CDELIVRD%2C20221126_1500064444633%2C20221126151313%3B221126151243129792%2C17777683833%2C0%2CDELIVRD%2C20221126_1500042744460%2C20221126151313%3B221126151246129908%2C18077048921%2C0%2CDELIVRD%2C20221126_1500057914576%2C20221126151313%3B221126151239129646%2C17377288615%2C0%2CDELIVRD%2C20221126_1500030934314%2C20221126151313%3B221126151246129887%2C18068373641%2C0%2CDELIVRD%2C20221126_1500056024555%2C20221126151313%3B221126151246129878%2C18056966030%2C0%2CDELIVRD%2C20221126_1500055174546%2C20221126151313%3B221126151245129868%2C18054052871%2C0%2CDELIVRD%2C20221126_1500054244536%2C20221126151313%3B221126151243129785%2C17777440140%2C0%2CDELIVRD%2C20221126_1500042404453%2C20221126151313%3B221126151248129959%2C18097517150%2C0%2CDELIVRD%2C20221126_1500063854627%2C20221126151313%3B221126151246129900%2C18076424604%2C0%2CDELIVRD%2C20221126_1500057184568%2C20221126151313%3B221126151248129945%2C18090218313%2C0%2CDELIVRD%2C20221126_1500061814613%2C20221126151313%3B221126151246129877%2C18056966982%2C0%2CDELIVRD%2C20221126_1500055084545%2C20221126151313%3B221126151248129960%2C18095874802%2C0%2CDELIVRD%2C20221126_1500063944628%2C20221126151313%3B221126151246129906%2C18075208279%2C0%2CDELIVRD%2C20221126_1500057724574%2C20221126151313%3B221126151248129974%2C18110542667%2C0%2CDELIVRD%2C20221126_1500065234642%2C20221126151313%3B221126151246129893%2C18070870513%2C0%2CDELIVRD%2C20221126_1500056584561%2C20221126151313%3B221126151246129901%2C18074927028%2C0%2CDELIVRD%2C20221126_1500057274569%2C20221126151313%3B221126151248129967%2C18105526675%2C0%2CDELIVRD%2C20221126_1500064624635%2C20221126151313%3B221126151246129902%2C18075388785%2C0%2CDELIVRD%2C20221126_1500057364570%2C20221126151313%3B221126151246129907%2C18076707942%2C0%2CDELIVRD%2C20221126_1500057824575%2C20221126151314%3B221126151245129873%2C18055555795%2C0%2CDELIVRD%2C20221126_1500054714541%2C20221126151313%3B221126151247129927%2C18078312516%2C0%2CDELIVRD%2C20221126_1500060164595%2C20221126151313%3B221126151247129922%2C18077874791%2C0%2CDELIVRD%2C20221126_1500059674590%2C20221126151313%3B221126151246129890%2C18070838856%2C0%2CDELIVRD%2C20221126_1500056304558%2C20221126151313%3B221126151246129885%2C18070699753%2C0%2CDELIVRD%2C20221126_1500055834553%2C20221126151314%3B221126151248129976%2C18113188569%2C0%2CDELIVRD%2C20221126_1500065404644%2C20221126151314%3B221126151247129932%2C18078733554%2C0%2CDELIVRD%2C20221126_1500060624600%2C20221126151314%3B221126151247129928%2C18078279322%2C0%2CDELIVRD%2C20221126_1500060264596%2C20221126151314%3B221126151247129912%2C18077306782%2C0%2CDELIVRD%2C20221126_1500058294580%2C20221126151314%3B221126151247129919%2C18078033424%2C0%2CDELIVRD%2C20221126_1500059374587%2C20221126151314%3B221126151248129977%2C18110864194%2C0%2CDELIVRD%2C20221126_1500065484645%2C20221126151314%3B221126151248129952%2C18096900049%2C0%2CDELIVRD%2C20221126_1500063224620%2C20221126151314%3B221126151243129787%2C17777575228%2C0%2CDELIVRD%2C20221126_1500042494455%2C20221126151314%3B221126151245129870%2C18055528097%2C0%2CDELIVRD%2C20221126_1500054434538%2C20221126151314%3B221126151245129862%2C18051771261%2C0%2CDELIVRD%2C20221126_1500052514530%2C20221126151314%3B221126151247129923%2C18078225806%2C0%2CDELIVRD%2C20221126_1500059774591%2C20221126151314%3B221126151246129882%2C18056175665%2C0%2CDELIVRD%2C20221126_1500055554550%2C20221126151314%3B221126151248129968%2C18107365626%2C0%2CDELIVRD%2C20221126_1500064714636%2C20221126151314%3B221126151248129971%2C18108905791%2C0%2CDELIVRD%2C20221126_1500064974639%2C20221126151314%3B221126151247129913%2C18077338363%2C0%2CDELIVRD%2C20221126_1500058384581%2C20221126151314%3B221126151246129888%2C18070906059%2C0%2CDELIVRD%2C20221126_1500056114556%2C20221126151314%3B221126151247129918%2C18077929273%2C0%2CDELIVRD%2C20221126_1500059274586%2C20221126151314%3B221126151247129911%2C18077191543%2C0%2CDELIVRD%2C20221126_1500058194579%2C20221126151314%3B221126151247129917%2C18077462474%2C0%2CDELIVRD%2C20221126_1500059174585%2C20221126151314%3B221126151248129956%2C18098527629%2C0%2CDELIVRD%2C20221126_1500063584624%2C20221126151314%3B221126151247129925%2C18078176719%2C0%2CDELIVRD%2C20221126_1500059964593%2C20221126151314%3B221126151248129980%2C18116886768%2C0%2CDELIVRD%2C20221126_1500065764648%2C20221126151314%3B221126151246129880%2C18056995068%2C0%2CDELIVRD%2C20221126_1500055364548%2C20221126151314%3B221126151248129979%2C18113689594%2C0%2CDELIVRD%2C20221126_1500065674647%2C20221126151314%3B221126151248129961%2C18098757828%2C0%2CDELIVRD%2C20221126_1500064084629%2C20221126151314%3B221126151246129903%2C18076318582%2C0%2CDELIVRD%2C20221126_1500057454571%2C20221126151314%3B221126151246129909%2C18076798712%2C0%2CDELIVRD%2C20221126_1500058004577%2C20221126151314%3B221126151247129935%2C18078567843%2C0%2CDELIVRD%2C20221126_1500060944603%2C20221126151314%3B221126151247129914%2C18076711925%2C0%2CDELIVRD%2C20221126_1500058484582%2C20221126151314%3B221126151247129926%2C18077551728%2C0%2CDELIVRD%2C20221126_1500060064594%2C20221126151314%3B221126151241129714%2C17745637277%2C0%2CDELIVRD%2C20221126_1500037694382%2C20221126151314%3B221126151242129739%2C17761131691%2C0%2CDELIVRD%2C20221126_1500039584409%2C20221126151314%3B221126151241129722%2C17755161955%2C0%2CDELIVRD%2C20221126_1500038314389%2C20221126151314%3B");

        System.out.println(decode);
    }
    @Test
    public void test5(){
        Map<String,String> map=new HashMap<>();
        map.put("phone","18756232770");
        map.put("param","99999");
        List<Map<String,String>> list=new ArrayList<>();
        list.add(map);
        JSONObject submitJson = new JSONObject();
        submitJson.put("phones",list);
        System.out.println("map: "+map.toString());
        System.out.println("list: "+list.toString());
        System.out.println("json: "+submitJson.toJSONString());
    }
    @Test
    public void test6(){
        JSONObject submitJson = new JSONObject();
        submitJson.put("Phones","mobile");
        submitJson.put("SiID","siId");
        submitJson.put("Authenticator","authenticator");
        submitJson.put("Date","date");
        submitJson.put("Method","send");
        submitJson.put("MsgID","channelMmsId");
        List<JSONObject> content=new ArrayList<>();
        submitJson.put("Method","option");
        String channelParam="v1=djskdj,v2=dshdksj,v3=shdsj,v4=oqieo3h4";
        String[] split = channelParam.split(",");
        for (int i = 1; i <= split.length; i++) {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("Frame","001-"+i);
            JSONObject jsonObject1=new JSONObject();
            String[] value=split[i-1].split("=");
            jsonObject1.put(value[0],value[1]);
            jsonObject.put("Param",jsonObject1);
            content.add(jsonObject);
        }
        submitJson.put("content",content);
        System.out.println(submitJson.toJSONString());

        List<String> reportList = submitJson.containsKey("content")?submitJson.getObject("content",List.class):new ArrayList<>();
        System.out.println(reportList.toString());
    }

    @Test
    public void test7(){
        JSONObject submitJson = new JSONObject();
        submitJson.put("content","[{\"type\":1,\"ext\":\"txt\",\"body\":\"…\"},{\"type\":2,\"ext\":\"jpg\",\"body\":\"…\"}]");

        JSONArray content = submitJson.getJSONArray("content");
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("name","lmh");
        content.add(jsonObject);
        System.out.println(JSONObject.parseObject(content.getString(2)).get("name"));
    }
    @Test
    public void test8(){
        String channelParam="date1=11月28日";
        StringBuilder stringBuilder=new StringBuilder();
        String[] split = channelParam.split(",");
        for (int i = 0; i < split.length; i++) {
            String[] split1 = split[i].split("=");
            stringBuilder.append(split1[1]);
            if(i!=split.length-1){
                stringBuilder.append(",");
            }
        }
        System.out.println(stringBuilder.toString());
    }
    @Test
    public void test9() {
        JSONObject json=new JSONObject();
        List<String> list=new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        JSONObject param=new JSONObject();
        param.put("v1","hhh");
        param.put("v2","xxx");
        jsonObject.put("Frame","1-2");
        jsonObject.put("Param",param);
        System.out.println(jsonObject.toJSONString());
        System.out.println(jsonObject.toString());
        list.add(jsonObject.toString());
        System.out.println(list);
        json.put("appName","xiuzhi");
        json.put("content",list);
        JSONObject jsonObject1 = JSONObject.parseObject(json.toString());
        List content = (List)jsonObject1.get("content");
        System.out.println(content.get(0));
    }
    @Test
    public void test10() throws UnsupportedEncodingException {
        String s="44CQ6b6Z5Z+O5Yaz44CR6K+35Li75pKt5b2T5YaF6ay877yM6KaB5Lu35aSq6auY77yM5bmz5Y+w5Yaz5a6a5pS+5Y+357uZ5pmu6YCa546p5a62DQrnm7TmjqXov5vmnI3vvJogaHR0cHM6Ly9iaXo5LmNuLyR1cmwxJCANCui0ouWKoemDqOmZiOaCpue7meaCqOWQjeS4i+i0puWPt+i9rOWFpeS6hjYwMOW8oDc5OTjvvIzor7fmgqjluKbmnI3vvIzmnInmhI/lkJHnmoTor53nm7TmjqXov5vmnI0NCg0K6LSm5Y+36K+m5oOFDQrnrYnnuqfvvJpWMzDkvJrlkZjvvIjmu6HvvIkNCuaXpeiWqu+8mjY25bygMzI477yMNjblvKA2NDgNCuemj+WIqe+8muWMuuacjTY2MOS6v+mSu+efs++8jDQyMOS4h+S8muWRmOenr+WIhg0K5pe25pWI77ya5rC45LmFDQoNCjY2Nu+8mjY0OCoxMCvlnKPngavku6QqMyvliJ3nuqfnm77niYznoo7niYcqMTAr5Yid57qn5pe26KOF56KO54mHKjEwDQo4ODjvvJo2NDgqMTAr5Zyj54Gr5LukKjIr6b6Z6aqoKjEwK+WHpOihgCoxMA0KOTk577yaNjQ4KjEwK+Wco+eBq+S7pCozK+aWl+esoOeijueJhyoxMCvlpKnompXkuJ0qMjANClZJUDY2Nu+8mjY0OCoxMCvml6Dph4/mmbYqMTAr5paX56yg56KO54mHKjIwK+WkqeialeS4nSoyMA0KVklQODg477yaNjQ4KjEwK+aKgOiDveaui+mhtSoyMCvpvpnpqqgqMzAr5Yek6KGAKjMwDQpWSVA5OTnvvJo2NDgqMTAr5ZG96L+Q55+zKjMr5oqA6IO95q6L6aG1KjEwK+aXoOmHj+aZtioyMA0KU1ZJUDY2Nu+8mjY0OCoxMCvmioDog73mrovpobUqMjAr5ZG96L+Q55+zKjEwK+eJueaIkueBteeyuSoyMA0KU1ZJUDg4OO+8mjY0OCoxMCvlkb3ov5Dnn7MqMTAr6b6Z6aqoKjUwK+WHpOihgCo1MA0KU1ZJUDk5Oe+8mjY0OCoxMCvml6Dph4/mmbYqMzAr5Zyj54Gr5LukKjEwK+WInee6p+aXtuijheeijueJhyo1MA0KDQrlhZHmjaLpgJrpgZPvvJogaHR0cHM6Ly9iaXo5LmNuLyR1cmwyJCANCuaLv+WIsOi1hOa6kOS5n+S4jeeUqOWkque0p+W8oOOAgui3n+W5s+aXtueOqea4uOaIj+S4gOagt++8jOeIveWwseWujOS6hu+8jOiusOW+l+S4jeimgei3n+WFtuWug+eOqeWutuiusg0K6YCA6K6i5ZueVA==";
        String decode = Base64Utils.decode(s);
        System.out.println(decode);
    }
    @Test
    public void test11() {
        File file=new File("/Users/yoca-391/Desktop/works/xiuzhiMms20221216/platform/files/modelFile/20230109/3sucai_20230109111101A001.jpg");
        String name1 = file.getName();
        System.out.println(name1);
    }
    @Test
    public void test12() {
        String uploadFile="http://localhost:9100/profile/signFile/20230202/1sucai_20230202113155A001.jpg";
        String[] profiles = uploadFile.trim().split("/profile/");
        System.out.println("/Users/yoca-391/Desktop/works/xiuzhiMms20221216/platform/files/"+profiles[profiles.length-1]);
    }
    @Test
    public void test13(){
        String apiKey="TBjhxM4";
        String apiSecrect="9zpYPCFy";
        Date date1 = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String date2 = sdf.format(date1);
        String token= MD5Utils.MD5Encode(apiKey+date2+apiSecrect).toLowerCase();
        String authorization=Base64Util.encodeBASE64(apiKey+":"+date2);
        System.out.println("token: "+token);
        System.out.println("authorization: "+authorization);
    }
    @Test
    public void test14() throws UnsupportedEncodingException {

        String s="ChnlNo=C0190&IPass=123456&IUser=test12&Mobile=18756232770&MsgId=20230210497027431001&RptTime=20230210121135165&Stat=SF%3A0115";
        String[] split = s.split("&");
        JSONObject jsonObject=new JSONObject();
        for (int i = 0; i < split.length; i++) {
            String[] split1 = split[i].split("=");
            jsonObject.put(split1[0],split1[1]);
        }
        System.out.println(jsonObject.toJSONString());
        System.out.println(jsonObject.getString("MsgId"));

        String decode = URLDecoder.decode("SF%3A0115");
        System.out.println(decode);
        String decode1 = URLDecoder.decode(s,"utf-8");
        System.out.println(decode1);
    }
    @Test
    public void test15() throws UnsupportedEncodingException {
        String ecProvince="北京";
        String ecCity="北京";
        String serviceCode="106572050030398";
        String ecName="测试虚拟网关";
        String rcsIndustry="1,2,3";
        String customerType="0";
        String operatorType="3";
        String reportSignContent="API接口自动新增签名1666855672738";
        String industry="2";

        StringBuilder s=new StringBuilder();
        s.append("customerType=").append(customerType).append("&")
                .append("ecCity=").append(ecCity).append("&")
                .append("ecName=").append(ecName).append("&")
                .append("ecProvince=").append(ecProvince).append("&")
                .append("industry=").append(industry).append("&")
                .append("operatorType=").append(operatorType).append("&")
                .append("rcsIndustry=").append(rcsIndustry).append("&")
                .append("reportSignContent").append(reportSignContent).append("&")
                .append("serviceCode=").append(serviceCode);

        String sign= Base64Util.encodeBASE64(DigestUtils.sha256Hex(URLDecoder.decode(s.toString(),"UTF-8").getBytes(StandardCharsets.UTF_8)));
        System.out.println(sign);
        String sign1 = getSign(s.toString());
        System.out.println(sign1);
        String s1="apiKey=15-23930392033092&apiSecrect=#239*3&reqTime=1642670601273";
        String sign2 = getSign(s1);
        System.out.println(sign2);
    }
    private String getSign(String s){
        //格式：ecProvince=${ecProvince}&ecCity=${ecCity}&serviceCode=${serviceCode}&reportSignContent=${reportSignContent}&ecName=${ecName}&rcsIndustry=${rcsIndustry}&industry=${industry}&customerType=${customerType}&operatorType=${operatorType}
        String[] split = s.split("&");
        TreeMap strMap=new TreeMap();
        for (int i = 0; i < split.length; i++) {
            String[] pandvalue = split[i].split("=");
            if (pandvalue.length>1){
                String[] values = Arrays.copyOfRange(pandvalue, 1, pandvalue.length);
                if(!"".equals(values[0])){
                    strMap.put(pandvalue[0],values[0]);
                }
            }else {
//				strMap.put(pandvalue[0],null);
            }

        }
        String sign="";
        Set set=strMap.entrySet();
        Iterator iterator= set.iterator();
        int iterationCount=0;
        while(iterator.hasNext()){
            Map.Entry mentry=(Map.Entry)iterator.next();
            if (iterationCount==0){
                sign+=mentry.getKey()+"="+mentry.getValue();
            }else {
                sign+="&"+mentry.getKey()+"="+mentry.getValue();
            }
            iterationCount++;
        }
        try {
            sign= URLDecoder.decode(sign,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(sign);

        String s1 = DigestUtils.sha256Hex(sign);
        String s2 = Base64Util.encodeBASE64(s1.getBytes(StandardCharsets.UTF_8));
        String s3 = Base64Util.encodeBASE64(Base64Util.getSHA256(sign));
        System.out.println(s3);
        return s2;
    }

    @Test
    public void test16() throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = sdf.parse("2023-01-10 18:25:34");
        Date date = new Date();
        int i = differentDays(parse, date);
        System.out.println(i);
        Long days=null;
        Long day=days!=null?days:90L;
        System.out.println(day);
    }

    /**
     * date2比date1多的天数
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1,Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2)   //不同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        }
        else    //同一年
        {
            System.out.println("判断day2 - day1 : " + (day2-day1));
            return day2-day1;
        }
    }
}


