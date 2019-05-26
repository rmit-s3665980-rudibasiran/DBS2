/**
 *  Database Systems - HEAP IMPLEMENTATION
 */

public interface dbimpl {

   public static final String HEAP_FNAME = "heap.";
   public static final String ENCODING = "utf-8";

   // fixed/variable lengths
   public static final int RECORD_SIZE = 336;
   public static final int EOF_PAGENUM_SIZE = 4;
   public static final int RID_SIZE = 4;
   
   public static final int DEVICE_ID_SIZE = 6;
   public static final int ARRIVAL_TIME_SIZE = 22;
   public static final int DEPART_TIME_SIZE = 22;
   public static final int DURATION_SIZE = 20;
   public static final int STREET_MARKER_SIZE = 12;
   public static final int SIGN_SIZE = 50;
   public static final int AREA_SIZE = 20;
   public static final int STREET_ID_SIZE = 10;
   public static final int STREET_NAME_SIZE = 50;
   public static final int BETWEEN_STREET_1_SIZE = 50;
   public static final int BETWEEN_STREET_2_SIZE = 50;
   public static final int SIDE_OF_STREET_SIZE = 1;
   public static final int VIOLATION_SIZE = 5;

   public static final int ARRIVAL_TIME_OFFSET 
                                          = RID_SIZE
                                          + DEVICE_ID_SIZE;

   public static final int DEPART_TIME_OFFSET 
                                          = RID_SIZE 
                                          + DEVICE_ID_SIZE
                                          + ARRIVAL_TIME_SIZE;

   public static final int DURATION_OFFSET 
                                          = RID_SIZE 
                                          + DEVICE_ID_SIZE
                                          + ARRIVAL_TIME_SIZE
                                          + DEPART_TIME_SIZE;

   public static final int STREET_MARKER_OFFSET 
                                          = RID_SIZE 
                                          + DEVICE_ID_SIZE
                                          + ARRIVAL_TIME_SIZE
                                          + DEPART_TIME_SIZE
                                          + DURATION_SIZE;

   public static final int SIGN_OFFSET   = RID_SIZE 
                                         + DEVICE_ID_SIZE
                                         + ARRIVAL_TIME_SIZE
                                         + DEPART_TIME_SIZE
                                         + DURATION_SIZE
                                         + STREET_MARKER_SIZE;

   public static final int AREA_OFFSET   = RID_SIZE 
                                         + DEVICE_ID_SIZE
                                         + ARRIVAL_TIME_SIZE
                                         + DEPART_TIME_SIZE
                                         + DURATION_SIZE
                                         + STREET_MARKER_SIZE
                                         + SIGN_SIZE;

   public static final int STREET_ID_OFFSET 
                                         = RID_SIZE
                                         + DEVICE_ID_SIZE
                                         + ARRIVAL_TIME_SIZE
                                         + DEPART_TIME_SIZE
                                         + DURATION_SIZE
                                         + STREET_MARKER_SIZE
                                         + SIGN_SIZE
                                         + AREA_SIZE;

   public static final int STREET_NAME_OFFSET 
                                         = RID_SIZE
                                         + DEVICE_ID_SIZE
                                         + ARRIVAL_TIME_SIZE
                                         + DEPART_TIME_SIZE
                                         + DURATION_SIZE
                                         + STREET_MARKER_SIZE
                                         + SIGN_SIZE
                                         + AREA_SIZE
                                         + STREET_ID_SIZE;

   public static final int BETWEEN_STREET_1_OFFSET = RID_SIZE
                                         + DEVICE_ID_SIZE
                                         + ARRIVAL_TIME_SIZE
                                         + DEPART_TIME_SIZE
                                         + DURATION_SIZE
                                         + STREET_MARKER_SIZE
                                         + SIGN_SIZE
                                         + AREA_SIZE
                                         + STREET_ID_SIZE
                                         + STREET_NAME_SIZE;

   public static final int BETWEEN_STREET_2_OFFSET = RID_SIZE
                                         + DEVICE_ID_SIZE
                                         + ARRIVAL_TIME_SIZE
                                         + DEPART_TIME_SIZE
                                         + DURATION_SIZE
                                         + STREET_MARKER_SIZE
                                         + SIGN_SIZE
                                         + AREA_SIZE
                                         + STREET_ID_SIZE
                                         + STREET_NAME_SIZE
                                         + BETWEEN_STREET_1_SIZE;

   public static final int SIDE_OF_STREET_OFFSET = RID_SIZE
                                         + DEVICE_ID_SIZE
                                         + ARRIVAL_TIME_SIZE
                                         + DEPART_TIME_SIZE
                                         + DURATION_SIZE
                                         + STREET_MARKER_SIZE
                                         + SIGN_SIZE
                                         + AREA_SIZE
                                         + STREET_ID_SIZE
                                         + STREET_NAME_SIZE
                                         + BETWEEN_STREET_1_SIZE
                                         + BETWEEN_STREET_2_SIZE;

   public static final int VIOLATION_OFFSET = RID_SIZE
                                         + DEVICE_ID_SIZE
                                         + ARRIVAL_TIME_SIZE
                                         + DEPART_TIME_SIZE
                                         + DURATION_SIZE
                                         + STREET_MARKER_SIZE
                                         + SIGN_SIZE
                                         + AREA_SIZE
                                         + STREET_ID_SIZE
                                         + STREET_NAME_SIZE
                                         + BETWEEN_STREET_1_SIZE
                                         + BETWEEN_STREET_2_SIZE
                                         + SIDE_OF_STREET_SIZE;

   public void readArguments(String args[]);

   public boolean isInteger(String s);


   // b+ tree global variables
   public static final String BPLUS_TREE_FILE_NAME = "bt.dat";
   public static final Boolean SAVE_TREE_TO_DISK = true;
   public static final String DEFAULT_KEY_STRING = "-";
   public static final Boolean SHOW_TREE_KEYS = false;
   public static final int TREE_RECORD_SIZE = DEVICE_ID_SIZE + ARRIVAL_TIME_SIZE + STREET_NAME_SIZE + 2;
   public static final boolean DEBUG_MODE = true;
   public static final boolean DEBUG_CHK_BUILD = false;
   public static final boolean SEARCH_ACTUAL_BTREE = true;
   public static final boolean DEBUG_MODE_SHOW_INSERT = false;
   public static final String DEBUG_MODE_SEARCH_STR = "22284";
   public static final int MAX_NUM_KEYS = 1024;
   public static void drawLine() {
		for (int x = 0; x < 50; x++)
			System.out.print("-");
		System.out.println("");
	}
}
