# Hướng dẫn triển khai lên Render

Tài liệu này hướng dẫn triển khai `shift-service` lên Render (Web Service) và kết nối tới PostgreSQL do Render cung cấp, đồng thời đăng ký service lên Eureka server `https://microservice-6dyb.onrender.com/`.

## Yêu cầu trước
- Tài khoản Render
- Repository đã chứa mã nguồn (maven, Java 21)
- Database trên Render (nếu dùng DB sẵn của bạn, dùng thông tin đã có)

> Dữ liệu DB cung cấp trong yêu cầu:
> - Internal hostname: `dpg-d6ntij15pdvs73ftol50-a`
> - Port: `5432`
> - Database: `shift_db_lqv6`
> - Username: `shift_db_lqv6_user`
> - Password: `i2CnAvRHtzNgXtf63QCQnvkKiDH4JyGx`
> - Internal JDBC URL: `postgresql://shift_db_lqv6_user:i2CnAvRHtzNgXtf63QCQnvkKiDH4JyGx@dpg-d6ntij15pdvs73ftol50-a/shift_db_lqv6`
> - External JDBC URL: `postgresql://shift_db_lqv6_user:i2CnAvRHtzNgXtf63QCQnvkKiDH4JyGx@dpg-d6ntij15pdvs73ftol50-a.oregon-postgres.render.com/shift_db_lqv6`

## Tổng quan bước triển khai
1. Build artifact (jar) bằng Maven
2. Trên Render: tạo `Postgres` service (nếu chưa có) hoặc sử dụng DB đã cung cấp
3. Tạo `Web Service` trên Render để deploy jar
4. Thiết lập environment variables (DB, Eureka, PORT)
5. Deploy và kiểm tra logs, kết nối DB, đăng ký Eureka

## Câu lệnh nhanh (local / CI)
```bash
# build
mvn clean package -DskipTests

# chạy local (nếu muốn test jar với external DB)
java -jar target/shift_service-0.0.1-SNAPSHOT.jar
```

## Cấu hình Render - chi tiết
### 1) Database
- Nếu bạn dùng DB đã có trên Render trong cùng team/region: chọn `Internal Hostname` (dpg-d6ntij15pdvs73ftol50-a) khi cấu hình service để service có thể kết nối nội mạng.
- Nếu deploy từ bên ngoài Render (hoặc test local), dùng `external` host: dpg-d6ntij15pdvs73ftol50-a.oregon-postgres.render.com

### 2) Tạo Web Service trên Render
- Type: `Web Service`
- Connect repository: chọn repo chứa project
- Branch: chọn branch muốn deploy
- Build Command: `mvn -DskipTests clean package`
- Start Command: `java -jar target/shift_service-0.0.1-SNAPSHOT.jar`
- Environment: `Docker` hoặc native: Render sẽ chạy jar với lệnh Start Command

### 3) Environment Variables (Render -> Environment -> Environment Variables)
Sử dụng biến môi trường để không hardcode thông tin:
- `PORT` (Render tự set, nhưng có thể override) — app hiện dùng `server.port=${PORT:8081}`
- `SPRING_DATASOURCE_URL` = `jdbc:postgresql://dpg-d6ntij15pdvs73ftol50-a/shift_db_lqv6`
- `SPRING_DATASOURCE_USERNAME` = `shift_db_lqv6_user`
- `SPRING_DATASOURCE_PASSWORD` = `i2CnAvRHtzNgXtf63QCQnvkKiDH4JyGx`

Thay vì `SPRING_DATASOURCE_URL`, bạn cũng có thể set `SPRING_DATASOURCE_URL` bằng `jdbc:postgresql://<host>:5432/<db>` nếu cần port rõ ràng.

Eureka config (biến môi trường, Spring Boot relaxed binding):
- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` = `https://microservice-6dyb.onrender.com/eureka/`
- `EUREKA_CLIENT_REGISTER_WITH_EUREKA` = `true`
- `EUREKA_CLIENT_FETCH_REGISTRY` = `true`

Lưu ý: Spring Boot sẽ chuyển tên biến ENV sang property tương ứng (ví dụ `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` -> `eureka.client.service-url.defaultZone`).

### 4) Secrets
- Thêm mật khẩu DB trong Render bằng cách đánh dấu là Secret (Render có lựa chọn `Protect`/`Secret` cho environment variables) để không bị lộ trong UI.

### 5) Health check & Logs
- Health check: nếu không có `spring-boot-actuator` bạn có thể dùng `/` hoặc tạo một endpoint health. Nếu thêm actuator, bật `management.endpoints.web.exposure.include=*` và dùng `/actuator/health`.
- Kiểm tra logs realtime từ Render dashboard -> Logs để debug lỗi startup (DB connection, missing dependency, SSL issues).

## Eureka - lưu ý
- Ứng dụng đã thêm dependency Eureka client và cấu hình `eureka.client.service-url.defaultZone` trỏ tới `https://microservice-6dyb.onrender.com/eureka/`.
- Đảm bảo Eureka server chấp nhận đăng ký qua HTTPS; nếu server có self-signed cert bạn cần thiết lập truststore hoặc chạy qua HTTP (không khuyến nghị).

## Thử nghiệm & Troubleshooting
- Lỗi kết nối DB: kiểm tra `SPRING_DATASOURCE_URL` và xem liệu bạn đang dùng internal hostname (chỉ hoạt động khi DB trong cùng VPC/region trên Render) hay external host.
- Nếu gặp lỗi SSL khi kết nối tới Postgres, thử thêm tham số `?sslmode=require` vào URL nếu DB yêu cầu SSL.
  Ví dụ: `jdbc:postgresql://<host>:5432/<db>?sslmode=require`
- Eureka không thấy service: kiểm tra app logs để xem quá trình `Eureka registration` (bao gồm URL, exception). Đảm bảo `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` đúng.

## Tối ưu & scaling
- Memory: nếu app bị OOM, tăng instance size trên Render hoặc tinh chỉnh JVM opts (e.g., `JAVA_TOOL_OPTIONS=-Xmx512m`).
- Scalability: bật Horizontal Autoscaling trên Render nếu cần.

---
Nếu bạn muốn, tôi sẽ:
- Thêm file cấu hình `render.yaml` (Infra as Code) để tự động tạo service trên Render.
- Hoặc tạo script `deploy.sh` để build và deploy tự động bằng Render CLI.

Hoặc bạn muốn tôi cập nhật `DEPLOY_RENDER.md` bằng tiếng Anh hoặc mở rộng phần `render.yaml` không?