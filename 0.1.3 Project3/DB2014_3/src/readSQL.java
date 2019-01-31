import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class readSQL {
	
	//JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/";

	//Database credentials
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
		    String query = "";   // one sql query
	  		//Open a connection
	  		System.out.println("Connecting to database...");
	  		conn = DriverManager.getConnection(DB_URL+ "?useUnicode=true&characterEncoding=utf8", DB_USER, DB_PASS);
	  		stmt = conn.createStatement();
	  		
	  		int a = 1;
	  		System.out.println("Reading file..");
	  		//read sql file
	  		conn.setAutoCommit(false);
	  		while ((line = br.readLine()) != null) {
	  			if (line.contains("/*")){
	  				line = line.substring(0, line.indexOf("/*")) + line.substring(line.indexOf("*/")+2, line.length());
	  			}
	  			if (line.startsWith("/*") || line.startsWith(";")) continue;
	  			if (line.endsWith(";")) {
	  				query = query + "\n" + line;
	  				stmt.execute(query); //one query is finished, addBatch
	  				System.out.println("batch added... : " + a + " lines");
	  				query = "";
	  				a = 1;
	  			} else if(!line.equals("")){
	  				a++;
	  				query = query +"\n"+ line; //add line to query
	  			}
	  		}
	  		System.out.println("executeBatch...");
	  		//stmt.executeBatch(); //execute all
	  		conn.commit();
	  		
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

