# ─── Stage 1: Build ──────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy dependency manifest trước để tận dụng Docker layer cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source và build jar (bỏ qua test)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─── Stage 2: Runtime ────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy jar từ stage build
COPY --from=builder /app/target/shift_service-0.0.1-SNAPSHOT.jar app.jar

# Render tự inject biến PORT; default fallback là 8081
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
