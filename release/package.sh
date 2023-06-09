#!/bin/bash

cp ../executables/*.jar app.jar

if [ -z "$NIGHTLY_BUILD" ]; then
  IMAGE_TAG=$(ls ../executables | grep Hedera | tail -n 1 | sed -E 's/Hedera-(.*).jar/\1/g')
else
  IMAGE_TAG=nightly
fi

docker build -t bkamelia/hedera:$IMAGE_TAG .
