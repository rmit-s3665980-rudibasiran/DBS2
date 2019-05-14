import java.util.Arrays;

/*
Title: RMIT Database Systems Assignment 1
Developer(s): 
- Rudi Basiran <s3665980@student.rmit.edu.au> 
Date Created: 31 March 2019 
Description: Page Class
Notes: --
Change History:
 */

class Page {

	private byte[] b;

	int currentByte;
	int pagesize;
	int gap = GlobalClass.pagegap;

	public Page() {

	}

	public Page(int p, int pageNumber) {
		b = new byte[p];
		b[0] = (byte) pageNumber;
		currentByte = 1;
		pagesize = p;
	}

	public void fillRecord(Record r) {
		int i = 0;
		for (char c : r.getRecord().toCharArray()) {
			b[currentByte] = (byte) r.getRecord().charAt(i);
			i++;
			currentByte++;
		}
	}

	public boolean pageFull(int sizeOfNewRecord) {
		return ((currentByte + sizeOfNewRecord + gap > this.pagesize) ? true : false);
	}

	public byte[] getPage() {
		return this.b;
	}

	public void clearPage(int pageNumber) {

		Arrays.fill(b, (byte) 0);

		b[0] = (byte) pageNumber;
		currentByte = 1;

	}
}