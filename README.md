# Event Booking API

[![Build and Test](https://github.com/majdsuliman98/event-booking-api/actions/workflows/ci.yaml/badge.svg)](https://github.com/majdsuliman98/event-booking-api/actions/workflows/ci.yaml)

A Spring Boot REST API for creating events and managing attendee registrations. Events have limited capacity: registrations are confirmed while space is available and automatically waitlisted once the event is full.

## Features

- Create, retrieve, update, delete, filter, paginate, and sort events
- Register attendees using a name and email address
- Automatically assign `CONFIRMED` or `WAITLISTED` status based on capacity
- Promote the earliest waitlisted attendee when a confirmed registration is cancelled
- Prevent duplicate email registrations for the same event
- Protect capacity allocation from concurrent registration requests with pessimistic locking
- Validate request bodies and return consistent API error responses
- Manage the PostgreSQL schema with versioned Flyway migrations
- Explore and test endpoints through Swagger UI
- Run unit, web-layer, and PostgreSQL integration tests
- Run the complete application stack with Docker Compose
- Build and test every push and pull request with GitHub Actions

## Technology Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA and Hibernate
- PostgreSQL 17
- Flyway
- Maven
- JUnit 5 and Mockito
- Testcontainers
- Springdoc OpenAPI and Swagger UI
- Docker and Docker Compose
- GitHub Actions

## Architecture

The application follows a layered, feature-based structure:

```text
src/main/java/com/majd/Event_booking
├── common/error       Global API error handling
├── event              Event controller, service, repository, entity, and DTOs
├── registration       Registration controller, service, repository, entity, and DTOs
├── EventBookingApplication.java
└── HealthController.java
```

Requests flow through the application as follows:

```text
HTTP request → Controller → Service → Repository → PostgreSQL
```

Controllers handle HTTP concerns, services contain business rules, and repositories provide database access.

## Quick Start with Docker

### Prerequisites

- Docker with Docker Compose

Build and start the API and PostgreSQL:

```bash
docker compose up --build -d
```

Check that the application is running:

```bash
curl -sS http://localhost:8080/api/health | jq
```

Expected response:

```json
{
  "status": "ok"
}
```

Open Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

View the generated OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

Follow application logs:

```bash
docker compose logs -f app
```

Stop the stack without deleting database data:

```bash
docker compose down
```

## API Endpoints

### Events

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/events` | List events with optional filtering, pagination, and sorting |
| `GET` | `/api/events/{eventId}` | Get an event by ID |
| `POST` | `/api/events` | Create an event |
| `PUT` | `/api/events/{eventId}` | Update an event |
| `DELETE` | `/api/events/{eventId}` | Delete an event |

### Registrations

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/events/{eventId}/registrations` | List registrations for an event |
| `POST` | `/api/events/{eventId}/registrations` | Register an attendee |
| `DELETE` | `/api/events/{eventId}/registrations/{registrationId}` | Cancel a registration |

### System

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | Check application health |

## Example Requests

Create an event:

```bash
curl -sS -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Spring Boot Meetup",
    "city": "Krakow",
    "capacity": 2,
    "startsAt": "2030-12-15T18:00:00Z"
  }' | jq
```

List events, five at a time, ordered by start time:

```bash
curl -sS \
  "http://localhost:8080/api/events?page=0&size=5&sort=startsAt,asc" | jq
```

Filter events by city:

```bash
curl -sS \
  "http://localhost:8080/api/events?city=Krakow&page=0&size=5" | jq
```

Register an attendee:

```bash
curl -sS -X POST http://localhost:8080/api/events/1/registrations \
  -H "Content-Type: application/json" \
  -d '{
    "attendeeName": "Alice Example",
    "attendeeEmail": "alice@example.com"
  }' | jq
```

When capacity is available, the response status is `CONFIRMED`. Once capacity is reached, later registrations receive `WAITLISTED`.

Cancel a registration:

```bash
curl -i -X DELETE \
  http://localhost:8080/api/events/1/registrations/1
```

Cancelling a confirmed registration promotes the earliest waitlisted registration to `CONFIRMED`.

## Pagination and Sorting

`GET /api/events` accepts Spring Data pagination parameters:

| Parameter | Example | Description |
|---|---|---|
| `page` | `page=0` | Zero-based page number |
| `size` | `size=10` | Number of events per page; maximum 100 |
| `sort` | `sort=startsAt,asc` | Entity property and direction |
| `city` | `city=Krakow` | Optional case-insensitive city filter |

Sorting also supports fields such as `id`, `name`, `city`, and `capacity`.

## Error Responses

Validation failures, missing resources, and conflicts use a consistent response format:

```json
{
  "timestamp": "2030-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "attendeeEmail: Attendee email must be valid",
  "path": "/api/events/1/registrations"
}
```

Common status codes:

- `400 Bad Request` — request validation failed
- `404 Not Found` — event or registration does not exist
- `409 Conflict` — email is already registered for the event

## Running Locally

### Prerequisites

- Java 21
- Docker with Docker Compose

Start only PostgreSQL:

```bash
docker compose up -d postgres
```

Run the application with the development profile:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

The default local database configuration can be overridden with environment variables:

| Variable | Default |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/event_booking` |
| `DB_USERNAME` | `event_user` |
| `DB_PASSWORD` | `event_password` |
| `SPRING_PROFILES_ACTIVE` | No profile |

The included credentials are development-only defaults and must not be used in production.

## Database Migrations

Flyway migrations are stored in:

```text
src/main/resources/db/migration
```

They run automatically during application startup. Hibernate uses `validate` mode to verify that the entity model matches the migrated schema without modifying it.

## Testing

Run the complete test suite:

```bash
./mvnw test
```

The suite includes:

- Unit tests for registration business rules using Mockito
- Web-layer tests for request validation
- Integration tests against a real PostgreSQL container
- An application context startup test

The integration tests require Docker because Testcontainers creates an isolated PostgreSQL database for the test run.

## Continuous Integration

The GitHub Actions workflow runs on every push and pull request. It provisions Java 21 and PostgreSQL, caches Maven dependencies, and executes:

```bash
./mvnw --batch-mode verify
```

The workflow definition is located at `.github/workflows/ci.yaml`.
