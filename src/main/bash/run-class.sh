#!/bin/bash

base_dir=$(dirname $0)/..

for file in $base_dir/lib/*.[jw]ar;
do
  CLASSPATH=$CLASSPATH:$file
done

if [ -z "$JAVA_HOME" ]; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
fi

exec $JAVA $JAVA_OPTS -cp $CLASSPATH "$@"
