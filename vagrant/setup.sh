#!/usr/bin/env bash

apt-get update
apt-get install maven2 openjdk-7-jre-headless tomcat7

service tomcat7 start

