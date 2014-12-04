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
 * This simple class is reads tweets from a file and then sends them on
 * to a Kafka queue. It is meant to stand in place for Solr which
 * would read tweets from Twitter's streaming API, store them, and then
 * send them to a Kafka queue.
 *
 * @author Kiran Hampal
 * @author Jack Markham
 */
public class TweetProducer {

    private static final String source = "src/test/resources/sample_tweets.json";
    private String topic, broker;

    private final Logger logger = LoggerFactory.getLogger (TweetProducer.class);

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
        OptionSet options = parser.parse (args);

        TweetProducer producer = new TweetProducer (
            (String) options.valueOf ("broker"),
            (String) options.valueOf ("topic")
        );
        producer.run ();
    }

    public TweetProducer (String broker, String topic) {
        this.broker = broker;
        this.topic = topic;
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
     * Sends each JSON object in an array to Kafka, with a delay of 1-6 seconds
     */
    private void sendJSON (JSONArray jsonArray) throws Exception {
        Producer<String, String> producer = new Producer<String, String> (config ());
        for (int i = 0; i < jsonArray.length (); i++) {
            logger.debug ("Sending message {}", i);
            String msg = jsonArray.get (i).toString ();
            KeyedMessage<String, String> data = new KeyedMessage<String, String> (topic, msg);
            producer.send (data);
            TimeUnit.SECONDS.sleep (randomTime (6));
        }
        producer.close ();
    }

    /**
     * Returns a random number between max and 1
     */
    private int randomTime (int max) {
        Random random = new Random ();
        return random.nextInt (max) + 1;
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
