# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build executable JAR
./gradlew clean bootJar

# Run locally
./gradlew bootRun

# Run tests
./gradlew test

# Single test class
./gradlew test --tests "com.backslash.kmorgan.SomeTest"

# SonarQube analysis
./gradlew sonar
```

The app runs on port 8080. Swagger UI is available at `/swagger-ui.html`.

## Project Overview

Real estate property management platform (Kmorgan) built with Spring Boot 3.4.5 / Java 17 / Gradle.

Multi-role system: Admin, Agent, Seller, Buyer, Non-Member.

## Package Structure

```
com.backslash.kmorgan/
├── api/
│   ├── admin/          # 35+ admin controllers
│   ├── member/         # agent/, seller/, buyer/, common/ controllers
│   └── common/         # shared endpoints
├── domain/             # business logic per domain
│   ├── member/
│   ├── admin/
│   ├── property/       # real estate listings (sale, lease, details)
│   ├── purchase/
│   ├── thumbnail/
│   └── ...             # ~15 other domain packages
└── common/
    ├── auth/           # JWT + session management
    ├── config/         # Security, JPA, QueryDSL, Swagger, S3, etc.
    ├── oauth/          # Naver, Kakao, Apple
    ├── payment/        # NicePayment integration
    ├── nice/           # NICE authentication
    ├── openapi/        # Land data, Building registry, Floor plan APIs
    ├── s3/             # AWS S3 uploads
    └── exception/      # Global exception handling
```

## Configuration

Spring profiles are split across multiple YML files loaded at startup:
- `application-db.yml` — MariaDB connection, JPA/Hibernate (`ddl-auto: update`)
- `application-security.yml` — JWT settings
- `application-oauth.yml` — Naver/Kakao/Apple OAuth credentials
- `application-aws.yml` — S3 bucket & credentials
- `application-api.yml` — External API keys (V-World, Kakao, Public Data)
- `application-nice.yml` — NICE payment/auth
- `application-payments.yml` — Payment processing
- `application-mail.yml` — SMTP config

Secrets come from `env/*.env` files (DB_URL, JWT_CLIENT_SECRET, AWS_ACCESS_KEY_ID, etc.) — these are not committed.

## Security Architecture

Two separate security filter chains:
- **Member chain**: `/member/**`, `/seller/**`, `/agent/**`, `/buyer/**`, `/common/**`, `/nonMember/**` — JWT Bearer token auth
- **Admin chain**: `/admin/**` — Custom session-based auth (header: `X-KMORGAN-SID`)

JWT filter runs before the standard authentication filter. Refresh tokens are supported.

CORS allows `localhost:3000/3001/5173/8080` and production domains with credentials.

## Database

MariaDB with JPA auditing. `BaseEntity` provides auto-increment Long ID, `createdAt`, `updatedAt`, `deletedAt` (soft deletes). QueryDSL is used for complex queries. Batch size is 1000.

Active profiles for development: `develop` (SQL logging), `db-create` (schema recreation), `debug`.

## External Integrations

- **NICE**: Identity verification & payment
- **AWS S3**: File/image storage
- **Firebase Admin SDK**: Push notifications
- **V-World / Public Building Registry**: Real estate data APIs
- **Naver/Kakao/Apple OAuth**: Social login
- **Apache POI**: Excel/Word/PowerPoint generation

## CI/CD

Jenkins pipeline (`Jenkinsfile`): checkout → chmod gradlew → SonarQube → `./gradlew clean bootJar` → archive artifacts → Slack notification.
