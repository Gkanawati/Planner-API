CREATE TABLE IF NOT EXISTS activities (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    occurs_at TIMESTAMP NOT NULL,
    trip_id UUID NOT NULL,
    CONSTRAINT activities_trip_id_fkey
        FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
);