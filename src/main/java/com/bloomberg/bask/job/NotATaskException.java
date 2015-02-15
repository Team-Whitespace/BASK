package com.bloomberg.bask.job;

public class NotATaskException extends RuntimeException {

    public NotATaskException(String message) {
        super(message);
    }
}
