FROM openjdk:8-jre-alpine
COPY target/scala-2.13/status.jar /status.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "status.jar" ]
