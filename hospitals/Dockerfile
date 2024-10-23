FROM openjdk:17-jdk-alpine
RUN apk add --no-cache ttf-dejavu freetype
WORKDIR /app
COPY target/hospitals-0.0.1-SNAPSHOT.jar /app/hospitals.jar
ENTRYPOINT ["java", "-jar", "hospitals.jar"]