package com.bloomberg.bask.stub;

import org.json.JSONTokener;
import org.json.JSONArray;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class reads a JSON array from a file and sends each object in the array
 * to a Kafka queue, with an optional delay.
 *
 * @author Kiran Hampal
 * @author Jack Markham
 */
public class JSONProducer {

    private String source;
    private String topic, broker;
    private Integer delay;

    private final Logger logger = LoggerFactory.getLogger (JSONProducer.class);

    public static void main (String[] args) {
        OptionParser parser = new OptionParser ();
        parser.accepts (
            "broker",
            "The kafka broker in the form of Host:IP"
        ).withRequiredArg ().ofType (String.class).required ();
        parser.accepts (
            "topic",
            "The topic to send messages to"
        ).withRequiredArg ().ofType (String.class).required ();
        parser.accepts (
            "source",
            "The JSON source file"
        ).withRequiredArg ().ofType (String.class).required ();
        parser.accepts (
            "delay",
            "The time delay between each tweet in ms"
        ).withRequiredArg ().ofType (Integer.class).required ();
        OptionSet options = parser.parse (args);

        JSONProducer producer = new JSONProducer (
            (String) options.valueOf ("broker"),
            (String) options.valueOf ("topic"),
            (String) options.valueOf ("source"),
            (Integer) options.valueOf ("delay")
        );
        producer.run ();
    }

    public JSONProducer (String broker, String topic, String source, Integer delay) {
        this.broker = broker;
        this.topic = topic;
        this.source = source;
        this.delay = delay;
    }

    public JSONProducer (String broker, String topic, String source) {
        this (broker, topic, source, 0);
    }

    private ProducerConfig config () {
        Properties props = new Properties ();
        props.put ("metadata.broker.list", broker);
        props.put ("serializer.class", "kafka.serializer.StringEncoder");
        props.put ("producer.type", "sync");
        props.put ("request.required.acks", "1");
        return new ProducerConfig (props);
    }

    /**
     * Sends each JSON object in an array to Kafka, with a specified delay
     */
    private void sendJSON (JSONArray jsonArray) throws Exception {
        Producer<String, String> producer = new Producer<String, String> (config ());
        for (int i = 0; i < jsonArray.length (); i++) {
            logger.debug ("Sending message {}", i);
            String msg = jsonArray.get (i).toString ();
            KeyedMessage<String, String> data = new KeyedMessage<String, String> (topic, msg);
            producer.send (data);
            if (delay > 0)
                TimeUnit.MILLISECONDS.sleep (delay);
        }
        producer.close ();
    }

    public void run () {
        try {
            InputStream inputStream = new FileInputStream (source);
            sendJSON (new JSONArray (new JSONTokener (inputStream)));
        } catch (Exception e) {
            logger.error ("Unable to send message to Kafka", e);
        }
    }
}
