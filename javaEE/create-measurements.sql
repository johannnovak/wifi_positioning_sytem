-- psql -U postgres -W lo53 < create-measurements.sql 

CREATE TABLE measurement (
	id SERIAL PRIMARY KEY, 
	rssi numeric(8,2) NOT NULL DEFAULT 0,
	mac_address VARCHAR(18) NOT NULL
);