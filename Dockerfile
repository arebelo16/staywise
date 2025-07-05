FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar staywise.jar

EXPOSE 8080
EXPOSE 5005  # Debug remoto

CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "staywise.jar"]
