#!/bin/bash

if [ "$1" != '' ]
then
	echo "reformatting dates"
	# sed -E 's,([0-9]{2})/([0-9]{2})/([0-9]{4}),\1.\2.\3,g' "$1" > "$1".fixeddates
    python fixdates.py "$1" > "$1".fixeddates
	echo "completed (in $1.fixeddates)"
else
	echo "Usage: . fixDate.sh input_csv_file"
fi