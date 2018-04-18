package com.newland.wechat.service;

import com.newland.wechat.entity.CardConsumeInfo;


public interface CardConsumeInfoService {
	//status为0是创建时数据为1是核销时数据
	void saveOrUpdateCardConsume(CardConsumeInfo cardConsumeInfo);
	CardConsumeInfo quaryByCardId(String cardId);
}
