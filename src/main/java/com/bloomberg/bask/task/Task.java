package com.bloomberg.bask.task;

import com.bloomberg.bask.system.Envelope;
import com.bloomberg.bask.system.SystemProducer;

public interface Task {

    public abstract void process(Envelope message, SystemProducer producer);
}
