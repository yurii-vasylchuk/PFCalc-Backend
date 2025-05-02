FROM maven:3.9.9-amazoncorretto-21 AS build

WORKDIR /app
COPY pom.xml .

RUN --mount=type=secret,id=github_user \
    --mount=type=secret,id=github_token \
    GITHUB_USER=$(cat /run/secrets/github_user) \
    GITHUB_TOKEN=$(cat /run/secrets/github_token) \
    mvn dependency:go-offline --batch-mode -ntp

COPY src ./src

RUN --mount=type=secret,id=github_user \
    --mount=type=secret,id=github_token \
    GITHUB_USER=$(cat /run/secrets/github_user) \
    GITHUB_TOKEN=$(cat /run/secrets/github_token) \
    mvn clean package --batch-mode -DskipTests

FROM eclipse-temurin:17-jre-focal AS layer-splitter
COPY --from=build /app/target/pfcalc-backend.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-jre-focal
COPY --from=layer-splitter dependencies/ ./
COPY --from=layer-splitter snapshot-dependencies/ ./
COPY --from=layer-splitter spring-boot-loader/ ./
COPY --from=layer-splitter application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
