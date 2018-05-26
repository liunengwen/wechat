package com.newland.wechat.common;


/**
 * 全局静态常量
 * @author fangxu.ge
 * @date 2017.6.3
 */
public final class Constants {

    // UTF8编码
    public static final String CHARSET_UTF8 = "UTF-8";

    // MD5
    public static final String SIGN_TYPE_MD5 = "MD5";
    
    //测试环境
    public static final  String COMPONENT_APPID = "wx5ab5d6f102803474";  
    public static final  String COMPONENT_APPSECRET = "664be25987338ea1832a6e312aeb4ae8";  
    public static final  String COMPONENT_ENCODINGAESKEY = "H0fCrx8qUvpkiqXKryHbm9yY9IxRJBsHfTYlHpL04ZT";  
    public static final  String COMPONENT_TOKEN = "developerStarpos";  
    
    //生产环境
    /*public static final   String COMPONENT_APPID = "wx5b736af140f4848d";  
    public static final  String COMPONENT_APPSECRET = "a519c0b1f0d889a6ff004aec7340e8ff";  
    public static final  String COMPONENT_ENCODINGAESKEY = "aUs7ca56gwOgVVdBmjq7GfafD8ePOhiOvTUIzcgi60Y";  
    public static final  String COMPONENT_TOKEN = "newland123456";*/
    //微信全网测试专用appId
    public static final   String APPID = "wx570bc396a51b8ff8";
    
    public static final  String USER_GET_CARD = "user_get_card";
    public static final  String USER_GIFTING_CARD = "user_gifting_card";
    public static final  String USER_DEL_CARD = "user_del_card";
    public static final  String CARD_PASS_CHECK = "card_pass_check";
    public static final  String CARD_NOT_PASS_CHECK = "card_not_pass_check";
    public static final  String SCAN = "SCAN";
    //上传媒体类型
    public static final  String video = "video";
    public static final  String image = "image";
    public static final  String voice = "voice";
    public static final  String thumb = "thumb";
    // 成功状态码
    public static final String SUCCESS_CODE = "SUCCESS";
    
    // 成功信息
    public static final String SUCCESS_MSG = "成功";
    
    public static final String REVOKE = "0";
    public static final String AUTHORIZE = "1";
    public static final String UNKNOWAUTHORIZE = "2";
}