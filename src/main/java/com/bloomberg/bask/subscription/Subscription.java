package com.bloomberg.bask.subscription;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.flax.luwak.CandidateMatcher;
import uk.co.flax.luwak.InputDocument;
import uk.co.flax.luwak.InputDocument.Builder;
import uk.co.flax.luwak.Monitor;
import uk.co.flax.luwak.MonitorQuery;
import uk.co.flax.luwak.QueryMatch;
import uk.co.flax.luwak.presearcher.TermFilteredPresearcher;
import uk.co.flax.luwak.parsers.LuceneQueryCache;
import uk.co.flax.luwak.matchers.SimpleMatcher;

/**
 * This class is designed to simplify adding alerts and inputting documents
 * into Luwak.
 *
 * @author Kiran Hampal
 */
public class Subscription {

    private static final Logger logger = LoggerFactory.getLogger (Subscription.class);

    private Monitor monitor;

    public Subscription () {
        try {
            monitor = new Monitor (new LuceneQueryCache ("tweet"), new TermFilteredPresearcher ());
        } catch (IOException ie) {
            logger.error ("Could not create monitor for subscription", ie);
        }
    }

    /**
     * @return A Luwak Monitor associated with the subscription
     */
    public Monitor getMonitor () {
        return monitor;
    }

    /**
     * Adds an alert
     *
     * @param id    The id of the alert
     * @param query The query to be run against the tweets
     */
    public synchronized void addAlert (String id, String query) throws IOException {
        MonitorQuery monitorQuery = new MonitorQuery (id, "tweet:" + query);
        monitor.update (monitorQuery);
    }

    /**
     * Sends a tweet to see if matches any alerts
     *
     * @param tweet The tweet with the keys "id_str" and "text", both values are of the string type
     * @return Queries (alerts) that match the tweet
     */
    public synchronized SimpleMatcher sendTweet (JSONObject tweet) throws IOException {
        InputDocument doc = InputDocument.builder (tweet.getString ("id_str"))
            .addField ("tweet", tweet.getString ("text"), new StandardAnalyzer (Version.LUCENE_48))
            .build ();
        return monitor.match (doc, SimpleMatcher.FACTORY);
    }
}


