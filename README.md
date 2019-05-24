# DBS2
Database Systems Assignment 2

Loading Syntax:
java dbload -p pagesize datafile

eg:
java dbload -p 4096 input.csv

Querying Syntax (query heapfile):
java dbquery text pagesize -h
eg:
java dbquery 17854 4096 -h
java dbquery bourke 4096 -h

Querying Syntax (query bTree:
java dbquery text pagesize -b
eg:
java dbquery 17854 4096 -b
java dbquery bourke 4096 -b

Note: bPlusTree creating properly but not saved to disk yet
