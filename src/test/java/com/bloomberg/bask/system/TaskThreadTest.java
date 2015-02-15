package com.bloomberg.bask.system;

import com.bloomberg.bask.task.StreamTask;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

import org.junit.Test;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TaskThreadTest {

    @Test
    public void testTaskThreadShouldCallTaskProcess() {
        StreamTask task = mock(StreamTask.class);
        SystemProducer producer = mock(SystemProducer.class);
        KafkaStream stream = mock(KafkaStream.class);
        ConsumerIterator iter = mock(ConsumerIterator.class);
        MessageAndMetadata msg = mock(MessageAndMetadata.class);

        when(stream.iterator()).thenReturn(iter);
        when(iter.hasNext()).thenReturn(true, false);
        when(iter.next()).thenReturn(msg);

        TaskThread taskThread = new TaskThread(task, producer, stream);
        taskThread.run();

        verify(task).process(any(Envelope.class), eq(producer));
    }
}
