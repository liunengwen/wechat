package com.newland.wechat.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "t_wechat_ticket")
public class WeixinOpenAccount implements Serializable  {
	private static final long serialVersionUID = 4433122933969595040L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String appId;//第三方平台appid
	private String ticket;//微信下发票据
	private Date getTicketTime;//获得票据时间
	private Date createTime;//创建时间
	private Date updateTime;//更新时间
	private String  accessToken;//令牌
	private Date accessTokenTime;//获取令牌时间（2小时失效）
	
	public Date getAccessTokenTime() {
		return accessTokenTime;
	}
	public void setAccessTokenTime(Date accessTokenTime) {
		this.accessTokenTime = accessTokenTime;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public Date getGetTicketTime() {
		return getTicketTime;
	}
	public void setGetTicketTime(Date getTicketTime) {
		this.getTicketTime = getTicketTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	@Override
	public String toString() {
		return "WeixinOpenAccount [id=" + id + ", appId=" + appId + ", ticket="
				+ ticket + ", getTicketTime=" + getTicketTime + ", createTime="
				+ createTime + ", updateTime=" + updateTime + ", accessToken="
				+ accessToken + ", accessTokenTime=" + accessTokenTime + "]";
	}
	
	
}
