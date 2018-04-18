package com.newland.wechat.service.third;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.newland.wechat.common.Constants;
import com.newland.wechat.model.response.HttpResult;
import com.newland.wechat.model.response.ResponseModel;
import com.newland.wechat.utils.PropertyUtils;


@Service("notifyService")
public class NotifyService{
	
	private Logger logger = LoggerFactory.getLogger(NotifyService.class);
	
	@Autowired
	private HttpClientService httpClientService;
	
	/**
	 * 通知卡券领取
	 * @param cardId
	 * @return
	 */
	public ResponseModel notifyGetCard(String cardId) {
		logger.info("=== notifyGetCard  cardId : {}  ===",cardId);
		ResponseModel resp = new ResponseModel();
		String notifyUrl = PropertyUtils.getNotifyGetCardUrl();
		JSONObject params = new JSONObject();
    	params.put("cardId", cardId);
    	
    	params.put("contentTyp", "application/json");
    	params.put("characterSet", "GBK");
    	params.put("version", "v1");
		HttpResult httpResult = null;
		try {
			httpResult = httpClientService.doPost(notifyUrl, params.toString());
			if(httpResult != null && httpResult.getCode() == HttpStatus.SC_OK 
					&& StringUtils.isNotBlank(httpResult.getData())){
				JSONObject resultJson = JSONObject.parseObject(httpResult.getData());
				String retCode = resultJson.getString("repCode");
				//retCode=000000表示成功
				if("000000".equals(retCode)){
					resp = new ResponseModel(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG);
				}else{
					logger.error("=== notifyGetCard  通知卡券领取失败   cardId: {} , returncode : {} ,repMsg : {} ===",cardId,httpResult.getCode(),resultJson.getString("repMsg"));
				}
			}else{
				logger.error("=== notifyGetCard    failed code : {} ===",httpResult.getCode());
			}
		} catch (Exception e) {
			logger.error("=== notifyGetCard   error : {} ===",e.toString());
			
		}
		return resp;
	}
	/**
	 * 通知授权状态
	 * @param appId
	 * @param status
	 * @return
	 */
	public ResponseModel notifyAuthorize(String appId,String status,String signature) {
		logger.info("=== notifyAuthorize   appId : {} ,status:{}  ===",appId,status);
		ResponseModel resp = new ResponseModel();
		String notifyAuthorizeUrl = PropertyUtils.getNotifyAuthorizeUrl();
		JSONObject inParam = new JSONObject();
		inParam.put("appid", appId);
		inParam.put("appidsts", status);
		inParam.put("appidname", signature);
		
		inParam.put("contentTyp", "application/json");
		inParam.put("characterSet", "UTF-8");
		inParam.put("version", "v1");
		
		HttpResult httpResult = null;
		try {
			httpResult = httpClientService.doPost(notifyAuthorizeUrl, inParam.toString());
			if(httpResult != null && httpResult.getCode() == HttpStatus.SC_OK 
					&& StringUtils.isNotBlank(httpResult.getData())){
				JSONObject resultJson = JSONObject.parseObject(httpResult.getData());
				String retCode = resultJson.getString("repCode");
				//retCode=000000表示成功
				if("000000".equals(retCode)){
					resp = new ResponseModel(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG);
				}else{
					logger.error("=== notifyAuthorize 通知授权状态失败    appId : {} , returncode : {} ,repMsg : {} ===",appId,httpResult.getCode(),resultJson.getString("repMsg"));
				}
			}else{
				logger.error("=== notifyAuthorize    failed code : {} ===",httpResult.getCode());
			}
		} catch (Exception e) {
			logger.error("=== notifyAuthorize    error : {} ===",e.toString());
		}
		return resp;
	}
	/**
	 * 通知审核状态
	 * @param cardId
	 * @param status
	 * @return
	 */
	public ResponseModel notifyCardIsPass(String cardId,String status) {
		logger.info("=== notifyCardIsPass  cardId : {} ,status:{}  ===",cardId,status);
		ResponseModel resp = new ResponseModel();
		String notifyCardIsPassUrl = PropertyUtils.getNotifyCardIsPassUrl();
		JSONObject inParam = new JSONObject();
		
		inParam.put("cardid", cardId);
		inParam.put("checksts", status);
		
		inParam.put("contentTyp", "application/json");
		inParam.put("characterSet", "GBK");
		inParam.put("version", "v1");
		HttpResult httpResult = null;
		try {
			httpResult = httpClientService.doPost(notifyCardIsPassUrl, inParam.toString());
			if(httpResult != null && httpResult.getCode() == HttpStatus.SC_OK 
					&& StringUtils.isNotBlank(httpResult.getData())){
				JSONObject resultJson = JSONObject.parseObject(httpResult.getData());
				String retCode = resultJson.getString("repCode");
				//retCode=000000表示成功
				if("000000".equals(retCode)){
					resp = new ResponseModel(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG);
				}else{
					logger.error("=== notifyAuthorize  通知审核状态失败   cardId : {} , returncode : {} ,repMsg : {} ===",cardId,httpResult.getCode(),resultJson.getString("repMsg"));
				}
			}else{
				logger.error("=== notifyAuthorize  failed code : {} ===",httpResult.getCode());
			}
		} catch (Exception e) {
			logger.error("=== notifyAuthorize   error : {} ===",e.toString());
		}
		return resp;
	}
	
}
