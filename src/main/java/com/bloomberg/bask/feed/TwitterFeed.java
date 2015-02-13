package com.bloomberg.bask.feed;

import com.bloomberg.bask.system.Envelope;
import com.bloomberg.bask.system.SystemProducer;

import java.util.Properties;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitterFeed implements Feed {

    private static final Logger logger = LoggerFactory.getLogger(TwitterFeed.class);

    private final String TWEET_STREAM = "tweets";
    private final SystemProducer producer;
    private BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    private BasicClient client;
    private Properties config;

    public TwitterFeed(Properties props, SystemProducer producer) {
        this.config = props;
        this.producer = producer;
    }

    @Override
    public void start() {
        OAuth1 auth = new OAuth1(
            config.getProperty("oauth.consumer.key"),
            config.getProperty("oauth.consumer.secret"),
            config.getProperty("oauth.token"),
            config.getProperty("oauth.secret"));

        System.out.println(config.getProperty("oauth.consumer.key"));

        client = new ClientBuilder()
                .name("bask-twitter-feed")
                .hosts(Constants.STREAM_HOST)
                .endpoint(new StatusesSampleEndpoint())
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        client.connect();
        pollTweets();
    }

    @Override
    public void stop() {
        client.stop();
    }

    private void pollTweets () {
        while (!client.isDone()) {
            try {
                String status = queue.poll(1, TimeUnit.SECONDS);
                if (status != null) {
                    producer.send(new Envelope(status, TWEET_STREAM));
                } else {
                    logger.warn("Did not receive a message from twitter in 1s");
                }
            } catch (InterruptedException ie) {
                stop();
            }
        }
    }
}
