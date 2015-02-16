package com.bloomberg.bask.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;

import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;

public class JSONEncoder implements Encoder<Object> {

    private ObjectMapper objectMapper = new ObjectMapper();

    public JSONEncoder(VerifiableProperties vp) {
    }

    @Override
    public byte[] toBytes(Object object) {
        try {
            return objectMapper.writeValueAsString(object).getBytes("UTF-8");
        } catch (UnsupportedEncodingException|JsonProcessingException e) {
            return new byte[0];
        }
    }
}
