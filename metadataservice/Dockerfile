# ================================
# Stage 1: Build
# ================================
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./

RUN chmod +x mvnw && ./mvnw -q -B dependency:go-offline

COPY src ./src

RUN ./mvnw clean package -DskipTests

# ================================
# Stage 2: Runtime
# ================================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

COPY --from=build /app/target/*.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8763

ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]