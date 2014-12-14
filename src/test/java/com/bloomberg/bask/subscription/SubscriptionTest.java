package com.bloomberg.bask.subscription;

import java.io.IOException;
import java.util.UUID;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import org.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import uk.co.flax.luwak.Monitor;
import uk.co.flax.luwak.InputDocument;
import uk.co.flax.luwak.InputDocument.Builder;
import uk.co.flax.luwak.QueryMatch;
import uk.co.flax.luwak.matchers.SimpleMatcher;

public class SubscriptionTest {

    Subscription subscription;

    @Before
    public void setUp () {
        subscription = new Subscription ();
    }

    @Test
    public void addAlertAddsToMonitor () throws IOException {
        SimpleMatcher matches;
        Monitor monitor = subscription.getMonitor ();

        subscription.addAlert ("fooAlert", "foo");
        subscription.addAlert ("barAlert", "bar");

        matches = monitor.match (createDoc ("test"), SimpleMatcher.FACTORY);
        assertEquals (matches.getMatchCount (), 0);

        matches = monitor.match (createDoc ("testbar foo test"), SimpleMatcher.FACTORY);
        assertEquals (matches.getMatchCount (), 1);

        matches = monitor.match (createDoc ("bar foo test"), SimpleMatcher.FACTORY);
        assertEquals (matches.getMatchCount (), 2);
    }

    @Test
    public void tweetIsSuccessfullyMatched () throws IOException {
        subscription.addAlert ("fooAlert", "foo");
        subscription.addAlert ("barAlert", "bar");
        subscription.addAlert ("bazAlert", "baz");

        JSONObject tweet = new JSONObject ()
            .put ("id_str", UUID.randomUUID ().toString ())
            .put ("text", "bar baz");

        for (QueryMatch match : subscription.sendTweet (tweet)) {
            assertTrue (
                match.getQueryId ().equals ("barAlert") ||
                match.getQueryId ().equals ("bazAlert")
            );
        }
    }

    private InputDocument createDoc (String term) {
        return InputDocument.builder (UUID.randomUUID ().toString ())
            .addField ("tweet", term, new StandardAnalyzer (Version.LUCENE_48))
            .build ();
    }
}
