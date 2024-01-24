#!/bin/bash

# Build packages
docker run -it --rm \
  -v "$(pwd)":/usr/src/mymaven \
  -v "$HOME/.m2":/root/.m2 \
  -w /usr/src/mymaven \
  maven:3-openjdk-17 \
  mvn clean package

# Build images
docker-compose build --build-arg GIT_VERSION="$(git rev-parse --short HEAD)"

# Deploy
docker-compose up -d $@

# Remove untagged images
images="$(docker images -f 'dangling=true' -q)"
if [ -n "$images" ]; then
    docker rmi ${images[@]}
fi

# Clean
docker run -it --rm \
  -v "$(pwd)":/usr/src/mymaven \
  -v "$HOME/.m2":/root/.m2 \
  -w /usr/src/mymaven \
  maven:3-openjdk-17 \
  mvn clean