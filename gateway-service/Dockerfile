# Build context: repository root, because the service inherits from the parent pom.
# Building with -f skips the parent's <modules>, so other services' poms aren't needed.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY gateway-service/pom.xml gateway-service/
COPY gateway-service/src gateway-service/src

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -q -f gateway-service/pom.xml package -Dmaven.test.skip=true

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /workspace/gateway-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
