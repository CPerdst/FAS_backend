package com.l1Akr.common.result;

import com.l1Akr.common.enums.ResultEnum;
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

        // 参数错误
        FILE_MAX_SIZE_OVERFLOW(400, "上传尺寸过大");

        final int code;
        final String message;

        ResultEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
