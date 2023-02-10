FROM eclipse-temurin:17-jdk-alpine
ARG JAR_FILE=target/*.jar
ADD JAR_FILE dossier.jar
ENTRYPOINT ["java", "-jar", "/dossier.jar"]