# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

# Copy all pom.xml files first to leverage Docker cache
COPY DOJ-BE/pom.xml .
COPY DOJ-BE/common/pom.xml ./common/
COPY DOJ-BE/gateway-service/pom.xml ./gateway-service/
COPY DOJ-BE/problem-service/pom.xml ./problem-service/
COPY DOJ-BE/sandbox-service/pom.xml ./sandbox-service/
COPY DOJ-BE/submission-service/pom.xml ./submission-service/
COPY DOJ-BE/user-service/pom.xml ./user-service/

# Download all dependencies
RUN mvn dependency:go-offline

# Copy all source code
COPY DOJ-BE/common/src ./common/src
COPY DOJ-BE/sandbox-service/src ./sandbox-service/src

# Build the specific service
RUN mvn -f ./pom.xml -pl sandbox-service -am clean package -DskipTests
RUN ls -al ./sandbox-service/target

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim

# Install Docker 
USER root
RUN apt-get update && apt-get install -y docker.io

WORKDIR /app

# Copy the executable jar from the build stage
COPY --from=build /app/sandbox-service/target/sandbox-service.jar ./app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "./app.jar"]