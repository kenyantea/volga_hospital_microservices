FROM openjdk:17-jdk-alpine
RUN apk add --no-cache ttf-dejavu freetype
WORKDIR /app
COPY target/demoauth-0.0.1-SNAPSHOT.jar /app/demoauth.jar
ENTRYPOINT ["java", "-jar", "demoauth.jar"]