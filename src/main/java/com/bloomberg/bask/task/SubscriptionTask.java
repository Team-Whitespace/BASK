package com.bloomberg.bask.task;

import com.bloomberg.bask.system.Envelope;
import com.bloomberg.bask.system.SystemProducer;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.flax.luwak.CandidateMatcher;
import uk.co.flax.luwak.InputDocument;
import uk.co.flax.luwak.InputDocument.Builder;
import uk.co.flax.luwak.Monitor;
import uk.co.flax.luwak.MonitorQuery;
import uk.co.flax.luwak.QueryError;
import uk.co.flax.luwak.QueryMatch;
import uk.co.flax.luwak.presearcher.TermFilteredPresearcher;
import uk.co.flax.luwak.parsers.LuceneQueryCache;
import uk.co.flax.luwak.matchers.SimpleMatcher;

public class SubscriptionTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionTask.class);

    private Monitor monitor;

    public SubscriptionTask() throws IOException {
        monitor = new Monitor(new LuceneQueryCache("document"), new TermFilteredPresearcher());
    }

    @Override
    public void process(Envelope envelope, SystemProducer producer) {
        String stream = envelope.getStream();
        switch (stream) {
            case "alerts":
                handleAlert((Map<String, String>) envelope.getMessage ());
                break;
            case "documents":
                Map<String, Object> matches = handleMatches((Map<String, Object>) envelope.getMessage());
                producer.send(new Envelope(matches, "matches"));
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

    private void handleAlert(Map<String, String> message) {
        String alert = message.get("alert");
        String action = message.get("action");
        try {
            switch (action) {
                case "add": addAlert(alert, "text:"+alert); break;
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
        InputDocument doc = InputDocument.builder ((String) document.get("id_str"))
            .addField ("text", (String) document.get("text"), new StandardAnalyzer(Version.LUCENE_48))
            .build ();
        return monitor.match(doc, SimpleMatcher.FACTORY);
    }
}
