package com.newland.wechat.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.newland.wechat.base.BaseService;
import com.newland.wechat.entity.CardConsumeInfo;
import com.newland.wechat.service.CardConsumeInfoService;
@Service("cardConsumeInfoService")
public class CardConsumeInfoServiceImpl extends BaseService<CardConsumeInfo> implements CardConsumeInfoService{
	private Logger log = LoggerFactory.getLogger(CardConsumeInfoServiceImpl.class);
	@Override
	public void saveOrUpdateCardConsume(CardConsumeInfo cardConsumeInfo) {
		CardConsumeInfo consumeInfo = new CardConsumeInfo();
		consumeInfo.setCardId(cardConsumeInfo.getCardId());
		CardConsumeInfo cInfo   = this.queryOne(consumeInfo);
		if(cInfo==null){
			cardConsumeInfo.setCreateTime(new Date());
			try{
				log.info("======saveOrUpdateCardConsume 保存核销卡券表数据   cardConsumeInfo：{}======",cardConsumeInfo.toString());
				this.save(cardConsumeInfo);
			}catch(Exception e){
				log.error("======saveOrUpdateCardConsume 保存核销卡券表数据异常 e：{}======",e.toString());
			}
		}else{
			cardConsumeInfo.setUpdateTime(new Date());
			try{
				log.info("======saveOrUpdateCardConsume 更新核销卡券表数据   cardConsumeInfo：{}======",cardConsumeInfo.toString());
				this.updateByIdSelective(cardConsumeInfo, CardConsumeInfo.class, cInfo.getId());
			}catch(Exception e){
				log.error("======saveOrUpdateCardConsume 更新核销卡券表数据异常   e：{}======",e.toString());
			}
			
		}
		
	}
	@Override
	public CardConsumeInfo quaryByCardId(String cardId) {
		CardConsumeInfo cardConsumeInfo = new CardConsumeInfo();
		cardConsumeInfo.setCardId(cardId);
		return this.queryOne(cardConsumeInfo);
	}

}
