package com.bloomberg.bask.system;

public class Envelope {

    private final String _key;
    private final Object _message;
    private final String _stream;

    public Envelope(String key, Object message, String stream) {
        _key = key;
        _message = message;
        _stream = stream;
    }

    public Envelope(Object message, String stream) {
        this(null, message, stream);
    }

    public String getKey() {
        return _key;
    }

    public Object getMessage() {
        return _message;
    }

    public String getStream() {
        return _stream;
    }
}
