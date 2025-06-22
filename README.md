# 🎥 StreamingApp

**Microservices-based Video Streaming Platform**  
Built using **Spring Boot (Backend)** and **React (Frontend)**

---

## 🚀 Overview

StreamingApp is a scalable and modular video streaming system designed using microservices architecture. It supports user authentication, video upload, and plans to enable real-time streaming.

Each service is independently deployable, discoverable via Eureka, and securely accessible via an API Gateway.

---

## 🧱 Tech Stack

### 💻 Backend (Spring Boot Microservices)
- Spring Boot 3.5.0
- Spring Cloud (Eureka, Gateway)
- Spring Security + JWT
- Spring Data JPA + MySQL
- Cloudinary SDK
- Maven Multi-Module Structure
- Java 17

### 🌐 Frontend
- React.js (Vite)
- Tailwind CSS (UI)
- Axios (API calls)

---

## 📂 Project Structure

StreamingApp/
├── backend/
│ ├── discovery-service
│ ├── api-gateway
│ ├── user-service
│ ├── video-upload-service
│ └── video-stream-service
├── frontend/ (React App)

---

## 🔧 Microservices Breakdown

### 🧭 1. Discovery Service (`discovery-service`)
- **Role**: Eureka Server for service registry
- **Tech**: Spring Cloud Netflix Eureka
- **Port**: `8761`

---

### 🚪 2. API Gateway (`api-gateway`)
- **Role**: Gateway to route requests to appropriate services
- **Tech**: Spring Cloud Gateway, Eureka Client
- **Port**: `8080`
- **Features**:
    - Request routing
    - Path rewriting
    - Load balancing

---

### 👤 3. User Service (`user-service`)
- **Role**: Handles user authentication & authorization
- **Tech**: Spring Security, JWT
- **Port**: `8081`
- **Features**:
    - Role-based auth (Student/Admin)
    - Signup / Login / Token management
    - Secure endpoints with JWT

---

### ⬆️ 4. Video Upload Service (`video-upload-service`)
- **Role**: Upload videos and store metadata
- **Tech**: Spring Data JPA, Cloudinary, MySQL
- **Port**: `8082`
- **Features**:
    - Multipart video upload
    - Store file metadata in MySQL
    - Upload to Cloudinary

---

### 📺 5. Video Stream Service (`video-stream-service`)
- **Role**: (Coming Soon) Serve video content on-demand
- **Status**: To be implemented

---

## 🧪 How to Run

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL (configured for `video-upload-service`)
- Cloudinary API credentials

### Steps
```
# Clone the repo
git clone https://github.com/anmol-45/StreamingApp.git
cd StreamingApp

# Build all microservices
mvn clean install

# Run discovery service
mvn spring-boot:run -pl backend/discovery-service

# Run other services similarly
mvn spring-boot:run -pl backend/api-gateway
mvn spring-boot:run -pl backend/user-service
mvn spring-boot:run -pl backend/video-upload-service

Each backend module is a Spring Boot microservice under a Maven multi-module project. This design supports clean separation of concerns and enables independent scaling and deployment.

---

## 📌 Services Overview

### 1️⃣ Discovery Service (`discovery-service`)
**Purpose**: Acts as a **Service Registry** using Spring Netflix Eureka Server.

**Tech Stack**:
- Spring Boot
- Spring Cloud Netflix Eureka Server
- Java 17

**Features**:
- Registers and tracks available services
- Enables service discovery for microservices (client-side load balancing)

**Run Command**:
```
mvn spring-boot:run -pl discovery-service
2️⃣ API Gateway (api-gateway)
Purpose: Central entry point to all services using Spring Cloud Gateway.

Tech Stack:

Spring Boot

Spring Cloud Gateway

Eureka Client

Java 17

Features:

Routing incoming requests to appropriate microservices

Global exception handling & fallback routes

Token validation logic (planned)

Acts as reverse proxy and supports path rewriting

Run Command:

mvn spring-boot:run -pl api-gateway
3️⃣ User Service (user-service)
Purpose: Handles all user-related operations such as registration, login, and profile management.

Tech Stack:

Spring Boot

Spring Security (JWT-based Authentication)

Spring Web

Lombok

Java 17

Features:

Google OAuth2 Login

JWT Token Generation

Role-based access (Student / Admin / etc.)

Token refresh logic (optional)

Profile APIs

Endpoints:

pgsql
Copy
Edit
POST /api/v1/user/signup         - Register user
POST /api/v1/user/login          - Login with credentials
GET  /api/v1/user/profile        - Fetch user profile
Run Command:

mvn spring-boot:run -pl user-service
4️⃣ Video Upload Service (video-upload-service)
Purpose: Allows uploading of videos to the platform with metadata.

Tech Stack:

Spring Boot

Spring Web

Spring Data JPA (MySQL)

Cloudinary for media storage

Lombok

Java 17

Features:

Upload videos using multipart requests

Store video metadata in MySQL

Upload media to Cloudinary

REST APIs for managing video data

Cloudinary Integration:

Uploads videos directly to Cloudinary

Returns public URLs to the client

Entity Example: Video, Course

Sample Endpoint:

POST /api/v1/videos/upload       - Upload new video
GET  /api/v1/videos/{id}         - Fetch video metadata
GET  /api/v1/videos/course/{id}  - List all videos in a course
Run Command:

mvn spring-boot:run -pl video-upload-service
5️⃣ Video Stream Service (video-stream-service)
🚧 WIP — This service will provide on-demand streaming functionality for uploaded videos.

Planned Features:

Chunked video response support

Adaptive streaming using HLS/DASH

Authenticated video access

🚀 Getting Started
Clone the Repository

git clone https://github.com/anmol-45/StreamingApp.git
cd StreamingApp
Build All Services

mvn clean install
Run Individual Services

# Discovery Server
mvn spring-boot:run -pl backend/discovery-service

# API Gateway
mvn spring-boot:run -pl backend/api-gateway

# User Service
mvn spring-boot:run -pl backend/user-service

# Video Upload Service
mvn spring-boot:run -pl backend/video-upload-service
📁 Folder Structure (Backend)
pgsql
Copy
Edit
backend/
├── pom.xml                      <-- Parent POM
├── discovery-service/
├── api-gateway/
├── user-service/
├── video-upload-service/
└── video-stream-service/
📌 Prerequisites
Java 17+

Maven 3.6+

MySQL (for video-upload-service)

Cloudinary account (with credentials in application.properties)

🔐 Future Enhancements
Role-based permissions using Spring Security

Service-to-service communication using Feign clients

CI/CD using GitHub Actions

Docker & K8s deployment

🤝 Contributing
Feel free to fork the repo and raise PRs. For larger changes, open an issue first.

📜 License
MIT License © 2025 Anmol Kumar & Prashant Kumar Aryan

---

Let me know if you want:
- Separate README for each submodule
- Badges (build status, license, etc.)
- A prettier version for GitHub Pages

I'm happy to help!
