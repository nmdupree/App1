--------------------------------------------------------------
-- Filename:  V1.8__modify_lookup_table.sql
--------------------------------------------------------------

-- Modify the lookup table to include a display_order column
ALTER TABLE lookup
ADD COLUMN display_order integer NULL;

comment on column lookup.display_order is 'A possible order to display the lookups on the front-end';

-- Modify the data in the lookup table to add values to the display order column
UPDATE lookup SET display_order = 1 WHERE name ILIKE 'low';
UPDATE lookup SET display_order = 2 WHERE name ILIKE 'medium';
UPDATE lookup SET display_order = 3 WHERE name ILIKE 'high';
UPDATE lookup SET display_order = 4 WHERE name ILIKE 'critical';

-- Insert indicator type and classification lookup types
insert into lookup_type(id, version, name) values(102, 1, 'indicator_type');
insert into lookup_type(id, version, name) values(103, 1, 'classification');

-- Insert indicator type lookup values
insert into lookup(id, version, lookup_type, name, display_order)  values(8, 1, 102, 'DOMAIN', 1);
insert into lookup(id, version, lookup_type, name, display_order)  values(9, 1, 102, 'IP', 2);
insert into lookup(id, version, lookup_type, name, display_order)  values(10, 1, 102, 'EMAIL', 3);

-- Insert classification lookup values
insert into lookup(id, version, lookup_type, name, display_order)  values(11, 1, 103, 'UNCLASSIFIED', 1);
insert into lookup(id, version, lookup_type, name, display_order)  values(12, 1, 103, 'UNCLASSIFIED/FOUO', 2);
insert into lookup(id, version, lookup_type, name, display_order)  values(13, 1, 103, 'SECRET', 3);

-- Modify Indicators table to include classification column
ALTER TABLE indicators
ADD COLUMN classification INTEGER NOT NULL;