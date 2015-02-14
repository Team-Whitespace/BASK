package com.bloomberg.bask.subscription.kafka.consumer;

import com.bloomberg.bask.subscription.Subscription;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.Decoder;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Properties;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs the Solr tweet consumer threads
 * @author Radu Ban
 */

public class SolrTweetConsumerManager implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(SolrTweetConsumerManager.class);
    private ExecutorService executor;
    private ConsumerConnector consumer;
    private String zkConnect, groupID;

    public static void main(String[] args)
    {
    	String zkConnect = "192.168.50.4:2181";
    	String groupID = "test-id";
    	SolrTweetConsumerManager app = new SolrTweetConsumerManager(zkConnect, groupID);
        app.run();
    }
    
    public SolrTweetConsumerManager(String zkConnect, String groupID)
    {
        this.zkConnect = zkConnect;
        this.groupID = groupID;
       	logger.info("zkConnect: {}",zkConnect);
       	logger.info("groupID: {}",groupID);
       	
    }

    /**
     * Runs all the Consumer threads
     */
    public void run()
    {
        Decoder decoder = new StringDecoder(new VerifiableProperties());
        Map<String, Integer> topicThreads = new HashMap<String, Integer>();
        topicThreads.put("tweets", 1);
        //topicThreads.put("documents", 1);
        consumer = Consumer.createJavaConsumerConnector(config());
        Map<String, List<KafkaStream<String, String>>> consumerMap
            = consumer.createMessageStreams(topicThreads, decoder, decoder);
        executor = Executors.newFixedThreadPool(totalThreads(topicThreads));
        runConsumer(consumerMap.get("tweets"), executor);
    }

    /**
     * Shuts down all the consumer threads
     */
    public void shutdown()
    {
        logger.info("Shutting down threads");
        if (consumer != null) consumer.shutdown();
        if (executor != null) executor.shutdown();
        logger.info("Threads shut down");
    }

    private ConsumerConfig config()
    {
        Properties props = new Properties();
        props.put("zookeeper.connect", zkConnect);
        props.put("group.id", groupID);
        return new ConsumerConfig(props);
    }

    private Integer totalThreads (Map<?, Integer> mapThreads)
    {
        Integer total = 0;
        for (Integer i : mapThreads.values())
        {
            total += i;
        }
        return total;
    }

    private void runConsumer
    (
        List<KafkaStream<String, String>> streams,
        ExecutorService executor
    )
    {
        for (final KafkaStream stream : streams)
            executor.submit(new SolrTweetConsumer(stream, "tweets"));
    }
}
