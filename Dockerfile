FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# JDK
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/domiledge-0.0.1-SNAPSHOT.jar domiledge.jar

EXPOSE 8080
EXPOSE 5005

CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "domiledge.jar"]
