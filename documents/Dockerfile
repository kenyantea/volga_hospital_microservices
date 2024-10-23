FROM openjdk:17-jdk-alpine
RUN apk add --no-cache ttf-dejavu freetype
WORKDIR /app
COPY target/documents-0.0.1-SNAPSHOT.jar /app/documents.jar
ENTRYPOINT ["java", "-jar", "documents.jar"]