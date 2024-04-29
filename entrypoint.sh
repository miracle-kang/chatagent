#!/bin/sh

serv=$1
shift

# If the first argument is "configserver", run the server.
case $serv in
"configserver")
  app="configserver-1.0-SNAPSHOT.jar"
  ;;
"identity")
  app="identity-1.0-SNAPSHOT.jar"
  ;;
"subscription")
  app="subscription-1.0-SNAPSHOT.jar"
  ;;
"agent")
  app="agent-1.0-SNAPSHOT.jar"
  ;;
*)
  echo "Unknown argument: $1"
  exit 1
  ;;
esac

configFiles="$(find /app/config -type f -regex ".*\.yaml$")"
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

# Github token for config-server
if [ "$serv" = "configserver" ]; then
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
fi

java -Duser.timezone=$TZ $JAVA_OPTS -server -jar "/app/$app" \
  --server.port=8080 \
  "$@"
