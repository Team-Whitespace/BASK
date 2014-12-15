package com.bloomberg.bask.stub;

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Properties;

import org.json.JSONObject;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriptionProducer {

    private final Logger logger = LoggerFactory.getLogger (SubscriptionProducer.class);

    private String topic, broker;

    private Producer<String,String> producer;

    public SubscriptionProducer (String broker, String topic) {
        this.broker = broker;
        this.topic = topic;
        producer = new Producer<String, String> (config ());
    }

    public void sendMessage (JSONObject data) {
        logger.info ("Sent message to {}: {}", topic, data.toString ());
        producer.send (new KeyedMessage<String, String> (topic, data.toString ()));
    }

    public void shutdown () {
        producer.close ();
    }

    private ProducerConfig config () {
        Properties props = new Properties ();
        props.put ("metadata.broker.list", broker);
        props.put ("serializer.class", "kafka.serializer.StringEncoder");
        props.put ("producer.type", "sync");
        props.put ("request.required.acks", "1");
        return new ProducerConfig (props);
    }
}
