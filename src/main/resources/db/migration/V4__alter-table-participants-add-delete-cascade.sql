ALTER TABLE participants
DROP CONSTRAINT IF EXISTS participants_trip_id_fkey;

ALTER TABLE participants
ADD CONSTRAINT participants_trip_id_fkey
FOREIGN KEY (trip_id) 
REFERENCES trips(id)
ON DELETE CASCADE;