#!/bin/bash

echo "Cleaning build..."
./gradlew clean

echo "Running game..."
./gradlew lwjgl3:run
