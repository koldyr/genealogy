#!/bin/sh

git pull
mvn -DskipTests clean install
rm -f ./app/genealogy.jar
cp ./webapp/target/genealogy-rest-*.jar ./app/genealogy.jar
