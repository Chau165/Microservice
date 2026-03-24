# ===================================================================
# Dockerfile for Metadata Service
# Compatible with: Render, Docker Compose, Local Development
# ===================================================================

# --- Stage 1: Build ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true -B

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
# Environment variables (can override on Render)
# Note: Eureka URL is hardcoded in application.yml
# ===================================================================
# PORT              : Render auto-sets this env var (default 8080)
# DATABASE_URL      : Database connection URL
# DATABASE_USER     : Database username
# DATABASE_PASSWORD : Database password
# ===================================================================

ENV PORT=8080

EXPOSE ${PORT}

# Server port is read from ${PORT} environment variable at runtime
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar app.jar --server.port=${PORT}"]
