CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    amount_cents BIGINT NOT NULL,
    currency VARCHAR(8) NOT NULL DEFAULT 'SGD',
    method VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    provider_txn_id VARCHAR(128) NULL,
    extra JSON NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_payment_order (order_id)
);

CREATE TABLE IF NOT EXISTS wallet_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_type VARCHAR(32) NOT NULL,
    owner_id BIGINT NOT NULL,
    balance_cents BIGINT NOT NULL DEFAULT 0,
    frozen_cents BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_wallet_owner (owner_type, owner_id)
);

CREATE TABLE IF NOT EXISTS wallet_transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    biz_type VARCHAR(32) NOT NULL,
    direction VARCHAR(16) NOT NULL,
    amount_cents BIGINT NOT NULL,
    order_id BIGINT NULL,
    idempotency_key VARCHAR(128) NOT NULL,
    remark VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_wallet_txn_idem (idempotency_key),
    KEY idx_wallet_txn_account (account_id),
    KEY idx_wallet_txn_order (order_id)
);

CREATE TABLE IF NOT EXISTS order_payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    runner_id BIGINT NULL,
    amount_cents BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    idempotency_key VARCHAR(128) NOT NULL,
    paid_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_payment_order (order_id),
    UNIQUE KEY uk_order_payment_idem (idempotency_key)
);

CREATE TABLE IF NOT EXISTS order_settlement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    runner_id BIGINT NOT NULL,
    gross_cents BIGINT NOT NULL,
    merchant_cents BIGINT NOT NULL,
    runner_cents BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    idempotency_key VARCHAR(128) NOT NULL,
    settled_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_settlement_order (order_id),
    UNIQUE KEY uk_order_settlement_idem (idempotency_key)
);
