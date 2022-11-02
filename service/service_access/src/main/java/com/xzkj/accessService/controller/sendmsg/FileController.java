package com.xzkj.accessService.controller.sendmsg;


import com.xzkj.accessService.conf.HttpsSkipRequestFactory;
import com.xzkj.accessService.entity.msgModel.MessageModel;
import com.xzkj.accessService.entity.msgModel.TextMsgModel;
import com.xzkj.accessService.utils.HttpHeaderUtil;
import com.xzkj.accessService.utils.TokenUtils;
import com.xzkj.utils.DateUtil;
import com.xzkj.utils.MD5Utils;
import com.xzkj.utils.R;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 文件上传下载删除
 */
@RestController
@RequestMapping("file")
public class FileController {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    HttpHeaderUtil httpHeaderUtil;
    @Autowired
    MessageModel messageModel;
    private static RestTemplate httpsTemplate=new RestTemplate(new HttpsSkipRequestFactory());


    @RequestMapping("upload")
    public R uploadFile(@RequestParam MultipartFile file){
        try {
            if (file.isEmpty()) {
                return R.error().message("文件为空");
            }
            String contentType = file.getContentType();
            System.out.println("文件格式："+contentType);
            long size = file.getSize();
            System.out.println("文件大小："+size+"字节（磁盘约"+size/1000+"KB)");
            // 获取文件名
            String fileName = file.getOriginalFilename();
            System.out.println("上传的文件名为：" + fileName);//写日志
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            System.out.println("文件的后缀名为：" + suffixName);//写日志
            // 设置文件存储路径         *************************************************
            String filePath = "./FILE/CHATBOT/";
            String path = filePath + fileName;
            File dest = new File(new File(path).getAbsolutePath());// dist为文件，有多级目录的文件
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {//因此这里使用.getParentFile()，目的就是取文件前面目录的路径
                dest.getParentFile().mkdirs();// 新建文件夹
            }
            file.transferTo(dest);// 文件写入
            return R.ok();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.error();
    }

    // 多个文件一起上传
    @RequestMapping("/batch")
    public R handleFileUpload(HttpServletRequest request) {   //注意参数
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
            String filePath = "./FILE/KING/";            // 文件路径
            File dest = new File(filePath);
            // 检测是否存在目录
            if (!dest.exists()) {
                dest.mkdirs();// 新建文件夹
            }
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    stream = new BufferedOutputStream(new FileOutputStream(
                            new File(filePath + file.getOriginalFilename())));//设置文件路径及名字
                    stream.write(bytes);// 写入
                    stream.close();
                } catch (Exception e) {
                    stream = null;
                    return R.error().message("第 " + i + " 个文件上传失败 ==> " + e.getMessage());
                }
            } else {
                return R.error().message("第 " + i + " 个文件上传失败因为文件为空");
            }
        }
        return R.ok();
    }

    @RequestMapping("/batchToYD")
    public R batchToYD(HttpServletRequest request){
        String cspid = request.getParameter("cspid");
        String csptoken = request.getParameter("csptoken");
        String chatbotURI = request.getParameter("chatbotURI");
        String serverRoot = request.getParameter("serverRoot");
        String tid = request.getParameter("tid");
        List<MultipartFile> thumbnails = ((MultipartHttpServletRequest) request).getFiles("Thumbnail");
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("File");

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        if(files.size()<=0&&files==null){
            return R.error().message("文件上传失败因为文件为空");
        }
        String s = getResource(files, map, "File");
        if(s.length()>1){
            return R.error().message(s);
        }
        //有缩略图
        if(thumbnails.size()>0){
            getResource(thumbnails,map,"Thumbnail");
        }
        map.add("tid",tid);
        //请求组包
        HttpHeaders httpHeaders = new HttpHeaders();
        String date = DateUtil.getGMTDate();
        String authorization = TokenUtils.getAuthorization(cspid, csptoken, date);
        httpHeaders.set("Authorization",authorization);
        httpHeaders.set("Date",date);
        httpHeaders.set("Terminal-type","Chatbot");
        httpHeaders.set("User-Agent","SP/"+chatbotURI);
        httpHeaders.set("X-3GPP-Intended-Identity",chatbotURI);
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, httpHeaders);
        ResponseEntity<String> response = httpsTemplate.postForEntity("https://" + serverRoot + "/Content", entity, String.class);
        System.out.println(response.getStatusCode());
        System.out.println(response.getHeaders().toString());
        System.out.println(response.toString());
        return R.ok();
    }
    //多文件组包
    private String getResource(List<MultipartFile> multipartFileList,LinkedMultiValueMap<String, Object> map,String fileType){
        StringBuilder s=new StringBuilder();
        for (int i = 0; i < multipartFileList.size(); ++i){
            MultipartFile file=multipartFileList.get(i);
            if(!file.isEmpty()){
                try {
                    ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                        @Override
                        public String getFilename() {
                            return file.getOriginalFilename();
                        }
                    };
                    map.add(fileType,resource);
                } catch (IOException e) {
                    s.append("第 " + i + " 个"+fileType+"文件上传失败因为文件为空 ");
                }
            }else {
                s.append("第 " + i + " 个"+fileType+"文件上传失败因为文件为空 ");
            }
        }
        return s.toString();
    }

    @RequestMapping("downloadFile")
    public R downloadFile(HttpServletRequest request){
        String cspid = request.getParameter("cspid");
        String cspToken = request.getParameter("csptoken");
        String filePath = request.getParameter("filePath");
        HttpHeaders headers=new HttpHeaders();
        headers.set("Terminal-type","Chatbot");
        headers.set("User-Agent","SP/"+filePath);
        String date = DateUtil.getGMTDate();
        headers.set("Date",date);
        headers.set("Authorization", TokenUtils.getAuthorization(cspid,cspToken,date));
        headers.set("X-3GPP-Intended-Identity",filePath);
        HttpEntity<String>entity=new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(filePath, HttpMethod.GET, entity, byte[].class);
        FileOutputStream out=null;
        try{
            byte[] body=response.getBody();
            //获取文件名
            HttpHeaders headers1 = response.getHeaders();
            List<String> list = headers1.get("Content-Disposition");
            String s2 = list.get(0);
            String[] split = s2.split(";");
            String filename=null;
            for (String value : split) {
                if (value.trim().startsWith("filename")) {
                    filename = value.substring(value.indexOf('=') + 1).trim();
                }
                if(StringUtils.hasText(filename)){
                    if(body!=null){
                        String file="./FILE/KING/"+filename;
                        File dest = new File(file);
                        // 检测是否存在目录
                        if (!dest.exists()) {
                            dest.mkdirs();// 新建文件夹
                        }
                        out = new FileOutputStream(dest);
                        out.write(body,0,body.length);
                        out.flush();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return R.ok();
    }

    @RequestMapping("deleteFile")
    public R deleteFile(@RequestBody TextMsgModel textMsgModel){
        HttpHeaders headers=new HttpHeaders();
        String date = DateUtil.getGMTDate();
        headers.set("Authorization",TokenUtils.getAuthorization(textMsgModel.getCspid(),textMsgModel.getCsptoken(),date));
        headers.set("Date",date);
        headers.set("Terminal-type","Chatbot");
        headers.set("User-Agent","SP/"+textMsgModel.getChatbotURI());
        headers.set("X-3GPP-Intended-Identity",textMsgModel.getChatbotURI());
        headers.set("tid",textMsgModel.getTid());
        headers.set("fileURL",textMsgModel.getFileURL());
        HttpEntity<String> entity=new HttpEntity<>(headers);
        ResponseEntity<String> response = httpsTemplate.exchange("https://" + textMsgModel.getServerRoot() + "/Content/delete", HttpMethod.DELETE, entity, String.class);
        System.out.println(response.getStatusCode());
        System.out.println(response.getHeaders().toString());
        System.out.println(response.toString());
        return R.ok();
    }
    @Test
    public void test(){
        long time = new Date().getTime();
        System.out.println("time:"+time);
        System.out.println("sign:"+ MD5Utils.MD5Encode("1" + "18756232770" + "0WoIOu2js$q89fzU" + time).toUpperCase());
    }
}
