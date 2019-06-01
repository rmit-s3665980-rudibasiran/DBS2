import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Database Systems - HEAP IMPLEMENTATION
 */

public class dbquery implements dbimpl {
	private bplustree _bt = new bplustree();
	// initialize
	public static void main(String args[]) {
		dbquery load = new dbquery();

		// calculate query time
		long startTime = System.currentTimeMillis();

		// java dbquery searchtext heapsize flag [-b, -h]
		load.readArguments(args);
		long endTime = System.currentTimeMillis();

		if (args[2].equals("-h"))
			System.out.println("Heapfile Search Time: " + (endTime - startTime) + "ms");
		dbimpl.drawLine();
	}

	// reading command line arguments
	public void readArguments(String args[]) {
		if (args.length == 3) {
			if (args[2].equals("-h")) {
				if (isInteger(args[1])) {
					System.out.println("Search Heap File for [" + args[0] + "]");
					readHeap(args[0], Integer.parseInt(args[1]));
				}
			}
			else if (args[2].contains("-b")) {

				// read b+ tree records from disk and re-buidling tree
				// do not count tree re-building time
				readBTree();

				long queryStartTime = System.currentTimeMillis();
				System.out.println("Searching B+ Tree for [" + args[0] + "]: ");

				Boolean doRangeSearch = false;
				String k1 = "";
				String k2 = "";
				String key = args[0];
				if (key.contains(dbimpl.RANGE_DELIMITER)) {
					String[] searchValues = key.split(dbimpl.RANGE_DELIMITER);
					k1 = searchValues[0];
					k2 = searchValues[1];
					doRangeSearch = true;
				}
				else {
					k1 = key;
				}

				if (doRangeSearch) {
					if (args[2].equals("-bdevice")) {
						_bt.rangeSearch(k1, k2, RANGE_KEY_DEVICE);
					}
					else if (args[2].equals("-bdate")) {
						_bt.rangeSearch(k1, k2, RANGE_KEY_DATE);
					}
				}
				else {
					_bt.search(key);
				}
			
				long queryEndTime = System.currentTimeMillis();
				System.out.println("B+ Tree Search Time: " + (queryEndTime - queryStartTime) + "ms");
			}

		} else {
			System.out.println("Error: Pass in 3 arguments: dbquery searchtext heapfile flag[-b/-h]");
		}
	}

	public void readBTree () {
		File btreeFilename = new File(BPLUS_TREE_FILE_NAME);
		int count = 0;
		try (FileInputStream fis = new FileInputStream(btreeFilename)) {
			boolean haveNextRecord = true;
		
			while (haveNextRecord) {
				byte[] buf = new byte[TREE_RECORD_SIZE];
				int i = fis.read(buf, 0, TREE_RECORD_SIZE);
				String rec = new String(buf);
				if (i != -1) {
					count++;
					// System.out.println(count + ": " + rec);
					String key = rec.split("[,]")[0];
					String val = rec.split("[,]")[1];

					// rebuild tree
					_bt.insert(key, val);
				}
				else 
					haveNextRecord = false;
			} 
			fis.close();
		}
		 catch (FileNotFoundException e) {
			System.out.println("File " + btreeFilename + " not found.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// check if pagesize is a valid integer
	public boolean isInteger(String s) {
		boolean isValidInt = false;
		try {
			Integer.parseInt(s);
			isValidInt = true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return isValidInt;
	}

	// read heapfile by page
	public void readHeap(String name, int pagesize) {
		File heapfile = new File(HEAP_FNAME + pagesize);
		int intSize = 4;
		int pageCount = 0;
		int recCount = 0;
		int recordLen = 0;
		int rid = 0;
		boolean isNextPage = true;
		boolean isNextRecord = true;
		
		try {
			FileInputStream fis = new FileInputStream(heapfile);
			// reading page by page
			while (isNextPage) {
				byte[] bPage = new byte[pagesize];
				byte[] bPageNum = new byte[intSize];
				fis.read(bPage, 0, pagesize);
				System.arraycopy(bPage, bPage.length - intSize, bPageNum, 0, intSize);

				// reading by record, return true to read the next record
				isNextRecord = true;
				while (isNextRecord) {
					byte[] bRecord = new byte[RECORD_SIZE];
					byte[] bRid = new byte[intSize];
					try {
						System.arraycopy(bPage, recordLen, bRecord, 0, RECORD_SIZE);
						System.arraycopy(bRecord, 0, bRid, 0, intSize);
						rid = ByteBuffer.wrap(bRid).getInt();
						if (rid != recCount) {
							isNextRecord = false;
						} else {
							printRecord(bRecord, name);
							recordLen += RECORD_SIZE;
						}
						recCount++;
						// if recordLen exceeds pagesize, catch this to reset to next page
					} catch (ArrayIndexOutOfBoundsException e) {
						isNextRecord = false;
						recordLen = 0;
						recCount = 0;
						rid = 0;
					}
				}
				// check to complete all pages
				if (ByteBuffer.wrap(bPageNum).getInt() != pageCount) {
					isNextPage = false;
				}
				pageCount++;
			}
			fis.close();
		} catch (FileNotFoundException e) {
			System.out.println("File: " + HEAP_FNAME + pagesize + " not found.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// returns records containing the argument text from shell
	public void printRecord(byte[] rec, String input) {
      String record = new String(rec);
      
      // substring (start index, end index)
		String ST_NAME = record.substring(STREET_NAME_OFFSET, STREET_NAME_OFFSET + STREET_NAME_SIZE);
		String DEVICE_ID = record.substring(RID_SIZE, RID_SIZE + DEVICE_ID_SIZE);
		String ARRIVAL_TIME = record.substring(ARRIVAL_TIME_OFFSET, ARRIVAL_TIME_OFFSET + ARRIVAL_TIME_SIZE);

		if (ST_NAME.toLowerCase().contains(input.toLowerCase())
			|| ARRIVAL_TIME.toLowerCase().contains(input.toLowerCase())
			|| DEVICE_ID.toLowerCase().contains(input.toLowerCase())) {
			System.out.println("Found in Heap File [" + input + "]: " + DEVICE_ID + "/" + ARRIVAL_TIME + " ==> " + ST_NAME);
		}
	}
}
