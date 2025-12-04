# Gym CRM System

A simple **Spring Core–based application** that simulates an in-memory CRM system for managing **trainees**, **trainers**, and **trainings**.

This project demonstrates:
- Spring Core configuration using `@Configuration` and `@ComponentScan`
- Externalized configuration with `@PropertySource` and `@Value`
- Bean lifecycle handling via `BeanPostProcessor`
- Layered architecture with DAO, Service, and Facade layers
- Initialization of in-memory storage from resource files

---

## ⚙️ Prerequisites

Before running the project, make sure you have the following installed:

| Tool | Required Version | Description |
|------|------------------|-------------|
| **Java** | 17               | Required JDK version |
| **Maven** | 3.9.10             | For building and dependency management |
| **IntelliJ IDEA** | (optional)       | Recommended IDE for running and debugging the application |

---

## How to Run the Application
 **From the command line**
1. git clone "https://git.epam.com/tatev_gyozalyan/epam-tasks.git"
2. cd epam-tasks
3. mvn clean package
4. mvn exec:java -Dexec.mainClass="org.example.Main"
