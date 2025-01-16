CREATE TABLE IF NOT EXISTS participants (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  is_confirmed BOOLEAN NOT NULL,
  trip_id UUID,
  CONSTRAINT participants_trip_id_fkey
      FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE SET NULL
);