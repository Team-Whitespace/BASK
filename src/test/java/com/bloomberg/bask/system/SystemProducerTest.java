package com.bloomberg.bask.system;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

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

        KafkaProducer producer = mock(KafkaProducer.class);
        SystemProducer sysProducer = new SystemProducer(producer);
        sysProducer.send(envelope);

        verify(producer).send(any(ProducerRecord.class));
    }
}
