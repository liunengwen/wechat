package com.newland.wechat.service.impl;

import javax.json.Json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.newland.wechat.entity.WeixinAuthorize;
import com.newland.wechat.mapper.WeixinAuthorizeMapper;
import com.newland.wechat.model.response.HttpResult;
import com.newland.wechat.service.QrCodeService;
import com.newland.wechat.service.third.HttpClientService;
import com.newland.wechat.utils.PropertyUtils;

@Service
public class QrCodeServiceImpl implements QrCodeService {
	
	@Autowired
	private WeixinAuthorizeMapper weixinAuthorizeMapper;
	
	@Autowired
	private HttpClientService httpClientService;

	@Override
	public String createQrCode(String appid, String parameter) throws Exception {
		
		WeixinAuthorize weixinAuthorize = new WeixinAuthorize();
		weixinAuthorize.setAppId(appid);
		
		weixinAuthorize = weixinAuthorizeMapper.selectOne(weixinAuthorize);
		
		String url = PropertyUtils.getCreateQrCodeUrl(weixinAuthorize.getAuthorizerAccessToken());
		JSONObject json = new JSONObject();
		json.put("expire_seconds", 604800);
		json.put("action_name", "QR_STR_SCENE");
		JSONObject action_info = new JSONObject();
		JSONObject scene_str = new JSONObject();
		scene_str.put("scene_str", parameter);
		action_info.put("scene", scene_str);
		json.put("action_info", action_info);
		
		HttpResult result = httpClientService.doPost(url, json.toJSONString());
		
		return JSONObject.parseObject(result.getData()).getString("url");
	}

}
