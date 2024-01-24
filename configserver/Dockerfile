FROM openjdk:21-jdk-slim

ARG GIT_VERSION
RUN mkdir /app \
  && echo $GIT_VERSION > /app/git-version

COPY entrypoint.sh /
COPY target/configserver-1.0-SNAPSHOT.jar /app/

EXPOSE 8888
VOLUME /config
WORKDIR /app

ENTRYPOINT ["sh", "/entrypoint.sh"]