package com.bloomberg.bask.subscription.kafka.consumer;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

/**
 * Solr tweet consumer
 * @author Radu Ban
 */

public class SolrTweetConsumer extends SubscriptionConsumer
{
    protected KafkaStream<String, String> stream;
    protected HttpSolrServer server;
    private final Logger logger = LoggerFactory.getLogger (TweetConsumer.class);
    private final String SolrURL = "http://localhost:8080/solr/tweets";
    
    public SolrTweetConsumer(KafkaStream stream, String topic)
    {
        super(stream, /*subscription*/null, topic);
    	//todo: take out hardcoded string in property file
        logger.info("Initializing SolrTwitterConsumer with topic: " +topic);
        logger.info("Attempting connection to Solr server: " +SolrURL);
        //HttpSolrServer server = new HttpSolrServer(SolrURL);
        //logger.info("connected to Solr");
    }

    protected void processMessage(String message)
    {
        JSONObject tweet = new JSONObject(new JSONTokener(message));
        try
        {
        	logger.info("received tweet with id: "+tweet.get("id_str"));
    		SolrInputDocument doc = new SolrInputDocument();
    		//todo: take out field names in property file
    		JSONObject user = tweet.getJSONObject("user");
    		doc.addField("id", tweet.getInt("id"));
    		doc.addField("id_str", tweet.get("id_str"));
    		doc.addField("text", tweet.get("text"));
    		doc.addField("created_at", tweet.get("created_at"));
    		doc.addField("user_profile_image_url", user.get("profile_image_url"));
    		doc.addField("user_screen_name", user.get("screen_name"));
    		//server.add(doc);
    		logger.info("Sending tweet to Solr with id: "+tweet.get("id_str"));
    		//server.commit();
    		logger.info("Tweet sucessfully indexed by Solr: "+tweet.get("id_str"));
        }
        catch (Exception ie)
        {
            logger.error("Failed to send tweet to Solr", ie);
        }
    }
}
