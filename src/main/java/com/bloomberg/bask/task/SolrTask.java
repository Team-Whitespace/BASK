package com.bloomberg.bask.task;

import com.bloomberg.bask.system.Envelope;
import com.bloomberg.bask.system.SystemProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrTask implements StreamTask {

    private static final Logger logger = LoggerFactory.getLogger(SolrTask.class);

    @Override
    public void process(Envelope envelope, SystemProducer producer) {
        // [TODO]
        System.out.println(envelope.getMessage());
    }

}
