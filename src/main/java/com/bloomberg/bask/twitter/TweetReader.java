package com.bloomberg.bask.twitter;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TweetReader implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger (TweetReader.class);

    private TwitterStream twitterStream;

    public static void main (String[] args) {
        TweetReader app = new TweetReader ();
        app.run ();
    }

    public void run () {
        twitterStream = new TwitterStreamFactory (config ()).getInstance ();
        try {
            BASKTweetListener tweetListener = new BASKTweetListener ();
            twitterStream.addListener (tweetListener);
            twitterStream.sample ();
        } catch (IOException ie) {
            logger.error ("Failed to create tweet listener", ie);
        }
    }

    private Configuration config () {
        Properties props = new Properties ();
        try {
            InputStream is = getClass ().getResourceAsStream ("/twitter.properties");
            if (is != null) props.load (is);
            else throw new IOException ("Could not load twitter.properties");
        } catch (IOException ie) {
            logger.error ("Could not read properties file, using default values instead", ie);
        } finally {
            Configuration config = new ConfigurationBuilder ()
                .setOAuthConsumerKey (props.getProperty ("oauth.consumer.key", ""))
                .setOAuthConsumerSecret (props.getProperty ("oauth.consumer.secret", ""))
                .setOAuthAccessToken (props.getProperty ("oauth.access.token", ""))
                .setOAuthAccessTokenSecret (props.getProperty ("oauth.access.secret", ""))
                .setJSONStoreEnabled(true)
                .build ();
            return config;
        }
    }

    public void shutdown () {
        if (twitterStream != null) twitterStream.shutdown ();
    }
}
