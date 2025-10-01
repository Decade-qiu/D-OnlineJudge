# D-OnlineJudge - Gemini Context

This document provides a comprehensive overview of the D-OnlineJudge project, its architecture, and development conventions to be used as instructional context for the Gemini AI assistant.

---

## 1. Project Overview

D-OnlineJudge is a full-stack, microservices-based online coding and automated judging platform. It allows users to solve programming problems, submit their code, and receive real-time feedback on their solutions.

- **Backend (`DOJ-BE`)**: A distributed system built with **Java 17**, **Spring Boot**, and **Spring Cloud Alibaba**. It handles all business logic, including user management, problem storage, code submission, and automated judging.
- **Frontend (`DOJ-FE`)**: A modern single-page application built with **Vue 3**, **TypeScript**, and **Vite**. It provides the user interface for browsing problems, writing code in an online editor, and viewing submission results.
- **Architecture**: The backend follows a classic microservice architecture, with services for Gateway, Users, Problems, Submissions, and a secure Docker-based Code Sandbox. **Nacos** is used for service discovery and centralized configuration.
- **Database**: The project uses **MySQL**, with each core microservice managing its own dedicated database (`doj_user`, `doj_problem`, `doj_submission`), following the database-per-service pattern.

> **Note on Focus:** While this is a full-stack project, the primary focus for AI-assisted tasks should be on the **backend (`DOJ-BE`)**. This includes architectural improvements, feature implementation, performance optimization, and maintaining the microservices ecosystem. Frontend (`DOJ-FE`) tasks are of lower priority.

---

## 2. Project Structure

The monorepo is organized into three main directories:

- **`/DOJ-BE/`**: Contains the Java backend source code, organized into Maven modules for each microservice.
- **`/DOJ-FE/`**: Contains the Vue.js frontend source code.
- **`/docs/`**: Contains extensive project documentation, including deployment guides, architecture diagrams, API specifications, and database schemas.

---

## 3. Building and Running

### 3.1. Backend (`DOJ-BE`)

The backend requires Docker, JDK 17, and Maven.

**1. Start Dependencies:**
The project relies on MySQL and Nacos. A detailed guide for setting them up with Docker is in `docs/1.docker部署.md`. The key steps are:
```sh
# 1. Create a dedicated network for the services
docker network create doj

# 2. Run MySQL
docker run -d --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123 --network doj mysql:8.0.23

# 3. Create databases (doj_user, doj_problem, doj_submission, nacos) inside the MySQL container.

# 4. Run Nacos, ensuring it's configured to connect to the MySQL container on the 'doj' network.
docker run -d --name nacos --env-file ./nacos/custom.env -p 8848:8848 --network doj nacos/nacos-server:v2.5.1-slim
```

**2. Configure Nacos:**
Log in to the Nacos dashboard (`http://localhost:8848/nacos`) and add the shared configurations (`shared-jdbc.yaml`, `shared-swagger.yaml`, `shared-jwt.yaml`) as detailed in `docs/1.docker部署.md`.

**3. Build the Application:**
Navigate to the backend root and use Maven to build all modules.
```sh
cd DOJ-BE/
mvn clean install
```

**4. Run the Microservices:**
Start each microservice from its target directory. It's recommended to start the gateway last.
```sh
java -jar user-service/target/user-service-1.0-SNAPSHOT.jar
java -jar problem-service/target/problem-service-1.0-SNAPSHOT.jar
java -jar submission-service/target/submission-service-1.0-SNAPSHOT.jar
java -jar sandbox-service/target/sandbox-service-1.0-SNAPSHOT.jar
java -jar gateway-service/target/gateway-service-1.0-SNAPSHOT.jar
```

### 3.2. Frontend (`DOJ-FE`)

The frontend requires Node.js and pnpm.

```sh
# Navigate to the frontend directory
cd DOJ-FE/

# Install dependencies
pnpm install

# Start the development server
pnpm dev
```

---

## 4. Key Architectural Concepts & Conventions

- **Microservice Communication**: All inter-service communication is handled via **OpenFeign**, with interfaces defined in the `common` module.
- **Authentication**: A stateless **JWT-based** authentication mechanism is enforced at the **API Gateway** (`gateway-service`). The gateway validates the token and injects the `userId` into the request headers for downstream services.
- **Configuration**: All shared configurations are managed centrally in **Nacos**. Each service loads its specific `application.yaml` and the shared configurations from Nacos.
- **Code Sandbox**: The `sandbox-service` is the security cornerstone. It uses **Docker** to create isolated, resource-limited environments for executing user-submitted code, preventing malicious actions. Execution is handled **asynchronously** in a dedicated thread pool to avoid blocking API calls.
- **API Documentation**: The project uses **Knife4j** (a Swagger enhancement) for API documentation, which is aggregated and exposed through the gateway.
- **Database Strategy**: Each core service (`user`, `problem`, `submission`) has its own MySQL database, ensuring loose coupling between services.

---

## 5. Git Commit Message Specification

All Git commits MUST adhere to the **Conventional Commits** + **Gitmoji** format.

**Format:**
```
:git_emoji: type(scope): subject
```

**Example:**
```
:sparkles: feat(cli): add support for verbose logging
:bug: fix(pcap): remove incorrect channel close in reader
:recycle: refactor(engine): solve circular dependency
```

| Gitmoji | type | Description |
|---|---|---|
| :sparkles: | feat | A new feature |
| :bug: | fix | A bug fix |
| :recycle: | refactor | Code refactoring without changing functionality |
| :memo: | docs | Documentation only changes |
| :lipstick: | style | Code style changes (formatting, etc.) |
| :white_check_mark: | test | Adding or modifying tests |
| :wrench: | chore | Build process or auxiliary tool changes |
| :rocket: | build | Changes that affect the build system or dependencies |
| :construction_worker: | ci | CI/CD configuration changes |
| :zap: | perf | A code change that improves performance |
