# Ecommerce Backend

A Spring Boot learning project I built as a student to practice building a simple eCommerce backend. 
It covers authentication, products, categories, carts, orders, payments and image uploads with a clean layered structure.

## What I Practiced

- Spring Security with JWT (stateless auth) & OAuth2
- JPA/Hibernate relationships and repositories
- DTOs and request/response models
- Validation and global exception handling
- Building REST APIs with controllers and services

## Tech Stack

- Spring Boot 3.5.14
- Java 21
- Postgres
- Spring Security + JJWT
- OAuth2
- Redis
- Cloudinary
- Stripe
- Springdoc OpenAPI (Swagger UI)
- Lombok, ModelMapper, Jakarta Validation

## Project Structure

- `config/` application configuration
- `controller/` REST endpoints
- `service/` business logic
- `repository/` data access
- `entity/` entities
- `dto/` API contracts
- `mapper/` entity–DTO mapping
- `security/` JWT and security config
- `exception/` global error handling

## Base URL

All endpoints start with `/api/v1`.

## Endpoints

Access levels:
- `(Public)` — no authentication required
- `(Authenticated)` — any valid JWT token
- `(Admin)` — requires `ROLE_ADMIN` role

Auth (`/auth`)
- `POST /auth/register` (Public)
- `POST /auth/login` (Public)

Products (`/products`)
- `GET /products` (Public)
- `GET /products/product/{productId}` (Public)
- `POST /products` (Admin)
- `PUT /products/product/{productId}` (Admin)
- `DELETE /products/product/{productId}` (Admin)
- `GET /products/by-brand-and-name` (Public)
- `GET /products/by-category-and-brand` (Public)
- `GET /products/by-name` (Public)
- `GET /products/by-brand` (Public)
- `GET /products/by-category` (Public)
- `GET /products/count/by-brand-and-name` (Public)

Categories (`/categories`)
- `GET /categories` (Public)
- `POST /categories` (Admin)
- `GET /categories/{id}` (Public)
- `GET /categories/name/{name}` (Public)
- `DELETE /categories/{id}` (Admin)
- `PUT /categories/{id}` (Admin)

Images (`/images`)
- `POST /images/upload` (Admin)
- `GET /images/view/{imageId}` (Public)
- `GET /images/download/{imageId}` (Public)
- `PUT /images/{imageId}` (Admin)
- `DELETE /images/{imageId}` (Admin)

Carts (`/carts`)
- `GET /carts` (Authenticated)
- `DELETE /carts` (Authenticated)
- `GET /carts/total-price` (Authenticated)

Cart Items (`/cartItems`)
- `POST /cartItems/add?productId={productId}&quantity={quantity}` (Authenticated)
- `PUT /cartItems/update/{productId}?quantity={quantity}` (Authenticated)
- `DELETE /cartItems/remove/{productId}` (Authenticated)

Orders (`/orders`)
- `POST /orders` (Authenticated)
- `GET /orders` (Admin)
- `GET /orders/{orderId}` (Authenticated)
- `GET /orders/my-orders` (Authenticated)
- `DELETE /orders/{orderId}` (Authenticated)
- `PATCH /orders/{orderId}/order-status?status={status}` (Admin)

Payments (`/payments`)
- `POST /payments/create-intent` (Authenticated)
- `GET /payments/{paymentId}` (Authenticated)
- `GET /payments/my-payments` (Authenticated)
- `GET /payments` (Admin)
- `PATCH /payments/{paymentId}/status?status={status}` (Admin)
- `DELETE /payments/{paymentId}/cancel` (Authenticated)
- `POST /payments/webhook` (Public — Stripe webhook)

Users (`/users`)
- `GET /users/{userId}` (Public)
- `GET /users` (Admin)
- `POST /users` (Public)
- `PUT /users/{userId}` (Admin)
- `DELETE /users/{userId}` (Admin)
- `GET /users/by-email?email={email}` (Public)

## Getting Started

### Prerequisites
- Java 21
- Docker & Docker Compose
- [Neon PostgreSQL](https://neon.tech) database (free tier) or local postgreSQL driver
- [Redis Cloud](https://redis.com/try-free) instance (free 30MB tier)

### Setup

1. Clone the repo:
   ```bash
   git clone <your-repo-url>
   cd ecommerce-backend
   ```

2. Copy `.env.example` to `.env.prod` and fill in your credentials:
   ```bash
   cp .env.example .env.prod
   ```

3. Run with Docker Compose:
   ```bash
   docker compose up --build
   ```

   The app starts at `http://localhost:9000`.

### Running without Docker

```bash
./mvnw spring-boot:run
```

---

## Docker

The project includes ready-to-use Docker configuration:

| File | Purpose |
|---|---|
| `Dockerfile` | Multi-stage build — Maven compiles the app, then JRE runs the JAR |
| `.dockerignore` | Excludes `.git/`, `target/`, `.env*`, `docs/` from the build context |
| `docker-compose.yml` | Runs the app with env vars loaded from `.env.prod` |

### Build & Run

```bash
docker compose up --build
```

To run in the background:
```bash
docker compose up --build -d
```

---

## Redis Cloud

This app uses Redis for caching. Sign up for a free 30MB instance at [redis.com/try-free](https://redis.com/try-free).

Once created, configure these in `.env.prod`:

```env
REDIS_HOST=your_redis_host
REDIS_PORT=your_redis_port 
REDIS_PASSWORD=your_redis_password
```

The host and port are shown in your Redis Cloud dashboard under **Public Endpoint**.

---

## Deploy to Render

1. Push the repo to GitHub
2. [Render Dashboard](https://dashboard.render.com) → **New Web Service** → Connect your repo
3. Runtime: **Docker** (auto-detected from `Dockerfile`)
4. Set all environment variables from `.env.prod` in Render's dashboard (do not commit them)
5. Port: **9000**
6. Deploy — your app will be live at `https://your-app.onrender.com`
7. Swagger docs at `https://your-app.onrender.com/docs`

Render builds using the Dockerfile — no docker-compose.yml needed on Render.

---

## Notes

- This is my Spring Boot learning project.
- Swagger/OpenAPI docs are available at `/docs`.
- I plan to add unit tests next.
