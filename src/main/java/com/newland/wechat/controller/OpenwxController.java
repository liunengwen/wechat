package com.newland.wechat.controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.Date;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.newland.wechat.common.Constants;
import com.newland.wechat.entity.WeixinAuthorize;
import com.newland.wechat.entity.WeixinOpenAccount;
import com.newland.wechat.exception.AesException;
import com.newland.wechat.exception.WexinReqException;
import com.newland.wechat.model.ApiComponentToken;
import com.newland.wechat.model.ApiGetAuthorizer;
import com.newland.wechat.model.ApiGetAuthorizerRet;
import com.newland.wechat.model.ApiGetAuthorizerRetAuthorizer;
import com.newland.wechat.model.ApiGetAuthorizerRetAuthortion;
import com.newland.wechat.model.BussinessInfo;
import com.newland.wechat.model.response.ResponseModel;
import com.newland.wechat.service.HandleEventOrText;
import com.newland.wechat.service.WeixinAuthorizeService;
import com.newland.wechat.service.WeixinOpenAccountService;
import com.newland.wechat.service.third.HttpClientService;
import com.newland.wechat.service.third.NotifyService;
import com.newland.wechat.utils.DecryptFromWeXin;
import com.newland.wechat.utils.JwThirdAPI;
import com.newland.wechat.utils.PropertyUtils;
import com.newland.wechat.utils.XMLParse;

  
/** 
 * 微信公众账号第三方平台全网发布源码（java） 
 * @author： jeewx开源社区 
 * @网址：www.jeewx.com 
 * @论坛：www.jeecg.org 
 * @date 20150801 
 */  
@Controller  
@RequestMapping("/openwx")  
public class OpenwxController {  
	private Logger log = LoggerFactory.getLogger(OpenwxController.class);
    /** 
     * 微信全网测试账号 
     */  
   
    @Autowired  
    private WeixinOpenAccountService weixinOpenAccountService;  
    @Autowired  
    private WeixinAuthorizeService weixinAuthorizeService;  
    @Autowired
    private JwThirdAPI jwThirdAPI;
    @Autowired
    private HttpClientService httpClientService;
    @Autowired
    private NotifyService notifyService;
    @Autowired
    private HandleEventOrText handleEventOrText;
    
     /** 
     * 授权事件接收 
     *  
     * @param request 
     * @param response 
     * @throws IOException 
     * @throws AesException 
     * @throws DocumentException 
     */  
    @RequestMapping(value = "/event/authorize")  
    public void acceptAuthorizeEvent(HttpServletRequest request, HttpServletResponse response) throws IOException, AesException, DocumentException {  
    	log.info("======微信第三方平台=========微信推送Ticket消息10分钟一次========" + request);  
    	weixinOpenAccountService.processAuthorizeEvent(request);  
//    	weixinOpenAccountService.output(response, "success"); // 输出响应的内容。
    	response.getWriter().write("success");
    }  
      
    /** 
     * 一键授权功能 
     * @param request 
     * @param response 
     * @throws IOException 
     * @throws AesException 
     * @throws DocumentException 
     * @throws WexinReqException 
     * @throws ParseException 
     */ 
    @RequestMapping(value = "/goAuthor")  
    public void goAuthor(HttpServletRequest request, HttpServletResponse response)  {  
    	String referer = request.getHeader("referer"); 
   	   log.info("======goAuthor  refer:{}======",referer);
        try{
        	String accessToken = jwThirdAPI.getAccessToken(getApiComponentToken());  
        	log.info("======goAuthor一键授权功能获取accessToken===========accessToken:{}====== ",accessToken);
            //预授权码  
            String preAuthCode = jwThirdAPI.getPreAuthCode(Constants.COMPONENT_APPID, accessToken); 
            log.info("======goAuthor一键授权功能获取预授权码preAuthCode===========preAuthCode:{}====== ",preAuthCode);
            String url = PropertyUtils.getComponentLoginPageUrl(Constants.COMPONENT_APPID, preAuthCode);
            log.info("======goAuthor一键授权功能获取登陆授权路径===========url:{}====== ",url);
            response.sendRedirect(url);
        }catch(Exception e){		
        	log.error("======goAuthor一键授权功能异常======e:{} ",e.toString()); 
        }
    }
    /**
     * 消息及事件推送
     * @param request
     * @param response
     * @throws IOException
     * @throws AesException
     * @throws DocumentException
     * @throws ParseException
     */
    @RequestMapping(value = "{appId}/callback")  
    public void acceptMessageAndEvent(HttpServletRequest request, HttpServletResponse response, @PathVariable("appId")String appId) throws IOException {  
        
        log.info("Receive WeChat events or push notifications");
        Element xml;
        try {
        	xml = DecryptFromWeXin.decryptMessage(request);
        	handleEventOrText.process(request, response, xml, appId);
		} catch (Exception e) {
			log.error("decrypt message from weixin failed:", e.getMessage());
			response.getWriter().write("success");// 回复
		}
    }
    /**
     * 一键授权后回调地址
     * @param request
     * @param response
     * @throws IOException
     * @throws AesException
     * @throws DocumentException
     * @throws ParseException
     */
    @RequestMapping(value = "/authorCallback")
    public void authorCallback(HttpServletRequest request, HttpServletResponse response) throws IOException, AesException, DocumentException, ParseException {
    	log.info("======全网发布接入检测消息进入授权后回调URI======"); 
    	String auth_code = request.getParameter("auth_code");//授权码
    	String expires_in = request.getParameter("expires_in");//过期时间
        String component_access_token = "";
         try{
        	 component_access_token = jwThirdAPI.getAccessToken(getApiComponentToken());
        	 log.info("======authorCallback获取到令牌，授权码===========component_access_token:{}，auth_code：{}======",component_access_token,auth_code);
        	 //使用授权码换取公众号的授权信息
        	 WeixinAuthorize weixinAuthorize = jwThirdAPI.getApiQueryAuthInfo(Constants.COMPONENT_APPID, auth_code, component_access_token);
        	 log.info("======authorCallback获取到公众号的授权信息===========weixinAuthorize:{}======",weixinAuthorize.toString());
        	 ApiGetAuthorizer apiGetAuthorizer = new ApiGetAuthorizer();
        	 apiGetAuthorizer.setAuthorizer_appid(weixinAuthorize.getAppId());//授权方appid
        	 apiGetAuthorizer.setComponent_appid(Constants.COMPONENT_APPID);//第三方appid
        	 //获取授权方的账户信息
        	 ApiGetAuthorizerRet apiGetAuthorizerRet = jwThirdAPI.apiGetAuthorizerInfo( apiGetAuthorizer, component_access_token);
        	 log.info("======authorCallback获取到授权方账户信息===========apiGetAuthorizerRet:{}======",apiGetAuthorizerRet.toString());  
        	 WeixinAuthorize weixinAuthorize2 = getWeixinAuthorize(weixinAuthorize,apiGetAuthorizerRet);
        	 log.info("======authorCallback获取到授权方账户信息及授权信息，最终保存信息===========weixinAuthorize2:{}======",weixinAuthorize2.toString());  
             
            	 ResponseModel resp = notifyService.notifyAuthorize(weixinAuthorize.getAppId(), Constants.AUTHORIZE,weixinAuthorize2.getNickName());
            	 if(resp.getCode().equals(Constants.SUCCESS_CODE)){
            		 weixinAuthorize2.setPushStatus("Y");
            		 
            	 }else{
            		 weixinAuthorize2.setPushStatus("N");
            	 }
            	 weixinAuthorize2.setPushStatusTime(new Date());
            	 try{
            	 weixinAuthorizeService.saveOrUpdateAuthorize(weixinAuthorize2);
            	 log.info("======authorCallback保存或更新授权表 成功===========weixinAuthorize2:{}======",weixinAuthorize2.toString());  
             }catch(Exception e){
            	 log.error("======authorCallback保存或更新授权表 异常===========e:{}======",e.toString());  
             }
         }catch(WexinReqException e){
         	log.error("======authorCallback获取component_access_token 异常===========e:{}======",e.toString());  
         }
         String url = PropertyUtils.getRedirectUrl();
         response.sendRedirect(url);
    }
    
    //获取ApiComponentToken
     public ApiComponentToken  getApiComponentToken(){
    	 ApiComponentToken apiComponentToken = new ApiComponentToken();  
         apiComponentToken.setComponent_appid(Constants.COMPONENT_APPID);  
         apiComponentToken.setComponent_appsecret(Constants.COMPONENT_APPSECRET);  
         WeixinOpenAccount  entity = weixinOpenAccountService.getWeixinOpenAccount(Constants.COMPONENT_APPID);  
         apiComponentToken.setComponent_verify_ticket(entity.getTicket());
         return apiComponentToken;
     }
     
     
    public WeixinAuthorize getWeixinAuthorize(WeixinAuthorize weixinAuthorize, ApiGetAuthorizerRet apiGetAuthorizerRet){
    	 ApiGetAuthorizerRetAuthorizer apiGetAuthorizerRetAuthorizer = apiGetAuthorizerRet.getAuthorizer_info();//授权方的账户信息
    	 ApiGetAuthorizerRetAuthortion apiGetAuthorizerRetAuthortion = apiGetAuthorizerRet.getAuthorization_info();//授权第三方相关信息
    	 weixinAuthorize.setNickName(apiGetAuthorizerRetAuthorizer.getNick_name());//昵称
    	// weixinAuthorize.setHeadImg(apiGetAuthorizerRetAuthorizer.getHead_img());//头像
    	 weixinAuthorize.setUserName(apiGetAuthorizerRetAuthorizer.getUser_name());//原始id
    	 weixinAuthorize.setAlias(apiGetAuthorizerRetAuthorizer.getAlias());//授权方公众号所设置的微信号
    	//授权方公众号类型，0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号
    	 weixinAuthorize.setServiceTypeInfo(apiGetAuthorizerRetAuthorizer.getService_type_info().getId());
    	//授权方认证类型，-1代表未认证，0代表微信认证，1代表新浪微博认证，2代表腾讯微博认证，3代表已资质认证通过但还未通过名称认证，4代表已资质认证通过、还未通过名称认证，但通过了新浪微博认证，5代表已资质认证通过、还未通过名称认证，但通过了腾讯微博认证
    	 weixinAuthorize.setVerifyTypeInfo(apiGetAuthorizerRetAuthorizer.getVerify_type_info().getId());
    	 weixinAuthorize.setQrcodeUrl(apiGetAuthorizerRetAuthorizer.getQrcode_url());//二维码地址
    	            
    	 BussinessInfo bussinessInfo = apiGetAuthorizerRetAuthorizer.getBusiness_info();
    	 weixinAuthorize.setOpenCard(bussinessInfo.getOpen_card());
    	 weixinAuthorize.setOpenPay(bussinessInfo.getOpen_pay());
    	 weixinAuthorize.setOpenScan(bussinessInfo.getOpen_scan());
    	 weixinAuthorize.setOpenShake(bussinessInfo.getOpen_shake());
    	 weixinAuthorize.setOpenStore(bussinessInfo.getOpen_store());
    	 
    	 weixinAuthorize.setPrincipalName(apiGetAuthorizerRetAuthorizer.getPrincipal_name());
    	 weixinAuthorize.setIdc(apiGetAuthorizerRetAuthorizer.getIdc());
    	 weixinAuthorize.setSignature(apiGetAuthorizerRetAuthorizer.getSignature());
    	 return weixinAuthorize;
    }
    
    
    @RequestMapping(value = "/author")
    public  String getAuthor(HttpServletRequest request){
    	 String referer = request.getHeader("referer");  
  	   log.info("======author  refer:{}======",referer);
  	 /*
    	  String Url = "http://wechat=thirdsystem=test.starpos.com.cn/wechat/openwx/goAuthor";
    	  JSONObject jsonObject = new JSONObject();
  		try {
  		sendPost(Url,jsonObject.toString());
  		}catch(Exception e){
  			e.printStackTrace();
  			log.error("=============================",e.toString());
  		}*/
  		return "index.jsp";
    }
    
    
    public static void sendPost(String url, String param) {
        PrintWriter out = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("referer","http://wechat-thirdsystem-test.starpos.com.cn/wechat/");
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
          
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            if(out!=null){
			    out.close();
			}
        }
    } 
  
    
}  
  
  
