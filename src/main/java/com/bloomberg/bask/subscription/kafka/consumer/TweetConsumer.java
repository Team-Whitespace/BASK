package com.bloomberg.bask.subscription.kafka.consumer;

import com.bloomberg.bask.subscription.Subscription;

import java.io.IOException;

import kafka.consumer.KafkaStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.flax.luwak.QueryMatch;
import uk.co.flax.luwak.matchers.SimpleMatcher;

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
        super (stream, subscription, "matches");
    }

    protected void processMessage (String message) {
        JSONObject tweet = new JSONObject (new JSONTokener (message));
        JSONArray keywords = new JSONArray ();
        JSONObject producerMessage = new JSONObject ()
            .put ("tweet", tweet)
            .put ("keywords", keywords);
        try {
            SimpleMatcher queries = subscription.sendTweet (tweet);
            for (QueryMatch query : queries) {
                keywords.put (query.getQueryId ());
            }
            producer.sendMessage (producerMessage);
        } catch (IOException ie) {
            logger.error ("Failed to send tweet to subscription", ie);
        }
    }
}
