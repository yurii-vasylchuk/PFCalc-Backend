FROM eclipse-temurin:17-jre-focal AS layer-splitter
COPY ./target/pfcalc-backend.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-jre-focal
COPY --from=layer-splitter dependencies/ ./
COPY --from=layer-splitter snapshot-dependencies/ ./
COPY --from=layer-splitter spring-boot-loader/ ./
COPY --from=layer-splitter application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
