package com.newland.wechat.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "t_wechat_authorize")
public class WeixinAuthorize implements Serializable  {
	private static final long serialVersionUID = 4433122933969595040L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String appId;//授权方公众号appid
	private String status;//0取消授权，1授权
	private Date authorizeTime;//授权时间
	private Date createTime;//创建时间
	private Date updateTime;//更新时间
	private String authorizerAccessToken;//授权access Token
	private String authorizerRefreshToken;//刷新令牌
	private Date authorizerAccessTokenTime;//获取令牌时间
	
	//昵称
	private String nickName; 
	//授权方公众号类型，0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号
	private Integer serviceTypeInfo;
	//授权方认证类型，-1代表未认证，0代表微信认证，1代表新浪微博认证，2代表腾讯微博认证，3代表已资质认证通过但还未通过名称认证，4代表已资质认证通过、还未通过名称认证，但通过了新浪微博认证，5代表已资质认证通过、还未通过名称认证，但通过了腾讯微博认证
	private Integer verifyTypeInfo;
	//授权方公众号的原始ID
	private String userName;
	//授权方公众号所设置的微信号，可能为空
	private String alias;
	//公众号授权给开发者的权限集列表（请注意，当出现用户已经将消息与菜单权限集授权给了某个第三方，再授权给另一个第三方时，由于该权限集是互斥的，后一个第三方的授权将去除此权限集，开发者可以在返回的func_info信息中验证这一点，避免信息遗漏），1到13分别代表
	private String funcInfo;
	//二维码路径
	private String qrcodeUrl;
	
	private Integer openPay;
	private Integer openShake;
	private Integer openScan;
	private Integer openCard;
	private Integer openStore;
	private Integer idc;
	private String principalName;
	private String signature;
	
	private String pushStatus;
	private Date pushStatusTime;
	
	
	public Date getPushStatusTime() {
		return pushStatusTime;
	}
	public void setPushStatusTime(Date pushStatusTime) {
		this.pushStatusTime = pushStatusTime;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getQrcodeUrl() {
		return qrcodeUrl;
	}
	public void setQrcodeUrl(String qrcodeUrl) {
		this.qrcodeUrl = qrcodeUrl;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getAuthorizeTime() {
		return authorizeTime;
	}
	public void setAuthorizeTime(Date authorizeTime) {
		this.authorizeTime = authorizeTime;
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
	
	public String getAuthorizerAccessToken() {
		return authorizerAccessToken;
	}
	public void setAuthorizerAccessToken(String authorizerAccessToken) {
		this.authorizerAccessToken = authorizerAccessToken;
	}
	
	public String getAuthorizerRefreshToken() {
		return authorizerRefreshToken;
	}
	public void setAuthorizerRefreshToken(String authorizerRefreshToken) {
		this.authorizerRefreshToken = authorizerRefreshToken;
	}
	
	public Date getAuthorizerAccessTokenTime() {
		return authorizerAccessTokenTime;
	}
	public void setAuthorizerAccessTokenTime(Date authorizerAccessTokenTime) {
		this.authorizerAccessTokenTime = authorizerAccessTokenTime;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	
	public Integer getServiceTypeInfo() {
		return serviceTypeInfo;
	}
	public void setServiceTypeInfo(Integer serviceTypeInfo) {
		this.serviceTypeInfo = serviceTypeInfo;
	}
	public Integer getVerifyTypeInfo() {
		return verifyTypeInfo;
	}
	public void setVerifyTypeInfo(Integer verifyTypeInfo) {
		this.verifyTypeInfo = verifyTypeInfo;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getFuncInfo() {
		return funcInfo;
	}
	public void setFuncInfo(String funcInfo) {
		this.funcInfo = funcInfo;
	}
	
	public String getPushStatus() {
		return pushStatus;
	}
	public void setPushStatus(String pushStatus) {
		this.pushStatus = pushStatus;
	}
	@Override
	public String toString() {
		return "WeixinAuthorize [id=" + id + ", appId=" + appId + ", status="
				+ status + ", authorizeTime=" + authorizeTime + ", createTime="
				+ createTime + ", updateTime=" + updateTime
				+ ", authorizerAccessToken=" + authorizerAccessToken
				+ ", authorizerRefreshToken=" + authorizerRefreshToken
				+ ", authorizerAccessTokenTime=" + authorizerAccessTokenTime
				+ ", nickName=" + nickName + ", serviceTypeInfo="
				+ serviceTypeInfo + ", verifyTypeInfo=" + verifyTypeInfo
				+ ", userName=" + userName + ", alias=" + alias + ", funcInfo="
				+ funcInfo + ", qrcodeUrl=" + qrcodeUrl + ", openPay="
				+ openPay + ", openShake=" + openShake + ", openScan="
				+ openScan + ", openCard=" + openCard + ", openStore="
				+ openStore + ", idc=" + idc + ", principalName="
				+ principalName + ", signature=" + signature + ", pushStatus="
				+ pushStatus + ", pushStatusTime=" + pushStatusTime + "]";
	}
	public Integer getOpenPay() {
		return openPay;
	}
	public void setOpenPay(Integer openPay) {
		this.openPay = openPay;
	}
	public Integer getOpenShake() {
		return openShake;
	}
	public void setOpenShake(Integer openShake) {
		this.openShake = openShake;
	}
	public Integer getOpenScan() {
		return openScan;
	}
	public void setOpenScan(Integer openScan) {
		this.openScan = openScan;
	}
	public Integer getOpenCard() {
		return openCard;
	}
	public void setOpenCard(Integer openCard) {
		this.openCard = openCard;
	}
	public Integer getOpenStore() {
		return openStore;
	}
	public void setOpenStore(Integer openStore) {
		this.openStore = openStore;
	}
	public Integer getIdc() {
		return idc;
	}
	public void setIdc(Integer idc) {
		this.idc = idc;
	}
	public String getPrincipalName() {
		return principalName;
	}
	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}
	
	
	
}
