CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  first_name VARCHAR(256) NOT NULL,
  last_name VARCHAR(256) NOT NULL,
  email VARCHAR(256) NOT NULL,
  password VARCHAR(256) NOT NULL
);

--;;

ALTER TABLE todos
  ADD COLUMN user_id INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE;