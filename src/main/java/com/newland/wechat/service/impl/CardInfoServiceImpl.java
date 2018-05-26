package com.newland.wechat.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.newland.wechat.base.BaseService;
import com.newland.wechat.entity.CardInfo;
import com.newland.wechat.service.CardInfoService;
@Service("cardInfoService")
public class CardInfoServiceImpl extends BaseService<CardInfo> implements CardInfoService{

	@Override
	public void saveOrUpdateCardInfo(CardInfo card) {
		if(card!=null&&card.getCardId()!=null){
			card.setCreateTime(new Date());
				this.save(card);
		}
	}

	

}
