FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: imagem leve com JDK
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/staywise-0.0.1-SNAPSHOT.jar staywise.jar

EXPOSE 8080
EXPOSE 5005  # Debug remoto

CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "staywise.jar"]
