# Resource Booking System

A production-ready RESTful Resource Booking System built with Spring Boot, Java 21, Spring Security (JWT), and MySQL. Users can browse resources and manage reservations while administrators have full control over resources and all bookings.

## ✨ Features

- **JWT-based Authentication**: Stateless, secure token-based authentication
- **Role-Based Access Control**:
  - `ADMIN`: Full CRUD on resources and reservations
  - `USER`: Read resources, manage only their own reservations
- **Reservation Management**: Create, view, update, and cancel reservations
- **Reservation Validation**:
  - End time must be after start time
  - Price must be greater than zero
  - Overlap detection prevents double-booking
- **Advanced Filtering & Pagination**: Filter by status, price range with pagination support
- **Centralized Exception Handling**: Proper HTTP status codes and error messages
- **Secure Configuration**: Environment-based secrets management
- **API Documentation**: Auto-generated Swagger UI and OpenAPI spec
- **Unit Tests**: Comprehensive test coverage with Spring Boot Test

## 📋 Tech Stack

| Component       | Version            |
| --------------- | ------------------ |
| Java            | 21 (LTS)           |
| Spring Boot     | 4.1.1              |
| Spring Security | Included with Boot |
| Spring Data JPA | Included with Boot |
| Hibernate       | 7.4.5              |
| MySQL Connector | Latest             |
| JJWT (JWT)      | 0.13.0             |
| Lombok          | Latest             |
| Maven           | 3.9+               |

## 🚀 Quick Start

### Prerequisites

- **Java 21**: Download from [Oracle](https://www.oracle.com/java/technologies/downloads/#java21) or use OpenJDK
- **MySQL**: Install and ensure the service is running
- **Maven**: Optional (included wrapper can be used: `./mvnw` or `mvnw.cmd`)

### 1. Setup Database

```bash
mysql -u root -p
CREATE DATABASE booking_system;
EXIT;
```

### 2. Clone Repository

```bash
git clone <repository-url>
cd bookingsystem
```

### 3. Configure Environment Variables

**Important**: Externalize secrets before deploying to production.

Option A: Set environment variables

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_secure_password
export JWT_SECRET=your_very_long_secret_key_at_least_32_chars
export JWT_EXPIRATION=86400000
```

Option B: Update `application.properties`:

```properties
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root1234}
jwt.secret=${JWT_SECRET:change_me_in_production}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

### 4. Run the Application

```bash
mvnw.cmd spring-boot:run
```

Application starts on **http://localhost:8080**

### 5. Access Swagger UI

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 📚 API Endpoints

### Authentication

**POST** `/auth/login` - Login with credentials, returns JWT token

```json
Request:
{
  "username": "admin",
  "password": "admin123"
}

Response (200):
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ADMIN"
}
```

### Resources (ADMIN only - require Authorization header)

| Method   | Endpoint          | Description                |
| -------- | ----------------- | -------------------------- |
| `POST`   | `/resources`      | Create resource (201)      |
| `GET`    | `/resources`      | List resources (paginated) |
| `GET`    | `/resources/{id}` | Get resource by ID         |
| `PUT`    | `/resources/{id}` | Update resource            |
| `DELETE` | `/resources/{id}` | Delete resource            |

### Reservations

| Method   | Endpoint                    | Description                     |
| -------- | --------------------------- | ------------------------------- |
| `POST`   | `/reservations`             | Create reservation (201)        |
| `GET`    | `/reservations`             | List with filters & pagination  |
| `GET`    | `/reservations/{id}`        | Get own reservation details     |
| `PUT`    | `/reservations/{id}/status` | Update status (ADMIN only)      |
| `DELETE` | `/reservations/{id}`        | Cancel reservation (ADMIN only) |

**Example - Filter reservations:**

```
GET /reservations?status=PENDING&minPrice=50&maxPrice=500&page=0&size=10&sort=startTime,desc
```

**Authorization Header (required for all endpoints except `/auth/login`):**

```
Authorization: Bearer <your_jwt_token>
```

## 🔐 Security Features

- **JWT Tokens**: Stateless authentication with JJWT 0.13+
- **BCrypt Hashing**: Secure password storage
- **Environment Variables**: Secrets externalized from codebase
- **User Identity**: Always derived from JWT token, never from request body
- **Ownership Validation**: Users cannot access other users' reservations

## 👥 Default Credentials

Seeded at application startup:

| Username | Password   | Role  |
| -------- | ---------- | ----- |
| `admin`  | `admin123` | ADMIN |
| `user`   | `user123`  | USER  |

⚠️ **Change these in production!**

## 🧪 Running Tests

```bash
mvn test
```

Test suite includes:

- Application context loading
- Spring Security configuration
- Basic functionality validation

## 📦 Project Structure

```
src/main/java/com/anmol/bookingsystem/
├── BookingsystemApplication.java       (Main entry point)
├── config/
│   ├── OpenApiConfig.java             (Swagger/OpenAPI)
│   └── SeedDataConfig.java            (Seed default users)
├── controller/
│   ├── AuthController.java
│   ├── ReservationController.java
│   └── ResourceController.java
├── service/
│   ├── AuthService.java
│   ├── ReservationService.java
│   └── ResourceService.java
├── repository/
│   ├── UserRepository.java
│   ├── ReservationRepository.java
│   └── ResourceRepository.java
├── entity/
│   ├── User.java
│   ├── Resource.java
│   ├── Reservation.java
│   ├── Role.java
│   └── ReservationStatus.java
├── dto/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── ResourceDTO.java
│   ├── ReservationRequestDTO.java
│   └── ReservationResponseDTO.java
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedAccessException.java
│   └── GlobalExceptionHandler.java
└── security/
    ├── JwtUtil.java
    ├── JwtAuthFilter.java
    ├── SecurityConfig.java
    └── CustomUserDetailsService.java
```

## 🔍 Example cURL Requests

### 1. Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 2. Create Resource (ADMIN, returns 201)

```bash
curl -X POST http://localhost:8080/resources \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Meeting Room A",
    "type":"Room",
    "description":"10-person capacity",
    "available":true
  }'
```

### 3. Create Reservation (returns 201)

```bash
curl -X POST http://localhost:8080/reservations \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "resourceId":1,
    "startTime":"2026-09-20T10:00:00",
    "endTime":"2026-09-20T12:00:00",
    "price":150.00
  }'
```

### 4. List Reservations with Filters

```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  "http://localhost:8080/reservations?status=PENDING&minPrice=100&maxPrice=500"
```

## 🐛 Known Issues & Future Improvements

- [ ] Implement transaction management for concurrent reservations
- [ ] Add caching layer for resource availability
- [ ] Audit logging for all reservation changes
- [ ] Email notifications on status changes
- [ ] Rate limiting and DDoS protection
- [ ] Advanced scheduling conflicts detection

## 📄 License

Provided as-is for educational and assessment purposes.

---

**Status**: Production-Ready | **Last Updated**: September 12, 2026 | **Java**: 21 (LTS)

### 3. Configure environment

Update `src/main/resources/application.properties` with your own database credentials and a secret key:

```properties
spring.application.name=bookingsystem
spring.datasource.url=jdbc:mysql://localhost:3306/booking_system
spring.datasource.username=root
spring.datasource.password=your_mysql_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=your_long_random_secret_key_at_least_32_characters
jwt.expiration=86400000
```

| Property                                  | Description                                                        |
| ----------------------------------------- | ------------------------------------------------------------------ |
| `spring.datasource.url`                   | JDBC URL of your MySQL database                                    |
| `spring.datasource.username` / `password` | MySQL credentials                                                  |
| `spring.jpa.hibernate.ddl-auto`           | `update` auto-creates/updates tables from entities                 |
| `jwt.secret`                              | Secret key used to sign JWT tokens (min. 256-bit / 32+ characters) |
| `jwt.expiration`                          | Token validity in milliseconds (default: 24 hours)                 |

### 4. Run the application

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

(Windows: `.\mvnw.cmd spring-boot:run`)

The application starts on **http://localhost:8080**.

### 5. Seed data

On first startup, two test accounts are automatically created:

| Username | Password   | Role  |
| -------- | ---------- | ----- |
| `admin`  | `admin123` | ADMIN |
| `user`   | `user123`  | USER  |

## API Documentation

Once the application is running, Swagger UI is available at:
