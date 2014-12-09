package com.bloomberg.bask.stub;

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Properties;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This simple class reads alerts from a file and then sends them on
 * to a Kafka queue. It is meant to stand in place for Solr which
 * would pass on search terms, and then send them to a Kafka queue.
 *
 * @author Kiran Hampal
 * @author Jack Markham
 */
public class AlertProducer {

    private static final String source = "src/test/resources/sample_queries";
    private String topic, broker;

    private final Logger logger = LoggerFactory.getLogger (AlertProducer.class);

    public static void main (String[] args) {
        OptionParser parser = new OptionParser ();
        parser.accepts (
            "broker",
            "The Kafka broker in the form of Host:IP"
        ).withRequiredArg ().ofType (String.class).required ();
        parser.accepts (
            "topic",
            "The topic to send messages to"
        ).withRequiredArg ().ofType (String.class).required ();
        OptionSet options = parser.parse (args);

        AlertProducer producer = new AlertProducer (
            (String) options.valueOf ("broker"),
            (String) options.valueOf ("topic")
        );
        producer.run ();
    }

    public AlertProducer (String broker, String topic) {
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
     * Sends each line from the source to Kafka
     */
    private void sendAlerts (String fileName) throws Exception {
        FileReader fileReader = new FileReader (fileName);
        BufferedReader bufferedReader = new BufferedReader (fileReader);
        Producer<String, String> producer = new Producer<String, String> (config ());
        KeyedMessage<String, String> data;
        String msg = null;
        while ((msg = bufferedReader.readLine ()) != null) {
            logger.info ("Keyword read {}", msg);
            data = new KeyedMessage<String, String> (topic, msg);
            producer.send (data);
        }
        producer.close ();
    }

    public void run () {
        try {
            sendAlerts (source);
        } catch (Exception e) {
            logger.error ("Unable to send message to Kafka", e);
        }
    }
}