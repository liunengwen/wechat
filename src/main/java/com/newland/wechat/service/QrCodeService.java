package com.newland.wechat.service;

public interface QrCodeService {
	
	/**
	 * 代授权公众号生成带参临时二维码(时间默认为604800秒)
	 * @param aPPID_B
	 * @param q
	 * @return
	 * @throws Exception 
	 */
	String createQrCode(String appid, String parameter) throws Exception;

}
