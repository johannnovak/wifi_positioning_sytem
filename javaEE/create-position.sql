-- psql -U postgres -W lo53 < create-position.sql 

CREATE TABLE position (
	position_id	SERIAL PRIMARY KEY,
	x numeric(8,2) NOT NULL DEFAULT 0,
	y numeric(8,2) NOT NULL DEFAULT 0
);