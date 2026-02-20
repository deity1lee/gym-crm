## Gym CRM System — Spring Boot Version

A Spring Boot + Hibernate REST application for managing a Gym CRM system.
This module enhances the previous non-Boot version by:

Migrating the project entirely to Spring Boot

Adding OpenAPI/Swagger UI documentation

Adding Spring Actuator with custom health indicators & Prometheus metrics

Adding Spring Profiles (local, dev, stg, prod)

Supporting environment-specific database configurations

Demonstrating environment-specific business logic (e.g., trainee deletion allowed only in dev)

No external Tomcat or WAR deployment is needed. The project now runs as a standalone Boot application.

## ⚙️ Prerequisites

Before running the project, make sure you have the following installed:

| Tool              | Required Version | Description                                               |
|-------------------|------------------|-----------------------------------------------------------|
| **Java**          | 17               | Required JDK version                                      |
| **Maven**         | 3.9.10           | For building and dependency management                    |
| **IntelliJ IDEA** | (optional)       | Recommended IDE for running and debugging the application |
| **MySQL**         | 8.0.34           | Database engine                                           |
| **Postman**       |                  | For exploring/testing APIs (actuators and more)           |
| **ActiveMQ**      | 6.2.0            | For messaging between services                            |
| MongoDB           | 8.2.5            | For workload storing                                      |


---


## Before running the app
#### Make sure 8080 port is not used.
#### Make sure 8081 port is not used.

### Create your schema for your preferred profile:
CREATE DATABASE gymdb_'profile name';

For example:

CREATE DATABASE gymdb_prod;

Drop the database if you want to run again.

# How to Run the Application

### Start Eureka discovery service

Go to "Run" from IntelliJ IDEA and run the DiscoveryService.

You can visit web console here: http://localhost:8761/
### Start ActiveMq
Go to **bin** folder or downloaded ActiveMQ broker and run "activemq start" command.

You can visit web console here: http://localhost:8161/admin/index.jsp

### Connect to MongoDB
Open downloaded MongoDB Compass (it will be downloaded in this step), you will see "localhost:27017" in connections tab, hover over it and you will see "CONNECT
button, click it.

### Start Trainer workload service

When DiscoveryService, ActiveMQ, MongoDB are running:

Go to "Run" from IntelliJ IDEA and run the TrainerWorkloadService

### Start User service
#### User service supports different environments

You can explore or edit application-<profile name>.properties. This application supports business logic for
different environments. By changing active profile to "prod" you can see functionality like deleting a trainee disabled.

#### If you're using IntelliJ IDEA:

Go to Run → Edit Configurations.

Add the desired profile (e.g., prod) to the "Active profiles" field.

#### If you're not using IntelliJ or running from the command line, you can activate a profile with:

mvn spring-boot:run "-Dspring-boot.run.profiles='profile name'"
For example:
mvn spring-boot:run "-Dspring-boot.run.profiles=prod"


## Swagger Documentation (API Explorer)

Swagger is enabled in this version of the project.

Once the application is running, open:

http://localhost:8080/swagger-ui/index.html

There you can:

Explore all available endpoints

Execute API calls from the browser

Preview request/response models


## Actuator — Health Indicators & Metrics

management.endpoints.web.exposure.include=health,prometheus

the application include:

### Custom Health Indicators

1. Trainer Overload Indicator
2. Trainee Assignment Indicator
3. Training Type Usage Indicator

#### How to use? 
Go to postman and send get request with URI:
1. http://localhost:8080/actuator/health/trainerOverload
2. http://localhost:8080/actuator/health/trainerTrainingTypeOveruse
3. http://localhost:8080/actuator/health/unassignedTrainer

You can also go to http://localhost:8081/actuator/health to check health of trainer workload service

### Custom metrics using Prometheus.

1. LoginMetrics - Total number of successful logins
2. TraineeMetrics - Time taken to assign trainers to a trainee
3. TrainerAssignmentMetrics = Number of currently active trainees

#### How to use?
Go to postman and send get request with URI:
http://localhost:8080/actuator/prometheus
Only custom metrics are enabled, so only this 3 metrics will be visible.

## Security & Authentication

This version of the application includes a full Spring Security–based authentication and authorization layer.

### Authentication

The application uses JWT (JSON Web Token)–based authentication.
Supported authentication flows:

### 1. Create Profile (Trainee / Trainer):

Public endpoint (no authentication required)

Username and password are generated automatically

Password is stored hashed using BCrypt

A JWT token is returned immediately after profile creation

### 2. Login

Public endpoint

Uses username and password

On successful authentication, a JWT token is returned



## Authorization

All endpoints except Create Profile and Login are protected

Protected endpoints require the following HTTP header:
Authorization: Bearer <JWT_TOKEN>


Requests without a valid token will receive 401 Unauthorized.

## Password Security

Passwords are stored using BCrypt hashing with salt


## Brute-Force Protection

To protect user accounts from brute-force attacks:

After 3 unsuccessful login attempts, the account is locked . The lock duration is 5 minutes. After the lock time expires, the account is automatically unlocked
Locked accounts receive HTTP 423 (LOCKED) on authentication attempts

## Logout

JWT tokens are invalidated using a server-side token blacklist. After logout, the same token can no longer access protected endpoints.

## Swagger & JWT Usage

Swagger UI is configured to work with JWT authentication.

To authorize requests in Swagger call the Login or Create Profile endpoint. Copy the returned JWT token
 then lick Authorize in Swagger UI, paste the token without the Bearer prefix  and Swagger will automatically include:
Authorization: Bearer <JWT_TOKEN>
in all protected requests.

### CORS Configuration

-CORS is configured, different port for ui can be considered.

-Authorization headers are allowed

-The application supports stateless JWT-based requests from browsers and API tools



### Architecture Overview

The system consists of:

Gym Service

Trainer Workload Service

Discovery Service


### Trainer Workload Service
Purpose:

-Every time a training session is added or deleted, the User Service sends data to the
Trainer Workload Service. Here endpoints are protected and API key is needed to access them.

The microservice:

-Calculates trainer working hours

-Aggregates workload per month / per year

-Stores data in NoSQL store - MongoDB (uses indexes for faster data retrieval)


### Service Discovery (Eureka)

-All microservices register themselves automatically

-Services communicate using logical service names, not hardcoded URLs

-Make sur this service is up before any other is started

### Distributed Consistency & Reliability Strategy

The system uses a Circuit Breaker and Saga Orchestrator pattern to ensure reliable inter-service communication.
As training add/delete action should make changes in both user and trainer workload services, compensation mechanism is used.

The system utilizes the Saga Pattern combined with asynchronous messaging via ActiveMQ to ensure eventual consistency between the Training Service and the Trainer Workload Service.

How it works
When a training is added or deleted, the system initiates a distributed transaction:

Asynchronous Event Dispatch: A workload update event is sent to an ActiveMQ queue.

Fail-Fast Messaging: If the message cannot be successfully handed off to the message broker (ActiveMQ), the Saga fails immediately and the local transaction is rolled back.

Asynchronous Consumption: The Trainer Workload Service consumes the message from the queue independently.

Handling Failures & Rollbacks

Business Logic Validation: If the Trainer Workload Service processes a message but determines it is invalid (e.g., business rule violation), it triggers a Compensating Transaction.

If a Add request was invalid, the service deletes the newly created training.
If a Delete request was invalid, the service restores the training record.

Reliable Delivery: ActiveMQ ensures that once a message is accepted, it will be delivered even if the Trainer Workload Service is temporarily offline.


## Notes

If you are going to start application using docker, consider using profile "local".

If an issue persists try to add this vm option:
-Djava.rmi.server.hostname=localhost