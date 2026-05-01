# 🏥 Clinic Booking Microservices

<div align="center">

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Keycloak](https://img.shields.io/badge/Keycloak-IAM-4d4d4d?style=for-the-badge&logo=keycloak&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

**Hệ thống đặt lịch khám phòng khám xây dựng theo kiến trúc Microservices**

[Tổng quan](#-tổng-quan) · [Kiến trúc](#-kiến-trúc-hệ-thống) · [Services](#-danh-sách-microservices) · [Công nghệ](#-công-nghệ-sử-dụng) · [Cài đặt](#-hướng-dẫn-cài-đặt) · [API](#-api-endpoints)

</div>

---

## 📋 Tổng quan

**Clinic Booking Microservices** là một hệ thống backend hoàn chỉnh cho phép người dùng đặt lịch khám tại phòng khám trực tuyến. Dự án được xây dựng theo kiến trúc **Microservices** hiện đại với các tính năng như xác thực OAuth2/OIDC, thanh toán trực tuyến, thông báo email/SMS, quản lý đánh giá và ưu đãi.

### ✨ Tính năng chính

| Chức năng | Mô tả |
|-----------|-------|
| 🔐 Xác thực & Phân quyền | OAuth2/OIDC với Keycloak, JWT Token, custom login theme |
| 🏥 Quản lý phòng khám | Thêm/sửa/xóa thông tin phòng khám, bác sĩ, lịch làm việc |
| 📅 Đặt lịch khám | Đặt lịch, hủy lịch, quản lý trạng thái booking |
| 💳 Thanh toán | Tích hợp cổng thanh toán, quản lý giao dịch |
| 📧 Thông báo | Gửi email/SMS xác nhận đặt lịch, nhắc lịch hẹn |
| ⭐ Đánh giá | Hệ thống rating & review phòng khám, bác sĩ |
| 🎁 Ưu đãi | Quản lý mã khuyến mãi, ưu đãi đặt lịch |
| 🗂️ Danh mục | Quản lý danh mục chuyên khoa, dịch vụ |

---

## 🏗️ Kiến trúc hệ thống

```
                          ┌─────────────────────────────────────────────────┐
                          │              CLIENT (Browser / Mobile)           │
                          └──────────────────────┬──────────────────────────┘
                                                 │
                          ┌──────────────────────▼──────────────────────────┐
                          │            API Gateway (Spring Cloud Gateway)    │
                          │               Port: 8080                         │
                          └──┬──────┬──────┬──────┬──────┬──────┬──────┬───┘
                             │      │      │      │      │      │      │
           ┌─────────────────▼─┐  ┌─▼──┐ ┌▼────┐ ┌▼───┐ ┌▼───┐ ┌▼───┐ ┌▼──────┐
           │   user-service    │  │cat.│ │clin.│ │book│ │pay.│ │rev.│ │notif. │
           │   :8081           │  │svc │ │svc  │ │svc │ │svc │ │svc │ │svc    │
           └───────────────────┘  └────┘ └─────┘ └────┘ └────┘ └────┘ └───────┘
                          ┌──────────────────────────────────────────────────┐
                          │         Eureka Service Registry  :8761            │
                          └──────────────────────────────────────────────────┘
                          ┌──────────────────────────────────────────────────┐
                          │          Keycloak IAM Server  :8180               │
                          └──────────────────────────────────────────────────┘
```

### Luồng xử lý Request

```
Client → API Gateway → (Xác thực JWT tại Keycloak) → Route đến Service → Response
```

---

## 📦 Danh sách Microservices

### 1. 🔗 `eureka-server` — Service Registry
- **Cổng:** `8070`
- **Mô tả:** Trung tâm đăng ký và khám phá service (Netflix Eureka). Tất cả microservice tự đăng ký vào đây khi khởi động.
- **Công nghệ:** Spring Cloud Netflix Eureka Server
- **Dashboard:** `http://localhost:8761`

---

### 2. 🌐 `gateway-server` — API Gateway
- **Cổng:** `5000`
- **Mô tả:** Điểm vào duy nhất (single entry point) cho toàn bộ hệ thống. Thực hiện routing, load balancing, xác thực token, rate limiting.
- **Công nghệ:** Spring Cloud Gateway, Spring Security OAuth2 Resource Server
- **Chức năng:**
  - Route request đến đúng microservice
  - Xác thực JWT token với Keycloak
  - CORS configuration
  - Filter chain xử lý request/response

---

### 3. 👤 `user-service` — Quản lý người dùng
- **Mô tả:** Quản lý thông tin hồ sơ người dùng, bác sĩ, admin. Đồng bộ dữ liệu với Keycloak.
- **Chức năng:**
  - CRUD thông tin người dùng (bệnh nhân, bác sĩ)
  - Quản lý profile ảnh đại diện
  - Phân quyền ROLE_PATIENT, ROLE_DOCTOR, ROLE_ADMIN

---

### 4. 🏥 `clinic-service` — Quản lý phòng khám
- **Mô tả:** Quản lý toàn bộ thông tin phòng khám, lịch làm việc, dịch vụ cung cấp.
- **Chức năng:**
  - CRUD phòng khám (tên, địa chỉ, mô tả, ảnh)
  - Quản lý bác sĩ thuộc phòng khám
  - Quản lý lịch làm việc, khung giờ khám
  - Tìm kiếm phòng khám theo chuyên khoa, địa điểm

---

### 5. 📅 `booking-service` — Đặt lịch khám
- **Mô tả:** Core service xử lý toàn bộ quy trình đặt lịch khám của bệnh nhân.
- **Chức năng:**
  - Tạo lịch hẹn mới
  - Hủy, đổi lịch hẹn
  - Kiểm tra slot trống theo thời gian thực
  - Quản lý trạng thái booking (PENDING → CONFIRMED → COMPLETED / CANCELLED)
  - Tích hợp với `notification-service` để gửi xác nhận

---

### 6. 💳 `payment-service` — Thanh toán
- **Mô tả:** Xử lý thanh toán cho dịch vụ khám bệnh.
- **Chức năng:**
  - Tích hợp cổng thanh toán
  - Quản lý giao dịch (tạo, xác nhận, hoàn tiền)
  - Lưu lịch sử thanh toán
  - Callback xử lý kết quả thanh toán

---

### 7. 📧 `notification-service` — Thông báo
- **Mô tả:** Xử lý gửi thông báo đến người dùng qua email.
- **Chức năng:**
  - Gửi email xác nhận đặt lịch
  - Gửi nhắc lịch hẹn trước 24h
  - Template email tùy chỉnh
  - Queue message xử lý bất đồng bộ

---

### 8. ⭐ `review-service` — Đánh giá & nhận xét
- **Mô tả:** Hệ thống đánh giá và bình luận cho phòng khám, bác sĩ sau khi hoàn thành khám.
- **Chức năng:**
  - Tạo, sửa, xóa đánh giá
  - Rating 1–5 sao
  - Phân trang danh sách đánh giá
  - Tính điểm trung bình cho phòng khám

---

### 9. 🎁 `offer-service` — Ưu đãi & khuyến mãi
- **Mô tả:** Quản lý các ưu đãi, mã giảm giá cho dịch vụ đặt lịch.
- **Chức năng:**
  - CRUD ưu đãi theo phòng khám / dịch vụ
  - Áp dụng mã giảm giá khi thanh toán
  - Quản lý thời gian hiệu lực

---

### 10. 🗂️ `category-service` — Danh mục chuyên khoa
- **Mô tả:** Quản lý danh mục các chuyên khoa y tế và dịch vụ khám chữa bệnh.
- **Chức năng:**
  - CRUD danh mục chuyên khoa (Tim mạch, Nhi khoa, Da liễu, ...)
  - Liên kết phòng khám với chuyên khoa

---

### 11. 🎨 `keycloak-themes/clinic-booking` — Custom Keycloak Theme
- **Mô tả:** Theme tùy chỉnh giao diện đăng nhập/đăng ký của Keycloak phù hợp với thương hiệu ứng dụng.
- **Công nghệ:** FreeMarker templates, CSS
- **Áp dụng cho:** Login page, Register page, Forgot password page

---

## 🛠️ Công nghệ sử dụng

### Backend Core
| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| Java | 17+ | Ngôn ngữ lập trình chính |
| Spring Boot | 3.x | Framework ứng dụng |
| Spring Cloud | 2023.x | Microservices infrastructure |
| Spring Security | 6.x | Bảo mật, xác thực |
| Spring Data JPA | 3.x | ORM, tương tác database |

### Infrastructure & DevOps
| Công nghệ | Mục đích |
|-----------|----------|
| **Netflix Eureka** | Service Discovery & Registration |
| **Spring Cloud Gateway** | API Gateway, Reverse Proxy |
| **Keycloak** | Identity & Access Management (IAM), OAuth2/OIDC |
| **Docker / Docker Compose** | Container hóa ứng dụng |

### Database
| Công nghệ | Mục đích |
|-----------|----------|
| **PostgreSQL / MySQL** | Relational database cho các service |
| **Spring Data JPA + Hibernate** | ORM layer |

### Template & Frontend
| Công nghệ | Mục đích |
|-----------|----------|
| **FreeMarker** | Template engine cho Keycloak custom themes |
| **CSS** | Styling cho Keycloak login pages |

---

## 🔐 Bảo mật

Hệ thống sử dụng **Keycloak** làm Identity Provider (IdP) theo chuẩn **OAuth 2.0 / OpenID Connect**:

```
┌──────────┐   1. Login request   ┌───────────┐
│  Client  │ ──────────────────► │  Keycloak │
│          │ ◄────────────────── │  IAM      │
└──────────┘   2. JWT Token       └───────────┘
      │
      │ 3. Request + Bearer Token
      ▼
┌─────────────┐   4. Validate Token   ┌───────────┐
│ API Gateway │ ─────────────────────► │  Keycloak │
│             │ ◄───────────────────── │  (JWKS)   │
└─────────────┘   5. Token valid       └───────────┘
      │
      │ 6. Forward authenticated request
      ▼
┌─────────────┐
│ Microservice│
└─────────────┘
```

**Roles hệ thống:**
- `ROLE_PATIENT` — Bệnh nhân: đặt lịch, xem kết quả, đánh giá
- `ROLE_DOCTOR` — Bác sĩ: quản lý lịch làm việc, xem booking
- `ROLE_ADMIN` — Quản trị viên: toàn quyền quản lý hệ thống

---

## 🚀 Hướng dẫn cài đặt

### Yêu cầu hệ thống

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Keycloak 22+ (hoặc chạy qua Docker)
- PostgreSQL 15+ (hoặc chạy qua Docker)

### Bước 1: Clone repository

```bash
git clone https://github.com/NgSang0127/BookingMicroservice.git
cd BookingMicroservice
```

### Bước 2: Khởi động Infrastructure (Keycloak + Database)

```bash
docker-compose up -d
```

### Bước 3: Cấu hình Keycloak

1. Truy cập Keycloak Admin Console: `http://localhost:8180`
2. Tạo Realm mới: `clinic-booking`
3. Import cấu hình realm (nếu có file export)
4. Cài đặt custom theme từ `keycloak-themes/clinic-booking`
5. Tạo Client cho API Gateway

### Bước 4: Khởi động các Services (theo thứ tự)

```bash
# 1. Eureka Server (phải khởi động đầu tiên)
cd eureka-server
mvn spring-boot:run

# 2. API Gateway
cd ../gateway-server
mvn spring-boot:run

# 3. Các Microservices (có thể chạy song song)
cd ../user-service && mvn spring-boot:run &
cd ../clinic-service && mvn spring-boot:run &
cd ../category-service && mvn spring-boot:run &
cd ../booking-service && mvn spring-boot:run &
cd ../payment-service && mvn spring-boot:run &
cd ../notification-service && mvn spring-boot:run &
cd ../review-service && mvn spring-boot:run &
cd ../offer-service && mvn spring-boot:run &
```

### Bước 5: Kiểm tra hệ thống

| Service | URL |
|---------|-----|
| Eureka Dashboard | http://localhost:8761 |
| API Gateway | http://localhost:8080 |
| Keycloak Admin | http://localhost:8180 |

---

## 🗂️ Cấu trúc dự án

```
BookingMicroservice/
├── 📁 eureka-server/           # Service Registry
│   └── src/main/
│       ├── java/               # Main application class
│       └── resources/
│           └── application.yml
│
├── 📁 gateway-server/          # API Gateway
│   └── src/main/
│       ├── java/               # Gateway config, filters
│       └── resources/
│           └── application.yml
│
├── 📁 user-service/            # User management
│   └── src/main/java/
│       ├── controller/         # REST controllers
│       ├── service/            # Business logic
│       ├── repository/         # Data access layer
│       ├── model/entity/       # JPA entities
│       └── dto/                # Data Transfer Objects
│
├── 📁 clinic-service/          # Clinic management
├── 📁 booking-service/         # Booking management
├── 📁 payment-service/         # Payment processing
├── 📁 notification-service/    # Notifications
├── 📁 review-service/          # Reviews & ratings
├── 📁 offer-service/           # Offers & promotions
├── 📁 category-service/        # Medical categories
│
├── 📁 keycloak-themes/
│   └── clinic-booking/         # Custom Keycloak UI theme
│       ├── login/
│       │   ├── *.ftl           # FreeMarker templates
│       │   └── resources/css/  # Custom stylesheets
│       └── theme.properties
│
└── 📄 .gitignore
```

---

## 🌐 API Endpoints

Tất cả request đi qua **API Gateway** tại `http://localhost:5000`

### Authentication
```
POST   /api/auth/login           # Đăng nhập (redirect to Keycloak)
POST   /api/auth/logout          # Đăng xuất
POST   /api/auth/refresh         # Refresh access token
```

### User Service
```
GET    /api/users/me             # Lấy thông tin người dùng hiện tại
PUT    /api/users/me             # Cập nhật thông tin cá nhân
GET    /api/users/{id}           # Lấy thông tin user theo ID (Admin)
```

### Clinic Service
```
GET    /api/clinics              # Danh sách phòng khám (có filter, phân trang)
GET    /api/clinics/{id}         # Chi tiết phòng khám
POST   /api/clinics              # Tạo phòng khám mới (Admin)
PUT    /api/clinics/{id}         # Cập nhật phòng khám
DELETE /api/clinics/{id}         # Xóa phòng khám
GET    /api/clinics/{id}/slots   # Lịch làm việc còn trống
```

### Booking Service
```
GET    /api/bookings             # Danh sách lịch hẹn của tôi
POST   /api/bookings             # Tạo lịch hẹn mới
GET    /api/bookings/{id}        # Chi tiết lịch hẹn
PUT    /api/bookings/{id}/cancel # Hủy lịch hẹn
GET    /api/bookings/doctor      # Lịch hẹn của bác sĩ
```

### Payment Service
```
POST   /api/payments             # Khởi tạo thanh toán
GET    /api/payments/{id}        # Trạng thái giao dịch
POST   /api/payments/callback    # Webhook từ cổng thanh toán
```

### Review Service
```
GET    /api/reviews/clinic/{id}  # Đánh giá theo phòng khám
POST   /api/reviews              # Tạo đánh giá
PUT    /api/reviews/{id}         # Sửa đánh giá
DELETE /api/reviews/{id}         # Xóa đánh giá
```

### Category Service
```
GET    /api/categories           # Danh sách chuyên khoa
POST   /api/categories           # Tạo danh mục (Admin)
PUT    /api/categories/{id}      # Cập nhật danh mục
DELETE /api/categories/{id}      # Xóa danh mục
```

### Offer Service
```
GET    /api/offers               # Danh sách ưu đãi
GET    /api/offers/{code}        # Kiểm tra mã ưu đãi
POST   /api/offers               # Tạo ưu đãi (Admin)
```

---

## ⚙️ Cấu hình môi trường

Mỗi service sử dụng file `application.yml`. Dưới đây là các biến môi trường quan trọng:

```yaml
# Eureka Client (tất cả services)
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8070/eureka/

# Keycloak (Gateway + các service cần xác thực)
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:7180/realms/clinic-booking

# Database (mỗi service có DB riêng)
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/{service_db}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

---

## 📊 Sơ đồ giao tiếp giữa Services

```
booking-service ──────────────────────────────► notification-service
     │                                           (Gửi email xác nhận)
     │
     └──────────────────────────────────────────► payment-service
                                                  (Tạo thanh toán)

clinic-service ───────────────────────────────► category-service
                                                 (Lấy chuyên khoa)

review-service ───────────────────────────────► booking-service
                                                 (Kiểm tra booking đã hoàn thành)
```

---

## 🗺️ Roadmap

- [x] Service Discovery với Eureka
- [x] API Gateway với Spring Cloud Gateway
- [x] Xác thực OAuth2/OIDC với Keycloak
- [x] Custom Keycloak Login Theme
- [x] Các microservice core (User, Clinic, Booking, Payment, Review, Offer, Category, Notification)
- [ ] Docker Compose toàn hệ thống
- [ ] Distributed Tracing với Zipkin / Jaeger
- [ ] Centralized Logging với ELK Stack
- [ ] Circuit Breaker với Resilience4j
- [ ] Message Queue (Kafka / RabbitMQ) cho async communication
- [ ] Unit & Integration Tests
- [ ] CI/CD Pipeline với GitHub Actions
- [ ] Kubernetes deployment

---

## 👨‍💻 Tác giả

**NgSang0127**

- GitHub: [@NgSang0127](https://github.com/NgSang0127)

---

## 📄 License

Dự án này được phân phối dưới [MIT License](LICENSE).

---

<div align="center">

⭐ Nếu dự án này hữu ích, hãy cho một **Star** nhé!

</div>
