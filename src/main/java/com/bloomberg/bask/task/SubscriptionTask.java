package com.bloomberg.bask.task;

import com.bloomberg.bask.serializer.JSONString;
import com.bloomberg.bask.system.Envelope;
import com.bloomberg.bask.system.SystemProducer;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.flax.luwak.CandidateMatcher;
import uk.co.flax.luwak.InputDocument;
import uk.co.flax.luwak.InputDocument.Builder;
import uk.co.flax.luwak.Monitor;
import uk.co.flax.luwak.MonitorQuery;
import uk.co.flax.luwak.Presearcher;
import uk.co.flax.luwak.QueryError;
import uk.co.flax.luwak.QueryMatch;
import uk.co.flax.luwak.presearcher.TermFilteredPresearcher;
import uk.co.flax.luwak.parsers.LuceneQueryCache;
import uk.co.flax.luwak.matchers.SimpleMatcher;

public class SubscriptionTask implements InitableTask, StreamTask {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionTask.class);

    private Monitor monitor;
    private JSONString decoder;

    @Override
    public void init(Properties config) {
        try {
            decoder = new JSONString();
            String indexPath = config.getProperty("subscription.lucene.directory");
            LuceneQueryCache queryCache = new LuceneQueryCache("text");
            Presearcher presearcher = new TermFilteredPresearcher();
            if (indexPath == null || indexPath.isEmpty()) {
                monitor = new Monitor(queryCache, presearcher, new RAMDirectory());
            } else {
                monitor = new Monitor(queryCache, presearcher, new MMapDirectory(new File(indexPath)));
            }
        } catch(IOException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @Override
    public void process(Envelope envelope, SystemProducer producer) {
        String stream = envelope.getStream();
        switch (stream) {
            case "alerts":
                handleAlert(decoder.fromString(envelope.getMessage()));
                break;
            case "documents":
                Map<String, Object> document = decoder.fromString(envelope.getMessage());
                if (document.get("id_str") != null && document.get("text") != null) {
                    String matches = decoder.toString(handleMatches(document));
                    producer.send(new Envelope(matches, "matches"));
                }
                break;
            default:
                throw new UnexpectedStreamException("Unexpected stream: " + stream);
        }
    }

    private Map<String, Object> handleMatches(Map<String, Object> message) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> keywords = new ArrayList<String>();
        try {
            for (QueryMatch match : matchDocument(message)) {
                keywords.add(match.getQueryId());
            }
            result.put("keywords", keywords);
            result.put("tweet", message);
        } catch (IOException ie) {
            logger.error ("Failed to match", ie);
        }

        return result;
    }

    private void handleAlert(Map<String, ?> message) {
        String alert = (String) message.get("alert");
        String action = (String) message.get("action");
        try {
            switch (action) {
                case "add": addAlert(alert, "text:" + alert); break;
                case "delete": deleteAlert(alert); break;
            }
        } catch (IOException ie) {
            logger.error("Failed to handle alert", ie);
        }
    }

    private void addAlert(String id, String alert) throws IOException {
        MonitorQuery monitorQuery = new MonitorQuery(id, alert);
        try {
            for (QueryError error : monitor.update(monitorQuery)) {
                logger.warn (error.toString());
            }
        } catch (IOException ie) {
            logger.error("Could not add query with id '{}' and value '{}'", id, alert);
            throw ie;
        }
    }

    private void deleteAlert(String id) throws IOException {
        try {
            monitor.deleteById(id);
        } catch (IOException ie) {
            logger.error("Could not delete query with id '{}'", id, ie);
            throw ie;
        }
    }

    private SimpleMatcher matchDocument(Map<String, Object> document) throws IOException {
        InputDocument doc = InputDocument.builder((String) document.get("id_str"))
            .addField ("text", (String) document.get("text"), new StandardAnalyzer(Version.LUCENE_48))
            .build();
        return monitor.match(doc, SimpleMatcher.FACTORY);
    }
}
