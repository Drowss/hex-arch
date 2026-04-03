#!/bin/bash

prefix="[deleteInfrastructure]"
echo "${prefix} Deleting kubernetes objects..."
echo

kubectl delete -f k8s