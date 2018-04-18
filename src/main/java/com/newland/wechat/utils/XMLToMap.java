package com.newland.wechat.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XMLToMap {  
      
      
    public static Map<String, String> getXML(String requestXml){  
        Map<String, String> map = new HashMap<String, String>();  
        // 将字符串转为XML  
        Document doc;  
        try {  
            doc = DocumentHelper.parseText(requestXml);  
            // 获取根节点  
            Element rootElm = doc.getRootElement();//从root根节点获取请求报文  
            XMLToMap xmlIntoMap = new XMLToMap();  
            map = xmlIntoMap.parseXML(rootElm, new HashMap<String, String>());  
        } catch (DocumentException e) {  
            e.printStackTrace();  
        }  
         
          
        return map;  
    }  
    /** 
     * 将xml解析成map键值对 
     * <功能详细描述> 
     * @param ele 需要解析的xml对象 
     * @param map 入参为空，用于内部迭代循环使用 
     * @return 
     * @see [类、类#方法、类#成员] 
     */  
    private  Map<String, String> parseXML(Element ele, Map<String, String> map)  
    {  
          
        for (Iterator<?> i = ele.elementIterator(); i.hasNext();)  
        {  
            Element node = (Element)i.next();  
            //System.out.println("parseXML node name:" + node.getName());  
            if (node.attributes() != null && node.attributes().size() > 0)  
            {  
                for (Iterator<?> j = node.attributeIterator(); j.hasNext();)  
                {  
                    Attribute item = (Attribute)j.next();  
                      
                    map.put(item.getName(), item.getValue());  
                }  
            }  
            if (node.getText().length() > 0)  
            {  
                map.put(node.getName(), node.getText());  
            }  
            if (node.elementIterator().hasNext())  
            {  
                parseXML(node, map);  
            }  
        }  
        return map;  
    }  
}  
