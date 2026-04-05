-- Последовательности
COMMENT ON SEQUENCE roles_sequence IS 'Генератор ID для таблицы roles';
COMMENT ON SEQUENCE users_sequence IS 'Генератор ID для таблицы users';
COMMENT ON SEQUENCE cards_sequence IS 'Генератор ID для таблицы cards';
COMMENT ON SEQUENCE block_request_sequence IS 'Генератор ID для таблицы card_block_orders';

-- Таблица ролей
COMMENT ON TABLE roles IS 'Справочник ролей пользователей системы';
COMMENT ON COLUMN roles.id IS 'Уникальный идентификатор роли';
COMMENT ON COLUMN roles.name IS 'Название роли: ROLE_ADMIN или ROLE_USER';

-- Таблица пользователей
COMMENT ON TABLE users IS 'Пользователи системы управления банковскими картами';
COMMENT ON COLUMN users.id IS 'Уникальный идентификатор пользователя';
COMMENT ON COLUMN users.email IS 'Email пользователя, используется для аутентификации';
COMMENT ON COLUMN users.password IS 'Хэш пароля пользователя (BCrypt)';
COMMENT ON COLUMN users.status IS 'Статус аккаунта: ACTIVE, BLOCKED, DELETED';
COMMENT ON COLUMN users.role_id IS 'Ссылка на роль пользователя';
COMMENT ON COLUMN users.created_at IS 'Дата и время создания записи';
COMMENT ON COLUMN users.updated_at IS 'Дата и время последнего обновления';

-- Таблица карт
COMMENT ON TABLE cards IS 'Банковские карты пользователей';
COMMENT ON COLUMN cards.id IS 'Уникальный идентификатор карты';
COMMENT ON COLUMN cards.user_id IS 'Владелец карты';
COMMENT ON COLUMN cards.card_number_encrypted IS 'Номер карты зашифрованный AES-256, уникален';
COMMENT ON COLUMN cards.card_holder IS 'Имя владельца карты';
COMMENT ON COLUMN cards.expires_at IS 'Срок действия карты';
COMMENT ON COLUMN cards.status IS 'Статус карты: ACTIVE, BLOCKED, EXPIRED';
COMMENT ON COLUMN cards.balance IS 'Баланс карты в рублях';
COMMENT ON COLUMN cards.created_at IS 'Дата и время создания карты';
COMMENT ON COLUMN cards.updated_at IS 'Дата и время последнего обновления';

-- Таблица заявок на блокировку
COMMENT ON TABLE card_block_orders IS 'Заявки пользователей на блокировку карт';
COMMENT ON COLUMN card_block_orders.id IS 'Уникальный идентификатор заявки';
COMMENT ON COLUMN card_block_orders.card_id IS 'Карта на которую подана заявка';
COMMENT ON COLUMN card_block_orders.user_id IS 'Пользователь подавший заявку';
COMMENT ON COLUMN card_block_orders.status IS 'Статус заявки: PENDING, APPROVED, REJECTED';
COMMENT ON COLUMN card_block_orders.created_at IS 'Дата и время подачи заявки';
COMMENT ON COLUMN card_block_orders.updated_at IS 'Дата и время последнего обновления';