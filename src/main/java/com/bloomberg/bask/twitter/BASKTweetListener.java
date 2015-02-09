package com.bloomberg.bask.twitter;

import com.bloomberg.bask.kafka.BASKProducer;
import com.bloomberg.bask.kafka.UnconfiguredProducerException;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterObjectFactory;

public class BASKTweetListener implements StatusListener {

    private final static Logger logger = LoggerFactory.getLogger (BASKTweetListener.class);

    private BASKProducer<String, String> producer;

    public BASKTweetListener () throws IOException {
        producer = new BASKProducer ("documents");
        producer.loadConfig ("/tweet.producer.properties");
    }

    public void onDeletionNotice (StatusDeletionNotice statusDeletionNotice) {
    }

    public void onScrubGeo (long userId, long upToStatusId) {
    }

    public void onStallWarning (StallWarning warning) {
    }

    public void onTrackLimitationNotice (int numberOfLimitedStatus) {
    }

    public void onStatus (Status status) {
        String statusJSON = TwitterObjectFactory.getRawJSON (status);
        logger.debug (statusJSON);
        try {
            producer.sendMessage (statusJSON);
        } catch (UnconfiguredProducerException ie) {
            throw new RuntimeException ();
        }
    }

    public void onException (Exception e) {
    }
}
