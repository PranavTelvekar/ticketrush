# 🎟️ TicketRush

> 🚧 Work in progress

A ticket booking REST API that stops two users from booking the same seat at once, built with **Spring Boot 4.1.1**, **Java 25**, **PostgreSQL** and **Redis**.

## Planned features
- JWT authentication with USER / ADMIN roles
- Venues, events and seat layouts
- Seat holds that expire automatically (Redis TTL)
- Prevents double booking (Redis locks + optimistic locking)
- Payment endpoint that is safe to retry (idempotency keys)
- Database migrations with Liquibase
- Docker Compose, Testcontainers, GitHub Actions CI

## Tech stack
Java 25 · Spring Boot 4.1.1 · Spring Security · Spring Data JPA · PostgreSQL · Redis · Liquibase · Docker
