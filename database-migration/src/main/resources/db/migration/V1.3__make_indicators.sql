--------------------------------------------------------------
-- Filename:  V1.5__indicators_table.sql
--------------------------------------------------------------

CREATE TABLE indicators
    (
    id      INTEGER PRIMARY KEY NOT NULL,
    type    INTEGER,
    value   VARCHAR(255)
    );

INSERT INTO indicators(id, type, value)
    VALUES  (1, 4, '10.2.2.3'),
        (2, 3, '192.168.3.1'),
        (3, 4, '10.1.1.2'),
        (4, 1, '172.16.150.24'),
        (5, 2, '192.168.1.8'),
        (6, 1, '8.8.8.8');