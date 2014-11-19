#!/usr/bin/env bash

kafka_mirror=http://mirror.ox.ac.uk/sites/rsync.apache.org/kafka/0.8.1.1/kafka_2.9.1-0.8.1.1.tgz
solr_mirror=http://mirror.ox.ac.uk/sites/rsync.apache.org/lucene/solr/4.10.2/solr-4.10.2.tgz

setup_kafka () {
    wget --quiet -O kafka.tgz $kafka_mirror > /dev/null
    tar xvf kafka.tgz > /dev/null
    rm -f kafka.tgz
    mv kafka_*/ /usr/share/kafka/
}

setup_solr () {
    wget --quiet -O solr.tgz $solr_mirror > /dev/null
    tar xvf solr.tgz > /dev/null
    rm -f solr.tgz
    mv solr-*/ /usr/share/solr/
    cd /usr/share/solr/
    cp example/lib/ext/* /usr/share/tomcat7/lib/
    cp dist/solr-*.war /var/lib/tomcat7/webapps/solr.war
    cp -R example/solr /var/lib/tomcat7
    chown -R tomcat7:tomcat7 /var/lib/tomcat7/solr
    cp /vagrant/vagrant/config/tomcat-users.xml /etc/tomcat7/tomcat-users.xml
    service tomcat7 restart
    cd
}

setup_baleene () {
    echo "TODO"
}

echo "Updating repositories"
apt-get -y update > /dev/null

echo "Installing software from repositories [this may take a while]"
apt-get -y install openjdk-7-jre-headless openjdk-7-jdk \
                   tomcat7 tomcat7-admin scala \
                   maven icedtea-7-plugin \
                   python-software-properties vim git-core curl \
                   > /dev/null

echo "Updating alternatives to Java 7"
update-java-alternatives -s java-1.7.0-openjdk-amd64

echo "Setting up Solr [this may take a while]"
setup_solr

echo "Setting up Kafka"
setup_kafka

echo "Setting up Baleene"
setup_baleene

echo "Finished!"
