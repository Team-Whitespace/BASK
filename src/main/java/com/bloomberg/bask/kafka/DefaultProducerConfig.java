package com.bloomberg.bask.kafka;

import java.util.Properties;

import kafka.producer.ProducerConfig;

public class DefaultProducerConfig extends ProducerConfig {

    public DefaultProducerConfig () {
        super (defaultConfig ());
    }

    private static Properties defaultConfig () {
        Properties props = new Properties ();
        props.setProperty ("metadata.broker.list", "127.0.0.1:9092");
        props.setProperty ("producer.type", "sync");
        props.setProperty ("request.required.acks", "1");
        props.setProperty ("serializer.class", "kafka.serializer.StringEncoder");
        return props;
    }
}
