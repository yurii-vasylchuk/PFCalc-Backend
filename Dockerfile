FROM eclipse-temurin:17-jre-focal

ARG SB_API_KEY=yurii-vasylchuk-ieggp1y3
ARG APT_PIN_DATE=2025-06-15T10:40:01Z

COPY ./docker/sb-apt.sh /opt/sb-apt.sh
RUN bash /opt/sb-apt.sh load-apt-sources ubuntu chromium

RUN apt update && apt install -y chromium

RUN useradd -m pfcalc
USER pfcalc

WORKDIR /app

COPY ./target/pfcalc-backend.jar /app/application.jar

ENTRYPOINT ["java"]
CMD ["-jar", "application.jar"]
