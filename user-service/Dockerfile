FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY user-service/pom.xml user-service/
COPY user-service/src user-service/src

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -q -f user-service/pom.xml package -Dmaven.test.skip=true

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /workspace/user-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
