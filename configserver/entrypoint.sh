#!/bin/sh

configFiles="$(find /config -type f -regex ".*\.yaml$")"
for file in $configFiles; do
  set -- \
    "$@" \
    "--spring.config.import=file:$file"
done

if [ -n "$GIT_USERNAME" ]; then
  set -- \
    "$@" \
    "--spring.cloud.config.server.git.username=$GIT_USERNAME"
fi
if [ -n "$GIT_PASSWORD" ]; then
  set -- \
    "$@" \
    "--spring.cloud.config.server.git.password=$GIT_PASSWORD"
fi

if [ -n "$HTTP_PROXY" ]; then
  # Split host and port
  # https://127.0.0.1:3128 -> host: 127.0.0.1, port: 3128
  host="$(echo "$HTTP_PROXY" | sed -e 's/^https\?:\/\///' | cut -d: -f1)"
  port="$(echo "$HTTP_PROXY" | sed -e 's/^https\?:\/\///' | cut -d: -f2)"
  set -- \
    "$@" \
    "--spring.cloud.config.server.git.proxy.http.host=$host" \
    "--spring.cloud.config.server.git.proxy.http.port=$port"
fi

if [ -n "$HTTPS_PROXY" ]; then
  # Split host and port
  # https://127.0.0.1:3128 -> host: 127.0.0.1, port: 3128
  host="$(echo "$HTTPS_PROXY" | sed -e 's/^https\?:\/\///' | cut -d: -f1)"
  port="$(echo "$HTTPS_PROXY" | sed -e 's/^https\?:\/\///' | cut -d: -f2)"
  set -- \
    "$@" \
    "--spring.cloud.config.server.git.proxy.https.host=$host" \
    "--spring.cloud.config.server.git.proxy.https.port=$port"
fi

java -Duser.timezone=$TZ $JAVA_OPTS -server -jar "/app/configserver-1.0-SNAPSHOT.jar" \
  --server.port=8888 \
  "$@"
