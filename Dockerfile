FROM eclipse-temurin:17-jre-focal
RUN apt-get update && \
    apt-get install -y chromium-browser fonts-liberation && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY ./target/pfcalc-backend.jar /app/application.jar

ENTRYPOINT ["java"]
CMD ["-jar", "application.jar"]
