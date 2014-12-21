package com.bloomberg.bask.subscription.kafka.producer;

import com.bloomberg.bask.kafka.BASKProducer;
import com.bloomberg.bask.kafka.DefaultProducerConfig;
import com.bloomberg.bask.kafka.UnconfiguredProducerException;

import java.io.IOException;

import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriptionProducer extends BASKProducer<String, String> {

    private static final Logger logger = LoggerFactory.getLogger (SubscriptionProducer.class);

    public SubscriptionProducer (String topic) {
        super (topic);
        try {
            loadConfig ("/subscription.producer.properties");
        } catch (IOException ie) {
            logger.warn ("Could not read subscription.producer.properties. Using default config...");
            loadConfig (new DefaultProducerConfig ());
        }
    }

    public void sendMessage (JSONObject data) {
        try {
            sendMessage (data.toString ());
        } catch (UnconfiguredProducerException upe) {
            logger.error ("Could not send message {}", data.toString (), upe);
        }
    }
}
