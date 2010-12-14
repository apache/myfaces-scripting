#!/bin/sh
mvn javadoc:jar install
mvn -Pgenerate-assembly -f ./extscript-bundles/pom.xml
mvn package
 