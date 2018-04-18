package com.newland.wechat.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "t_wechat_card_consume")
public class CardConsumeInfo {
	private static final long serialVersionUID = 4433122933969595040L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String cardId;
	private String cardType;
	private String cardName;
	private Date createTime;
	private Date updateTime;
	@Override
	public String toString() {
		return "CardConsumeInfo [id=" + id + ", cardId=" + cardId
				+ ", cardType=" + cardType + ", cardName=" + cardName
				+ ", createTime=" + createTime + ", updateTime=" + updateTime
				+ "]";
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
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
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
	
}
