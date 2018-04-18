package com.newland.wechat.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newland.wechat.entity.CardConsumeDetail;
import com.newland.wechat.entity.CardConsumeInfo;
import com.newland.wechat.model.response.ResponseModel;
import com.newland.wechat.service.BaseService;
import com.newland.wechat.service.CardConsumeDetailService;
import com.newland.wechat.service.CardConsumeInfoService;
@Service("cardConsumeDetailService")
public class CardConsumeDetailServiceImpl extends BaseService<CardConsumeDetail> implements CardConsumeDetailService{
	private Logger log = LoggerFactory.getLogger(CardConsumeDetailServiceImpl.class);
	@Autowired
	private CardConsumeInfoService cardConsumeInfoService;
	@Override
	public void saveOrUpdateCardConsumeDetail(
			CardConsumeDetail cardConsumeDetail) {
		//根据cardId查询卡券名称，卡券类型
		CardConsumeInfo caConsumeInfo = cardConsumeInfoService.quaryByCardId(cardConsumeDetail.getCardId());
		//查询卡券为空，则为公众号创建卡券核销，不进行保存
		if(caConsumeInfo!=null){
			cardConsumeDetail.setConsumeTime(new Date());
			cardConsumeDetail.setCardName(caConsumeInfo.getCardName());
			cardConsumeDetail.setCardType(caConsumeInfo.getCardType());
			CardConsumeDetail caConsumeDetail = new CardConsumeDetail();
			//根据code查询详情表
			caConsumeDetail.setCode(cardConsumeDetail.getCode());
			CardConsumeDetail cDetail = this.queryOne(caConsumeDetail);
			if(cDetail==null){
				try{
					log.info("======saveOrUpdateCardConsumeDetail 保存核销卡券详情表数据======cardConsumeDetail:{}======",cardConsumeDetail.toString());
					cardConsumeDetail.setCreateTime(new Date());
					this.save(cardConsumeDetail);
				}catch(Exception e){
					log.error("======saveOrUpdateCardConsumeDetail 保存核销卡券详情表数据异常======e:{}======",e.toString());
				}	
			}else{
				try{
					log.info("======saveOrUpdateCardConsumeDetail 更新核销卡券详情表数据======cardConsumeDetail:{}======",cardConsumeDetail.toString());
					cardConsumeDetail.setUpdateTime(new Date());
					this.updateByIdSelective(cardConsumeDetail, CardConsumeDetail.class, cDetail.getId());
				}catch(Exception e){
					log.error("======saveOrUpdateCardConsumeDetail 更新核销卡券详情表数据异常======e:{}======",e.toString());
				}
			}
			
		}
	}
	/*@Override
	public ResponseModel getConsumeCode(String apiSerialNo, String inParam) {
		ResponseModel resp = new ResponseModel();
		CardConsumeDetail cardConsumeDetail = new CardConsumeDetail();
		
		return resp;
	}*/

}
