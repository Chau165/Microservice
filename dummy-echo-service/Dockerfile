# ================================
# Stage 1: Build
# ================================
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and pom.xml first (for better layer caching)
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./

# Make mvnw executable and download dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build, skip tests
RUN ./mvnw package -DskipTests -B

# ================================
# Stage 2: Runtime
# ================================
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

# Add a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership
RUN chown appuser:appgroup app.jar

USER appuser

# Render and Railway inject PORT as environment variable
# Default to 8762 if not set
EXPOSE 8762

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
