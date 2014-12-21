package com.bloomberg.bask.subscription.kafka.consumer;

import com.bloomberg.bask.subscription.Subscription;

import java.io.IOException;

import kafka.consumer.KafkaStream;

import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consumes alerts, adds them to a subscription and then sends them a results
 * producer
 *
 * @author Kiran Hampal
 * @author Jack Markham
 * @author William Blakey
 * @author Thomas Michiels
 */
public class AlertConsumer extends SubscriptionConsumer {

    private final Logger logger = LoggerFactory.getLogger (AlertConsumer.class);

    public AlertConsumer (KafkaStream stream, Subscription subscription) {
        super (stream, subscription, "results");
    }

    protected void processMessage (String message) {
        JSONObject producerMessage = new JSONObject ().put ("keyword", message);
        try {
            subscription.addAlert (message, message);
            producerMessage.put ("status", 200);
        } catch (IOException ie) {
            logger.error ("Failed to add alert to the subscription");
            producerMessage.put ("status", 500);
        }
        producer.sendMessage (producerMessage);
    }
}
