package com.bloomberg.bask.kafka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;

public class TwitterStatusEncoder implements Encoder<Status> {

    private static final Logger logger = LoggerFactory.getLogger (TwitterStatusEncoder.class);

    public TwitterStatusEncoder (VerifiableProperties vp) {
    }
    
    @Override
    public byte[] toBytes (Status status) {
        try {
            ByteArrayOutputStream bytesOutStream = new ByteArrayOutputStream ();
            ObjectOutputStream objectOutStream = new ObjectOutputStream (bytesOutStream);
            objectOutStream.writeObject (status);
            return bytesOutStream.toByteArray ();
        } catch (IOException ie) {
            logger.error (ie.getMessage (), ie);
        }
        return new byte[0];
    }
}
