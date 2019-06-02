import sys
from datetime import datetime

i = 0
with open(sys.argv[1]) as f:
  first = True
  for line in f:
    if first:
      first = False
      print(line.strip())
      continue
    # assume no quotes in csv file
    p = line.strip().split(',')
    p[1] = datetime.strptime(p[1], "%m/%d/%Y %I:%M:%S %p").isoformat(' ')
    p[2] = datetime.strptime(p[2], "%m/%d/%Y %I:%M:%S %p").isoformat(' ')
    print(','.join(p))
    i += 1
    if i >= 1000000:
      exit()
