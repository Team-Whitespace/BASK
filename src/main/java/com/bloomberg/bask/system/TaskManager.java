package com.bloomberg.bask.system;

import com.bloomberg.bask.task.StreamTask;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.Decoder;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskManager {

    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    private final SystemProducer producer;
    private final Properties config;
    private ConsumerConnector consumer;
    private ExecutorService executor;

    public TaskManager(Properties config, SystemProducer producer) {
        this.producer = producer;
        this.config = config;
    }

    public void run(final StreamTask task, List<String> inputStreams) {
        Map<String, Integer> topicThreads = new HashMap<String, Integer>();
        for (String topic : inputStreams) topicThreads.put(topic, 1);

        String messageDecoderClass = config.getProperty("kafka.consumer.deserializer.class");
        Decoder keyDecoder = new StringDecoder(new VerifiableProperties());
        Decoder messageDecoder = null;
        if (messageDecoderClass != null) {
            try {
                messageDecoder = (Decoder)Class.forName(messageDecoderClass).newInstance();
            } catch (ClassNotFoundException|InstantiationException|IllegalAccessException e) {
                logger.error("Could not load deserializer class", e);
            }
        }
        if (messageDecoder == null) {
            messageDecoder = keyDecoder;
        }

        consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(config));
        Map<String, List<KafkaStream<String, Object>>> consumerMap = consumer.createMessageStreams(
                topicThreads, keyDecoder, messageDecoder);

        executor = Executors.newFixedThreadPool(totalThreads(topicThreads));

        for (Map.Entry<String, List<KafkaStream<String, Object>>> streamEntry : consumerMap.entrySet()) {
            for (final KafkaStream stream : streamEntry.getValue()) {
                executor.submit(new TaskThread(task, producer, stream));
            }
        }
    }

    public void shutdown() {
        logger.debug("Shutting down threads");
        if (consumer != null) consumer.shutdown();
        if (executor != null) executor.shutdown();
        logger.debug("Threads shut down");
    }

    private Integer totalThreads(Map<?, Integer> mapThreads) {
        Integer total = 0;
        for(Integer i : mapThreads.values()) {
            total += i;
        }
        return total;
    }
}
