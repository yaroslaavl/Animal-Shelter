# Animal Shelter

## Overview
Animal Shelter is a platform where users can submit requests to become guardians for animals. Users fill out their personal information and apply for guardianship. The shelter’s administration reviews each request and can approve or decline it.

## Features
- User authorisation and authentication
- View animals available for care
- Submit and manage applications
- Admin panel to view and manage applications and animals
- Notifications to email and in-app notifications
- Application status tracking for users
- Admin monitoring system with Spring Boot Admin
- Animal medical records management by veterinarian
- Check medical records once a month

## Technologies Used
- Java
- Spring (Core, Boot, Data JPA, MVC, Security), Jakarta Validation (Hibernate Validator)
- Flyway
- Hibernate
- Redis
- PostgreSQL
- MapStruct
- Lombok
- Maven
- Docker
- MinIo

## Getting Started

### Prerequisites
- JDK 17 or later
- Maven
- PostgreSQL
- Docker (for containerization)

# Install dependencies
 ```

mvn clean install

 ```

### Configuring Email Settings
To properly configure email settings in your application:

Host: Set host to the SMTP server hostname. Example: smtp.example.com.

Port: Specify the port number used by the SMTP server. Example: 465 for SSL/TLS.

Username: Enter the email address used for authentication on the SMTP server. Example: your-email@example.com.

Password: To generate a 16-digit key for email configuration, follow these steps:

Log in to your Email Service Provider:
Access the website of your email service provider (e.g., Gmail, Yahoo Mail) and log in to your account.

Navigate to API or App Settings:
Find the section related to API access or app settings. Look for options related to generating API keys or app-specific passwords.

Generate New API Key or App Password:
Depending on the provider, select the option to generate a new API key or app password. This key will be used to authenticate your application when sending emails.

Copy and Save the Key:
Once generated, copy the 16-digit key or password provided by the email service provider. Ensure to save it securely as it will not be shown again.

Ensure these settings are correctly configured in application.yml file to enable email functionality in application.

### Installation
1. Clone the repository:


git clone https://github.com/yaroslaavl/Animal-Shelter.git

### Running Spring Boot Admin Server
  ```

mvn -pl adminServer spring-boot:run

 ```

### Running Spring Boot Server
 ```

mvn spring-boot:run

```

## With Docker

### Build Docker images:
 ```

docker-compose build

```

### Start Docker Containers:
 ```

docker-compose up

```

### Contact
Email - yaroslav.lopatkin.work@gmail.com
