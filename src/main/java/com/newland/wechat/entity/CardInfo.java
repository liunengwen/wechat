package com.newland.wechat.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "t_wechat_card_info")
public class CardInfo {
	private static final long serialVersionUID = 4433122933969595040L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String cardId;
	private Long quantity;
	private Long totalQuantity;
	private String title;
	private String cardType;
	private Date createTime;
	private Date updateTime;
	private String appId;
	private String examineStatus ;      
	private Date  examineTime   ;
	private String examineFaileResult ;
	private String pushExamineStatus;
	private Date pushExamineStatusTime;
	
	private String pushGetCardStatus;
	private Date pushGetCardStatusTime;
	private String code;
	@Override
	public String toString() {
		return "CardInfo [id=" + id + ", cardId=" + cardId + ", quantity="
				+ quantity + ", totalQuantity=" + totalQuantity + ", title="
				+ title + ", cardType=" + cardType + ", createTime="
				+ createTime + ", updateTime=" + updateTime + ", appId="
				+ appId + ", examineStatus=" + examineStatus + ", examineTime="
				+ examineTime + ", examineFaileResult=" + examineFaileResult
				+ ", pushExamineStatus=" + pushExamineStatus
				+ ", pushExamineStatusTime=" + pushExamineStatusTime
				+ ", pushGetCardStatus=" + pushGetCardStatus
				+ ", pushGetCardStatusTime=" + pushGetCardStatusTime
				+ ", code=" + code + "]";
	}
	


	public String getPushExamineStatus() {
		return pushExamineStatus;
	}



	public void setPushExamineStatus(String pushExamineStatus) {
		this.pushExamineStatus = pushExamineStatus;
	}



	public Date getPushExamineStatusTime() {
		return pushExamineStatusTime;
	}



	public void setPushExamineStatusTime(Date pushExamineStatusTime) {
		this.pushExamineStatusTime = pushExamineStatusTime;
	}



	public String getPushGetCardStatus() {
		return pushGetCardStatus;
	}



	public void setPushGetCardStatus(String pushGetCardStatus) {
		this.pushGetCardStatus = pushGetCardStatus;
	}



	public Date getPushGetCardStatusTime() {
		return pushGetCardStatusTime;
	}



	public void setPushGetCardStatusTime(Date pushGetCardStatusTime) {
		this.pushGetCardStatusTime = pushGetCardStatusTime;
	}



	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	public Long getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(Long totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
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
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getExamineStatus() {
		return examineStatus;
	}
	public void setExamineStatus(String examineStatus) {
		this.examineStatus = examineStatus;
	}
	public Date getExamineTime() {
		return examineTime;
	}
	public void setExamineTime(Date examineTime) {
		this.examineTime = examineTime;
	}
	public String getExamineFaileResult() {
		return examineFaileResult;
	}
	public void setExamineFaileResult(String examineFaileResult) {
		this.examineFaileResult = examineFaileResult;
	}



	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}
	
}
