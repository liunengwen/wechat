package com.newland.wechat.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.newland.wechat.common.Constants;
import com.newland.wechat.exception.AesException;

public class DecryptFromWeXin {
	
	public static Element decryptMessage(HttpServletRequest request) throws IOException, AesException, DocumentException {
		String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        String msgSignature = request.getParameter("msg_signature");
        String xml = XMLParse.getXmlFromRequest(request);
        
        WXBizMsgCrypt wxBizMsgCrypt = new WXBizMsgCrypt(Constants.COMPONENT_TOKEN, 
        		Constants.COMPONENT_ENCODINGAESKEY, Constants.COMPONENT_APPID);
        
        xml = wxBizMsgCrypt.DecryptMsg(msgSignature, timestamp, nonce, xml);
        return DocumentHelper.parseText(xml).getRootElement();
	}

}
