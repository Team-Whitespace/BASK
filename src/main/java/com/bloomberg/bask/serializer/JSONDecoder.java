package com.bloomberg.bask.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import kafka.serializer.Decoder;
import kafka.utils.VerifiableProperties;

public class JSONDecoder implements Decoder<Object> {

    private ObjectMapper objectMapper = new ObjectMapper();

    public JSONDecoder(VerifiableProperties vp) {
    }

    @Override
    public Object fromBytes(byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, Object.class);
        } catch (IOException e) {
            return null;
        }
    }
}
