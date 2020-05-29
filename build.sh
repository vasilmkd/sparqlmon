#!/bin/bash

function buildImage () {
    cd ${1}/target/scala-2.13
    mv ${1}-* ${1}.jar
    cd ../..
    docker build -t sparqlmon/${1} .
    cd ..
}

sbt clean assembly
buildImage registration
buildImage availability
buildImage status
buildImage alerting
buildImage gateway
