package com.newland.wechat.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.newland.wechat.base.BaseService;
import com.newland.wechat.common.Constants;
import com.newland.wechat.emun.ExceptionCodeEnum;
import com.newland.wechat.entity.WeixinAuthorize;
import com.newland.wechat.mapper.WeixinAuthorizeMapper;
import com.newland.wechat.model.response.ResponseModel;
import com.newland.wechat.service.WeixinAuthorizeService;
import com.newland.wechat.service.WeixinOpenAccountService;
import com.newland.wechat.utils.AppBeanUtils;
import com.newland.wechat.utils.JwThirdAPI;
@Service("weixinAuthorizeService")
public class WeixinAuthorizeServiceImpl extends BaseService<WeixinAuthorize> implements WeixinAuthorizeService{
	private Logger log = LoggerFactory.getLogger(WeixinAuthorizeServiceImpl.class);
	@Autowired
	private WeixinAuthorizeMapper weixinAuthorizeMapper;
	@Autowired
	private JwThirdAPI jwThirdAPI;
	@Autowired
	private WeixinOpenAccountService weixinOpenAccountService;
	@Override
	public void updateStatusByAppid(WeixinAuthorize weixinAuthorize) {
		this.updateByIdSelective(weixinAuthorize, WeixinAuthorize.class, weixinAuthorize.getId());
	}

	@Override
	public WeixinAuthorize quaryByEntity(WeixinAuthorize weixinAuthorize) {
		
		return this.queryOne(weixinAuthorize);
	}

	@Override
	public void saveOrUpdateAuthorize(WeixinAuthorize weixinAuthorize) {
		log.info("======saveOrUpdateAuthorize保存或更新授权表     weixinAuthorize：{}====== ",weixinAuthorize.toString());
		WeixinAuthorize wAuthorize = new WeixinAuthorize();
		wAuthorize.setAppId(weixinAuthorize.getAppId());
		WeixinAuthorize wAuthorize2 = this.queryOne(wAuthorize);
		if(wAuthorize2!= null){
			weixinAuthorize.setUpdateTime(new Date());
			this.updateByIdSelective(weixinAuthorize, WeixinAuthorize.class, wAuthorize2.getId());
		}else{
			weixinAuthorize.setCreateTime(new Date());
			this.save(weixinAuthorize);
		}
		
	}

	@Override
	public List<WeixinAuthorize> quaryAuthorizerAccessTokenInvalid() {
		
		return weixinAuthorizeMapper.quaryAuthorizerAccessTokenInvalid();
	}

	@Override
	public void updateAuthorizeById(WeixinAuthorize weixinAuthorize) {
		
		 this.updateByIdSelective(weixinAuthorize, WeixinAuthorize.class, weixinAuthorize.getId());
	}

	@Override
	public ResponseModel quaryAuthorize(String apiSerialNo, String inParam) {
		ResponseModel resp = new ResponseModel();
		//参数转换为JSON
		JSONObject params = JSONObject.parseObject(inParam);
		//请求参数非空校验
		String errorMsg = AppBeanUtils.validateJsonValue(params,"appId");
		if(StringUtils.isNotBlank(errorMsg)){
			log.info("======查询appid是否授权请求参数为空 errorMsg:{}======",errorMsg);
			return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),errorMsg);
		}
		String appId = params.getString("appId");
		Map<String, Object> data = new HashMap<String, Object>();
		WeixinAuthorize weixinAuthorize = new WeixinAuthorize();
		weixinAuthorize.setAppId(appId);
		WeixinAuthorize wAuthorize = this.queryOne(weixinAuthorize);
		if(wAuthorize==null){
			data.put("status", Constants.UNKNOWAUTHORIZE);
		}else{
			data.put("status",wAuthorize.getStatus());
			data.put("name", wAuthorize.getNickName());
			data.put("authorizeTime", wAuthorize.getAuthorizeTime());
		}
		resp = new ResponseModel(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG,data);
		return resp;
	}

	@Override
	public String getAccessTokenByAppIdAndMecId(String appId) {
		log.info("======getAccessTokenByAppIdAndMecId 根据appId查询授权令牌  appId：{}======",appId);
		String accessToken = "";
		WeixinAuthorize weixinAuthorize = new WeixinAuthorize();
		weixinAuthorize.setAppId(appId);
		weixinAuthorize = this.queryOne(weixinAuthorize);
		if(weixinAuthorize != null){
			accessToken = weixinAuthorize.getAuthorizerAccessToken();
		}
		log.info("======getAccessTokenByAppIdAndMecId 根据appId查询授权令牌  accessToken：{}======",accessToken);
		return accessToken;
	}

	

}
