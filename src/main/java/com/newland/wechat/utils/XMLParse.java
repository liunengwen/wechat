/**
 * 对公众平台发送给公众账号的消息加解密示例代码.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------
package com.newland.wechat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Attribute;
import org.dom4j.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.newland.wechat.exception.AesException;
/**
 * XMLParse class
 *
 * 提供提取消息格式中的密文及生成回复消息格式的接口.
 */
public class XMLParse {

	/**
	 * 提取出xml数据包中的加密消息
	 * @param xmltext 待提取的xml字符串
	 * @return 提取出的加密消息字符串
	 * @throws AesException 
	 */
	public static Object[] extract(String xmltext) throws AesException     {
		Object[] result = new Object[3];
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(xmltext);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);

			Element root = document.getDocumentElement();
			NodeList nodelist1 = root.getElementsByTagName("Encrypt");
			NodeList nodelist2 = root.getElementsByTagName("ToUserName");
			result[0] = 0;
			result[1] = nodelist1.item(0).getTextContent();
			if(nodelist2.item(0) != null){
                result[2] = nodelist2.item(0).getTextContent();
            }
			/*result[2] = nodelist2.item(0).getTextContent();*/
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ParseXmlError);
		}
	}

	/**
	 * 生成xml消息
	 * @param encrypt 加密后的消息密文
	 * @param signature 安全签名
	 * @param timestamp 时间戳
	 * @param nonce 随机字符串
	 * @return 生成的xml字符串
	 */
	public static String generate(String encrypt, String signature, String timestamp, String nonce) {

		String format = "<xml>\n" + "<Encrypt><![CDATA[%1$s]]></Encrypt>\n"
				+ "<MsgSignature><![CDATA[%2$s]]></MsgSignature>\n"
				+ "<TimeStamp>%3$s</TimeStamp>\n" + "<Nonce><![CDATA[%4$s]]></Nonce>\n" + "</xml>";
		return String.format(format, encrypt, signature, timestamp, nonce);

	}
	    /** 
	     * 将xml解析成map键值对 
	     * <功能详细描述> 
	     * @param ele 需要解析的xml对象 
	     * @param map 入参为空，用于内部迭代循环使用 
	     * @return 
	     * @see [类、类#方法、类#成员] 
	     */  
	    private  static Map<String, String> parseXML(Element ele, Map<String, String> map)  
	    {  
	          
	        for (Iterator<?> i = ((org.dom4j.Element) ele).elementIterator(); i.hasNext();)  
	        {  
	            Element node = (Element)i.next();  
	            //System.out.println("parseXML node name:" + node.getName());  
	            if (((org.dom4j.Element) node).attributes() != null && ((org.dom4j.Element) node).attributes().size() > 0)  
	            {  
	                for (Iterator<?> j = ((org.dom4j.Element) node).attributeIterator(); j.hasNext();)  
	                {  
	                    Attribute item = (Attribute)j.next();  
	                      
	                    map.put(item.getName(), item.getValue());  
	                }  
	            }  
	            if (((Node) node).getText().length() > 0)  
	            {  
	                map.put(((Node) node).getName(), ((Node) node).getText());  
	            }  
	            if (((org.dom4j.Element) node).elementIterator().hasNext())  
	            {  
	                parseXML(node, map);  
	            }  
	        }  
	        return map;  
	    }
	    /**
	     * 获取xml
	     * @param request
	     * @return
	     * @throws IOException
	     */
	  public static String getXmlFromRequest(HttpServletRequest request) throws IOException{
		  StringBuilder sb = new StringBuilder();  
	        BufferedReader in = request.getReader();  
	        String line;  
	        while ((line = in.readLine()) != null) {  
	            sb.append(line);  
	        }  
	        in.close();
			return sb.toString();
	  }
}
