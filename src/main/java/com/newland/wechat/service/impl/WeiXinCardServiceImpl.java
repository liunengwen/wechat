package com.newland.wechat.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.newland.wechat.common.Constants;
import com.newland.wechat.emun.ExceptionCodeEnum;
import com.newland.wechat.entity.CardConsumeDetail;
import com.newland.wechat.entity.CardConsumeInfo;
import com.newland.wechat.model.response.HttpResult;
import com.newland.wechat.model.response.ResponseModel;
import com.newland.wechat.service.CardConsumeDetailService;
import com.newland.wechat.service.CardConsumeInfoService;
import com.newland.wechat.service.WeiXinCardService;
import com.newland.wechat.service.WeixinAuthorizeService;
import com.newland.wechat.service.third.HttpClientService;
import com.newland.wechat.service.third.NotifyService;
import com.newland.wechat.utils.AppBeanUtils;
import com.newland.wechat.utils.CommonUtils;
import com.newland.wechat.utils.PropertyUtils;
import com.newland.wechat.utils.WeChatApiUtil;
@Service("weiXinCardService")
public class WeiXinCardServiceImpl implements WeiXinCardService{
	private Logger log = LoggerFactory.getLogger(WeiXinCardServiceImpl.class);
	@Autowired
	private WeixinAuthorizeService weixinAuthorizeService;
	@Autowired
	private HttpClientService httpClientService;
	@Autowired
	private NotifyService mercInfoService;
	@Autowired
	private CardConsumeInfoService cardConsumeInfoService;
	@Autowired
	private CardConsumeDetailService cardConsumeDetailService;
	@Autowired
	private WeChatApiUtil weChatApiUtil;
	@Override
	public ResponseModel createCard(String apiSerialNo, String inParam) {
		ResponseModel resp = new ResponseModel();
		//参数转换为JSON
		JSONObject params = JSONObject.parseObject(inParam);
		//请求参数非空校验
		String errorMsg = AppBeanUtils.validateJsonValue(params,"appId");
		if(StringUtils.isNotBlank(errorMsg)){
			log.info("======创建卡券请求参数为空 apiSerialNo :{}  errorMsg:{}=======",apiSerialNo,errorMsg);
			return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),errorMsg);
		}
		String appId = params.getString("appId");
		//String cardnm = params.getString("cardnm");
		JSONObject card = params.getJSONObject("card");
		String cardType = card.getString("card_type");
		log.info("======createCard 创建卡券获取到卡券类型  apiSerialNo :{} cardType：{}=======",apiSerialNo,cardType);
		JSONObject getCardType = card.getJSONObject(StringUtils.lowerCase(cardType));
		if(getCardType==null){
			return new ResponseModel(ExceptionCodeEnum.PARAM_FORMAT_ERROR.getCode(),ExceptionCodeEnum.PARAM_FORMAT_ERROR.getLabel());
		}
		
		ResponseModel checkCard = checkCreateCard(getCardType,cardType);
		if(!checkCard.getCode().equals(Constants.SUCCESS_CODE)){
			return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),checkCard.getMessage());
		}
		String cardName = getCardType.getJSONObject("base_info").getString("title");
		//根据商户号和appId查询商户是否授权		
		String accessToken = weixinAuthorizeService.getAccessTokenByAppIdAndMecId(appId);
		if(StringUtils.isBlank(accessToken)){
			return new ResponseModel(ExceptionCodeEnum.INVALID_AUTHORIZE.getCode(),ExceptionCodeEnum.INVALID_AUTHORIZE.getLabel());
		}
		JSONObject cardInfo = getCardInfo(getCardType,accessToken);
		card.put(StringUtils.lowerCase(cardType), cardInfo);
		log.info("======上传图片后获得请求参数  apiSerialNo :{}, card：{}======",apiSerialNo,card.toString());
		
		JSONObject cardJson  = new JSONObject();
		cardJson.put("card", card);
		String createCardUrl = PropertyUtils.getCreateCardUrl(accessToken);
		HttpResult httpResult = null;
		try {
			httpResult = httpClientService.doPost(createCardUrl, cardJson.toString());
			if(httpResult != null && httpResult.getCode() == HttpStatus.SC_OK 
					&& StringUtils.isNotBlank(httpResult.getData())){
				JSONObject resultJson = JSONObject.parseObject(httpResult.getData());
				String retCode = resultJson.getString("errcode");
				//retCode=000000表示成功
				if("0".equals(retCode)){
					String card_id = resultJson.getString("card_id");
					log.info("======createCard 创建卡券成功 获取到卡券id为  apiSerialNo:{}  card_id：{}======",apiSerialNo,card_id);
					CardConsumeInfo cardConsumeInfo = new CardConsumeInfo();
					cardConsumeInfo.setCardId(card_id);
					cardConsumeInfo.setCardType(card.getString("card_type"));
					cardConsumeInfo.setCardName(cardName);
					//cardConsumeInfo.setCardNm(cardnm);
					cardConsumeInfoService.saveOrUpdateCardConsume(cardConsumeInfo);
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("card_id", card_id);
					resp = new ResponseModel(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG,data);
					log.info("======createCard 创建卡券成功 返回数据  apiSerialNo:{}  resp：{}======",apiSerialNo,resp.getCode(),resp.getMessage());
				}else{
					log.error("======调用微信接口创建卡券失败======失败原因 erromessage:{}",resultJson.getString("errmsg"));
					return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),resultJson.getString("errmsg"));
				}
			}else{
				log.error("======调用微信接口创建卡券失败======");
				return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),ExceptionCodeEnum.WECHAT_FAILED.getLabel());
			}
		}catch(Exception e){
			log.error("======调用微信接口创建卡券异常  e:{}======",e.toString());
			return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),ExceptionCodeEnum.WECHAT_FAILED.getLabel());
		}
		return resp;
	}

	@Override
	public ResponseModel modifyCard(String apiSerialNo, String inParam) {
		ResponseModel resp = new ResponseModel();
		Map<String, Object> data = new HashMap<String, Object>();
		//参数转换为JSON
		JSONObject params = JSONObject.parseObject(inParam);
		//请求参数非空校验
		JSONObject card = params.getJSONObject("card");
		String errorMsg = AppBeanUtils.validateJsonValue(params,"appId");
		if(StringUtils.isNotBlank(errorMsg)){
			log.info("======modifyCard 修改卡券请求参数为空 apiSerialNo：{}  errorMsg:{}",apiSerialNo,errorMsg);
			return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),errorMsg);
		}
		
		String appId = params.getString("appId");
	
		//根据商户号和appId查询商户是否授权	
		//根据cardId查询类型
		String cardType = "";
		if(card==null){
			return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),ExceptionCodeEnum.PARAM_NULL.getLabel());
		}
		if(StringUtils.isBlank(card.getString("card_id"))){
			log.info("======modifyCard 修改卡券请求参数card_id为空  apiSerialNo:{}======",apiSerialNo);
			return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),"修改卡券请求参数card_id为空");
		}
		if(cardConsumeInfoService.quaryByCardId(card.getString("card_id"))!=null){
		 cardType =cardConsumeInfoService.quaryByCardId(card.getString("card_id")).getCardType();
		 log.info("======modifyCard 修改卡券根据cardId查询到的卡券类型为  apiSerialNo:{}  cardType：{}======",apiSerialNo,cardType);
		}
		String cardTypes = StringUtils.lowerCase(cardType);
		if(card.getJSONObject(cardTypes)==null){
			return new ResponseModel(ExceptionCodeEnum.PARAM_FORMAT_ERROR.getCode(),ExceptionCodeEnum.PARAM_FORMAT_ERROR.getLabel());
		}
		ResponseModel checkCard = checkUpdateCard(card.getJSONObject(cardTypes));
		if(!checkCard.getCode().equals(Constants.SUCCESS_CODE)){
			return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),checkCard.getMessage());
		}
		
		String accessToken = weixinAuthorizeService.getAccessTokenByAppIdAndMecId(appId);
		if(StringUtils.isBlank(accessToken)){
			return new ResponseModel(ExceptionCodeEnum.INVALID_AUTHORIZE.getCode(),ExceptionCodeEnum.INVALID_AUTHORIZE.getLabel());
		}
		JSONObject cardInfo = getCardInfo(card.getJSONObject(cardTypes),accessToken);
		
		card.put(cardTypes, cardInfo);
		
		log.info("======上传图片后获得请求参数 card：{}",card.toString());
		
		String modifyCardUrl = PropertyUtils.getUpdateCardInfoUrl(accessToken);
		HttpResult httpResult = null;
		try {
			httpResult = httpClientService.doPost(modifyCardUrl, card.toString());
			if(httpResult != null && httpResult.getCode() == HttpStatus.SC_OK 
					&& StringUtils.isNotBlank(httpResult.getData())){
				JSONObject resultJson = JSONObject.parseObject(httpResult.getData());
				String retCode = resultJson.getString("errcode");
				//retCode=000000表示成功
				if("0".equals(retCode)){
					data.put("send_check", resultJson.getString("send_check"));
					resp = new ResponseModel(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG,data);
				}else{
					log.error("======调用微信接口修改卡券失败======失败原因 erromessage:{}",resultJson.getString("errmsg"));
					return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),resultJson.getString("errmsg"));
				}
			}else{
				log.error("======调用微信接口修改卡券失败======");
				return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),ExceptionCodeEnum.WECHAT_FAILED.getLabel());
			}
		}catch(Exception e){
			log.error("======调用微信接口修改卡券异常  e:{}======",e.toString());
			return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),ExceptionCodeEnum.WECHAT_FAILED.getLabel());
		}
		return resp;
	}

	@Override
	public ResponseModel modifystock(String apiSerialNo, String inParam) {
		ResponseModel resp = new ResponseModel();
		//参数转换为JSON
		JSONObject params = JSONObject.parseObject(inParam);
		//请求参数非空校验
		String errorMsg = AppBeanUtils.validateJsonValue(params,"cardId","appId");
		if(StringUtils.isNotBlank(errorMsg)){
			log.info("======修改库存请求参数为空 errorMsg:{}======",errorMsg);
			return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),errorMsg);
		}
		String appId = params.getString("appId");
		String cardId = params.getString("cardId");
		String increaseStockValue = params.getString("increaseStockValue");
		String reduceVtockValue = params.getString("reduceVtockValue");
		//根据appId查询商户是否授权		
		String accessToken = weixinAuthorizeService.getAccessTokenByAppIdAndMecId(appId);
		if(StringUtils.isBlank(accessToken)){
			return new ResponseModel(ExceptionCodeEnum.INVALID_AUTHORIZE.getCode(),ExceptionCodeEnum.INVALID_AUTHORIZE.getLabel());
		}
		
		String modifystockUrl = PropertyUtils.getModifystockUrl(accessToken);
		JSONObject json = new JSONObject();
		json.put("card_id", cardId);
		if(StringUtils.isNotBlank(increaseStockValue)){
			json.put("increase_stock_value", Integer.parseInt(increaseStockValue));
		}
		if(StringUtils.isNotBlank(reduceVtockValue)){
		json.put("reduce_stock_value", Integer.parseInt(reduceVtockValue));
		}
		HttpResult httpResult = null;
		try {
			httpResult = httpClientService.doPost(modifystockUrl, json.toString());
			if(httpResult != null && httpResult.getCode() == HttpStatus.SC_OK 
					&& StringUtils.isNotBlank(httpResult.getData())){
				JSONObject resultJson = JSONObject.parseObject(httpResult.getData());
				String retCode = resultJson.getString("errcode");
				//retCode=000000表示成功
				if("0".equals(retCode)){
					resp = new ResponseModel(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG);
				}else{
					log.error("======调用微信接口修改库存失败======失败原因 erromessage:{}======",resultJson.getString("errmsg"));
					return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),resultJson.getString("errmsg"));
				}
			}else{
				log.error("======调用微信接口修改库存失败======");
				return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),ExceptionCodeEnum.WECHAT_FAILED.getLabel());
			}
		}catch(Exception e){
			log.error("======调用微信接口修改库存异常  e:{}======",e.toString());
			return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),ExceptionCodeEnum.WECHAT_FAILED.getLabel());
		}
		return resp;
	}

	@Override
	public ResponseModel removeCard(String apiSerialNo, String inParam) {
		ResponseModel resp = new ResponseModel();
		//参数转换为JSON
		JSONObject params = JSONObject.parseObject(inParam);
		//请求参数非空校验
		String errorMsg = AppBeanUtils.validateJsonValue(params,"cardId","appId");
		if(StringUtils.isNotBlank(errorMsg)){
			log.info("======删除卡券请求参数为空 errorMsg:{}======",errorMsg);
			return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),errorMsg);
		}
		String appId = params.getString("appId");
		String cardId = params.getString("cardId");
		//根据商户号和appId查询商户是否授权		
		String accessToken = weixinAuthorizeService.getAccessTokenByAppIdAndMecId(appId);
		if(StringUtils.isBlank(accessToken)){
			return new ResponseModel(ExceptionCodeEnum.INVALID_AUTHORIZE.getCode(),ExceptionCodeEnum.INVALID_AUTHORIZE.getLabel());
		}
		String removeCardUrl = PropertyUtils.getDelCardUrl(accessToken);
		JSONObject json = new JSONObject();
		json.put("card_id", cardId);
		HttpResult httpResult = null;
		try {
			httpResult = httpClientService.doPost(removeCardUrl, json.toString());
			if(httpResult != null && httpResult.getCode() == HttpStatus.SC_OK 
					&& StringUtils.isNotBlank(httpResult.getData())){
				JSONObject resultJson = JSONObject.parseObject(httpResult.getData());
				String retCode = resultJson.getString("errcode");
				//retCode=000000表示成功
				if("0".equals(retCode)){
					resp = new ResponseModel(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG);
				}else{
					log.error("======调用微信接口删除卡券失败======失败原因 erromessage:{}======",resultJson.getString("errmsg"));
					return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),resultJson.getString("errmsg"));
				}
			}else{
				log.error("======调用微信接口删除卡券失败======");
				return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),ExceptionCodeEnum.WECHAT_FAILED.getLabel());
			}
		}catch(Exception e){
			log.error("======调用微信接口删除卡券异常  e:{}======",e.toString());
			return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),ExceptionCodeEnum.WECHAT_FAILED.getLabel());
		}
		return resp;
	}

	@Override
	public ResponseModel quaryCard(String apiSerialNo, String inParam) {
		ResponseModel resp = new ResponseModel();
		//参数转换为JSON
		JSONObject params = JSONObject.parseObject(inParam);
		//请求参数非空校验
		String errorMsg = AppBeanUtils.validateJsonValue(params,"appId","cardId");
		if(StringUtils.isNotBlank(errorMsg)){
			log.info("======查询卡券详情请求参数为空 errorMsg:{}======",errorMsg);
			return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),errorMsg);
		}
		String appId = params.getString("appId");
		String cardId = params.getString("cardId");
		//根据商户号和appId查询商户是否授权		
		String accessToken = weixinAuthorizeService.getAccessTokenByAppIdAndMecId(appId);
		if(StringUtils.isBlank(accessToken)){
			return new ResponseModel(ExceptionCodeEnum.INVALID_AUTHORIZE.getCode(),ExceptionCodeEnum.INVALID_AUTHORIZE.getLabel());
		}
		String cardInfoUrl = PropertyUtils.getCardInfoUrl(accessToken);
		JSONObject json = new JSONObject();
		json.put("card_id", cardId);
		HttpResult httpResult = null;
		try {
			httpResult = httpClientService.doPost(cardInfoUrl, json.toString());
			if(httpResult != null && httpResult.getCode() == HttpStatus.SC_OK 
					&& StringUtils.isNotBlank(httpResult.getData())){
				JSONObject resultJson = JSONObject.parseObject(httpResult.getData());
				String retCode = resultJson.getString("errcode");
				//retCode=000000表示成功
				if("0".equals(retCode)){
					resp = new ResponseModel(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG,resultJson.getJSONObject("card"));
				}else{
					log.error("======调用微信接口查询卡券失败======失败原因 erromessage:{}======",resultJson.getString("errmsg"));
					return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),resultJson.getString("errmsg"));
				}
			}else{
				log.error("======调用微信接口查询卡券失败======");
				return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),ExceptionCodeEnum.WECHAT_FAILED.getLabel());
			}
		}catch(Exception e){
			log.error("======调用微信接口查询卡券异常  e:{}======",e.toString());
			return new ResponseModel(ExceptionCodeEnum.WECHAT_FAILED.getCode(),ExceptionCodeEnum.WECHAT_FAILED.getLabel());
		}
		return resp;
	}

	@Override
	public ResponseModel deleteCode(String apiSerialNo, String inParam) {
		ResponseModel resp = new ResponseModel();
		//参数转换为JSON
		JSONObject params = JSONObject.parseObject(inParam);
		//请求参数非空校验
		String errorMsg = AppBeanUtils.validateJsonValue(params,"appId","code","trmNo");
		if(StringUtils.isNotBlank(errorMsg)){
			log.info("======核销卡券请求参数为空 errorMsg:{}======",errorMsg);
			return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),errorMsg);
		}
		String appId = params.getString("appId");
		String code = params.getString("code");
		String sn = params.getString("trmNo");
		//根据sn查询trmNo
		/*String trmNo = findStoreInfo(sn);*/
		
		//根据商户号和appId查询商户是否授权		
		String accessToken = weixinAuthorizeService.getAccessTokenByAppIdAndMecId(appId);
		if(StringUtils.isBlank(accessToken)){
			return new ResponseModel(ExceptionCodeEnum.INVALID_AUTHORIZE.getCode(),ExceptionCodeEnum.INVALID_AUTHORIZE.getLabel());
		}
		
		String codeInfoUrl = PropertyUtils.getCodeStatusUrl(accessToken);
		JSONObject json = new JSONObject();
		if(StringUtils.isNotBlank(code)){
			json.put("code", code);
		}
		json.put("check_consume", true);
		HttpResult httpResult = null;
		
		try {
			httpResult = httpClientService.doPost(codeInfoUrl, json.toString());
		} catch (Exception e) {
			log.error("======调用微信接口查询卡券状态异常  e:{}======",e.toString());
			return new ResponseModel(ExceptionCodeEnum.QUARY_CODE_FAILE.getCode(),ExceptionCodeEnum.QUARY_CODE_FAILE.getLabel());
		}
		if(httpResult != null && httpResult.getCode() == HttpStatus.SC_OK 
				&& StringUtils.isNotBlank(httpResult.getData())){
			JSONObject resultJson = JSONObject.parseObject(httpResult.getData());
			String retCode = resultJson.getString("errcode");
			//retCode=000000表示成功
			if("0".equals(retCode)){
				String can_consume = resultJson.getString("can_consume");//卡券是否可以核销
				JSONObject card = resultJson.getJSONObject("card");
				String cardId = card.getString("card_id");
				if("true".equals(can_consume)){
					String consumeUrl = PropertyUtils.getDeleteCodeUrl(accessToken);
					JSONObject consumeJson = new JSONObject();
					consumeJson.put("code", code);
					HttpResult consumeHttpResult = null;
					try {
						consumeHttpResult = httpClientService.doPost(consumeUrl, consumeJson.toString());
					} catch (Exception e) {
						log.error("======调用微信接口核销卡券异常  e:{}======",e.toString());
						return new ResponseModel(ExceptionCodeEnum.CONSUME_CODE_FAILE.getCode(),ExceptionCodeEnum.CONSUME_CODE_FAILE.getLabel());
					}
					if(consumeHttpResult != null && consumeHttpResult.getCode() == HttpStatus.SC_OK 
							&& StringUtils.isNotBlank(consumeHttpResult.getData())){
						JSONObject consumeResultJson = JSONObject.parseObject(consumeHttpResult.getData());
						String consumRetCode = consumeResultJson.getString("errcode");
						CardConsumeDetail cardConsumeDetail = new CardConsumeDetail();
						cardConsumeDetail.setCardId(cardId);
						cardConsumeDetail.setAppId(appId);
						cardConsumeDetail.setConsumeTime(new Date());
						cardConsumeDetail.setTrmNo(sn);
						cardConsumeDetail.setCode(code);
						if("0".equals(consumRetCode)){
							//核销成功
							cardConsumeDetail.setStatus("0");
							cardConsumeDetailService.saveOrUpdateCardConsumeDetail(cardConsumeDetail);
							Map<String, Object> data = new HashMap<String, Object>();
							if(StringUtils.isNotBlank( cardConsumeDetail.getCardName())&&StringUtils.isNotBlank(cardConsumeDetail.getCardType())){
								data.put("cardName", cardConsumeDetail.getCardName());
								data.put("cardType", getCardType(cardConsumeDetail.getCardType()));
							}
							resp = new ResponseModel(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG,data);
						}else{
							//核销失败
							log.error("======调用微信接口核销失败======失败原因 erromessage:{}======",consumeResultJson.getString("errmsg"));
							cardConsumeDetail.setStatus("1");
							cardConsumeDetail.setReason(consumeResultJson.getString("errmsg"));
							cardConsumeDetailService.saveOrUpdateCardConsumeDetail(cardConsumeDetail);
							return new ResponseModel(consumeResultJson.getString("errcode"),consumeResultJson.getString("errmsg"));
						}
					}else{
						log.error("======调用微信接口核销卡券Http返回失败   状态码======httpCode:{}======",consumeHttpResult.getCode());
						return new ResponseModel(ExceptionCodeEnum.CONSUME_CODE_FAILE.getCode(),String.valueOf(consumeHttpResult.getCode()));
					}
				}else{
					//卡券不能核销
					log.error("======调用微信接口核销卡券，卡券已转增，或核销删除，无法核销======");
					return new ResponseModel(ExceptionCodeEnum.INVALID_CONSUM.getCode(),ExceptionCodeEnum.INVALID_CONSUM.getLabel());
				}
			}else{
				log.error("======调用微信接口查询code失败======失败原因 erromessage:{}======",resultJson.getString("errmsg"));
				return new ResponseModel(resultJson.getString("errcode"),resultJson.getString("errmsg"));
			}
		}else{
			log.error("======调用微信接口核销卡券失败======");
			return new ResponseModel(ExceptionCodeEnum.QUARY_CODE_FAILE.getCode(),String.valueOf(httpResult.getCode()));
		}
	return resp;
}
	//获得图片路径上传到微信服务器
	public JSONObject getCardInfo(JSONObject cardType,String accessToken){
		//基础字段
		JSONObject base_info = cardType.getJSONObject("base_info");
		//高级字段
		JSONObject advanced_info = cardType.getJSONObject("advanced_info");
		//封面介绍
		JSONObject abstracts = new JSONObject();
		if(advanced_info!=null){
			abstracts = advanced_info.getJSONObject("abstract");
			JSONObject use_condition = advanced_info.getJSONObject("use_condition");
			if(use_condition!=null&&use_condition.containsKey("can_use_with_other_discount")){
				String can_use_with_other_discount = use_condition.getString("can_use_with_other_discount");
				if(StringUtils.isNotBlank(can_use_with_other_discount) &&can_use_with_other_discount.equals("true")){
					
					use_condition.put("can_use_with_other_discount", true);
				}if(StringUtils.isNotBlank(can_use_with_other_discount) &&can_use_with_other_discount.equals("false")){
					
					use_condition.put("can_use_with_other_discount", false);
				}
			}
			advanced_info.put("abstract", abstracts);
		}
		//图文介绍
		JSONArray text_image_list = new JSONArray();
		if(advanced_info!=null&&advanced_info.containsKey("text_image_list")){
			text_image_list  = advanced_info.getJSONArray("text_image_list");
		}
		//logo路径
		String logoUrl = "";
		if(base_info!=null){
			logoUrl = base_info.getString("logo_url");
		}
		JSONArray icon_url_list = new JSONArray();
		if(abstracts!=null){
			icon_url_list = abstracts.getJSONArray("icon_url_list");
		}
		String icon_url_listUrl = "";
		//封面路径
		if(icon_url_list!=null&&icon_url_list.size()>0){
			icon_url_listUrl = icon_url_list.getString(0);
		}
		JSONArray icon_url_listRet = new JSONArray();
		JSONArray text_image_listRet = new JSONArray();
		String logReturnUrl ="";
		String icon_url_listUrlRet ="";
		if(text_image_list!=null&&text_image_list.size()>0){
			  for(int i=0;i<text_image_list.size();i++){
			    JSONObject job = text_image_list.getJSONObject(i);  
			    String imgUrl = job.getString("image_url");  // 得到 每个对象中的属性值
			    String text = job.getString("text");
			    try {
					String imgUrlRet = weChatApiUtil.uploadMedia(imgUrl,accessToken);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("image_url", imgUrlRet);
					jsonObject.put("text", text);
					text_image_listRet.add(jsonObject);
				} catch (IllegalStateException | IOException e) {
					log.error("======getCardInfo ======将图片上传到微信服务器异常====== e",e.toString());
				}
			  }
			}
		try {
			if(StringUtils.isNotBlank(logoUrl)&&!(logoUrl.startsWith("http"))){
				logReturnUrl = weChatApiUtil.uploadMedia(logoUrl,accessToken);
			}
			if(StringUtils.isNotBlank(icon_url_listUrl)){
				 icon_url_listUrlRet = weChatApiUtil.uploadMedia(icon_url_listUrl,accessToken);
			}
			
		} catch (IllegalStateException | IOException e) {
			log.error("======getCardInfo ======将图片上传到微信服务器异常====== e",e.toString());
		}
		
		if(StringUtils.isNotBlank(logReturnUrl)){
			base_info.put("logo_url", logReturnUrl);
		}
		if(StringUtils.isNotBlank(icon_url_listUrlRet)){
			icon_url_listRet.add(icon_url_listUrlRet);
			abstracts.put("icon_url_list", icon_url_listRet);
			advanced_info.put("abstract", abstracts);
		}
		
		if(text_image_listRet.size()>0){
			advanced_info.put("text_image_list", text_image_listRet);
		}
		if(base_info!=null){
			String can_give_friend = base_info.getString("can_give_friend");
			String can_share = base_info.getString("can_share");
			if(StringUtils.isNotBlank(can_give_friend) &&can_give_friend.equals("true")){
				
				base_info.put("can_give_friend", true);
			}if(StringUtils.isNotBlank(can_give_friend) &&can_give_friend.equals("false")){
				base_info.put("can_give_friend", false);
			}
			if(StringUtils.isNotBlank(can_share) &&can_share.equals("true")){
				base_info.put("can_share", true);
			}if(StringUtils.isNotBlank(can_share) &&can_share.equals("false")){
				base_info.put("can_share", false);
			}
			cardType.put("base_info", base_info);
		}
		if(advanced_info!=null){
			cardType.put("advanced_info", advanced_info);
		}
		return cardType;
	}
	public String getCardType(String cardType){
		String cardTypes = "";
		if("GROUPON".equals(cardType)){
			cardTypes = "团购券";
		}if("GIFT".equals(cardType)){
			cardTypes = "兑换券";
		}if("CASH".equals(cardType)){
			cardTypes = "代金券";
		}if("GENERAL_COUPON".equals(cardType)){
			cardTypes = "优惠券";
		}if("DISCOUNT".equals(cardType)){
			cardTypes = "折扣券";
		}
		return cardTypes;
	}
	public ResponseModel checkCreateCard(JSONObject cardInfo,String cardType){
		log.info("======checkCreateCard 创建请求参数验证 cardInfo:{}   cardType:{}======",cardInfo.toString(),cardType);
		ResponseModel resp = new ResponseModel();
		//基础字段
		JSONObject base_info = cardInfo.getJSONObject("base_info");
		//高级字段
		JSONObject advanced_info = cardInfo.getJSONObject("advanced_info");
		//请求参数非空校验
		StringBuffer returnMsg = new StringBuffer();
		if(base_info!=null){
			String base_infoErrorMsg = AppBeanUtils.validateJsonValue(base_info,"logo_url","code_type",
					"brand_name","title","color","notice");
			if(StringUtils.isNotBlank(base_infoErrorMsg)){
				log.info("======创建请求base_info参数为空 errorMsg:{}======",base_infoErrorMsg);
				returnMsg.append(base_infoErrorMsg);
				//return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),errorMsg);
			}
			JSONObject sku = base_info.getJSONObject("sku");
			String skuErrorMsg = AppBeanUtils.validateJsonValue(sku,"quantity");
			if(StringUtils.isNotBlank(skuErrorMsg)){
				log.info("======创建请求sku参数为空 errorMsg:{}======",skuErrorMsg);
				//return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),skuErrorMsg);
				returnMsg.append(skuErrorMsg);
			}
			JSONObject date_info = base_info.getJSONObject("date_info");
			String typeErrorMsg = AppBeanUtils.validateJsonValue(date_info,"type");
			if(StringUtils.isNotBlank(typeErrorMsg)){
				log.info("======创建请求type参数为空 errorMsg:{}======",typeErrorMsg);
				//return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),skuErrorMsg);
				returnMsg.append(typeErrorMsg);
			}else{
				String date_infoErrorMsg ="";
				if(date_info.getString("type").equals("DATE_TYPE_FIX_TIME_RANGE")){
					date_infoErrorMsg = AppBeanUtils.validateJsonValue(date_info,"begin_timestamp","end_timestamp");
					if(StringUtils.isNotBlank(date_infoErrorMsg)){
						log.info("======创建请求date_info参数为空 errorMsg:{}======",date_infoErrorMsg);
						//return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),skuErrorMsg);
						returnMsg.append(date_infoErrorMsg);
					}
				}
				if(date_info.getString("type").equals("DATE_TYPE_FIX_TERM")){
					date_infoErrorMsg = AppBeanUtils.validateJsonValue(date_info,"fixed_term","fixed_term");
					if(StringUtils.isNotBlank(date_infoErrorMsg)){
						log.info("======创建请求date_info参数为空 errorMsg:{}======",date_infoErrorMsg);
						//return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),skuErrorMsg);
						returnMsg.append(date_infoErrorMsg);
					}
				}
			}
		}else{
			returnMsg.append("base_info不能为空");
		}
		
		if(advanced_info !=null){
			JSONObject abstracts = advanced_info.getJSONObject("abstract");
			String abstractsErroMsg = "";
			if(abstracts !=null){
				abstractsErroMsg = AppBeanUtils.validateJsonValue(abstracts,"abstract","icon_url_list");
				if(StringUtils.isNotBlank(abstractsErroMsg)){
					log.info("======创建请求参数为空 errorMsg:{}======",abstractsErroMsg);
					//return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),skuErrorMsg);
					returnMsg.append(abstractsErroMsg);
				}
			}
			JSONArray text_image_list = advanced_info.getJSONArray("text_image_list");
			if(text_image_list!=null&&text_image_list.size()>0){
				String text_image_listErroMsg = "";
				  for(int i=0;i<text_image_list.size();i++){
				    JSONObject job = text_image_list.getJSONObject(i);  
				    text_image_listErroMsg = AppBeanUtils.validateJsonValue(job,"image_url","text");
				    if(StringUtils.isNotBlank(text_image_listErroMsg)){
						log.info("======创建请求参数为空 errorMsg:{}",text_image_listErroMsg);
						//return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),skuErrorMsg);
						returnMsg.append(text_image_listErroMsg);
					}
				  }
			}
		}
		String cardTypeErro = "";
		if("GROUPON".equals(cardType)){
			cardTypeErro =AppBeanUtils.validateJsonValue(cardInfo,"deal_detail");
		}if("GIFT".equals(cardType)){
			//cardTypeErro =AppBeanUtils.validateJsonValue(cardInfo,"gift");
		}if("CASH".equals(cardType)){
			cardTypeErro =AppBeanUtils.validateJsonValue(cardInfo,"reduce_cost");
		}if("GENERAL_COUPON".equals(cardType)){
			cardTypeErro =AppBeanUtils.validateJsonValue(cardInfo,"default_detail");
		}if("DISCOUNT".equals(cardType)){
			cardTypeErro =AppBeanUtils.validateJsonValue(cardInfo,"discount");
		}
		if(StringUtils.isNotBlank(cardTypeErro)){
			returnMsg.append(cardTypeErro);
		}
		if(StringUtils.isNotBlank(returnMsg)){
			
			return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),returnMsg.toString());
		}
		resp = new ResponseModel(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG);
		return resp;
	}
	
	public ResponseModel checkUpdateCard(JSONObject cardInfo){
		ResponseModel resp = new ResponseModel();
		log.info("======checkUpdateCard 修改请求参数验证 cardInfo:{} ======",cardInfo.toString());
		//高级字段
		JSONObject advanced_info = cardInfo.getJSONObject("advanced_info");
		//请求参数非空校验
		StringBuffer returnMsg = new StringBuffer();
		if(advanced_info !=null){
			JSONObject abstracts = advanced_info.getJSONObject("abstract");
			String abstractsErroMsg = "";
			if(abstracts !=null){
				abstractsErroMsg = AppBeanUtils.validateJsonValue(abstracts,"abstract","icon_url_list");
				if(StringUtils.isNotBlank(abstractsErroMsg)){
					log.info("======创建请求参数为空 errorMsg:{}======",abstractsErroMsg);
					//return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),skuErrorMsg);
					returnMsg.append(abstractsErroMsg);
				}
			}
			JSONArray text_image_list = advanced_info.getJSONArray("text_image_list");
			if(text_image_list!=null&&text_image_list.size()>0){
				String text_image_listErroMsg = "";
				  for(int i=0;i<text_image_list.size();i++){
				    JSONObject job = text_image_list.getJSONObject(i);  
				    text_image_listErroMsg = AppBeanUtils.validateJsonValue(job,"image_url","text");
				    if(StringUtils.isNotBlank(text_image_listErroMsg)){
						log.info("======创建请求参数为空 errorMsg:{}======",text_image_listErroMsg);
						//return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),skuErrorMsg);
						returnMsg.append(text_image_listErroMsg);
					}
				  }
			}
		}
		if(StringUtils.isNotBlank(returnMsg)){
			
			return new ResponseModel(ExceptionCodeEnum.PARAM_NULL.getCode(),returnMsg.toString());
		}
		resp = new ResponseModel(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG);
		return resp;
	}
	/**
	 * 收单--门户信息查询接口
	 * 根据sn号查询门店信息
	 */
	/*public String findStoreInfo(String sn) {
		String queryStoeInfoUrl = PropertyUtils.queryStoreInfoUrl();
		JSONObject inParam = new JSONObject();
		inParam.put("type", "qryStoeInfo");
		inParam.put("sn", sn);
		HttpResult httpResult = null;
		String trmNo = "";
		try {
			//调用收单门店信息接口
			httpResult = httpClientService.doPost(queryStoeInfoUrl, inParam.toString());
			if(httpResult != null && httpResult.getCode() == HttpStatus.SC_OK 
					&& StringUtils.isNotBlank(httpResult.getData())){
				JSONObject resultJson = JSONObject.parseObject(httpResult.getData());
				String retCode = resultJson.getString("retCode");
				//retCode=000000表示成功
				if("000000".equals(retCode)){
					//保存门店及法人进件信息
					trmNo = resultJson.getString("trmNo");
				}else{
					log.error("=== queryEtmspubStoeInfoBySn sn : {} , returncode : {} ,retMsg : {} ===",sn,httpResult.getCode(),CommonUtils.UTF8Decode(resultJson.getString("retMsg")));
				}
			}else{
				log.error("=== queryEtmspubStoeInfoBySn sn : {}  failed code : {} ===",sn,httpResult.getCode());
			}
		} catch (Exception e) {
			log.error("=== queryEtmspubStoeInfoBySn sn : {} , error : {} ===",sn,e);
			
		}
		return trmNo;
	}*/
}


