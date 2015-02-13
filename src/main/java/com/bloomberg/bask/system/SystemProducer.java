package com.bloomberg.bask.system;

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
public class SystemProducer {

    private static final Logger logger = LoggerFactory.getLogger(SystemProducer.class);

    private Producer<String, Object> producer;

    public SystemProducer() {}

    public SystemProducer(Properties props) {
        producer = new Producer<String, Object>(new ProducerConfig(props));
    }

    public void send(Envelope envelope) {
        producer.send(new KeyedMessage<String, Object>(envelope.getStream(),
                envelope.getKey(), envelope.getMessage()));
    }

    public void shutdown() {
        logger.debug ("Shutting down producer");
        if (producer != null) {
            producer.close ();
        }
    }
}
