package com.bloomberg.bask.luwak.kafka;

import java.io.IOException;
import java.util.Properties;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedKafka {

    private static final Logger logger = LoggerFactory.getLogger (EmbeddedKafka.class);

    private KafkaServerStartable kafka;
    private ZooKeeperServerMainStoppable zooKeeperServer;
    private ServerConfig serverConfig;

    public void start () {
        try {
            startZooKeeper (zooKeeperProperties ());
            startKafka (kafkaProperties ());
        } catch (Exception e) {
            logger.error ("Embedded Kafka/ZooKeeper failed to start", e);
        }
    }

    public void stop () {
        kafka.shutdown ();
        zooKeeperServer.stop ();
    }

    private void startZooKeeper (Properties props) throws Exception {
        QuorumPeerConfig qpConfig = new QuorumPeerConfig ();
        qpConfig.parseProperties (props);
        serverConfig = new ServerConfig ();
        serverConfig.readFrom (qpConfig);
        Thread zkThread = new Thread (new EmbeddedZooKeeper ());
        zkThread.start ();
    }

    private void startKafka (Properties props) {
        kafka = new KafkaServerStartable (new KafkaConfig (props));
        kafka.startup ();
    }

    private Properties zooKeeperProperties () {
        Properties props = new Properties ();
        props.setProperty ("clientPort", "23548");
        props.setProperty ("maxClientCnxns", "0");
        props.setProperty ("dataDir", "/tmp/embedded-zk-logs");
        return props;
    }

    private Properties kafkaProperties () {
        Properties props = new Properties ();
        props.setProperty ("host.name", "localhost");
        props.setProperty ("port", "23547");
        props.setProperty ("broker.id", "547");
        props.setProperty ("zookeeper.connect", "localhost:23548");
        props.setProperty ("log.dirs", "/tmp/embedded-kafka-logs");
        return props;
    }

    public class EmbeddedZooKeeper implements Runnable {

        public void run () {
            try {
                zooKeeperServer = new ZooKeeperServerMainStoppable ();
                zooKeeperServer.runFromConfig (serverConfig);
            } catch (IOException e) {
                logger.error ("Embedded ZooKeeper failed to start", e);
            }
        }
    }

    public class ZooKeeperServerMainStoppable extends ZooKeeperServerMain {

        public void stop () {
            super.shutdown ();
        }
    }
}



