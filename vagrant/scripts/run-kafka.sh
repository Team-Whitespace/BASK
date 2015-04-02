#!/bin/bash

KAFKA_LOCATION=/usr/share/kafka

echo "Starting ZooKeeper..."
$KAFKA_LOCATION/bin/zookeeper-server-start.sh $KAFKA_LOCATION/config/zookeeper.properties &
sleep 3

echo "Starting Kafka Broker..."
$KAFKA_LOCATION/bin/kafka-server-start.sh $KAFKA_LOCATION/config/server.properties &
sleep 3

echo "Done"
