#!/bin/bash

# This shell script will clean the gradle build, and then run the program

echo "Cleaning build..."
./gradlew clean

echo "Running build..."
./gradlew lwjgl3:run

echo HELLO THIS IS A CHANGE
# ./gradlew clean && ./gradlew lwjgl3:run
