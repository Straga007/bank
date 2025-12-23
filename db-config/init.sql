-- Создание таблицы accounts, если она не существует
CREATE TABLE IF NOT EXISTS accounts (
    user_id VARCHAR(255) PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    birth_date DATE,
    balance NUMERIC(19, 2) DEFAULT 0.00
);