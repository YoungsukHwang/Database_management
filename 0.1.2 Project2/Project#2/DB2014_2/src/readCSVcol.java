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


public class readCSVcol {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/";
	
	static final String DB_NAME = "db2014_2";
	static final String DB_USER = "root";
	static final String DB_PASS = "12341234";//"12341234";
	static final int NUM_VALUES = 1500;
	static final int MAX_BATCH = 1000;
	//almost same as readCSVtable but it has Column name which is same as table name (some column of csv file -> DB table)
	// see readCSVtable first and than it's specific codes start with ///////////////////////...Long...///////////////////////////////
	public static void read(String FILENAME, String Column) {
		
		System.out.println("-------------- Start ReadCSVcol from " + FILENAME+ " ("+ Column+") --------------");
		
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		String sql = null;
		int count = 0;
		String[] colName = new String[20];
		String[] colType = new String[20];
		boolean SPECIAL = false;
		
		//Set buffered reader with file reader
		BufferedReader br = null;
		try {
			System.out.println("readCSVcol : Locating file..");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(FILENAME), "UTF-8")); 
		}
		catch (FileNotFoundException e) {
			System.out.println("readCSVcol : File not found");
			e.printStackTrace();
    		return;
		}
		catch (UnsupportedEncodingException e) {
			System.out.println("readCSVcol : UnsupportedEncoding exception");
			e.printStackTrace();
		}
		
		//remove ".csv" from filename
	    FILENAME = FILENAME.substring(0, FILENAME.length() -4);
		
	    
	    //Read first line to make column name -> meta[]
	    String line = null;
	    System.out.println("readCSVcol : Reading file..");
	    try {
			line = br.readLine();
		} catch (IOException e1) {
			System.out.println("readCSVcol : first line error");
			e1.printStackTrace();
		}
	    String[] meta = line.substring(1, line.length()-1).split("\",\"");
	    
	    
		
		//Register JDBC driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("readCSVcol : Error: unable to load driver class!");
			e.printStackTrace();
		}
		
		//initialize
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		
		
		try {
			//Open a connection
			System.out.println("readCSVcol : Connecting to database...");
			conn = DriverManager.getConnection(DB_URL + DB_NAME + "?useUnicode=true&characterEncoding=utf8", DB_USER, DB_PASS);
			stmt = conn.createStatement();
			
			//Find the table matches the file name 
			sql = "SELECT * FROM " + Column.toLowerCase() + " LIMIT 0;";
			
			
			
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
		    int[] place = new int[meta.length];  // have position of csv file's column matches DB table's attribute
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
		    
		    ///////////////////////////////////////////////////////////////////////////////////////////////////
		    // if column name is 'SPECIAL' or 'ADDRESS2', make it special case
		    if (Column.equals("SPECIAL")||Column.equals("ADDRESS2")){
		    	for (int i = 0; i < meta.length; i++){
		    		if(meta[i].toUpperCase().equals("SPECIAL")||meta[i].toUpperCase().equals("ADDRESS2")){
		    			place[a] = i;
		    			SPECIAL = true;
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
		    
		    int numofbatch = 0;  // to control the batch size
		    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd"); 	//make date form
		    
		    //initialize sql query
		    sql = "INSERT IGNORE INTO " + Column.toLowerCase() + " (";     ///////////////////////////////////////////use column name
		    sql = sql + Arrays.toString(netmeta).substring(1, Arrays.toString(netmeta).length() -1) + ") VALUES ("  + Arrays.toString(questions).substring(1,  Arrays.toString(questions).length() -1)+");";
		    
		    //make prepareStatement with sql
	        pstmt = conn.prepareStatement(sql);

	        //turn off AutoCommit to execute faster
	        conn.setAutoCommit(false);
	        
	        //start read csv file!
		    while ((line = br.readLine()) != null) {
		    	String[] values = new String[netmeta.length];						// have net values for the table attributes
		        String[] arr = line.substring(1, line.length()-1).split("\",\"");   // have all values from one line of the csv file
		        
		        /////////////////////////////////////////////////////////
		        // if it is special case ignore the line
		        if (SPECIAL && (arr[place[a]].equals("0")||arr[place[a]].equals(""))){
		        	continue;
		        }
		        
		        // get net values from csv file
		        for (int i = 0 ; i < netmeta.length; i++){
		        	values[i] = arr[place[i]];
		        }
		        
		        // change ? -> values (typed)
		        for (int i = 0; i < netmeta.length; i++){
		        	if (colType[i].equals("INT")){
		        		pstmt.setInt(i+1, Integer.parseInt(values[i]));	
		        	}
		        	else if (colType[i].equals("VARCHAR")){
		        		pstmt.setString(i+1, values[i]);
		        	}
		        	else if(colType[i].equals("DECIMAL")){
		        		pstmt.setBigDecimal(i+1, new BigDecimal(values[i]));	
		        	}
		        	else if(colType[i].equals("DATE")){
		        		pstmt.setDate(i+1, new java.sql.Date(format.parse(values[i]).getTime()));
		        	}
		        	else {
		        		System.out.println("New!!!! Type!!! : " + colType[i]);
		        		break;
		        	}
		        }
		        
		        // add batch!
		        pstmt.addBatch();
		        numofbatch++;
		        
		        // if number of batched queries is greater than MAX_BATCH executeBatch (for memory and speed issues)
		        if(numofbatch == MAX_BATCH){
		        	System.out.println("readCSVcol : excuteBatch!");
		        	pstmt.executeBatch();
		        	pstmt.clearBatch();
		        	numofbatch = 0;
		        }
		        
		      }
		    // after all, if batch is not cleared, executeBatch.
		    System.out.println("readCSVcol : excuteBatch!");
		    pstmt.executeBatch();
		    System.out.println("readCSVcol : excuteBatch finished");
		    pstmt.clearBatch();
		    conn.commit();  // commit changes
		    
		    
		    // copy the values of the table (has key) to the table that has no key (because of duplicate issue)/////////////
		    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			rs = stmt.executeQuery("SELECT * FROM "+ Column.toLowerCase() + ";");  // get the table
		    String sql2 = "INSERT IGNORE INTO " + Column.toLowerCase() + "_nokey (";
		    sql2 = sql2 + Arrays.toString(netmeta).substring(1, Arrays.toString(netmeta).length() -1) + ") VALUES (" + Arrays.toString(questions).substring(1,  Arrays.toString(questions).length() -1)+");";
	        pstmt2 = conn.prepareStatement(sql2);
	        
	        // insert values!
			numofbatch=0;
			while (rs.next()){
				for (int i = 0; i < count; i++){
					if(colType[i].equals("INT")){
						pstmt2.setInt(i+1, rs.getInt(i+1));
					}
					else if(colType[i].equals("VARCHAR")){
						pstmt2.setString(i+1, rs.getString(i+1));
					}
					else if(colType[i].equals("DECIMAL")){
		        		pstmt2.setBigDecimal(i+1, rs.getBigDecimal(i+1));	
		        	}
		        	else if(colType[i].equals("DATE")){
		        		pstmt2.setDate(i+1, rs.getDate(i+1));
		        	}
				}
				pstmt2.addBatch(); // addBatch
				numofbatch++;
				
				// executeBatch!
		        if(numofbatch == MAX_BATCH){
		        	System.out.println("readCSVcol : excuteBatch for nokey_table");
		        	pstmt2.executeBatch();
		        	pstmt2.clearBatch();
		        	numofbatch = 0;
		        }
			}
			
			System.out.println("readCSVcol : excuteBatch for nokey_table");
			// executeBatch for the last time (if batch is not cleared)
			pstmt2.executeBatch();
			System.out.println("readCSVcol : excuteBatch for nokey_table finished");
			conn.commit();  // commit changes
		} catch (SQLException e) {

			//Handle errors for JDBC
			System.out.println("readCSVcol : SQL Exception occured");
			e.printStackTrace();

		} catch (IOException e) {
			//Handle IOerrors
	    	System.out.println("readCSVcol : File reading error");
	      	e.printStackTrace();
	      	
		} catch (Exception e) {
			//Handle errors for Class.forName
			System.out.println("readCSVcol : Exception occured");
			e.printStackTrace();

		} finally {
			//finally block used to close resources
			System.out.println("readCSVcol : Closing DB resources");
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