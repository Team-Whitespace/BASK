package com.bloomberg.bask.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kafka.serializer.Decoder;
import kafka.utils.VerifiableProperties;

public class JSONString {

    private ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> fromString(String string) {
        try {
            return objectMapper.readValue(string, new TypeReference<HashMap<String,Object>>() {});
        } catch (IOException e) {
            return null;
        }
    }
    
    public String toString(Map<String, Object> object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }  
}
