package com.xzkj.accessService.utils;

import com.xzkj.accessService.entity.msgModel.FileInfo;
import com.xzkj.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
@Slf4j
public class XMLUtil {


    /**
     * //TODO 利用反射机制，做通用优化
     * @param list file文件列表
     * @param root 根结点
     * @param xmlns
     * @return
     */
    public static String FileTemplateXml(List<FileInfo> list, String root, String xmlns){
        String xmlStr=null;
        try {
            Document document = DocumentHelper.createDocument();
            Element files = document.addElement(root, xmlns);
            for (int i = 0; i < list.size(); i++) {
                FileInfo fileInfo=list.get(i);
                Element file = files.addElement("file-info");
                if(StringUtils.hasText(fileInfo.getType())){
                    file.addAttribute("type",fileInfo.getType());
                }

                if(StringUtils.hasText(fileInfo.getFileSize())){
                    Element file_size = file.addElement("file-size");
                    file_size.setText(fileInfo.getFileSize());
                }

                if(StringUtils.hasText(fileInfo.getFileName())){
                    Element file_name = file.addElement("file-name");
                    file_name.setText(fileInfo.getFileName());
                }

                if(StringUtils.hasText(fileInfo.getContentType())){
                    Element content_type = file.addElement("content-type");
                    content_type.setText(fileInfo.getContentType());
                }

                Map<String, String> map = fileInfo.getData();
                if(map.containsKey("url")&&StringUtils.hasText(map.get("url"))){
                    Element data = file.addElement("data");
                    data.addAttribute("url",map.get("url"));
                    if (map.containsKey("until")&&StringUtils.hasText(map.get("until"))){
                        data.addAttribute("until",map.get("until"));
                    }
                }

            }
            xmlStr = getXmlStr(document, "UTF-8", false,false,"/Users/yoca-391/Documents/demo01/xmlfiles/test1.xml");
        }catch (Exception e){
            e.printStackTrace();
        }
        return xmlStr;
    }

    /**
     * 构造消息体xml
     * @param map 参数为value，标签为key  其中奇偶将true，false分开
     * @param root 根结点
     * @param xmlns 根结点的属性
     * @return xml字符串
     */
    //纯文本消息体（xml格式）  TODO 日志打印 xml内容
    public static String txtTemplateXml(Map<String,String> map,String root,String xmlns,String el){
        String xmlStr=null;
        try{
            Document document=DocumentHelper.createDocument();
            //根结点(一级标签)
            Element element = document.addElement(root, xmlns);
            //（二级标签）
            Element outboundIMMessage = element.addElement(el);
            //（三级标签）
            Element serviceCapability = outboundIMMessage.addElement("serviceCapability");

            int count=0;
            for (Map.Entry<String,String> entry:map.entrySet()){
                String key=entry.getKey();
                String value=entry.getValue();
                //一级标签所属
                //电话标签,值为电话号码，多个号码时，用逗号隔开
                if("destinationAddress".equals(key)){
                    if(value.indexOf(",")!=-1){
                        String[] split = value.split(",");
                        //address 多个或者一个目标地址时，默认写第一个。
                        Element address = element.addElement("address");
                        address.setText("tel:+86"+split[0]);
                        for (int i = 0; i < split.length; i++) {
                            Element phoneNum = element.addElement(key);
                            phoneNum.setText("tel:+86"+split[i]);
                        }
                    }else if(StringUtils.hasText(value)){
                        Element address = element.addElement("address");
                        address.setText("tel:+86"+value);
                        Element phoneNum = element.addElement(key);
                        phoneNum.setText("tel:+86"+value);
                    }
                }else if("senderAddress".equals(key)||"clientCorrelator".equals(key)){
                    Element e = element.addElement(key);
                    e.setText(value);
                }
                //二级标签所属下的特殊格式标签
                else if("smsBodyText".equals(key)||"bodyText".equals(key)
                        ||"mmsBodyText".equals(key)||"mmsBodyTextLarge".equals(key)){
                    StringBuilder stringBuilder2=new StringBuilder();
                    stringBuilder2.append("<![CDATA[");
                    stringBuilder2.append(value);
                    stringBuilder2.append("]]>");
                    Element body=outboundIMMessage.addElement(key);
                    body.setText(stringBuilder2.toString());
                }else if("reportRequest".equals(key)){
                    if(value.indexOf(",")!=-1){
                        String[] split = value.split(",");
                        for (int i = 0; i < split.length; i++) {
                            Element phoneNum = element.addElement(key);
                            phoneNum.setText(split[i]);
                        }
                    }else if(StringUtils.hasText(value)){
                        Element phoneNum = element.addElement(key);
                        phoneNum.setText(value);
                    }
                } else if(("conversationID".equals(key)||"contributionID".equals(key))
                        &&(!map.containsValue("conversationID")||!map.containsValue("contributionID"))){
                    Element conversationID = outboundIMMessage.addElement("conversationID");
                    conversationID.setText(value);
                    Element contributionID = outboundIMMessage.addElement("contributionID");
                    contributionID.setText(value);
                }//else if("shortMessageSupported".equals(key)||"storeSupported".equals(key)
//                        ||"multimediaMessageSupported".equals(key)){
//                    //参数键，使用奇偶数，区分true和false，也保证键唯一
//                    Element element1 = outboundIMMessage.addElement(key);
//                    int i = Integer.parseInt(value);
//                    if (i%2==0){
//                        element1.setText("false");
//                    }else {
//                        element1.setText("true");
//                    }
//                }

                //三级标签所属
                else if("capabilityId".equals(key)||"version".equals(key)){
                    Element element1 = serviceCapability.addElement(key);
                    element1.setText(value);
                }else {
                    //二级标签所属(较多)
                    Element addElement = outboundIMMessage.addElement(key);
                    addElement.setText(value);
                }
                xmlStr = getXmlStr(document, "UTF-8", false);
            }
        }catch (Exception e){
            //生成错误
            e.printStackTrace();
            //TODO 日志
            log.info("组包出错：缺少参数");
        }
        return xmlStr;
    }


    /**
     * 返回字符串，并打印xml文件
     * @param document xml创建对象
     * @param code 编码（UTF-8）
     * @param escapeText1 字符串是否转义
     * @param escapeText2 文件是否转义
     * @param path 文件地址
     * @return
     */
    public static String getXmlStr(Document document,String code,boolean escapeText1,boolean escapeText2,String path){
        String xmlStr=null;
        try {
            // 设置生成xml的格式
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码格式
            format.setEncoding("UTF-8");
            // 生成xml文件
            File file = new File("/Users/yoca-391/Documents/demo01/xmlfiles/test1.xml");
            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            writer.setEscapeText(false);
            writer.write(document);
            writer.close();
            // 生成xml字符串
            StringWriter sw = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(sw, format);
            // 设置是否转义，默认使用转义字符
            xmlWriter.setEscapeText(false);
            xmlWriter.write(document);
            xmlWriter.close();
            xmlStr=sw.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return xmlStr;
    }

    /**
     * 只返回字符串
     * @param document xml创建对象
     * @param code 编码（UTF-8）
     * @param escapeText1 是否转义
     * @return xml字符串
     */
    public static String getXmlStr(Document document,String code,boolean escapeText1){
        String xmlStr=null;
        try {
            // 设置生成xml的格式
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码格式
            format.setEncoding("UTF-8");
            // 生成xml字符串
            StringWriter sw = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(sw, format);
            // 设置是否转义，默认使用转义字符
            xmlWriter.setEscapeText(false);
            xmlWriter.write(document);
            xmlWriter.close();
            xmlStr=sw.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return xmlStr;
    }


    public static String createXml() {
        try {
            // 1、创建document对象
            Document document = DocumentHelper.createDocument();
            // 2、创建根节点root
            Element msg = document.addElement("msg:outboundMessageRequest","urn:oma:xml:rest:netapi:messaging:1");
            // 3、向msg节点添加name属性
            //msg.addAttribute("name", "test");
            // 4、生成子节点及子节点内容
            Element address = msg.addElement("address");
            address.setText("tel:+8619585550104");
            Element destinationAddress1 = msg.addElement("destinationAddress");
//            destinationAddress1.addAttribute("name","子节点");
            destinationAddress1.setText("tel:+8619585550104");
            Element senderAddress = msg.addElement("senderAddress");
            senderAddress.setText("chatbotUrl");

                Element outboundIMMessage = msg.addElement("outboundIMMessage");
                    Element conversationID = outboundIMMessage.addElement("conversationID");
                    String uuid32 = UUIDUtil.getUUID32();
                    conversationID.setText(uuid32);
                    Element contributionID = outboundIMMessage.addElement("contributionID");
                    contributionID.setText(uuid32);
                    Element serviceCapability = outboundIMMessage.addElement("serviceCapability");
                        Element capabilityId = serviceCapability.addElement("capabilityId");
                        capabilityId.setText("chatbotSA");
                        Element version = serviceCapability.addElement("version");
                        version.setText("+g.gsma.rcs.botversion=&quot;#=1&quot;");
                    Element contentType = outboundIMMessage.addElement("contentType");
                    contentType.setText("text/plain");
                    Element bodyText = outboundIMMessage.addElement("bodyText");
                    bodyText.setText("这是一条纯文本即时消息");
                    Element reportRequest1 = outboundIMMessage.addElement("reportRequest");
                    reportRequest1.setText("Delivered");
                    Element reportRequest2 = outboundIMMessage.addElement("reportRequest");
                    reportRequest2.setText("Failed");
                    Element reportRequest3 = outboundIMMessage.addElement("reportRequest");
                    reportRequest3.setText("Interworking");
                Element clientCorrelator = msg.addElement("clientCorrelator");
                clientCorrelator.setText("567895");


            // 5、设置生成xml的格式
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码格式
            format.setEncoding("UTF-8");
            // 6、生成xml文件
            File file = new File("/Users/yoca-391/Documents/demo01/xmlfiles/test1.xml");
            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            writer.setEscapeText(false);
            writer.write(document);
            writer.close();
            // 7、生成xml字符串
            StringWriter sw = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(sw, format);
            // 设置是否转义，默认使用转义字符
            xmlWriter.setEscapeText(false);
            xmlWriter.write(document);
            xmlWriter.close();
            return sw.toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("生成失败");
        }
        return "fail";
    }




}
