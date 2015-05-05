-- psql -U postgres -W lo53 < create-cell.sql 

CREATE TABLE cell (
	cell_id	SERIAL PRIMARY KEY, 
	position_id INTEGER, 
	accessPoint_id INTEGER,
	mac_address VARCHAR(17) NOT NULL,
	CONSTRAINT fk_position_id FOREIGN KEY (position_id) REFERENCES position(position_id) ON UPDATE cascade,
	CONSTRAINT fk_access FOREIGN KEY (accessPoint_id) REFERENCES accessPoint(accessPoint_id) ON UPDATE cascade
);