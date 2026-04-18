# HMCTS Developer Challenge – Task Manager

## Overview

A lightweight **Task Management API** built using **Spring Boot (Java 21)** that allows users to create, manage, and track tasks.

---

## 🚀 Tech Stack

* Java 21
* Spring Boot 3.x
* Spring Web / Spring Data JPA
* H2 (in-memory database)
* JUnit 5 + Mockito (testing)
* Logback (structured logging)
* Swagger (OpenAPI)

---

## 🧱 Architecture

The application follows a **layered architecture**:

```
Controller → Service → Repository → Database
              ↓
            DTOs
```

### Key Principles

* Separation of concerns
* DTOs (records) for immutability
* Centralised exception handling
* Stateless REST API

---

## 📦 Features

### Task Management

* Create task
* Retrieve task by ID
* List tasks (with pagination & filtering)
* Update task status
* Delete task

### Validation

* Title must not be blank
* Due date must be at least **2 days in the future**

### Status Model

```
TODO → IN_PROGRESS → DONE / FAILED
```

(No restriction enforced on transitions for simplicity)

---

## 🌐 API Endpoints

| Method | Endpoint           | Description                      |
| ------ | ------------------ | -------------------------------- |
| POST   | /tasks             | Create task                      |
| GET    | /tasks             | List tasks (pagination + filter) |
| GET    | /tasks/{id}        | Get task by ID                   |
| PATCH  | /tasks/{id}/status | Update task status               |
| DELETE | /tasks/{id}        | Delete task                      |

---

### Example: Create Task



## 📘 API Documentation

The API is documented using OpenAPI (Swagger).

Access via:
http://localhost:8080/swagger-ui.html

Features:
- Interactive API testing
- Request/response schemas
- Endpoint descriptions

### Example: Create Task
**Request**
```http
POST /tasks
Content-Type: application/json
{
    "title": "Trade processing",
    "description": "Process FX trades",
    "dueDate": "2026-04-25"
}
``` 
**Response**
```http
{
    "id": 1,
    "title": "Trade processing",
    "status": "TODO"
}
``` 

---
## 🔍 Pagination & Filtering

```
GET /tasks?page=0&size=15&status=TODO
```

Defaults:

* page = 0
* size = 15

---

## ⚠️ Error Handling

Centralised via `@RestControllerAdvice`.

Handled scenarios:

* Validation errors → 400
* Invalid JSON / enum → 400
* Task not found → 404
* Unexpected errors → 500

### Example Response

```json
{
  "timestamp": "2026-04-18T14:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "title must not be blank"
}
```

---

## 🧪 Testing

* Unit tests for **service layer**
* Controller tests using **MockMvc**
* Positive + negative scenarios covered

### Coverage

~85–90%

### Key Scenarios Tested

* Valid task creation
* Invalid input (validation failure)
* Task not found
* Invalid enum handling
* Delete operations

---

## 📊 Logging

* Structured logging using Logback
* Correlation ID added per request
* Layered logging (controller/service/exception)

Example:

```
2026-04-18 14:12:01.456 INFO TaskService [correlationId] - Saving task
```

---

## ▶️ Running the Application

### 1. Build

```
mvn clean install
```

### 2. Run

```
mvn spring-boot:run
```
## ⚡ Quick Start (Manual Testing)

1. Start the application:
   mvn spring-boot:run

2. Open Swagger UI:
   http://localhost:8080/swagger-ui.html

3. Open frontend UI:
   http://localhost:8080/index.html

4. Create a task using POST /tasks

5. Retrieve tasks using GET /tasks

This allows full end-to-end validation without additional setup.
---

## 🌐 Access
### H2 Console

http://localhost:8080/h2-console

```
JDBC URL: jdbc:h2:mem:taskdb
Username: sa
Password: (empty)
```

---

## 🧠 Design Decisions

### Why DTO Records?

* Immutability
* Reduced boilerplate
* Clear separation from domain

### Why H2?

* Simplicity for local execution
* No external dependencies

### Why Global Exception Handling?

* Consistent API responses
* Separation of error logic

### Why not Microservices?

* Scope of problem is small
* Focus on correctness over complexity

---

## ⚠️ Trade-offs

* No authentication (out of scope)
* No persistent DB (H2 used for simplicity)
* No distributed concerns (intentionally avoided)

---

## 🚀 Future Improvements

* Add authentication (JWT / OAuth2)
* Replace H2 with Postgres
* Add audit logging
* Introduce Docker support
* Add integration tests

---

## ✅ Summary

This solution prioritises:

* Clean architecture
* Correctness and validation
* Production-grade error handling
* High-quality test coverage
* Readable and maintainable code

---

## 👤 Note

Repository intentionally contains **no personal identifiers** in line with the name-blind recruitment process.
