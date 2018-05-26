package com.newland.wechat.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newland.wechat.common.Constants;
import com.newland.wechat.entity.WeixinAuthorize;
import com.newland.wechat.entity.WeixinOpenAccount;
import com.newland.wechat.exception.WexinReqException;
import com.newland.wechat.model.ApiAuthorizerToken;
import com.newland.wechat.model.ApiAuthorizerTokenRet;
import com.newland.wechat.model.ApiComponentToken;
import com.newland.wechat.utils.JwThirdAPI;

@Service
public class BatchService {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private WeixinAuthorizeService weixinAuthorizeService;
	
	@Autowired
	private WeixinOpenAccountService weixinOpenAccountService;
	
	@Autowired
	private JwThirdAPI jwThirdAPI;
	
	public void batchUpdateAuthorizerAccessToken() throws WexinReqException, ParseException {
		
		// 查询即将过期的授权方令牌
		List<WeixinAuthorize> list = weixinAuthorizeService.quaryAuthorizerAccessTokenInvalid();
		
		
		for(WeixinAuthorize weixinAuthorize :list){
			ApiAuthorizerToken apiAuthorizerToken = new ApiAuthorizerToken();
			//获取授权方appid，第三方appid及刷新令牌
			apiAuthorizerToken.setComponent_appid(Constants.COMPONENT_APPID);
			apiAuthorizerToken.setAuthorizer_appid(weixinAuthorize.getAppId());
			apiAuthorizerToken.setAuthorizer_refresh_token(weixinAuthorize.getAuthorizerRefreshToken());
			
			//获取第三方平台授权令牌
			ApiComponentToken apiComponentToken = new ApiComponentToken();  
            apiComponentToken.setComponent_appid(Constants.COMPONENT_APPID);  
            apiComponentToken.setComponent_appsecret(Constants.COMPONENT_APPSECRET);
            
            WeixinOpenAccount entity = weixinOpenAccountService.getWeixinOpenAccount(Constants.COMPONENT_APPID);
            
            apiComponentToken.setComponent_verify_ticket(entity.getTicket());
            String component_access_token = jwThirdAPI.getAccessToken(apiComponentToken); 
            
            //获取刷新后的授权方令牌
            ApiAuthorizerTokenRet apiAuthorizerTokenRet = jwThirdAPI.apiAuthorizerToken(apiAuthorizerToken, component_access_token);
            weixinAuthorize.setAuthorizerAccessToken(apiAuthorizerTokenRet.getAuthorizer_access_token());
            weixinAuthorize.setAuthorizerAccessTokenTime(new Date());
            weixinAuthorize.setUpdateTime(new Date());
            weixinAuthorize.setAuthorizerRefreshToken(apiAuthorizerTokenRet.getAuthorizer_refresh_token());
            try{
            	weixinAuthorizeService.updateAuthorizeById(weixinAuthorize);
            	log.info("------批量刷新授权方令牌------ weixinAuthorize:{}",weixinAuthorize.toString());
            }catch(Exception e){
            	log.error("------批量刷新授权方令牌更新失败------ e:{}",e);
            }
           
		}
		
	}
}
