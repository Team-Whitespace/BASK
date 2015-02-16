package com.bloomberg.bask.system;

public class Envelope {

    private final String _key;
    private final String _message;
    private final String _stream;

    public Envelope(String key, String message, String stream) {
        _key = key;
        _message = message;
        _stream = stream;
    }

    public Envelope(String message, String stream) {
        this(null, message, stream);
    }

    public String getKey() {
        return _key;
    }

    public String getMessage() {
        return _message;
    }

    public String getStream() {
        return _stream;
    }
}
