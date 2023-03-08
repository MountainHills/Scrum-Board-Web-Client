package com.pagejump.scrumboardwebclient.exception;

public class InvalidTaskRequestException extends RuntimeException{

    public InvalidTaskRequestException() {
        super();
    }

    public InvalidTaskRequestException(String message) {
        super(message);
    }

    public InvalidTaskRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTaskRequestException(Throwable cause) {
        super(cause);
    }

    protected InvalidTaskRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
