package com.l1Akr.common.result;

import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Data
public class Result<T> {

    // 操作代码
    Integer code;

    // 提示信息
    String message;

    // 结果数据
    T data;

    public Result(@NotNull ResultEnum resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public Result(@NotNull ResultEnum resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public Result(String message) {
        this.message = message;
    }

    //成功返回封装-无数据
    @Contract(" -> new")
    public static @NotNull Result<String> success() {
        return new Result<String>(ResultEnum.SUCCESS);
    }

    //成功返回封装-带数据
    @Contract("_ -> new")
    public static <T> @NotNull Result<T> success(T data) {
        return new Result<T>(ResultEnum.SUCCESS, data);
    }

    //失败返回封装-使用默认提示信息
    @Contract(" -> new")
    public static @NotNull Result<String> error() {
        return new Result<String>(ResultEnum.FAILED);
    }

    //失败返回封装-使用自定义提示信息
    @Contract("_ -> new")
    public static @NotNull Result<String> error(String message) {
        return new Result<>(ResultEnum.FAILED, message);
    }

    /**
     * 参数错误：10001-19999
     * 用户错误：20001-29999
     * 系统错误：40001-49999
     * 数据错误：50001-59999
     * 权限错误：70001-79999
     */
    @Getter
    public enum ResultEnum {
        // 成功响应
        SUCCESS(0, "操作成功"),
        // 失败响应
        FAILED(1, "操作失败"),

        // 用户名或密码错误
        USER_PASSWORD_ERROR(20001, "用户名或密码错误"),

        // 上传尺寸过大
        UPLOAD_SIZE_LIMIT_EXCEEDED(40001, "上传尺寸过大"),

        // 用户名不能为空
        USER_NAME_EMPTY(20002, "用户名不能为空"),

        // 用户两次输入的密码一样
        USER_PASSWORD_SAME(20003, "两次输入的密码一样"),

        // 输入的密码不能为空
        USER_PASSWORD_EMPTY(20004, "输入的密码不能为空"),

        // 用户不存在
        USER_NOT_EXIST(20005, "用户不存在"),

        // 用户已存在
        USER_EXIST(20007, "用户已存在"),

        // 用户更新失败
        USER_UPDATE_FAILED(20006, "用户更新失败"),
        
        // 用户不可删除
        USER_NOT_DELETABLE(20009, "该用户不可删除"),
        
        // 用户删除失败
        USER_DELETE_FAILED(20010, "用户删除失败"),
        
        // 用户不可更新
        USER_NOT_UPDATABLE(20011, "该用户不可更新"),

        // 参数有问题
        PARAM_ERROR(10001, "参数错误"),

        // 文件上传失败
        UPLOAD_FAILED(40002, "文件上传失败"),

        // 仅支持JPG/PNG格式且不超过2MB
        UPLOAD_FORMAT_LIMIT_EXCEEDED(40003, "仅支持JPG/PNG格式且不超过2MB"),

        // 仅支持JPG/PNG格式且不超过50MB
        UPLOAD_FORMAT_LIMIT_EXCEEDED_50MB(40003, "仅支持JPG/PNG格式且不超过50MB"),

        // 文件MD5失败
        FILE_MD5_ERROR(40004, "文件MD5失败"),

        // 页码或页数错误
        PAGE_NUM_OR_SIZE_ERROR(10002, "页码或页数错误"),

        // 用户没有登录
        USER_NOT_LOGIN(20008, "用户没有登录"),

        // 用户无权限
        USER_NO_PERMISSION(70001, "用户无权限"),

        // 角色已存在
        ROLE_EXIST(50001, "角色已存在"),

        // 角色不存在
        ROLE_NOT_EXIST(50002, "角色不存在"),

        // 角色更新失败
        ROLE_UPDATE_FAILED(50003, "角色更新失败"),
        
        // 角色不可删除
        ROLE_NOT_DELETABLE(50004, "该角色不可删除"),
        
        // 角色删除失败
        ROLE_DELETE_FAILED(50005, "角色删除失败"),
        
        // 角色不可更新
        ROLE_NOT_UPDATABLE(50006, "该角色不可更新"),
        
        // 权限已存在
        PERMISSION_EXIST(70002, "权限已存在"),
        
        // 权限不存在
        PERMISSION_NOT_EXIST(70003, "权限不存在"),
        
        // 权限添加失败
        PERMISSION_ADD_FAILED(70004, "权限添加失败"),
        
        // 权限更新失败
        PERMISSION_UPDATE_FAILED(70005, "权限更新失败"),
        
        // 权限不可删除
        PERMISSION_NOT_DELETABLE(70006, "该权限不可删除"),
        
        // 权限删除失败
        PERMISSION_DELETE_FAILED(70007, "权限删除失败"),
        
        // 权限不可更新
        PERMISSION_NOT_UPDATABLE(70008, "该权限不可更新");

        final int code;
        final String message;

        ResultEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
