package com.newland.wechat.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "t_wechat_card_consume_detail")
public class CardConsumeDetail {
	private static final long serialVersionUID = 4433122933969595040L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String code;
	private String appId;
	private String trmNo;
	private String cardType;
	private String status;//0成功 1失败
	private String reason;
	private Date consumeTime;
	private String cardName;
	private Date createTime;
	private Date updateTime;
	private String cardId;
	@Override
	public String toString() {
		return "CardConsumeDetail [id=" + id + ", code=" + code + ", appId="
				+ appId + ", trmNo=" + trmNo + ", cardType=" + cardType
				+ ", status=" + status + ", reason=" + reason
				+ ", consumeTime=" + consumeTime + ", cardName=" + cardName
				+ ", createTime=" + createTime + ", updateTime=" + updateTime
				+ ", cardId=" + cardId + "]";
	}
	
	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getTrmNo() {
		return trmNo;
	}
	public void setTrmNo(String trmNo) {
		this.trmNo = trmNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Date getConsumeTime() {
		return consumeTime;
	}
	public void setConsumeTime(Date consumeTime) {
		this.consumeTime = consumeTime;
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
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	
	
}
