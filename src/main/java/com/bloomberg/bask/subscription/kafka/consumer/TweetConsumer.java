package com.bloomberg.bask.subscription.kafka.consumer;

import com.bloomberg.bask.subscription.Subscription;

import java.io.IOException;

import kafka.consumer.KafkaStream;

import org.json.JSONObject;
import org.json.JSONTokener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consumes tweets, matches against alerts using a subscription and then sends
 * them to another Kafka server
 *
 * @author Kiran Hampal
 * @author Jack Markham
 */
public class TweetConsumer extends SubscriptionConsumer {

    private final Logger logger = LoggerFactory.getLogger (TweetConsumer.class);

    public TweetConsumer (KafkaStream stream, Subscription subscription) {
        super (stream, subscription);
    }

    protected void processMessage (String message) {
        JSONObject tweet = new JSONObject (new JSONTokener (message));
        try {
            subscription.sendTweet (tweet);
        } catch (IOException ie) {
            logger.error ("Failed to send tweet to subscription", ie);
        }
    }
}
