package com.newland.wechat.service;

import java.util.List;

import com.newland.wechat.entity.WeixinAuthorize;
import com.newland.wechat.model.response.ResponseModel;

public interface WeixinAuthorizeService {
	/**
	 * 根据appid更新授权状态
	 * @param weixinAuthorize
	 */
	void updateStatusByAppid(WeixinAuthorize weixinAuthorize);
	/**
	 * 查询授权表
	 * @param weixinAuthorize
	 * @return
	 */
	WeixinAuthorize quaryByEntity(WeixinAuthorize weixinAuthorize);
	/**
	 * 更新或保存授权表
	 * @param weixinAuthorize
	 */
	void saveOrUpdateAuthorize(WeixinAuthorize weixinAuthorize);
	/**
	 * 查询即将失效的token
	 * @return
	 */
	List<WeixinAuthorize> quaryAuthorizerAccessTokenInvalid();
	/**
	 * 
	 * @param weixinAuthorize
	 */
	void updateAuthorizeById(WeixinAuthorize weixinAuthorize);
	/**
	 * 
	 * @param apiSerialNo
	 * @param inParam
	 * @return
	 */
	ResponseModel quaryAuthorize(String apiSerialNo, String inParam);
	/**
	 * 根据appId获取授权码
	 * @param appId
	 * @return
	 */
	String getAccessTokenByAppIdAndMecId(String appId);
}
