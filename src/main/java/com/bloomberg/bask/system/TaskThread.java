package com.bloomberg.bask.system;

import com.bloomberg.bask.task.Task;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

public class TaskThread implements Runnable {

    private KafkaStream stream;
    private SystemProducer producer;
    private Task task;

    public TaskThread(Task task, SystemProducer producer, KafkaStream stream) {
        this.task = task;
        this.stream = stream;
        this.producer = producer;
    }

    public void run() {
        ConsumerIterator<String, Object> iter = stream.iterator();
        while (iter.hasNext()) {
            MessageAndMetadata<String, Object> msg = iter.next();
            Envelope message = new Envelope(msg.key(), msg.message(), msg.topic());
            task.process(message, producer);
        }
    }
}
