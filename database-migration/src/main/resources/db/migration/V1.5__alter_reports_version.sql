--------------------------------------------------------------
-- Filename:  V1.5__alter_reports_version.sql
--------------------------------------------------------------

UPDATE reports
SET version = 1
WHERE version IS NULL;

ALTER TABLE reports
ALTER COLUMN  version SET DEFAULT 1;

ALTER TABLE reports
ALTER COLUMN  version  SET NOT NULL;

