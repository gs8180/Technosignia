# Legal Contract Document Modification System
**A digital platform for contract lifecycle, clause modification, approval workflow, version control, and audit tracking.**

Built with **Java 21**, **Spring Boot 3**, **MySQL**, **Spring Security (JWT)**, and **Angular 17**.

---

## Architecture Overview

```mermaid
flowchart TD
    subgraph Frontend["Angular 17 Web Application (Port 4200)"]
        A[Login & JWT Session] --> B[Role-Based Navbar]
        B --> C[Dashboard with Metrics & KPIs]
        B --> D[Contract Management & Search]
        B --> E[Clause Ordering & Document Attachments]
        B --> F[Modification Requests & Side-by-Side Diff]
        B --> G[Approver Dashboard (Approve / Reject Workflow)]
        B --> H[Version History Snapshot Viewer]
        B --> I[System Audit Trail Log]
        B --> J[Admin User & Role Management]
    end

    subgraph Backend["Spring Boot 3 REST API (Port 8080)"]
        K[Spring Security & JWT Filter] --> L[Auth & Users API]
        K --> M[Contracts API]
        K --> N[Clauses API with Ordering]
        K --> O[Documents API with Upload & Download]
        K --> P[Modifications API & Approval Workflow]
        K --> Q[Contract Versions API]
        K --> R[Audit Logs API]
    end

    subgraph Database["MySQL Database (Port 3307 / 3306)"]
        S[(contract_db)]
    end

    Frontend -->|REST / JSON + Bearer Token| Backend
    Backend -->|Spring Data JPA / Hibernate| Database
```

---

## Tasks 1 to 11 Implementation Summary

| Task | Module | Implementation Details |
|------|--------|------------------------|
| **Task 1** | Project Overview | Digital contract workflow: Upload → Review → Modify Clause → Save New Version → Approval Workflow → Audit History. |
| **Task 2** | Project Setup & Git | Clean modular structure: `backend/` (Spring Boot 3 + Maven), `frontend/` (Angular 17), Git repository initialized. |
| **Task 3** | Database & Entity Design | Entities designed: `User`, `Role`, `Contract`, `ContractStatus`, `Document`, `Clause`, `ModificationRequest`, `ContractVersion`, `AuditLog`. |
| **Task 4** | Models & CRUD APIs | Full Spring Data JPA repositories, services, and REST controllers with DTOs and validation. |
| **Task 5** | Authentication & RBAC | Spring Security + JJWT with BCrypt password hashing, token filter, route guards (`authGuard`), and role-based UI. |
| **Task 6** | User & Role Management | Admin panel for managing users, editing details, assigning roles, and toggling active/inactive status. |
| **Task 7** | Contract Management | Contract CRUD supporting statuses: `DRAFT`, `ACTIVE`, `EXPIRED`, `TERMINATED`. |
| **Task 8** | Search, Filter & Document Upload | Live keyword search (title, number, description), status filtering dropdown, file upload attachments to contracts. |
| **Task 9** | Document, Clause & Modification | Multi-document upload/preview/download, clause ordering (Move Up/Down), and proposal creation (Contract or Clause level). |
| **Task 10** | Diff Viewer, Workflow & Approvals | Side-by-side Diff comparison highlighting exact differences, Approver Dashboard, Approve with comments, Reject with mandatory reason. |
| **Task 11** | Version Management & Audit Log | Automatic version snapshotting (v1.0 → v1.1) on approval, snapshot clause inspector, and full system activity audit trail. |

---

## Pre-Configured Demo Accounts

The system automatically initializes these demo accounts upon first startup:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| `admin` | `password123` | **ROLE_ADMIN** | Full system access: users, contracts, approvals, audit logs |
| `manager` | `password123` | **ROLE_CONTRACT_MANAGER** | Contract manager: create contracts, attach documents, organize clauses |
| `approver` | `password123` | **ROLE_APPROVER** | Legal reviewer: inspect side-by-side diffs, approve/reject changes |
| `user` | `password123` | **ROLE_USER** | Client representative: view contracts, propose clause modifications |

> **Tip:** The login page features **1-Click Quick Fill** buttons for each role for instant testing.

---

## Database Configuration

In `backend/src/main/resources/application.properties`:
```properties
# MySQL configuration (configured for MySQL port 3307 and pass@123)
spring.datasource.url=jdbc:mysql://localhost:3307/contract_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=pass@123
spring.jpa.hibernate.ddl-auto=update
```

*(Note: If your MySQL service uses standard port `3306`, simply update `3307` to `3306` in `application.properties`).*

*(Optional In-Memory H2 Profile: Run with `--spring.profiles.active=h2` to use built-in H2 database).*

---

## How to Run the Application

### 1. Run the Spring Boot Backend
Open a terminal in the `backend/` directory:
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Or run with H2 fallback profile
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2
```
Backend will start on `http://localhost:8080`.

### 2. Run the Angular Frontend
Open a second terminal in the `frontend/` directory:
```bash
npm start
```
Frontend will be available at `http://localhost:4200`.

---

## Step-by-Step Testing Workflow

1. **Login:** Navigate to `http://localhost:4200/login` and click **Approver** or **Manager** quick-fill button.
2. **Contract Management:** Go to **Contracts**, create a contract or open the pre-seeded agreement `CNT-2026-001`.
3. **Clauses:** Open **Clauses** tab, re-order clauses with the ▲/▼ buttons, or add new clauses.
4. **Documents:** Go to **Documents** tab, upload a document attachment, and download it.
5. **Modification Request:** Click **Suggest Edit** on a clause, propose a new wording and justification reason, and submit.
6. **Approver Workflow & Diff:** Log in as `approver`, go to **Approvals**, click **Review**, view the **Side-by-Side Diff comparison**, and click **Approve Modification** with a comment.
7. **Version Snapshot & Audit:** Notice the contract version automatically increments to `v1.1`, the change snapshot is archived in the **Version History** tab, and the full event is logged in the **Audit Trail**.

---

## Internship & Developer Details

### 🚀 Domain
Full Stack Java Developer

### 👨‍💻 About Me
Passionate Full Stack Java Developer with a strong interest in building scalable web applications and learning modern software development technologies. I enjoy solving problems, developing projects, and continuously improving my technical skills.

### 🛠️ Skills
- HTML / CSS / JavaScript
- Core Java & Advanced Java
- Hibernate & Spring Boot
- Angular & Bootstrap
- MySQL & REST APIs
- Git & GitHub

### 🎯 Career Goal
To become a skilled Full Stack Java Developer and build high-quality web applications using Java, Spring Boot, Angular, and modern technologies while contributing to innovative software projects.

### 🌱 Currently Learning
- Advanced Spring Boot & Microservices
- REST APIs & System Design
- Data Structures & Algorithms
