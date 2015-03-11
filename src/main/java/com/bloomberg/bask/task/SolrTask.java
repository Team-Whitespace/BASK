package com.bloomberg.bask.task;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import com.bloomberg.bask.system.Envelope;
import com.bloomberg.bask.serializer.JSONString;
import com.bloomberg.bask.system.SystemProducer;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.common.SolrInputDocument;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SolrTask implements InitableTask, StreamTask
 {
    private static final Logger logger = LoggerFactory.getLogger(SolrTask.class);
    //protected HttpSolrServer server;
    protected ConcurrentUpdateSolrServer server;
    private JSONString decoder;
    
    @Override
    public void init(Properties config)
    {
        try
        {
            decoder = new JSONString();
            String url = config.getProperty("solr.url");
            int threadCount = Integer.parseInt(config.getProperty("solr.thread.count"));
            int queueSize = Integer.parseInt(config.getProperty("solr.queue.size"));
            logger.info("Attempting connection to Solr, URL: " +url);
            //this.server = new HttpSolrServer(url);
            this.server = new ConcurrentUpdateSolrServer(url, queueSize, threadCount);
            logger.info("connected to Solr");
        }
        catch(Exception e)
        {
            logger.error("Exception in init method: \n", e);
        }
    }
    
    @Override
    public void process(Envelope envelope, SystemProducer producer)
    {
        String message = "";
        try
        {
            message = envelope.getMessage();
            Map<String, Object> tweet = decoder.fromString(envelope.getMessage());
            Map<String, Object> user = (Map<String, Object>) tweet.get("user");
            SolrInputDocument doc = new SolrInputDocument();
            //todo: take out field names into property file?
            doc.addField("id", Long.parseLong(tweet.get("id").toString()));
            doc.addField("id_str", tweet.get("id_str").toString());
            doc.addField("text", tweet.get("text").toString());
            Date createdAt = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy").parse((String) tweet.get("created_at").toString());
            String dateUTC = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").format(createdAt);
            doc.addField("created_at", dateUTC);
    	    doc.addField("user_profile_image_url", user.get("profile_image_url").toString());
    	    doc.addField("user_screen_name", user.get("screen_name").toString());
            doc.addField("tweet", message);
            logger.info("Sending tweet to Solr with id: "+tweet.get("id_str"));
            server.add(doc);
    	    server.commit();
            logger.info("Tweet sucessfully indexed by Solr: "+tweet.get("id_str"));
        }
        catch(Exception e)
        {
            logger.error("Exception in process method, message received: \n" +message, e);
        }
    }
}
