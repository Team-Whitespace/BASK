package com.bloomberg.bask.kafka;

public class UnconfiguredProducerException extends Exception {

    public UnconfiguredProducerException () {
    }

    public UnconfiguredProducerException (String message) {
        super (message);
    }

    public UnconfiguredProducerException (Throwable cause) {
        super (cause);
    }

    public UnconfiguredProducerException (String message, Throwable cause) {
        super (message, cause);
    }
}
