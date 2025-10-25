# D-OnlineJudge - 一个现代化的分布式在线判题系统

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.12-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.x-green.svg)](https://vuejs.org/)
[![Docker](https://img.shields.io/badge/Docker-Powered-blue.svg)](https://www.docker.com/)

**D-OnlineJudge** 是一个基于 Spring Cloud Alibaba 构建的、完全容器化的分布式在线编程判题平台。它不仅实现了传统 OJ 的核心功能，更在系统架构、工程化、可观测性和用户体验上，采用了大量现代化的设计与实践。

---

## 🏛️ 系统架构

```mermaid
graph TD
    subgraph 用户端
        User[<i class="fa fa-user"></i> 用户 / 管理员]
    end

    subgraph 前端 (Vue 3)
        FE[DOJ-FE]
    end

    subgraph 部署 & 运维
        CICD[<i class="fa fa-cogs"></i> GitHub Actions CI/CD]
        Docker[<i class="fa fa-docker"></i> Docker Compose]
    end

    subgraph 可观测性体系
        Monitor[<i class="fa fa-chart-line"></i> Prometheus + Grafana]
        Trace[<i class="fa fa-sitemap"></i> SkyWalking]
        Log[<i class="fa fa-file-alt"></i> Loki]
    end

    subgraph 核心后端 (Spring Cloud)
        Gateway[<i class="fa fa-door-open"></i> Gateway-Service]
        UserService[<i class="fa fa-users"></i> User-Service]
        ProblemService[<i class="fa fa-book"></i> Problem-Service]
        SubmissionService[<i class="fa fa-paper-plane"></i> Submission-Service]
        SandboxService[<i class="fa fa-box"></i> Sandbox-Service]
    end

    subgraph 核心中间件
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

    SubmissionService -- 发送判题任务 --> RabbitMQ
    RabbitMQ -- 消费判题任务 --> SandboxService
    SandboxService -- 发送判题结果 --> RabbitMQ
    RabbitMQ -- 消费判题结果 --> SubmissionService

    ProblemService <--> ES
    
    subgraph 所有服务
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

## ✨ 项目亮点与技术实现

- 🚀 **现代化的微服务架构**
  - **技术**: Spring Cloud Alibaba 全家桶。
  - **实现**: 使用 Nacos 作为注册与配置中心，实现了服务的动态发现和集中式配置管理；使用 Sentinel 实现了网关层的流量控制和熔断降级，保证了系统的稳定性。

- ⚡️ **高性能全文检索**
  - **技术**: Elasticsearch 8.x + IK 中文分词器。
  - **实现**: 解决了传统 `MySQL LIKE` 查询的性能瓶颈。通过“双写同步”架构，将题目数据实时同步至 ES。实现了毫秒级的题目名称、描述、标签全文检索，并支持相关性排序。

- ⛓️ **全链路可观测性 (The Three Pillars)**
  - **技术**: SkyWalking + Prometheus + Grafana + Loki。
  - **实现**: 构建了完整的“监控、追踪、日志”三位一体的可观测性体系。SkyWalking 提供分布式链路追踪，Prometheus 和 Grafana 负责指标监控与告警，Loki 负责日志的集中聚合与查询，极大提升了线上问题的排查效率。

- 📦 **轻量级判题沙箱**
  - **技术**: Docker-out-of-Docker。
  - **实现**: `sandbox-service` 自身在容器中运行，通过挂载宿主机的 `docker.sock` 文件，巧妙地获得了调用宿主机 Docker Daemon 的能力。这使得它能动态地创建和销毁用于执行用户代码的“一次性”判题容器，实现了轻量、安全、高效的沙箱隔离。

- 💨 **核心业务异步化**
  - **技术**: RabbitMQ 消息队列。
  - **实现**: 将耗时的“判题”流程从主业务流程中剥离。`submission-service` 在接收到提交后，仅需向 MQ 投递一个任务消息即可立即返回，由 `sandbox-service` 异步消费并执行。这种设计极大地提升了系统的吞吐能力和用户体验，并增强了服务的可用性和解耦性。

- 🛡️ **优雅的认证与权限控制**
  - **技术**: Gateway + Spring Interceptor + 自定义注解。
  - **实现**: 在网关层统一进行 JWT 认证，并将用户 ID 注入请求头。下游服务通过 `ThreadLocal` 缓存用户上下文，并通过自定义的 `@AdminRequired` 注解和拦截器，实现了非侵入式的、优雅的 RBAC 权限控制。

- ⚙️ **全流程自动化 CI/CD**
  - **技术**: GitHub Actions。
  - **实现**: 编写了完整的 CI/CD 流水线，实现了从“代码提交”到“自动编译、构建 Docker 镜像、推送到镜像仓库、最后部署到服务器”的全流程自动化，极大地提升了开发和交付效率。

- 🐳 **彻底的容器化部署**
  - **技术**: Docker + Docker Compose。
  - **实现**: 项目的所有服务，包括所有中间件，都可通过 Docker 进行部署。通过环境变量和外部化配置，实现了在不同环境中（本地开发、服务器部署）的无缝迁移。

## 🛠️ 技术栈

| 类别 | 技术 |
| :--- | :--- |
| **后端** | Spring Boot, Spring Cloud Alibaba, Mybatis-Plus, Spring Security, JWT |
| **前端** | Vue 3, TypeScript, Vite, Element-Plus, Pinia |
| **数据库** | MySQL, Redis (缓存) |
| **搜索引擎** | Elasticsearch 8.x, IK Analyzer |
| **消息队列** | RabbitMQ |
| **服务治理** | Nacos, Sentinel |
| **可观测性** | SkyWalking, Prometheus, Grafana, Loki |
| **部署** | Docker, Docker Compose, GitHub Actions |

## 🚀 快速开始

详细的部署步骤请参考 `docs/0.build.md` 文件。该文件包含了所有中间件和应用服务的详细部署命令和配置说明。

1.  **环境准备**: 确保您的机器已安装 Docker 和 Docker Compose。
2.  **网络创建**: `docker network create doj`
3.  **部署中间件**: 按照 `docs/0.build.md` 中的指南，依次部署 MySQL, Redis, RabbitMQ, Nacos, Sentinel, 以及自定义的 Elasticsearch。
4.  **部署应用**: 修改 `docker-compose-service.yml` 中的环境变量（如果需要），然后执行 `docker-compose -f docker-compose-service.yml up -d --build`。
5.  **访问**: 前端默认访问地址为 `http://localhost:8088`。