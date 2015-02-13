BASK
====

About
-----

BASK is a Samza inspired real time Twitter alerting application written by 5 University
of Bristol students.

Running Bask
-------------

1. You will need to build the whole project initially

        $ ./build.sh

   If you already have built Solr, Lucense & Luwak you can just do

        $ mvn clean install

2. Start up Vagrant, and run Kafka/Zookeeper/Solr on it

        $ vagrant up && vagrant ssh
        $ /vagrant/vagrant/scripts/run-kafka.sh --init-topics
        $ exit

3. Extract the generated archive

        $ mkdir -p deploy/bask
        $ tar -xvf ./target/bask-1.0-SNAPSHOT-dist.tar.gz -C deploy/bask

4. Start the feed

        $ deploy/bask/bin/run-feed.sh --feed deploy/bask/config/twitter.feed.properties

   **NOTE:** You will need to add your own oauth details in the properties file and change `localhost` to `192.168.50.4` if using the vagrant machine provided!

5. Start the task(s)

        $ deploy/bask/bin/run-task.sh --task deploy/bask/config/solr.task.properties
        $ deploy/bask/bin/run-task.sh --task deploy/bask/config/subscription.task.properties

   **NOTE:** You will need to change `localhost` to `192.168.50.4` if using the vagrant machine provided!

Development notes
------------------

For debugging purpose, it may be useful to see message on a topic, or send messages to one.

Viewing the messages on the sample consumer:

        $ /usr/share/kafka/bin/kafka-console-consumer.sh --zookeeper 192.168.50.4:2181 --topic TOPICNAME --from-beginning

Sending messages on the sample producer:

        $ /usr/share/kafka/bin/kafka-console-producer.sh --broker-list 192.168.50.4:9092 --topic TOPICNAME

To run the application without extracting the tarball, you can do:

        $ mvn -q -e exec:java -Dexec.mainClass="com.bloomberg.bask.job.RunFeed" -Dexec.args="--feed src/main/config/FEEDNAME.feed.properties"
        $ mvn -q -e exec:java -Dexec.mainClass="com.bloomberg.bask.job.RunTask" -Dexec.args="--task src/main/config/TASKNAME.task.properties"

