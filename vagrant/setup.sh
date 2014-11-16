#!/usr/bin/env bash

kafka_mirror=http://mirror.ox.ac.uk/sites/rsync.apache.org/kafka/0.8.1.1/kafka_2.9.1-0.8.1.1.tgz

setup_kafka () {
    wget --quiet -O kafka.tgz $kafka_mirror > /dev/null
    tar xvf kafka.tgz > /dev/null
    rm -f kafka.tgz
    mv kafka_*/ /usr/local/kafka/
    echo "export PATH=$PATH:/usr/local/kafka/bin" >> .bashrc
}

setup_baleene () {
    echo "Setting up Baleene"
}

echo "Updating repositories"
apt-get -y update > /dev/null

echo "Installing software from repositories"
apt-get -y install openjdk-7-jre-headless openjdk-7-jdk \
                   solr-jetty jetty libjetty-extra scala \
                   maven icedtea-7-plugin \
                   python-software-properties vim git-core curl \
                   > /dev/null

echo "Updating alternatives to Java 7"
update-java-alternatives -s java-1.7.0-openjdk-amd64

echo "Setting up Kafka"
setup_kafka

echo "Setting up Baleene"
setup_baleene

echo "Finished!"
