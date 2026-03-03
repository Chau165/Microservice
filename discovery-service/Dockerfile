# ===================================================================
# Dockerfile for Discovery Service (Eureka Server)
# Compatible with: Railway, Render, Docker Compose
# ===================================================================

# --- Stage 1: Build ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership
RUN chown -R spring:spring /app
USER spring

# ===================================================================
# Environment variables (override on Railway / Render / docker-compose)
# ===================================================================
# PORT: Railway & Render auto-set this env var
# EUREKA_HOSTNAME: hostname for Eureka instance
# ===================================================================

ENV PORT=8761
ENV EUREKA_HOSTNAME=localhost

EXPOSE ${PORT}

ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar", \
    "--server.port=${PORT}", \
    "--eureka.instance.hostname=${EUREKA_HOSTNAME}", \
    "--eureka.client.service-url.defaultZone=http://${EUREKA_HOSTNAME}:${PORT}/eureka/"]
