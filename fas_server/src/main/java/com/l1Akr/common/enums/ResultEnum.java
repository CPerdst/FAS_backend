package com.l1Akr.common.enums;

import lombok.Getter;

/**
 * 参数错误：10001-19999
 * 用户错误：20001-29999
 * 系统错误：40001-49999
 * 数据错误：50001-59999
 * 权限错误：70001-79999
 */
@Getter
public enum ResultEnum {
    /* 成功状态码 */
    SUCCESS(200, "操作成功！"),

    /* 错误状态码 */
    FAIL(400, "操作失败！"),

    /* 内部错误状态码 */
    ERROR(500, "服务器发生未知错误"),

    /* 参数错误：10001-19999 */
    PARAM_IS_INVALID(400, "参数无效"),
    PARAM_IS_BLANK(400, "参数为空"),
    PARAM_TYPE_BIND_ERROR(400, "参数格式错误"),
    PARAM_NOT_COMPLETE(400, "参数缺失"),

    /* 用户错误：20001-29999*/
    USER_NOT_LOGGED_IN(400, "用户未登录，请先登录"),
    USER_LOGIN_ERROR(400, "账号不存在或密码错误"),
    USER_ACCOUNT_FORBIDDEN(400, "账号已被禁用"),
    USER_NOT_EXIST(400, "用户不存在"),
    USER_HAS_EXISTED(400, "用户已存在"),

    /* 系统错误：40001-49999 */
    FILE_MAX_SIZE_OVERFLOW(400, "上传尺寸过大"),
    FILE_ACCEPT_NOT_SUPPORT(400, "上传文件格式不支持"),
    FILE_UPLOAD_FAILED(400, "上传文件失败"),

    /* 数据错误：50001-599999 */
    RESULT_DATA_NONE(400, "数据未找到"),
    DATA_IS_WRONG(400, "数据有误"),
    DATA_ALREADY_EXISTED(400, "数据已存在"),
    AUTH_CODE_ERROR(400, "验证码错误"),
    DATA_PASSWORD_ERROR(400, "两次输入密码一样"),


    /* 权限错误：70001-79999 */
    PERMISSION_UNAUTHENTICATED(400, "此操作需要登陆系统！"),

    PERMISSION_UNAUTHORIZED(400, "权限不足，无权操作！"),

    PERMISSION_EXPIRE(400, "登录状态过期！"),

    PERMISSION_TOKEN_EXPIRED(400, "token已过期"),

    PERMISSION_LIMIT(400, "访问次数受限制"),

    PERMISSION_TOKEN_INVALID(400, "无效token"),

    PERMISSION_SIGNATURE_ERROR(400, "签名失败");

    // 状态码
    final int code;
    // 提示信息
    final String message;

    ResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

}
