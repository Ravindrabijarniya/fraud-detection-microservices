# 🛡️ Fraud Detection Microservices System

A distributed microservices-based system designed to detect fraudulent transactions in real time.  
The system uses event-driven architecture with Kafka, Redis caching, and multiple independent services.

---

## 🏗️ System Architecture

This project follows a **microservices architecture** where each service has a specific responsibility.


---

# 🚀 Tech Stack

| Technology | Purpose |
|------------|--------|
| **Spring Boot** | Backend framework |
| **Spring Security + JWT** | Authentication |
| **Apache Kafka** | Event streaming between services |
| **Redis** | Caching and fast lookups |
| **MySQL** | Data storage |
| **Docker & Docker Compose** | Containerized deployment |
| **Maven** | Dependency management |

---

# 📦 Microservices

### 1️⃣ API Gateway
Handles routing of requests to appropriate microservices.

**Port**
```
8080
```

Responsibilities:
- Request routing
- Entry point for clients

---

### 2️⃣ Auth Service

Handles authentication and authorization.

**Port**
```
8081
```

Features:
- User authentication
- JWT token generation
- User validation

---

### 3️⃣ Transaction Service

Processes financial transactions and publishes transaction events to Kafka.

**Port**
```
8082
```

Features:
- Transaction processing
- Kafka event publishing
- Database persistence

---

### 4️⃣ Fraud Engine Service

Consumes transaction events and runs fraud detection rules.

**Port**
```
8083
```

Features:
- Fraud detection logic
- Risk evaluation
- Event processing

---

### 5️⃣ Notification Service

Sends alerts for suspicious or fraudulent transactions.

**Port**
```
8084
```

Features:
- Fraud alerts
- Notification processing

---

# 🗄️ Database

MySQL is used as the primary database.

### Databases Created

```
auth_db
transaction_db
fraud_db
notification_db
```

Initialization script:

```
mysql/init/init.sql
```

---

# ⚡ Event Flow (Kafka)

```
Transaction Service
        │
        ▼
   Kafka Topic
        │
        ▼
Fraud Engine Service
        │
        ▼
Notification Service
```

---

# 🐳 Running the Project

Make sure you have:

- Docker
- Docker Compose
- Java 21

### Run the entire system

```bash
docker compose up --build
```

---

### Check running containers

```bash
docker ps
```

---

# 📊 Services and Ports

| Service | Port |
|------|------|
API Gateway | 8080 |
Auth Service | 8081 |
Transaction Service | 8082 |
Fraud Engine Service | 8083 |
Notification Service | 8084 |
Kafka | 9092 |
Redis | 6379 |
MySQL | 3307 |

---

# 📂 Project Structure

```
fraud-detection-microservices
│
├── api-gateway
├── auth-service
├── event-model
├── fraud-engine-service
├── notification-service
├── transaction-service
│
├── mysql
│   └── init
│       └── init.sql
│
└── docker-compose.yml
```

---

# 🔮 Future Improvements

- Add monitoring with **Prometheus + Grafana**
- Implement **Kafka retry and dead-letter queues**
- Add **rate limiting at API gateway**
- Add **distributed tracing (Zipkin)**

---

# 👨‍💻 Author

**Ravindra Bijarniya**

GitHub  
https://github.com/Ravindrabijarniya

```






---

💡 If you want, I can also show you **3 small changes that will make this repository look like a FAANG-level backend project (architecture diagram, badges, and API docs)**.
