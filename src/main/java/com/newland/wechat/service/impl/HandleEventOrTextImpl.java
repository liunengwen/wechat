package com.newland.wechat.service.impl;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newland.wechat.common.EnumMsgType;
import com.newland.wechat.service.HandleEventOrText;
import com.newland.wechat.service.WeixinOpenAccountService;

@Service
public class HandleEventOrTextImpl implements HandleEventOrText {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private WeixinOpenAccountService weixinOpenAccountService;

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response, Element xml, String appId) 
			throws IOException, DocumentException, ParseException {
		
		String msgType = xml.elementText("MsgType");
		log.info("appId:[{}]", appId);
        log.info("hand {} from weixin", msgType);
        
        if(StringUtils.equals(msgType, EnumMsgType.text.toString())) {
        	processText(request, response, xml);
        }else if(StringUtils.equals(msgType, EnumMsgType.event.toString())) {
        	processEvent(request, response, xml);
        }else {
        	response.getWriter().write("success");
        }
	}
	
	private void processText(HttpServletRequest request, HttpServletResponse response, Element xml) 
			throws IOException, DocumentException, ParseException {
		
		String fromUserName = xml.elementText("FromUserName");
		String toUserName = xml.elementText("ToUserName");
		String content = xml.elementText("Content");
		
		weixinOpenAccountService.processTextMessage(request, response, content, toUserName, fromUserName);;
		
	}
	private void processEvent(HttpServletRequest request, HttpServletResponse response, Element xml) 
			throws DocumentException, IOException, ParseException {
		
		String fromUserName = xml.elementText("FromUserName");
        String toUserName = xml.elementText("ToUserName");
        String event = xml.elementText("Event");
        if(event.contains("card")) {
        	weixinOpenAccountService.replyEventMessage(request, response, event, toUserName, fromUserName, xml);
        }else {
        	
        	
        }
	}

	
	
	
	
	
	
	
}
