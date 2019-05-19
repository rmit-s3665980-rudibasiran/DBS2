/*
Title: RMIT Database Systems Assignment 1 / 2
Developer(s): 
- Rudi Basiran <s3665980@student.rmit.edu.au> 
Date Created: 30 March 2019 
Description: Global Class
Notes: Global Variables
Change History:
 */

public class GlobalClass {

	public static String delimiter = ",";
	public static int pagesize = 4096;
	public static int pagegap = 256;
	public static int logWrite = 0;
	public static int logSearch = 1;
	public static boolean usePageClass = false;
	public static boolean debugMode = true;
	public static String SerializableFileName = "bt.dat";
	public static Boolean doSerializable = true;
	public static int maxBTreePages = 100;

}