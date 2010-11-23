#!/bin/sh
export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
mvn -o -P development -f extscript-examples/myfaces20-example/pom.xml clean jetty:run-exploded 
