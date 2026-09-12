# Resource Booking System

A RESTful API for booking resources (rooms, vehicles, equipment) built with Spring Boot, Java 21, Spring Security, JWT, and MySQL.

## Features

- JWT-based authentication (`POST /auth/login`)
- Role-based access control (ADMIN / USER)
- ADMIN: full CRUD on resources and reservations
- USER: read-only access to resources, create/view own reservations
- User identity derived from JWT (never trusted from request body)
- Reservation statuses: PENDING, CONFIRMED, CANCELLED
- Dynamic filtering by status, minPrice, maxPrice
- Pagination and sorting
- Global exception handling with clean JSON error responses
- Swagger/OpenAPI documentation

## Tech Stack

- Java 21
- Spring Boot 4.1.1
- Spring Security + JWT (jjwt 0.13.0)
- Spring Data JPA / Hibernate
- MySQL
- Lombok
- springdoc-openapi (Swagger UI)

## Prerequisites

- Java 21 or higher
- Maven (wrapper included, no separate install needed)
- MySQL 8.x running locally

## Setup Instructions

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd bookingsystem
```

### 2. Create the database

```sql
CREATE DATABASE booking_system;
```

### 3. Configure environment

Edit `src/main/resources/application.properties` (or set environment variables) with your local MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/booking_system
spring.datasource.username=root
spring.datasource.password=your_mysql_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=your_long_random_secret_key_at_least_32_characters
jwt.expiration=86400000
```

| Variable                     | Description                            | Example                                      |
| ---------------------------- | -------------------------------------- | -------------------------------------------- |
| `spring.datasource.url`      | MySQL JDBC URL                         | `jdbc:mysql://localhost:3306/booking_system` |
| `spring.datasource.username` | MySQL username                         | `root`                                       |
| `spring.datasource.password` | MySQL password                         | —                                            |
| `jwt.secret`                 | HMAC signing key for JWT (256-bit min) | random hex string                            |
| `jwt.expiration`             | Token validity in milliseconds         | `86400000` (24h)                             |

### 4. Run the application

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
.\mvnw.cmd spring-boot:run
```

The app starts on `http://localhost:8080`.

### 5. Seed users

On first run, two test accounts are automatically created:

| Username | Password   | Role  |
| -------- | ---------- | ----- |
| `admin`  | `admin123` | ADMIN |
| `user`   | `user123`  | USER  |

## API Documentation

Swagger UI: `http://localhost:8080/swagger-ui.html`

To authorize in Swagger:

1. Call `POST /auth/login` with one of the seed accounts
2. Copy the returned `token`
3. Click **Authorize** (top right), paste the token (no "Bearer " prefix needed), click Authorize

## Key Endpoints

| Method | Endpoint                    | Access                 | Description                                                                                                        |
| ------ | --------------------------- | ---------------------- | ------------------------------------------------------------------------------------------------------------------ |
| POST   | `/auth/login`               | Public                 | Login, returns JWT                                                                                                 |
| GET    | `/resources`                | Any authenticated user | List resources                                                                                                     |
| POST   | `/resources`                | ADMIN only             | Create resource                                                                                                    |
| PUT    | `/resources/{id}`           | ADMIN only             | Update resource                                                                                                    |
| DELETE | `/resources/{id}`           | ADMIN only             | Delete resource                                                                                                    |
| POST   | `/reservations`             | Any authenticated user | Create reservation (user identity from JWT)                                                                        |
| GET    | `/reservations`             | Any authenticated user | List reservations (own for USER, all for ADMIN); supports `status`, `minPrice`, `maxPrice`, `page`, `size`, `sort` |
| GET    | `/reservations/{id}`        | Owner or ADMIN         | Get single reservation                                                                                             |
| PUT    | `/reservations/{id}/status` | ADMIN only             | Update reservation status                                                                                          |
| DELETE | `/reservations/{id}`        | ADMIN only             | Delete reservation                                                                                                 |

## Running Tests

```bash
./mvnw test
```

Test coverage includes:

- Login success/failure (`AuthControllerTest`)
- RBAC enforcement — USER blocked from creating resources, ADMIN allowed (`ReservationSecurityTest`)
- Unauthenticated request handling

## Project Structure

```
src/main/java/com/anmol/bookingsystem/
├── entity/        # JPA entities (User, Resource, Reservation, enums)
├── repository/     # Spring Data JPA repositories
├── service/        # Business logic
├── controller/      # REST controllers
├── dto/           # Request/response DTOs
├── security/        # JWT, filters, security config
├── exception/       # Global exception handling
└── config/         # Swagger config, seed data
```
