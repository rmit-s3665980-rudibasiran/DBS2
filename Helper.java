import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/*
Title: RMIT Database Systems Assignment 1
Developer(s): 
- Rudi Basiran <s3665980@student.rmit.edu.au> 
Date Created: 30 March 2019 
Description: Helper Class
Notes: --
Change History:
 */

public class Helper {

	public static void drawLine() {
		for (int x = 0; x < 50; x++)
			System.out.print("-");
		System.out.println("");
	}

	public static Record createRecord(String[] metadata) {

		String device_id = metadata[0];
		String arrival_time = metadata[1];
		String departure_time = metadata[2];
		double duration_seconds = Double.parseDouble(metadata[3]);
		String street_marker = metadata[4];
		String sign = metadata[5];
		String area = metadata[6];
		String street_id = metadata[7];
		String street_name = metadata[8];
		String between_street1 = metadata[9];
		String between_street2 = metadata[10];
		int side_of_street = Integer.parseInt(metadata[11]);
		String in_violation = metadata[12];

		// create and return record of meta-data
		Record r = new Record(device_id, arrival_time, departure_time, duration_seconds, street_marker, sign, area,
				street_id, street_name, between_street1, between_street2, side_of_street, in_violation);
		return r;
	}

	// data input stream
	public static DataInputStream openInputStream(String filename) throws Exception {
		DataInputStream in = null;
		File file = new File(filename);
		in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		return in;
	}

	// data output stream
	public static DataOutputStream openOutputStream(String filename) throws Exception {
		DataOutputStream out = null;
		File file = new File(filename);
		out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		return out;
	}

	// data output stream for page
	public static DataOutputStream openOutputStreamPage(String filename) throws Exception {
		DataOutputStream out = null;
		File file = new File(filename);
		out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		return out;
	}

	// write page
	public static void writePage(Page p, DataOutputStream out) throws Exception {
		out.write(p.getPage());
	}

	public static void writeRecords(Record r, DataOutputStream out) throws Exception {

		// write as UTF, Int or Double
		out.writeUTF(r.getDAName());
		out.writeUTF(r.getDeviceID());
		out.writeUTF(r.getArrivalTime());
		out.writeUTF(r.getDepartureTime());
		out.writeDouble(r.getDuration());
		out.writeUTF(r.getStreetMarker());
		out.writeUTF(r.getSign());
		out.writeUTF(r.getArea());
		out.writeUTF(r.getStreetID());
		out.writeUTF(r.getStreetName());
		out.writeUTF(r.getBetweenStreet1());
		out.writeUTF(r.getBetweenStreet2());
		out.writeInt(r.getSideOfStreet());
		out.writeUTF(r.getInViolationStr());
	}

	// log when test search found
	public static void loggerMatch(String s) {
		FileOutputStream fos = null;
		File file;
		try {
			file = new File("stdout");
			fos = new FileOutputStream(file, true);

			if (!file.exists()) {
				file.createNewFile();
			}
			fos.write(s.getBytes());
		}

		catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException ioe) {
				System.out.println("Error");
			}
		}

	}

	// log results
	public static void logger(int numRec, int numPages, long totalTime, int logtype) {
		/*
		 * Your dbload program must also output the following to stdout, the number of
		 * records loaded, number of pages used and the number of milliseconds to create
		 * the heap file.
		 */
		FileOutputStream fos = null;
		File file;
		try {
			file = new File("stdout");
			fos = new FileOutputStream(file, true);

			if (!file.exists()) {
				file.createNewFile();
			}
			String s = (logtype == GlobalClass.logSearch ? "Search Statistics:" : "Loading Statistics:")
					+ "\nNo. of Pages: " + numPages + "\n" + "No. of records: " + numRec + "\n"
					+ "Time Elapsed in Milliseconds: " + Long.toString(totalTime / 1000000) + "\n";
			fos.write(s.getBytes());
		}

		catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException ioe) {
				System.out.println("Error");
			}
		}
	}

}
