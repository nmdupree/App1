--------------------------------------------------------------
-- Filename:  V1.2__tables.sql
--------------------------------------------------------------

-- Create the reports table
CREATE TABLE reports
(
  id                  INTEGER PRIMARY KEY NOT NULL,
  version             INTEGER,
  description         TEXT,
  display_name        VARCHAR(255),
  reviewed            BOOLEAN,
  reference_source    INTEGER,
  priority            INTEGER,
  created_date        TIMESTAMP,
  last_modified_date  TIMESTAMP,
  is_custom_report    BOOLEAN DEFAULT FALSE,
  reserved            BOOLEAN,
  reserved_by         VARCHAR(255)
);

-- Create the users table
CREATE TABLE users
(
  id                INTEGER        PRIMARY KEY NOT NULL,
  version           INTEGER        NOT NULL,
  user_id           VARCHAR (256)  NOT NULL,
  dn                VARCHAR(256)   NOT NULL,
  last_login_date   TIMESTAMP      NOT NULL,
  full_name         VARCHAR(150)       NULL,
  email_address     VARCHAR(256)       NULL,
  phone_number      VARCHAR(30)        NULL
);


-- Create the user_reports table
CREATE TABLE user_reports
(
  user_id    INTEGER NOT NULL,
  report_id  INTEGER NOT NULL
);
