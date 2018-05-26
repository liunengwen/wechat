package com.newland.wechat.service;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.dom4j.Element;

public interface HandleEventOrText {

	void process(HttpServletRequest request, HttpServletResponse response, Element xml, String appId) throws IOException, DocumentException, ParseException;

}
