package com.bloomberg.bask.kafka;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import kafka.serializer.Decoder;
import kafka.utils.VerifiableProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;

public class TwitterStatusDecoder implements Decoder<Status> {

    private static final Logger logger = LoggerFactory.getLogger (TwitterStatusEncoder.class);

    public TwitterStatusDecoder (VerifiableProperties vp) {
    }
    
    @Override
    public Status fromBytes (byte[] bytes) {
        try {
            ByteArrayInputStream bytesInStream = new ByteArrayInputStream (bytes);
            ObjectInputStream objectInStream = new ObjectInputStream (bytesInStream);
            return (Status) objectInStream.readObject ();
        } catch (IOException|ClassNotFoundException e) {
            logger.error (e.getMessage (), e);
        }
        return null;
    }
}
