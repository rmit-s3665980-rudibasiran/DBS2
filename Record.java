/*
Title: RMIT Database Systems Assignment 1
Developer(s): 
- Rudi Basiran <s3665980@student.rmit.edu.au> 
Date Created: 30 March 2019 
Description: Record Class
Notes: --
Change History:
 */

class Record {

	private String da_name;
	private String device_id;
	private String arrival_time;
	private String departure_time;
	private double duration_seconds;
	private String street_marker;
	private String sign;
	private String area;
	private String street_id;
	private String street_name;
	private String between_street1;
	private String between_street2;
	private int side_of_street;
	private boolean in_violation;

	public Record() {

	}

	public Record(String device_id, String arrival_time, String departure_time, double duration_seconds,
			String street_marker, String sign, String area, String street_id, String street_name,
			String between_street1, String between_street2, int side_of_street, String in_violation) {

		this.da_name = device_id + arrival_time;
		this.device_id = device_id;
		this.arrival_time = arrival_time;
		this.departure_time = departure_time;
		this.duration_seconds = duration_seconds;
		this.street_marker = street_marker;
		this.sign = sign;
		this.area = area;
		this.street_id = street_id;
		this.street_name = street_name;
		this.between_street1 = between_street1;
		this.between_street2 = between_street2;
		this.side_of_street = side_of_street;
		this.in_violation = (in_violation == "TRUE" ? true : false);
	}

	public String getDAName() {
		return this.da_name;
	}

	public String getDeviceID() {
		return this.device_id;
	}

	public String getArrivalTime() {
		return this.arrival_time;
	}

	public String getDepartureTime() {
		return this.departure_time;
	}

	public double getDuration() {
		return this.duration_seconds;
	}

	public String getStreetMarker() {
		return this.street_marker;
	}

	public String getSign() {
		return this.sign;
	}

	public String getArea() {
		return this.area;
	}

	public String getStreetID() {
		return this.street_id;
	}

	public String getStreetName() {
		return this.street_name;
	}

	public String getBetweenStreet1() {
		return this.between_street1;
	}

	public String getBetweenStreet2() {
		return this.between_street2;
	}

	public int getSideOfStreet() {
		return this.side_of_street;
	}

	public boolean getInViolation() {
		return this.in_violation;
	}

	public String getInViolationStr() {
		return (this.in_violation ? "TRUE" : "FALSE");
	}

	public int getSizeOfRecord() {
		return getRecord().getBytes().length;
	}

	public String getRecord() {
		return da_name + GlobalClass.delimiter + device_id + GlobalClass.delimiter + arrival_time
				+ GlobalClass.delimiter + departure_time + GlobalClass.delimiter + Double.toString(duration_seconds)
				+ GlobalClass.delimiter + street_marker + GlobalClass.delimiter + sign + GlobalClass.delimiter + area
				+ GlobalClass.delimiter + street_id + GlobalClass.delimiter + street_name + GlobalClass.delimiter
				+ between_street1 + GlobalClass.delimiter + between_street2 + GlobalClass.delimiter
				+ Integer.toString(side_of_street) + GlobalClass.delimiter + (in_violation ? "TRUE" : "FALSE");
	}

}
