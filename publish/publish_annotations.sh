#!/bin/sh
#stop script on failure
set -e

echo " ____________
< Publishing Klutter Annotations modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build annotations-jvm modules"
echo "------------------"
./gradlew clean -p "packages/annotations-jvm"
./gradlew build -p "packages/annotations-jvm"

echo "\0/ Klutter: step: publish annotations-jvm modules"
echo "------------------"
./gradlew publish -p "packages/annotations-jvm"

echo "\0/ Klutter: step: build annotations-kmp modules"
echo "------------------"
./gradlew clean -p "packages/annotations-kmp"
./gradlew build -p "packages/annotations-kmp"

echo "\0/ Klutter: step: publish annotations-kmp modules"
echo "------------------"
./gradlew publish -p "annotations-kmp"

echo "\0/ Klutter: step: build annotations-processor modules"
echo "------------------"
./gradlew clean -p "annotations-processor"
./gradlew build -p "annotations-processor"

echo "\0/ Klutter: step: publish annotations-processor modules"
echo "------------------"
./gradlew publish -p "annotations-processor"