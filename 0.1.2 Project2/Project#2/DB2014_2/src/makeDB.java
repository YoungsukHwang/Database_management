import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class makeDB {
	//JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/";

	//Database credentials
	static final String DB_NAME = "db2014_2";
	static final String DB_USER = "root";
	static final String DB_PASS = "12341234";//"12341234";


	public static void make() {
		System.out.println("------------------ Start Make Schema --------------------");
		
		
		//Register JDBC driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("makeDB : Error: unable to load driver class!");
			e.printStackTrace();
		}

		//Do JDBC work
		Connection conn = null;
		Statement stmt = null;
		try {
			//Open a connection
			System.out.println("makeDB : Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

			//Execute SQL using Statement
			stmt = conn.createStatement();

			//Create DB
			stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);      
			conn.setSchema(DB_NAME);

		} catch (SQLException e) {

			//Handle errors for JDBC
			System.out.println("makeDB : SQL Exception occured");
			e.printStackTrace();

		} catch (Exception e) {

			//Handle errors for Class.forName
			System.out.println("makeDB : Exception occured");
			e.printStackTrace();

		} finally {
			//finally block used to close resources
			System.out.println("makeDB : Closing DB resources");
			try {
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}