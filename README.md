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
- CI/CD support (GitHub Actions)

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
- Service API facade

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

Requirements:

- JDK 17+
- Docker

Optional:

- (native-image) Graal VM 21+
- (local) docker-compose plugin
- (k8s) helm

### Build package

```shell script
./mvnw clean package
```

### (optional) Build native-image

Required Graal VM 21, and install `native-image` to your `$PATH`.
Refer to [GraalVM](https://www.graalvm.org/docs/getting-started/)

```shell
# Install commons library
./mvnw clean install -pl commons
# Build executable binary
./mvnw -Pnative clean package -pl identity,subscription,assistant
# Or build docker image by spring-boot plugin
./mvnw -Pnative spring-boot:build-image -pl identity,subscription,assistant

```

### Config

- Customize your [compose.yaml](compose.yaml)

### Run (docker-compose)

```shell
# build docker image and run
docker-compose up -d --build
# or using compose plugin
docker compose up -d --build
```

### Kubernetes support

Helm chart base on [.chart](.charts) directory

Refer to [build-dev-image.yml](.github%2Fworkflows%2Fbuild-dev-image.yml) (for develop)
and [build-tags.yml](.github%2Fworkflows%2Fbuild-tags.yml) (for release)

Build helm chart, replace the variables with your own
```shell
# Replace chart name and image repository
sed -i "s/__REPLACE_CHART_NAME__/${CHART_NAME}/g" ./.charts/Chart.yaml
sed -i "s,__REPLACE_IMAGE_NAME__,${IMAGE_REPO},g" ./.charts/values.yaml
# Add custom helm repo
helm repo add ${HELM_REPO} ${HELM_REPO_URL} --username ${HELM_REPO_USERNAME} --password ${HELM_REPO_PASSWORD}
helm repo update ${HELM_REPO}
# Install helm cm-push plugin
helm plugin install https://github.com/chartmuseum/helm-push
# Build & push helm chart package
helm cm-push ./.charts ${HELM_REPO}
# Or build & push with customized version
helm cm-push ./.charts ${HELM_REPO} --version ${VERSION} --app-version ${APP_VERSION}
```

Customize your helm [values.yaml](.charts/values.yaml) if needed. 
And then install helm chart to k8s, replace the variables with your own
```shell
# Add custom helm repo if needed
helm repo add ${HELM_REPO} ${HELM_REPO_URL} --username ${HELM_REPO_USERNAME} --password ${HELM_REPO_PASSWORD}
helm repo update ${HELM_REPO}
# Install helm chart
helm install ${RELEASE_NAME} ${HELM_REPO}/${CHART_NAME} --namespace ${NAMESPACE} --version ${VERSION}
# Or install with customized values
helm install ${RELEASE_NAME} ${HELM_REPO}/${CHART_NAME} --namespace ${NAMESPACE} --version ${VERSION} --values ./your-customized-values.yaml
```