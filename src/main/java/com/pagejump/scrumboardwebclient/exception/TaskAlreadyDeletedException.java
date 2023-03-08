package com.pagejump.scrumboardwebclient.exception;

public class TaskAlreadyDeletedException extends RuntimeException{

    public TaskAlreadyDeletedException() {
        super();
    }

    public TaskAlreadyDeletedException(String message) {
        super(message);
    }

    public TaskAlreadyDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskAlreadyDeletedException(Throwable cause) {
        super(cause);
    }

    protected TaskAlreadyDeletedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
