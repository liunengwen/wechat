package com.newland.wechat.service;




import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.newland.wechat.entity.WeixinOpenAccount;
import com.newland.wechat.exception.AesException;

public interface WeixinOpenAccountService {
	/**
	 * 保存或更新WeixinOpenAccount
	 * @param entity
	 */
	void saveOrUpdate(WeixinOpenAccount  entity);
	
	
	/** 
     * 处理授权事件的推送 
     *  
     * @param request 
     * @throws IOException 
     * @throws AesException 
     * @throws DocumentException 
     */  
    public void processAuthorizeEvent(HttpServletRequest request) throws IOException, DocumentException, AesException ;
      
    /** 
     * 保存Ticket 
     * @param xml 
     */  
    void processAuthorizationEvent(String xml); 
      
    /** 
     * 获取授权账号信息 
     * @param appid 
     * @return 
     */  
    WeixinOpenAccount getWeixinOpenAccount(String appId);
      
    /** 
     * 获取授权的Appid 
     * @param xml 
     * @return 
     */  
    String getAuthorizerAppidFromXml(String xml) ;
     
      
    public void checkWeixinAllNetworkCheck(HttpServletRequest request, HttpServletResponse response,String xml) throws DocumentException, IOException, AesException,ParseException;  
      
     /**
      * 回复事件
      * @param request
      * @param response
      * @param event
      * @param toUserName
      * @param fromUserName
      * @throws DocumentException
      * @throws IOException
      */
    public void replyEventMessage(HttpServletRequest request, HttpServletResponse response, String event, String toUserName, String fromUserName,Element rootElt) throws DocumentException, IOException,ParseException ; 
   
    public void processTextMessage(HttpServletRequest request, HttpServletResponse response,String content,String toUserName, String fromUserName) throws IOException, DocumentException,ParseException; 
   /**
    * api回复文本信息
    * @param request
    * @param response
    * @param auth_code
    * @param fromUserName
    * @throws DocumentException
    * @throws IOException
    */
    public void replyApiTextMessage(HttpServletRequest request, HttpServletResponse response, String auth_code, String fromUserName) throws DocumentException, IOException,ParseException;     
      
    /** 
     * 验证是否过期 
     * @param accessTokenExpires 
     * @return 
     */  
    boolean isExpired(long accessTokenExpires);  
      
    /** 
     * 回复微信服务器"文本消息" 
     * @param request 
     * @param response 
     * @param content 
     * @param toUserName 
     * @param fromUserName 
     * @throws DocumentException 
     * @throws IOException 
     */  
    public void replyTextMessage(HttpServletRequest request, HttpServletResponse response, String content, String toUserName, String fromUserName) throws DocumentException, IOException ;  
      
    /** 
     * 工具类：回复微信服务器"文本消息" 
     * @param response 
     * @param returnvaleue 
     */  
    public void output(HttpServletResponse response,String returnvaleue);  
      
    /** 
     * 判断是否加密 
     * @param token 
     * @param signature 
     * @param timestamp 
     * @param nonce 
     * @return 
     */  
    public boolean checkSignature(String token,String signature,String timestamp,String nonce);
    
    public WeixinOpenAccount quaryOpenAccount();
    
    /**
     * 查询即将过期的ComponentAccessToken
     * @return
     */
   /* public List<WeixinOpenAccount> quaryComponentAccessTokenInvalid();*/
}
