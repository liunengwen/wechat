package com.newland.wechat.mapper;

import java.util.List;

import com.github.abel533.mapper.Mapper;
import com.newland.wechat.entity.WeixinAuthorize;

public interface WeixinAuthorizeMapper extends Mapper<WeixinAuthorize>{
	
	List<WeixinAuthorize> quaryAuthorizerAccessTokenInvalid();
	
}
