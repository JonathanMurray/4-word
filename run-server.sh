#!/bin/sh

echo "Running server ..."

cd out/production/android-test
java fourword.protocol.Server 4444

