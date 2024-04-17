CREATE TYPE payment_status AS ENUM (
    'PAY_NONE',
    'PAY_IN_PROCESS',
    'PAY_DONE'
);
CREATE CAST (varchar AS payment_status) WITH INOUT AS IMPLICIT;

ALTER TABLE books
ADD COLUMN payment_status payment_status NOT NULL DEFAULT 'NO_PAYMENT';