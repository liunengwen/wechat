
package com.newland.wechat.utils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.newland.wechat.common.Constants;
import com.newland.wechat.entity.WeixinAuthorize;
import com.newland.wechat.entity.WeixinOpenAccount;
import com.newland.wechat.exception.WexinReqException;
import com.newland.wechat.model.ApiAuthorizerToken;
import com.newland.wechat.model.ApiAuthorizerTokenRet;
import com.newland.wechat.model.ApiComponentToken;
import com.newland.wechat.model.ApiGetAuthorizer;
import com.newland.wechat.model.ApiGetAuthorizerRet;
import com.newland.wechat.model.AuthorizerOption;
import com.newland.wechat.model.AuthorizerOptionRet;
import com.newland.wechat.model.AuthorizerSetOption;
import com.newland.wechat.model.AuthorizerSetOptionRet;
import com.newland.wechat.model.OrderInfo;
import com.newland.wechat.model.ReOpenAccessToken;
import com.newland.wechat.service.WeixinOpenAccountService;

/**
 * 微信--token信息
 * 
 * @author lizr
 * 
 */
@Component
public class JwThirdAPI {
	private  Logger log = LoggerFactory.getLogger(JwThirdAPI.class);
	//获取预授权码
	@Autowired
	private WeixinOpenAccountService weixinOpenAccountService;
    /**
	 * 1、获取第三方平台access_token
	 * @param appid
	 * @param appscret
	 * @return kY9Y9rfdcr8AEtYZ9gPaRUjIAuJBvXO5ZOnbv2PYFxox__uSUQcqOnaGYN1xc4N1rI7NDCaPm_0ysFYjRVnPwCJHE7v7uF_l1hI6qi6QBsA
	 * @throws WexinReqException
     * @throws ParseException 
	 */
	public String getAccessToken(ApiComponentToken apiComponentToken) throws WexinReqException, ParseException{
		String component_access_token = "";
		WeixinOpenAccount weixinOpenAccount = weixinOpenAccountService.quaryOpenAccount();
		if(weixinOpenAccount != null &&weixinOpenAccount.getAccessTokenTime()!= null){
			int time = DateUtils.daysBetween(weixinOpenAccount.getAccessTokenTime(),new Date());
			if(time < 119){
				//第三方平台component_access_token有效期（2小时）,两小时以内直接返回
				return weixinOpenAccount.getAccessToken();
			}
		}
		//没有获取到component_access_token或component_access_token过期，进行刷新
		String requestUrl = PropertyUtils.getApiComponentTokenUrl();
		JSONObject obj = JSONObject.fromObject(apiComponentToken);
		JSONObject result = WxstoreUtils.httpRequest(requestUrl, "POST", obj.toString());
		if (result.has("errcode")) {
			log.error("获取第三方平台access_token！errcode=" + result.getString("errcode") + ",errmsg = "  +result.getString("errmsg"));
			throw new WexinReqException("获取第三方平台access_token！errcode=" + result.getString("errcode") + ",errmsg = "+  result.getString("errmsg"));
		} else {
			component_access_token = result.getString("component_access_token");
			weixinOpenAccount.setAccessToken(component_access_token);
			weixinOpenAccount.setAccessTokenTime(new Date());
			weixinOpenAccountService.saveOrUpdate(weixinOpenAccount);
		}
		return component_access_token;
	}
	
	/**
	 * 2、获取预授权码
	 * @param appid
	 * @param appscret
	 * @return kY9Y9rfdcr8AEtYZ9gPaRUjIAuJBvXO5ZOnbv2PYFxox__uSUQcqOnaGYN1xc4N1rI7NDCaPm_0ysFYjRVnPwCJHE7v7uF_l1hI6qi6QBsA
	 * @throws WexinReqException
	 */
	public  String getPreAuthCode(String component_appid, String component_access_token) throws WexinReqException{
		String pre_auth_code = "";
		String requestUrl = PropertyUtils.getApiCreatePreauthcodeUrl(component_access_token);
		Map<String, Object> getPreAuthCodeParam = new HashMap<String, Object>();
		getPreAuthCodeParam.put("component_appid",Constants.COMPONENT_APPID);
		JSONObject obj = JSONObject.fromObject(getPreAuthCodeParam);
		JSONObject result = WxstoreUtils.httpRequest(requestUrl, "POST", obj.toString());
		if (result.containsKey("errcode")) {
			log.error("获取权限令牌信息！errcode=" + result.getString("errcode") + ",errmsg = " + result.getString("errmsg"));
			throw new WexinReqException("获取权限令牌信息！errcode=" + result.getString("errcode") + ",errmsg = " + result.getString("errmsg"));
		} else {
			pre_auth_code = result.getString("pre_auth_code");
		}
		return pre_auth_code;
	}
	
	/**
	 * 3、使用授权码换取公众号的授权信息
	 * @param appid
	 * @param appscret
	 * @return kY9Y9rfdcr8AEtYZ9gPaRUjIAuJBvXO5ZOnbv2PYFxox__uSUQcqOnaGYN1xc4N1rI7NDCaPm_0ysFYjRVnPwCJHE7v7uF_l1hI6qi6QBsA
	 * @throws WexinReqException
	 */
	public  WeixinAuthorize  getApiQueryAuthInfo(String component_appid,String authorization_code,String component_access_token) throws WexinReqException{
		String requestUrl = PropertyUtils.getApiQueryAuthUrl(component_access_token);
		log.info("-------------------使用授权码换取公众号的授权信息------requestUrl:{}",requestUrl);
		Map<String,String> mp = new HashMap<String,String>();
		mp.put("component_appid", component_appid);
		mp.put("authorization_code", authorization_code);
		JSONObject obj = JSONObject.fromObject(mp);
		JSONObject authorizationInfoJson = WxstoreUtils.httpRequest(requestUrl,"POST", obj.toString());
		 net.sf.json.JSONObject infoJson = authorizationInfoJson.getJSONObject("authorization_info");  
		WeixinAuthorize weixinAuthorize = new WeixinAuthorize();
		if (authorizationInfoJson.has("errcode")) {
			log.error("获取第三方平台access_token！errcode=" + infoJson.getString("errcode") + ",errmsg = " + infoJson.getString("errmsg"));
			throw new WexinReqException("获取第三方平台access_token！errcode=" + infoJson.getString("errcode") + ",errmsg = "  +infoJson.getString("errmsg"));
		}else{
			log.info("------获取第三方平台infojson------" , infoJson.toString());
		if(infoJson.has("authorizer_access_token")&&infoJson.has("authorizer_refresh_token")){
			log.info("------获取第三方平台infojson有api权限------" , infoJson.toString());
			 String authorizer_access_token = infoJson.getString("authorizer_access_token");//授权方令牌（在授权的公众号具备API权限时，才有此返回值）
			 String authorizer_refresh_token = infoJson.getString("authorizer_refresh_token");//刷新令牌（在授权的公众号具备API权限时，才有此返回值）
			 weixinAuthorize.setAuthorizerAccessToken(authorizer_access_token);
	         weixinAuthorize.setAuthorizerRefreshToken(authorizer_refresh_token);
		}
         String authorizer_appid = infoJson.getString("authorizer_appid");//授权方appid
         weixinAuthorize.setAppId(authorizer_appid);
         weixinAuthorize.setStatus("1");//授权成功
         weixinAuthorize.setAuthorizeTime(new Date());
         weixinAuthorize.setAuthorizerAccessTokenTime(new Date());
		}
		return weixinAuthorize;
	}
	
	
	/**
	 * 4、获取（刷新）授权公众号的令牌
	 * @param apiAuthorizerToken
	 * @param component_access_token
	 */
	public ApiAuthorizerTokenRet apiAuthorizerToken(ApiAuthorizerToken apiAuthorizerToken,String component_access_token) throws WexinReqException{
		String requestUrl = PropertyUtils.getApiAuthorizerTokenUrl(component_access_token);
		JSONObject param = JSONObject.fromObject(apiAuthorizerToken);
		JSONObject result = WxstoreUtils.httpRequest(requestUrl,"POST", param.toString());
		ApiAuthorizerTokenRet apiAuthorizerTokenRet = (ApiAuthorizerTokenRet)JSONObject.toBean(result, ApiAuthorizerTokenRet.class);
		return apiAuthorizerTokenRet;
	}
	/**
	 * 5、获取授权方的账户信息
	 */
	public  ApiGetAuthorizerRet apiGetAuthorizerInfo(ApiGetAuthorizer apiGetAuthorizer,String component_access_token) throws WexinReqException{
		String requestUrl = PropertyUtils.getApiGetAuthorizerInfoUrl(component_access_token);
		JSONObject param = JSONObject.fromObject(apiGetAuthorizer);
		JSONObject result = WxstoreUtils.httpRequest(requestUrl,"POST", param.toString());
		ApiGetAuthorizerRet apiGetAuthorizerRet = (ApiGetAuthorizerRet)JSONObject.toBean(result, ApiGetAuthorizerRet.class);
		return apiGetAuthorizerRet;
	}
	
	/**
	 * 6、获取授权方的选项设置信息
	 */
	public  AuthorizerOptionRet apiGetAuthorizerOption(AuthorizerOption authorizerOption,String component_access_token) throws WexinReqException{
		String requestUrl = PropertyUtils.getApiGetAuthorizerOptionUrl(component_access_token);
		JSONObject param = JSONObject.fromObject(authorizerOption);
		JSONObject result = WxstoreUtils.httpRequest(requestUrl,"POST", param.toString());
		AuthorizerOptionRet authorizerOptionRet = (AuthorizerOptionRet)JSONObject.toBean(result, AuthorizerOptionRet.class);
		return authorizerOptionRet;
	}
	/**
	 * 7、设置授权方的选项信息
	 */
	public  AuthorizerSetOptionRet apiSetAuthorizerOption(AuthorizerSetOption authorizerSetOption,String component_access_token) throws WexinReqException{
		String requestUrl = PropertyUtils.getApiSetAuthorizerOptionUrl(component_access_token);
		JSONObject param = JSONObject.fromObject(authorizerSetOption);
		JSONObject result = WxstoreUtils.httpRequest(requestUrl,"POST", param.toString());
		AuthorizerSetOptionRet authorizerSetOptionRet = (AuthorizerSetOptionRet)JSONObject.toBean(result, AuthorizerSetOptionRet.class);
		return authorizerSetOptionRet;
	}
	/**
	 * 获取第三方平台access_token
	 * @param appid
	 * @param appscret
	 * @return kY9Y9rfdcr8AEtYZ9gPaRUjIAuJBvXO5ZOnbv2PYFxox__uSUQcqOnaGYN1xc4N1rI7NDCaPm_0ysFYjRVnPwCJHE7v7uF_l1hI6qi6QBsA
	 * @throws WexinReqException
	 */
	public  ReOpenAccessToken getAccessTokenByCode(String appid,String code,String grant_type,String component_appid,String component_access_token) throws WexinReqException{
		String requestUrl = PropertyUtils.getAccessTokenBycodeUrl( appid, code, grant_type, component_appid, component_access_token);
		JSONObject result = WxstoreUtils.httpRequest(requestUrl,"GET", null);
		ReOpenAccessToken reOpenAccessToken = (ReOpenAccessToken)JSONObject.toBean(result, OrderInfo.class);
		if (result.has("errcode")) {
			log.error("获取第三方平台access_token！errcode=" + result.getString("errcode")  +",errmsg = " + result.getString("errmsg"));
			throw new WexinReqException("获取第三方平台access_token！errcode=" + result.getString("errcode") + ",errmsg = " + result.getString("errmsg"));
		}
		return reOpenAccessToken;
	}
	
	
	 /**
     * 发送客服消息
     * @param obj
     * @param ACCESS_TOKEN
     * @return
     */
    public  String sendMessage(Map<String,Object> obj,String ACCESS_TOKEN){
    	JSONObject json = JSONObject.fromObject(obj);
    	System.out.println("--------发送客服消息---------json-----"+json.toString());
    	// 调用接口获取access_token
    	String url = PropertyUtils.getSendMessageUrl(ACCESS_TOKEN);
    	JSONObject jsonObject = WxstoreUtils.httpRequest(url, "POST", json.toString());
    	return jsonObject.toString();
    }
}