# D-OnlineJudge - A Modern, Distributed Online Judging System

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.12-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.x-green.svg)](https://vuejs.org/)
[![Docker](https://img.shields.io/badge/Docker-Powered-blue.svg)](https://www.docker.com/)

**D-OnlineJudge** is a fully containerized, distributed online programming judging platform built on Spring Cloud Alibaba. It not only implements the core functionalities of a traditional Online Judge but also adopts a wealth of modern design principles and engineering practices in its system architecture, observability, and user experience.

---

## 🏛️ System Architecture

```mermaid
graph TD
    subgraph User End
        User[<i class="fa fa-user"></i> User / Admin]
    end

    subgraph Frontend (Vue 3)
        FE[DOJ-FE]
    end

    subgraph Deployment & DevOps
        CICD[<i class="fa fa-cogs"></i> GitHub Actions CI/CD]
        Docker[<i class="fa fa-docker"></i> Docker Compose]
    end

    subgraph Observability Stack
        Monitor[<i class="fa fa-chart-line"></i> Prometheus + Grafana]
        Trace[<i class="fa fa-sitemap"></i> SkyWalking]
        Log[<i class="fa fa-file-alt"></i> Loki]
    end

    subgraph Core Backend (Spring Cloud)
        Gateway[<i class="fa fa-door-open"></i> Gateway-Service]
        UserService[<i class="fa fa-users"></i> User-Service]
        ProblemService[<i class="fa fa-book"></i> Problem-Service]
        SubmissionService[<i class="fa fa-paper-plane"></i> Submission-Service]
        SandboxService[<i class="fa fa-box"></i> Sandbox-Service]
    end

    subgraph Core Middleware
        Nacos[<i class="fa fa-compass"></i> Nacos]
        Sentinel[<i class="fa fa-shield-alt"></i> Sentinel]
        MySQL[<i class="fa fa-database"></i> MySQL]
        Redis[<i class="fa fa-memory"></i> Redis]
        RabbitMQ[<i class="fa fa-envelope"></i> RabbitMQ]
        ES[<i class="fa fa-search"></i> Elasticsearch]
    end

    User --> FE
    FE --> Gateway
    
    Gateway --> UserService
    Gateway --> ProblemService
    Gateway --> SubmissionService

    SubmissionService -- Sends Judging Task --> RabbitMQ
    RabbitMQ -- Consumes Judging Task --> SandboxService
    SandboxService -- Sends Judging Result --> RabbitMQ
    RabbitMQ -- Consumes Judging Result --> SubmissionService

    ProblemService <--> ES
    
    subgraph All Services
        UserService <--> MySQL
        ProblemService <--> MySQL
        SubmissionService <--> MySQL
        UserService <--> Redis
    end

    Gateway -.-> Nacos
    UserService -.-> Nacos
    ProblemService -.-> Nacos
    SubmissionService -.-> Nacos
    SandboxService -.-> Nacos

    Gateway -.-> Sentinel

    CICD --> Docker
    Docker --> Gateway
    Docker --> FE

    Monitor --> Gateway
    Trace --> Gateway
    Log --> Gateway
```

## ✨ Key Features & Highlights

- 🚀 **Modern Microservices Architecture**
  - **Technology**: Spring Cloud Alibaba Suite.
  - **Implementation**: Utilizes Nacos as a service registry and configuration center for dynamic service discovery and centralized configuration management. Employs Sentinel at the gateway layer for traffic control and circuit breaking, ensuring system stability.

- ⚡️ **High-Performance Full-Text Search**
  - **Technology**: Elasticsearch 8.x + IK Chinese Analyzer.
  - **Implementation**: Overcomes the performance bottlenecks of traditional `MySQL LIKE` queries. A "dual-write" architecture synchronizes problem data to Elasticsearch in near real-time. This provides millisecond-level full-text search capabilities across problem titles, descriptions, and tags, with support for relevance scoring.

- ⛓️ **Comprehensive Observability (The Three Pillars)**
  - **Technology**: SkyWalking + Prometheus + Grafana + Loki.
  - **Implementation**: Establishes a complete observability system integrating metrics, tracing, and logging. SkyWalking provides distributed tracing, Prometheus and Grafana handle metrics monitoring and alerting, and Loki manages centralized log aggregation and querying, dramatically improving troubleshooting efficiency for online issues.

- 📦 **Lightweight Judging Sandbox**
  - **Technology**: Docker-out-of-Docker.
  - **Implementation**: The `sandbox-service` runs within a container but is capable of orchestrating the host's Docker daemon by mounting the `docker.sock` file. This allows it to dynamically create and destroy ephemeral "single-use" containers for executing user code, achieving lightweight, secure, and efficient sandboxing.

- 💨 **Asynchronous Core Business Flow**
  - **Technology**: RabbitMQ Message Queue.
  - **Implementation**: The time-consuming "judging" process is decoupled from the main business flow. Upon receiving a submission, the `submission-service` simply dispatches a task to RabbitMQ and returns immediately. The `sandbox-service` asynchronously consumes and processes the task. This design mulheres boosts system throughput, enhances user experience, and improves service availability and decoupling.

- 🛡️ **Elegant Authentication & Authorization**
  - **Technology**: Gateway + Spring Interceptor + Custom Annotations.
  - **Implementation**: JWT authentication is handled uniformly at the gateway layer, with the user ID injected into request headers. Downstream services use `ThreadLocal` to manage the user context. A custom `@AdminRequired` annotation combined with an interceptor provides a non-intrusive, elegant RBAC (Role-Based Access Control) mechanism.

- ⚙️ **Fully Automated CI/CD Pipeline**
  - **Technology**: GitHub Actions.
  - **Implementation**: A complete CI/CD pipeline automates the entire workflow from code commit to compiling, building Docker images, pushing to a registry, and deploying to the server, significantly improving development and delivery efficiency.

- 🐳 **Thorough Containerization**
  - **Technology**: Docker + Docker Compose.
  - **Implementation**: All project services, including middleware, are deployed via Docker. The use of environment variables and externalized configurations allows for seamless migration between different environments (e.g., local development, server deployment).

## 🛠️ Technology Stack

| Category | Technology |
| :--- | :--- |
| **Backend** | Spring Boot, Spring Cloud Alibaba, Mybatis-Plus, Spring Security, JWT |
| **Frontend** | Vue 3, TypeScript, Vite, Element-Plus, Pinia |
| **Database** | MySQL, Redis (Caching) |
| **Search Engine** | Elasticsearch 8.x, IK Analyzer |
| **Message Queue** | RabbitMQ |
| **Service Governance** | Nacos, Sentinel |
| **Observability** | SkyWalking, Prometheus, Grafana, Loki |
| **Deployment** | Docker, Docker Compose, GitHub Actions |

## 🚀 Quick Start

For detailed deployment steps, please refer to the `docs/0.build.md` file, which contains comprehensive commands and configuration instructions for all middleware and application services.

1.  **Prerequisites**: Ensure Docker and Docker Compose are installed on your machine.
2.  **Create Network**: `docker network create doj`
3.  **Deploy Middleware**: Follow the guide in `docs/0.build.md` to deploy MySQL, Redis, RabbitMQ, Nacos, Sentinel, and the custom Elasticsearch instance.
4.  **Deploy Applications**: Modify environment variables in `docker-compose-service.yml` if needed, then run `docker-compose -f docker-compose-service.yml up -d --build`.
5.  **Access**: The frontend is available by default at `http://localhost:8088`.
