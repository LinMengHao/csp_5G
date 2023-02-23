package com.xzkj.platform.khd.domain;




import com.xzkj.platform.common.core.domain.BaseEntity;

import java.util.Date;

public class MmsModel  extends BaseEntity
{
	
	private int id;
	
	private String title;
	
	private String appId;
	
	private String modelId;
	
	private String status;
	
	private Date addTime;
	
	private String channelId;
	
	private String chanModelId;
	
	private String channelName;

	private String ext;

	private String productId;

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChanModelId() {
		return chanModelId;
	}

	public void setChanModelId(String chanModelId) {
		this.chanModelId = chanModelId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
}
