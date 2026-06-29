CREATE TABLE users (
    id UUID NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uc_users_email UNIQUE (email)
);

CREATE TABLE roles (
    id UUID NOT NULL,
    name VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE categories (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uc_categories_name UNIQUE (name)
);

CREATE TABLE products (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255),
    price DECIMAL NOT NULL,
    inventory INTEGER,
    description VARCHAR(1000),
    category_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE carts (
    id UUID NOT NULL,
    total_amount DECIMAL,
    user_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_carts PRIMARY KEY (id),
    CONSTRAINT uc_carts_user UNIQUE (user_id),
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE cart_items (
    id UUID NOT NULL,
    quantity INTEGER,
    unit_price DECIMAL,
    total_price DECIMAL,
    product_id UUID,
    cart_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_cart_items PRIMARY KEY (id),
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id)
);

CREATE TABLE orders (
    id UUID NOT NULL,
    order_date TIMESTAMP,
    total_amount DECIMAL,
    order_status VARCHAR(255),
    shipping_address VARCHAR(255) NOT NULL,
    user_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_items (
    id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL NOT NULL,
    order_id UUID,
    product_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE payments (
    id UUID NOT NULL,
    amount DECIMAL,
    payment_status VARCHAR(255),
    payment_date TIMESTAMP,
    payment_method VARCHAR(255),
    currency VARCHAR(255),
    payment_provider VARCHAR(255),
    external_transaction_id VARCHAR(255),
    order_id UUID,
    user_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_payments PRIMARY KEY (id),
    CONSTRAINT uc_payments_order UNIQUE (order_id),
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_payments_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_payments_external_transaction_id ON payments(external_transaction_id);
CREATE INDEX idx_payments_order_id ON payments(order_id);

CREATE TABLE images (
    id UUID NOT NULL,
    file_name VARCHAR(255),
    file_type VARCHAR(255),
    download_url VARCHAR(255),
    public_id VARCHAR(255),
    product_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_images PRIMARY KEY (id),
    CONSTRAINT fk_images_product FOREIGN KEY (product_id) REFERENCES products(id)
);
