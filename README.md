# Chat-App

Messenger backend with REST API for exchanging messages between users

## Stack

- Java 17
- Spring Boot 4.0.3
- Spring Security (session-based authentication)
- Spring Data JPA + Hibernate
- Spring Session + Redis
- PostgreSQL
- H2 (test profile)
- OpenAPI 3.0 / Swagger UI
- Gradle

## Requirements

- JDK 17+
- PostgreSQL
- Redis

## Getting Started
```bash
git clone https://github.com/uneronnik/Chat-App.git
cd Chat-App
```

Configure the database connection in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/chatapp
spring.datasource.username=your_user
spring.datasource.password=your_password
```

Start Redis:
```bash
redis-server
```

Run the application:
```bash
./gradlew bootRun
```

The app will be available at `http://localhost:8080/swagger-ui.html`.

## API

| Method | Path                       | Description                                | Auth     |
|--------|----------------------------|--------------------------------------------|----------|
| POST   | `/auth/register`           | Register a new user                        | No       |
| POST   | `/auth/login`              | Log in (create session)                    | No       |
| POST   | `/auth/logout`             | Log out (invalidate session)               | Yes      |
| POST   | `/messages/{username}`     | Send a message to a user                   | Yes      |
| GET    | `/messages/{username}`     | Get conversation history with a user       | Yes      |
| GET    | `/messages`                | Get a list of all conversations            | Yes      |

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Tests
```bash
./gradlew test
```

Tests use an H2 in-memory database and do not require PostgreSQL or Redis.
