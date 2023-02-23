package com.xzkj.platform.model.domain;

import com.xzkj.platform.common.annotation.Excel;
import com.xzkj.platform.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 模板素材对象 e_model_material
 * 
 * @author ruoyi
 * @date 2022-11-30
 */
public class ModelMaterial extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 模板id */
    @Excel(name = "模板id")
    private String modelId;

    /** 媒体类型 1-文本 2-图片 3-视频 4-音频 */
    @Excel(name = "媒体类型 1-文本 2-图片 3-视频 4-音频")
    private Integer mediaType;

    /** 媒体帧索引 */
    @Excel(name = "媒体帧索引")
    private Integer frameIndex;

    /** 媒体帧内容排序(0,1,2) */
    @Excel(name = "媒体帧内容排序(0,1,2)")
    private Integer frameSort;

    /** 文件扩展名txt,jpg… */
    @Excel(name = "文件扩展名txt,jpg…")
    private String ext;

    /** 素材内容(文本直接显示，其他显示素材地址) */
    @Excel(name = "素材内容(文本直接显示，其他显示素材地址)")
    private String content;

    /** 文件大小(文本显示文字字数，文件显示字节数KB) */
    @Excel(name = "文件相对路径")
    private String filePath;

    /** 文件大小(文本显示文字字数，文件显示字节数KB) */
    @Excel(name = "文件大小(文本显示文字字数，文件显示字节数KB)")
    private Integer fileSize;

    /** 操作员id */
    @Excel(name = "操作员id")
    private Long userId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setModelId(String modelId) 
    {
        this.modelId = modelId;
    }

    public String getModelId() 
    {
        return modelId;
    }
    public void setMediaType(Integer mediaType)
    {
        this.mediaType = mediaType;
    }

    public Integer getMediaType()
    {
        return mediaType;
    }
    public void setFrameIndex(Integer frameIndex)
    {
        this.frameIndex = frameIndex;
    }

    public Integer getFrameIndex()
    {
        return frameIndex;
    }
    public void setFrameSort(Integer frameSort)
    {
        this.frameSort = frameSort;
    }

    public Integer getFrameSort()
    {
        return frameSort;
    }
    public void setExt(String ext) 
    {
        this.ext = ext;
    }

    public String getExt() 
    {
        return ext;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileSize(Integer fileSize)
    {
        this.fileSize = fileSize;
    }

    public Integer getFileSize()
    {
        return fileSize;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("modelId", getModelId())
            .append("mediaType", getMediaType())
            .append("frameIndex", getFrameIndex())
            .append("frameSort", getFrameSort())
            .append("ext", getExt())
            .append("content", getContent())
            .append("fileSize", getFileSize())
            .append("userId", getUserId())
            .append("updateTime", getUpdateTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
