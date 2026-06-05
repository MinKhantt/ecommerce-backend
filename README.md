# Shopping Cart API

A Spring Boot learning project I built as a student to practice building a simple eCommerce backend. It covers authentication, products, categories, carts, orders and image uploads with a clean layered structure.

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

Products (`/products`)
- `GET /products`
- `GET /products/product/{productId}`
- `POST /products`
- `PUT /products/product/{productId}`
- `DELETE /products/product/{productId}`
- `GET /products/by-brand-and-name`
- `GET /products/by-category-and-brand`
- `GET /products/by-name`
- `GET /products/by-brand`
- `GET /products/by-category`
- `GET /products/count/by-brand-and-name`

Categories (`/categories`)
- `GET /categories`
- `POST /categories`
- `GET /categories/{id}`
- `GET /categories/name/{name}`
- `DELETE /categories/{id}`
- `PUT /categories/{id}`

Images (`/images`)
- `POST /images/upload`
- `GET /images/view/{imageId}`
- `GET /images/download/{imageId}`
- `PUT /images/{imageId}`
- `DELETE /images/{imageId}`

Carts (`/carts`)
- `GET /carts/{cartId}/my-cart`
- `DELETE /carts/{cartId}/clear`
- `GET /carts/{cartId}/cart/total-price`

Cart Items (`/cartItems`)
- `POST /cartItems/item/add`
- `DELETE /cartItems/cart/{cartId}/item/{itemId}/remove`
- `PUT /cartItems/cart/{cartId}/item/{itemId}/update`

Orders (`/orders`)
- `POST /orders/order`
- `GET /orders/{orderId}/order`
- `GET /orders/{userId}/order`

Users (`/users`)
- `GET /users/{userId}`
- `POST /users`
- `PUT /users/{userId}`
- `DELETE /users/{userId}
- `GET /users/by-email

Auth (`/auths`)
- `POST /auths/register`
- `POST /auths/login`

## Getting Started

Prerequisites
- JDK 25
- Postgres

Configure database
- Edit `src/main/resources/application.properties` with your Postgres URL, username, and password.

Run the app
```bash
./mvnw spring-boot:run
```

## Notes

This is my Spring Boot learning project.
I plan to add unit tests and Swagger/OpenAPI docs next.
