ALTER TABLE books
ADD COLUMN rating INTEGER CHECK (rating BETWEEN 0 AND 100)