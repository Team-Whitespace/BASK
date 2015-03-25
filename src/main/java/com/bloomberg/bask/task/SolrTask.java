package com.bloomberg.bask.task;

import com.bloomberg.bask.serializer.JSONString;
import com.bloomberg.bask.system.Envelope;
import com.bloomberg.bask.system.SystemProducer;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.common.SolrInputDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrTask implements InitableTask, StreamTask {

    private static final Logger logger = LoggerFactory.getLogger(SolrTask.class);
    protected ConcurrentUpdateSolrServer server;
    private JSONString decoder;

    @Override
    public void init(Properties config) {
        decoder = new JSONString();
        String url = config.getProperty("solr.url");
        int threadCount = Integer.parseInt(config.getProperty("solr.thread.count"));
        int queueSize = Integer.parseInt(config.getProperty("solr.queue.size"));
        logger.info("Attempting connection to Solr, URL: {}", url);
        server = new ConcurrentUpdateSolrServer(url, queueSize, threadCount);
        logger.info("Connected to Solr");
    }

    @Override
    public void process(Envelope envelope, SystemProducer producer) {

        String message = envelope.getMessage();
        Map<String, Object> tweet = decoder.fromString(envelope.getMessage());
        Map<String, Object> user = (Map<String, Object>) tweet.get("user");

        try {
            if (isValid(tweet, Arrays.asList("id","id_str","text", "created_at")) &&
                    isValid(user, Arrays.asList("profile_image_url", "screen_name"))) {
                SolrInputDocument doc = new SolrInputDocument();
                doc.addField("id", Long.parseLong(tweet.get("id").toString()));
                doc.addField("id_str", tweet.get("id_str").toString());
                doc.addField("text", tweet.get("text").toString());
                Date createdAt = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy")
                    .parse((String) tweet.get("created_at").toString());
                String dateUTC = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").format(createdAt);
                doc.addField("created_at", dateUTC);
                doc.addField("user_profile_image_url", user.get("profile_image_url").toString());
                doc.addField("user_screen_name", user.get("screen_name").toString());
                doc.addField("tweet", message);
                server.add(doc);
                server.commit();
                logger.debug("Tweet sucessfully indexed by Solr: {}", tweet.get("id_str"));
            }
        } catch (SolrServerException|IOException|ParseException e) {
            logger.error("Exception in process method, message received: {}", message, e);
        }
    }

    private boolean isValid(Map<String, ?> map, List<String> fields) {
        for (String field : fields) {
          if (map.get(field) == null) return false;
        }
        return true;
    }
}
