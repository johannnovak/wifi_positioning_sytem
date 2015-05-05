-- psql -U postgres -W lo53 < create-accessPoint.sql 

CREATE TABLE accessPoint (
	accessPoint_id SERIAL PRIMARY KEY, 
	rssi numeric(8,2) NOT NULL DEFAULT 0
);