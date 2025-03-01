CREATE TABLE IF NOT EXISTS trips (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  destination VARCHAR(255) NOT NULL,
  starts_at TIMESTAMP NOT NULL,
  ends_at TIMESTAMP NOT NULL,
  is_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
  owner_name VARCHAR(255) NOT NULL,
  owner_email VARCHAR(255) NOT NULL
);