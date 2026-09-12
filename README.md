# Resource Booking System

A RESTful Resource Booking System built with Spring Boot, Java 21, Spring Security, JWT, and MySQL. Users can view available resources and manage their own reservations, while administrators have full control over resources and all reservations.

## Tech Stack

- Java 21
- Spring Boot 4.1.1
- Spring Security (JWT-based, stateless)
- Spring Data JPA / Hibernate
- MySQL
- H2 (in-memory database, used only for automated tests)
- Swagger / OpenAPI (springdoc)
- Maven

## Features

- JWT-based authentication (`POST /auth/login`)
- Role-based access control: `ADMIN` and `USER`
- ADMIN: full CRUD on resources and reservations
- USER: read-only access to resources, can create and view only their own reservations
- User identity for reservations is always derived from the JWT token, never from the request body
- Reservation statuses: `PENDING`, `CONFIRMED`, `CANCELLED`
- Reservation price stored as a precise decimal value
- Filtering reservations by status, minimum price, and maximum price
- Pagination and optional sorting on reservation listings
- Centralized validation and error handling
- Auto-generated API documentation via Swagger UI

## Prerequisites

- Java 21 (JDK)
- Maven (or use the included Maven Wrapper — no separate install needed)
- MySQL Server (running locally, or update the connection URL for a remote instance)

## Setup Instructions

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd bookingsystem
```

### 2. Create the MySQL database

```sql
CREATE DATABASE booking_system;
```

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
