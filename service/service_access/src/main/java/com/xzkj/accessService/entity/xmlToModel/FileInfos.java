package com.xzkj.accessService.entity.xmlToModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;

public class FileInfos implements Serializable {
    private static final long serialVersionUID = 12L;
    @JsonIgnore
    private String status;

    @JsonIgnore
    private String tid;

    //file-info标签type属性
    @JacksonXmlProperty(localName = "type",isAttribute = true)
    private String type;

    //file-name值
    @JacksonXmlProperty(localName = "file-size")
    private String fileName;

    //file-size值
    @JacksonXmlProperty(localName = "file-name")
    private String fileSize;

    //content-type值
    @JacksonXmlProperty(localName = "content-type")
    private String contentType;

    @JacksonXmlProperty(localName = "data")
    private Data data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public FileInfos() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
