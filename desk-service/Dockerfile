FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY desk-service/pom.xml desk-service/
COPY desk-service/src desk-service/src

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -q -f desk-service/pom.xml package -Dmaven.test.skip=true

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /workspace/desk-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
