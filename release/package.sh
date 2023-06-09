#!/bin/bash

cp ../executables/*.jar app.jar

if [ -z "$NIGHTLY_BUILD" ]; then
  export IMAGE_TAG=$(ls ../executables | grep Hedera | tail -n 1 | sed -E 's/Hedera-(.*).jar/\1/g')
else
  export IMAGE_TAG="nightly"
fi
