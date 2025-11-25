# Gym CRM System

A Spring Core + Hibernate–based application that implements a gym Customer Relationship Management system.
This module extends the previous in-memory Spring Core project by adding full database support, Hibernate ORM, transaction management, authentication, and full CRUD functionality for Trainees, Trainers, and Trainings.

---

## ⚙️ Prerequisites

Before running the project, make sure you have the following installed:

| Tool | Required Version | Description |
|------|------------------|-------------|
| **Java** | 17               | Required JDK version |
| **Maven** | 3.9.10             | For building and dependency management |
| **IntelliJ IDEA** | (optional)       | Recommended IDE for running and debugging the application |
| **MySQL**         |     8.0.34          |                                                           |


---
## Before running the app

### Add your DB settings into:

src/main/resources/application-mysql.properties

### create your schema:

CREATE DATABASE gymdb;


## Before running the app

### Add your DB settings into:

src/main/resources/application-mysql.properties

### create your schema:

CREATE DATABASE gymdb;

## How to Run the Application
**From the command line**
1. git clone "https://git.epam.com/tatev_gyozalyan/epam-tasks.git"
2. cd epam-tasks
3. mvn clean package
4. mvn exec:java -Dexec.mainClass="org.example.Main"

## Notes

Hibernate automatically loads initial data from src/main/resources/import.sql.

Make sure the MySQL server is running before starting the application.
