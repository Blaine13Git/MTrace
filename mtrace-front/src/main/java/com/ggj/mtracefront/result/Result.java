package com.ggj.mtracefront.result;

import lombok.Data;

@Data
public class Result {
    private int code;
    private String message;
    private Object data; //响应结果对象

    Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
