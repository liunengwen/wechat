package com.newland.wechat.controller;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.newland.wechat.common.Constants;
import com.newland.wechat.entity.WeixinOpenAccount;
import com.newland.wechat.exception.AesException;
import com.newland.wechat.exception.WexinReqException;
import com.newland.wechat.model.ApiComponentToken;
import com.newland.wechat.model.response.ResponseModel;
import com.newland.wechat.service.WeixinOpenAccountService;
import com.newland.wechat.service.third.NotifyService;
import com.newland.wechat.utils.JwThirdAPI;
import com.newland.wechat.utils.WXBizMsgCrypt;
import com.newland.wechat.utils.WeChatApiUtil;
@RestController
@RequestMapping("test")
public class TestController {
	@Autowired
	private WeixinOpenAccountService weixinOpenAccountService;
	@Autowired
	private JwThirdAPI jwThirdAPI;
	@Autowired
	private WeChatApiUtil weChatApiUtil;
	@Autowired
	private NotifyService notifyService;
	  @RequestMapping("/testAccessToken")
	   	public @ResponseBody void testAccessToken() throws ParseException{
	   		try {
	   			//String s = JwThirdAPI.getPreAuthCode("wx5412820bba6f6bd6","unisk");
	   			
	   			ApiComponentToken apiComponentToken = new ApiComponentToken();
	   			//apiComponentToken.setComponent_appid("wx5412820bba6f6bd6");
	   			apiComponentToken.setComponent_appid("wxdc347549bbfb2a24");
	   			apiComponentToken.setComponent_appsecret("30a87d94c34f5db74bd5d5cb04ef1c8f");
	   			apiComponentToken.setComponent_verify_ticket("");
	   			String s = jwThirdAPI.getAccessToken(apiComponentToken);
	   			System.out.println(s);
	   		} catch (WexinReqException e) {
	   			e.printStackTrace();
	   		}
	   	}
	    @RequestMapping("testEncryptMsg")
	    public @ResponseBody void testEncryptMsg(){
	    	Long createTime = Calendar.getInstance().getTimeInMillis() / 1000;  
	        String replyMsg = "LOCATIONfrom_callback";  
	          
	        String returnvaleue = "";  
	        try {  
	            WXBizMsgCrypt pc = new WXBizMsgCrypt(Constants.COMPONENT_TOKEN, Constants.COMPONENT_ENCODINGAESKEY, Constants.COMPONENT_APPID);  
	            returnvaleue = pc.EncryptMsg(replyMsg, createTime.toString(), "easemob");  
	            System.out.println(returnvaleue);  
	        } catch (AesException e) {  
	            e.printStackTrace();  
	        }  
	    }
	    @RequestMapping("testSave")
	    public @ResponseBody void testSave(){
	    	processAuthorizationEvent("12313");
	    }
	    void processAuthorizationEvent(String xml){  
	        Document doc;  
	        try {  
	            doc = DocumentHelper.parseText(xml);  
	            Element rootElt = doc.getRootElement();  
	            String ticket = rootElt.elementText("ComponentVerifyTicket");  
	            if(StringUtils.isNotEmpty(ticket)){  
	                //log.info("8、推送component_verify_ticket协议-----------ticket = "+ticket);  
	                WeixinOpenAccount  entity =new WeixinOpenAccount();  
	               // entity = entity==null?new WeixinOpenAccount():entity;  
	                entity.setTicket(ticket);  
	                entity.setAppId(Constants.COMPONENT_APPID);  
	                entity.setGetTicketTime(new Date());  
	                weixinOpenAccountService.saveOrUpdate(entity);  
	            }  
	        } catch (DocumentException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	
	    @RequestMapping("testNotifyGetCard")
	    public ResponseModel  testNotifyGetCard(String cardId){
	    	return notifyService.notifyGetCard(cardId);
	    }
	
	    @RequestMapping("testNotifyAuthorize")
	    public  ResponseModel testNotifyAuthorize(String appId,String status,String signature){
	    	return notifyService.notifyAuthorize(appId, status, signature);
	    }
	
	    @RequestMapping("testNotifyCardExam")
	    public  ResponseModel testNotifyCardExam(String cardId,String status){
	    	return notifyService.notifyCardIsPass(cardId, status);
	    }

	
	  }





