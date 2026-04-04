CREATE SEQUENCE users_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE roles_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE cards_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE roles
(
    id   BIGINT PRIMARY KEY DEFAULT nextval('roles_sequence'),
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE users
(
    id         BIGINT PRIMARY KEY    DEFAULT nextval('users_sequence'),
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'BLOCKED', 'DELETED')),
    role_id    BIGINT       NOT NULL REFERENCES roles (id),
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE cards
(
    id                    BIGINT PRIMARY KEY      DEFAULT nextval('cards_sequence'),
    user_id               BIGINT         NOT NULL REFERENCES users (id),
    card_number_encrypted VARCHAR(512)   NOT NULL UNIQUE ,
    card_holder           VARCHAR(255)   NOT NULL,
    expires_at            DATE           NOT NULL,
    status                VARCHAR(20)    NOT NULL CHECK (status IN ('ACTIVE', 'BLOCKED', 'EXPIRED')),
    balance               DECIMAL(19, 2) NOT NULL CHECK (balance >= 0),
    created_at            TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP
);

CREATE INDEX idx_cards_user_id ON cards (user_id);