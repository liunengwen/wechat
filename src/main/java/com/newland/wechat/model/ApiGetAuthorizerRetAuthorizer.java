package com.newland.wechat.model;
/**
 * 授权方的账户信息实体
 */
public class ApiGetAuthorizerRetAuthorizer {
	//昵称
	private String nick_name ;
	
	private BussinessInfo business_info; 
	//授权方公众号类型，0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号
	private ApiGetAuthorizerRetAuthorizerSType service_type_info;
	//授权方认证类型，-1代表未认证，0代表微信认证，1代表新浪微博认证，2代表腾讯微博认证，3代表已资质认证通过但还未通过名称认证，4代表已资质认证通过、还未通过名称认证，但通过了新浪微博认证，5代表已资质认证通过、还未通过名称认证，但通过了腾讯微博认证
	private ApiGetAuthorizerRetAuthorizerVType verify_type_info;
	//授权方公众号的原始ID
	private String user_name;
	//授权方公众号所设置的微信号，可能为空
	private String alias;
	private String qrcode_url;
	private int idc;
	private String principal_name;
	private String signature;
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public ApiGetAuthorizerRetAuthorizerSType getService_type_info() {
		return service_type_info;
	}
	public void setService_type_info(
			ApiGetAuthorizerRetAuthorizerSType service_type_info) {
		this.service_type_info = service_type_info;
	}
	public ApiGetAuthorizerRetAuthorizerVType getVerify_type_info() {
		return verify_type_info;
	}
	public void setVerify_type_info(
			ApiGetAuthorizerRetAuthorizerVType verify_type_info) {
		this.verify_type_info = verify_type_info;
	}
	
	public BussinessInfo getBusiness_info() {
		return business_info;
	}
	public void setBusiness_info(BussinessInfo business_info) {
		this.business_info = business_info;
	}
	public String getQrcode_url() {
		return qrcode_url;
	}
	public void setQrcode_url(String qrcode_url) {
		this.qrcode_url = qrcode_url;
	}
	public int getIdc() {
		return idc;
	}
	public void setIdc(int idc) {
		this.idc = idc;
	}
	public String getPrincipal_name() {
		return principal_name;
	}
	public void setPrincipal_name(String principal_name) {
		this.principal_name = principal_name;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	@Override
	public String toString() {
		return "ApiGetAuthorizerRetAuthorizer [nick_name=" + nick_name
				+ ", business_info=" + business_info + ", service_type_info="
				+ service_type_info + ", verify_type_info=" + verify_type_info
				+ ", user_name=" + user_name + ", alias=" + alias
				+ ", qrcode_url=" + qrcode_url + ", idc=" + idc
				+ ", principal_name=" + principal_name + ", signature="
				+ signature + "]";
	}
	
	
}