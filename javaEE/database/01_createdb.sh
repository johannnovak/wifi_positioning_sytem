#!/bin/bash

sql=$sql$(cat create-position.sql)
sql=$sql$(cat create-measurement.sql)
sql=$sql$(cat insert_positions.sql)
sql=$sql$(cat insert_measurements.sql)

echo $sql > temp.sql
psql -U postgres -W lo53 < temp.sql
rm temp.sql
