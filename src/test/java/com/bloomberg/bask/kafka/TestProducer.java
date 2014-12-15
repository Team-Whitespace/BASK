package com.bloomberg.bask.kafka;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * @author Kiran Hampal
 */
public class TestProducer {

    private String topic, broker;

    private Producer<String,String> producer;

    public TestProducer (String broker, String topic) {
        this.broker = broker;
        this.topic = topic;
        producer = new Producer<String, String> (config ());
    }
    
    public void sendMesage (String message) throws Exception {
        producer.send (new KeyedMessage<String, String> (topic, message));
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
