FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar main-server.jar
ENTRYPOINT ["java", "-jar", "/main-server.jar"]