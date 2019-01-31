import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.sql.Connection;


public class readCSVtables {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/";
	
	static final String DB_NAME = "db2014_2";
	static final String DB_USER = "root";
	static final String DB_PASS = "12341234";//"12341234";
	static final int NUM_VALUES = 1500;
	static final int MAX_BATCH = 1000;
	
	//almost everything is same as readCSVtable.java, but have two FILES (two csv files -> one DB table)
	// see readCSVtable first and than it's specific codes start with ///////////////////////...Long...///////////////////////////////
	public static void read(String FILENAME1, String FILENAME2) {
		
		System.out.println("------------ Start ReadCSVtables from " + FILENAME1 + ", "+ FILENAME2 +" --------------");
		
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		String sql = null;
		int count = 0;
		String[] colName = new String[20];
		String[] colType = new String[20];
		
		
		
		
		//Set buffered reader with file reader
		BufferedReader br = null, br1 = null;
		try {
			System.out.println("readCSVtables : Locating file..");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(FILENAME1), "UTF-8")); 
			br1 = new BufferedReader(new InputStreamReader(new FileInputStream(FILENAME2), "UTF-8")); /////////// have one more br
		} 
		catch (FileNotFoundException e) {
			System.out.println("readCSVtables : File not found");
			e.printStackTrace();
    		return;
		} 
		catch (UnsupportedEncodingException e) {
			System.out.println("readCSVtables : UnsupportedEncoding exception");
			e.printStackTrace();
		}
		
		
		//remove ".csv" from filename
	    String line = null;
	    FILENAME1 = FILENAME1.substring(0, FILENAME1.length() -4);
	    FILENAME2 = FILENAME2.substring(0, FILENAME2.length() -4);
		
	    
	    
	    //Read first line to make column name -> meta[]
	    try {
	    	System.out.println("readCSVtables : Reading file..");
			line = br.readLine() + "," +br1.readLine();
		} catch (IOException e1) {
			System.out.println("readCSVtables : first line error");
			e1.printStackTrace();
		}
	    String[] meta = line.substring(1, line.length()-1).split("\",\"");
	    
		
	    
		//Register JDBC driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("readCSVtables : Error: unable to load driver class!");
			e.printStackTrace();
		}
		
		//initialize
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		
		
		
		try {
			//Open a connection
			System.out.println("readCSVtables : Connecting to database...");
			conn = DriverManager.getConnection(DB_URL + DB_NAME + "?useUnicode=true&characterEncoding=utf8", DB_USER, DB_PASS);
			stmt = conn.createStatement();
			
			//Find the table matches the file name 
			sql = "SELECT * FROM " + FILENAME1.toLowerCase() + " LIMIT 0;";
			
			//Get Attributes' names and types -> comName[], colType[]
			rs = stmt.executeQuery(sql);
			rsmd = rs.getMetaData();
			count = rsmd.getColumnCount();
			for (int i = 1; i <= count; i++){
				colName[i-1] = rsmd.getColumnName(i);
				colType[i-1] = rsmd.getColumnTypeName(i);
			}

			
			// remove attributes that doesn't contained in csv metadata from colName[] -> tempmeta[]
			String[] tempmeta = new String[meta.length];
		    int[] place = new int[meta.length];          // have position of csv file's column matches DB table's attribute
		    int a = 0;
		    for (int i = 0; i < count; i++){
		    	boolean flag = true;
		    	for (int j = 0; (j < meta.length) && flag; j++){
		    		if (meta[j].toUpperCase().equals(colName[i])){
		    			flag = false;
		    			place[a] = j;
		    			a++;
		    			tempmeta[i] = colName[i];
		    		}
		    	}
		    }
		    
		    //tempmeta[] -> netmeta[] (remove null space)
		    String[] netmeta = new String[a];
		    for (int i = 0; i < a ; i++){
		    	netmeta[i] = tempmeta[i];
		    }
		    
		    //make [?, ?, ?, .....] for prepareStatement
		    String[] questions = new String[netmeta.length];
		    for (int i = 0 ; i < netmeta.length; i++){
	        	questions[i] = "?";
	        }
		    
		    // make [(?, ?, ?, ...) (?, ?, ?, ...) (?, ?, ?, ...) (?, ?, ?, ...) ...] for insert multiple rows at once
		    String[] multiValued = new String[NUM_VALUES];
		    for (int i = 0; i< NUM_VALUES; i++){
		    	multiValued[i] = "(" + Arrays.toString(questions).substring(1, Arrays.toString(questions).length()-1) + ")"; 
		    }
		    
		    
		    int numofline = 0;  // to control the position of values
		    int numofbatch = 0; // to control the batch size 
		    		  
		    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");   //make date form
		    
		    //initialize sql query
		    sql = "INSERT IGNORE INTO " + FILENAME1.toLowerCase() + " (";
		    String sql2 = "INSERT IGNORE INTO " + FILENAME1.toLowerCase() + "_nokey (";
		    sql = sql + Arrays.toString(netmeta).substring(1, Arrays.toString(netmeta).length() -1) + ") VALUES "  + Arrays.toString(multiValued).substring(1,  Arrays.toString(multiValued).length() -1)+";";
		    sql2 = sql2 + Arrays.toString(netmeta).substring(1, Arrays.toString(netmeta).length() -1) + ") VALUES " + Arrays.toString(multiValued).substring(1,  Arrays.toString(multiValued).length() -1)+";";
	        
		    //make prepareStatement with sql
		    pstmt = conn.prepareStatement(sql);
	        pstmt2 = conn.prepareStatement(sql2);
	        
	        //turn off AutoCommit to execute faster
	        conn.setAutoCommit(false);
	        
	        //start read csv file!
		    while ((line = br.readLine()) != null) {
		    	////////////////////////////////////////////////////////////////////////////////////////////////
		    	line = line + "," + br1.readLine();////////merge two lines. one from FILE1, the other from FILE2
		    	
		    	String[] values = new String[netmeta.length];						   // have net values for the table attributes
		        String[] arr = line.substring(1, line.length()-1).split("\",\"");      // have all values from one line of the csv file
		        
		        // get net values from csv file
		        for (int i = 0 ; i < netmeta.length; i++){
		        	values[i] = arr[place[i]];
		        }
		        
		        // change ? -> values (typed) // table(have key), table(don't have key) -> executed together(no duplicated row here)
		        for (int i = 0; i < netmeta.length; i++){
		        	if (colType[i].equals("INT")){
		        		pstmt.setInt(i+1+(netmeta.length*numofline), Integer.parseInt(values[i]));	//i+1 -> position of ? in (?,?,?,...),   
		        		pstmt2.setInt(i+1+(netmeta.length*numofline), Integer.parseInt(values[i])); //netmeta.length*numberofline -> position of (?,?,?,...) in [(?,?,?,...), (?,?,?,...), (?,?,?,...), (?,?,?,...), ...]
		        	}
		        	else if (colType[i].equals("VARCHAR")){
		        		pstmt.setString(i+1+(netmeta.length*numofline), values[i]);
		        		pstmt2.setString(i+1+(netmeta.length*numofline), values[i]);
		        	}
		        	else if(colType[i].equals("DECIMAL")){
		        		pstmt.setBigDecimal(i+1+(netmeta.length*numofline), new BigDecimal(values[i]));	
		        		pstmt2.setBigDecimal(i+1+(netmeta.length*numofline), new BigDecimal(values[i]));
		        	}
		        	else if(colType[i].equals("DATE")){
		        		pstmt.setDate(i+1+(netmeta.length*numofline), new java.sql.Date(format.parse(values[i]).getTime()));
		        		pstmt2.setDate(i+1+(netmeta.length*numofline), new java.sql.Date(format.parse(values[i]).getTime()));
		        	}
		        	else {
		        		System.out.println("New!!!! Type!!! : " + colType[i]);
		        		break;
		        	}
		        }
		        
		        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		        numofline++;  // count lines for filling the (?, ?, ?, ...) (?, ?, ?, ...) (?, ?, ?, ...) (?, ?, ?, ...) ....
		        if (numofline == NUM_VALUES){  //if numofline equal to NUM_VALUES, all ?s changed to values so it's time to addBatch
		        	pstmt.addBatch();
		        	pstmt2.addBatch();
		        	numofbatch++;
		        	numofline = 0;
		        }
		        
		        // if number of batched queries is greater than MAX_BATCH executeBatch (for memory and speed issues)
		        if(numofbatch == MAX_BATCH){
		        	System.out.println("readCSVtalbes : excuteBatch!");
		        	pstmt.executeBatch();
		        	pstmt2.executeBatch();
		        	pstmt.clearBatch();
		        	pstmt2.clearBatch();
		        	numofbatch = 0;
		        }
		      }
		    
		    // after all, if batch is not cleared, executeBatch.
		    System.out.println("readCSVtalbes : excuteBatch!");
		    pstmt.executeBatch();
		    pstmt2.executeBatch();
		    System.out.println("readCSVtalbes : excuteBatch finished");
		    conn.commit();   // commit all!!!!!
		 
		} catch (SQLException e) {
			//Handle errors for JDBC
			System.out.println("readCSVtables : SQL Exception occured");
			e.printStackTrace();
			
		} catch (IOException e) {
			//Handle IOerrors
	    	System.out.println("readCSVtables : File reading error");
	      	e.printStackTrace();
	      	
		} catch (Exception e) {
			//Handle errors for Class.forName
			System.out.println("readCSVtables : Exception occured");
			e.printStackTrace();

		} finally {
			//finally block used to close resources
			System.out.println("readCSVtables : Closing DB resources");
			try {
				if (stmt != null)	stmt.close();
				if (pstmt != null)	pstmt.close();
				if (pstmt2 != null)	pstmt2.close();
				if (conn != null)	conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			//
			if (br != null) {
		        try {
		          br.close();
		        } catch (Exception e) {
		          e.printStackTrace();
		        }
		      }
		}
	}
}