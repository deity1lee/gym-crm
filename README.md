## Gym CRM System — REST API Version

A Spring Core + Hibernate + REST application that exposes a complete RESTful backend for a Gym Customer Relationship Management System.

This module extends the previous Spring Core + Hibernate project by:

Adding REST controllers

Packaging the application as a WAR file

Deploying it on an external Apache Tomcat Server

Providing API documentation via Swagger UI

The application supports full CRUD functionality for Trainees, Trainers, and Trainings, including authentication and transactional DB operations.


## ⚙️ Prerequisites

Before running the project, make sure you have the following installed:

| Tool              | Required Version | Description |
|-------------------|------------------|-------------|
| **Java**          | 17               | Required JDK version |
| **Maven**         | 3.9.10           | For building and dependency management |
| **IntelliJ IDEA** | (optional)       | Recommended IDE for running and debugging the application |
| **MySQL**         | 8.0.34           |                   Database engine                                        |
| **Apache Tomcat** | 9.0.104          |    Application server for deploying WAR                                                       |


---


## Before running the app

### Add your DB settings into:

src/main/resources/application-mysql.properties

### create your schema:

CREATE DATABASE gymdb;

### Initial data

Hibernate loads data automatically from:
src/main/resources/import.sql

## How to Run the Application

### Add Tomcat to IntelliJ
1. Open IntelliJ. Go to File → Settings
2. Navigate to:
   Build, Execution, Deployment → Application Servers
3. Click + → Tomcat Server . Choose your Tomcat directory.

### Create a Tomcat Run Configuration
1. Go to:
   Run → Edit Configurations…
2. Click + → select Tomcat Server (Local)
3. Choose your Tomcat (the one you added earlier).

### Add Your WAR Artifact to Tomcat
This tells IntelliJ which file to deploy to Tomcat.
1. In the same configuration window: Go to the Deployment tab.
2. Click the + button → choose Artifact…
3. Select your artifact:
   <Gym-CRM-System>:war exploded (better for development)
   or
   <project-name>:war
4. Set Application Context for example:  /gym. This means the app will run at:http://localhost:8080/gym
5. Build with Maven, run: mvn clean package
6. Start Tomcat From IntelliJ. Click the Run (green triangle) next to your Tomcat configuration. IntelliJ will start Tomcat and deploy your WAR (or exploded WAR)


## Swagger Documentation (API Explorer)

Swagger is enabled in the REST version of the project.

Once the application is running, open (if your Application Context is "/gym""):

http://localhost:8080/gym/swagger-ui/index.html


There you can:

Explore all available endpoints

Execute API calls from the browser

Preview request/response models


## Notes

Ensure MySQL is running before starting Tomcat.

If Tomcat runs on a different port, adjust the URL (default is 8080).

If the WAR deploys under a different name (application context), replace gym-crm in the URLs.

If you make code changes, re-run mvn clean package and redeploy the WAR.