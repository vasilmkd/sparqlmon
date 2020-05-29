FROM openjdk:8-jre-alpine
COPY target/scala-2.13/gateway.jar /gateway.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "gateway.jar" ]
