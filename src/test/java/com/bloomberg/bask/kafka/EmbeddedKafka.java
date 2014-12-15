package com.bloomberg.bask.kafka;

import java.io.IOException;
import java.util.Properties;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;
import kafka.utils.TestUtils;

import org.apache.curator.test.TestingServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedKafka {

    private static final Logger logger = LoggerFactory.getLogger (EmbeddedKafka.class);

    private KafkaServerStartable kafkaServer;
    private TestingServer zkServer;

    public void start () {
        try {
            startZK ();
        } catch (Exception e) {
            logger.error ("ZooKeeper failed to start", e);
        }
        startKafka ();
    }

    public void stop () {
        kafkaServer.shutdown ();
        try {
            zkServer.stop ();
        } catch (IOException ie) {
            logger.error ("ZooKeeper failed to stop", ie);
        }
    }

    public String getKafkaBrokerString () {
        return String.format (
            "%s:%d",
            kafkaServer.serverConfig ().hostName (),
            kafkaServer.serverConfig ().port ()
        );
    }

    public String getZKConnectString () {
        return zkServer.getConnectString ();
    }

    private void startZK () throws Exception {
        zkServer = new TestingServer (true);
    }

    private void startKafka () {
        kafkaServer = new KafkaServerStartable (kafkaConfig ());
        kafkaServer.startup ();
    }

    private KafkaConfig kafkaConfig () {
        Properties props = TestUtils.createBrokerConfigs (1).iterator ().next ();
        props.put ("zookeeper.connect", getZKConnectString ());
        return new KafkaConfig (props);
    }
}
