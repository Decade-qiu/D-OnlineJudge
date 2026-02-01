# D-OnlineJudge

[![Java](https://img.shields.io/badge/java-17-blue)](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) [![Spring Boot](https://img.shields.io/badge/spring--boot-2.7.12-green)](https://spring.io/projects/spring-boot) [![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2021.0.3-green)](https://spring.io/projects/spring-cloud) [![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud%20Gateway-3.1.5-green)](https://spring.io/projects/spring-cloud-gateway) [![OpenFeign](https://img.shields.io/badge/OpenFeign-3.1.5-green)](https://github.com/OpenFeign/feign) [![Alibaba Nacos](https://img.shields.io/badge/Alibaba%20Nacos-2.5.1-green)](https://nacos.io/) [![Redis](https://img.shields.io/badge/Redis-7.0-red)](https://redis.io/) [![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3--management-orange)](https://www.rabbitmq.com/) [![Elasticsearch](https://img.shields.io/badge/Elasticsearch-7.17.6-yellow)](https://www.elastic.co/) [![Sentinel](https://img.shields.io/badge/Sentinel-1.8.6-green)](https://github.com/alibaba/Sentinel) [![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/) [![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.2-green)](https://baomidou.com/) [![Vue.js](https://img.shields.io/badge/vue.js-3.3%2B-green)](https://vuejs.org/) [![TypeScript](https://img.shields.io/badge/TypeScript-5.0%2B-blue)](https://www.typescriptlang.org/) [![Element Plus](https://img.shields.io/badge/Element%20Plus-2.5%2B-green)](https://element-plus.org/) [![Docker](https://img.shields.io/badge/docker-20.10%2B-blue)](https://www.docker.com/) [![SkyWalking](https://img.shields.io/badge/SkyWalking-8.0%2B-purple)](https://skywalking.apache.org/) [![Prometheus](https://img.shields.io/badge/Prometheus-2.40%2B-blue)](https://prometheus.io/) [![License](https://img.shields.io/badge/license-Apache-blue.svg)](LICENSE)

<div align="center">
<p align="center">
  <img src="./docs/welcome.jpg" width="48%">
  <img src="./docs/code.jpg" width="48%"/>
</p>
</div>

## 📖 Overview

**D-OnlineJudge** is a full-stack online coding and competitive programming platform built with **Java Spring Cloud microservices architecture** and **Vue 3 frontend framework**.

It provides a secure, scalable, and feature-rich environment for programming competitions, code submissions, and real-time verdict feedback.

### ✨ Key Features

- **🏗️ Microservices Architecture**: Spring Cloud-based modular design with independent deployable services, high cohesion, and loose coupling
- **🔒 Secure Sandbox Environment**: Docker containerization provides isolated execution environments to prevent malicious code attacks
- **⚡ Asynchronous Judging**: Redis task queues + RabbitMQ event-driven architecture for high-performance, non-blocking code execution
- **🔐 Enterprise-Grade Authentication**: Long-lived and short-lived token mechanism (Access Token + Refresh Token) with Redis-backed session management
- **📊 Full-Stack Observability**: SkyWalking distributed tracing, Prometheus metrics monitoring, and Loki log aggregation for complete system visibility
- **🚀 Automated CI/CD Pipeline**: GitHub Actions workflow for continuous integration, building, and deployment
- **🔍 Intelligent Full-Text Search**: Elasticsearch with IK Chinese tokenizer for millisecond-level problem search
- **💾 Three-Tier Cache Architecture**: Local cache (Caffeine) + distributed cache (Redis) + database, dramatically reducing database load
- **🌐 Real-Time WebSocket Notifications**: Global push system for real-time verdict feedback without page blocking
- **🛡️ Traffic Management**: Alibaba Sentinel integration for rate limiting, circuit breaking, and system protection

---

## 🚀 Quick Start

This guide will help you set up and run D-OnlineJudge locally. Follow the steps below.

### Prerequisites

Ensure you have the following software installed:

| Software | Version | Purpose |
| :--- | :--- | :--- |
| **JDK** | 17+ | Backend compilation and runtime |
| **Maven** | 3.6+ | Project build tool |
| **Docker** | 20.10+ | Containerization |
| **Docker Compose** | 1.29+ | Container orchestration |
| **Git** | 2.0+ | Version control |
| **Node.js** | 16+ (optional) | Frontend development |
| **Pnpm** | 8+ (optional) | Frontend package manager |

### Step 1: Clone the Repository

```bash
git clone https://github.com/yourusername/D-OnlineJudge.git
cd D-OnlineJudge
```

### Step 2: Backend Setup (`DOJ-BE`)

#### 2.1 Start Core Dependency Services

We provide complete Docker Compose configurations to start all dependencies with a single command:

```bash
# Create Docker network
docker network create doj

# Start all core services (MySQL, Redis, RabbitMQ, Nacos, Elasticsearch, etc.)
# For detailed commands, see docs/0.build.md section "2. Deploying Core Dependency Services"
```

#### 2.2 Configure Nacos

1. Access Nacos console: `http://localhost:8848/nacos` (default: `nacos`/`nacos`)
2. Navigate to "Configuration Management" → "Configuration List"
3. Create the following shared configuration files:

| Data ID | Description |
| :--- | :--- |
| `shared-jdbc.yaml` | Database and cache connection settings |
| `shared-swagger.yaml` | API documentation and logging config |
| `shared-jwt.yaml` | JWT authentication key configuration |
| `shared-rabbitmq.yaml` | RabbitMQ message queue settings |

> 💡 **Configuration templates** are available in `docs/0.build.md` section "2.8 Adding Shared Configurations to Nacos".

#### 2.3 Generate JWT Keystore

Execute the following command in `DOJ-BE/common/src/main/resources/`:

```bash
keytool -genkeypair -alias decade -keyalg RSA -keysize 2048 \
  -validity 365 -keypass doj123 -keystore doj.jks -storepass doj123
```

#### 2.4 Build Backend Project

```bash
cd DOJ-BE
mvn clean install
```

#### 2.5 Start Microservices

Open a separate terminal for each microservice and start them in order:

```bash
# 1. Start Gateway Service
java -Dhttp.proxySet=false -Dhttps.proxySet=false \
  -jar gateway-service/target/gateway-service-1.0-SNAPSHOT.jar

# 2. Start User Service
java -Dhttp.proxySet=false -Dhttps.proxySet=false \
  -jar user-service/target/user-service-1.0-SNAPSHOT.jar

# 3. Start Problem Service
java -Dhttp.proxySet=false -Dhttps.proxySet=false \
  -jar problem-service/target/problem-service-1.0-SNAPSHOT.jar

# 4. Start Submission Service
java -Dhttp.proxySet=false -Dhttps.proxySet=false \
  -jar submission-service/target/submission-service-1.0-SNAPSHOT.jar

# 5. Start Sandbox Service
java -Dhttp.proxySet=false -Dhttps.proxySet=false \
  -jar sandbox-service/target/sandbox-service-1.0-SNAPSHOT.jar
```

> 🎯 **Verify Backend Startup**:
> - Check Nacos service list: `http://localhost:8848/nacos` should show all services in healthy status
> - Access API documentation: `http://localhost:8080/doc.html` should display all service endpoints

### Step 3: Frontend Deployment (`DOJ-FE`)

#### Option A: Docker Container Deployment (Recommended for Production)

```bash
cd DOJ-FE

# One-click build, package, and start (grant execute permission once)
chmod +x run-docker.sh
./run-docker.sh

# Application will start at http://localhost:8088
```

This script automatically:
- ✅ Executes `pnpm build` to generate static files
- ✅ Builds Nginx image based on `Dockerfile`
- ✅ Stops old container and starts new one
- ✅ Maps port `8088 → 80`

#### Option B: Local Development Mode

```bash
cd DOJ-FE

# Install dependencies
pnpm install

# Start development server with hot reload
pnpm dev

# Access at http://localhost:5173
```

### Step 4: Access the Application

Once all services are running successfully, access them via:

| Component | URL | Description |
| :--- | :--- | :--- |
| **Frontend Application** | `http://localhost:8088` | Online coding competition platform |
| **API Documentation** | `http://localhost:8080/doc.html` | Knife4j Swagger documentation |
| **Nacos Console** | `http://localhost:8848/nacos` | Service registry and configuration |
| **SkyWalking UI** | `http://localhost:9999` | Distributed tracing (optional) |
| **Grafana Dashboard** | `http://localhost:3000` | Metrics monitoring and alerting (optional) |
| **Kibana Logs** | `http://localhost:5601` | Log aggregation and analysis (optional) |

---

## 📊 Observability

### Comprehensive Monitoring System

| Pillar | Technology | Purpose |
| :--- | :--- | :--- |
| **Traces** | Apache SkyWalking | "What did the request experience?" - Link visualization, performance analysis |
| **Metrics** | Prometheus + Grafana | "How is the system performing?" - Real-time monitoring and alerting |
| **Logs** | Loki + Promtail | "What happened?" - Log aggregation and troubleshooting |

### Quick Start Observability Stack

```bash
# Start SkyWalking
docker-compose -f docker-compose-skywalking.yml up -d
# Access at http://localhost:9999

# Start Prometheus + Grafana + Loki
docker-compose -f docker-compose-monitoring.yml up -d
# Access Grafana at http://localhost:3000 (admin/admin)
```

For detailed setup, see `docs/0.build.md` section "4. Building the Observability Platform".

---

## 🐳 Docker and Container Orchestration

### Start All Services

```bash
# Start all backend microservices (dependencies must be started first)
docker-compose -f docker-compose-service.yml up -d

# Start frontend application
cd DOJ-FE && ./run-docker.sh

# View running containers
docker ps
```

### Build Sandbox Environment Images

```bash
# Build multi-language code execution environments
docker build -t code-runner-cpp -f docs/Dockerfile.cpp .
docker build -t code-runner-java -f docs/Dockerfile.java .
docker build -t code-runner-python -f docs/Dockerfile.python .
```

---

## 🤝 Contributing

We welcome all forms of contributions!

1. **Fork** this repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## 📞 Contact and Feedback

For questions, suggestions, or feedback, please reach out through:

- **GitHub Issues**: [Submit an Issue](https://github.com/yourusername/D-OnlineJudge/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/D-OnlineJudge/discussions)

---

## 🙏 Acknowledgments

Special thanks to the following open-source projects for their inspiration and support

---

**⭐ If this project helps you, please give it a Star! ⭐**
