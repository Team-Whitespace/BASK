package com.bloomberg.bask.subscription.kafka.consumer;

import com.bloomberg.bask.subscription.Subscription;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

/**
 * Base class for the subscription consumers
 *
 * @author Kiran Hampal
 */
public abstract class SubscriptionConsumer implements Runnable {

    protected KafkaStream<String, String> stream;
    protected Subscription subscription;

    public SubscriptionConsumer (KafkaStream stream, Subscription subscription) {
        this.stream = stream;
        this.subscription = subscription;
    }

    /**
     * Runs the consumer
     */
    public void run () {
        ConsumerIterator<String, String> iter = stream.iterator ();
        while (iter.hasNext ()) processMessage (iter.next ().message ());
    }

    /**
     * Function that should decide what happens to the message
     *
     * @param message The message consumed from the Kafka server
     */
    protected abstract void processMessage (String message);
}
