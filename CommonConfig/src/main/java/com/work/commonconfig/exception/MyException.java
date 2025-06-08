package com.work.commonconfig.exception;

import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;

public class MyException extends RuntimeException {
    private int code; // 错误码

    private static final Logger logger = LoggerFactory.getLogger(MyException.class);

    public MyException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    // 统一抛出带日志的异常
    public static void throwError(String errorMessage, int statusCode) {
        // 打印错误日志
        logger.error(() -> errorMessage);
        // 抛出自定义异常
        throw new MyException(statusCode, errorMessage);
    }
}
