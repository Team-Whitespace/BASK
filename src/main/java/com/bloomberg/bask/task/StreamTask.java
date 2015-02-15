package com.bloomberg.bask.task;

import com.bloomberg.bask.system.Envelope;
import com.bloomberg.bask.system.SystemProducer;

public interface StreamTask {

    void process(Envelope message, SystemProducer producer);
}
