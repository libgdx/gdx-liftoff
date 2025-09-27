#!/bin/bash

clear

echo "Cleaning build..."
./gradlew clean

echo "Gradle build cleaned..."
./gradlew lwjgl3:run

echo HELLO THIS IS A CHANGE
# ./gradlew clean && ./gradlew lwjgl3:run
