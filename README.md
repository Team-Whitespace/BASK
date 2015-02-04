BASK
====

Building Bask
-------------

    You will need to build the whole project initially

    $ ./build.sh

    If you already have built Solr, Lucense & Luwak you can just do

    $ mvn clean install

Kafka
-----

Start up Vagrant and SSH into it:

    $ vagrant up
    
    $ vagrant ssh

Start up ZooKeeper and Kafka:

    $ /vagrant/vagrant/scripts/run-kafka.sh --init-topics

    
Running the demo
----------------

    $ mvn -q -e exec:java -Dexec.mainClass="com.bloomberg.bask.subscription.SubscriptionRunner" -Dexec.classpathScope="test" -Dexec.args="192.168.50.4:2181"


Running the sample consumers
-----------------------------
    
Viewing the messages on the sample consumer (on the vagrant machine):

    $ cd /usr/share/kafka

    $ bin/kafka-console-consumer.sh --zookeeper 192.168.50.4:2181 --topic documents --from-beginning
    
For Subscription:

    $ bin/kafka-console-consumer.sh --zookeeper 192.168.50.4:2181 --topic matches --from-beginning
    
    $ bin/kafka-console-consumer.sh --zookeeper 192.168.50.4:2181 --topic results --from-beginning
    
Send messages on the sample producer:

    $ bin/kafka-console-producer.sh --broker-list 192.168.50.4:9092 --topic documents

    
Running the throwaway producer
-------------------------------

    
Adding Sample Alerts:
    
    $ mvn -q -e exec:java -Dexec.mainClass="com.bloomberg.bask.stub.JSONProducer" -Dexec.classpathScope="test" -Dexec.args="--broker 192.168.50.4:9092 --topic alerts --source src/test/resources/sample_queries.json --randomDelay false"


Adding Sample Tweets:
    
    $ mvn -q -e exec:java -Dexec.mainClass="com.bloomberg.bask.stub.JSONProducer" -Dexec.classpathScope="test" -Dexec.args="--broker 192.168.50.4:9092 --topic documents --source src/test/resources/sample_tweets.json --randomDelay true"

