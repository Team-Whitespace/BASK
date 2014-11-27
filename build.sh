#!/bin/bash

echo "Cloning and building Lucene intervals"
git clone -b lucene_solr_4_8 https://github.com/bloomberg/lucene-solr.git
cd lucene-solr/maven-build
git reset --hard HEAD; git clean -f -d; git pull
mvn clean install -DskipTests
cd ../..

echo "Cloning and building Luwak"
git clone -b 1.1.x https://github.com/bloomberg/luwak.git
cd luwak
git reset --hard HEAD; git clean -f -d; git pull
mvn clean install
cd ..

echo "Building BASK"
mvn clean install
