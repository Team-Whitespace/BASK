package com.bloomberg.bask.feed;

import com.bloomberg.bask.system.SystemProducer;
import com.bloomberg.bask.util.PropertiesHelper;

import java.util.Properties;

public class TwitterFeedFactory implements FeedFactory {

    public Feed getFeed(Properties props) {
        SystemProducer producer = new SystemProducer(PropertiesHelper.extractConfig("kafka.producer.", props));
        return new TwitterFeed(props, producer);
    }
}
