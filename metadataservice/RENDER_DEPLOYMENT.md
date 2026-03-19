# Metadata Service - Render Deployment Guide

## Overview
This document explains how to deploy the `metadata-service` to Render with hardcoded Eureka configuration.

## Files Modified/Created

### 1. **Dockerfile** (NEW)
Location: `metadataservice/Dockerfile`

Multi-stage build configuration:
- **Stage 1 (Build)**: Uses Maven 3.9.6 with JDK 17 to compile and package the application
- **Stage 2 (Runtime)**: Uses Alpine JRE 17 for minimal image size
- Includes non-root user `spring` for security
- Exposes port via `$PORT` environment variable (Render standard)

### 2. **application.yml** (UPDATED)
Location: `metadataservice/src/main/resources/application.yml`

#### Key Changes:

##### A. Server Port Configuration
```yaml
server:
  address: 0.0.0.0
  port: ${PORT:8763}
```
- Reads `PORT` environment variable (Render sets this automatically)
- Defaults to 8763 for local development

##### B. Eureka Client Configuration - HARDCODED
```yaml
eureka:
  client:
    service-url:
      defaultZone: https://microservice-i7nc.onrender.com/eureka/
    fetch-registry: true
    register-with-eureka: true
    healthcheck:
      enabled: true
```
- **Hardcoded Eureka URL**: `https://microservice-i7nc.onrender.com/eureka/`
- No environment variables used - URL is fixed in config
- Enables health checks and service retrieval

##### C. Eureka Instance Registration
```yaml
eureka:
  instance:
    prefer-ip-address: false
    hostname: metadata-service-render.onrender.com
    instance-id: metadata-service:${random.value}
    
    secure-port-enabled: true
    secure-port: 443
    non-secure-port-enabled: false
    
    home-page-url: https://metadata-service-render.onrender.com/
    status-page-url: https://metadata-service-render.onrender.com/actuator/info
    health-check-url: https://metadata-service-render.onrender.com/actuator/health
```
- Uses HTTPS (secure-port: 443)
- Hostname hardcoded (update after knowing actual Render service name)
- Health check endpoints pointing to Render service

##### D. Database Configuration
Currently set up for SQL Server (local):
```yaml
datasource:
  url: jdbc:sqlserver://localhost:1433;databaseName=metadata_db;encrypt=true;trustServerCertificate=true
  username: sa
  password: 12345
  driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

**For Render deployment**, you have options:
- Use Render PostgreSQL: Uncomment the PostgreSQL hikari config
- Use external SQL Server: Update the JDBC URL and credentials
- Use environment variables: Add them to Render services

## Deployment Steps to Render

### Step 1: Connect GitHub Repository
1. Push this code to GitHub
2. In Render dashboard: New → Web Service
3. Connect your GitHub repository
4. Select the branch containing metadataservice

### Step 2: Configure Build and Start Commands
In Render dashboard:
- **Build Command**: `mvn clean package -DskipTests`
- **Start Command**: `java -jar target/metadataservice-*.jar`

### Step 3: Environment Variables (if needed)
Optional - only if you want to override the hardcoded config:
- `PORT` - Render sets this automatically (usually 10000)
- `DATABASE_URL` - For PostgreSQL databases
- `DATABASE_USER` - Database username
- `DATABASE_PASSWORD` - Database password

**Note**: The Eureka URL is hardcoded in `application.yml`, so you don't need to set it as an environment variable.

### Step 4: Note the Service URL
After deployment, Render will provide a URL like:
```
https://metadata-service-xxxxx.onrender.com
```

**Important**: Update the hostname in `application.yml` Eureka instance config:
```yaml
eureka:
  instance:
    hostname: metadata-service-xxxxx.onrender.com  # Replace xxxxx with actual Render subdomain
    home-page-url: https://metadata-service-xxxxx.onrender.com/
    status-page-url: https://metadata-service-xxxxx.onrender.com/actuator/info
    health-check-url: https://metadata-service-xxxxx.onrender.com/actuator/health
```

### Step 5: Verify Eureka Registration
1. Navigate to Eureka server: https://microservice-i7nc.onrender.com/eureka/
2. Look for `METADATA-SERVICE` in the registered instances
3. Check instance details to ensure hostname and health check URL are correct

## Important Notes

### Database Configuration
The current `application.yml` uses SQL Server on localhost. For Render:
- **Option 1**: Use Render PostgreSQL add-on (recommended)
  - Uncomment PostgreSQL config in datasource section
  - Render injects DATABASE_URL automatically
  
- **Option 2**: Connect to external SQL Server
  - Update JDBC URL in application.yml
  - Provide credentials (environment variables or hardcode)

- **Option 3**: Use SQL Server on Render
  - Requires additional setup and is less integrated

### Actuator Endpoints
Enabled endpoints for monitoring:
- `/actuator/health` - Health status
- `/actuator/info` - Application info  
- `/actuator/metrics` - Metrics collection

These are used by:
- Eureka for health checks
- Render for deployment readiness
- Load balancers/monitoring systems

### Java Version
- Requires Java 17+ (matches Spring Boot 3.2.0)
- Dockerfile uses Eclipse Temurin JDK 17

### Security
- Non-root user `spring` runs the application
- HTTPS enforced (secure-port-enabled: true)
- Non-secure port disabled

### Performance
Hibernate batch settings for better database performance:
```yaml
hibernate:
  jdbc:
    batch_size: 20
    fetch_size: 50
```

## Troubleshooting

### Service Not Registering with Eureka
1. Check if Eureka server is accessible: https://microservice-i7nc.onrender.com/eureka/
2. Verify health endpoint responds: `/actuator/health`
3. Check application logs for Eureka client errors
4. Ensure network allows outbound HTTPS (443) to Eureka server

### Port Binding Issues
- Render automatically assigns PORT environment variable
- If deployment fails: check that server.port is set to `${PORT:8763}`

### Database Connection Issues
- For LocalSQL Server: database must be accessible from Render
- For SQL Server: SQL Server must allow remote connections
- For PostgreSQL: Use Render's native PostgreSQL add-on

## Local Development Setup
To test locally with these configs:
```bash
# Run with custom port
export PORT=8080
mvn spring-boot:run

# Or build and run jar
mvn clean package -DskipTests
java -jar target/metadataservice-*.jar
```

## File Structure After Changes
```
metadataservice/
├── Dockerfile (NEW)
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/group1/app/
│   │   │       ├── MetadataShiftApplication.java (@EnableDiscoveryClient enables Eureka)
│   │   │       ├── common/
│   │   │       ├── metadata/
│   │   │       └── shift/
│   │   └── resources/
│   │       └── application.yml (UPDATED with hardcoded Eureka config)
│   └── test/
│       └── ...
├── target/
│   ├── classes/
│   └── *.jar
├── SOLUTION.md
└── RENDER_DEPLOYMENT.md (THIS FILE)
```

## Next Steps
1. Update the hostname in application.yml after knowing the Render service URL
2. Configure database connection for Render environment
3. Push changes to GitHub
4. Deploy to Render using the steps above
5. Monitor logs in Render dashboard for any startup issues
