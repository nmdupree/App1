--------------------------------------------------------------
-- Filename:  V1.7__indicator_types2.sql
--------------------------------------------------------------

-- Create the reports table
CREATE TABLE indicator_types
(
    id      INTEGER,
    name    VARCHAR(50)
);

INSERT INTO indicator_types(id, name)
    VALUES  (3, 'ip'),
            (5, 'domain');