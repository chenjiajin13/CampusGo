# Production Deployment Prep (Backend)

## 1) Environment variables
Copy `.env.prod.example` to `.env.prod` and fill real values.

Key required variables:
- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_CLOUD_CONFIG_PROFILE=prod`
- `CONFIG_SERVER_URL`
- `EUREKA_SERVER_URL`
- `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
- `KAFKA_BOOTSTRAP_SERVERS`
- `AUTH_JWT_SECRET`
- `CORS_ALLOWED_ORIGIN_PATTERNS`

## 2) Recommended DB password
Use:
- `DB_PASSWORD=Cg0Db_2026!R9vT4m_CampusGo`
- `MYSQL_ROOT_PASSWORD=Cg0Db_2026!R9vT4m_CampusGo`

## 3) SQL init strategy
For production profile (`*-prod.yaml`), `spring.sql.init.mode=never` is enabled to avoid re-seeding data.

## 4) Docker infra notes
- `docker-compose.mysql.yml` now reads `MYSQL_ROOT_PASSWORD` from env.
- `docker-compose.kafka.yml` now reads `KAFKA_ADVERTISED_HOST` for cloud network.
- `docker-compose.redis.yml` cleaned for current compose format.

## 5) Service discovery
`auth-service` Admin Feign client now uses Eureka (`name=admin-service`) and no localhost fallback URL.

## 6) CORS whitelist
Gateway now uses `CORS_ALLOWED_ORIGIN_PATTERNS` (do not keep wildcard in production).

## 7) Observability
Actuator is enabled across all business services, and prod config exposes:
- `health`
- `info`
- `prometheus`

## 8) Start order (recommended)
1. MySQL / Redis / Kafka
2. Config Server
3. Eureka
4. Core services (auth, user, merchant, runner, order, payment, notification, admin)
5. Gateway

## 9) Quick checks
- `GET /actuator/health` for each service
- login -> add cart -> checkout -> notifications/inbox
- merchant/runner wallet settlement flows
