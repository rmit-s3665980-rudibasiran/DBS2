import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Database Systems - HEAP IMPLEMENTATION
 */

public class dbload implements dbimpl {
	private bPlusTree _bt = new bPlusTree();
	// initialize

	public static void main(String args[]) {
		dbload load = new dbload();

		// calculate load time
		long startTime = System.currentTimeMillis();
		load.readArguments(args);
		long endTime = System.currentTimeMillis();

		System.out.println("Load time: " + (endTime - startTime) + "ms");
	
			
	}

	// reading command line arguments
	public void readArguments(String args[]) {
		if (args.length == 3) {
			if (args[0].equals("-p") && isInteger(args[1])) {
				readFile(args[2], Integer.parseInt(args[1]));
			}
		} else {
			System.out.println("Error: only pass in three arguments");
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

	// read .csv file using buffered reader
	@SuppressWarnings("unchecked")
	public void readFile(String filename, int pagesize) {
		dbload load = new dbload();
		File heapfile = new File(HEAP_FNAME + pagesize);
		BufferedReader br = null;
		FileOutputStream fos = null;
		String line = "";
		String nextLine = "";
		String stringDelimeter = ",";
		byte[] RECORD = new byte[RECORD_SIZE];
		int outCount, pageCount, recCount;
		outCount = pageCount = recCount = 0;

		try {
			// create stream to write bytes to according page size
			fos = new FileOutputStream(heapfile);
			br = new BufferedReader(new FileReader(filename));
			// read line by line
			while ((line = br.readLine()) != null) {
				String[] entry = line.split(stringDelimeter, -1);
				RECORD = createRecord(RECORD, entry, outCount);

				String recordStr = new String(RECORD);
				// String DEVICE_ID = recordStr.substring(RID_SIZE, RID_SIZE + DEVICE_ID_SIZE);
				String DA_NAME = recordStr.substring(RID_SIZE, RID_SIZE + DEVICE_ID_SIZE + ARRIVAL_TIME_SIZE);
				String ST_NAME = recordStr.substring(STREET_NAME_OFFSET, STREET_NAME_OFFSET + STREET_NAME_SIZE);

				_bt.insert(DA_NAME, ST_NAME);
				// _bt.insert(DA_NAME, Integer.toString(pageCount));

				// outCount is to count record and reset everytime
				// the number of bytes has exceed the pagesize
				outCount++;
				fos.write(RECORD);
				if ((outCount + 1) * RECORD_SIZE > pagesize) {
					eofByteAddOn(fos, pagesize, outCount, pageCount);
					// reset counter to start newpage
					outCount = 0;
					pageCount++;
				}
				recCount++;
			}
		} catch (FileNotFoundException e) {
			System.out.println("File: " + filename + " not found.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					// final add on at end of file
					if ((nextLine = br.readLine()) == null) {
						eofByteAddOn(fos, pagesize, outCount, pageCount);
						pageCount++;
					}
					fos.close();
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Page total: " + pageCount);
		System.out.println("Record total: " + recCount);

		long queryStartTime = System.currentTimeMillis();
		System.out.println("Searching for [" + DEBUG_MODE_SEARCH_STR + "]: ");
		System.out.println(_bt.search(DEBUG_MODE_SEARCH_STR));
		long queryEndTime = System.currentTimeMillis();
		System.out.println("Search time: " + (queryEndTime - queryStartTime) + "ms");

		if (saveTreeToDisk)
			doSaveTreeToDisk();

	}

	// create byte array for a field and append to record array at correct
	// offset using array copy
	public void copy(String entry, int SIZE, int DATA_OFFSET, byte[] rec) throws UnsupportedEncodingException {
		byte[] DATA = new byte[SIZE];
		byte[] DATA_SRC = entry.trim().getBytes(ENCODING);
		if (entry != "") {
			System.arraycopy(DATA_SRC, 0, DATA, 0, DATA_SRC.length);
		}
		System.arraycopy(DATA, 0, rec, DATA_OFFSET, DATA.length);
	}

	// creates record by appending using array copy and then applying offset
	// where neccessary
	public byte[] createRecord(byte[] rec, String[] entry, int out) throws UnsupportedEncodingException {
		byte[] RID = intToByteArray(out);
		System.arraycopy(RID, 0, rec, 0, RID.length);

		copy(entry[0], DEVICE_ID_SIZE, RID_SIZE, rec);
		copy(entry[1], ARRIVAL_TIME_SIZE, ARRIVAL_TIME_OFFSET, rec);
		copy(entry[2], DEPART_TIME_SIZE, DEPART_TIME_OFFSET, rec);
		copy(entry[3], DURATION_SIZE, DURATION_OFFSET, rec);
		copy(entry[4], STREET_MARKER_SIZE, STREET_MARKER_OFFSET, rec);
		copy(entry[5], SIGN_SIZE, SIGN_OFFSET, rec);
		copy(entry[6], AREA_SIZE, AREA_OFFSET, rec);
		copy(entry[7], STREET_ID_SIZE, STREET_ID_OFFSET, rec);
		copy(entry[8], STREET_NAME_SIZE, STREET_NAME_OFFSET, rec);
		copy(entry[9], BETWEEN_STREET_1_SIZE, BETWEEN_STREET_1_OFFSET, rec);
		copy(entry[10], BETWEEN_STREET_2_SIZE, BETWEEN_STREET_2_OFFSET, rec);
		copy(entry[11], SIDE_OF_STREET_SIZE, SIDE_OF_STREET_OFFSET, rec);
		copy(entry[12], VIOLATION_SIZE, VIOLATION_OFFSET, rec);

		
		return rec;
	}

	// EOF padding to fill up remaining pagesize
	// * minus 4 bytes to add page number at end of file
	public void eofByteAddOn(FileOutputStream fos, int pSize, int out, int pCount) throws IOException {
		byte[] fPadding = new byte[pSize - (RECORD_SIZE * out) - 4];
		byte[] bPageNum = intToByteArray(pCount);
		fos.write(fPadding);
		fos.write(bPageNum);
	}

	// converts ints to a byte array of allocated size using bytebuffer
	public byte[] intToByteArray(int i) {
		ByteBuffer bBuffer = ByteBuffer.allocate(4);
		bBuffer.putInt(i);
		return bBuffer.array();
	}

	public void doSaveTreeToDisk() {

	}
}
