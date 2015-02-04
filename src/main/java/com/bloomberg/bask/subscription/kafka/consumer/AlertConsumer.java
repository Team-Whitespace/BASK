package com.bloomberg.bask.subscription.kafka.consumer;

import com.bloomberg.bask.subscription.Subscription;

import java.io.IOException;

import kafka.consumer.KafkaStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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
        JSONObject producerMessage;
        String keyword;
        JSONArray alerts = new JSONArray (new JSONTokener (message));
        for (int i = 0; i < alerts.length (); i++) {
            producerMessage = new JSONObject ();
            try {
                keyword = alerts.getJSONObject (i).getString ("alert");
                producerMessage.put ("keyword", keyword);
                subscription.addAlert (keyword, keyword);
                producerMessage.put ("status", 200);
            } catch (IOException e) {
                logger.error ("Failed to add alert to the subscription", e);
                producerMessage.put ("status", 500);
            } catch (JSONException e) {
                logger.error ("Failed to read JSON array", e);
                return;
            }
            producer.sendMessage (producerMessage);
        }
    }
}
