FROM openjdk:8-jre-alpine
COPY target/scala-2.13/alerting.jar /alerting.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "alerting.jar" ]
