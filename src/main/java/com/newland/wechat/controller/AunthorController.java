package com.newland.wechat.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.newland.wechat.model.response.ResponseModel;
import com.newland.wechat.utils.WeChatApiUtil;

@RestController
@RequestMapping("upload")
public class AunthorController {
	@Autowired
	private WeChatApiUtil weChatApiUtil;
	
	@RequestMapping(value="/{version}/uploadFile", method = RequestMethod.POST)
	public ResponseModel uploadFile(HttpServletRequest request,MultipartFile fileUpload) throws IllegalStateException, IOException {
		ResponseModel resp = new ResponseModel();
		String appId = request.getParameter("appId");
		resp = weChatApiUtil.uploadMediaToService(fileUpload,appId);
		return resp;
	}
}
