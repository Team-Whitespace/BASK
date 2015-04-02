package com.bloomberg.bask.system;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemProducer {

    private static final Logger logger = LoggerFactory.getLogger(SystemProducer.class);

    private KafkaProducer<String, Object> producer;

    public SystemProducer(Properties props) {
        this.producer = new KafkaProducer<String, Object>(props);
    }

    public SystemProducer(KafkaProducer producer) {
        this.producer = producer;
    }

    public void send(Envelope envelope) {
        producer.send(new ProducerRecord<String, Object>(envelope.getStream(),
                envelope.getKey(), envelope.getMessage()));
    }

    public void shutdown() {
        logger.debug ("Shutting down producer");
        if (producer != null) {
            producer.close ();
        }
    }
}
