package com.newland.wechat.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;

import com.newland.wechat.exception.WexinReqException;
import com.newland.wechat.service.BatchService;
import com.newland.wechat.service.WeixinAuthorizeService;


public class BatchController {
	@Autowired
	private BatchService batchService;
	//定时刷新令牌
	public void getAuthorizerAccessToken() throws WexinReqException, ParseException{
		
		batchService.batchUpdateAuthorizerAccessToken();
		
		/*batchService.batchUpdateComponentAccessToken();*/
		
	}
}
