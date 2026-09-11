# Student Management System

A full-stack web application built with **Spring Boot / Java 21**, **Spring Data JPA**, **Thymeleaf**, **Bootstrap**, and **MySQL** for managing student records, departments, enrollments, and academic administration.

---

## 🚀 Features

- **Student Management:** View, create, update, and remove student profiles.
- **Search & Pagination:** Live filtering and paginated listings for handling large volumes of student records.
- **Department Allocation:** Organize students into designated academic departments.
- **Interactive UI:** Thymeleaf server-side templates styled with clean, responsive CSS and Bootstrap.
- **Database Seeding:** Pre-configured SQL seed scripts (`seed_maharashtra_students.sql`) for instant local testing.

---

## 🛠️ Tech Stack

- **Backend:** Spring Boot (Java 21), Spring Data JPA / Hibernate
- **Database:** MySQL
- **Frontend / Templating:** Thymeleaf, HTML5, CSS3, Bootstrap
- **Build Tool:** Maven (`mvnw`)

---

## ⚙️ Database Configuration

Configured in `src/main/resources/application.properties`:

```properties
spring.application.name=StudentMgtSystem
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3307/std_mgt_system
spring.datasource.username=root
spring.datasource.password=ganesh
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

*(Note: Adjust the port and credentials to match your local MySQL configuration if needed).*

---

## 🏃 How to Run

1. Make sure MySQL is running and the database `std_mgt_system` exists:
   ```sql
   CREATE DATABASE IF NOT EXISTS std_mgt_system;
   ```
2. Optional: Seed initial data using `seed_maharashtra_students.sql`.
3. Run the application via Maven:
   ```bash
   # Windows
   .\mvnw.cmd spring-boot:run
   ```
4. Access the web interface in your browser at:
   ```
   http://localhost:8080
   ```
