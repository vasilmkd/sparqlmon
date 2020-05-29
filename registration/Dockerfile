FROM openjdk:8-jre-alpine
COPY target/scala-2.13/registration.jar /registration.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "registration.jar" ]
