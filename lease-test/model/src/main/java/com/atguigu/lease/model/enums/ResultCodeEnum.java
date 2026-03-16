package com.atguigu.lease.model.enums;


public enum ResultCodeEnum {
    // ========== 基础状态码 ==========
    /** 操作成功 */
    SUCCESS(200, "成功"),
    /** 请求失败 */
    FAIL(201, "失败"),
    /** 参数错误 */
    PARAM_ERROR(202, "参数不正确"),
    /** 服务异常 */
    SERVICE_ERROR(203, "服务异常"),
    /** 数据异常 */
    DATA_ERROR(204, "数据异常"),
    /** 数据版本异常（乐观锁） */
    DATA_UPDATE_ERROR(205, "数据版本异常，请刷新后重试"),
    /** 未登录 */
    LOGIN_AUTH(208, "未登录"),
    /** 无权限 */
    PERMISSION(209, "没有权限"),

    // ========== 用户相关错误码 ==========
    /** 验证码错误 */
    CODE_ERROR(210, "验证码错误"),
    /** 账号不正确 */
    LOGIN_MOBILE_ERROR(211, "账号不正确"),
    /** 用户已被禁用 */
    LOGIN_DISABLED_ERROR(212, "该用户已被禁用"),
    /** 手机号已被使用 */
    REGISTER_MOBILE_ERROR(213, "手机号已被使用"),
    /** 需要登录 */
    LOGIN_AUTH_AGAIN(214, "需要登录"),
    /** 没有权限 */
    LOGIN_ACL(215, "没有权限"),

    APP_SEND_SMS_ERROR(28008, "应用发送短信失败"),

    // ========== 系统级错误码 ==========
    /** URL编码失败 */
    URL_ENCODE_ERROR(216, "URL编码失败"),
    /** 非法回调请求 */
    ILLEGAL_CALLBACK_REQUEST_ERROR(217, "非法回调请求"),
    /** 获取AccessToken失败 */
    FETCH_ACCESSTOKEN_FAILED(218, "获取accessToken失败"),
    /** 获取用户信息失败 */
    FETCH_USERINFO_ERROR(219, "获取用户信息失败"),
    /** 支付中 */
    PAY_RUN(220, "支付中"),
    /** 取消订单失败 */
    CANCEL_ORDER_FAIL(225, "取消订单失败"),
    /** 不能取消预约 */
    CANCEL_ORDER_NO(226, "不能取消预约"),
    /** 医院编号已存在 */
    HOSCODE_EXIST(230, "医院编号已经存在"),
    /** 可预约号不足 */
    NUMBER_NO(240, "可预约号不足"),
    /** 当前时间不可以预约 */
    TIME_NO(250, "当前时间不可以预约"),
    /** 签名错误 */
    SIGN_ERROR(300, "签名错误"),
    /** 医院未开通 */
    HOSPITAL_OPEN(310, "医院未开通，暂时不能访问"),
    /** 医院被锁定 */
    HOSPITAL_LOCK(320, "医院被锁定，暂时不能访问"),

    // ========== 阿里云短信服务特定错误码 ==========
    /** 短信签名无效或未审核 */
    SMS_SIGN_INVALID(5006, "短信签名无效或未审核"),
    /** 短信模板无效或未审核 */
    SMS_TEMPLATE_INVALID(5007, "短信模板无效或未审核"),
    /** 短信发送过于频繁，触发流控 */
    SMS_BUSINESS_LIMIT_CONTROL(5008, "短信发送过于频繁，请稍后再试"),
    /** 手机号码格式错误 */
    SMS_PHONE_NUMBER_ILLEGAL(5009, "手机号码格式错误"),
    /** 短信服务内部错误 */
    SMS_SYSTEM_ERROR(5010, "短信服务内部错误"),

    // ========== 其他业务错误码 ==========
    /** 未知异常 */
    UNKNOWN_ERROR(0, "未知异常"),
    /** 数据库异常 */
    DB_ERROR(-1, "数据库异常");

    /** 响应状态码 */
    private final Integer code;
    /** 响应消息 */
    private final String message;

    /**
     * 构造方法
     * @param code 状态码
     * @param message 状态描述
     */
    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}