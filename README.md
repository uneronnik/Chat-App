# Chat-App

Backend мессенджера с поддержкой real-time обмена сообщениями между пользователями.

## Стек

- Java 17
- Spring Boot 4.0.3
- Spring Security (сессионная аутентификация)
- Spring Data JPA + Hibernate
- Spring Session + Redis
- WebSocket
- PostgreSQL
- H2 (тестовый профиль)
- OpenAPI 3.0 / Swagger UI
- Gradle

## Требования

- JDK 17+
- PostgreSQL
- Redis

## Запуск
```bash
git clone https://github.com/your-username/Chat-App.git
cd Chat-App
```

Настроить подключение к БД в `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/chatapp
spring.datasource.username=your_user
spring.datasource.password=your_password
```

Запустить Redis:
```bash
redis-server
```

Запустить приложение:
```bash
./gradlew bootRun
```

Приложение будет доступно на `http://localhost:8080`.

## API

| Метод  | Путь                       | Описание                                   | Авторизация |
|--------|----------------------------|--------------------------------------------|-------------|
| POST   | `/auth/register`           | Регистрация нового пользователя            | Нет         |
| POST   | `/auth/login`              | Вход (создание сессии)                     | Нет         |
| POST   | `/auth/logout`             | Выход (инвалидация сессии)                 | Да          |
| POST   | `/messages/{username}`     | Отправить сообщение пользователю           | Да          |
| GET    | `/messages/{username}`     | Получить историю переписки с пользователем | Да          |
| GET    | `/messages`                | Получить список всех собеседников          | Да          |

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Тесты
```bash
./gradlew test
```

Тесты используют H2 in-memory базу и не требуют PostgreSQL или Redis.
