--------------------------------------------------------------
-- Filename:  R__views.sql
--------------------------------------------------------------

--Create a view for reports table with grid information
DROP VIEW IF EXISTS view_all_reports;

CREATE VIEW view_all_reports
    AS
        SELECT r.id, r.display_name, r.description, l.name AS priority, r.created_date
        FROM reports r
        JOIN lookup l ON r.priority = l.id;
    ;