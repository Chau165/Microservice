# ===================================================================
# Dockerfile for API Gateway Service
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
# PORT              : Railway & Render auto-set this env var
# EUREKA_URL        : Full Eureka URL (e.g. http://discovery-service:8761/eureka/)
# JWK_SET_URI       : JWKS endpoint for OAuth2 JWT validation
# ACCOUNT_JWKS_URL  : Account service JWKS URL
# JWT_SECRET        : JWT secret key
# ===================================================================

ENV PORT=8080
ENV EUREKA_URL=https://microservice-59zl.onrender.com/eureka/
ENV JWK_SET_URI=http://localhost:8085/.well-known/jwks.json
ENV ACCOUNT_JWKS_URL=http://localhost:8081/.well-known/jwks.json
ENV JWT_SECRET=Z3JvdXAxX2ZyYW5jaGlzZV9zZXJ2aWNlX3NlY3JldF8yMDI2

EXPOSE ${PORT}

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar", \
  "--server.port=${PORT}", \
  "--eureka.client.service-url.defaultZone=${EUREKA_URL}", \
  "--spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${JWK_SET_URI}", \
  "--app.account-service.jwks-url=${ACCOUNT_JWKS_URL}", \
  "--jwt.secret=${JWT_SECRET}"]
