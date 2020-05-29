#!/bin/bash

function buildImage () {
    cd ${1}/target/universal
    unzip ${1}-*.zip
    rm ${1}-*.zip
    mv ${1}-* $1
    cd ../..
    docker build -t sparqlmon/${1} .
    cd ..
}

sbt clean universal:packageBin

buildImage registration

buildImage availability

buildImage status

buildImage alerting
