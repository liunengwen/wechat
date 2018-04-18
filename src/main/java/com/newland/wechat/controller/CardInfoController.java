package com.newland.wechat.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.newland.wechat.model.response.ResponseModel;
import com.newland.wechat.service.CardConsumeDetailService;
import com.newland.wechat.service.WeiXinCardService;
import com.newland.wechat.service.WeixinAuthorizeService;


@RestController
@RequestMapping("/rest")
public class CardInfoController {
	private Logger log = LoggerFactory.getLogger(OpenwxController.class);
	@Autowired
	private WeiXinCardService weiXinCardService;
	@Autowired
	private WeixinAuthorizeService weixinAuthorizeService;
	@Autowired
	private CardConsumeDetailService cardConsumeDetailService;
	//创建卡券
	@RequestMapping(value="/{version}/card/createCard", method = RequestMethod.POST)
	public ResponseModel createCard(HttpServletRequest request) {
		String apiSerialNo = (String) request.getAttribute("apiSerialNo");
		String inParam = (String) request.getAttribute("inParam");
		ResponseModel resp = weiXinCardService.createCard(apiSerialNo, inParam);
		return resp;
	}
	/**
	 * 修改卡券
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/{version}/card/modifyCard", method = RequestMethod.POST)
	public ResponseModel modifyCard(HttpServletRequest request) {
		String apiSerialNo = (String) request.getAttribute("apiSerialNo");
		String inParam = (String) request.getAttribute("inParam");
		ResponseModel resp = weiXinCardService.modifyCard(apiSerialNo, inParam);
		return resp;
	}
	/**
	 * 修改库存
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/{version}/card/modifystock", method = RequestMethod.POST)
	public ResponseModel modifystock(HttpServletRequest request) {
		String apiSerialNo = (String) request.getAttribute("apiSerialNo");
		String inParam = (String) request.getAttribute("inParam");
		ResponseModel resp = weiXinCardService.modifystock(apiSerialNo, inParam);
		return resp;
	}
	/**
	 * 查询卡券详情
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/{version}/card/quaryCardDetail", method = RequestMethod.POST)
	public ResponseModel quaryCardDeatil(HttpServletRequest request) {
		String apiSerialNo = (String) request.getAttribute("apiSerialNo");
		String inParam = (String) request.getAttribute("inParam");
		ResponseModel resp = weiXinCardService.quaryCard(apiSerialNo, inParam);
		return resp;
	}
	/**
	 * 删除卡券
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/{version}/card/removeCard", method = RequestMethod.POST)
	public ResponseModel removeCard(HttpServletRequest request) {
		String apiSerialNo = (String) request.getAttribute("apiSerialNo");
		String inParam = (String) request.getAttribute("inParam");
		ResponseModel resp = weiXinCardService.removeCard(apiSerialNo, inParam);
		return resp;
	}
	@RequestMapping(value="/{version}/card/quaryAuthorize", method = RequestMethod.POST)
	public ResponseModel quaryAuthorize(HttpServletRequest request) {
		String apiSerialNo = (String) request.getAttribute("apiSerialNo");
		String inParam = (String) request.getAttribute("inParam");
		ResponseModel resp = weixinAuthorizeService.quaryAuthorize(apiSerialNo, inParam);
		return resp;
	}
	/**
	 * 核销卡券
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/{version}/card/deleteCode", method = RequestMethod.POST)
	public ResponseModel deleteCode(HttpServletRequest request) {
		String apiSerialNo = (String) request.getAttribute("apiSerialNo");
		String inParam = (String) request.getAttribute("inParam");
		ResponseModel resp = weiXinCardService.deleteCode(apiSerialNo, inParam);
		return resp;
	}
	/**
	 * 查询核销卡券信息
	 * @param request
	 * @return
	 */
	/*@RequestMapping(value="/{version}/card/getConsumeCode", method = RequestMethod.POST)
	public ResponseModel getConsumeCode(HttpServletRequest request) {
		String apiSerialNo = (String) request.getAttribute("apiSerialNo");
		String inParam = (String) request.getAttribute("inParam");
		ResponseModel resp = cardConsumeDetailService.getConsumeCode(apiSerialNo, inParam);
		return resp;
	}*/
	
}
