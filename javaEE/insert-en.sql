-- psql -U postgres -W lo53 < insert-en.sql 

INSERT INTO POSITION(position_id, x, y) VALUES (1, 34.9055, 4.77); 
INSERT INTO POSITION(position_id, x, y) VALUES (2, 50.755, 33244.77); 
INSERT INTO POSITION(position_id, x, y) VALUES (3, 990.555, 23244.77); 
INSERT INTO POSITION(position_id, x, y) VALUES (4, 324.575, 44221.77); 
INSERT INTO POSITION(position_id, x, y) VALUES (5, 3490.535, 4444.89877); 
INSERT INTO POSITION(position_id, x, y) VALUES (6, 3445.6755, 4784.77); 

INSERT INTO ACCESSPOINT(accesspoint_id, rssi) VALUES (1, 9.3); 
INSERT INTO ACCESSPOINT(accesspoint_id, rssi) VALUES (2, 9167.63); 
INSERT INTO ACCESSPOINT(accesspoint_id, rssi) VALUES (3, 9867.3); 
INSERT INTO ACCESSPOINT(accesspoint_id, rssi) VALUES (4, 127.3987); 
INSERT INTO ACCESSPOINT(accesspoint_id, rssi) VALUES (5, 457.36); 
INSERT INTO ACCESSPOINT(accesspoint_id, rssi) VALUES (6, 17.21); 
INSERT INTO ACCESSPOINT(accesspoint_id, rssi) VALUES (7, 161773); 
INSERT INTO ACCESSPOINT(accesspoint_id, rssi) VALUES (8, 6337.3); 

INSERT INTO CELL(cell_id, position_id, accesspoint_id, mac_address) VALUES (1, 1, 1, '01:80:C2:00:00:00' ); 
INSERT INTO CELL(cell_id, position_id, accesspoint_id, mac_address) VALUES (2, 1, 2, '01:00:0C:CC:CC:CC' ); 
INSERT INTO CELL(cell_id, position_id, accesspoint_id, mac_address) VALUES (3, 1, 3, '00:0a:95:9d:68:16' ); 
INSERT INTO CELL(cell_id, position_id, accesspoint_id, mac_address) VALUES (4, 1, 4, '00-14-22-01-23-45' ); 
INSERT INTO CELL(cell_id, position_id, accesspoint_id, mac_address) VALUES (5, 1, 5, '00:11:43:00:00:01' ); 
INSERT INTO CELL(cell_id, position_id, accesspoint_id, mac_address) VALUES (6, 4, 1, '00:11:43:00:00:01' ); 
INSERT INTO CELL(cell_id, position_id, accesspoint_id, mac_address) VALUES (7, 6, 1, '01:80:C2:00:00:00' ); 
INSERT INTO CELL(cell_id, position_id, accesspoint_id, mac_address) VALUES (8, 3, 5, '01:77:C2:00:34:00' ); 
INSERT INTO CELL(cell_id, position_id, accesspoint_id, mac_address) VALUES (9, 3, 6, '01:N0:C2:Z0:90:00' ); 