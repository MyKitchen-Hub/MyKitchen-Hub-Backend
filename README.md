# MyKitchen Hub 🍳

A digital solution to centralize recipes, facilitate automated shopping list creation, and enable social interaction between users through comments, chat, and reactions. This project promotes organized and healthy eating habits through technology.

## 🎯 Project Overview

MyKitchen Hub is a Spring Boot application that helps users optimize their cooking time by avoiding improvisation and reducing food waste. It provides a secure REST API for managing recipes and shopping lists, with social features for user interaction.

## 🚀 Current Implementation Status

### ✅ **COMPLETED FEATURES**

#### **Project Foundation**

- [x] Spring Boot 3.5.4 project setup with Maven
- [x] Java 21 configuration
- [x] MySQL database integration
- [x] Environment variable configuration with spring-dotenv
- [x] CORS configuration for frontend integration
- [x] Comprehensive logging configuration

#### **User Management System**

- [x] User entity with JPA annotations and validation
- [x] Role-based authorization (USER, ADMIN)
- [x] User registration DTO with validation
- [x] User login DTO
- [x] User response DTO
- [x] User repository interface
- [x] Spring Security integration
- [x] JWT token dependencies (jjwt-api, jjwt-impl, jjwt-jackson)

#### **Email System**

- [x] SMTP email configuration (Gmail)
- [x] Spring Boot Mail starter
- [x] Environment-based email credentials

#### **WebSocket Support**

- [x] WebSocket starter for real-time chat functionality

#### **Data Validation**

- [x] Bean validation starter
- [x] Input validation annotations (@NotBlank, @Email, @Size)

### 🔄 IN PROGRESS

#### **Database & Data**

- [x] Database schema configuration
- [x] Data initialization setup
- [x] JPA configuration with Hibernate
- [x] Recipe entity and relationships
- [x] Ingredient entity and relationships
- [ ] Comment entity and relationships
- [ ] Shopping list entity and relationships

#### **Core Recipe Management**

- [ ] Recipe entity with CRUD operations
- [ ] Ingredient entity and management
- [x] Recipe-ingredient relationships
- [ ] Recipe service layer
- [ ] Recipe controller with REST endpoints
- [ ] Recipe search and filtering

#### **Shopping List System**

- [x] Shopping list entity
- [ ] Automatic shopping list generation
- [ ] Ingredient consolidation logic
- [ ] Shopping list service
- [ ] Shopping list controller

#### **Authentication & Security**

- [ ] JWT token generation and validation
- [ ] Authentication service
- [ ] Security configuration
- [ ] Password encoding
- [ ] Login/logout endpoints

#### **Social Features**

- [ ] Comment system per recipe
- [ ] Like/dislike system
- [ ] Favorite recipes functionality
- [ ] General chat system (WebSocket)
- [ ] User interaction services

#### **Email Notifications**

- [ ] User registration confirmation emails
- [ ] Shopping list email delivery
- [ ] Email templates and services

#### **Testing & Quality**

- [ ] Unit tests (target: 70% coverage)
- [ ] Integration tests
- [ ] Security tests
- [ ] API documentation with Swagger

#### **DevOps & Deployment**

- [ ] Docker configuration
- [ ] GitHub Actions CI/CD
- [ ] Conventional commits
- [ ] Production deployment setup

## 🏗️ **Architecture & Technology Stack**

### **Backend**

- **Framework**: Spring Boot 3.5.4
- **Language**: Java 21
- **Database**: MySQL with JPA/Hibernate
- **Security**: Spring Security + JWT
- **Build Tool**: Maven
- **Real-time**: WebSocket support

### **Dependencies**

- Spring Boot Web, Data JPA, Security, Mail, WebSocket
- MySQL Connector
- JWT implementation (jjwt)
- Lombok for boilerplate reduction
- Bean validation
- Spring dotenv for environment management

## 📁 **Project Structure**

```

```

## 🧪 **Testing**

```

```

## 🚀 **Getting Started**

### **Prerequisites**

- Java 21
- Maven 3.6+
- MySQL 8.0+
- Environment variables configured

### **Environment Variables**

```bash
DB_URL=jdbc:mysql://localhost:3306/mykitchen_hub
DB_USERNAME=your_username
DB_PASSWORD=your_password
SERVER_PORT=8080
EMAIL=your_email@gmail.com
EMAIL_PASSWORD=your_app_password
```

### **Running the Application**

```bash
mvn spring-boot:run
```
