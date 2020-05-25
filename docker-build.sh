#!/bin/sh
rm -rf build
./gradlew build -x test
docker build . -t app
echo
echo
echo "To run the docker container execute:"
echo "  docker run --name app app"

