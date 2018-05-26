package com.newland.wechat.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newland.wechat.base.BaseController;
import com.newland.wechat.common.Constants;
import com.newland.wechat.model.response.ResponseModel;
import com.newland.wechat.service.QrCodeService;

@RestController
public class QrCodeController extends BaseController{
	
	@Autowired
	private QrCodeService qrCodeService;
	
	private final String APPID_B = "wx9da07c3fb189d02e";
	
	private final String APPID_C = "wxdc347549bbfb2a24";
	
	@RequestMapping(value = "/create/qrcode/bc")
	public ResponseModel createQrCode(@Param(value = "q")String q) throws Exception {
		
		log.info("create QrCode begin, parameter from request is :[{}]", q);
		
		String QrCode_B = qrCodeService.createQrCode(APPID_B, q);
		
		String QrCode_C = qrCodeService.createQrCode(APPID_C, q);
		
		Map<String, String> qrCodes = new HashMap<>();
		qrCodes.put("b", QrCode_B);
		qrCodes.put("c", QrCode_C);
		
		return new ResponseModel(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG, qrCodes);
	}
}
