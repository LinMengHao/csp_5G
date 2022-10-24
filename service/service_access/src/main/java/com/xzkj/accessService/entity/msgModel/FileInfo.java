package com.xzkj.accessService.entity.msgModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
//构建file xml使用
public class FileInfo implements Serializable {
    private static final long serialVersionUID = 19L;

    //file-info标签type属性
    private String type;
    //file-name值
    private String fileName;
    //file-size值
    private String fileSize;
    //content-type值
    private String contentType;
    //data标签，属性： url：xxx  until：xxx
    private String url;
    //data标签，属性： url：xxx  until：xxx
    private String until;
    //data标签，属性： url：xxx  until：xxx
    private Map<String,String> data=new HashMap<>();

    public FileInfo() {
        data.put("url","");
        data.put("until","");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUntil() {
        return until;
    }

    public void setUntil(String until) {
        this.until = until;
    }

    public String getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "type='" + type + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", contentType='" + contentType + '\'' +
                ", url='" + url + '\'' +
                ", until='" + until + '\'' +
                ", data=" + data +
                '}';
    }
}
