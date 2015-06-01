CREATE TABLE measurement (
	measurement_id SERIAL PRIMARY KEY, 
	rssi REAL NOT NULL DEFAULT 0,
	mac_address VARCHAR(18) NOT NULL,
	position_id INTEGER NOT NULL,
	CONSTRAINT fk_position FOREIGN KEY (position_id) REFERENCES position(position_id) ON UPDATE cascade
);
