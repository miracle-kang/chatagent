FROM openjdk:21-jdk-slim

ARG GIT_VERSION
RUN mkdir /app \
  && echo $GIT_VERSION > /app/git-version

# All in one image
COPY entrypoint.sh /
COPY configserver/target/configserver-1.0-SNAPSHOT.jar /app/
COPY identity/target/identity-1.0-SNAPSHOT.jar /app/
COPY subscription/target/subscription-1.0-SNAPSHOT.jar /app/
COPY agent/target/agent-1.0-SNAPSHOT.jar /app/

EXPOSE 8080
VOLUME /app/config
WORKDIR /app

ENTRYPOINT ["sh", "/entrypoint.sh"]