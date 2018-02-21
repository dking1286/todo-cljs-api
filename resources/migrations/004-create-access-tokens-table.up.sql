CREATE TABLE access_tokens (
  id SERIAL PRIMARY KEY,
  client_id INTEGER NOT NULL REFERENCES clients (id) ON DELETE CASCADE,
  user_id INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE
);