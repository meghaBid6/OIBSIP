# ATM Interface – Spring Boot

## Project Overview

The ATM Interface is a Java-based banking application developed using Spring Boot. It provides REST APIs to perform basic ATM operations and manage account-related activities.

## Features

- Create a new account
- User login with PIN validation
- Check account balance
- Deposit money
- Withdraw money
- Transfer money between accounts
- Change ATM PIN
- View transaction history
- Lock account after repeated failed login attempts
- Input validation and centralized exception handling
- Password/PIN hashing using BCrypt
- API documentation using Swagger UI

## Technologies Used

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- Hibernate
- MySQL
- Maven
- REST APIs
- Swagger / OpenAPI
- IntelliJ IDEA
- Postman

## Prerequisites

Before running this project, install:

- Java JDK 17 or later
- MySQL Server
- IntelliJ IDEA or another Java IDE
- Maven (optional if using the included Maven Wrapper)

## Database Configuration

1. Create a MySQL database named `atm_db`.
2. Configure the database username and password using environment variables.
3. Check `src/main/resources/application.properties` for the database configuration.

Set the `DB_PASSWORD` environment variable to your own MySQL password before starting the application.

**Do not commit real passwords or other secrets to GitHub.**

## How to Run

1. Clone this repository.
2. Open the `Java-Task-ATMInterface` folder in IntelliJ IDEA.
3. Configure the `DB_PASSWORD` environment variable.
4. Make sure MySQL is running and the `atm_db` database exists.
5. Run `AtminterfaceApplication.java`.
6. Use Postman to test the API endpoints.

## API Documentation

If Swagger is enabled and the application starts successfully, open:

`http://localhost:8080/swagger-ui/index.html`

## Testing

REST APIs can be tested using Postman. Check successful responses and validation/error responses for the supported ATM operations.

## Author

Megha Bid

## License

This project was created for internship and educational purposes.