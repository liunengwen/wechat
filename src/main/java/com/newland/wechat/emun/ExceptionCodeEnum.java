package com.newland.wechat.emun;
/**
 * 异常码
 * @author fangxu.ge
 * 
 */
public enum ExceptionCodeEnum {

    /** common 异常码 **/

	UNKNOWN("UNKNOWN", "未知异常"),

    EXCEPTION("EXCEPTION", "系统异常"),

	ERROR("ERROR", "系统错误"),

    BAD_REQUEST("BAD_REQUEST", "无效请求"),

    SIGN_ERROR("SIGN_ERROR", "签名错误"),
    
    PARAM_NULL("PARAM_NULL", "输入参数为空"),
    
    PARAM_FORMAT_ERROR("PARAM_FORMAT_ERROR", "输入参数格式错误"),
    
	INVALID_AUTHORIZE("INVALID_AUTHORIZE","公众号未授权"),
	
	WECHAT_FAILED("WECHAT_FAILED","调用微信接口失败"),
	
	INVALID_CONSUM("WECHAT_FAILED","卡券无法核销"),
	
	CONSUM_FAILED("CONSUM_FAILED","核销失败"),
	
	CONSUME_CODE_FAILE("00001","核销卡券失败"),
	
	QUARY_CODE_FAILE("00002","查询code状态失败");
    /**
     * 根据编码找枚举
     * 
     * @param code 编码
     * @return
     */
    public static ExceptionCodeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }

        for (ExceptionCodeEnum t : values()) {
            if (t.getCode().equals(code)) {
                return t;
            }
        }
        return null;
    }

    private final String code;

    private final String label;

    private ExceptionCodeEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public ExceptionCodeEnum returnEnum(String persistedValue) {
        return getByCode(persistedValue);
    }

}
