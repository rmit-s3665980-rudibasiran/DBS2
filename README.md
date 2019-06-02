# DBS2
Database Systems Assignment 2

Loading Syntax:
java -Xmx1g dbload -p pagesize datafile
eg:
java -Xmx1g dbload -p 4096 input.csv

Querying Syntax (query heapfile):
java dbquery text pagesize -h
eg:
java -Xmx1g dbquery 17854 4096 -h
java -Xmx1g dbquery bourke 4096 -h

Querying Syntax (query bTree):
java -Xmx1g dbquery text pagesize -b
eg:
java -Xmx1g dbquery 17854 4096 -b
java -Xmx1g dbquery bourke 4096 -b

Querying Syntax (query bTree device ID range):
java -Xmx1g dbquery deviceid---range pagesize -bdevice
eg:
java -Xmx1g dbquery 17854---18377 4096 -bdevice

Querying Syntax (query bTree date range):
java -Xmx1g dbquery date---range pagesize -bdate
eg:
java -Xmx1g dbquery 2017-12-30---2017-12-31 4096 -bdate

