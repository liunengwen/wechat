package com.newland.wechat.utils;

import com.newland.wechat.common.PropertyPlaceholder;
import com.newland.wechat.model.ApiComponentToken;
import com.newland.wechat.service.impl.WeixinOpenAccountServiceImpl;


/**
 * Description: 配置项工具类
 * @author fangxu.ge
 * @date 2017年8月9日 下午5:51:42
 */
public class PropertyUtils {
	//获取access_token
	public static String getAccessToken(ApiComponentToken apiComponentToken){
		String access_token_url = (String)PropertyPlaceholder.getProperty("wechat.access.token.url");
		String appId = apiComponentToken.getComponent_appid();
		String appsecret = apiComponentToken.getComponent_appsecret();
		String requestUrl = access_token_url.replace("APPID", appId).replace("APPSECRET", appsecret);
		return requestUrl;
	}
	
	//获取预授权码
	public static String getApiCreatePreauthcodeUrl(String component_access_token){
		return ((String)PropertyPlaceholder.getProperty("api_create_preauthcode_url")).replace("COMPONENT_ACCESS_TOKEN", component_access_token);
	}
	//获取第三方平台access_token
	public static String getApiComponentTokenUrl(){
		return (String)PropertyPlaceholder.getProperty("api_component_token_url");
	}
	//获取第三方平台access_token（根据code）
	public static String getAccessTokenBycodeUrl(String appid,String code,String grant_type,String component_appid,String component_access_token){
		return ((String)PropertyPlaceholder.getProperty("get_access_token_bycode_url")).replace("COMPONENT_APPID", component_appid).replace("COMPONENT_ACCESS_TOKEN", component_access_token).replace("authorization_code", grant_type).replace("CODE", code).replace("APPID", appid);
	}
	//使用授权码换取公众号的授权信息
	public static String getApiQueryAuthUrl(String component_access_token){
		return ((String)PropertyPlaceholder.getProperty("api_query_auth_url")).replace("xxxx", component_access_token);
	}
	//客服接口地址
	public static String getSendMessageUrl(String ACCESS_TOKEN){
		return ((String)PropertyPlaceholder.getProperty("send_message_url")).replace("ACCESS_TOKEN",ACCESS_TOKEN);
	}
	//获取（刷新）授权公众号的令牌
	public static String getApiAuthorizerTokenUrl(String component_access_token){
		return ((String)PropertyPlaceholder.getProperty("api_authorizer_token_url")).replace("COMPONENT_ACCESS_TOKEN", component_access_token);
	}
	//获取授权方的账户信息
	public static String getApiGetAuthorizerInfoUrl(String component_access_token){
		return ((String)PropertyPlaceholder.getProperty("api_get_authorizer_info_url")).replace("COMPONENT_ACCESS_TOKEN", component_access_token);
	}
	//获取授权方的选项设置信息
	public static String getApiGetAuthorizerOptionUrl(String component_access_token){
		return ((String)PropertyPlaceholder.getProperty("api_get_authorizer_option_url")).replace("COMPONENT_ACCESS_TOKEN", component_access_token);
	}
	//设置授权方的选项信息
	public static String getApiSetAuthorizerOptionUrl(String component_access_token){
		return ((String)PropertyPlaceholder.getProperty("api_set_authorizer_option_url")).replace("COMPONENT_ACCESS_TOKEN", component_access_token);
	}
	//登陆授权后回调地址
	public static String getComponentLoginPageUrl(String component_appid,String pre_auth_code){
		return ((String)PropertyPlaceholder.getProperty("component_login_page_url")).replace("COMPONENT_APPID", component_appid).replace("Pre_Auth_Code", pre_auth_code);
	}
	//上传媒体
	public static String getUploadMediaUrl(String access_token){
		return ((String)PropertyPlaceholder.getProperty("upload_media_url")).replace("ACCESS_TOKEN", access_token);
	}
	//下载媒体
	public static String getDownloadMediaUrl(String access_token, String media_id){
		return ((String)PropertyPlaceholder.getProperty("download_media_url")).replace("ACCESS_TOKEN", access_token).replace("MEDIA_ID", media_id);
	}
	//查询卡券
	public static String getCardInfoUrl(String access_token){
		return ((String)PropertyPlaceholder.getProperty("get_card_info_url")).replace("ACCESS_TOKEN", access_token);
	}
	//修改卡券
	public static String getUpdateCardInfoUrl(String access_token){
		return ((String)PropertyPlaceholder.getProperty("update_card_info_url")).replace("ACCESS_TOKEN", access_token);
	}
	//修改库存
	public static String getModifystockUrl(String access_token){
		return ((String)PropertyPlaceholder.getProperty("get_modifystock_url")).replace("ACCESS_TOKEN", access_token);
	}
	//删除卡券
	public static String getDelCardUrl(String access_token){
		return ((String)PropertyPlaceholder.getProperty("get_del_card_url")).replace("ACCESS_TOKEN", access_token);
	}
	//创建卡券
	public static String getCreateCardUrl(String access_token){
		return ((String)PropertyPlaceholder.getProperty("get_create_card_url")).replace("ACCESS_TOKEN", access_token);
	}
	//查询code码状态
	public static String getCodeStatusUrl(String access_token){
		return ((String)PropertyPlaceholder.getProperty("get_code_status_url")).replace("ACCESS_TOKEN", access_token);
	}
	//核销卡券
	public static String getDeleteCodeUrl(String access_token){
		return ((String)PropertyPlaceholder.getProperty("delete_code_url")).replace("ACCESS_TOKEN", access_token);
	}
	/*public static String quaryStoreInfoUrl(){
		return ((String)PropertyPlaceholder.getProperty("wechat.queryStoreInfoUrl"));
	}*/
	/**
	 * 卡券列表
	 * @param access_token
	 * @return
	 */
	/*public static String getCardListUrl(String access_token){
		return ((String)PropertyPlaceholder.getProperty("get_card_batch_list")).replace("ASSESS_TOKEN", access_token);
	}*/
	/**
	 * code解密
	 * @param access_token
	 * @return
	 */
	public static String getDecryptUrl(String access_token){
		return ((String)PropertyPlaceholder.getProperty("get_decrypt_url")).replace("ACCESS_TOKEN", access_token);
	}
	public static String getNotifyGetCardUrl(){
		return ((String)PropertyPlaceholder.getProperty("get_notify_get_card_url"));
	}
	public static String getNotifyAuthorizeUrl(){
		return ((String)PropertyPlaceholder.getProperty("get_notify_authorize_url"));
	}
	public static String getNotifyCardIsPassUrl(){
		return ((String)PropertyPlaceholder.getProperty("get_notify_crad_is_pass_url"));
	}
	public static String getRedirectUrl(){
		return ((String)PropertyPlaceholder.getProperty("redirect_url"));
	}
	/*public static String queryStoreInfoUrl(){
		return ((String)PropertyPlaceholder.getProperty("queryStoreInfoUrl"));
	}
	*/
	
}
