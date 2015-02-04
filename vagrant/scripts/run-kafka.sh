#!/bin/bash

KAFKA_LOCATION=/usr/share/kafka

while [ $# != 0 ]; do
    case $1 in
        --init-topics)
            INIT_TOPICS=true
            ;;
        *)
            ;;
    esac
    shift
done


setup_topics () {
    $KAFKA_LOCATION/bin/kafka-topics.sh --create --zookeeper 192.168.50.4:2181 --replication-factor 1 --partitions 2 --topic documents
    $KAFKA_LOCATION/bin/kafka-topics.sh --create --zookeeper 192.168.50.4:2181 --replication-factor 1 --partitions 2 --topic alerts
    $KAFKA_LOCATION/bin/kafka-topics.sh --create --zookeeper 192.168.50.4:2181 --replication-factor 1 --partitions 2 --topic matches
    $KAFKA_LOCATION/bin/kafka-topics.sh --create --zookeeper 192.168.50.4:2181 --replication-factor 1 --partitions 2 --topic results
    $KAFKA_LOCATION/bin/kafka-topics.sh --create --zookeeper 192.168.50.4:2181 --replication-factor 1 --partitions 2 --topic tweets
}

echo "Starting ZooKeeper..."
$KAFKA_LOCATION/bin/zookeeper-server-start.sh $KAFKA_LOCATION/config/zookeeper.properties &
sleep 3

echo "Starting Kafka Broker..."
$KAFKA_LOCATION/bin/kafka-server-start.sh $KAFKA_LOCATION/config/server.properties &
sleep 3

if [ $INIT_TOPICS ] ; then
    echo "Setting up Topics..."
    setup_topics
fi

echo "Done"
