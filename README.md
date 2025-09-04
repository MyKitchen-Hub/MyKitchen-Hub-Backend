# MyKitchen Hub 🍳

A comprehensive digital solution for recipe management, automated shopping list creation, and social cooking interaction. This Spring Boot application helps users organize their cooking, reduce food waste, and share culinary experiences through a modern REST API.

## Project Overview

MyKitchen Hub is a full-featured recipe management platform that enables users to:

- Create, manage, and share recipes with detailed ingredients and instructions
- Generate automated shopping lists from selected recipes
- Interact socially through comments, likes, and favorites
- Upload and manage recipe images via Cloudinary integration
- Receive email notifications and PDF shopping lists
- Secure authentication with JWT tokens

## **Architecture & Technology Stack**

### **Backend Technologies**

![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Maven](https://img.shields.io/badge/apachemaven-C71A36.svg?style=for-the-badge&logo=apachemaven&logoColor=white)
![Cloudinary](https://img.shields.io/badge/Cloudinary-3448C5?style=for-the-badge&logo=Cloudinary&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
- **Email**: Spring Mail with Gmail SMTP
- **PDF Generation**: Apache PDFBox


### **Key Dependencies**

```xml
- Spring Boot Web, Data JPA, Security, Mail, WebSocket
- MySQL Connector
- JWT implementation (jjwt 0.12.6)
- Cloudinary SDK
- Apache PDFBox
- Lombok
- Bean Validation
- Spring Dotenv
- H2 Database (testing)
```

## 📁 **Project Structure**

```
src/main/java/femcoders25/mykitchen_hub/
├── auth/                    # Authentication & Security
│   ├── config/             # Security configuration
│   ├── controller/         # Auth endpoints
│   ├── dto/               # Auth DTOs
│   ├── filter/            # JWT filters
│   └── service/           # Auth services
├── cloudinary/            # Image management
├── comment/               # Comment system
├── common/                # Shared utilities
│   ├── dto/              # Common DTOs
│   └── exception/        # Custom exceptions
├── email/                 # Email services
├── favorite/              # Favorite recipes
├── ingredient/            # Ingredient management
├── like/                  # Like/dislike system
├── recipe/                # Recipe management
├── shoppinglist/          # Shopping list system
├── swagger/               # API documentation
└── user/                  # User management
```

## 🗄️ **Database Schema**
[![temp-Imagew-Fdp3r.avif](https://i.postimg.cc/K4S078bC/temp-Imagew-Fdp3r.avif)](https://postimg.cc/0KnGPxbC)

### **Core Entities**

- **User**: User accounts with roles and authentication
- **Recipe**: Recipe information with metadata
- **Ingredient**: Recipe ingredients with quantities
- **ShoppingList**: Generated shopping lists
- **ListItem**: Individual items in shopping lists
- **Comment**: User comments on recipes
- **Like**: User likes/dislikes on recipes
- **Favorite**: User favorite recipes

### **Relationships**

- User → Recipes (One-to-Many)
- Recipe → Ingredients (One-to-Many)
- User → ShoppingLists (One-to-Many)
- ShoppingList → ListItems (One-to-Many)
- Recipe → Comments (One-to-Many)
- User → Comments (One-to-Many)
- Recipe → Likes (One-to-Many)
- User → Likes (One-to-Many)
- Recipe → Favorites (One-to-Many)
- User → Favorites (One-to-Many)

##  **API Endpoints**

### **Authentication** (`/api/auth`)

- `POST /register` - User registration
- `POST /login` - User login
- `POST /refresh` - Refresh JWT token
- `POST /logout` - User logout

### **Users** (`/api/users`)

- `GET /` - Get all users (Admin)
- `GET /{id}` - Get user by ID
- `POST /` - Create user (Admin)
- `PUT /{id}` - Update user
- `DELETE /{id}` - Delete user (Admin)

### **Recipes** (`/api/recipes`)

- `GET /` - Get all recipes (with pagination, search, filtering)
- `GET /{id}` - Get recipe by ID
- `POST /` - Create recipe (with image upload)
- `PUT /{id}` - Update recipe
- `DELETE /{id}` - Delete recipe

### **Shopping Lists** (`/api/shopping-lists`)

- `GET /` - Get user's shopping lists
- `GET /{id}` - Get shopping list by ID
- `POST /` - Create shopping list from recipes
- `PUT /{id}` - Update shopping list
- `DELETE /{id}` - Delete shopping list

### **Comments** (`/api/recipes/{recipeId}/comments`)

- `GET /` - Get recipe comments
- `POST /` - Create comment
- `DELETE /{commentId}` - Delete comment

### **Likes** (`/api/recipes/{recipeId}/likes`)

- `POST /` - Like/dislike recipe
- `DELETE /` - Remove like

### **Favorites** (`/api/recipes/{recipeId}/favorites`)

- `POST /` - Add to favorites
- `DELETE /` - Remove from favorites

## **Testing**

### **Test Coverage**

- Unit tests for all service layers
- Integration tests 
- Controller tests with MockMvc
- Security tests for authentication
- Email service tests
- PDF generation tests

### **Running Tests**

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=RecipeServiceTest
```

##  **Getting Started**

### **Prerequisites**

- Java 21
- Maven 3.6+
- MySQL 8.0+
- Cloudinary account (for image storage)
- Gmail account (for email functionality)

### **Environment Variables**

Create a `.env` file in the project root:

```bash
# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/mykitchen_hub
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Server Configuration
SERVER_PORT=8080

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Email Configuration
EMAIL=your_email@gmail.com
EMAIL_PASSWORD=your_app_password

# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
CLOUDINARY_DEFAULT_IMAGE_URL=http://localhost:8080/images/logo.png
```

### **Running the Application**

#### **Using Maven**

```bash
# Clone the repository
git clone <repository-url>
cd mykitchen_hub

# Set up environment variables
cp .env.example .env
# Edit .env with your configuration

# Run the application
mvn spring-boot:run
```

#### **Using Docker**

##### **Docker Compose (Recommended)**

```bash
# Build and run all services (app + database)
docker-compose up --build

# Run in background
docker-compose up -d --build

# Stop all services
docker-compose down

# View logs
docker-compose logs -f

# Rebuild only the app service
docker-compose up --build app
```

## **API Documentation**

Once the application is running, access the Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

##  **Docker Configuration**

### **Dockerfile Details**

The application uses a multi-stage Docker build:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

**Key Features:**

- **Base Image**: Eclipse Temurin JRE 21 (lightweight, production-ready)
- **Working Directory**: `/app` for clean container structure
- **Port Exposure**: 8080 for Spring Boot application
- **JAR Execution**: Runs the compiled Spring Boot JAR file

### **Docker Compose Services**

#### **Application Service (`app`)**

```yaml
app:
  build: .
  ports:
    - "8080:8080"
  environment:
    - DB_URL=${DB_URL_DOCKER}
    - DB_USERNAME=${DB_USERNAME}
    - DB_PASSWORD=${DB_PASSWORD}
    - SERVER_PORT=${SERVER_PORT}
    - EMAIL=${EMAIL}
    - EMAIL_PASSWORD=${EMAIL_PASSWORD}
  depends_on:
    - db
  restart: unless-stopped
```

#### **Database Service (`db`)**

```yaml
db:
  image: mysql:8.0
  environment:
    MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
    MYSQL_DATABASE: mykitchen_hub
  ports:
    - "3306:3306"
  volumes:
    - mysql_data:/var/lib/mysql
  restart: unless-stopped
```

### **Docker Commands Reference**

#### **Development Commands**

```bash
# Start all services
docker-compose up

# Start in background
docker-compose up -d

# View logs
docker-compose logs -f app
docker-compose logs -f db

# Stop all services
docker-compose down

# Stop and remove volumes (deletes database data)
docker-compose down -v
```

#### **Build Commands**

```bash
# Build application image
docker build -t mykitchen-hub .
```

### **Docker Volumes**

- **`mysql_data`**: Persistent storage for MySQL database
- **Location**: `/var/lib/mysql` in container
- **Purpose**: Ensures data persistence across container restarts

## **Configuration**

### **Database Setup**

1. Create MySQL database: `mykitchen_hub`
2. Update database credentials in `.env`
3. Application will auto-create tables on startup

### **Cloudinary Setup**

1. Create account at cloudinary.com
2. Get API credentials from dashboard
3. Update Cloudinary variables in `.env`

### **Email Setup**

1. Enable 2-factor authentication on Gmail
2. Generate app-specific password
3. Update email credentials in `.env`

## **Performance & Monitoring**

### **Logging**

- Comprehensive logging with SLF4J
- Request/response logging
- Error tracking and monitoring
- Performance metrics logging

### **Security Features**

- JWT token-based authentication
- Password encryption with BCrypt
- CORS configuration for frontend integration
- Input validation and sanitization
- SQL injection prevention with JPA

## **COMPLETED FEATURES**

### **Authentication & Security**

- [x] JWT-based authentication with refresh tokens
- [x] Role-based authorization (USER, ADMIN)
- [x] Password encoding with BCrypt
- [x] Token blacklist for secure logout
- [x] User registration and login endpoints
- [x] Spring Security configuration with CORS support

### **User Management**

- [x] Complete user CRUD operations
- [x] User profile management
- [x] Email validation and uniqueness checks
- [x] Welcome email notifications
- [x] User role management
- [x] Pagination for user listings

### **Recipe Management**

- [x] Full recipe CRUD operations
- [x] Recipe search and filtering
- [x] Pagination and sorting
- [x] Image upload via Cloudinary integration
- [x] Recipe categorization with tags
- [x] Ingredient management with quantities and units
- [x] Recipe validation and error handling

### **Shopping List System**

- [x] Automated shopping list generation from recipes
- [x] Ingredient consolidation and merging logic
- [x] Shopping list CRUD operations
- [x] Email delivery of shopping lists
- [x] PDF generation for shopping lists
- [x] List item management with checkboxes

### **Social Features**

- [x] Comment system for recipes
- [x] Like/dislike functionality
- [x] Favorite recipes system
- [x] User interaction tracking
- [x] Social engagement metrics

### **Email & Notifications**

- [x] SMTP email configuration (Gmail)
- [x] Welcome email templates
- [x] Shopping list email notifications
- [x] HTML email templates with embedded logo
- [x] PDF attachment support
- [x] Email error handling and logging

### **Image Management**

- [x] Cloudinary integration for image storage
- [x] Image upload validation (size, format, type)
- [x] Automatic image optimization
- [x] Default image fallback
- [x] Image deletion and replacement
- [x] Support for multiple image formats (JPG, PNG, GIF, WebP)

### **Data Management**

- [x] MySQL database integration
- [x] JPA/Hibernate ORM
- [x] Database schema auto-generation
- [x] Data validation with Bean Validation
- [x] Transaction management
- [x] Database relationships and constraints

### **API & Documentation**

- [x] RESTful API design
- [x] Swagger/OpenAPI documentation
- [x] Comprehensive API documentation
- [x] Request/response examples
- [x] Error handling and status codes
- [x] API versioning support

### **Testing Infrastructure**

- [x] Unit test framework setup
- [x] Integration test configuration
- [x] Test data initialization
- [x] Mock service testing
- [x] Security test configuration
- [x] H2 in-memory database for testing

### **DevOps & Deployment**

- [x] Docker containerization
- [x] Docker Compose configuration
- [x] Maven build configuration
- [x] Environment variable management
- [x] Logging configuration

## **Contributing**

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## **License**

This project is part of the FemCoders Bootcamp individual project.

## **Team**

Developed as an individual project for FemCoders Bootcamp 2025.

---

**MyKitchen Hub** - Making cooking organized, social, and efficient! 🍳
