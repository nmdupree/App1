--------------------------------------------------------------
-- Filename:  V1.6__indicator_types.sql
--------------------------------------------------------------

-- Create the reports table
CREATE TABLE types
(
    id      INTEGER,
    type    VARCHAR(50)
);

INSERT INTO types(id, type)
    VALUES  (3, 'ip'),
            (5, 'domain');