# ChatGPT Assistant

ChatGPT Assistant

## Features

Design by DDD(Domain-Driven Design) architecture

Microservice architecture

- Spring Boot 3
- Spring Cloud OpenFeign
- Spring Cloud Config
- Spring WebFlux
- Spring Security
- Spring Doc OpenAPI
- Spring Data JPA
- Native-image support
- Docker & Compose support
- K8S support (helm)
- CI/CD support (Github Actions)

### configserver

Centralized configuration service

- Refer
  to [spring-cloud-config](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#_spring_cloud_config_server)

### commons

Common library

- Global request & exception handler
- Global security configuration
- Global access control
- Domain-based model
- API facade

### identity

Identity & Authentication service

- RBAC (Role Based Access Control) support
- JWT (JSON Web Token) support
- OAuth2 support
- User management
- Some common features

### subscription

Subscription & Order service

- Subscription management
- User equity management
- Product & Order management

### assistant

ChatGPT Assistant service

- OpenAI API support
- ChatGPT conversation
- Token Account & Charge management

## Build

### Dependencies

- (for native-image) Graal VM 21
- (normally) JVM 17+
- maven 3.8+
- docker
- docker-compose plugin
- (k8s) helm

### Build package

```shell script
mvn clean package
```

### (optional) Build native-image

Required Graal VM 21, and install `native-image` to your `$PATH`
Refer to [GraalVM](https://www.graalvm.org/docs/getting-started/)

```shell
# Install commons library
mvn clean install -pl commons
# Build executable binary
mvn -Pnative clean package -pl identity,subscription,assistant
# Or build docker image by spring-boot plugin
mvn -Pnative spring-boot:build-image -pl identity,subscription,assistant

```

### Config

- Customize your [compose.yaml](compose.yaml)

### Run (docker-compose)

```shell script
# build docker image and run
docker-compose up -d --build
# or using compose plugin
docker compose up -d --build

```

### Kubernetes helm support

Helm chart base on [.chart](.charts) directory

Refer to [build-dev-image.yml](.github%2Fworkflows%2Fbuild-dev-image.yml) (for develop)
and [build-tags.yml](.github%2Fworkflows%2Fbuild-tags.yml) (for release)