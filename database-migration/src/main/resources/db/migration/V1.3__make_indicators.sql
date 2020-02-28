--------------------------------------------------------------
-- Filename:  V1.3.__make_indicators.sql
--------------------------------------------------------------

CREATE TABLE indicators
    (
    id      INTEGER PRIMARY KEY NOT NULL,
    type    INTEGER,
    value   VARCHAR(255)
    );

