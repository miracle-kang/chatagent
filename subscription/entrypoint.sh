#!/bin/sh

configFiles="$(find /config -type f -regex ".*\.yaml$")"
for file in $configFiles; do
  set -- \
    "$@" \
    "--spring.config.import=file:$file"
done

if [ -n "$MYSQL_URL" ] && [ -n "$MYSQL_DATABASE" ]; then
  set -- \
    "$@" \
    "--spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver" \
    "--spring.datasource.url=jdbc:$MYSQL_URL/$MYSQL_DATABASE?useUnicode=true&characterEncoding=utf-8&characterSetResults=utf-8&createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&useTimezone=true"
fi
if [ -n "$MYSQL_USER" ] && [ -n "$MYSQL_PASSWORD" ]; then
  set -- \
    "$@" \
    "--spring.datasource.username=$MYSQL_USER" \
    "--spring.datasource.password=$MYSQL_PASSWORD"
fi

if [ -n "$REDIS_ADDRESS" ]; then
  set -- \
    "$@" \
    "--redis.enabled=true" \
    "--redis.address=$REDIS_ADDRESS"
fi
if [ -n "$REDIS_DATABASE" ]; then
  set -- \
    "$@" \
    "--redis.database=$REDIS_DATABASE"
fi
if [ -n "$REDIS_PASSWORD" ]; then
  set -- \
    "$@" \
    "--redis.password=$REDIS_PASSWORD"
fi

if [ -n "$DINGTALK_TOKEN" ]; then
  set -- \
    "$@" \
    "--third-party.dingtalk.token=$DINGTALK_TOKEN"
fi

if [ -n "$DINGTALK_SECRET" ]; then
  set -- \
    "$@" \
    "--third-party.dingtalk.secret=$DINGTALK_SECRET"
fi

if [ -n "$OPENAI_TOKEN" ]; then
  set -- \
    "$@" \
    "--third-party.openai.token=$OPENAI_TOKEN"
fi

if [ -n "$MAILSERVER_USERNAME" ]; then
  set -- \
    "$@" \
    "--spring.mail.username=$MAILSERVER_USERNAME"
fi

if [ -n "$MAILSERVER_PASSWORD" ]; then
  set -- \
    "$@" \
    "--spring.mail.password=$MAILSERVER_PASSWORD"
fi

if [ -n "$JWT_SECRET" ]; then
  set -- \
    "$@" \
    "--auth.jwt.key=$JWT_SECRET"
fi

java -Duser.timezone=$TZ $JAVA_OPTS -server -jar "/app/subscription-1.0-SNAPSHOT.jar" \
  --server.port=8080 \
  "$@"
