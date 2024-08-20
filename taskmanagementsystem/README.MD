### Инструкция по запуску через Docker

1. **Убедитесь, что у вас установлены Docker и Docker Compose.**
   - [Установка Docker](https://docs.docker.com/get-docker/)
   - [Установка Docker Compose](https://docs.docker.com/compose/install/)

2. **Создайте файл `Dockerfile` с следующим содержимым:**

```Dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x gradlew

RUN ./gradlew build -x test

CMD ["java", "-jar", "build/libs/taskmanagementsystem-0.0.1-SNAPSHOT.jar"]
```

3. **Создайте файл `docker-compose.yml` с следующим содержимым:**

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/taskmanagement
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
    depends_on:
      - db

  db:
    image: postgres:16
    environment:
      POSTGRES_DB: taskmanagement
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
     - "/var/lib/postgresql/data"
```

4. **Запустите Docker Compose:**

```sh
docker-compose up --build
```

5. **Приложение будет доступно по адресу:**

```
http://localhost:8080
```

### Описание для README.md

```markdown
# Task Management System

## Описание

Task Management System - это приложение для управления задачами, предоставляющее API для создания, обновления, удаления и получения задач.

## Запуск через Docker

### Предварительные требования

- Docker
- Docker Compose

### Инструкция по запуску

1. Убедитесь, что у вас установлены Docker и Docker Compose.
   - [Установка Docker](https://docs.docker.com/get-docker/)
   - [Установка Docker Compose](https://docs.docker.com/compose/install/)

2. Создайте файл `Dockerfile` с следующим содержимым:

    ```Dockerfile
    FROM openjdk:21-jdk-slim

    WORKDIR /app

    COPY build.gradle settings.gradle gradlew ./
    COPY gradle ./gradle
    COPY src ./src

    RUN chmod +x gradlew

    RUN ./gradlew build -x test

    CMD ["java", "-jar", "build/libs/taskmanagementsystem-0.0.1-SNAPSHOT.jar"]
    ```

3. Создайте файл `docker-compose.yml` с следующим содержимым:

    ```yaml
    version: '3.8'

    services:
      app:
        build: .
        ports:
          - "8080:8080"
        environment:
          SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/taskmanagement
          SPRING_DATASOURCE_USERNAME: postgres
          SPRING_DATASOURCE_PASSWORD: postgres
        depends_on:
          - db

      db:
        image: postgres:16
        environment:
          POSTGRES_DB: taskmanagement
          POSTGRES_USER: postgres
          POSTGRES_PASSWORD: postgres
        ports:
          - "5432:5432"
        volumes:
         - "/var/lib/postgresql/data"
    ```

4. Запустите Docker Compose:

    ```sh
    docker-compose up --build
    ```

5. Приложение будет доступно по адресу:

    ```
    http://localhost:8080
    ```

## API Документация

API документация доступна по адресу:

```
http://localhost:8080/swagger-ui/
```

## Лицензия

Этот проект лицензирован под лицензией MIT. Подробности см. в файле LICENSE.
```

Если у вас есть еще вопросы или нужна помощь с чем-то еще, дайте знать! 😊
