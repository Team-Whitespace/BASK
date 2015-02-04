package com.bloomberg.bask.subscription;

import com.bloomberg.bask.subscription.kafka.consumer.ConsumerManager;

public class SubscriptionRunner {

    public static void main (String[] args) {
        Subscription subscription = new Subscription ();
        ConsumerManager cm = new ConsumerManager (
            subscription,
            args[0],
            "test"
        );
        cm.run ();
    }
}
