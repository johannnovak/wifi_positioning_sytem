-- psql -U postgres -W lo53 < insert-en.sql 

INSERT INTO POSITION(position_id, x, y) VALUES (1, 34.9055, 4.77); 
INSERT INTO POSITION(position_id, x, y) VALUES (2, 50.755, 33244.77); 
INSERT INTO POSITION(position_id, x, y) VALUES (3, 990.555, 23244.77); 
INSERT INTO POSITION(position_id, x, y) VALUES (4, 324.575, 44221.77); 
INSERT INTO POSITION(position_id, x, y) VALUES (5, 3490.535, 4444.89877); 
INSERT INTO POSITION(position_id, x, y) VALUES (6, 3445.6755, 4784.77); 

INSERT INTO measurement(id, rssi, mac_address) VALUES (1, 9.3, '01:80:C2:00:00:00' ); 
INSERT INTO measurement(id, rssi, mac_address) VALUES (2, 9167.63, '01:00:0C:CC:CC:CC'); 
INSERT INTO measurement(id, rssi, mac_address) VALUES (3, 9867.3, '00:0a:95:9d:68:16' ); 
INSERT INTO measurement(id, rssi, mac_address) VALUES (4, 127.3987, '00-14-22-01-23-45' ); 
INSERT INTO measurement(id, rssi, mac_address) VALUES (5, 457.36, '00:11:43:00:00:01' ); 
INSERT INTO measurement(id, rssi, mac_address) VALUES (6, 17.21, '00:11:43:00:00:01' ); 
INSERT INTO measurement(id, rssi, mac_address) VALUES (7, 161773, '01:80:C2:00:00:00'); 
INSERT INTO measurement(id, rssi, mac_address) VALUES (8, 6337.3, '01:77:C2:00:34:00' ); 

INSERT INTO rssi_per_cell(id, position_id, accesspoint_id) VALUES (1, 1, 1); 
INSERT INTO rssi_per_cell(id, position_id, accesspoint_id) VALUES (2, 1, 2); 
INSERT INTO rssi_per_cell(id, position_id, accesspoint_id) VALUES (3, 1, 3); 
INSERT INTO rssi_per_cell(id, position_id, accesspoint_id) VALUES (4, 1, 4); 
INSERT INTO rssi_per_cell(id, position_id, accesspoint_id) VALUES (5, 1, 5); 
INSERT INTO rssi_per_cell(id, position_id, accesspoint_id) VALUES (6, 4, 1); 
INSERT INTO rssi_per_cell(id, position_id, accesspoint_id) VALUES (7, 6, 1); 
INSERT INTO rssi_per_cell(id, position_id, accesspoint_id) VALUES (8, 3, 5); 
INSERT INTO rssi_per_cell(id, position_id, accesspoint_id) VALUES (9, 3, 6); 

--select * from measurements inner join rssi_per_cell on measurements.id=rssi_per_cell.id inner join position on position.position_id=rssi_per_cell.id;