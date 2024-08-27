package com.decade.doj.common.exception;

public class BizIllegalException extends CommonException{

    public BizIllegalException(String message) {
        super(message, 500);
    }

    public BizIllegalException(String message, Throwable cause) {
        super(message, cause, 500);
    }

    public BizIllegalException(Throwable cause) {
        super(cause, 500);
    }
}
