package com.bloomberg.bask.task;

import com.bloomberg.bask.system.Envelope;
import com.bloomberg.bask.system.SystemProducer;

import com.google.common.collect.ImmutableMap;

import java.io.IOException;

import org.junit.Test;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscriptionTaskTest {

    @Test
    public void testSubscriptionsProcessedCorrectly() throws IOException {
        Envelope envelopeAlert = mock(Envelope.class);
        when(envelopeAlert.getMessage()).thenReturn(ImmutableMap.of("action", "add", "alert", "foobar"));
        when(envelopeAlert.getStream()).thenReturn("alerts");

        Envelope envelopeDoc = mock(Envelope.class);
        when(envelopeDoc.getMessage()).thenReturn(ImmutableMap.of("id_str", "314", "text", "foobar"));
        when(envelopeDoc.getStream()).thenReturn("documents");

        SystemProducer producer = mock(SystemProducer.class);

        SubscriptionTask task = new SubscriptionTask();

        task.process(envelopeAlert, producer);
        task.process(envelopeDoc, producer);

        verify(producer).send(any(Envelope.class));
    }
}
