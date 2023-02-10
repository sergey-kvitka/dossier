FROM eclipse-temurin:17-jdk-alpine
ADD target/*.jar dossier.jar
ENTRYPOINT ["java", "-jar", "/dossier.jar"]
