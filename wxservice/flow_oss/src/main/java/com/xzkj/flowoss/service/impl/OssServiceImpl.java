package com.xzkj.flowoss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import com.xzkj.flowoss.service.OssService;
import com.xzkj.flowoss.utils.ConstantPropertiesUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {
    @Override
    public String uploadFileAvatar(MultipartFile file) {
        //工具类获取阿里云oss相关信息
        String endpoint= ConstantPropertiesUtils.END_POINT;
        String keyId=ConstantPropertiesUtils.ACCESS_KEY_ID;
        String keySecret=ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName=ConstantPropertiesUtils.BUCKET_NAME;
        InputStream inputStream=null;
        try{
            OSS OssClick=new OSSClientBuilder().build(endpoint,keyId,keySecret);
            //获取文件上传流
            inputStream=file.getInputStream();
            //获取文件名
            String fileName=file.getOriginalFilename();
            //使文件名唯一，可加工filename
            String s= UUID.randomUUID().toString().replaceAll("-","");
            fileName=s+fileName;
            //把文件按日期分类
            String datePath=new DateTime().toString("yyyy/MM/dd");
            fileName=datePath+"/"+fileName;
            //上传
            //参数一：bucket名
            //参数二：文件名
            //参数三：文件流
            OssClick.putObject(bucketName,fileName,inputStream);
            //关闭流
            OssClick.shutdown();
            //上传路径结束后，把文件访问url返回
            String url="https://"+bucketName+"."+endpoint+"/"+fileName;
            return url;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
