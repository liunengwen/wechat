package com.newland.wechat.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.newland.wechat.common.CustomRequestWrapper;
import com.newland.wechat.emun.ExceptionCodeEnum;
import com.newland.wechat.model.response.ResponseModel;
import com.newland.wechat.utils.SerialUtils;


/**
 * 报文校验拦截器
 * @author fangxu.ge
 *
 */
public class RequestHeaderInterceptor extends HandlerInterceptorAdapter {
	
	private static final Logger log = LoggerFactory.getLogger(RequestHeaderInterceptor.class);

	/**
	 * 预处理回调方法，只对POST类型做拦截
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		response.setContentType("application/json;charset=utf-8");
		//api序列号用于日志跟踪
		String apiSerialNo = SerialUtils.getSerialNo();
		request.setAttribute("apiSerialNo", apiSerialNo);
		log.info("=== preHandle apiSerialNo ：{} {}",apiSerialNo ,",start ===");
		String submitMehtod = request.getMethod();
		// POST
		if ("POST".equals(submitMehtod)) {
			String payload = new CustomRequestWrapper(request).getBody();
			log.info("=== preHandle apiSerialNo ：{} , servletPath : {} , inParam：{} ===",apiSerialNo,request.getServletPath(),payload);
			//参数非空判断
			if(StringUtils.isBlank(payload)){
				response.getWriter().write(JSONObject.toJSONString(new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),
	                    ExceptionCodeEnum.PARAM_NULL.getLabel())));
				log.info("=== preHandle apiSerialNo ：{} , {} ===",apiSerialNo,ExceptionCodeEnum.PARAM_NULL.getLabel());
				return false;
			}else{
				//参数放到request,在Controller中取
				request.setAttribute("inParam", payload);
			}
		}
		return true;
	}

	/**
	 * 后处理回调方法
	 */
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	/**
	 * 整个请求处理完毕回调方法
	 */
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		log.info("=== afterCompletion apiSerialNo ：{} {}", request.getAttribute("apiSerialNo"), ",end ===");
	}

}