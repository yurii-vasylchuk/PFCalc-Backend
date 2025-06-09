FROM eclipse-temurin:17-jre-focal AS layer-splitter
COPY ./target/pfcalc-backend.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-jre-focal
RUN apt-get update && \
    apt-get install -y chromium-browser fonts-liberation && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN mkdir "/app"

COPY --from=layer-splitter dependencies/ /app
COPY --from=layer-splitter snapshot-dependencies/ /app
COPY --from=layer-splitter spring-boot-loader/ /app
COPY --from=layer-splitter application/ /app

WORKDIR /app

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
