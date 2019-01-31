import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class readSQL {
	
	//JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/";

	//Database credentials
	static final String DB_NAME = "db2014_2";
	static final String DB_USER = "root";
	static final String DB_PASS = "12341234";//"12341234";
	
	public static void read(String FILENAME) {
		
		System.out.println("---------Start read " + FILENAME +"----------");
		
	    BufferedReader br = null;
		Connection conn = null;
		Statement stmt = null;
		
	    try {
	      System.out.println("Locating file..");
	      //Set buffered reader with file reader
	      br = new BufferedReader(new InputStreamReader(new FileInputStream(FILENAME), "UTF-8")); 
	      
	    } catch (FileNotFoundException e) {
	      System.out.println("File not found");
	      e.printStackTrace();
	      return;
	      
	    } catch (UnsupportedEncodingException e) {
	      System.out.println("UnsupportedEncoding exception");
	      e.printStackTrace();
	    }

	    //Register JDBC driver
	  	try {
	  		Class.forName("com.mysql.jdbc.Driver");
	  	} catch (ClassNotFoundException e) {
	  		System.out.println("Error: unable to load driver class!");
	  		e.printStackTrace();
	  	}
	  		
	  	try {
	  		String line = null;    // line from sql file 
		    String query = null;   // one sql query
	  		//Open a connection
	  		System.out.println("Connecting to database...");
	  		conn = DriverManager.getConnection(DB_URL + DB_NAME + "?useUnicode=true&characterEncoding=utf8", DB_USER, DB_PASS);
	  		stmt = conn.createStatement();
	  		
	  		
	  		System.out.println("Reading file..");
	  		//read sql file
	  		while ((line = br.readLine()) != null) {
	  			//if one line starts with "CREATE" it means it starts new query
	  			if (line.startsWith("CREATE")){
	  				if (query != null){
	  					stmt.addBatch(query); //one query is finished, addBatch
	  				}
	  				query = line;
	  			} else{
	  				query = query +"\n"+ line; //add line to query
	  			}
	  		}
	  		stmt.addBatch(query); // for last query
	  		
	  		System.out.println("executeBatch...");
	  		stmt.executeBatch(); //execute all
	  		
	  	} catch (IOException e) {
	  		System.out.println("Fiel reading error");
	  		e.printStackTrace();
	  	} catch (SQLException e){
	    	System.out.println("Batch error");
	    	e.printStackTrace();
	  	} catch(Exception e){
	    	System.out.println("error?");
	    	e.printStackTrace();
	  	} finally {
	  		System.out.println("Quit readSQL....");
	    	if (br != null) {
	    		try {
	    			br.close();
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	try {
	    		if (stmt != null) stmt.close();
	    		if (conn != null) conn.close();
	    	} catch (SQLException e) {
	    		e.printStackTrace();
	    	}
	    }
	}
}

