package com.vector.comet.constants;

/**
 * Created by jinku on 2018/4/1.
 */
public enum ErrorCode {

    SUCCESS(0, "操作成功"),
    FAILED(1, "操作失败"),
    BAD_REQUEST(2, "请求非法");

    private int code;
    private String desc;

    ErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
