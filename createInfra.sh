#!/bin/bash

prefix="[createInfrastructure]"
echo "${prefix} Building modules..."
echo

./gradlew build

echo
echo "${prefix} Setting up docker images..."
echo

docker build -t hexarch-image .
docker pull postgres

echo
echo "${prefix} Applying kubernetes files..."
echo

kubectl apply -f k8s

echo
echo "${prefix} Finished creating infrastructure!"