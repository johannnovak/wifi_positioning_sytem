-- psql -U postgres -W lo53 < create-rssi_per_cell.sql 

CREATE TABLE rssi_per_cell (
	id	SERIAL PRIMARY KEY, 
	position_id INTEGER, 
	accessPoint_id INTEGER,
	CONSTRAINT fk_position_id FOREIGN KEY (position_id) REFERENCES position(position_id) ON UPDATE cascade,
	CONSTRAINT fk_access FOREIGN KEY (accessPoint_id) REFERENCES measurement(id) ON UPDATE cascade
);