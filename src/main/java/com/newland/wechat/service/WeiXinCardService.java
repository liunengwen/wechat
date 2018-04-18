package com.newland.wechat.service;

import com.newland.wechat.model.response.ResponseModel;

public interface WeiXinCardService {
	/**
	 * 创建卡券
	 * @param apiSerialNo
	 * @param inParam
	 * @return
	 */
	ResponseModel createCard(String apiSerialNo, String inParam);
	/**
	 * 修改卡券
	 * @param apiSerialNo
	 * @param inParam
	 * @return
	 */
	ResponseModel modifyCard(String apiSerialNo, String inParam);
	/**
	 * 修改库存
	 * @param apiSerialNo
	 * @param inParam
	 * @return
	 */
	ResponseModel modifystock(String apiSerialNo, String inParam);
	/**
	 * 删除卡券
	 * @param apiSerialNo
	 * @param inParam
	 * @return
	 */
	ResponseModel removeCard(String apiSerialNo, String inParam);
	/**
	 * 查询卡券
	 * @param apiSerialNo
	 * @param inParam
	 * @return
	 */
	ResponseModel quaryCard(String apiSerialNo, String inParam);
	/**
	 * 核销卡券
	 * @return
	 */
	ResponseModel deleteCode(String apiSerialNo, String inParam);
	
}
