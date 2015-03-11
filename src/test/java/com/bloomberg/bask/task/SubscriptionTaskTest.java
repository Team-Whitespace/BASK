package com.bloomberg.bask.task;

import com.bloomberg.bask.system.Envelope;
import com.bloomberg.bask.system.SystemProducer;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscriptionTaskTest {

    @Test
    public void testSubscriptionsProcessedCorrectly() throws IOException {
        Envelope envelopeAlert = mock(Envelope.class);
        when(envelopeAlert.getMessage()).thenReturn("{\"alert\": \"test\", \"action\": \"add\"}");
        when(envelopeAlert.getStream()).thenReturn("alerts");

        Envelope envelopeDoc = mock(Envelope.class);
        when(envelopeDoc.getMessage()).thenReturn("{\"text\": \"test abcd\", \"id_str\": \"1337\"}");
        when(envelopeDoc.getStream()).thenReturn("tweets");

        SystemProducer producer = mock(SystemProducer.class);

        SubscriptionTask task = new SubscriptionTask();
        task.init(new Properties());

        task.process(envelopeAlert, producer);
        task.process(envelopeDoc, producer);

        verify(producer).send(any(Envelope.class));
    }
}
