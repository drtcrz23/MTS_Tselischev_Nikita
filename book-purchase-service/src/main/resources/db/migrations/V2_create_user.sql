CREATE TABLE user_table
(
    id   BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    balanse INTEGER NOT NULL CHECK (balance >= 0)
)