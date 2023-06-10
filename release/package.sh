#!/bin/bash

cp ../executables/*.jar app.jar

export IMAGE_TAG=$(ls ../executables | grep Hedera | tail -n 1 | sed -E 's/Hedera-(.*).jar/\1/g')
