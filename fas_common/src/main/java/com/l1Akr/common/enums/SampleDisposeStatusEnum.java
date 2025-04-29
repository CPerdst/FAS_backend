package com.l1Akr.common.enums;

import lombok.Getter;

@Getter
public enum SampleDisposeStatusEnum {
    /**
     * 未处理
     */
    UNDISPOSED(1, "未处理"),

    /**
     * 正在处理
     */
    DISPOSING(2, "正在处理"),

    /**
     * 未发现病毒
     */
    NO_VIRUS(3, "未发现病毒"),

    /**
     * 发现病毒
     */
    FOUND_VIRUS(4, "发现病毒"),

    /**
     * 处理失败
     */
    DISPOSE_FAILED(5, "处理失败"),

    /**
     * 未知状态
     */
    UNKNOWN(6, "未知状态");

    final Integer code;
    final String Stauts;

    SampleDisposeStatusEnum(Integer code, String stauts) {
        this.code = code;
        Stauts = stauts;
    }

    public static SampleDisposeStatusEnum getByCode(Integer code) {
        for (SampleDisposeStatusEnum status : SampleDisposeStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static SampleDisposeStatusEnum getByStauts(String stauts) {
        for (SampleDisposeStatusEnum status : SampleDisposeStatusEnum.values()) {
            if (status.getStauts().equals(stauts)) {
                return status;
            }
        }
        return null;
    }
}
