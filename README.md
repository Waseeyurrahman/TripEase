# TripEase – Cab Booking Backend

A secure, production-deployed cab booking backend built with Java and Spring Boot. Features JWT-based authentication, role-based access control, a multi-step booking workflow, and a fully automated CI/CD pipeline.

🔗 **Live API:** [Swagger UI](http://13.60.209.170:8080/swagger-ui/index.html)  
🐳 **Docker Hub:** [waseeyurrahman/tripease-app](https://hub.docker.com/)  
☁️ **Deployed on:** AWS EC2 + Amazon RDS (MySQL)

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [API Modules](#api-modules)
- [Booking Workflow](#booking-workflow)
- [Security](#security)
- [Database Design](#database-design)
- [CI/CD Pipeline](#cicd-pipeline)
- [Getting Started](#getting-started)
- [Environment Configuration](#environment-configuration)
- [Running with Docker](#running-with-docker)

---

## Features

- **JWT Authentication** — Stateless token-based auth with BCrypt password hashing
- **Role-Based Access Control** — USER and ADMIN roles with method-level security via `@EnableMethodSecurity`
- **Multi-Step Booking Workflow** — Customer validation → random cab selection → driver assignment → booking persistence → email confirmation
- **Real-Time Cab Availability** — Cab availability updated atomically on booking and cancellation
- **Email Notifications** — Booking confirmation emails via JavaMailSender (SMTP)
- **Custom JPA Queries** — Native SQL and HQL for cab availability and driver lookup
- **Centralized Exception Handling** — Global error responses via `@ControllerAdvice`
- **API Documentation** — Full Swagger/OpenAPI documentation at `/swagger-ui/index.html`
- **Containerized Deployment** — Docker + AWS EC2 with RDS-managed MySQL
- **CI/CD Pipeline** — 5-step GitHub Actions pipeline on every push to main

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security, JWT, BCrypt |
| ORM | Spring Data JPA (Hibernate) |
| Database | MySQL (AWS RDS) |
| Containerization | Docker |
| CI/CD | GitHub Actions |
| Cloud | AWS EC2, AWS RDS |
| Documentation | Swagger (Springdoc OpenAPI) |
| Email | JavaMailSender (SMTP) |
| Build Tool | Maven |

---

## Architecture

```
┌─────────────────────────────────────────────┐
│                  Client                     │
└──────────────────┬──────────────────────────┘
                   │ HTTP Request
┌──────────────────▼──────────────────────────┐
│           JWT Auth Filter                   │
│     (validates token on every request)      │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│              Controller Layer               │
│  AuthController | BookingController         │
│  CustomerController | DriverController      │
│  CabController                              │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│              Service Layer                  │
│  BookingService | CustomerService           │
│  DriverService  | CabService                │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│           Repository Layer                  │
│     Spring Data JPA + Custom Queries        │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│         MySQL Database (AWS RDS)            │
└─────────────────────────────────────────────┘
```

**Design Patterns Used:**
- Controller → Service → Repository layered architecture
- Transformer pattern for entity-to-DTO conversion (4 transformer classes)
- Builder pattern via Lombok `@Builder`

---

## API Modules

### Auth — `/auth` (Public)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login and receive JWT token |

### Customer — `/customer`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/customer/add` | Register new customer (public) |
| GET | `/customer/get/customer-id/{id}` | Get customer by ID |
| GET | `/customer/get/gender/{gender}` | Get customers by gender |
| GET | `/customer/get` | Get all customers |
| GET | `/customer/get-by-age-greater-than` | Filter by age |

### Driver — `/driver`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/driver/add` | Add new driver |
| GET | `/driver/all` | Get all drivers |
| GET | `/driver/get/{id}` | Get driver by ID |

### Cab — `/cab`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/cab/register/driver/{driverid}` | Register cab for driver |
| GET | `/cab/all` | Get all cabs |

### Booking — `/booking`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/booking/book/customer/{customerid}` | Book a cab |
| GET | `/booking/get/{id}` | Get booking by ID |
| DELETE | `/booking/cancel/{id}` | Cancel booking |
| GET | `/booking/customer/{customerId}` | Get bookings by customer |
| GET | `/booking/all` | Get all bookings (ADMIN) |
| PUT | `/booking/status/{bookingId}` | Update trip status (ADMIN) |

> 3 public endpoints, 15 JWT-secured endpoints

---

## Booking Workflow

Every booking request goes through a 10-step workflow:

```
1.  Validate customer exists in database
         ↓
2.  Query available cab randomly (native SQL: ORDER BY RAND())
         ↓
3.  Throw CabUnavailableException if no cabs found
         ↓
4.  Fetch assigned driver for selected cab
         ↓
5.  Create Booking entity from request + per-km rate
         ↓
6.  Set entity relationships (Customer, Cab, Driver)
         ↓
7.  Persist booking to database
         ↓
8.  Update cab availability → false (atomically)
         ↓
9.  Maintain bidirectional consistency on Customer and Driver
         ↓
10. Send booking confirmation email via JavaMailSender
         ↓
    Return BookingResponse DTO
```

**Cancellation Flow:**
```
1. Fetch booking by ID
2. Set cab.available = true
3. Delete booking from database
```

---

## Security

**Authentication Flow:**
```
POST /auth/login
  → Validate credentials
  → Generate JWT token (signed with secret key)
  → Return token to client

Subsequent requests:
  → Client sends: Authorization: Bearer <token>
  → JwtAuthFilter validates token
  → Sets SecurityContext
  → Request proceeds to controller
```

**Role Permissions:**

| Endpoint | USER | ADMIN |
|---|---|---|
| Book cab | ✅ | ✅ |
| View own bookings | ✅ | ✅ |
| View all bookings | ❌ | ✅ |
| Update trip status | ❌ | ✅ |
| Register customer | ✅ (public) | ✅ |

**Security implementation:**
- Passwords hashed with BCrypt
- JWT stored stateless (no server-side session)
- Method-level security via `@EnableMethodSecurity`
- CSRF disabled (stateless REST API)

---

## Database Design

**5 Entities with the following relationships:**

```
User
 └── credentials for auth

Customer
 └── OneToMany → Booking

Driver
 ├── OneToOne  → Cab
 └── OneToMany → Booking

Cab
 └── (available: boolean — toggled on booking/cancel)

Booking
 ├── ManyToOne → Customer
 ├── ManyToOne → Driver
 └── ManyToOne → Cab
```

**Custom Queries:**
```java
// Native SQL — random available cab
SELECT * FROM cab WHERE available = true ORDER BY RAND() LIMIT 1

// Native SQL — driver by cab
SELECT * FROM driver WHERE cab_id = :cabId

// HQL — customers by gender and age
SELECT c FROM Customer c 
WHERE c.gender = :gender AND c.age > :age
```

---

## CI/CD Pipeline

**5-step GitHub Actions pipeline** triggered on every push to `main`:

```yaml
1. Checkout Code          (actions/checkout@v4)
2. Set up Java 17         (actions/setup-java@v4 + Maven cache)
3. Build & Test           (mvn clean install)
4. Build Docker Image     (docker build)
5. Push to Docker Hub     (docker push)
```

Pipeline status: ✅ 4/4 runs passing

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+ (or use Docker)
- Git

### Clone the Repository
```bash
git clone https://github.com/Waseeyurrahman/TripEase.git
cd TripEase
```

---

## Environment Configuration

Create `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/tripease
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=YOUR_JWT_SECRET_KEY

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL
spring.mail.password=YOUR_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> ⚠️ Never commit `application.properties` to version control. It is listed in `.gitignore`.

### Build and Run
```bash
mvn clean install
mvn spring-boot:run
```

Access Swagger UI at: `http://localhost:8080/swagger-ui/index.html`

---

## Running with Docker

### Using Docker directly
```bash
# Build image
docker build -t tripease-app .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://your-db-host:3306/tripease \
  -e SPRING_DATASOURCE_USERNAME=your_username \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  tripease-app
```

### Pull from Docker Hub
```bash
docker pull waseeyurrahman/tripease-app
docker run -p 8080:8080 waseeyurrahman/tripease-app
```

---

## Project Structure

```
src/
├── main/java/com/example/TripEase/
│   ├── config/          # SecurityConfig, SwaggerConfig
│   ├── controller/      # AuthController, BookingController,
│   │                      CabController, CustomerController,
│   │                      DriverController
│   ├── dto/
│   │   ├── request/     # AuthRequest, BookingRequest,
│   │   │                  CabRequest, CustomerRequest,
│   │   │                  DriverRequest
│   │   └── response/    # BookingResponse, CabResponse,
│   │                      CustomerResponse, DriverResponse
│   ├── Enum/            # Gender, Role, TripStatus
│   ├── exception/       # GlobalExceptionHandler,
│   │                      CabUnavailableException,
│   │                      CustomerNotFoundException,
│   │                      DriverNotFoundException
│   ├── model/           # Booking, Cab, Customer, Driver, User
│   ├── repository/      # 5 JPA repositories
│   ├── security/        # JwtAuthFilter, JwtUtil,
│   │                      CustomUserDetailsService
│   ├── service/         # BookingService, CabService,
│   │                      CustomerService, DriverService
│   └── transformer/     # BookingTransformer, CabTransformer,
│                          CustomerTransformer, DriverTransformer
└── test/
    └── BookingServiceTest.java
```

---

## Author

**MD Waseeyur Rahman**  
Java Backend Developer  
📧 waseeyurrahman@gmail.com  
🔗 [LinkedIn](https://linkedin.com/in/waseeyur)  
🐙 [GitHub](https://github.com/Waseeyurrahman)
