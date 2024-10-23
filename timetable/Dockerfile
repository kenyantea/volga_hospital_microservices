FROM openjdk:17-jdk-alpine
RUN apk add --no-cache ttf-dejavu freetype
WORKDIR /app
COPY target/timetable-0.0.1-SNAPSHOT.jar /app/timetable.jar
ENTRYPOINT ["java", "-jar", "timetable.jar"]