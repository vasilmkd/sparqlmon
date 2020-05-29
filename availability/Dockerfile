FROM openjdk:8-jre-alpine
COPY target/scala-2.13/availability.jar /availability.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "availability.jar" ]
