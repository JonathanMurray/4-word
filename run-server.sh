#!/bin/sh

echo "Running server ..."

cd target/classes 
java fourword.server.Server 4444

