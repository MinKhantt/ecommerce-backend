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

Prerequisites
- JDK 25
- Postgres

Configure database
- Edit `src/main/resources/application.properties` with your Postgres URL, username, and password.

Configure Stripe
- Set `STRIPE_API_KEY` and `STRIPE_WEBHOOK_SECRET` in your `.env` file.
- In test mode, use the Stripe CLI to forward webhooks: `stripe listen --forward-to localhost:{port}/api/v1/payments/webhook`

Run the app
```bash
./mvnw spring-boot:run
```

## Notes

This is my Spring Boot learning project.
I plan to add unit tests and Swagger/OpenAPI docs next.
