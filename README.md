# CampusGo - (Microservices-based Campus Service Platform) Project plan

> **CampusGo** is a microservices-based campus service platform for NUS, inspired by Grab but focused on **errand services** and **food ordering (delivery/self-pickup)**.

# 1. Introduction

CampusGo aims to provide convenient one-stop services for students and staff within campus:  
- **Errand Service**: Pick up parcels, buy items, deliver goods  
- **Food Ordering**: Support both self-pickup and delivery from canteens and shops  
- **Notification & Review**: Order updates and feedback loop  

The project adopts a **Spring Boot microservices architecture + Next.js frontend + Docker/Kubernetes deployment**, ensuring fast iteration, scalability, and maintainability.

---

# 2 System Architecture

## 2.1 Technology Stack
- **Frontend**: Next.js (Web for users & runners)  
- **API Gateway**: Nginx  
- **Authentication & Users**:  SpringSecurity + JWT  
- **Microservices**: Spring Boot, domain-driven design  
- **Database**:  MySQL  
- **Cache & Queue**: Redis / Kafka  
- **Object Storage**: MinIO (for images, receipts, etc.)  
- **Monitoring & Logging**: Prometheus + Grafana + ELK  

## 2.2 Core Microservices
- **identity-auth-service**: Login/authentication, JWT tokens, NUS SSO integration  
- **user-profile-service**: User profile & frequent locations  
- **catalog-merchant-service**: Merchants, menus, operating hours  
- **order-orchestration-service**: Unified order flow (errand/food), order state machine  
- **dispatch-matching-service**: Order dispatching (zone/nearest runner)  
- **runner-service**: Runner status, online/offline, order handling  
- **notification-service**: Notification center (in-app, email, push)  
- **rating-review-service**: Order reviews and ratings  
- **payment-service (optional)**: Integration with campus payments or mock service  


## 2.3 Project Structure
CampusGo/
 ├─ gateway/               # API Gateway config
 ├─ services/
 │   ├─ identity-auth/
 │   ├─ user-profile/
 │   ├─ catalog-merchant/
 │   ├─ order-orchestration/
 │   ├─ dispatch-matching/
 │   ├─ runner/
 │   ├─ notification/
 │   ├─ payment/
 │   └─ rating-review/
 │
 ├─ web-app/               # Next.js frontend
 ├─ mobile-app/            # Flutter mobile app (optional)
 ├─ deploy/
 │   └─ docker/            # Dockerfiles
 └─ docs/                  # Documentation (architecture, API, ADR)


## 2.4 Core functionalities 

### Errand Order
1. User selects **pickup/dropoff** POIs and places order  
2. `order-orchestration` records order, publishes `ORDER_CREATED` event  
3. `dispatch-matching` consumes event, finds nearby runner  
4. Runner accepts → `RUNNER_ASSIGNED` → notify user  
5. Runner completes → `ORDER_COMPLETED` → user review  

### Food Ordering
- **Self-pickup**: Place order → merchant confirms → user picks up → complete  
- **Delivery**: Merchant → `dispatch-matching` → runner assigned → delivery completed  

---

## 2.5 Database Design

### 2.5.1 Users
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_uid VARCHAR(64) NOT NULL UNIQUE COMMENT 'NUS ID / Login ID',
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('STUDENT','STAFF','RUNNER','ADMIN') NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2.5.2 Runner
```sql
CREATE TABLE runners (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vehicle ENUM('FOOT','BIKE') NOT NULL DEFAULT 'FOOT',
    online BOOLEAN NOT NULL DEFAULT FALSE,
    last_seen_at DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_runner_user FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 2.5.3 merchants
```sql
CREATE TABLE merchants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category ENUM('FOOD','SHOP','SERVICE') NOT NULL DEFAULT 'FOOD',
    address VARCHAR(255) NOT NULL,
    place_id VARCHAR(64) NOT NULL UNIQUE,
    phone VARCHAR(20),
    opening_hours VARCHAR(255) COMMENT '例如 09:00-21:00',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2.5.4 MenuItems
```sql
CREATE TABLE menu_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_menu_merchant FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE CASCADE
);
```


### 2.5.5 orders
```sql
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_code VARCHAR(64) NOT NULL UNIQUE COMMENT 'e.g., ord_001',
    type ENUM('ERRAND','FOOD_DELIVERY','FOOD_PICKUP') NOT NULL,
    status ENUM('PENDING_ASSIGNMENT','ASSIGNED','EN_ROUTE','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PENDING_ASSIGNMENT',
    pickup_place_id VARCHAR(64) NOT NULL,
    dropoff_place_id VARCHAR(64),
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    assigned_runner_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_runner FOREIGN KEY (assigned_runner_id) REFERENCES runners(id)
    CONSTRAINT fk_order_runner
        FOREIGN KEY (assigned_runner_id) REFERENCES runners(id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    CONSTRAINT fk_order_merchant
        FOREIGN KEY (merchant_id) REFERENCES merchants(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
    
);
```

### 2.5.6 orders_items
```sql
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
、、、






