#!/bin/bash

# This shell script will clean the gradle build, and then run the program

clear

echo "Cleaning build..."
./gradlew clean

echo "Gradle build cleaned..."
./gradlew lwjgl3:run

# ./gradlew clean && ./gradlew lwjgl3:run