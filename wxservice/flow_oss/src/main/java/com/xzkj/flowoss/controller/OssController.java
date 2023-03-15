package com.xzkj.flowoss.controller;


import com.xzkj.flowoss.service.OssService;
import com.xzkj.flowoss.utils.MsgBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**阿里云Oss存储技术"
 * */
//@CrossOrigin
@RestController
@RequestMapping("/oss/flow")
public class OssController {
    @Autowired
    private OssService ossService;
    //上传头像，并返回头像访问地址
    @PostMapping("avatar")
        public String uploadOssFile(MultipartFile file){
            String url=ossService.uploadFileAvatar(file);
        return url;
    }
}
