package com.bloomberg.bask.kafka;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Barebones Kafka Producer
 *
 * @author Kiran Hampal
 */
public class BASKProducer<K, V> {

    private static final Logger logger = LoggerFactory.getLogger (BASKProducer.class);

    private String topic;
    private Producer<K, V> producer;

    public BASKProducer (String topic) {
        this.topic = topic;
    }

    public void sendMessage (V data) throws UnconfiguredProducerException {
        logger.debug ("Sending message to {}", topic);
        if (producer != null) {
            producer.send (new KeyedMessage<K, V> (topic, data));
        } else {
            throw new UnconfiguredProducerException ("Producer is not configured");
        }
    }

    public String getTopic () {
        return topic;
    }

    public void shutdown () {
        logger.debug ("Shutting down producer");
        if (producer != null) {
            producer.close ();
        }
    }

    public void loadConfig (Properties props) {
        loadConfig (new ProducerConfig (props));
    }

    public void loadConfig (ProducerConfig conf) {
        producer = new Producer<K, V> (conf);
    }

    public void loadConfig (String filename) throws IOException {
        Properties props = new Properties ();
        InputStream is = getClass ().getResourceAsStream (filename);
        if (is != null) {
            props.load (is);
            loadConfig (props);
        } else {
            throw new IOException ("Could not read " + filename);
        }
    }
}
