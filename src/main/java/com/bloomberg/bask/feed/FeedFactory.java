package com.bloomberg.bask.feed;

import java.util.Properties;

public interface FeedFactory {

    public Feed getFeed(Properties props);
}
