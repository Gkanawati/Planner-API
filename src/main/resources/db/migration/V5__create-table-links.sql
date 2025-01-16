CREATE TABLE IF NOT EXISTS links (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    trip_id UUID NOT NULL,
    CONSTRAINT links_trip_id_fkey
        FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
);