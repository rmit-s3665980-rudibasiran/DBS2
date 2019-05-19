import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

/*
Title: RMIT Database Systems Assignment 1 / 2
Developer(s): 
- Rudi Basiran <s3665980@student.rmit.edu.au> 
Date Created: 30 March 2019 
Description: dbload Class
Notes: --
Change History:
 */

public class dbload {

	private static BTree _bt = new BTree();

	public dbload() {
	}

	public static void main(String[] args) throws Exception {

		long startTime = System.nanoTime();

		// java dbload -p pagesize datafile

		// Note that input error detection is not implemented as the aim of the codes is
		// to simulate paging and it assumes that the user will input erroneous
		// parameters when executing the program.

		String option = ""; // -p option
		String t_pagesize = ""; // temp page size to capture integer input
		String datafile = ""; // file to read

		option = args[0]; // "-p"
		t_pagesize = args[1]; // "pagesize"
		datafile = args[2]; // "datafile"
		int pagesize = GlobalClass.pagesize; // set default page size

		pagesize = Integer.parseInt(t_pagesize); // set page size according to what user input

		String heapfile = "heap." + t_pagesize; // set heap file name

		Helper.drawLine();
		System.out.println("Parameters Captured:");
		System.out.println("Option: " + option);
		System.out.println("Pagesize: " + t_pagesize);
		System.out.println("Datafile: " + datafile);
		System.out.println("Heapfile: " + heapfile);
		Helper.drawLine();

		int numPage = 0; // count total number of pages
		int numRec = 0; // count total number of records read

		DataOutputStream dos = null;
		DataOutputStream pos = null;
		FileInputStream inputStream = null;
		Scanner sc = null;

		Helper.drawLine();

		dos = Helper.openOutputStream(heapfile);
		pos = Helper.openOutputStreamPage(heapfile + ".page");

		try {

			Record record = null;
			Page p = new Page(pagesize, numPage);
			int checkSizeofPage = pagesize - GlobalClass.pagegap;

			inputStream = new FileInputStream(datafile);
			sc = new Scanner(inputStream, "UTF-8");
			sc.nextLine();

			// read whole file
			while (sc.hasNextLine()) {
				String line = sc.nextLine();

				String[] attributes = line.split(GlobalClass.delimiter); // get all columns delimited into string array
				record = Helper.createRecord(attributes); // build columns into record class

				// insert key + record in BTree
				_bt.insert(record.getDAName(), numPage);

				// check whether page is full
				if (checkSizeofPage - record.getSizeOfRecord() > 0) {
					// decrement balance space if not full
					checkSizeofPage = checkSizeofPage - record.getSizeOfRecord();
				} else {
					if (GlobalClass.usePageClass)
						Helper.writePage(p, pos);
					// set new page if current page full
					numPage++;
					checkSizeofPage = pagesize - GlobalClass.pagegap;
					if (GlobalClass.usePageClass)
						p.clearPage(numPage);
				}

				// write to disk
				Helper.writeRecords(record, dos);
				if (GlobalClass.usePageClass)
					p.fillRecord(record);

				numRec++;
				System.out.println("[Reading CSV line: " + numRec + "][Page:" + numPage + "]");

			}
			// scanner suppresses exceptions
			if (sc.ioException() != null) {
				throw sc.ioException();
			}

		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (sc != null) {
				sc.close();
			}
		}

		if (GlobalClass.doSerializable) {
			fillSerializable();
		}

		pos.close();
		dos.close();

		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;

		Helper.logger(numRec, numPage, totalTime, GlobalClass.logWrite);

		Helper.drawLine();

	}

	public static void fillSerializable() {

		try {
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(GlobalClass.SerializableFileName));
			output.writeObject(_bt);
			output.close();
		} catch (FileNotFoundException e) {
			System.out.println(
					"FileNotFoundException | Serializable File " + GlobalClass.SerializableFileName + " not found.");
		} catch (IOException e) {
			System.out.println("IOException");
		}

	}

}
