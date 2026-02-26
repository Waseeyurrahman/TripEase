TripEase – Cab Booking Backend Application

TripEase is a Spring Boot–based backend application that provides a secure, scalable, and modular cab booking system. The project is built using industry-standard backend practices, including JWT authentication, role-based authorization, and a layered architecture.

📌 **Features**
🔐 Authentication & Security
User registration and login using JWT
Role-based access control (ADMIN, USER)
Password encryption using BCrypt
Stateless authentication with Spring Security


** Customer Management**

Customer registration
Fetch customers by:
ID
Gender
Age
Gender + Age
Gender + Age greater than a value

 **Driver & Cab Management**

Driver onboarding
Cab registration and assignment to drivers
Real-time cab availability handling

 **Booking System**

Book a cab for a customer
Cancel bookings
View bookings by customer
View all bookings (ADMIN)
Update trip status (ADMIN)

📧 Email Notifications

Automatic booking confirmation emails using SMTP (Gmail)

📄 API Documentation

Swagger / OpenAPI integration for easy API testing

🛠️** Tech Stack**

Language: Java
Framework: Spring Boot
Security: Spring Security, JWT
Database: MySQL
ORM: Spring Data JPA (Hibernate)
Validation: Jakarta Bean Validation
Build Tool: Maven
Documentation: Swagger (Springdoc OpenAPI)

🧱 Project Architecture
Controller → Service → Repository

DTOs for request/response handling
Transformers for entity–DTO conversion
Centralized exception handling
Stateless JWT-based authentication

🔑 Roles & Permissions
Role	Access
USER	Book cabs, view own bookings
ADMIN	View all bookings, update trip status


🚀 Getting Started
Prerequisites
Java 17+
Maven
MySQL
Git



🔧 Configuration

Create a file named application.properties (not committed to Git):

spring.datasource.url=jdbc:mysql://localhost:3306/tripease
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

spring.jpa.hibernate.ddl-auto=update

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL
spring.mail.password=YOUR_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true


🔍 **Swagger UI**

Access API documentation at:
http://localhost:8080/swagger-ui/index.html


🧪** API Flow (Basic)**

Register user/admin
Login to receive JWT token
Use token for secured APIs
Book cabs and manage trips


📈 Future Enhancements

Fare estimation before booking
Driver rating system
Payment gateway integration
Ride history analytics

