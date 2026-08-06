# Stage 1 - Build the application
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Stage 2 - Run the application
FROM eclipse-temurin:25-jre

WORKDIR /app

LABEL org.opencontainers.image.title="transaction-monitoring-backend"
LABEL org.opencontainers.image.description="Spring Boot backend service for transaction monitoring and alert workflow"
LABEL org.opencontainers.image.source="https://github.com/Neueda-Learning/114_TLE_Transaction_Monitoring_System"

COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]