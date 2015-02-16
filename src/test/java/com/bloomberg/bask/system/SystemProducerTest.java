package com.bloomberg.bask.system;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import org.junit.Test;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SystemProducerTest {

    @Test
    public void testSendingEnvelopeSendsMessage() {
        Envelope envelope = mock(Envelope.class);
        when(envelope.getStream()).thenReturn("baz");

        Producer producer = mock(Producer.class);
        SystemProducer sysProducer = new SystemProducer(producer);
        sysProducer.send(envelope);

        verify(producer).send(any(KeyedMessage.class));
    }
}
