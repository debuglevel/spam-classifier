<!--- some badges to display on the GitHub page -->

![Travis (.org)](https://img.shields.io/travis/debuglevel/spam-classifier?label=Travis%20build)
![Gitlab pipeline status](https://img.shields.io/gitlab/pipeline/debuglevel/spam-classifier?label=GitLab%20build)
![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/debuglevel/spam-classifier?sort=semver)
![GitHub](https://img.shields.io/github/license/debuglevel/spam-classifier)
[![Gitpod ready-to-code](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/debuglevel/spam-classifier)

# Spam Classifier

This microservice learns and classifies texts into spam or ham.

## HTTP API

### OpenAPI / Swagger

There is an OpenAPI (former: Swagger) specification created, which is available
at <http://localhost:8080/swagger/greeter-microservice-0.1.yml>, `build/tmp/kapt3/classes/main/META-INF/swagger/` in the
source directory and `META-INF/swagger/` in the jar file. It can easily be pasted into
the [Swagger Editor](https://editor.swagger.io) which provides a live demo
for [Swagger UI](https://swagger.io/tools/swagger-ui/), but also offers to create client libraries
via [OpenAPI Generator](https://openapi-generator.tech).

### Add person

```bash
$ curl --location --request POST 'http://localhost:8080/persons/' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "Foo Bar"
}'

{
    "id": "3e266d3b-df74-4918-9d5d-22a5983e9dc2",
    "name": "Foo Bar"
}
```

### Get person

```bash
$ curl --location --request GET 'http://localhost:8080/persons/3e266d3b-df74-4918-9d5d-22a5983e9dc2'

{
    "id": "3e266d3b-df74-4918-9d5d-22a5983e9dc2",
    "name": "Foo Bar"
}
```

## gRPC API

For services that primarily handle RPC-style requests (in contrast to resource-orientated REST)
, [gRPC](https://grpc.io/) might be a better approach.

### Protocol buffer and Service definitions

`*.proto` files in `src/main/proto/` describe the gRPC services and their protobuf.

### Generation

`./gradlew generateProto` generates Java/Kotlin files from the service and protobuf definitions
at `src/main/proto/*.proto`. They will be placed at `build/generated/source/proto/main/` and contain classes for
the `message`s and functions for the `service` `rpc`s.

### Endpoint implementation

Implementing endpoints is quite simple and demonstrated in `GreetingEndpoint`.

### List services

As gRPC server reflection is provided by the `ReflectionFactory`, `grpcurl` can list all available services:

```
$ ./grpcurl -plaintext localhost:50051 list
greeting.Greeting
grpc.reflection.v1alpha.ServerReflection
```

### Call

A gRPC call can be made with:

```
$ ./grpcurl -plaintext -d '{"name":"Hans", "locale":"de_DE"}' localhost:50051 greeting.Greeting/Greet
{
  "message": "Grüß Gott, Hans"
}
```

## Configuration

There is a `application.yml` included in the jar file. Its content can be modified and saved as a
separate `application.yml` on the level of the jar file. Configuration can also be applied via the other supported ways
of Micronaut (see <https://docs.micronaut.io/latest/guide/index.html#config>). For Docker, the configuration via
environment variables is the most interesting one (see `docker-compose.yml`).
