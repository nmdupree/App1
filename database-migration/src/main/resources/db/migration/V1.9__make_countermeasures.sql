--------------------------------------------------------------
-- Filename:  V1.9__make_countermeasures.sql
--------------------------------------------------------------

-- Create the countermeasures table

CREATE TABLE countermeasures
(
    id                  INTEGER PRIMARY KEY NOT NULL,
    version             INTEGER,
    value               VARCHAR(255),
    status              INTEGER,
    start_date          TIMESTAMP,
    end_date            TIMESTAMP,

CONSTRAINT status_fkey FOREIGN KEY (status) REFERENCES lookup(id)
);