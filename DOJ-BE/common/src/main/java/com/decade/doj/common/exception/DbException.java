package com.decade.doj.common.exception;

public class DbException extends CommonException{

    public DbException(String message) {
        super(message, 500);
    }

    public DbException(String message, Throwable cause) {
        super(message, cause, 500);
    }

    public DbException(Throwable cause) {
        super(cause, 500);
    }
}
