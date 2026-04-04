CREATE SEQUENCE block_request_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE card_block_orders (
                                   id          BIGINT PRIMARY KEY DEFAULT nextval('block_request_sequence'),
                                   card_id     BIGINT NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
                                   user_id     BIGINT NOT NULL REFERENCES users(id),
                                   status      VARCHAR(20) NOT NULL
                                       CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
                                   created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
                                   updated_at  TIMESTAMP
);

CREATE INDEX idx_card_block_orders_card_status ON card_block_orders(card_id, status);