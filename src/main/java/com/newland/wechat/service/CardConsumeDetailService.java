package com.newland.wechat.service;

import com.newland.wechat.entity.CardConsumeDetail;
import com.newland.wechat.model.response.ResponseModel;

public interface CardConsumeDetailService {
	void saveOrUpdateCardConsumeDetail(CardConsumeDetail cardConsumeDetail);
	
	/*ResponseModel getConsumeCode(String apiSerialNo, String inParam);*/
}
