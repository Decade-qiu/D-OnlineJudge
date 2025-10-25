# D-OnlineJudge

[![Java](https://img.shields.io/badge/java-17-blue)](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)  [![Spring Boot](https://img.shields.io/badge/spring--boot-2.7.12-green)](https://spring.io/projects/spring-boot)  [![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2021.0.3-green)](https://spring.io/projects/spring-cloud)  [![Spring Gateway](https://img.shields.io/badge/Spring%20Gateway-2.7.12-green)](https://spring.io/projects/spring-cloud-gateway)  [![Feign](https://img.shields.io/badge/Feign-11.8-green)](https://github.com/OpenFeign/feign)  [![Nacos](https://img.shields.io/badge/Nacos-2021.0.4.0-green)](https://nacos.io/) [![MySQL](https://img.shields.io/badge/MySQL-8.0.23-blue)](https://www.mysql.com/)  [![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.2-green)](https://baomidou.com/) [![Vue.js](https://img.shields.io/badge/vue.js-3.0%2B-green)](https://vuejs.org/)  [![TypeScript](https://img.shields.io/badge/TypeScript-4.5%2B-blue)](https://www.typescriptlang.org/)  [![Element Plus](https://img.shields.io/badge/Element%20Plus-2.5.1-green)](https://element-plus.org/)  [![Docker](https://img.shields.io/badge/docker-20.10%2B-blue)](https://www.docker.com/) [![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

**D-OnlineJudge** is a full-stack online coding platform featuring a Spring Cloud-based microservices architecture and Vue 3 frontend. It provides secure code execution environments through Docker containerization and robust inter-service communication via OpenFeign. The system supports programming competitions, code submission evaluation, and real-time progress tracking.

---

## Project Structure

The project is organized into the following directories:

- **`DOJ-BE`**: Contains the backend code, consisting of multiple microservices.
- **`DOJ-FE`**: Contains the frontend code, built with Vue 3.
- **`docs`**: Contains additional project documentation.

---

## Quick Start

This guide provides the essential steps to get the D-OnlineJudge application running locally.

### 1. Backend Setup (`DOJ-BE`)

- **Start Dependencies**: Run `MySQL` and `Nacos` using Docker. For detailed instructions, see `docs/1.docker部署.md`.
- **Configure Nacos**: Add the shared configurations (`shared-jdbc.yaml`, etc.) to the Nacos dashboard.
- **Build**: Navigate to the `DOJ-BE/` directory and run `mvn clean install`.
- **Run Services**: Start each microservice by running its corresponding JAR file from the `target` directory (e.g., `java -jar user-service/target/user-service-1.0-SNAPSHOT.jar`). If you have a system proxy enabled, you may need to disable it for the JVM by adding `-Dhttp.proxySet=false -Dhttps.proxySet=false` to the command.

### 2. Frontend Setup (`DOJ-FE`)

- **Install Dependencies**: Navigate to the `DOJ-FE/` directory and run `pnpm install`.
- **Deploy**: Use the provided script to build and deploy the frontend in a Docker container.
  ```sh
  # (Run once) Add execute permission to the script
  chmod +x run-docker.sh

  # Run the deployment script
  ./run-docker.sh
  ```

### 3. Access the Application

Once all services are running, you can access the application at `http://localhost:8088`.

## ✨ 项目特性

- ⚡️ **高性能全文检索**: 基于 Elasticsearch 和 IK 中文分词器，提供毫秒级的题目名称、描述、标签全文检索能力。
- 🚀 **现代化的技术栈**: 后端采用 Spring Cloud Alibaba 微服务架构，前端采用 Vue 3 + TypeScript + Vite。
- ☁️ **全容器化部署**: 所有服务（包括中间件）均可通过 Docker 和 Docker Compose 一键启动，实现了环境的完全隔离和快速部署。
