package com.newland.wechat.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newland.wechat.base.BaseService;
import com.newland.wechat.common.Constants;
import com.newland.wechat.entity.CardInfo;
import com.newland.wechat.entity.WeixinAuthorize;
import com.newland.wechat.entity.WeixinOpenAccount;
import com.newland.wechat.exception.AesException;
import com.newland.wechat.exception.WexinReqException;
import com.newland.wechat.mapper.WeixinOpenAccountMapper;
import com.newland.wechat.model.ApiComponentToken;
import com.newland.wechat.model.response.ResponseModel;
import com.newland.wechat.service.CardInfoService;
import com.newland.wechat.service.WeixinAuthorizeService;
import com.newland.wechat.service.WeixinOpenAccountService;
import com.newland.wechat.service.third.NotifyService;
import com.newland.wechat.utils.JwThirdAPI;
import com.newland.wechat.utils.WXBizMsgCrypt;
import com.newland.wechat.utils.XMLToMap;

@Service("weixinOpenAccountService")
public class WeixinOpenAccountServiceImpl extends BaseService<WeixinOpenAccount> implements WeixinOpenAccountService{
	private Logger log = LoggerFactory.getLogger(WeixinOpenAccountServiceImpl.class);
	@Autowired
	private WeixinAuthorizeService weixinAuthorizeService;
	
	@Autowired
	private JwThirdAPI jwThirdAPI;
	
	@Autowired
	private NotifyService notifyService;
	@Autowired
	private CardInfoService cardInfoService;
	
	@Autowired
	private WeixinOpenAccountMapper weixinOpenAccountMapper;
	public void saveOrUpdate(WeixinOpenAccount entity) {
		WeixinOpenAccount weixinOpenAccount = new WeixinOpenAccount();
		weixinOpenAccount.setAppId(entity.getAppId());
		//根据appid查询，如果存在则更新，不存在保存
		WeixinOpenAccount weixinOpenAccount2 = queryOne(weixinOpenAccount);
		if(weixinOpenAccount2 == null){
			entity.setCreateTime(new Date());
			this.save(entity);
		}else{
			entity.setUpdateTime(new Date());
			this.updateByIdSelective(entity, WeixinOpenAccount.class, weixinOpenAccount2.getId());
		}
		
	}

	/** 
     * 处理授权事件的推送 
     *  
     * @param request 
     * @throws IOException 
     * @throws AesException 
     * @throws DocumentException 
     */  
    public void processAuthorizeEvent(HttpServletRequest request) throws IOException, DocumentException, AesException {  
    	 log.info("======processAuthorizeEvent开始处理授权事件的推送=======request:{}======",request.toString());  
    	String nonce = request.getParameter("nonce");  //随机数
        String timestamp = request.getParameter("timestamp"); //时间戳 
        String signature = request.getParameter("signature");  //微信加密签名
        String msgSignature = request.getParameter("msg_signature");
        log.info("======processAuthorizeEvent处理授权事件的推送  获取参数 nonce：{} ,timestamp :{},signature :{} ,msgSignature:{} =======",nonce,timestamp,signature,msgSignature);  
        if (StringUtils.isBlank(msgSignature)){  
            return;// 微信推送给第三方开放平台的消息一定是加过密的，无消息加密无法解密消息  
        }else{
        log.info("======processAuthorizeEvent开始查看微信推送信息是否加密=======");  
        boolean isValid = checkSignature(Constants.COMPONENT_TOKEN, signature, timestamp, nonce);  
        if (isValid) { 
        	log.info("======processAuthorizeEvent微信推送信息已加密======="); 
            StringBuilder sb = new StringBuilder();  
            BufferedReader in = request.getReader();  
            String line;  
            while ((line = in.readLine()) != null) {  
                sb.append(line);  
            }  
            String xml = sb.toString();  
            log.info("======processAuthorizeEvent第三方平台全网发布======原始 Xml:{}=======",xml);  
            String encodingAesKey = Constants.COMPONENT_ENCODINGAESKEY;// 第三方平台组件加密密钥  
            String appId = getAuthorizerAppidFromXml(xml);// 此时加密的xml数据中ToUserName是非加密的，解析xml获取即可  
            log.info("======processAuthorizeEvent第三方平台全网发布=============appid:{}======",appId);  
            WXBizMsgCrypt pc = new WXBizMsgCrypt(Constants.COMPONENT_TOKEN, encodingAesKey, Constants.COMPONENT_APPID); 
            log.info("======processAuthorizeEvent第三方平台全网发布=============pc:{}======",pc.toString());  
            xml = pc.DecryptMsg(msgSignature, timestamp, nonce, xml);  
            log.info("======processAuthorizeEvent第三方平台全网发布=======解密后 Xml:{}======",xml);
            Map<String, String> xmlMap = XMLToMap.getXML(xml);
            if(StringUtils.isNotBlank(xmlMap.get("InfoType")) && "unauthorized".equals(xmlMap.get("InfoType"))){
            	 log.info("======processAuthorizeEvent第三方平台全网发布=======微信公众号取消授权======appid：{}======",xmlMap.get("AuthorizerAppid"));
                String appid = xmlMap.get("AuthorizerAppid");
                WeixinAuthorize account = new WeixinAuthorize();
                account.setAppId(appid);
                account = weixinAuthorizeService.quaryByEntity(account);
                if(account != null){
                    //公众号取消授权，设置相应的状态标志
                    account.setStatus("0");//取消授权
                    account.setUpdateTime(new Date());
                    ResponseModel resp = notifyService.notifyAuthorize(appid, Constants.REVOKE,account.getNickName());
                    if(resp.getCode().equals(Constants.SUCCESS_CODE)){
                    	//推送授权结果成功
                    	 log.info("======推送取消授权结果成功======appId:{}====== ",appid);
                    	account.setPushStatus("Y");
               	 }else{
               		 log.info("======推送取消授权结果失败======appId:{}======= ",appid);
               		account.setPushStatus("N");
               	 }
                    account.setPushStatusTime(new Date());
                    weixinAuthorizeService.updateStatusByAppid(account);
                    log.info("======wx_account Cancel authorization set status = 0  appid:{}======",appid);
                  
                }
            }
            processAuthorizationEvent(xml);  
        } 
       }
    }  
      
    /** 
     * 保存Ticket 
     * @param xml 
     */  
    public void processAuthorizationEvent(String xml){ 
    	log.info("======processAuthorizationEvent 开始解析并保存ticket========xml:{}======",xml.toString());  
        Document doc;  
        try {  
            doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();
            String ticket = rootElt.elementText("ComponentVerifyTicket");
            if(StringUtils.isNotEmpty(ticket)){
                log.info("======processAuthorizationEvent 推送component_verify_ticket协议===========ticket:{}====== ",ticket);
                WeixinOpenAccount entity = new WeixinOpenAccount();
                entity.setTicket(ticket);
                entity.setAppId(Constants.COMPONENT_APPID);
                entity.setGetTicketTime(new Date());
                try{
                	saveOrUpdate(entity);  
                }catch(Exception e){
                	 log.error("======processAuthorizationEvent 保存ticket异常===========e :{}======= ",e.toString());
                }
            }  
        } catch (DocumentException e) {  
            log.error("======processAuthorizationEvent 推送component_verify_ticket协议异常===========e :{}======= ",e.toString());  
        }  
    }  
      
    /** 
     * 获取授权账号信息 
     * @param appid 
     * @return 
     */  
    public WeixinOpenAccount getWeixinOpenAccount(String appId){  
        WeixinOpenAccount  entity = new WeixinOpenAccount();
        entity.setAppId(appId);
        return this.queryOne(entity);  
    }  
      
    /** 
     * 获取授权的Appid 
     * @param xml 
     * @return 
     */  
    public String getAuthorizerAppidFromXml(String xml) {
    	 log.info("======全网发布接入检测==开始获取授权的appid======"); 
        Document doc;  
        try {  
            doc = DocumentHelper.parseText(xml);  
            Element rootElt = doc.getRootElement();  
            String toUserName = rootElt.elementText("ToUserName");  
            log.info("======全网发布接入检测==获取授权的appid结束======",toUserName); 
            return toUserName;  
        } catch (DocumentException e) {  
            e.printStackTrace();  
            log.info("======全网发布接入检测==获取授权的appid异常======",e.toString()); 
            return null;  
        }  
       
    }  
     
    public void replyEventMessage(HttpServletRequest request, HttpServletResponse response, String event, String toUserName, String fromUserName,Element rootElt) throws DocumentException, IOException {  
    	
        CardInfo card = new CardInfo();
    	if(Constants.USER_GET_CARD.equals(event)){
        	//用户领取卡券
        	log.info("======全网发布接入检测======用户领取卡券======cardId:{}=======",rootElt.elementText("CardId")); 
        	String card_id = rootElt.elementText("CardId");
        	String IsGiveByFriend = rootElt.elementText("IsGiveByFriend");
        	if("0".equals(IsGiveByFriend)){
        		//是否为转赠领取，1代表是，0代表否。
        		card.setCode(rootElt.elementText("UserCardCode"));
    			card.setCardId(card_id);
    			card.setPushGetCardStatusTime(new Date());
        		ResponseModel resp = notifyService.notifyGetCard(card_id);
        		if(Constants.SUCCESS_CODE.equals(resp.getCode())){
        			card.setPushGetCardStatus("Y");
        		}else{
        			card.setPushGetCardStatus("N");
        		}
           	 log.info("======全网发布接入检测======step.4=======通知收单用户领券返回   resp:{},cardId:{}====== ",resp.toString(),card_id);
        	}
        }else if(Constants.USER_GIFTING_CARD.equals(event)){
        	//用户转赠卡券
        	log.info("=======全网发布接入检测======用户转赠卡券======"); 
        }else if(Constants.USER_DEL_CARD.equals(event)){
        	//用户删除卡券
        	log.info("======全网发布接入检测======用户删除卡券======"); 
        }else if(Constants.CARD_PASS_CHECK.equals(event)){
        	//卡券通过审核
        	log.info("=======全网发布接入检测======卡券通过审核======"); 
        	String cardId = rootElt.elementText("CardId");
        	ResponseModel resp = notifyService.notifyCardIsPass(cardId,"1");
        	log.info("=======全网发布接入检测======step.4=======通知收单卡券通过审核返回   resp:{} ,cardId:{}======",resp.toString(),cardId);
        	card.setCardId(cardId);
			card.setExamineStatus("Y");
			card.setExamineTime(new Date());
			card.setPushExamineStatusTime(new Date());
        	if(Constants.SUCCESS_CODE.equals(resp.getCode())){
        		card.setPushExamineStatus("Y");
    		}else{
    			card.setPushExamineStatus("N");
    		}
        }else if(Constants.CARD_NOT_PASS_CHECK.equals(event)){
        	//卡券未通过审核
        	log.info("======全网发布接入检测======卡券未通过审核======cardId：{}======",rootElt.elementText("CardId"));
        	String cardId = rootElt.elementText("CardId");
        	ResponseModel resp = notifyService.notifyCardIsPass(cardId,"2");
        	log.info("======全网发布接入检测======step.4=======通知收单卡券未通过审核返回   resp:{} cardId:{}====== ",resp.toString(),cardId);
        	card.setCardId(cardId);
        	card.setExamineStatus("N");
        	card.setExamineFaileResult(rootElt.elementText("RefuseReason"));
			card.setPushExamineStatusTime(new Date());
        	if(Constants.SUCCESS_CODE.equals(resp.getCode())){
        		card.setPushExamineStatus("Y");
    		}else{
    			card.setPushExamineStatus("N");
    		}
        }else if(Constants.SCAN.equals(event)) {
        	String parameter = rootElt.elementText("EventKey");
        	String returnContent = "&lt;a href=&quot;http://www.baidu.com&quot;&gt;点我绑定星管家帐号领积分&lt;/a&gt;";
            replyTextMessage(request,response,returnContent,toUserName,fromUserName);
        }
    	try{
    	cardInfoService.saveOrUpdateCardInfo(card);
    	}catch(Exception e){
    		 log.error("======通知收单卡券信息结果保存失败======  e:{},卡券id:{},事件:{}======",e.toString(),rootElt.elementText("CardId"),event);  
    	}
    }  
   
    public void processTextMessage(HttpServletRequest request, HttpServletResponse response,String content,String toUserName, String fromUserName) throws IOException, DocumentException, ParseException{  
        if("TESTCOMPONENT_MSG_TYPE_TEXT".equals(content)){  
            String returnContent = content+"_callback";  
            replyTextMessage(request,response,returnContent,toUserName,fromUserName);  
        }else if(StringUtils.startsWithIgnoreCase(content, "QUERY_AUTH_CODE")){  
            output(response, "");  
            //接下来客服API再回复一次消息  
            replyApiTextMessage(request,response,content.split(":")[1],fromUserName);  
        }  
    }  
   
    public void replyApiTextMessage(HttpServletRequest request, HttpServletResponse response, String auth_code, String fromUserName) throws DocumentException, IOException, ParseException {  
        String authorization_code = auth_code;  
        // 得到微信授权成功的消息后，应该立刻进行处理！！相关信息只会在首次授权的时候推送过来  
        log.info("======step.1====使用客服消息接口回复粉丝====逻辑开始=========================");  
        try {  
            ApiComponentToken apiComponentToken = new ApiComponentToken();  
            apiComponentToken.setComponent_appid(Constants.COMPONENT_APPID);  
            apiComponentToken.setComponent_appsecret(Constants.COMPONENT_APPSECRET);  
            WeixinOpenAccount  entity = getWeixinOpenAccount(Constants.COMPONENT_APPID);  
            apiComponentToken.setComponent_verify_ticket(entity.getTicket());  
            String accessToken = jwThirdAPI.getAccessToken(apiComponentToken);  
              
            log.info("======step.2====使用客服消息接口回复粉丝======= component_access_token = "+accessToken + "=========authorization_code = "+authorization_code);  
            WeixinAuthorize weixinAuthorize = jwThirdAPI.getApiQueryAuthInfo(Constants.COMPONENT_APPID, authorization_code, accessToken);  
            log.info("======step.3====使用客服消息接口回复粉丝============== 获取authorizationInfoJson:{}=====  ",weixinAuthorize.toString());  
            String authorizer_access_token = weixinAuthorize.getAuthorizerAccessToken();  
            
            Map<String,Object> obj = new HashMap<String,Object>();  
            Map<String,Object> msgMap = new HashMap<String,Object>();  
            String msg = auth_code + "_from_api";  
            msgMap.put("content", msg);  
              
            obj.put("touser", fromUserName);  
            obj.put("msgtype", "text");  
            obj.put("text", msgMap);  
            jwThirdAPI.sendMessage(obj, authorizer_access_token);  
        } catch (WexinReqException e) {  
            e.printStackTrace();  
        }  
          
    }     
      
    /** 
     * 验证是否过期 
     * @param accessTokenExpires 
     * @return 
     */  
    public boolean isExpired(long accessTokenExpires){  
        return false;  
    }  
      
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
    public void replyTextMessage(HttpServletRequest request, HttpServletResponse response, String content, String toUserName, String fromUserName) throws DocumentException, IOException {  
        Long createTime = Calendar.getInstance().getTimeInMillis() / 1000;  
        StringBuffer sb = new StringBuffer();  
        sb.append("<xml>");  
        sb.append("<ToUserName><![CDATA["+fromUserName+"]]></ToUserName>");  
        sb.append("<FromUserName><![CDATA["+toUserName+"]]></FromUserName>");  
        sb.append("<CreateTime>"+createTime+"</CreateTime>");  
        sb.append("<MsgType><![CDATA[text]]></MsgType>");  
//        sb.append("<Content><![CDATA["+content+"]]></Content>");
        sb.append("<Content>"+content+"</Content>");
        sb.append("</xml>");  
        String replyMsg = sb.toString();  
          
        String returnvaleue = "";  
        try {  
            WXBizMsgCrypt pc = new WXBizMsgCrypt(Constants.COMPONENT_TOKEN, Constants.COMPONENT_ENCODINGAESKEY, Constants.COMPONENT_APPID);  
            returnvaleue = pc.EncryptMsg(replyMsg, createTime.toString(), "easemob");  
            log.info("==================加密后的返回内容 returnvaleue：{}====== "+returnvaleue);  
        } catch (AesException e) {  
            e.printStackTrace();  
        }  
        output(response, returnvaleue);  
    }  
      
    /** 
     * 工具类：回复微信服务器"文本消息" 
     * @param response 
     * @param returnvaleue 
     */  
    public void output(HttpServletResponse response,String returnvaleue){  
        try {  
            PrintWriter pw = response.getWriter();
            pw.write(returnvaleue);
            log.info("****************returnvaleue***************="+returnvaleue);  
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }  
    }  
      
    /** 
     * 判断是否加密 
     * @param token 
     * @param signature 
     * @param timestamp 
     * @param nonce 
     * @return 
     */  
    public boolean checkSignature(String token,String signature,String timestamp,String nonce){  
        log.info("###token:"+token+";signature:"+signature+";timestamp:"+timestamp+"nonce:"+nonce);  
           boolean flag = false;  
           if(signature!=null && !signature.equals("") && timestamp!=null && !timestamp.equals("") && nonce!=null && !nonce.equals("")){  
              String sha1 = "";  
              String[] ss = new String[] { token, timestamp, nonce };   
              Arrays.sort(ss);    
              for (String s : ss) {    
               sha1 += s;    
              }    
       
              sha1 = AddSHA1.SHA1(sha1);    
       
              if (sha1.equals(signature)){  
               flag = true;  
              }  
           }  
           return flag;  
       }

	@Override
	public WeixinOpenAccount quaryOpenAccount() {
		WeixinOpenAccount record = new WeixinOpenAccount();
		record.setAppId(Constants.COMPONENT_APPID);
		return this.queryOne(record);
	}


}
class AddSHA1 {  
    public static String SHA1(String inStr) {  
        MessageDigest md = null;  
        String outStr = null;  
        try {  
            md = MessageDigest.getInstance("SHA-1");     //选择SHA-1，也可以选择MD5  
            byte[] digest = md.digest(inStr.getBytes());       //返回的是byet[]，要转化为String存储比较方便  
            outStr = bytetoString(digest);  
        }  
        catch (NoSuchAlgorithmException nsae) {  
            nsae.printStackTrace();  
        }  
        return outStr;  
    }  
      
      
    public static String bytetoString(byte[] digest) {  
        String str = "";  
        String tempStr = "";  
          
        for (int i = 0; i < digest.length; i++) {  
            tempStr = (Integer.toHexString(digest[i] & 0xff));  
            if (tempStr.length() == 1) {  
                str = str + "0" + tempStr;  
            }  
            else {  
                str = str + tempStr;  
            }  
        }  
        return str.toLowerCase();  
    }  
   
}  