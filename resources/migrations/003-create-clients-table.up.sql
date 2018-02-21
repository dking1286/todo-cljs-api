CREATE TABLE clients (
  id SERIAL PRIMARY KEY,
  client_id VARCHAR(32)
    NOT NULL CHECK (character_length(client_id) = 32),
  client_secret VARCHAR(32)
    NOT NULL CHECK (character_length(client_secret) = 32),
  is_trusted BOOLEAN DEFAULT false
);