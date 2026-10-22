package com.labinspection.common;

/**
 * 业务异常
 */
public class BizException extends RuntimeException {
    public BizException(String msg) {
        super(msg);
    }
}
