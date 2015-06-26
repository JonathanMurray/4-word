#!/bin/sh

echo "Compiling all ..."

javac -cp /home/jonathan/Documents/AndEngine/src:/usr/local/android-sdk-linux/platforms/android-19/android.jar:/home/jonathan/IdeaProjects/4-word/out/production/android-test -d /home/jonathan/IdeaProjects/4-word/out/production/android-test/ src/fourword/*

cd out/production/android-test
java fourword.MultiplayerServer 4444
