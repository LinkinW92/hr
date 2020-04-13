package my.linkin.common;

/**
 * @author yonghong.ge
 * @date 2019/4/26 19:19
 */
public enum ResultCode {

    /**成功*/
    SUPER_SUCCESS("0","超级成功"),
    SUCCESS("0000", "成功"),
    SYSTEM_ERROR("1400", "系统繁忙"),
    BIZ_EXCEPTION("1401", "业务异常"),
    ILLEGAL_ARGUMENTS("1402", "非法参数"),
    DATA_NOT_FOUND("1403", "数据不存在"),
    QUANTITY_INSUFFICIENT("1404" ,"数量不足"),
    TOKEN_EXPIRE("1419","token过期"),
    TOKEN_AQUIRE_FAILED("1420","获取token失败,请稍后重试"),
    INACTIVE("1421", "当前活动未激活或已过期"),

    /**
     * openId已参加过
     */
    ACT_SUPPORT_JOINED_ALREADY_EXCEPTION("1461", "您已参加过该活动喔~"),
    ACT_SUPPORT_JOINED_INFO_NOT_FOUND("1462", "您还未参加过该活动喔~"),
    ACT_SUPPORT_MOBILE_NO_JOINED_ALREADY_EXCEPTION("1463", "该手机号已参加过该活动喔~"),;

    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
